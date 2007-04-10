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
import org.w3c.dom.css.CSSFontFaceRule;


/**
 * 
 */
class CSSFontFaceRuleImpl extends CSSRuleDeclContainer implements CSSFontFaceRule {

	/**
	 * 
	 */
	CSSFontFaceRuleImpl() {
		super();
	}

	CSSFontFaceRuleImpl(CSSFontFaceRuleImpl that) {
		super(that);
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSFontFaceRuleImpl cloned = new CSSFontFaceRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * @return java.lang.String
	 */
	String extractPreString() {
		return "@font-face";//$NON-NLS-1$
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return FONTFACERULE_NODE;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return FONT_FACE_RULE;
	}
}
