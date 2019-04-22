/*******************************************************************************
 * Copyright (c) 2010, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.quickoutline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AttributeShowingLabelProvider extends JFaceNodeLabelProvider implements IStyledLabelProvider {
	static final String ATTR_NAME = "name";
	static final String ATTR_ID = "id";
	private boolean fShowAttributes;
	/**
	 * The maximum length of an element's "pulled up" text content. Never
	 * decrease to under 4.
	 */
	static final int MAX_ELEMENTTEXT_LENGTH = 72;

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}
	
	protected String firstLine(String s, boolean handleEllipses) {
		String truncated = s;
		int n = 0;
		boolean needsEllipses = false;
		if ((n = truncated.indexOf('\r')) >= 0) {
			truncated = truncated.substring(0, n);
			needsEllipses= true;
		}
		if ((n = truncated.indexOf('\n')) >= 0) {
			truncated = truncated.substring(0, n);
			needsEllipses= true;
		}

		if (truncated.length() > MAX_ELEMENTTEXT_LENGTH) {
			truncated = truncated.substring(0, MAX_ELEMENTTEXT_LENGTH-3).concat("...");
		}
		else if (handleEllipses && needsEllipses) {
			return truncated.concat("...");
		}
		return truncated;
	}

	public Node getAttributeToShow(Element element) {
		NamedNodeMap attributes = element.getAttributes();
		Node idTypedAttribute = null;
		Node requiredAttribute = null;
		boolean hasId = false;
		boolean hasName = false;
		Node shownAttribute = null;

		// try to get content model element
		// declaration
		CMElementDeclaration elementDecl = null;
		ModelQuery mq = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
		if (mq != null) {
			elementDecl = mq.getCMElementDeclaration(element);
		}
		// find an attribute of type (or just named)
		// ID
		if (elementDecl != null) {
			final CMNamedNodeMap attributeDeclarationMap = elementDecl.getAttributes();
			int i = 0;
			while (i < attributes.getLength() && idTypedAttribute == null) {
				Node attr = attributes.item(i);
				String attrName = attr.getNodeName();
				CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) attributeDeclarationMap.getNamedItem(attrName);
				if (attrDecl != null) {
					if ((attrDecl.getAttrType() != null) && (CMDataType.ID.equals(attrDecl.getAttrType().getDataTypeName()))) {
						idTypedAttribute = attr;
					}
					else if ((attrDecl.getUsage() == CMAttributeDeclaration.REQUIRED) && (requiredAttribute == null)) {
						/*
						 * as a backup, keep tabs on any required
						 * attributes
						 */
						requiredAttribute = attr;
					}
					else {
						hasId = hasId || attrName.equals(ATTR_ID);
						hasName = hasName || attrName.equals(ATTR_NAME);
					}
				}
				++i;
			}
		}

		/*
		 * If no suitable attribute with type "ID" was found, then prefer
		 * "id" or "name", otherwise try using a required attribute, if
		 * none, then just use the first attribute
		 */
		if (idTypedAttribute != null) {
			shownAttribute = idTypedAttribute;
		}
		else if (hasId) {
			shownAttribute = attributes.getNamedItem(ATTR_ID);
		}
		else if (hasName) {
			shownAttribute = attributes.getNamedItem(ATTR_NAME);
		}
		else if (requiredAttribute != null) {
			shownAttribute = requiredAttribute;
		}
		if (shownAttribute == null) {
			shownAttribute = attributes.item(0);
		}

		return shownAttribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object o) {
		if (fShowAttributes && o instanceof Node) {
			Node node = (Node) o;
			StringBuilder builder = new StringBuilder();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				builder.append(super.getText(node));
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
				if (node.hasAttributes()) {
					Node shownAttribute = getAttributeToShow((Element) node);
					if (shownAttribute != null) {
						String attributeName = shownAttribute.getNodeName();
						if (attributeName != null && attributeName.length() > 0) {
							String attributeValue = shownAttribute.getNodeValue();
							if (attributeValue != null) {
								builder.append(" "); //$NON-NLS-1$
								builder.append(attributeName);
								// https://bugs.eclipse.org/486252
								builder.append("="); //$NON-NLS-1$
								builder.append(attributeValue);
							}
						}
					}
				}
				else if (node instanceof IDOMElement) {
					Node possibleText = node.getFirstChild();
					if (possibleText != null && possibleText.getNextSibling() == null && possibleText.getNodeType() == Node.TEXT_NODE) {
						builder.append(" : ");
						builder.append(firstLine(possibleText.getNodeValue().trim(), true));
					}
				}
			}
			else if (node.getNodeType() == Node.COMMENT_NODE) {
				builder.append(firstLine(node.getNodeValue().trim(), true));
			}
			else {
				builder.append(super.getText(node));
			}
			return builder.toString();
		}
		return super.getText(o);
	}

	String getIdMatchValue(Object o) {
		// this method could be called after #dispose()
		if (o instanceof Node) {
			Node node = (Node) o;
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
				if (node.hasAttributes()) {
					Node shownAttribute = getAttributeToShow((Element) node);
					if (shownAttribute != null) {
						String attributeName = shownAttribute.getNodeName();
						if (attributeName != null && attributeName.length() > 0) {
							String attributeValue = shownAttribute.getNodeValue();
							if (attributeValue != null) {
								return attributeValue;
							}
						}
					}
				}
				else if (node instanceof IDOMElement) {
					Node possibleText = node.getFirstChild();
					if (possibleText != null && possibleText.getNextSibling() == null && possibleText.getNodeType() == Node.TEXT_NODE) {
						return firstLine(possibleText.getNodeValue().trim(), false);
					}
				}
			}
			else if (node.getNodeType() == Node.COMMENT_NODE) {
				return firstLine(node.getNodeValue().trim(), false);
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
	 */
	public String getToolTipText(Object element) {
		if (element instanceof Node) {
			switch (((Node) element).getNodeType()) {
				case Node.COMMENT_NODE :
				case Node.CDATA_SECTION_NODE :
				case Node.PROCESSING_INSTRUCTION_NODE :
				case Node.TEXT_NODE : {
					String nodeValue = ((Node) element).getNodeValue().trim();
					return prepareText(nodeValue);
				}
				case Node.ELEMENT_NODE : {
					// show the preceding comment's tooltip information
					Node previous = ((Node) element).getPreviousSibling();
					if (previous != null && previous.getNodeType() == Node.TEXT_NODE)
						previous = previous.getPreviousSibling();
					if (previous != null && previous.getNodeType() == Node.COMMENT_NODE)
						return getToolTipText(previous);
				}
			}
		}
		return super.getToolTipText(element);
	}

	/**
	 * Remove leading indentation from each line in the give string.
	 * @param text
	 * @return
	 */
	private String prepareText(String text) {
		StringBuilder nodeText = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c != '\r' && c != '\n') {
				nodeText.append(c);
			}
			else if (c == '\r' || c == '\n') {
				nodeText.append('\n');
				while (Character.isWhitespace(c) && i < text.length()) {
					i++;
					c = text.charAt(i);
				}
				nodeText.append(c);
			}
		}
		return nodeText.toString();
	}

	public StyledString getStyledText(Object element) {
		if (element instanceof Node) {
			Node node = (Node) element;
			StyledString styleString = new StyledString();
			if (fShowAttributes && node.getNodeType() == Node.ELEMENT_NODE) {
				styleString.append(super.getText(node));
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
				if (node.hasAttributes()) {
					Node shownAttribute = getAttributeToShow((Element) node);
					if (shownAttribute != null) {
						// display the attribute and styled value
						String attributeName = shownAttribute.getNodeName();
						if (attributeName != null && attributeName.length() > 0) {
							String attributeValue = shownAttribute.getNodeValue();
							if (attributeValue != null) {
								styleString.append(" "); //$NON-NLS-1$
								styleString.append(attributeName, StyledString.DECORATIONS_STYLER);
								// https://bugs.eclipse.org/486252
								styleString.append("=", StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
								styleString.append(attributeValue, StyledString.DECORATIONS_STYLER);
							}
						}
					}
				}
				else {
					Node possibleText = node.getFirstChild();
					if (possibleText != null && possibleText.getNextSibling() == null && possibleText.getNodeType() == Node.TEXT_NODE) {
						styleString.append(" : ");
						styleString.append(firstLine(possibleText.getNodeValue().trim(), true), StyledString.DECORATIONS_STYLER);
					}
				}
			}
			else if (fShowAttributes && node.getNodeType() == Node.COMMENT_NODE) {
				styleString.append(firstLine(node.getNodeValue().trim(), true), StyledString.DECORATIONS_STYLER);
			}
			else {
				styleString.append(super.getText(node));
			}
			return styleString;
		}
		return new StyledString(getText(element));
	}

	/**
	 * @param doShow
	 *            - whether to include identifiers and content in the text
	 *            label. The caller must refresh the viewer themselves.
	 */
	public void showAttributes(boolean doShow) {
		fShowAttributes = doShow;
	}
}
