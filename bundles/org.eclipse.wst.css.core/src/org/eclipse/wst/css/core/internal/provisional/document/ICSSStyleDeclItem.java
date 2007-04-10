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
package org.eclipse.wst.css.core.internal.provisional.document;



import org.w3c.dom.css.CSSValue;

/**
 * 
 */
public interface ICSSStyleDeclItem extends ICSSNode, ICSSValueList {

	java.lang.String IMPORTANT = "important"; //$NON-NLS-1$

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param value
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	ICSSPrimitiveValue appendValue(ICSSPrimitiveValue value) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.css.CSSValue
	 */
	CSSValue getCSSValue();

	/**
	 * @return java.lang.String
	 */
	String getPriority();

	/**
	 * @return java.lang.String
	 */
	String getPropertyName();

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param value
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	ICSSPrimitiveValue removeValue(ICSSPrimitiveValue value) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param newValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @param oldValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	ICSSPrimitiveValue replaceValue(ICSSPrimitiveValue newValue, ICSSPrimitiveValue oldValue) throws org.w3c.dom.DOMException;

	/**
	 * @param priority
	 *            java.lang.String
	 */
	void setPriority(String priority);
}
