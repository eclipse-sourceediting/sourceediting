/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (Intalion) - Cleanup Repeated Conditional check in isXMLType method
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.cleanup;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.html.core.internal.Logger;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// nakamori_TODO: check and remove CSS formatting

public class ElementNodeCleanupHandler extends AbstractNodeCleanupHandler {

	/** Non-NLS strings */
	protected static final String START_TAG_OPEN = "<"; //$NON-NLS-1$
	protected static final String END_TAG_OPEN = "</"; //$NON-NLS-1$
	protected static final String TAG_CLOSE = ">"; //$NON-NLS-1$
	protected static final String EMPTY_TAG_CLOSE = "/>"; //$NON-NLS-1$
	protected static final String SINGLE_QUOTES = "''"; //$NON-NLS-1$
	protected static final String DOUBLE_QUOTES = "\"\""; //$NON-NLS-1$
	protected static final char SINGLE_QUOTE = '\''; //$NON-NLS-1$
	protected static final char DOUBLE_QUOTE = '\"'; //$NON-NLS-1$

	public Node cleanup(Node node) {
		IDOMNode renamedNode = (IDOMNode) cleanupChildren(node);

		// call quoteAttrValue() first so it will close any unclosed attr
		// quoteAttrValue() will return the new start tag if there is a
		// structure change
		renamedNode = quoteAttrValue(renamedNode);

		// insert tag close if missing
		// if node is not comment tag
		// and not implicit tag
		if (!((IDOMElement) renamedNode).isCommentTag() && (renamedNode.getStartStructuredDocumentRegion() != null)) {
			IDOMModel structuredModel = renamedNode.getModel();

			// save start offset before insertTagClose()
			// or else renamedNode.getStartOffset() will be zero if
			// renamedNode replaced by insertTagClose()
			int startTagStartOffset = renamedNode.getStartOffset();

			// for start tag
			IStructuredDocumentRegion startTagStructuredDocumentRegion = renamedNode.getStartStructuredDocumentRegion();
			insertTagClose(structuredModel, startTagStructuredDocumentRegion);

			// update renamedNode and startTagStructuredDocumentRegion after
			// insertTagClose()
			renamedNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset);
			startTagStructuredDocumentRegion = renamedNode.getStartStructuredDocumentRegion();

			// for end tag
			IStructuredDocumentRegion endTagStructuredDocumentRegion = renamedNode.getEndStructuredDocumentRegion();
			if (endTagStructuredDocumentRegion != startTagStructuredDocumentRegion)
				insertTagClose(structuredModel, endTagStructuredDocumentRegion);
		}

		// call insertMissingTags() next, it will generate implicit tags if
		// there are any
		// insertMissingTags() will return the new missing start tag if one is
		// missing
		// then compress any empty element tags
		// applyTagNameCase() will return the renamed node.
		// The renamed/new node will be saved and returned to caller when all
		// cleanup is done.
		renamedNode = insertMissingTags(renamedNode);
		renamedNode = compressEmptyElementTag(renamedNode);
		renamedNode = insertRequiredAttrs(renamedNode);
		renamedNode = applyTagNameCase(renamedNode);
		applyAttrNameCase(renamedNode);
		cleanupCSSAttrValue(renamedNode);

