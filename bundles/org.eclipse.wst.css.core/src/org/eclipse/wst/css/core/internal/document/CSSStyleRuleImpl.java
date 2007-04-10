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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.w3c.dom.DOMException;


/**
 * 
 */
class CSSStyleRuleImpl extends CSSRuleDeclContainer implements ICSSStyleRule {

	ICSSSelectorList fSelectorList = new CSSSelectorListImpl(null);

	/**
	 * 
	 */
	CSSStyleRuleImpl() {
		super();
	}

	CSSStyleRuleImpl(CSSStyleRuleImpl that) {
		super(that);
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSStyleRuleImpl cloned = new CSSStyleRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * @return java.lang.String
	 */
	String extractPreString() {
		return getSelectorText();
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return STYLERULE_NODE;
	}

	public ICSSSelectorList getSelectors() {
		return fSelectorList;
	}

	/**
	 * The textual representation of the selector for the rule set. The
	 * implementation may have stripped out insignificant whitespace while
	 * parsing the selector.
	 * 
	 * @exception DOMException
	 *                SYNTAX_ERR: Raised if the specified CSS string value has
	 *                a syntax error and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this rule is
	 *                readonly.
	 */
	public String getSelectorText() {
		return getAttribute(SELECTOR);
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return STYLE_RULE;
	}

	/**
	 * 
	 */
	public void setSelectorText(String selectorText) throws DOMException {
		setAttribute(SELECTOR, selectorText);
		fSelectorList = new CSSSelectorListImpl(selectorText);
	}
}
