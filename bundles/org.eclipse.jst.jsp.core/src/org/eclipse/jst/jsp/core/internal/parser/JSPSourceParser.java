/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.parser;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.parser.internal.JSPTokenizer;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTokenizer;
import org.eclipse.wst.sse.core.internal.ltk.parser.JSPCapableParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandlerExtension;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.IRegionComparible;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Takes input from the JSPTokenizer and creates a tag list
 */

public class JSPSourceParser extends XMLSourceParser implements JSPCapableParser {
	protected class NestablePrefixHandler implements StructuredDocumentRegionHandler, StructuredDocumentRegionHandlerExtension {

		private static final String XMLNS = "xmlns:"; //$NON-NLS-1$

		/**
		 * Enables a TLD owning the given prefix loaded from the given URI at
		 * the anchorFlatNode. The list of additionalCMDocuments will claim to
		 * not know any of its tags at positions earlier than that
		 * IStructuredDocumentRegion's position.
		 * 
		 * For taglib directives, the taglib is the anchor while taglibs
		 * registered through include directives use the primary include
		 * directive as their anchor.
		 */
		protected void enableForTaglib(String prefix, IStructuredDocumentRegion anchorFlatNode) {
			if (prefix == null)
				return;
			List tagmarkers = ((JSPTokenizer) getTokenizer()).getNestablePrefixes();
			for (int i = 0; i < tagmarkers.size(); i++) {
				if (((TagMarker) tagmarkers.get(i)).getTagName().equals(prefix))
					return;
			}
			((JSPTokenizer) getTokenizer()).getNestablePrefixes().add(new TagMarker(prefix, anchorFlatNode));
		}

		public void nodeParsed(IStructuredDocumentRegion aCoreFlatNode) {
			// could test > 1, but since we only care if there are 8 (<%@,
			// taglib, uri, =, where, prefix, =, what) [or 4 for includes]
			if (aCoreFlatNode.getNumberOfRegions() > 4 && aCoreFlatNode.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = aCoreFlatNode.getRegions().get(1);
				try {
					int offset = aCoreFlatNode.getStartOffset(name);
					int length = name.getTextLength();
					boolean taglibdetected = false;
					boolean taglibdirectivedetected = false;
					if (fCharSequenceSource instanceof IRegionComparible) {
						taglibdetected = ((IRegionComparible) fCharSequenceSource).regionMatches(offset, length, JSP12TLDNames.TAGLIB);
						taglibdirectivedetected = ((IRegionComparible) fCharSequenceSource).regionMatches(offset, length, JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
					}
					else {
						// old fashioned way
						String directiveName = getText(offset, length);
						taglibdetected = directiveName.equals(JSP12TLDNames.TAGLIB);
						taglibdirectivedetected = directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
					}
					boolean processDirectiveName = taglibdetected || taglibdirectivedetected;
					if (processDirectiveName) {
						processTaglib(aCoreFlatNode);
					}
				}
				catch (StringIndexOutOfBoundsException sioobExc) {
					// do nothing
				}
			}
			// could test > 1, but since we only care if there are 5 (<,
			// jsp:root, xmlns:prefix, =, where)
			else if (aCoreFlatNode.getNumberOfRegions() > 4 && (aCoreFlatNode.getRegions().get(1)).getType() == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME) {
				processJSPRoot(aCoreFlatNode);
			}
		}

		protected void processJSPRoot(IStructuredDocumentRegion taglibFlatNode) {
			ITextRegionList regions = taglibFlatNode.getRegions();
			String prefix = null;
			boolean taglib = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						int offset = taglibFlatNode.getStartOffset(region);
						int length = region.getTextLength();

						String name = getText(offset, length);
						if (name.startsWith(XMLNS) && name.length() > XMLNS.length()) { //$NON-NLS-1$
							prefix = name.substring(6);
							taglib = true;
						}
						else {
							prefix = null;
							taglib = false;
						}
					}
					else if (taglib && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						prefix = StringUtils.strip(prefix);
						if (prefix != null && prefix.length() > 0) {
							enableForTaglib(prefix, taglibFlatNode);
							prefix = null;
						}
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				prefix = null;
			}
		}

		/**
		 * Pulls the prefix from the given taglib directive
		 * IStructuredDocumentRegion and makes sure the prefix is nestable.
		 */
		protected void processTaglib(IStructuredDocumentRegion taglibFlatNode) {
			ITextRegionList regions = taglibFlatNode.getRegions();
			String prefix = null;
			boolean prefixname = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						if (fCharSequenceSource != null && fCharSequenceSource instanceof IRegionComparible) {
							int offset = taglibFlatNode.getStartOffset(region);
							int length = region.getTextLength();
							prefixname = ((IRegionComparible) fCharSequenceSource).regionMatches(offset, length, JSP12TLDNames.PREFIX);
						}
						else {
							// old fashioned way
							prefixname = (getText(taglibFlatNode.getStartOffset(region), region.getTextLength()).equals(JSP12TLDNames.PREFIX));
						}
					}
					else if (prefixname && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						prefix = getText(taglibFlatNode.getStartOffset(region), region.getTextLength());
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				prefix = null;
			}
			prefix = StringUtils.strip(prefix);
			if (prefix != null && prefix.length() > 0) {
				enableForTaglib(prefix, taglibFlatNode);
			}
		}

