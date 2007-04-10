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

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;



abstract public class CSSMetaModelTraverser {

	/**
	 * Constructor for Traverser.
	 */
	public CSSMetaModelTraverser() {
		super();
	}

	public final void apply(CSSMMNode node) {
		fTravStack = new Stack();

		begin(node);
		traverse(node);
		end(node);
	}

	private final short traverse(CSSMMNode node) {
		if (node == null) {
			return TRAV_CONT;
		}

		fTravStack.push(node);

		short rc;
		rc = preNode(node);

		if (rc == TRAV_CONT) {
			Iterator i = node.getChildNodes();
			while (i.hasNext()) {
				CSSMMNode child = (CSSMMNode) i.next();
				short rcChild = traverse(child);
				if (rcChild == TRAV_STOP) {
					fTravStack.pop();
					return TRAV_STOP;
				}
			}
		}
		else if (rc == TRAV_STOP) {
			fTravStack.pop();
			return TRAV_STOP;
		}

		rc = postNode(node);

		fTravStack.pop();
		return (rc == TRAV_PRUNE) ? TRAV_CONT : rc;
	}

	protected void begin(CSSMMNode node) {

	}

	protected void end(CSSMMNode node) {

	}

	protected short preNode(CSSMMNode node) {
		return TRAV_CONT;
	}

	protected short postNode(CSSMMNode node) {
		return TRAV_CONT;
	}


	Stack fTravStack = null;

	protected static final short TRAV_CONT = 0;
	protected static final short TRAV_STOP = 1;
	protected static final short TRAV_PRUNE = 2;

}
