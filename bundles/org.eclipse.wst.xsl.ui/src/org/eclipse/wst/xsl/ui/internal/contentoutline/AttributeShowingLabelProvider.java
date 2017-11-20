package org.eclipse.wst.xsl.ui.internal.contentoutline;

import java.util.List;

import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNamedNodeMapImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xsl.core.XSLCore;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AttributeShowingLabelProvider extends JFaceNodeLabelProvider {
	boolean fShowAttributes = false;
	static final String ATTR_NAME = "name"; //$NON-NLS-1$
	static final String ATTR_ID = "id"; //$NON-NLS-1$
	
	public AttributeShowingLabelProvider(boolean showAttributes) {
		fShowAttributes = showAttributes;
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object o) {
		StringBuffer text = null;
		if (o instanceof Node) {
			Node node = (Node) o;
			if ((node.getNodeType() == Node.ELEMENT_NODE) && fShowAttributes) {
				text = new StringBuffer(super.getText(o));
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
				if (node.hasAttributes()) {
					Element element = (Element) node;
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
						int i = 0;
						while ((i < attributes.getLength()) && (idTypedAttribute == null)) {
							Node attr = attributes.item(i);
							String attrName = attr.getNodeName();
							CMNamedNodeMap attributeDeclarationMap = elementDecl.getAttributes();
							
							CMNamedNodeMapImpl allAttributes = new CMNamedNodeMapImpl(attributeDeclarationMap);
							List nodes = ModelQueryUtil.getModelQuery(node.getOwnerDocument()).getAvailableContent(element, elementDecl, ModelQuery.INCLUDE_ATTRIBUTES);
							for (int k = 0; k < nodes.size(); k++) {
								CMNode cmnode = (CMNode) nodes.get(k);
								if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) {
									allAttributes.put(cmnode);
								}
							}
							attributeDeclarationMap = allAttributes;

							CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) attributeDeclarationMap.getNamedItem(attrName);
							if (attrDecl != null) {
								if ((attrDecl.getAttrType() != null) && (CMDataType.ID.equals(attrDecl.getAttrType().getDataTypeName()))) {
									idTypedAttribute = attr;
								}
								else if ((attrDecl.getUsage() == CMAttributeDeclaration.REQUIRED) && (requiredAttribute == null)) {
									// as a backup, keep tabs on
									// any required
									// attributes
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
					 * If no suitable attribute was found, try using a
					 * required attribute, if none, then prefer "id" or
					 * "name", otherwise just use first attribute
					 */
					if (idTypedAttribute != null) {
						shownAttribute = idTypedAttribute;
					}
					else if (requiredAttribute != null) {
						shownAttribute = requiredAttribute;
					}
					else if (hasId) {
						shownAttribute = attributes.getNamedItem(ATTR_ID);
					}
					else if (hasName) {
						shownAttribute = attributes.getNamedItem(ATTR_NAME);
					}
					if (shownAttribute == null) {
						shownAttribute = attributes.item(0);
					}

					// display the attribute and value (without quotes)
					String attributeName = shownAttribute.getNodeName();
					if ((attributeName != null) && (attributeName.length() > 0)) {
						text.append(" "); //$NON-NLS-1$
						text.append(attributeName);
						String attributeValue = shownAttribute.getNodeValue();
						if ((attributeValue != null) && (attributeValue.length() > 0)) {
							text.append("="); //$NON-NLS-1$
							text.append(StringUtils.strip(attributeValue));
						}
					}
					
//					if (XSLCore.XSL_NAMESPACE_URI.equals(node.getNamespaceURI())) {
//						Element el = (Element) node;
//						Attr attr = el.getAttributeNode("mode"); //$NON-NLS-1$
//						if (attr != null) {
//							text.append(" "); //$NON-NLS-1$
//							text.append(attr.getName());
//							text.append("="); //$NON-NLS-1$
//							text.append(StringUtils.strip(attr.getNodeValue()));
//						}
//					}
				}
			}
			else {
				text = new StringBuffer(super.getText(o));
			}
		}
		else {
			return super.toString();
		}
		return text.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
	 */
	@Override
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
					Element elem = (Element) element;
					if (XSLCore.isXSLNamespace((Node)element)) {
						if (elem.hasAttribute("mode")) { //$NON-NLS-1$
							return "Mode: " + elem.getAttribute("mode"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
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
		StringBuffer nodeText = new StringBuffer();
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
	
	public void setShowAttributes(boolean fShowAttributes) {
		this.fShowAttributes = fShowAttributes;
	}
}
