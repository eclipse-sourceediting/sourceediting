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
package org.eclipse.wst.css.core.internal.parser;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;


public class CSSRegionUtil {

	/**
	 */
	public static boolean isDeclarationValueType(String type) {
		return (type == CSSRegionContexts.CSS_DECLARATION_VALUE_DIMENSION || type == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION || type == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH || type == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT || type == CSSRegionContexts.CSS_DECLARATION_VALUE_IMPORTANT || type == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR || type == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE || type == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE || type == CSSRegionContexts.CSS_DECLARATION_VALUE_S || type == CSSRegionContexts.CSS_DECLARATION_VALUE_STRING || type == CSSRegionContexts.CSS_DECLARATION_VALUE_UNICODE_RANGE || type == CSSRegionContexts.CSS_DECLARATION_VALUE_URI);
	}

	/**
	 * these type can be beggining of selector
	 */
	public static boolean isSelectorBegginingType(String type) {
		return (type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_UNIVERSAL || type == CSSRegionContexts.CSS_SELECTOR_PSEUDO || type == CSSRegionContexts.CSS_SELECTOR_CLASS || type == CSSRegionContexts.CSS_SELECTOR_ID || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_START);
	}


	/**
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isDeclarationType(String type) {
		return (type == CSSRegionContexts.CSS_DECLARATION_PROPERTY || type == CSSRegionContexts.CSS_DECLARATION_SEPARATOR || isDeclarationValueType(type) || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER);
	}

	/**
	 * These types consist selector region
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSelectorType(String type) {
		return (type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_END || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_NAME || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_OPERATOR || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_START || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_VALUE || type == CSSRegionContexts.CSS_SELECTOR_CLASS || type == CSSRegionContexts.CSS_SELECTOR_COMBINATOR || type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_ID || type == CSSRegionContexts.CSS_SELECTOR_PSEUDO || type == CSSRegionContexts.CSS_SELECTOR_SEPARATOR || type == CSSRegionContexts.CSS_SELECTOR_UNIVERSAL);
	}

}
