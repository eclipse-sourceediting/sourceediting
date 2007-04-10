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
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;


/**
 * 
 */
class CSSPageRuleImpl extends CSSRuleDeclContainer implements ICSSPageRule {

	ICSSSelectorList fSelectorList = new CSSSelectorListImpl(null);

	/**
	 * 
	 */
	CSSPageRuleImpl() {
		super();
	}

	CSSPageRuleImpl(CSSPageRuleImpl that) {
		super(that);
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSPageRuleImpl cloned = new CSSPageRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * @return java.lang.String
	 */
	String extractPreString() {
		StringBuffer preStr = new StringBuffer("@page ");//$NON-NLS-1$
		preStr.append(getSelectorText());

		return preStr.toString();
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return PAGERULE_NODE;
	}

	public ICSSSelectorList getSelectors() {
		return fSelectorList;
	}

	/**
	 * The parsable textual representation of the page selector for the rule.
	 * 
	 * @exception org.w3c.dom.DOMException
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
		return PAGE_RULE;
	}

	/**
	 * 
	 */
	public void setSelectorText(String selectorText) throws org.w3c.dom.DOMException {
		setAttribute(SELECTOR, selectorText);
		fSelectorList = new CSSSelectorListImpl(selectorText);
	}
}