		return renamedNode;
	}

	/**
	 * Checks if cleanup should modify case. Returns true case should be
	 * preserved, false otherwise.
	 * 
	 * @param element
	 * @return true if element is case sensitive, false otherwise
	 */
	private boolean shouldPreserveCase(IDOMElement element) {
		// case option can be applied to no namespace tags
		return !element.isGlobalTag();
		/*
		 * ModelQueryAdapter mqadapter = (ModelQueryAdapter)
		 * element.getAdapterFor(ModelQueryAdapter.class); ModelQuery mq =
		 * null; CMNode nodedecl = null; if (mqadapter != null) mq =
		 * mqadapter.getModelQuery(); if (mq != null) nodedecl =
		 * mq.getCMNode(node); // if a Node isn't recognized as HTML or is and
		 * cares about case, do not alter it // if (nodedecl == null ||
		 * (nodedecl instanceof HTMLCMNode && ((HTMLCMNode)
		 * nodedecl).shouldIgnoreCase())) if (!
		 * nodedecl.supports(HTMLCMProperties.SHOULD_IGNORE_CASE)) return
		 * false; return
		 * ((Boolean)cmnode.getProperty(HTMLCMProperties.SHOULD_IGNORE_CASE)).booleanValue();
		 */
	}

	/**
	 * Checks if cleanup should force modifying element name to all lowercase.
	 * 
	 * @param element
	 * @return true if cleanup should lowercase element name, false otherwise
	 */
	private boolean isXMLTag(IDOMElement element) {
		return element.isXMLTag();
	}

	protected void applyAttrNameCase(IDOMNode node) {
		IDOMElement element = (IDOMElement) node;
		if (element.isCommentTag())
			return; // do nothing

		int attrNameCase = HTMLCorePreferenceNames.ASIS;
		if (!shouldPreserveCase(element)) {
			if (isXMLTag(element))
				attrNameCase = HTMLCorePreferenceNames.LOWER;
			else
				attrNameCase = getCleanupPreferences().getAttrNameCase();
		}

		NamedNodeMap attributes = node.getAttributes();
		int attributesLength = attributes.getLength();

		for (int i = 0; i < attributesLength; i++) {
			IDOMNode eachAttr = (IDOMNode) attributes.item(i);
			if (hasNestedRegion(eachAttr.getNameRegion()))
				continue;
			String oldAttrName = eachAttr.getNodeName();
			String newAttrName = oldAttrName;
			/*
			 * 254961 - all HTML tag names and attribute names should be in
			 * English even for HTML files in other languages like Japanese or
			 * Turkish. English locale should be used to convert between
			 * uppercase and lowercase (otherwise "link" would be converted to
			 * Turkish "I Overdot Capital").
			 */
			if (attrNameCase == HTMLCorePreferenceNames.LOWER)
				newAttrName = oldAttrName.toLowerCase(Locale.US);
			else if (attrNameCase == HTMLCorePreferenceNames.UPPER)
				newAttrName = oldAttrName.toUpperCase(Locale.US);

			if (newAttrName.compareTo(oldAttrName) != 0) {
				int attrNameStartOffset = eachAttr.getStartOffset();
				int attrNameLength = oldAttrName.length();

				IDOMModel structuredModel = node.getModel();
				IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
				replaceSource(structuredModel, structuredDocument, attrNameStartOffset, attrNameLength, newAttrName);
			}
		}
	}

	/**
	 * True if container has nested regions, meaning container is probably too
	 * complicated (like JSP regions) to validate with this validator.
	 */
	private boolean hasNestedRegion(ITextRegion container) {
		if (!(container instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) container).getRegions();
		if (regions == null)
			return false;
		return true;
	}

	protected IDOMNode applyTagNameCase(IDOMNode node) {
		IDOMElement element = (IDOMElement) node;
		if (element.isCommentTag())
			return node; // do nothing

		int tagNameCase = HTMLCorePreferenceNames.ASIS;

		if (!shouldPreserveCase(element)) {
			if (isXMLTag(element))
				tagNameCase = HTMLCorePreferenceNames.LOWER;
			else
				tagNameCase = getCleanupPreferences().getTagNameCase();
		}

		String oldTagName = node.getNodeName();
		String newTagName = oldTagName;
		IDOMNode newNode = node;

		/*
		 * 254961 - all HTML tag names and attribute names should be in
		 * English even for HTML files in other languages like Japanese or
		 * Turkish. English locale should be used to convert between uppercase
		 * and lowercase (otherwise "link" would be converted to Turkish "I
		 * Overdot Capital").
		 */
		if (tagNameCase == HTMLCorePreferenceNames.LOWER)
			newTagName = oldTagName.toLowerCase(Locale.US);
		else if (tagNameCase == HTMLCorePreferenceNames.UPPER)
			newTagName = oldTagName.toUpperCase(Locale.US);

		IDOMModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

		IStructuredDocumentRegion startTagStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
		if (startTagStructuredDocumentRegion != null) {
			ITextRegionList regions = startTagStructuredDocumentRegion.getRegions();
			if (regions != null && regions.size() > 0) {
				ITextRegion startTagNameRegion = regions.get(1);
				int startTagNameStartOffset = startTagStructuredDocumentRegion.getStartOffset(startTagNameRegion);
				int startTagNameLength = startTagStructuredDocumentRegion.getTextEndOffset(startTagNameRegion) - startTagNameStartOffset;

				replaceSource(structuredModel, structuredDocument, startTagNameStartOffset, startTagNameLength, newTagName);
				newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagNameStartOffset); // save
				// new
				// node
			}
		}

		IStructuredDocumentRegion endTagStructuredDocumentRegion = node.getEndStructuredDocumentRegion();
		if (endTagStructuredDocumentRegion != null) {
			ITextRegionList regions = endTagStructuredDocumentRegion.getRegions();
			if (regions != null && regions.size() > 0) {
				ITextRegion endTagNameRegion = regions.get(1);
				int endTagNameStartOffset = endTagStructuredDocumentRegion.getStartOffset(endTagNameRegion);
				int endTagNameLength = endTagStructuredDocumentRegion.getTextEndOffset(endTagNameRegion) - endTagNameStartOffset;

				if (startTagStructuredDocumentRegion != endTagStructuredDocumentRegion)
					replaceSource(structuredModel, structuredDocument, endTagNameStartOffset, endTagNameLength, newTagName);
			}
		}

		return newNode;
	}

	protected Node cleanupChildren(Node node) {
		Node parentNode = node;

		if (node != null) {
			Node childNode = node.getFirstChild();
			HTMLCleanupHandlerFactory factory = HTMLCleanupHandlerFactory.getInstance();
			while (childNode != null) {
				// cleanup this child node
				IStructuredCleanupHandler cleanupHandler = factory.createHandler(childNode, getCleanupPreferences());
				childNode = cleanupHandler.cleanup(childNode);

				// get new parent node
				parentNode = childNode.getParentNode();

				// get next child node
				childNode = childNode.getNextSibling();
			}
		}

		return parentNode;
	}

	/**
	 */
	protected void cleanupCSSAttrValue(IDOMNode node) {
		if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
			return;
		IDOMElement element = (IDOMElement) node;
		if (!element.isGlobalTag())
			return;

		Attr attr = element.getAttributeNode("style"); //$NON-NLS-1$
		if (attr == null)
			return;
		String value = getCSSValue(attr);
		if (value == null)
			return;
		String oldValue = ((IDOMNode) attr).getValueSource();
		if (oldValue != null && value.equals(oldValue))
			return;
		attr.setValue(value);
	}

	/**
	 */
	private ICSSModel getCSSModel(Attr attr) {
		if (attr == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) attr.getOwnerElement();
		if (notifier == null)
			return null;
		INodeAdapter adapter = notifier.getAdapterFor(IStyleDeclarationAdapter.class);
		if (adapter == null)
			return null;
		if (!(adapter instanceof IStyleDeclarationAdapter))
			return null;
		IStyleDeclarationAdapter styleAdapter = (IStyleDeclarationAdapter) adapter;
		return styleAdapter.getModel();
	}

	/**
	 */
	private String getCSSValue(Attr attr) {
		ICSSModel model = getCSSModel(attr);
		if (model == null)
			return null;
		ICSSNode document = model.getDocument();
		if (document == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) document;
		CSSSourceFormatter formatter = (CSSSourceFormatter) notifier.getAdapterFor(CSSSourceFormatter.class);
		// try another way to get formatter
		if (formatter == null)
			formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(notifier);
		if (formatter == null)
			return null;
		StringBuffer buffer = formatter.cleanup(document);
		if (buffer == null)
			return null;
		return buffer.toString();
	}

	private boolean isEmptyElement(IDOMElement element) {
		Document document = element.getOwnerDocument();
		if (document == null)
			// undefined tag, return default
			return false;

		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
		if (modelQuery == null)
			// undefined tag, return default
			return false;

		CMElementDeclaration decl = modelQuery.getCMElementDeclaration(element);
		if (decl == null)
			// undefined tag, return default
			return false;

		return (decl.getContentType() == CMElementDeclaration.EMPTY);
	}

	protected IDOMNode insertEndTag(IDOMNode node) {
		IDOMElement element = (IDOMElement) node;

		int startTagStartOffset = node.getStartOffset();
		IDOMModel structuredModel = node.getModel();
		IDOMNode newNode = null;

		if (element.isCommentTag()) {
			// do nothing
		}
		else if (isEmptyElement(element)) {
			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
			IStructuredDocumentRegion startStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
			ITextRegionList regions = startStructuredDocumentRegion.getRegions();
			ITextRegion lastRegion = regions.get(regions.size() - 1);
			replaceSource(structuredModel, structuredDocument, startStructuredDocumentRegion.getStartOffset(lastRegion), lastRegion.getLength(), EMPTY_TAG_CLOSE);

			if (regions.size() > 1) {
				ITextRegion regionBeforeTagClose = regions.get(regions.size() - 1 - 1);

				// insert a space separator before tag close if the previous
				// region does not have extra spaces
				if (regionBeforeTagClose.getTextLength() == regionBeforeTagClose.getLength())
					replaceSource(structuredModel, structuredDocument, startStructuredDocumentRegion.getStartOffset(lastRegion), 0, " "); //$NON-NLS-1$
			}
		}
		else {
			String tagName = node.getNodeName();
			String endTag = END_TAG_OPEN.concat(tagName).concat(TAG_CLOSE);

			IDOMNode lastChild = (IDOMNode) node.getLastChild();
			int endTagStartOffset = 0;
			if (lastChild != null)
				// if this node has children, insert the end tag after the
				// last child
				endTagStartOffset = lastChild.getEndOffset();
			else
				// if this node does not has children, insert the end tag
				// after the start tag
				endTagStartOffset = node.getEndOffset();

			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
			replaceSource(structuredModel, structuredDocument, endTagStartOffset, 0, endTag);
		}

		newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
		// new
		// node

		return newNode;
	}

	protected IDOMNode insertMissingTags(IDOMNode node) {
		boolean insertMissingTags = getCleanupPreferences().getInsertMissingTags();
		IDOMNode newNode = node;

		if (insertMissingTags) {
			IStructuredDocumentRegion startTagStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
			if (startTagStructuredDocumentRegion == null) {
				// implicit start tag; generate tag for it
				newNode = insertStartTag(node);
				startTagStructuredDocumentRegion = newNode.getStartStructuredDocumentRegion();
			}

			IStructuredDocumentRegion endTagStructuredDocumentRegion = newNode.getEndStructuredDocumentRegion();

			ITextRegionList regionList = startTagStructuredDocumentRegion.getRegions();
			if (startTagStructuredDocumentRegion != null && regionList != null && regionList.get(regionList.size() - 1).getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {

			}
			else {
				if (startTagStructuredDocumentRegion == null) {
					// start tag missing
					if (isStartTagRequired(newNode))
						newNode = insertStartTag(newNode);
				}
				else if (endTagStructuredDocumentRegion == null) {
					// end tag missing
					if (isEndTagRequired(newNode))
						newNode = insertEndTag(newNode);
				}
			}
		}

		return newNode;
	}

	protected IDOMNode insertStartTag(IDOMNode node) {
		IDOMElement element = (IDOMElement) node;
		if (element.isCommentTag())
			return node; // do nothing

		IDOMNode newNode = null;

		String tagName = node.getNodeName();
		String startTag = START_TAG_OPEN.concat(tagName).concat(TAG_CLOSE);
		int startTagStartOffset = node.getStartOffset();

		IDOMModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
		replaceSource(structuredModel, structuredDocument, startTagStartOffset, 0, startTag);
		newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
		// new
		// node

		return newNode;
	}

	protected void insertTagClose(IDOMModel structuredModel, IStructuredDocumentRegion flatNode) {
		if ((flatNode != null) && (flatNode.getRegions() != null)) {
			ITextRegionList regionList = flatNode.getRegions();
			ITextRegion lastRegion = regionList.get(regionList.size() - 1);
			if (lastRegion != null) {
				String regionType = lastRegion.getType();
				if ((regionType != DOMRegionContext.XML_EMPTY_TAG_CLOSE) && (regionType != DOMRegionContext.XML_TAG_CLOSE)) {
					IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

					// insert ">" after lastRegion of flatNode
					// as in "<a</a>" if flatNode is for start tag, or in
					// "<a></a" if flatNode is for end tag
					replaceSource(structuredModel, structuredDocument, flatNode.getTextEndOffset(lastRegion), 0, ">"); //$NON-NLS-1$
				}
			}
		}
	}

	protected boolean isEndTagRequired(IDOMNode node) {
		if (node == null)
			return false;
		return node.isContainer();
	}

	/**
	 * The end tags of HTML EMPTY content type, such as IMG, and HTML
	 * undefined tags are parsed separately from the start tags. So inserting
	 * the missing start tag is useless and even harmful.
	 */
	protected boolean isStartTagRequired(IDOMNode node) {
		if (node == null)
			return false;
		return node.isContainer();
	}

	protected boolean isXMLType(IDOMModel structuredModel) {
		boolean result = false;

		if (structuredModel != null) {
			IDOMDocument document = structuredModel.getDocument();

			if (document != null)
				result = document.isXMLType();
		}

		return result;
	}

	protected IDOMNode quoteAttrValue(IDOMNode node) {
		IDOMElement element = (IDOMElement) node;
		if (element.isCommentTag())
			return node; // do nothing

		boolean quoteAttrValues = getCleanupPreferences().getQuoteAttrValues();
		IDOMNode newNode = node;

		if (quoteAttrValues) {
			NamedNodeMap attributes = newNode.getAttributes();
			int attributesLength = attributes.getLength();
			ISourceGenerator generator = node.getModel().getGenerator();

			for (int i = 0; i < attributesLength; i++) {
				attributes = newNode.getAttributes();
				attributesLength = attributes.getLength();
				IDOMAttr eachAttr = (IDOMAttr) attributes.item(i);
				// ITextRegion oldAttrValueRegion = eachAttr.getValueRegion();
				String oldAttrValue = eachAttr.getValueRegionText();
				if (oldAttrValue == null) {
					IDOMModel structuredModel = node.getModel();
					if (isXMLType(structuredModel)) {
						// TODO: Kit, please check. Is there any way to not
						// rely on getting regions from attributes?
						String newAttrValue = "=\"" + eachAttr.getNameRegionText() + "\""; //$NON-NLS-1$ //$NON-NLS-2$

						IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
						replaceSource(structuredModel, structuredDocument, eachAttr.getNameRegionEndOffset(), 0, newAttrValue);
						newNode = (IDOMNode) structuredModel.getIndexedRegion(node.getStartOffset()); // save
						// new
						// node
					}
				}
				else {

					char quote = StringUtils.isQuoted(oldAttrValue) ? oldAttrValue.charAt(0) : DOUBLE_QUOTE;
					String newAttrValue = generator.generateAttrValue(eachAttr, quote);

					// There is a problem in
					// StructuredDocumentRegionUtil.getAttrValue(ITextRegion)
					// when the region is instanceof ContextRegion.
					// Workaround for now...
					if (oldAttrValue.length() == 1) {
						char firstChar = oldAttrValue.charAt(0);
						if (firstChar == SINGLE_QUOTE)
							newAttrValue = SINGLE_QUOTES;
						else if (firstChar == DOUBLE_QUOTE)
							newAttrValue = DOUBLE_QUOTES;
					}

					if (newAttrValue != null) {
						if (newAttrValue.compareTo(oldAttrValue) != 0) {
							int attrValueStartOffset = eachAttr.getValueRegionStartOffset();
							int attrValueLength = oldAttrValue.length();
							int startTagStartOffset = node.getStartOffset();

							IDOMModel structuredModel = node.getModel();
							IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
							replaceSource(structuredModel, structuredDocument, attrValueStartOffset, attrValueLength, newAttrValue);
							newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
							// new
							// node
						}
					}
				}
			}
		}

		return newNode;
	}

	private IDOMNode insertRequiredAttrs(IDOMNode node) {
		boolean insertRequiredAttrs = getCleanupPreferences().getInsertRequiredAttrs();
		IDOMNode newNode = node;

		if (insertRequiredAttrs) {
			List requiredAttrs = getRequiredAttrs(newNode);
			if (requiredAttrs.size() > 0) {
				NamedNodeMap currentAttrs = node.getAttributes();
				List insertAttrs = new ArrayList();
				if (currentAttrs.getLength() == 0)
					insertAttrs.addAll(requiredAttrs);
				else {
					for (int i = 0; i < requiredAttrs.size(); i++) {
						String requiredAttrName = ((CMAttributeDeclaration) requiredAttrs.get(i)).getAttrName();
						boolean found = false;
						for (int j = 0; j < currentAttrs.getLength(); j++) {
							String currentAttrName = currentAttrs.item(j).getNodeName();
							if (requiredAttrName.compareToIgnoreCase(currentAttrName) == 0) {
								found = true;
								break;
							}
						}
						if (!found)
							insertAttrs.add(requiredAttrs.get(i));
					}
				}
				if (insertAttrs.size() > 0) {
					IStructuredDocumentRegion startStructuredDocumentRegion = newNode.getStartStructuredDocumentRegion();
					int index = startStructuredDocumentRegion.getEndOffset();
					ITextRegion lastRegion = startStructuredDocumentRegion.getLastRegion();
					if (lastRegion.getType() == DOMRegionContext.XML_TAG_CLOSE) {
						index--;
						lastRegion = startStructuredDocumentRegion.getRegionAtCharacterOffset(index - 1);
					}
					else if (lastRegion.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
						index = index - 2;
						lastRegion = startStructuredDocumentRegion.getRegionAtCharacterOffset(index - 1);
					}
					MultiTextEdit multiTextEdit = new MultiTextEdit();
					try {
						for (int i = insertAttrs.size() - 1; i >= 0; i--) {
							CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) insertAttrs.get(i);
							String requiredAttributeName = attrDecl.getAttrName();
							String defaultValue = attrDecl.getDefaultValue();
							if (defaultValue == null)
								defaultValue = ""; //$NON-NLS-1$
							String nameAndDefaultValue = " "; //$NON-NLS-1$
							if (i == 0 && lastRegion.getLength() > lastRegion.getTextLength())
								nameAndDefaultValue = ""; //$NON-NLS-1$
							nameAndDefaultValue += requiredAttributeName + "=\"" + defaultValue + "\""; //$NON-NLS-1$ //$NON-NLS-2$
							multiTextEdit.addChild(new InsertEdit(index, nameAndDefaultValue));
							// BUG3381: MultiTextEdit applies all child
							// TextEdit's basing on offsets
							// in the document before the first TextEdit, not
							// after each
							// child TextEdit. Therefore, do not need to
							// advance the index.
							// index += nameAndDefaultValue.length();
						}
						multiTextEdit.apply(newNode.getStructuredDocument());
					}
					catch (BadLocationException e) {
						// log or now, unless we find reason not to
						Logger.log(Logger.INFO, e.getMessage());
					}
				}
			}
		}

		return newNode;
	}


	protected ModelQuery getModelQuery(Node node) {
		ModelQuery result = null;
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			result = ModelQueryUtil.getModelQuery((Document) node);
		}
		else {
			result = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
		}
		return result;
	}

	protected List getRequiredAttrs(Node node) {
		List result = new ArrayList();

		ModelQuery modelQuery = getModelQuery(node);
		if (modelQuery != null) {
			CMElementDeclaration elementDecl = modelQuery.getCMElementDeclaration((Element) node);
			if (elementDecl != null) {
				CMNamedNodeMap attrMap = elementDecl.getAttributes();
				Iterator it = attrMap.iterator();
				CMAttributeDeclaration attr = null;
				while (it.hasNext()) {
					attr = (CMAttributeDeclaration) it.next();
					if (attr.getUsage() == CMAttributeDeclaration.REQUIRED) {
						result.add(attr);
					}
				}
			}
		}

		return result;
	}

	/**
	 * <p>Compress empty element tags if the prefence is set to do so</p>
	 * 
	 * @copyof org.eclipse.wst.xml.core.internal.cleanup.ElementNodeCleanupHandler#compressEmptyElementTag
	 * 
	 * @param node the {@link IDOMNode} to possible compress
	 * @return the compressed node if the given node should be compressed, else the node as it was given
	 */
	private IDOMNode compressEmptyElementTag(IDOMNode node) {
		boolean compressEmptyElementTags = getCleanupPreferences().getCompressEmptyElementTags();
		IDOMNode newNode = node;

		IStructuredDocumentRegion startTagStructuredDocumentRegion = newNode.getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion endTagStructuredDocumentRegion = newNode.getLastStructuredDocumentRegion();

		//only compress tags if they are empty
		if ((compressEmptyElementTags && startTagStructuredDocumentRegion != endTagStructuredDocumentRegion &&
				startTagStructuredDocumentRegion != null)) {

			//only compress end tags if its XHTML or not a container
			if(isXMLTag((IDOMElement)newNode) || !newNode.isContainer()) {
				ITextRegionList regions = startTagStructuredDocumentRegion.getRegions();
				ITextRegion lastRegion = regions.get(regions.size() - 1);
				// format children and end tag if not empty element tag
				if (lastRegion.getType() != DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
					NodeList childNodes = newNode.getChildNodes();
					if (childNodes == null || childNodes.getLength() == 0 || (childNodes.getLength() == 1 && (childNodes.item(0)).getNodeType() == Node.TEXT_NODE && ((childNodes.item(0)).getNodeValue().trim().length() == 0))) {
						IDOMModel structuredModel = newNode.getModel();
						IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

						int startTagStartOffset = newNode.getStartOffset();
						int offset = endTagStructuredDocumentRegion.getStart();
						int length = endTagStructuredDocumentRegion.getLength();
						structuredDocument.replaceText(structuredDocument, offset, length, ""); //$NON-NLS-1$
						newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset); // save

						offset = startTagStructuredDocumentRegion.getStart() + lastRegion.getStart();
						structuredDocument.replaceText(structuredDocument, offset, 0, "/"); //$NON-NLS-1$
						newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
					}
				}
			}
		}

		return newNode;
	}
}