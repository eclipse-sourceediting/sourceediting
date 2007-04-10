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
 * CMElementDeclaration interface
 */
public interface CMElementDeclaration extends CMContent {
  
	public static final int ANY = 0;
	public static final int EMPTY = 1;
	public static final int ELEMENT = 2;
	public static final int MIXED = 3;
	public static final int PCDATA = 4;
	public static final int CDATA = 5; // todo... clarify this one
/**
 * getAttributes method
 * @return CMNamedNodeMap
 *
 * Returns CMNamedNodeMap of AttributeDeclaration
 */
CMNamedNodeMap getAttributes();
/**
 * getCMContent method
 * @return CMContent
 *
 * Returns the root node of this element's content model.
 * This can be an CMElementDeclaration or a CMGroup
 */
CMContent getContent();
/**
 * getContentType method
 * @return int
 *
 * Returns one of :
 * ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA.
 */
public int getContentType();
/**
 * getElementName method
 * @return java.lang.String
 */
String getElementName();

/**
 * getDataType method
 * @return java.lang.String
 */
CMDataType getDataType();

/**
 * getLocalElements method
 * @return CMNamedNodeMap
 *
 * Returns a list of locally defined elements.
 */
CMNamedNodeMap getLocalElements();
}
