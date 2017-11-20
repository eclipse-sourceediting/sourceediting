/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
 * CMNode interface
 */
public interface CMNode {        
  
  static final int ANY_ELEMENT           = 1;
	static final int ATTRIBUTE_DECLARATION = 2;
	static final int DATA_TYPE             = 3;
	static final int DOCUMENT              = 4;
	static final int ELEMENT_DECLARATION   = 5;
	static final int ENTITY_DECLARATION    = 6;
	static final int GROUP                 = 7;
  static final int NAME_SPACE            = 8;
  static final int DOCUMENTATION         = 9;
/**
 * getNodeName method
 * @return java.lang.String
 */
String getNodeName();
/**
 * getNodeType method
 * @return int
 *
 * Returns one of :
 *
 */
int getNodeType();

/**
 * supports method
 * @return boolean
 *
 * Returns true if the CMNode supports a specified property
 *
 */
boolean supports(String propertyName);

/**
 * getProperty method
 * @return java.lang.Object
 *
 * Returns the object property desciped by the propertyName
 *
 */
Object getProperty(String propertyName); //todo throw unsupported property exception

}
