/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Implementation class of {@link <code>HTMLAttributeDeclaration</code>} interface.<br>
 */
class HTMLAttrDeclImpl extends CMNodeImpl implements HTMLAttributeDeclaration {

	private HTMLCMDataTypeImpl type = null;
	private int usage = 0;
	private boolean is_obsolete = false;


	/**
	 */
	public HTMLAttrDeclImpl(String attrName, HTMLCMDataTypeImpl valueType, int valueUsage) {
		super(attrName);
		this.type = valueType;

		switch (valueUsage) {
			case OPTIONAL :
			case REQUIRED :
			case FIXED :
			case PROHIBITED :
				this.usage = valueUsage;
				break;
			default :
				// should warn...
				this.usage = OPTIONAL; // fall back
				break;
		}
	}

	/**
	 * getAttrName method
	 * @return java.lang.String
	 */
	public String getAttrName() {
		return getNodeName();
	}

	/**
	 * getAttrType method
	 * @return CMDataType
	 */
	public CMDataType getAttrType() {
		return type;
	}

	/**
	 * @deprecated by superinterface
	 */
	public String getDefaultValue() {
		if (type.getImpliedValueKind() != CMDataType.IMPLIED_VALUE_DEFAULT)
			return null;
		return type.getImpliedValue();
	}

	/**
	 * @deprecated by superinterface
	 */
	public Enumeration getEnumAttr() {
		Vector v = new Vector(Arrays.asList(type.getEnumeratedValues()));
		return v.elements();
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 * ELEMENT_DECLARATION, ATTRIBUTE_DECLARATION, GROUP, ENTITY_DECLARATION.
	 */
	public int getNodeType() {
		return CMNode.ATTRIBUTE_DECLARATION;
	}

	/**
	 * @return int
	 */
	public int getUsage() {
		return usage;
	}

	/**
	 */
	public boolean supports(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return true;
		else if (propertyName.equals(HTMLCMProperties.IS_SCRIPTABLE))
			return true;
		else if (propertyName.equals(HTMLCMProperties.IS_OBSOLETE))
			return is_obsolete;
		
		return super.supports(propertyName);
	}

	/**
	 */
	public Object getProperty(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return new Boolean(true);
		else if (propertyName.equals(HTMLCMProperties.IS_SCRIPTABLE)) {
			return getAttrType().getDataTypeName() == HTMLCMDataType.SCRIPT ? new Boolean(true) : new Boolean(false);
		}
		else if (propertyName.equals(HTMLCMProperties.IS_OBSOLETE))
			return new Boolean(is_obsolete);
		
		return super.getProperty(propertyName);
	}
	
	public void obsolete(boolean is_obsolete){
		this.is_obsolete = is_obsolete;
	}
}
