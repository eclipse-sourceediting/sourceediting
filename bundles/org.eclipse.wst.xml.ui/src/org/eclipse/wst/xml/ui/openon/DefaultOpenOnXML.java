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
package org.eclipse.wst.xml.ui.openon;



import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.common.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.common.contentmodel.CMDataType;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.common.uriresolver.URIResolverPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.openon.AbstractOpenOn;
import org.eclipse.wst.xml.core.document.XMLAttr;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * This action class retrieves the link/file selected by the cursor and
 * attempts to open the link/file in the default editor or web browser
 * 
 * @deprecated Use base support for hyperlink navigation
 */
public class DefaultOpenOnXML extends AbstractOpenOn {
	private final String NO_NAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation"; //$NON-NLS-1$
	private final String SCHEMA_LOCATION = "schemaLocation"; //$NON-NLS-1$
	private final String XMLNS = "xmlns"; //$NON-NLS-1$
	private final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.AbstractOpenOn#doGetOpenOnRegion(int)
	 */
	protected IRegion doGetOpenOnRegion(int offset) {
		// find the element for this node
		Node currNode = getCurrentNode(offset);
		if (currNode != null) {
			// handle doc type node
			if (currNode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				XMLNode docNode = (XMLNode) currNode;
				return new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			}

			Attr linkableAtt = getLinkableAttrFromNode(currNode, offset);
			// found attribute to open on
			if (linkableAtt != null) {
				XMLAttr att = (XMLAttr) linkableAtt;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				int regLength = att.getValueRegion().getTextLength();
				String attValue = att.getValueRegionText();
				if (StringUtils.isQuoted(attValue)) {
					regOffset = ++regOffset;
					regLength = regLength - 2;
				}
				return new Region(regOffset, regLength);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.AbstractOpenOn#doOpenOn(org.eclipse.jface.text.IRegion)
	 */
	protected void doOpenOn(IRegion region) {
		String uriString = getURIString(region.getOffset());
		openFileInEditor(uriString);
	}

	/**
	 * Get the base location from the current model
	 * 
	 */
	protected String getBaseLocation() {
		String baseLoc = null;

		// get the base location from the current model
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
			if (sModel != null) {
				IPath location = new Path(sModel.getBaseLocation());
				if (location.toFile().exists()) {
					baseLoc = location.toString();
				}
				else {
					IPath basePath = new Path(sModel.getBaseLocation());
					if(basePath.segmentCount() > 1)
						baseLoc = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath).getLocation().toString();
					else
						baseLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(basePath).toString();
				}
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return baseLoc;
	}

	/**
	 * Returns the attribute node within node at offset
	 * 
	 * @param node
	 * @param offset
	 * @return Node
	 */
	private Node getCurrentAttrNode(Node node, int offset) {
		if ((node instanceof IndexedRegion) && ((IndexedRegion) node).contains(offset) && (node.hasAttributes())) {
			NamedNodeMap attrs = node.getAttributes();
			// go through each attribute in node and if attribute contains
			// offset, return that attribute
			for (int i = 0; i < attrs.getLength(); ++i) {
				// assumption that if parent node is of type IndexedRegion,
				// then its attributes will also be of type IndexedRegion
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset)) {
					return attrs.item(i);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the node the cursor is currently on in the document. null if no
	 * node is selected
	 * 
	 * @param offset
	 * @return Node
	 */
	protected Node getCurrentNode(int offset) {
		// get the current node at the offset (returns element, doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		inode = sModel.getIndexedRegion(offset);
		if (inode == null)
			inode = sModel.getIndexedRegion(offset - 1);
		sModel.releaseFromRead();

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}

	/**
	 * Return an attr of element that is "openOn-able" if one exists. null
	 * otherwise
	 * 
	 * @param element -
	 *            cannot be null
	 * @return Attr attribute that can be used for open on, null if no
	 *         attribute could be found
	 */
	protected Attr getLinkableAttr(Element element) {
		ModelQuery mq = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
		if (mq != null) {
			// get the list of attributes for this node
			NamedNodeMap attrs = element.getAttributes();
			for (int i = 0; i < attrs.getLength(); ++i) {
				// check if this attribute is "openOn-able"
				Attr att = (Attr) attrs.item(i);
				if (isLinkableAttr(att, mq)) {
					return att;
				}
			}
		}
		return null;
	}

	/**
	 * Return an attr of element that is "openOn-able" if one exists. null
	 * otherwise
	 * 
	 * @param node -
	 *            cannot be null
	 * @return Attr attribute that can be used for open on, null if no
	 *         attribute could be found
	 */
	protected Attr getLinkableAttrFromNode(Node node, int offset) {
		// check to see if we're already on an attribute we can work with
		Attr currentAtt = null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			currentAtt = (Attr) node;
		}
		else {
			Node attN = getCurrentAttrNode(node, offset);
			if (attN != null)
				currentAtt = (Attr) attN;
		}
		if ((currentAtt != null) && isLinkableAttr(currentAtt, null)) {
			return currentAtt;
		}

		// now check the whole element tag and see if there's an attribute we
		// can work with
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			// find an attribute that is "openOn-able"
			return getLinkableAttr((Element) node);
		}
		return null;
	}

	/**
	 * Find the location hint for the given namespaceURI if it exists
	 * 
	 * @param elementNode -
	 *            cannot be null
	 * @param namespaceURI -
	 *            cannot be null
	 * @return location hint (systemId) if it was found, null otherwise
	 */
	private String getLocationHint(Element elementNode, String namespaceURI) {
		Attr schemaLocNode = elementNode.getAttributeNodeNS(XSI_NAMESPACE_URI, SCHEMA_LOCATION);
		if (schemaLocNode != null) {
			StringTokenizer st = new StringTokenizer(schemaLocNode.getValue());
			while (st.hasMoreTokens()) {
				String publicId = st.hasMoreTokens() ? st.nextToken() : null;
				String systemId = st.hasMoreTokens() ? st.nextToken() : null;
				// found location hint
				if (namespaceURI.equalsIgnoreCase(publicId))
					return systemId;
			}
		}
		return null;
	}

	/**
	 * Returns the URI string
	 * 
	 * @param offset
	 */
	protected String getURIString(int offset) {
		Node currNode = getCurrentNode(offset);
		if (currNode != null) {
			// need the base location, publicId, and systemId for URIResolver
			String baseLoc = null;
			String publicId = null;
			String systemId = null;

			// handle doc type node
			if (currNode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				baseLoc = getBaseLocation();
				publicId = ((DocumentType) currNode).getPublicId();
				systemId = ((DocumentType) currNode).getSystemId();
			}
			else { // handle all other types of nodes
				Attr linkableAtt = getLinkableAttrFromNode(currNode, offset);
				// found attribute to open on
				if (linkableAtt != null) {
					baseLoc = getBaseLocation();
					String attrName = linkableAtt.getName();
					String attrValue = linkableAtt.getValue();
					attrValue = StringUtils.strip(attrValue);

					// handle schemaLocation attribute
					String prefix = DOMNamespaceHelper.getPrefix(attrName);
					String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attrName);
					if ((XMLNS.equals(prefix)) || (XMLNS.equals(unprefixedName))) {
						publicId = attrValue;
						systemId = getLocationHint(linkableAtt.getOwnerElement(), publicId);
					}
					else if ((XSI_NAMESPACE_URI.equals(DOMNamespaceHelper.getNamespaceURI(linkableAtt))) && (unprefixedName.equals("schemaLocation"))) { //$NON-NLS-1$
						// for now just use the first pair
						// need to look into being more precise
						StringTokenizer st = new StringTokenizer(attrValue);
						publicId = st.hasMoreTokens() ? st.nextToken() : null;
						systemId = st.hasMoreTokens() ? st.nextToken() : null;
						// else check if xmlns publicId = value
					}
					else {
						systemId = attrValue;
					}
				}
			}

			String resolvedURI = resolveURI(baseLoc, publicId, systemId);
			return resolvedURI;
		}
		return null;
	}

	/**
	 * Checks to see if the given attribute is openOn-able. Attribute is
	 * openOn-able if it is a namespace declaration attribute or if the
	 * attribute value is of type URI.
	 * 
	 * @param attr
	 *            cannot be null
	 * @param query
	 *            ModelQuery associated with the attribute (can be null)
	 * @return true if this attribute is "openOn-able" false otherwise
	 */
	protected boolean isLinkableAttr(Attr attr, ModelQuery query) {
		String prefix = DOMNamespaceHelper.getPrefix(attr.getName());
		String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attr.getName());
		// determine if attribute is namespace declaration
		if ((XMLNS.equals(prefix)) || (XMLNS.equals(unprefixedName)))
			return true;

		// determine if attribute contains schema location
		if ((XSI_NAMESPACE_URI.equals(DOMNamespaceHelper.getNamespaceURI(attr))) && ((SCHEMA_LOCATION.equals(unprefixedName)) || (NO_NAMESPACE_SCHEMA_LOCATION.equals(unprefixedName))))
			return true;

		// determine if attribute value is of type URI
		if (query != null) {
			CMAttributeDeclaration attrDecl = query.getCMAttributeDeclaration(attr);
			if ((attrDecl != null) && (attrDecl.getAttrType() != null) && (CMDataType.URI.equals(attrDecl.getAttrType().getDataTypeName()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Resolves the given URI information
	 * 
	 * @param baseLocation
	 * @param publicId
	 * @param systemId
	 * @return String resolved uri.
	 */
	protected String resolveURI(String baseLocation, String publicId, String systemId) {
		// dont resolve if there's nothing to resolve
		if ((baseLocation == null) && (publicId == null) && (systemId == null))
			return null;
		return URIResolverPlugin.createResolver().resolve(baseLocation, publicId, systemId);
	}
}
