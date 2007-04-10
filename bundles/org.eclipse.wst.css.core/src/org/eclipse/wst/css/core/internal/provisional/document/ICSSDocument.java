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



import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.stylesheets.MediaList;

/**
 * 
 */
public interface ICSSDocument extends ICSSNode {

	/**
	 * @return org.w3c.dom.css.CSSCharsetRule
	 */
	ICSSCharsetRule createCSSCharsetRule();

	/**
	 * @return org.w3c.dom.css.CSSFontFaceRule
	 */
	CSSFontFaceRule createCSSFontFaceRule();

	/**
	 * @return org.w3c.dom.css.CSSImportRule
	 */
	ICSSImportRule createCSSImportRule();

	/**
	 * @return org.w3c.dom.css.ICSSMediaRule
	 */
	ICSSMediaRule createCSSMediaRule();

	/**
	 * @return org.w3c.dom.css.CSSPageRule
	 */
	ICSSPageRule createCSSPageRule();

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 */
	ICSSPrimitiveValue createCSSPrimitiveValue(short primitiveType);

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            java.lang.String
	 */
	CSSRule createCSSRule(String rule);

	/**
	 * @return org.w3c.dom.css.CSSStyleDeclaration
	 */
	ICSSStyleDeclaration createCSSStyleDeclaration();

	/**
	 * @param propertyName
	 *            java.lang.String
	 */
	ICSSStyleDeclItem createCSSStyleDeclItem(String propertyName);

	/**
	 * @return org.w3c.dom.css.CSSStyleRule
	 */
	ICSSStyleRule createCSSStyleRule();

	/**
	 * @return org.w3c.dom.css.CSSUnknownRule
	 */
	CSSUnknownRule createCSSUnknownRule();

	/**
	 * @return org.w3c.dom.stylesheets.MediaList
	 */
	MediaList createMediaList();

	ICSSModel getModel();

	/**
	 * @return boolean
	 */
	boolean isDocument();
}
