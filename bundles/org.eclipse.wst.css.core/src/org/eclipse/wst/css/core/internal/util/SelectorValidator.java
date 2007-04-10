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
package org.eclipse.wst.css.core.internal.util;



import org.eclipse.wst.css.core.internal.document.CSSSelectorListImpl;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;


public class SelectorValidator {

	/**
	 * Constructor for SelectorValidator.
	 */
	public SelectorValidator(String text) {
		super();
		fText = text;
	}

	/**
	 * Returns true if the text consists of one CLASS selector. syntax check
	 * is a little loose (.123 is passed)
	 * 
	 * ".class" -> true ".123" -> true ".class , .class2" -> false
	 * ".class.class2" -> false ".123{}" -> false
	 */
	public boolean isClass() {
		ICSSSimpleSelector selector = getOnlyOneSimpleSelector();
		if (selector != null) {
			return (selector.getName().length() == 0 && selector.getNumOfAttributes() == 0 && selector.getNumOfClasses() == 1 && selector.getNumOfIDs() == 0 && selector.getNumOfPseudoNames() == 0);
		}
		return false;
	}

	/**
	 * Returns true if the text consists of one ID selector.
	 * 
	 * "#ID" -> true "H1#myID" -> false "#abc{}" -> false
	 */
	public boolean isID() {
		ICSSSimpleSelector selector = getOnlyOneSimpleSelector();
		if (selector != null) {
			return (selector.getName().length() == 0 && selector.getNumOfAttributes() == 0 && selector.getNumOfClasses() == 0 && selector.getNumOfIDs() == 1 && selector.getNumOfPseudoNames() == 0);
		}
		return false;
	}

	/**
	 * overall check
	 * 
	 * "P#hoge98 + *:hover > A:link, A.external:visited" -> true "H1 H2 {}" ->
	 * false
	 */
	public boolean isValid() {
		parse();
		return (fSelectorList != null && fSelectorList.getErrorCount() == 0);
	}

	private ICSSSimpleSelector getOnlyOneSimpleSelector() {
		parse();
		if (fSelectorList != null && fSelectorList.getLength() == 1) {
			ICSSSelector selector = fSelectorList.getSelector(0);
			int nItem = selector.getLength();
			if (nItem == 1) {
				ICSSSelectorItem item = selector.getItem(0);
				if (item instanceof ICSSSimpleSelector) {
					return (ICSSSimpleSelector) item;
				}
			}
		}
		return null;
	}

	private void parse() {
		if (fSelectorList == null) {
			if (fText != null) {
				fSelectorList = new CSSSelectorListImpl(fText);
			}
		}
	}


	private String fText = null;
	private ICSSSelectorList fSelectorList = null;
}
