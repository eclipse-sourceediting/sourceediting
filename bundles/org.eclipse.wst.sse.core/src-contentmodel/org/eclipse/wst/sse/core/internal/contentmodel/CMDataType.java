/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.contentmodel;

/**
 * CMDataType
 */
public interface CMDataType extends CMNode {
    
  // todo... at some point we should remove these names to
  // avoid exposing implementation details via the interfaces
  //
	static final String CDATA = "CDATA";
	static final String ID = "ID";
	static final String IDREF = "IDREF";
	static final String ENTITY = "ENTITY";
	static final String ENTITIES = "ENTITIES";
	static final String NOTATION = "NOTATION";
	static final String NMTOKEN = "NMTOKEN";
	static final String NMTOKENS = "NMTOKENS";
	static final String NUMBER = "NUMBER";
	static final String URI = "URI";
	static final String ENUM = "ENUM";

  static final int IMPLIED_VALUE_NONE             = 1;
  static final int IMPLIED_VALUE_FIXED            = 2;
  static final int IMPLIED_VALUE_DEFAULT          = 3;

/**
 * getTypeName method
 * @return java.lang.String
 */
String getDataTypeName();    

/**
 * getImpliedValueKind method
 * @return int
 *
 * Returns one of :
 * IMPLIED_VALUE_NONE, IMPLIED_VALUE_FIXED, IMPLIED_VALUE_DEFAULT.
 */      

int getImpliedValueKind();  

/**
 * getTypeName method
 * @return java.lang.String  
 *
 * Returns the implied value or null if none exists.
 */
String getImpliedValue();

/**
 * getTypeName method
 * @return java.lang.String[]
 *
 */
String[] getEnumeratedValues();

/**
 * getTypeName method
 * @return java.lang.String
 *
 * This method returns a suitable default value that can be used when an instance of the data type is created.
 * This returns null of a suitable default is not available.
 */
String generateInstanceValue();
}
