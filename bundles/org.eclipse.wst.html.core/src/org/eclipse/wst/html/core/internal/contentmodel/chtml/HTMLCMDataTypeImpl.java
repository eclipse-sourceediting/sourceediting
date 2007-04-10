/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.chtml;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;



/**
 */
class HTMLCMDataTypeImpl extends CMNodeImpl implements HTMLCMDataType {

	private int impliedValueKind = IMPLIED_VALUE_NONE;
	private String impliedValue = null;
	private final static String[] emptyArray = new String[0];
	private String[] enumValues = emptyArray;
	private String instanceValue = null;

	/**
	 * HTMLCMDataTypeImpl constructor comment.
	 * @param nm java.lang.String
	 */
	public HTMLCMDataTypeImpl(String typeName) {
		super(typeName);
	}

	/**
	 * HTMLCMDataTypeImpl constructor comment.
	 * @param nm java.lang.String
	 */
	public HTMLCMDataTypeImpl(String typeName, String instanceValue) {
		super(typeName);
		this.instanceValue = instanceValue;
	}

	/**
	 * getTypeName method
	 * @return java.lang.String
	 *
	 * This method returns a suitable default value that can be used when an instance of the data type is created.
	 * This returns null of a suitable default is not available.
	 */
	public String generateInstanceValue() {
		return instanceValue;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMDataType
	 */
	public String getDataTypeName() {
		return getNodeName();
	}

	/**
	 * getTypeName method
	 * @return java.lang.String[]
	 *
	 */
	public String[] getEnumeratedValues() {
		return enumValues;
	}

	/**
	 * getTypeName method
	 * @return java.lang.String  
	 *
	 * Returns the implied value or null if none exists.
	 */
	public String getImpliedValue() {
		return impliedValue;
	}

	/**
	 * getImpliedValueKind method
	 * @return int
	 *
	 * Returns one of :
	 * IMPLIED_VALUE_NONE, IMPLIED_VALUE_FIXED, IMPLIED_VALUE_DEFAULT.
	 */
	public int getImpliedValueKind() {
		return impliedValueKind;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	public int getNodeType() {
		return CMNode.DATA_TYPE;
	}

	/**
	 */
	void setEnumValues(String[] values) {
		enumValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			enumValues[i] = values[i];
		}
	}

	/**
	 * package scope.
	 */
	void setImpliedValue(int kind, String value) {
		switch (kind) {
			case IMPLIED_VALUE_FIXED :
			case IMPLIED_VALUE_DEFAULT :
				impliedValueKind = kind;
				impliedValue = value;
				break;
			case IMPLIED_VALUE_NONE :
			default :
				impliedValueKind = IMPLIED_VALUE_NONE;
				impliedValue = null; // maybe a null string?
				break;
		}
	}
}
