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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
class CSSMediaRuleImpl extends CSSRuleImpl implements ICSSMediaRule {

	/**
	 * 
	 */
	CSSMediaRuleImpl() {
		super();
	}

	CSSMediaRuleImpl(CSSMediaRuleImpl that) {
		super(that);
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule appendRule(org.w3c.dom.css.CSSRule rule) throws org.w3c.dom.DOMException {
		if (rule == null)
			return null;

		CSSRule ret = (CSSRule) appendChild((CSSNodeImpl) rule);
		return ret;
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSMediaRuleImpl cloned = new CSSMediaRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * Used to delete a rule from the media block.
	 * 
	 * @param index
	 *            The index within the media block's rule collection of the
	 *            rule to remove.
	 * @exception DOMException
	 *                INDEX_SIZE_ERR: Raised if the specified index does not
	 *                correspond to a rule in the media rule list. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this media rule
	 *                is readonly.
	 */
	public void deleteRule(int index) throws DOMException {
		CSSNodeImpl node = getIndexedRule(index);
		if (node != null)
			removeChild(node);
	}

	/**
	 * A list of all CSS rules contained within the media block.
	 */
	public CSSRuleList getCssRules() {
		CSSRuleListImpl list = new CSSRuleListImpl();

		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSRule)
				list.appendNode(node);
		}

		return list;
	}

	CSSRuleImpl getIndexedRule(int index) {
		if (index < 0)
			return null;

		int i = 0;
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSRule) {
				if (i++ == index)
					return (CSSRuleImpl) node;
			}
		}
		return null;
	}

	/**
	 * A list of media types for this rule.
	 */
	public MediaList getMedia() {
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof MediaListImpl)
				return (MediaList) node;
		}

		return null;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return MEDIARULE_NODE;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return MEDIA_RULE;
	}

	/**
	 * Used to insert a new rule into the media block.
	 * 
	 * @param rule
	 *            The parsable text representing the rule. For rule sets this
	 *            contains both the selector and the style declaration. For
	 *            at-rules, this specifies both the at-identifier and the rule
	 *            content.
	 * @param index
	 *            The index within the media block's rule collection of the
	 *            rule before which to insert the specified rule. If the
	 *            specified index is equal to the length of the media blocks's
	 *            rule collection, the rule will be added to the end of the
	 *            media block.
	 * @return The index within the media block's rule collection of the newly
	 *         inserted rule.
	 * @exception DOMException
	 *                HIERARCHY_REQUEST_ERR: Raised if the rule cannot be
	 *                inserted at the specified index, e.g., if an
	 *                <code>@import</code> rule is inserted after a standard rule set or other
	 *         at-rule. <br>
	 *         INDEX_SIZE_ERR: Raised if the specified index is not a valid
	 *         insertion point. <br>
	 *         NO_MODIFICATION_ALLOWED_ERR: Raised if this media rule is
	 *         readonly. <br>
	 *         SYNTAX_ERR: Raised if the specified rule has a syntax error and
	 *         is unparsable.
	 */
	public int insertRule(String rule, int index) throws DOMException {
		int length = getCssRules().getLength();
		if (index < 0 || length < index)
			throw new DOMException(DOMException.INDEX_SIZE_ERR, "");//$NON-NLS-1$

		CSSRuleImpl refRule = (length != index) ? getIndexedRule(index) : null;

		CSSRuleImpl newRule = (CSSRuleImpl) getOwnerDocument().createCSSRule(rule);

		// prevent from nesting @media rule
		if (newRule.getType() == CSSRule.MEDIA_RULE)
			throw new DOMException(DOMException.SYNTAX_ERR, "");//$NON-NLS-1$

		insertBefore(newRule, refRule);

		return index;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newRule
	 *            org.w3c.dom.css.CSSRule
	 * @param refRule
	 *            org.w3c.dom.css.CSSRule
	 */
	public org.w3c.dom.css.CSSRule insertRuleBefore(org.w3c.dom.css.CSSRule newRule, org.w3c.dom.css.CSSRule refRule) throws org.w3c.dom.DOMException {
		if (newRule == null && refRule == null)
			return null;

		CSSRule ret = (CSSRule) insertBefore((CSSNodeImpl) newRule, (CSSNodeImpl) refRule);
		return ret;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule removeRule(org.w3c.dom.css.CSSRule rule) throws org.w3c.dom.DOMException {
		if (rule == null)
			return null;

		CSSRule ret = (CSSRule) removeChild((CSSNodeImpl) rule);
		return ret;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newChild
	 *            org.w3c.dom.css.CSSRule
	 * @param oldChild
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule replaceRule(org.w3c.dom.css.CSSRule newRule, org.w3c.dom.css.CSSRule oldRule) throws org.w3c.dom.DOMException {
		if (newRule == null && oldRule == null)
			return null;

		CSSRule ret = (CSSRule) replaceChild((CSSNodeImpl) newRule, (CSSNodeImpl) oldRule);
		return ret;
	}
}
