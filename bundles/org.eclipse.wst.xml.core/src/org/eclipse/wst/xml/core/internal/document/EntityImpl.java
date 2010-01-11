/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

/**
 * EntityImpl class
 */
public class EntityImpl extends NodeImpl implements Entity {

	private String name = null;
	private String notationName = null;
	private String publicId = null;
	private String systemId = null;

	/**
	 * EntityImpl constructor
	 */
	protected EntityImpl() {
		super();
	}

	/**
	 * EntityImpl constructor
	 * 
	 * @param that
	 *            EntityImpl
	 */
	protected EntityImpl(EntityImpl that) {
		super(that);

		if (that != null) {
			this.name = that.name;
			this.publicId = that.publicId;
			this.systemId = that.systemId;
			this.notationName = that.notationName;
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
		EntityImpl cloned = new EntityImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the text declaration, the encoding
	 * of this entity, when it is an external parsed entity. This is
	 * <code>null</code> otherwise.
	 * 
	 * @see DOM Level 3
	 */
	public java.lang.String getEncoding() {
		return null;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		if (this.name == null)
			return NodeImpl.EMPTY_STRING;
		return this.name;
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return ENTITY_NODE;
	}

	/**
	 * getNotationName method
	 * 
	 * @return java.lang.String
	 */
	public String getNotationName() {
		return this.notationName;
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
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the text declaration, the version
	 * number of this entity, when it is an external parsed entity. This is
	 * <code>null</code> otherwise.
	 * 
	 * @see DOM Level 3
	 */
	public java.lang.String getVersion() {
		return null;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the text declaration, the encoding
	 * of this entity, when it is an external parsed entity. This is
	 * <code>null</code> otherwise.
	 * 
	 * @see DOM Level 3
	 */
	public void setEncoding(java.lang.String encoding) {
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
	 * setNotationName method
	 * 
	 * @param notationName
	 *            java.lang.String
	 */
	public void setNotationName(String notationName) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.notationName = notationName;
	}

	/**
	 * setPublicId method
	 * 
	 * @param publicId
	 *            java.lang.String
	 */
	public void setPublicId(String publicId) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.publicId = publicId;
	}

	/**
	 * setSystemId method
	 * 
	 * @param systemId
	 *            java.lang.String
	 */
	public void setSystemId(String systemId) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.systemId = systemId;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the text declaration, the version
	 * number of this entity, when it is an external parsed entity. This is
	 * <code>null</code> otherwise.
	 * 
	 * @see DOM Level 3
	 */
	public void setVersion(java.lang.String version) {
	}

	public String getInputEncoding() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version"); //$NON-NLS-1$
	}

	public String getXmlEncoding() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version"); //$NON-NLS-1$
	}


	public String getXmlVersion() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version"); //$NON-NLS-1$
	}

}
