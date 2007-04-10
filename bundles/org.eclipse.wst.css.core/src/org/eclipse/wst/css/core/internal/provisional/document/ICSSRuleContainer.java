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



import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;

/**
 * 
 */
public interface ICSSRuleContainer {

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	CSSRule appendRule(CSSRule rule) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newRule
	 *            org.w3c.dom.css.CSSRule
	 * @param refRule
	 *            org.w3c.dom.css.CSSRule
	 */
	CSSRule insertRuleBefore(CSSRule newRule, CSSRule refRule) throws DOMException;

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	CSSRule removeRule(CSSRule rule) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newChild
	 *            org.w3c.dom.css.CSSRule
	 * @param oldChild
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	CSSRule replaceRule(CSSRule newRule, CSSRule oldRule) throws org.w3c.dom.DOMException;
}
