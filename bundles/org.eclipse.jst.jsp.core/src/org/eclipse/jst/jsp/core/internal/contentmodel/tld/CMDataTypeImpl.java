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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMDataTypeImpl implements CMDataType {

	protected String dataTypeName;

	public CMDataTypeImpl(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	/**
	 * getTypeName method
	 * @return java.lang.String
	 *
	 * This method returns a suitable default value that can be used when an instance of the data type is created.
	 * This returns null of a suitable default is not available.
	 */
	public String generateInstanceValue() {
		return null;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	/**
	 * getTypeName method
	 * @return java.lang.String[]
	 *
	 */
	public String[] getEnumeratedValues() {
		return null;
	}

	/**
	 * getTypeName method
	 * @return java.lang.String  
	 *
	 * Returns the implied value or null if none exists.
	 */
	public String getImpliedValue() {
		return null;
	}

	/**
	 * getImpliedValueKind method
	 * @return int
	 *
	 * Returns one of :
	 * IMPLIED_VALUE_NONE, IMPLIED_VALUE_FIXED, IMPLIED_VALUE_DEFAULT.
	 */
	public int getImpliedValueKind() {
		return IMPLIED_VALUE_NONE;
	}

	public String getNodeName() {
		return dataTypeName;
	}

	public int getNodeType() {
		return CMNode.DATA_TYPE;
	}

	/**
	 * getProperty method
	 * @return java.lang.Object
	 *
	 * Returns the object property described by the propertyName
	 *
	 */
	public Object getProperty(String propertyName) {
		return null;
	}

	/**
	 * supports method
	 * @return boolean
	 *
	 * Returns true if the CMNode supports a specified property
	 *
	 */
	public boolean supports(String propertyName) {
		return false;
	}
}
