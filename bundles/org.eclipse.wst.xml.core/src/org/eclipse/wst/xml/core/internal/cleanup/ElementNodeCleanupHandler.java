/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.cleanup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.document.XMLAttr;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLGenerator;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ElementNodeCleanupHandler extends NodeCleanupHandler {
	protected static final char DOUBLE_QUOTE = '\"'; //$NON-NLS-1$
	protected static final String DOUBLE_QUOTES = "\"\""; //$NON-NLS-1$
	protected static final String EMPTY_TAG_CLOSE = "/>"; //$NON-NLS-1$
	protected static final String END_TAG_OPEN = "</"; //$NON-NLS-1$
	protected static final char SINGLE_QUOTE = '\''; //$NON-NLS-1$
	protected static final String SINGLE_QUOTES = "''"; //$NON-NLS-1$

	/** Non-NLS strings */
	protected static final String START_TAG_OPEN = "<"; //$NON-NLS-1$
	protected static final String TAG_CLOSE = ">"; //$NON-NLS-1$

	public Node cleanup(Node node) {
		Node newNode = cleanupChildren(node);
		XMLNode renamedNode = newNode instanceof XMLNode ? (XMLNode) newNode : null;

		// call quoteAttrValue() first so it will close any unclosed attr
		// quoteAttrValue() will return the new start tag if there is a
		// structure change
		renamedNode = quoteAttrValue(renamedNode);

		// insert tag close if missing
		// if node is not comment tag
		// and not implicit tag
		if (!isCommentTag(renamedNode) && !isImplicitTag(renamedNode)) {
			XMLModel structuredModel = renamedNode.getModel();

			// save start offset before insertTagClose()
			// or else renamedNode.getStartOffset() will be zero if
			// renamedNode replaced by insertTagClose()
			int startTagStartOffset = renamedNode.getStartOffset();

			// for start tag
			IStructuredDocumentRegion startTagStructuredDocumentRegion = renamedNode.getStartStructuredDocumentRegion();
			insertTagClose(structuredModel, startTagStructuredDocumentRegion);

			// update renamedNode and startTagStructuredDocumentRegion after
			// insertTagClose()
			renamedNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset);
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
		renamedNode = insertMissingTags(renamedNode);

		renamedNode = compressEmptyElementTag(renamedNode);

		renamedNode = insertRequiredAttrs(renamedNode);

		return renamedNode;
	}

	protected Node cleanupChildren(Node node) {
		Node parentNode = node;

		if (node != null) {
			Node childNode = node.getFirstChild();
			while (childNode != null) {
				// get cleanup handler
				IStructuredCleanupHandler cleanupHandler = getCleanupHandler(childNode);

				// cleanup each child
				childNode = cleanupHandler.cleanup(childNode);

				// get new parent node
				parentNode = (XMLNode) childNode.getParentNode();

				// get next child node
				childNode = (XMLNode) childNode.getNextSibling();
			}
		}

		return parentNode;
	}

	private XMLNode compressEmptyElementTag(XMLNode node) {
		boolean compressEmptyElementTags = getCleanupPreferences().getCompressEmptyElementTags();
		XMLNode newNode = node;

		IStructuredDocumentRegion startTagStructuredDocumentRegion = newNode.getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion endTagStructuredDocumentRegion = newNode.getLastStructuredDocumentRegion();

		if (compressEmptyElementTags && startTagStructuredDocumentRegion != endTagStructuredDocumentRegion && startTagStructuredDocumentRegion != null) {
			ITextRegionList regions = startTagStructuredDocumentRegion.getRegions();
			ITextRegion lastRegion = regions.get(regions.size() - 1);
			// format children and end tag if not empty element tag
			if (lastRegion.getType() != XMLRegionContext.XML_EMPTY_TAG_CLOSE) {
				NodeList childNodes = newNode.getChildNodes();
				if (childNodes == null || childNodes.getLength() == 0 || (childNodes.getLength() == 1 && (childNodes.item(0)).getNodeType() == Node.TEXT_NODE && ((childNodes.item(0)).getNodeValue().trim().length() == 0))) {
					XMLModel structuredModel = newNode.getModel();
					IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

					int startTagStartOffset = newNode.getStartOffset();
					int offset = endTagStructuredDocumentRegion.getStart();
					int length = endTagStructuredDocumentRegion.getLength();
					structuredDocument.replaceText(structuredDocument, offset, length, ""); //$NON-NLS-1$
					newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset); // save

					offset = startTagStructuredDocumentRegion.getStart() + lastRegion.getStart();
					structuredDocument.replaceText(structuredDocument, offset, 0, "/"); //$NON-NLS-1$
					newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
				}
			}
		}

		return newNode;
	}

	protected IStructuredCleanupHandler getCleanupHandler(Node node) {
		short nodeType = node.getNodeType();
		IStructuredCleanupHandler cleanupHandler = null;
		switch (nodeType) {
			case org.w3c.dom.Node.ELEMENT_NODE : {
				cleanupHandler = new ElementNodeCleanupHandler();
				break;
			}
			case org.w3c.dom.Node.TEXT_NODE : {
				cleanupHandler = new NodeCleanupHandler();
				break;
			}
			default : {
				cleanupHandler = new NodeCleanupHandler();
			}
		}

		// init CleanupPreferences
		cleanupHandler.setCleanupPreferences(getCleanupPreferences());

		return cleanupHandler;
	}


	protected ModelQuery getModelQuery(Node node) {
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			return ModelQueryUtil.getModelQuery((Document) node);
		} else {
			return ModelQueryUtil.getModelQuery(node.getOwnerDocument());
		}
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

	private XMLNode insertEndTag(XMLNode node) {
		XMLNode newNode = node;
		XMLElement element = (XMLElement) node;
		if (element.isCommentTag())
			return node; // do nothing

		int startTagStartOffset = node.getStartOffset();
		XMLModel structuredModel = node.getModel();

		if (isEmptyElement(element)) {
			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
			IStructuredDocumentRegion startStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
			ITextRegionList regions = startStructuredDocumentRegion.getRegions();
			ITextRegion lastRegion = regions.get(regions.size() - 1);
			structuredDocument.replaceText(structuredDocument, startStructuredDocumentRegion.getStartOffset(lastRegion), lastRegion.getLength(), EMPTY_TAG_CLOSE);

			if (regions.size() > 1) {
				ITextRegion regionBeforeTagClose = regions.get(regions.size() - 1 - 1);

				// insert a space separator before tag close if the previous
				// region does not have extra spaces
				if (regionBeforeTagClose.getTextLength() == regionBeforeTagClose.getLength())
					structuredDocument.replaceText(structuredDocument, startStructuredDocumentRegion.getStartOffset(lastRegion), 0, " "); //$NON-NLS-1$
			}
		} else {
			String tagName = node.getNodeName();
			String endTag = END_TAG_OPEN.concat(tagName).concat(TAG_CLOSE);

			XMLNode lastChild = (XMLNode) node.getLastChild();
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
			structuredDocument.replaceText(structuredDocument, endTagStartOffset, 0, endTag);
		}

		newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
		// new
		// node

		return newNode;
	}

	private XMLNode insertMissingTags(XMLNode node) {
		boolean insertMissingTags = getCleanupPreferences().getInsertMissingTags();
		XMLNode newNode = node;

		if (insertMissingTags) {
			IStructuredDocumentRegion startTagStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
			if (startTagStructuredDocumentRegion == null) {
				// implicit start tag; generate tag for it
				newNode = insertStartTag(node);
				startTagStructuredDocumentRegion = newNode.getStartStructuredDocumentRegion();
			}

			IStructuredDocumentRegion endTagStructuredDocumentRegion = newNode.getEndStructuredDocumentRegion();
			ITextRegionList startStructuredDocumentRegionRegions = startTagStructuredDocumentRegion.getRegions();
			if (startTagStructuredDocumentRegion != null && startStructuredDocumentRegionRegions != null && (startStructuredDocumentRegionRegions.get(startStructuredDocumentRegionRegions.size() - 1)).getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE) {

			} else {
				if (startTagStructuredDocumentRegion == null) {
					// start tag missing
					if (isStartTagRequired(newNode))
						newNode = insertStartTag(newNode);
				} else if (endTagStructuredDocumentRegion == null) {
					// end tag missing
					if (isEndTagRequired(newNode))
						newNode = insertEndTag(newNode);
				}
			}
		}

		return newNode;
	}

	private XMLNode insertRequiredAttrs(XMLNode node) {
		boolean insertRequiredAttrs = getCleanupPreferences().getInsertRequiredAttrs();
		XMLNode newNode = node;

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
					if (lastRegion.getType() == XMLRegionContext.XML_TAG_CLOSE) {
						index--;
						lastRegion = startStructuredDocumentRegion.getRegionAtCharacterOffset(index - 1);
					} else if (lastRegion.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE) {
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
							//          in the document before the first TextEdit, not
							// after each
							//          child TextEdit. Therefore, do not need to
							// advance the index.
							//index += nameAndDefaultValue.length();
						}
						multiTextEdit.apply(newNode.getStructuredDocument());
					} catch (BadLocationException e) {
						throw new SourceEditingRuntimeException(e);
					}
				}
			}
		}

		return newNode;
	}

	private XMLNode insertStartTag(XMLNode node) {
		XMLNode newNode = node;

		if (isCommentTag(node))
			return node; // do nothing

		String tagName = node.getNodeName();
		String startTag = START_TAG_OPEN.concat(tagName).concat(TAG_CLOSE);
		int startTagStartOffset = node.getStartOffset();

		XMLModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
		structuredDocument.replaceText(structuredDocument, startTagStartOffset, 0, startTag);
		newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
		// new
		// node

		return newNode;
	}

	private void insertTagClose(XMLModel structuredModel, IStructuredDocumentRegion flatNode) {
		if (flatNode != null) {
			ITextRegionList flatnodeRegions = flatNode.getRegions();
			if (flatnodeRegions != null) {
				ITextRegion lastRegion = flatnodeRegions.get(flatnodeRegions.size() - 1);
				if (lastRegion != null) {
					String regionType = lastRegion.getType();
					if ((regionType != XMLRegionContext.XML_EMPTY_TAG_CLOSE) && (regionType != XMLRegionContext.XML_TAG_CLOSE)) {
						IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

						// insert ">" after lastRegion of flatNode
						// as in "<a</a>" if flatNode is for start tag, or in
						// "<a></a" if flatNode is for end tag
						structuredDocument.replaceText(structuredDocument, flatNode.getTextEndOffset(lastRegion), 0, ">"); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * @param renamedNode
	 * @return
	 */
	private boolean isCommentTag(Node renamedNode) {
		boolean result = false;
		if (renamedNode instanceof XMLElement) {
			XMLElement element = (XMLElement) renamedNode;
			result = element.isCommentTag();
		}
		return result;
	}

	private boolean isEmptyElement(XMLElement element) {
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

	private boolean isEndTagRequired(XMLNode node) {
		if (node == null)
			return false;
		return node.isContainer();
	}

	/**
	 * A tag is implicit if it has not corresponding region in document.
	 * 
	 * @param renamedNode
	 * @return
	 */
	private boolean isImplicitTag(XMLNode renamedNode) {
		return renamedNode.getStartStructuredDocumentRegion() == null;
	}

	/**
	 * The end tags of HTML EMPTY content type, such as IMG, and HTML
	 * undefined tags are parsed separately from the start tags. So inserting
	 * the missing start tag is useless and even harmful.
	 */
	private boolean isStartTagRequired(XMLNode node) {
		if (node == null)
			return false;
		return node.isContainer();
	}

	private boolean isXMLType(XMLModel structuredModel) {
		boolean result = false;

		if (structuredModel != null && structuredModel != null) {
			XMLDocument document = structuredModel.getDocument();

			if (document != null)
				result = document.isXMLType();
		}

		return result;
	}

	private XMLNode quoteAttrValue(XMLNode node) {
		XMLNode newNode = node;
		//XMLElement element = (XMLElement) node;
		if (isCommentTag(node))
			return node; // do nothing

		boolean quoteAttrValues = getCleanupPreferences().getQuoteAttrValues();

		if (quoteAttrValues) {
			NamedNodeMap attributes = newNode.getAttributes();
			if (attributes != null) {
				int attributesLength = attributes.getLength();
				XMLGenerator generator = node.getModel().getGenerator();

				for (int i = 0; i < attributesLength; i++) {
					attributes = newNode.getAttributes();
					attributesLength = attributes.getLength();
					XMLAttr eachAttr = (XMLAttr) attributes.item(i);
					//ITextRegion oldAttrValueRegion =
					// eachAttr.getValueRegion();
					String oldAttrValue = eachAttr.getValueRegionText();
					if (oldAttrValue == null) {
						XMLModel structuredModel = node.getModel();
						if (isXMLType(structuredModel)) {
							String newAttrValue = "\"" + eachAttr.getNameRegionText() + "\""; //$NON-NLS-1$ //$NON-NLS-2$

							IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
							if (eachAttr.getEqualRegion() != null)
								// equal region exists
								structuredDocument.replaceText(structuredDocument, eachAttr.getEndOffset(), 0, newAttrValue);
							else
								// no equal region
								structuredDocument.replaceText(structuredDocument, eachAttr.getNameRegionTextEndOffset(), 0, "=".concat(newAttrValue)); //$NON-NLS-1$
							newNode = (XMLNode) structuredModel.getIndexedRegion(node.getStartOffset()); // save
							// new
							// node
						}
					} else {
						//String oldAttrValue = oldAttrValueRegion.getText();
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

								XMLModel structuredModel = node.getModel();
								IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
								structuredDocument.replaceText(structuredDocument, attrValueStartOffset, attrValueLength, newAttrValue);
								newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset); // save
								// new
								// node
							}
						}
					}
				}
			}
		}

		return newNode;
	}
}
