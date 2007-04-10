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
package org.eclipse.wst.html.core.internal.htmlcss;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.util.AbstractCssTraverser;
import org.eclipse.wst.css.core.internal.util.CSSStyleDeclarationFactory;
import org.w3c.dom.Element;
import org.w3c.dom.css.ElementCSSInlineStyle;

/**
 */
public class CSSQueryTraverser extends AbstractCssTraverser {

	private Element element;
	private String pseudoName;
	private CSSQueryContext context = null;
	ICSSStyleDeclaration decl = null;

	/**
	 */
	public ICSSStyleDeclaration getDeclaration() {
		try {
			ICSSStyleDeclaration inlineStyle = (ICSSStyleDeclaration) ((ElementCSSInlineStyle) element).getStyle();
			if (inlineStyle != null) {
				if (context == null) {
					context = new CSSQueryContext();
				}
				context.overrideWithExpand(inlineStyle, 10000);
				// style attribute's specificity is 100 (in CSS2 spec.) and
				// our implement use 100 as base number (see CSSSelector.java)
			}
		}
		catch (ClassCastException ex) {
			// element is not ElementCSSInlineStyle ???
		}
		if (context == null)
			return null;

		if (decl == null)
			decl = CSSStyleDeclarationFactory.getInstance().createStyleDeclaration();
		context.applyFull(decl);
		return decl;
	}

	/**
	 */
	private void overwriteDeclaration(ICSSStyleDeclaration d, int specificity) {
		if (d == null)
			return;
		if (context == null)
			context = new CSSQueryContext();
		context.overrideWithExpand(d, specificity);
	}

	/**
	 */
	protected short postNode(ICSSNode node) {
		return TRAV_CONT;
	}

	/**
	 */
	protected short preNode(ICSSNode node) {
		if (node instanceof ICSSStyleRule) {
			// style rule
			ICSSStyleRule style = (ICSSStyleRule) node;
			ICSSSelectorList list = style.getSelectors();
			int nSelectors = list.getLength();
			int maxSpecificity = -1;
			for (int iSelector = 0; iSelector < nSelectors; iSelector++) {
				// Check each Selector Lists
				ICSSSelector selector = list.getSelector(iSelector);
				int specificity = selector.getSpecificity();
				if (maxSpecificity < specificity && selector.match(element, pseudoName)) {
					maxSpecificity = specificity;
				}
			}
			if (maxSpecificity >= 0) {
				// apply this style to the element
				overwriteDeclaration((ICSSStyleDeclaration) style.getStyle(), maxSpecificity);
			}
			return TRAV_PRUNE;
		}
		return TRAV_CONT;
	}

	/**
	 */
	private void resetContext() {
		context = null;
	}

	/**
	 */
	public void setElement(Element element, String pseudoName) {
		this.element = element;
		this.pseudoName = pseudoName;
		resetContext();
	}
}
