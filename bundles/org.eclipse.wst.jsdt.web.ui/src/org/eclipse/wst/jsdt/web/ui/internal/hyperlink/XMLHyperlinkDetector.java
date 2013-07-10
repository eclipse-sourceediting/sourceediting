/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.hyperlink;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.icu.util.StringTokenizer;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class XMLHyperlinkDetector extends AbstractHyperlinkDetector {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.wst.jsdt.web.ui.internal.hyperlink
	private final String HTTP_PROTOCOL = "http://";//$NON-NLS-1$
	private final String NO_NAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation"; //$NON-NLS-1$
	private final String SCHEMA_LOCATION = "schemaLocation"; //$NON-NLS-1$
	private final String XMLNS = "xmlns"; //$NON-NLS-1$
	private final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
	
	/**
	 * Create the appropriate hyperlink
	 * 
	 * @param uriString
	 * @param hyperlinkRegion
	 * @return IHyperlink
	 */
	private IHyperlink createHyperlink(String uriString, IRegion hyperlinkRegion, IDocument document, Node node) {
		IHyperlink link = null;
		if (isHttp(uriString)) {
			link = new URLHyperlink(hyperlinkRegion, uriString);
		} else {
			// try to locate the file in the workspace
			File systemFile = getFileFromUriString(uriString);
			if (systemFile != null) {
				String systemPath = systemFile.getPath();
				IFile file = getFile(systemPath);
				if (file != null) {
					// this is a WorkspaceFileHyperlink since file exists in
					// workspace
					link = new WorkspaceFileHyperlink(hyperlinkRegion, file);
				} else {
					// this is an ExternalFileHyperlink since file does not
					// exist in workspace
					link = new ExternalFileHyperlink(hyperlinkRegion, systemFile);
				}
			}
		}
		return link;
	}
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		// for now, only capable of creating 1 hyperlink
		List hyperlinks = new ArrayList(0);
		if (region != null && textViewer != null) {
			IDocument document = textViewer.getDocument();
			Node currentNode = getCurrentNode(document, region.getOffset());
			if (currentNode != null) {
				String uriString = null;
				if (currentNode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
					// doctype nodes
					uriString = getURIString(currentNode, document);
				} else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					// element nodes
					Attr currentAttr = getCurrentAttrNode(currentNode, region.getOffset());
					if (currentAttr != null) {
						// try to find link for current attribute
						// resolve attribute value
						uriString = getURIString(currentAttr, document);
						// verify validity of uri string
						if (uriString == null || !isValidURI(uriString)) {
							// reset current attribute
							currentAttr = null;
						}
					}
					if (currentAttr == null) {
						// try to find a linkable attribute within element
						currentAttr = getLinkableAttr((Element) currentNode);
						if (currentAttr != null) {
							uriString = getURIString(currentAttr, document);
						}
					}
					currentNode = currentAttr;
				}
				// try to create hyperlink from information gathered
				if (uriString != null && currentNode != null && isValidURI(uriString)) {
					IRegion hyperlinkRegion = getHyperlinkRegion(currentNode);
					IHyperlink hyperlink = createHyperlink(uriString, hyperlinkRegion, document, currentNode);
					if (hyperlink != null) {
						hyperlinks.add(hyperlink);
					}
				}
			}
		}
		if (hyperlinks.size() == 0) {
			return null;
		}
		return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[0]);
	}
	
	/**
	 * Get the base location from the current model (local file system)
	 */
	private String getBaseLocation(IDocument document) {
		String baseLoc = null;
		// get the base location from the current model
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (sModel != null) {
				IPath location = new Path(sModel.getBaseLocation());
				if (location.toFile().exists()) {
					baseLoc = location.toString();
				} else {
					if (location.segmentCount() > 1) {
						baseLoc = ResourcesPlugin.getWorkspace().getRoot().getFile(location).getLocation().toString();
					} else {
						baseLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(location).toString();
					}
				}
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return baseLoc;
	}
	
	/**
	 * Get the CMElementDeclaration for an element
	 * 
	 * @param element
	 * @return CMElementDeclaration
	 */
	private CMElementDeclaration getCMElementDeclaration(Element element) {
		CMElementDeclaration ed = null;
		ModelQuery mq = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
		if (mq != null) {
			ed = mq.getCMElementDeclaration(element);
		}
		return ed;
	}
	
	/**
	 * Returns the attribute node within node at offset
	 * 
	 * @param node
	 * @param offset
	 * @return Attr
	 */
	private Attr getCurrentAttrNode(Node node, int offset) {
		if ((node instanceof IndexedRegion) && ((IndexedRegion) node).contains(offset) && (node.hasAttributes())) {
			NamedNodeMap attrs = node.getAttributes();
			// go through each attribute in node and if attribute contains
			// offset, return that attribute
			for (int i = 0; i < attrs.getLength(); ++i) {
				// assumption that if parent node is of type IndexedRegion,
				// then its attributes will also be of type IndexedRegion
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset)) {
					return (Attr) attrs.item(i);
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
	 * @return Node either element, doctype, text, or null
	 */
	private Node getCurrentNode(IDocument document, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null) {
				inode = sModel.getIndexedRegion(offset - 1);
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}
	
	/**
	 * Returns an IFile from the given uri if possible, null if cannot find file
	 * from uri.
	 * 
	 * @param fileString
	 *            file system path
	 * @return returns IFile if fileString exists in the workspace
	 */
	private IFile getFile(String fileString) {
		IFile file = null;
		if (fileString != null) {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(fileString));
			for (int i = 0; i < files.length && file == null; i++) {
				if (files[i].exists()) {
					file = files[i];
				}
			}
		}
		return file;
	}
	
	/**
	 * Create a file from the given uri string
	 * 
	 * @param uriString -
	 *            assumes uriString is not http://
	 * @return File created from uriString if possible, null otherwise
	 */
	private File getFileFromUriString(String uriString) {
		File file = null;
		try {
			// first just try to create a file directly from uriString as
			// default in case create file from uri does not work
			file = new File(uriString);
			// try to create file from uri
			URI uri = new URI(uriString);
			file = new File(uri);
		} catch (Exception e) {
			// if exception is thrown while trying to create File just ignore
			// and file will be null
		}
		return file;
	}
	
	private IRegion getHyperlinkRegion(Node node) {
		IRegion hyperRegion = null;
		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE) {
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			} else if (nodeType == Node.ATTRIBUTE_NODE) {
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				ITextRegion valueRegion = att.getValueRegion();
				if (valueRegion != null) {
					int regLength = valueRegion.getTextLength();
					String attValue = att.getValueRegionText();
					if (StringUtils.isQuoted(attValue)) {
						++regOffset;
						regLength = regLength - 2;
					}
					hyperRegion = new Region(regOffset, regLength);
				}
			}
		}
		return hyperRegion;
	}
	
	/**
	 * Attempts to find an attribute within element that is openable.
	 * 
	 * @param element -
	 *            cannot be null
	 * @return Attr attribute that can be used for open on, null if no attribute
	 *         could be found
	 */
	private Attr getLinkableAttr(Element element) {
		CMElementDeclaration ed = getCMElementDeclaration(element);
		// get the list of attributes for this node
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); ++i) {
			// check if this attribute is "openOn-able"
			Attr att = (Attr) attrs.item(i);
			if (isLinkableAttr(att, ed)) {
				return att;
			}
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
				if (namespaceURI.equalsIgnoreCase(publicId)) {
					return systemId;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the URI string
	 * 
	 * @param node -
	 *            assumes not null
	 */
	private String getURIString(Node node, IDocument document) {
		String resolvedURI = null;
		// need the base location, publicId, and systemId for URIResolver
		String baseLoc = null;
		String publicId = null;
		String systemId = null;
		short nodeType = node.getNodeType();
		// handle doc type node
		if (nodeType == Node.DOCUMENT_TYPE_NODE) {
			baseLoc = getBaseLocation(document);
			publicId = ((DocumentType) node).getPublicId();
			systemId = ((DocumentType) node).getSystemId();
		} else if (nodeType == Node.ATTRIBUTE_NODE) {
			// handle attribute node
			Attr attrNode = (Attr) node;
			String attrName = attrNode.getName();
			String attrValue = attrNode.getValue();
			attrValue = StringUtils.strip(attrValue);
			if (attrValue != null && attrValue.length() > 0) {
				baseLoc = getBaseLocation(document);
				// handle schemaLocation attribute
				String prefix = DOMNamespaceHelper.getPrefix(attrName);
				String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attrName);
				if ((XMLNS.equals(prefix)) || (XMLNS.equals(unprefixedName))) {
					publicId = attrValue;
					systemId = getLocationHint(attrNode.getOwnerElement(), publicId);
					if (systemId == null) {
						systemId = attrValue;
					}
				} else if ((XSI_NAMESPACE_URI.equals(DOMNamespaceHelper.getNamespaceURI(attrNode))) && (SCHEMA_LOCATION.equals(unprefixedName))) {
					// for now just use the first pair
					// need to look into being more precise
					StringTokenizer st = new StringTokenizer(attrValue);
					publicId = st.hasMoreTokens() ? st.nextToken() : null;
					systemId = st.hasMoreTokens() ? st.nextToken() : null;
					// else check if xmlns publicId = value
				} else {
					systemId = attrValue;
				}
			}
		}
		resolvedURI = resolveURI(baseLoc, publicId, systemId);
		return resolvedURI;
	}
	
	/**
	 * Returns true if this uriString is an http string
	 * 
	 * @param uriString
	 * @return true if uriString is http string, false otherwise
	 */
	private boolean isHttp(String uriString) {
		boolean isHttp = false;
		if (uriString != null) {
			String tempString = uriString.toLowerCase();
			if (tempString.startsWith(HTTP_PROTOCOL)) {
				isHttp = true;
			}
		}
		return isHttp;
	}
	
	/**
	 * Checks to see if the given attribute is openable. Attribute is openable
	 * if it is a namespace declaration attribute or if the attribute value is
	 * of type URI.
	 * 
	 * @param attr
	 *            cannot be null
	 * @param cmElement
	 *            CMElementDeclaration associated with the attribute (can be
	 *            null)
	 * @return true if this attribute is "openOn-able" false otherwise
	 */
	private boolean isLinkableAttr(Attr attr, CMElementDeclaration cmElement) {
		String attrName = attr.getName();
		String prefix = DOMNamespaceHelper.getPrefix(attrName);
		String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attrName);
		// determine if attribute is namespace declaration
		if ((XMLNS.equals(prefix)) || (XMLNS.equals(unprefixedName))) {
			return true;
		}
		// determine if attribute contains schema location
		if ((XSI_NAMESPACE_URI.equals(DOMNamespaceHelper.getNamespaceURI(attr))) && ((SCHEMA_LOCATION.equals(unprefixedName)) || (NO_NAMESPACE_SCHEMA_LOCATION.equals(unprefixedName)))) {
			return true;
		}
		// determine if attribute value is of type URI
		if (cmElement != null) {
			CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) cmElement.getAttributes().getNamedItem(attrName);
			if ((attrDecl != null) && (attrDecl.getAttrType() != null) && (CMDataType.URI.equals(attrDecl.getAttrType().getDataTypeName()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether the given uriString is really pointing to a file
	 * 
	 * @param uriString
	 * @return boolean
	 */
	private boolean isValidURI(String uriString) {
		boolean isValid = false;
		if (isHttp(uriString)) {
			isValid = true;
		} else {
			File file = getFileFromUriString(uriString);
			if (file != null) {
				isValid = file.isFile();
			}
		}
		return isValid;
	}
	
	/**
	 * Resolves the given URI information
	 * 
	 * @param baseLocation
	 * @param publicId
	 * @param systemId
	 * @return String resolved uri.
	 */
	private String resolveURI(String baseLocation, String publicId, String systemId) {
		// dont resolve if there's nothing to resolve
		if ((baseLocation == null) && (publicId == null) && (systemId == null)) {
			return null;
		}
		return URIResolverPlugin.createResolver().resolve(baseLocation, publicId, systemId);
	}
}
