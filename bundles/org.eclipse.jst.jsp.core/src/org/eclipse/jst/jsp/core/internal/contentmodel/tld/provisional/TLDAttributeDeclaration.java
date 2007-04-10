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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;



import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

/**
 * Represents an attribute definition from a TLD
 */
public interface TLDAttributeDeclaration extends CMAttributeDeclaration {

	/**
	 * a description of the attribute
	 * @return String
	 * @see  JSP 2.0
	 */
	String getDescription();
	
	/**
	 * the attribute's name
	 * @see  JSP 1.1
	 */
	String getId();

	CMDocument getOwnerDocument();

	/**
	 * whether the attribute's value may be dynamically calculated at runtime by an expression
	 * @see  JSP 1.1
	 */
	String getRtexprvalue();

	/**
	 * the type of the attribute's value
	 * @see  JSP 1.2
	 */
	String getType();

	/**
	 * whether this attribute is a fragment
	 * 
	 * @return boolean
	 */
	boolean isFragment();
	
	/**
	 * if the attribute is required or optional
	 * @see  JSP 1.1
	 */
	boolean isRequired();
}
