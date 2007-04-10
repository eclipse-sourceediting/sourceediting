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



import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * 
 */
public interface ICSSPrimitiveValue extends ICSSNode, ICSSValue, CSSPrimitiveValue {

	short CSS_INTEGER = 26;
	short CSS_HASH = 27;
	short CSS_URANGE = 28;
	short CSS_FORMAT = 29;
	short CSS_LOCAL = 30;
	short CSS_SLASH = 31;
	short CSS_COMMA = 32;
	short CSS_INHERIT_PRIMITIVE = 33;

	/**
	 * @param floatValue
	 *            float
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	void setValue(float floatValue) throws org.w3c.dom.DOMException;

	/**
	 * @param stringValue
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	void setValue(String stringValue) throws org.w3c.dom.DOMException;
}
