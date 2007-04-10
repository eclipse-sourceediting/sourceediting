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
package org.eclipse.wst.css.core.internal.metamodel.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;


public class CSSMMTypeCollector extends CSSMetaModelTraverser {

	/**
	 * Constructor for TypeCollector.
	 */
	public CSSMMTypeCollector() {
		super();
	}

	public void apply(CSSMMNode node, String type) {
		fType = type;
		fNodes = new ArrayList();
		apply(node);
	}

	public void collectNestedType(boolean nested) {
		fNested = nested;
	}

	protected short preNode(CSSMMNode node) {
		short rc = TRAV_CONT;
		if (node != null) {
			if (fType == node.getType()) {
				if (!fNodes.contains(node)) {
					fNodes.add(node);
				}
				if (!fNested) {
					rc = TRAV_PRUNE;
				}
			}
		}
		return rc;
	}

	public Iterator getNodes() {
		return fNodes.iterator();
	}


	private boolean fNested = false;
	private String fType = null;
	List fNodes = null;
}
