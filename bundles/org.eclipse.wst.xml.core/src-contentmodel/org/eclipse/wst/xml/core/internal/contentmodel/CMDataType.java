/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel;

/**
 * CMDataType
 */
public interface CMDataType extends CMNode {
    
  // todo... at some point we should remove these names to
  // avoid exposing implementation details via the interfaces
  //
	static final String CDATA = "CDATA"; //$NON-NLS-1$
	static final String ID = "ID"; //$NON-NLS-1$
	static final String IDREF = "IDREF"; //$NON-NLS-1$
	static final String IDREFS = "IDREFS"; //$NON-NLS-1$
	static final String ENTITY = "ENTITY"; //$NON-NLS-1$
	static final String ENTITIES = "ENTITIES"; //$NON-NLS-1$
	static final String NOTATION = "NOTATION"; //$NON-NLS-1$
	static final String NMTOKEN = "NMTOKEN"; //$NON-NLS-1$
	static final String NMTOKENS = "NMTOKENS"; //$NON-NLS-1$
	static final String NUMBER = "NUMBER"; //$NON-NLS-1$
	static final String URI = "URI"; //$NON-NLS-1$
	static final String ENUM = "ENUM"; //$NON-NLS-1$

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
