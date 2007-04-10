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



import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;


/**
 * 
 */
public class ImportRuleCollector extends org.eclipse.wst.css.core.internal.util.AbstractCssTraverser {

	protected java.util.Vector rules = new Vector();
	protected ICSSStyleSheet target = null;

	/**
	 * 
	 */
	public ImportRuleCollector() {
		super();
		setTraverseImported(false);
	}

	/**
	 * 
	 */
	public ImportRuleCollector(ICSSStyleSheet sheet) {
		super();
		target = sheet;
	}

	/**
	 * 
	 */
	public java.util.Vector getRules() {
		return rules;
	}

	/**
	 * 
	 */
	protected short preNode(ICSSNode node) {
		if (node.getNodeType() == ICSSNode.MEDIARULE_NODE || node.getNodeType() == ICSSNode.STYLESHEET_NODE) {
			return TRAV_CONT;
		}
		if (node.getNodeType() == ICSSNode.IMPORTRULE_NODE) {
			if (target != null) {
				ICSSImportRule imp = (ICSSImportRule) node;
				if (imp.getStyleSheet() != target)
					return TRAV_PRUNE;
			}
			if (!rules.contains(node))
				rules.add(node);
		}
		return TRAV_PRUNE;
	}
}
