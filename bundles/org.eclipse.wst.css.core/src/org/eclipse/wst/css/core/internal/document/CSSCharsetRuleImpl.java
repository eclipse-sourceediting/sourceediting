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



import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSCharsetRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSUtil;


/**
 * 
 */
class CSSCharsetRuleImpl extends CSSRuleImpl implements ICSSCharsetRule {

	/**
	 * 
	 */
	CSSCharsetRuleImpl() {
		super();
	}

	CSSCharsetRuleImpl(CSSCharsetRuleImpl that) {
		super(that);
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSCharsetRuleImpl cloned = new CSSCharsetRuleImpl(this);

		return cloned;
	}

	/**
	 * The encoding information used in this <code>@charset</code> rule.
	 * @exception org.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the specified encoding value has a
	 *                syntax error and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this encoding
	 *                rule is readonly.
	 */
	public String getEncoding() {
		return CSSUtil.extractStringContents(getAttribute(ENCODING));
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return CHARSETRULE_NODE;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return CHARSET_RULE;
	}

	public void setEncoding(String encoding) throws org.w3c.dom.DOMException {
		String quote = CSSCorePlugin.getDefault().getPluginPreferences().getString(CSSCorePreferenceNames.FORMAT_QUOTE);
		String enc = CSSUtil.extractStringContents(encoding);
		quote = CSSUtil.detectQuote(enc, quote);
		setAttribute(ENCODING, quote + enc + quote);
	}
}
