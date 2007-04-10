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
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.util.CSSMetaModelTraverser;



class ValueCollector extends CSSMetaModelTraverser {
	public void begin(CSSMMNode node) {
		fNodes = new ArrayList();
	}

	protected short preNode(CSSMMNode node) {
		short rc = TRAV_CONT;
		if (node != null) {
			String type = node.getType();
			if (type == CSSMMNode.TYPE_PROPERTY || type == CSSMMNode.TYPE_DESCRIPTOR || type == CSSMMNode.TYPE_CONTAINER) {
				rc = TRAV_CONT;
			}
			else {
				if (!fNodes.contains(node)) {
					fNodes.add(node);
					rc = TRAV_PRUNE;
				}
			}
		}
		return rc;
	}

	public Iterator getNodes() {
		return fNodes.iterator();
	}


	private List fNodes = null;
}
