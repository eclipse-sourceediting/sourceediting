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
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * DocumentType class
 */
public class DocumentTypeImpl extends NodeImpl implements IDOMDocumentType {
	private String internalSubset = null;

	private String name = null;
	private String publicId = null;
	private String systemId = null;

	/**
	 * DocumentTypeImpl constructor
	 */
	protected DocumentTypeImpl() {
		super();
	}

	/**
	 * DocumentTypeImpl constructor
	 * 
	 * @param that
	 *            DocumentTypeImpl
	 */
	protected DocumentTypeImpl(DocumentTypeImpl that) {
		super(that);

		if (that != null) {
			this.name = that.name;
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		DocumentTypeImpl cloned = new DocumentTypeImpl(this);
		return cloned;
	}

	/**
	 * getEntities method
	 * 
	 * @return org.w3c.dom.NamedNodeMap
	 */
	public NamedNodeMap getEntities() {
		return null;
	}

	/**
	 */
	public String getInternalSubset() {
		return this.internalSubset;
	}

	/**
	 * getName method
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		if (this.name == null)
			return new String();
		return this.name;
	}

	/**
	 * getNodeName
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return getName();
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return DOCUMENT_TYPE_NODE;
	}

	/**
	 * getNotations method
	 * 
	 * @return org.w3c.dom.NamedNodeMap
	 */
	public NamedNodeMap getNotations() {
		return null;
	}

	/**
	 * getPublicId method
	 * 
	 * @return java.lang.String
	 */
	public String getPublicId() {
		return this.publicId;
	}

	/**
	 * getSystemId method
	 * 
	 * @return java.lang.String
	 */
	public String getSystemId() {
		return this.systemId;
	}

	/**
	 */
	public boolean isClosed() {
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return true; // will be generated
		String regionType = StructuredDocumentRegionUtil.getLastRegionType(flatNode);
		return (regionType == DOMRegionContext.XML_DOCTYPE_DECLARATION_CLOSE || regionType == DOMRegionContext.XML_DECLARATION_CLOSE);
	}

	/**
	 */
	public void setInternalSubset(String internalSubset) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, new String());
		}

		this.internalSubset = internalSubset;
	}

	/**
	 * setName method
	 * 
	 * @param name
	 *            java.lang.String
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * setPublicId method
	 * 
	 * @param publicId
	 *            java.lang.String
	 */
	public void setPublicId(String publicId) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, new String());
		}

		this.publicId = publicId;

		notifyValueChanged();
	}

	/**
	 * setSystemId method
	 * 
	 * @param systemId
	 *            java.lang.String
	 */
	public void setSystemId(String systemId) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, new String());
		}

		this.systemId = systemId;

		notifyValueChanged();
	}

	/**
	 * toString method
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getName());
		buffer.append('(');
		buffer.append(getPublicId());
		buffer.append(')');
		buffer.append('(');
		buffer.append(getSystemId());
		buffer.append(')');
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode != null) {
			buffer.append('@');
			buffer.append(flatNode.toString());
		}
		return buffer.toString();
	}
}
