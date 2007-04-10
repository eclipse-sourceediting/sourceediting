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



import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;


/**
 * 
 */
public class SelectorsCollector extends AbstractCssTraverser {

	protected Vector selectors = new Vector();
	protected Vector selectorsToAvoid = null;

	/**
	 * 
	 */
	public SelectorsCollector() {
		super();
		setTraverseImported(false);
	}

	/**
	 * 
	 */
	public void addSelectorToAvoid(ICSSSelector selToAvoid) {
		if (selToAvoid == null)
			return;
		if (selectorsToAvoid == null)
			selectorsToAvoid = new Vector();

		if (!selectorsToAvoid.contains(selToAvoid))
			selectorsToAvoid.add(selToAvoid);
	}

	/**
	 * 
	 */
	public void addSelectorToAvoid(ICSSStyleRule rule) {
		ICSSSelectorList list = rule.getSelectors();
		Iterator it = list.getIterator();
		while (it.hasNext()) {
			ICSSSelector sel = (ICSSSelector) it.next();
			addSelectorToAvoid(sel);
		}
	}

	/**
	 * 
	 */
	public java.util.List getSelectors() {
		return selectors;
	}

	/**
	 * 
	 */
	protected short preNode(ICSSNode node) {
		if (node.getNodeType() == ICSSNode.STYLERULE_NODE) {
			ICSSStyleRule rule = (ICSSStyleRule) node;
			ICSSSelectorList list = rule.getSelectors();
			Iterator it = list.getIterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (selectorsToAvoid != null && selectorsToAvoid.contains(obj))
					continue;
				if (!selectors.contains(obj))
					selectors.add(obj);
			}
			return TRAV_PRUNE;
		}
		else if (node.getNodeType() == ICSSNode.STYLESHEET_NODE) {
			return TRAV_CONT;
		}
		return TRAV_PRUNE;
	}

	/**
	 * 
	 */
	public void setSelectorsToAvoid(java.util.Vector newSelectorsToAvoid) {
		selectorsToAvoid = newSelectorsToAvoid;
	}
}
