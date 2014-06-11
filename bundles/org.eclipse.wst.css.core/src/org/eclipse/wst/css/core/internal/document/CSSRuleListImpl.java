/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;

/**
 * 
 */
public class CSSRuleListImpl extends AbstractCSSNodeList implements CSSRuleList {

	/**
	 * 
	 */
	CSSRuleListImpl() {
		super();
	}

	/**
	 * Used to retrieve a CSS rule by ordinal index. The order in this
	 * collection represents the order of the rules in the CSS style sheet. If
	 * index is greater than or equal to the number of rules in the list, this
	 * returns <code>null</code>.
	 * 
	 * @param indexIndex
	 *            into the collection
	 * @return The style rule at the <code>index</code> position in the
	 *         <code>CSSRuleList</code>, or <code>null</code> if that is
	 *         not a valid index.
	 */
	public CSSRule item(int index) {
		return (CSSRule) itemImpl(index);
	}
}