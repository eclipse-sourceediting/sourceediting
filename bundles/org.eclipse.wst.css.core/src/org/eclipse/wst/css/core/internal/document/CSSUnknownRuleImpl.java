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
import org.w3c.dom.css.CSSUnknownRule;


/**
 * 
 */
class CSSUnknownRuleImpl extends CSSRuleImpl implements CSSUnknownRule {

	// TODO: This field is never read
	String fCssText = null;

	/**
	 * 
	 */
	CSSUnknownRuleImpl() {
		super();
	}

	CSSUnknownRuleImpl(CSSUnknownRuleImpl that) {
		super(that);
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSUnknownRuleImpl cloned = new CSSUnknownRuleImpl(this);

		return cloned;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return UNKNOWNRULE_NODE;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return UNKNOWN_RULE;
	}

	/**
	 * @param cssText
	 *            java.lang.String
	 */
	public void setCssText(String cssText) {
		this.fCssText = cssText;

		super.setCssText(cssText);
	}
}
