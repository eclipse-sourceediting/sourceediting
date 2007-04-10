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
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;


/**
 * 
 */
abstract class CSSRuleImpl extends CSSStructuredDocumentRegionContainer implements CSSRule {

	/**
	 * 
	 */
	CSSRuleImpl() {
		super();
	}

	CSSRuleImpl(CSSRuleImpl that) {
		super(that);
	}

	/**
	 * If this rule is contained inside another rule (e.g. a style rule inside
	 * an
	 * 
	 * @media block), this is the containing rule. If this rule is not nested
	 *        inside any other rules, this returns <code>null</code>.
	 */
	public CSSRule getParentRule() {
		ICSSNode rule = getParentNode();

		// parent rule is used only if parent is media rule
		if (rule instanceof CSSMediaRuleImpl)
			return (CSSRule) rule;

		return null;
	}

	/**
	 * The style sheet that contains this rule.
	 */
	public CSSStyleSheet getParentStyleSheet() {
		ICSSNode parent = getParentNode();
		while (parent != null) {
			if (parent instanceof CSSStyleSheetImpl)
				return (CSSStyleSheet) parent;
			parent = parent.getParentNode();
		}
		return null;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public abstract short getType();

	/**
	 * @return boolean
	 */
	public boolean hasProperties() {
		return true;
	}
}
