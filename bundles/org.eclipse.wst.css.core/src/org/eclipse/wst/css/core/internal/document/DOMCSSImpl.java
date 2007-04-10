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
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;


/**
 * 
 */
public class DOMCSSImpl {

	/**
	 * DOMCSSImpl constructor comment.
	 */
	public DOMCSSImpl() {
		super();
	}

	public static ICSSStyleDeclaration createCSSStyleDeclaration() throws org.w3c.dom.DOMException {
		CSSStyleDeclarationImpl document = new CSSStyleDeclarationImpl(true);

		return document;
	}

	/**
	 * @return org.w3c.dom.css.CSSStyleSheet
	 * @param title
	 *            java.lang.String
	 * @param media
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public static CSSStyleSheet createCSSStyleSheet(String title, String media) throws org.w3c.dom.DOMException {
		return new CSSStyleSheetImpl();
	}
}
