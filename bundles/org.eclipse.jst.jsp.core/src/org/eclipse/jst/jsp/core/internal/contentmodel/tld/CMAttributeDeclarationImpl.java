/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import java.util.Enumeration;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDAttributeDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMAttributeDeclarationImpl implements TLDAttributeDeclaration {

	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=170556
	private static final CMDataType attrType = new CMDataTypeImpl(CMDataType.CDATA);
	
	private String fDescription;

	//
	private boolean fIsFragment = false;

	private CMDocument fOwnerDocument;
	// optional id
	private String id = null;

	// required name
	private String name = null;
	// optional "required" element present, defaults to not present
	private boolean required = false;

	// optional run-time (scriplet derived) value of attributes, defaults to
	// none/false
	private String rtexprvalue = null;

	private String type = null;

	/**
	 * CMAttributeDeclarationImpl constructor comment.
	 */
	public CMAttributeDeclarationImpl(CMDocument owner) {
		super();
		fOwnerDocument = owner;
	}

	public CMAttributeDeclarationImpl(CMDocument owner, String defaultRtexprvalue) {
		this(owner);
		rtexprvalue = defaultRtexprvalue;
	}

	/**
	 * getAttrName method
	 * 
	 * @return java.lang.String
	 */
	public String getAttrName() {
		return getNodeName();
	}

	/**
	 * getAttrType method
	 * 
	 * @return CMDataType
	 */
	public CMDataType getAttrType() {
		return attrType;
	}

	/**
	 * @deprecated in superclass
	 */
	public String getDefaultValue() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * @deprecated in superclass
	 */
	public Enumeration getEnumAttr() {
		return null;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return name;
	}

	/**
	 * getNodeType method
	 * 
	 * @return int
	 * 
	 * Returns one of :
	 * 
	 */
	public int getNodeType() {
		return CMNode.ATTRIBUTE_DECLARATION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.jsp.core.contentmodel.tld.TLDAttributeDeclaration#getOwnerDocument()
	 */
	public CMDocument getOwnerDocument() {
		return fOwnerDocument;
	}

	/**
	 * getProperty method
	 * 
	 * @return java.lang.Object
	 * 
	 * Returns the object property described by the propertyName
	 * 
	 */
	public Object getProperty(String propertyName) {
		if (propertyName != null && propertyName.equals(JSP12TLDNames.DESCRIPTION)) {
			return getDescription(); // return attribute description
		}
		else if (propertyName.equals(TLDDocument.CM_KIND)) {
			return TLDDocument.JSP_TLD;
		}
		else if (propertyName.equals(JSP12TLDNames.SMALL_ICON) || propertyName.equals(JSP12TLDNames.LARGE_ICON)) {
			return getOwnerDocument().getProperty(propertyName);
		}
		return null;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getRtexprvalue() {
		return rtexprvalue;
	}

	public String getType() {
		return type;
	}

	/**
	 * getUsage method
	 * 
	 * @return int OPTIONAL|REQUIRED
	 */
	public int getUsage() {
		return required ? REQUIRED : OPTIONAL;
	}

	/**
	 * @return Returns the isFragment.
	 */
	public boolean isFragment() {
		return fIsFragment;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		fDescription = description;
	}

	/**
	 * @param isFragment
	 *            The isFragment to set.
	 */
	public void setFragment(boolean isFragment) {
		fIsFragment = isFragment;
	}

	/**
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(String newId) {
		id = newId;
	}

	public void setNodeName(String string) {
		name = string;
	}

	/**
	 * 
	 * @param newRequired
	 *            boolean
	 */
	public void setRequired(boolean newRequired) {
		required = newRequired;
	}

	/**
	 * 
	 * @param newRequired
	 *            boolean
	 */
	public void setRequiredString(String newRequired) {
		if (newRequired != null) {
			if (newRequired.equalsIgnoreCase(JSP12TLDNames.TRUE) || newRequired.equalsIgnoreCase(JSP12TLDNames.YES))
				setRequired(true);
			else if (newRequired.equalsIgnoreCase(JSP12TLDNames.FALSE) || newRequired.equalsIgnoreCase(JSP12TLDNames.NO))
				setRequired(false);
		}
	}

	/**
	 * 
	 * @param newRtexprvalue
	 *            java.lang.String
	 */
	public void setRtexprvalue(String newRtexprvalue) {
		rtexprvalue = newRtexprvalue;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * supports method
	 * 
	 * @return boolean
	 * 
	 * Returns true if the CMNode supports a specified property
	 * 
	 */
	public boolean supports(String propertyName) {
		return propertyName == null || propertyName.equals(JSP12TLDNames.DESCRIPTION) || propertyName.equals(TLDDocument.CM_KIND) || propertyName.equals(JSP12TLDNames.SMALL_ICON) || propertyName.equals(JSP12TLDNames.LARGE_ICON);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n\t " + super.toString()); //$NON-NLS-1$
		buffer.append("\n\t name:" + StringUtils.escape(getNodeName())); //$NON-NLS-1$
		// Boolean.toString(boolean) is introduced in 1.4
		// buffer.append("\n\t required:" +
		// StringUtils.escape(Boolean.toString(isRequired())));
		buffer.append("\n\t required:" + StringUtils.toString(isRequired())); //$NON-NLS-1$
		buffer.append("\n\t rtexpr:" + StringUtils.escape(getRtexprvalue())); //$NON-NLS-1$
		if (getId() != null)
			buffer.append("\n\t id:" + StringUtils.escape(getId())); //$NON-NLS-1$
		return buffer.toString();
	}
}
