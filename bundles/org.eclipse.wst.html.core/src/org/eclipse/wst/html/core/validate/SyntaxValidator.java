/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.validate;

import java.util.Iterator;

import org.eclipse.wst.html.core.document.HTMLDocumentTypeEntry;
import org.eclipse.wst.html.core.document.HTMLDocumentTypeRegistry;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.document.InvalidCharacterException;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.document.SourceValidator;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class SyntaxValidator extends PrimeValidator implements ErrorState {


	static private boolean isValidRegion(ITextRegion rgn) {
		String type = rgn.getType();
		if (type == null)
			return false; // no type is invalid.
		if (type == XMLRegionContext.XML_END_TAG_OPEN || type == XMLRegionContext.XML_TAG_NAME || type == XMLRegionContext.XML_TAG_CLOSE) {
			return true;
		}
		return false;
	}

	static private String getTagName(IStructuredDocumentRegion tag) {
		ITextRegionList regions = tag.getRegions();
		Iterator iter = regions.iterator();
		while (iter.hasNext()) {
			ITextRegion rgn = (ITextRegion) iter.next();
			if (rgn.getType() == XMLRegionContext.XML_TAG_NAME)
				return tag.getText(rgn);
		}
		return "";//$NON-NLS-1$
	}

	static private boolean isEmptyContent(CMElementDeclaration decl) {
		return (decl != null) && (decl.getContentType() == CMElementDeclaration.EMPTY);
	}

	public SyntaxValidator() {
		super();
	}

	public boolean isAdapterForType(Object type) {
		return ((type == SyntaxValidator.class) || super.isAdapterForType(type));
	}

	class ElementInfo {
		public ElementInfo() {
			super();
		}

		public XMLElement target = null;
		public CMElementDeclaration decl = null;
		public IStructuredDocumentRegion startTag = null;
		public IStructuredDocumentRegion endTag = null;
		public boolean hasStartTag = false;
		public boolean hasEndTag = false;
		public boolean isXHTML = false;
	}

	public void validate(IndexedRegion indexedNode) {
		Node node = (Node) indexedNode;
		validateChildren(node);

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return;
		if (!(node instanceof XMLElement))
			return;

		ElementInfo info = new ElementInfo();
		info.target = (XMLElement) node;

		// gather information to validate from target at once.
		getInfo(info);

		if (info.target.isGlobalTag()) {
			validateTags(info);
			validateNames(info);
			if (info.decl != null && info.isXHTML) {
				validateTagCase(info);
			}
		}
	}

	private void getInfo(ElementInfo info) {
		info.decl = CMUtil.getDeclaration(info.target);
		info.startTag = info.target.getStartStructuredDocumentRegion();
		info.endTag = info.target.getEndStructuredDocumentRegion();

		info.hasStartTag = (info.startTag != null);
		info.hasEndTag = (info.endTag != null);

		Document doc = info.target.getOwnerDocument();
		if (!(doc instanceof XMLDocument))
			return;
		String typeid = ((XMLDocument) doc).getDocumentTypeId();
		if (typeid != null) {
			HTMLDocumentTypeEntry entry = HTMLDocumentTypeRegistry.getInstance().getEntry(typeid);
			info.isXHTML = (entry != null && entry.isXMLType());
		}
	}

	class TagErrorInfoImpl extends AbstractErrorInfo {
		private String hint = null;

		public TagErrorInfoImpl(int state, IStructuredDocumentRegion tag, String hint) {
			super(state, new Segment(tag));
			this.hint = hint;
		}

		public String getHint() {
			return hint;
		}

		public short getTargetType() {
			return Node.ELEMENT_NODE;
		}
	}

	private boolean isEndTagCorrupted(ElementInfo info) {
		ITextRegionList regions = info.endTag.getRegions();
		if (regions == null || regions.isEmpty())
			return false;
		Iterator iter = regions.iterator();
		while (iter.hasNext()) {
			ITextRegion rgn = (ITextRegion) iter.next();
			if (!isValidRegion(rgn))
				return true; // found invalid region type.
		}
		return false; // all regions are valid.
	}

	private String getEndTagFullText(ElementInfo info) {
		String hint = "";//$NON-NLS-1$
		ITextRegionList regions = info.endTag.getRegions();
		Iterator iter = regions.iterator();
		while (iter.hasNext()) {
			ITextRegion rgn = (ITextRegion) iter.next();
			String type = rgn.getType();
			if (type == null)
				continue;
			if (type == XMLRegionContext.XML_END_TAG_OPEN || type == XMLRegionContext.XML_TAG_CLOSE)
				continue;
			hint += info.endTag.getFullText(rgn);
		}
		return hint;
	}

	private void reportCorruptedEndTagError(ElementInfo info) {
		String hint = getEndTagFullText(info);
		TagErrorInfoImpl error = new TagErrorInfoImpl(UNDEFINED_NAME_ERROR, info.endTag, hint);
		this.reporter.report(MessageFactory.createMessage(error));
	}

	private void validateTags(ElementInfo info) {
		if (info.hasStartTag) {
			if (!info.target.isStartTagClosed()) {
				// Mark the whole START tag as an error segment.
				Segment errorSeg = new Segment(info.startTag);
				report(UNCLOSED_TAG_ERROR, errorSeg, info.target);
			}
		}
		else {
			if (info.hasEndTag) {
				if (info.decl != null) {
					if (CMUtil.isHTML(info.decl) && !info.target.hasChildNodes()) {
						if (info.target.isContainer()) {
							// Set the error mark to the start of the element.
							Segment errorSeg = new Segment(info.target.getStartOffset(), 0);
							report(MISSING_START_TAG_ERROR, errorSeg, info.target);
						}
						else {
							// Mark the whole END tag as an error segment.
							Segment errorSeg = new Segment(info.endTag);
							report(UNNECESSARY_END_TAG_ERROR, errorSeg, info.target);
						}
					}
					else if (info.isXHTML) {
						Segment errorSeg = new Segment(info.target.getStartOffset(), 0);
						report(MISSING_START_TAG_ERROR, errorSeg, info.target);
					}
				}
			}
		}

		if (info.hasEndTag) {
			if (!info.target.isClosed()) {
				// Set the whole END tag as error segment.
				Segment errorSeg = new Segment(info.endTag);
				report(UNCLOSED_END_TAG_ERROR, errorSeg, info.target);
			}
		}
		else {
			if (info.isXHTML) { // XHTML
				if (!info.target.isEmptyTag()) {
					if (isEmptyContent(info.decl)) {
						// EMPTY element should be written in <.../> form.
						Segment errorSeg = FMUtil.getSegment(info.target, FMUtil.SEG_START_TAG);
						report(INVALID_EMPTY_ELEMENT_TAG, errorSeg, info.target);
					}
					else {
						// end tag is required.
						Segment errorSeg = new Segment(info.target.getEndOffset(), 0);
						report(MISSING_END_TAG_ERROR, errorSeg, info.target);
					}
				}
			}
			else { // HTML
				if (info.hasStartTag) {
					if (info.decl != null && CMUtil.isHTML(info.decl) && !info.target.isEmptyTag() && !CMUtil.isEndTagOmissible(info.decl)) {
						// Set the error mark to the end of the element.
						Segment errorSeg = new Segment(info.target.getEndOffset(), 0);
						report(MISSING_END_TAG_ERROR, errorSeg, info.target);
					}
				}
			}
		}
	}

	/* perform validation about tag name definition. */
	private void validateNames(ElementInfo info) {
		boolean corrupted = info.hasEndTag && isEndTagCorrupted(info);
		if (info.decl == null) {
			// If no declaration is retrieved, the target is really
			// unknown element.
			if (!info.hasStartTag && corrupted) {
				reportCorruptedEndTagError(info);
			}
			else {
				Segment errorSeg = FMUtil.getSegment(info.target, FMUtil.SEG_START_TAG);
				report(UNDEFINED_NAME_ERROR, errorSeg, info.target);
			}
		}
		else {
			// Even if a declaration could be retrieved, if the end
			// tag is corrupted, it should be reported as undefined
			// name. (D202493)
			if (corrupted) {
				reportCorruptedEndTagError(info);
			}
		}
	}

	/* perform validation tag case only for XHTML document */
	private void validateTagCase(ElementInfo info) {
		String declared = info.decl.getElementName();
		String startTagName = "";//$NON-NLS-1$
		String endTagName = "";//$NON-NLS-1$
		if (declared == null)
			return;

		// start tag
		if (info.hasStartTag) {
			startTagName = getTagName(info.startTag);
			if (!declared.equals(startTagName)) {
				TagErrorInfoImpl error = new TagErrorInfoImpl(MISMATCHED_ERROR, info.startTag, startTagName);
				this.reporter.report(MessageFactory.createMessage(error));
			}
		}
		// end tag
		if (info.hasEndTag) {
			endTagName = getTagName(info.endTag);
			if (!info.hasStartTag || (!endTagName.equals(startTagName))) {
				if (!declared.equals(endTagName)) {
					TagErrorInfoImpl error = new TagErrorInfoImpl(MISMATCHED_ERROR, info.endTag, endTagName);
					this.reporter.report(MessageFactory.createMessage(error));
				}
			}
		}
	}

	private void validateChildren(Node target) {
		if ((target.getNodeType() == Node.ELEMENT_NODE) && CMUtil.isForeign((Element) target))
			return;

		for (Node child = target.getFirstChild(); child != null; child = child.getNextSibling()) {
			switch (child.getNodeType()) {
				case Node.TEXT_NODE :
					{
						XMLNode text = (XMLNode) child;
						int charOffset = validateTextSource(text);
						if (charOffset >= 0) {
							charOffset += text.getStartOffset();
							Segment errorSeg = new Segment(charOffset, 1);
							if (errorSeg != null)
								report(INVALID_CHAR_ERROR, errorSeg, text);
						}
						break;
					}
				case Node.COMMENT_NODE :
				case Node.DOCUMENT_TYPE_NODE :
				case Node.PROCESSING_INSTRUCTION_NODE :
				case Node.CDATA_SECTION_NODE :
					{
						XMLNode tag = (XMLNode) child;
						if (!tag.isClosed()) {
							Segment errorSeg = FMUtil.getSegment(tag, FMUtil.SEG_WHOLE_TAG);
							if (errorSeg != null)
								report(UNCLOSED_TAG_ERROR, errorSeg, tag);
						}
						break;
					}
				default :
					break;
			}
		}
	}

	private int validateTextSource(XMLNode text) {
		try {
			SourceValidator validator = new SourceValidator(text);
			validator.validateSource(text.getSource());
		}
		catch (InvalidCharacterException ex) {
			return ex.getOffset();
		}
		return -1;
	}

	private void report(int state, Segment errorSeg, Node node) {
		ErrorInfo info = new ErrorInfoImpl(state, errorSeg, node);
		reporter.report(MessageFactory.createMessage(info));
	}
}