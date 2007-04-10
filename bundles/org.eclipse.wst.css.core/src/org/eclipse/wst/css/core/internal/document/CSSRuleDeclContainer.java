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
import org.w3c.dom.css.CSSStyleDeclaration;


/**
 * 
 */
abstract class CSSRuleDeclContainer extends CSSRuleImpl {

	/**
	 * CSSRuleDeclContainer constructor comment.
	 */
	CSSRuleDeclContainer() {
		super();
	}

	/**
	 * CSSRuleDeclContainer constructor comment.
	 * 
	 */
	CSSRuleDeclContainer(CSSRuleDeclContainer that) {
		super(that);
	}

	/**
	 * @return java.lang.String
	 */
	abstract String extractPreString();

	/**
	 * @return org.w3c.dom.css.CSSStyleDeclaration
	 */
	public CSSStyleDeclaration getStyle() {

		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSStyleDeclaration)
				return (CSSStyleDeclaration) node;
		}

		return null;
	}
}
