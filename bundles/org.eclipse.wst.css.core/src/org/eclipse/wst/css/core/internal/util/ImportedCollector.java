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



import java.util.List;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;


/**
 * 
 */
public class ImportedCollector extends org.eclipse.wst.css.core.internal.util.AbstractCssTraverser {

	protected java.util.Vector externals = new Vector();

	/**
	 * 
	 */
	public ImportedCollector() {
		super();
		setTraverseImported(true);
	}

	/**
	 * 
	 */
	public List getExternals() {
		return externals;
	}

	/**
	 * 
	 */
	protected short preNode(ICSSNode node) {
		if (node.getNodeType() == ICSSNode.MEDIARULE_NODE) {
			return TRAV_CONT;
		}
		if (node.getNodeType() == ICSSNode.STYLESHEET_NODE) {
			if (!externals.contains(node)) {
				externals.add(node);
			}
			return TRAV_CONT;
		}
		if (node.getNodeType() == ICSSNode.IMPORTRULE_NODE) {
			return TRAV_CONT;
		}
		return TRAV_PRUNE;
	}
}
