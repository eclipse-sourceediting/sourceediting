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



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;


/**
 * 
 */
public class CSSClassTraverser extends AbstractCssTraverser {

	ArrayList fClassNames;

	/**
	 * 
	 */
	public CSSClassTraverser() {
		super();
	}

	/**
	 * 
	 */
	private void addClassNames(ICSSStyleRule rule) {
		ICSSSelectorList selectorList = rule.getSelectors();
		Iterator iSelector = selectorList.getIterator();
		while (iSelector.hasNext()) {
			ICSSSelector selector = (ICSSSelector) iSelector.next();
			Iterator iItem = selector.getIterator();
			while (iItem.hasNext()) {
				ICSSSelectorItem item = (ICSSSelectorItem) iItem.next();
				if (item.getItemType() == ICSSSelectorItem.SIMPLE) {
					ICSSSimpleSelector sel = (ICSSSimpleSelector) item;
					int nClasses = sel.getNumOfClasses();
					for (int iClass = 0; iClass < nClasses; iClass++) {
						String className = sel.getClass(iClass);
						if (!fClassNames.contains(className))
							fClassNames.add(className);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void begin(ICSSNode node) {
		if (fClassNames == null)
			fClassNames = new ArrayList();
	}

	/**
	 * 
	 */
	protected void end(ICSSNode node) {
	}

	/**
	 * 
	 */
	public Collection getClassNames() {
		return (fClassNames != null) ? fClassNames : Collections.EMPTY_LIST;
	}

	/**
	 * 
	 */
	protected short postNode(ICSSNode node) {
		return TRAV_CONT;
	}

	/**
	 * 
	 */
	protected short preNode(ICSSNode node) {
		short ret;
		if (node instanceof ICSSStyleRule) {
			addClassNames((ICSSStyleRule) node);
			ret = TRAV_PRUNE;
		}
		else if (node instanceof ICSSStyleSheet || node instanceof ICSSMediaRule || node instanceof ICSSImportRule) {
			ret = TRAV_CONT;
		}
		else {
			ret = TRAV_PRUNE;
		}
		return ret;
	}
}