		public void resetNodes() {
			Iterator tagmarkers = ((JSPTokenizer) getTokenizer()).getNestablePrefixes().iterator();
			while (tagmarkers.hasNext()) {
				if (!((TagMarker) tagmarkers.next()).isGlobal())
					tagmarkers.remove();
			}
		}

		public void setStructuredDocument(IStructuredDocument newDocument) {
			resetNodes();
			getStructuredDocumentRegionHandlers().remove(this);
			if (newDocument != null && newDocument.getParser() instanceof StructuredDocumentRegionParser) {
				((StructuredDocumentRegionParser) newDocument.getParser()).addStructuredDocumentRegionHandler(this);
			}
		}
	}

	/**
	 * JSPSourceParser constructor comment.
	 */
	public JSPSourceParser() {
		super();
	}

	public void addNestablePrefix(TagMarker marker) {
		((JSPTokenizer) getTokenizer()).addNestablePrefix(marker);
	}

	public List getNestablePrefixes() {
		return ((JSPTokenizer) getTokenizer()).getNestablePrefixes();
	}

	protected BlockTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new JSPTokenizer();
			getStructuredDocumentRegionHandlers().add(new NestablePrefixHandler());
		}
		return fTokenizer;
	}


	public RegionParser newInstance() {
		JSPSourceParser newInstance = new JSPSourceParser();
		newInstance.setTokenizer(getTokenizer().newInstance());
		return newInstance;
	}

	protected IStructuredDocumentRegion parseNodes() {
		// regions are initially reported as complete offsets within the
		// scanned input
		// they are adjusted here to be indexes from the currentNode's start
		// offset
		IStructuredDocumentRegion headNode = null;
		IStructuredDocumentRegion lastNode = null;
		ITextRegion region = null;
		// DMW: 2/12/03. Made current node local variable, since
		// we changed class to not require state
		IStructuredDocumentRegion currentNode = null;
		String type = null;

		while ((region = getNextRegion()) != null) {
			type = region.getType();
			// these types (might) demand a IStructuredDocumentRegion for each
			// of them
			if (type == DOMRegionContext.BLOCK_TEXT) {
				if (currentNode != null && currentNode.getLastRegion().getType() == DOMRegionContext.BLOCK_TEXT) {
					// multiple block texts indicated embedded containers; no
					// new IStructuredDocumentRegion
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
					// DW, 4/16/2003 token regions no longer have parents
					// region.setParent(currentNode);
					if (region instanceof ITextRegionContainer) {
						((ITextRegionContainer) region).setParent(currentNode);
					}
				}
				else {
					// not continuing a IStructuredDocumentRegion
					if (currentNode != null) {
						// ensure that any existing node is at least
						// terminated
						if (!currentNode.isEnded()) {
							currentNode.setLength(region.getStart() - currentNode.getStart());
							// fCurrentNode.setTextLength(region.getStart() -
							// fCurrentNode.getStart());
						}
						lastNode = currentNode;
					}
					fireNodeParsed(currentNode);
					currentNode = createStructuredDocumentRegion(type);
					if (lastNode != null) {
						lastNode.setNext(currentNode);
					}
					currentNode.setPrevious(lastNode);
					currentNode.setStart(region.getStart());
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					currentNode.setEnded(true);
					region.adjustStart(-currentNode.getStart());
					currentNode.addRegion(region);
					// DW, 4/16/2003 token regions no longer have parents
					// region.setParent(currentNode);
					if (region instanceof ITextRegionContainer) {
						((ITextRegionContainer) region).setParent(currentNode);
					}

				}
			}
			// the following contexts OPEN new StructuredDocumentRegions
			else if ((currentNode != null && currentNode.isEnded()) || (type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE) || (type == DOMRegionContext.XML_PI_OPEN) || (type == DOMRegionContext.XML_TAG_OPEN) || (type == DOMRegionContext.XML_END_TAG_OPEN) || (type == DOMRegionContext.XML_COMMENT_OPEN) || (type == DOMRegionContext.XML_CDATA_OPEN) || (type == DOMRegionContext.XML_DECLARATION_OPEN) || (type == DOMJSPRegionContexts.JSP_COMMENT_OPEN) || (type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN) || (type == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN) || (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) || (type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) || (type == DOMJSPRegionContexts.JSP_CLOSE) || type == DOMJSPRegionContexts.JSP_EL_OPEN) {
				if (currentNode != null) {
					// ensure that any existing node is at least terminated
					if (!currentNode.isEnded()) {
						currentNode.setLength(region.getStart() - currentNode.getStart());
						// fCurrentNode.setTextLength(region.getStart() -
						// fCurrentNode.getStart());
					}
					lastNode = currentNode;
				}
				fireNodeParsed(currentNode);
				currentNode = createStructuredDocumentRegion(type);
				if (lastNode != null) {
					lastNode.setNext(currentNode);
				}
				currentNode.setPrevious(lastNode);
				currentNode.setStart(region.getStart());
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW, 4/16/2003 token regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}

			}
			// the following contexts NEITHER open nor close
			// StructuredDocumentRegions; just add to them
			else if ((type == DOMRegionContext.XML_TAG_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) || (type == DOMRegionContext.XML_COMMENT_TEXT) || (type == DOMRegionContext.XML_PI_CONTENT) || (type == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET) || (type == DOMJSPRegionContexts.JSP_COMMENT_TEXT) || (type == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME) || (type == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) || type == DOMJSPRegionContexts.JSP_EL_CONTENT) {
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW, 4/16/2003 token regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			// the following contexts close off StructuredDocumentRegions
			// cleanly
			else if ((type == DOMRegionContext.XML_PI_CLOSE) || (type == DOMRegionContext.XML_TAG_CLOSE) || (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE) || (type == DOMRegionContext.XML_COMMENT_CLOSE) || (type == DOMRegionContext.XML_CDATA_CLOSE) || (type == DOMJSPRegionContexts.JSP_CLOSE) || (type == DOMJSPRegionContexts.JSP_COMMENT_CLOSE) || (type == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE) || (type == DOMRegionContext.XML_DECLARATION_CLOSE) || type == DOMJSPRegionContexts.JSP_EL_CLOSE) {
				currentNode.setEnded(true);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				currentNode.addRegion(region);
				region.adjustStart(-currentNode.getStart());
				// DW, 4/16/2003 token regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			// this is extremely rare, but valid
			else if (type == DOMRegionContext.WHITE_SPACE) {
				ITextRegion lastRegion = currentNode.getLastRegion();
				// pack the embedded container with this region
				if (lastRegion instanceof ITextRegionContainer) {
					ITextRegionContainer container = (ITextRegionContainer) lastRegion;
					container.getRegions().add(region);
					// DW, 4/16/2003 container regions have parent. Probably a
					// better place to set,
					// but for now, will (re)set each time through
					container.setParent(currentNode);
					// DW, 4/16/2003 token regions no longer have parents
					// region.setParent(container);
					region.adjustStart(container.getLength() - region.getStart());
				}
				currentNode.getLastRegion().adjustLength(region.getLength());
				currentNode.adjustLength(region.getLength());
			}
			else if (type == DOMRegionContext.UNDEFINED && currentNode != null) {
				// skip on a very-first region situation as the default
				// behavior is good enough
				// combine with previous if also undefined
				if (currentNode.getLastRegion() != null && currentNode.getLastRegion().getType() == DOMRegionContext.UNDEFINED) {
					currentNode.getLastRegion().adjustLength(region.getLength());
					currentNode.adjustLength(region.getLength());

					//if adding this region to a previous container then need to add this
					//region to the container and update its start location
					if(currentNode.getLastRegion() instanceof ITextRegionContainer) {
						region.adjustStart(-currentNode.getLastRegion().getStart() - currentNode.getStart());
						((ITextRegionContainer)currentNode.getLastRegion()).getRegions().add(region);
					}
				}
				// previous wasn't undefined
				else {
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
				}
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			else {
				// if an unknown type is the first region in the document,
				// ensure that a node exists
				if (currentNode == null) {
					currentNode = createStructuredDocumentRegion(type);
					currentNode.setStart(region.getStart());
				}
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW, 4/16/2003 token regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}

				if (Debug.debugTokenizer)
					System.out.println(getClass().getName() + " found region of not specifically handled type " + region.getType() + " @ " + region.getStart() + "[" + region.getLength() + "]");//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

			}

			// these regions also get their own node, so close them cleanly
			// NOTE: these regions have new StructuredDocumentRegions created
			// for them above; it may
			// be more readable if that is handled here as well, but the
			// current layout
			// ensures that they open StructuredDocumentRegions the same way
			if ((type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE) || (type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN) || (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) || (type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) || (type == DOMJSPRegionContexts.JSP_CONTENT) || (type == DOMJSPRegionContexts.JSP_CLOSE)) {
				currentNode.setEnded(true);
			}
			if (headNode == null && currentNode != null) {
				headNode = currentNode;
			}
		}
		if (currentNode != null) {
			fireNodeParsed(currentNode);
			currentNode.setPrevious(lastNode);
		}
		primReset();
		return headNode;
	}

	public void removeNestablePrefix(String tagName) {
		((JSPTokenizer) getTokenizer()).removeNestablePrefix(tagName);
	}

}
