/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.document;



import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * 
 */
public interface ICSSStyleDeclaration extends ICSSDocument, CSSStyleDeclaration {

	/**
	 * @return com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @param propertyName
	 *            java.lang.String
	 */
	ICSSStyleDeclItem getDeclItemNode(String propertyName);

	/**
	 * @return com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @param oldDecl
	 *            com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	ICSSStyleDeclItem removeDeclItemNode(ICSSStyleDeclItem oldDecl) throws org.w3c.dom.DOMException;

	/**
	 * @return com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @param newDecl
	 *            com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	ICSSStyleDeclItem setDeclItemNode(ICSSStyleDeclItem newDecl) throws org.w3c.dom.DOMException;
}