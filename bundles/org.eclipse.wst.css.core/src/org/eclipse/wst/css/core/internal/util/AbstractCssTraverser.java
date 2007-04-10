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
import java.util.Stack;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;



/**
 * 
 */
public abstract class AbstractCssTraverser {

	public static short TRAV_CONT = 0;
	public static short TRAV_PRUNE = 1;
	public static short TRAV_STOP = 2;
	protected java.util.Stack travStack;
	private boolean fTraverseImported = false;
	private boolean fTraverseImportFirst = false;
	private java.util.Vector traversedSheets;

	/**
	 * CssPropCMNode constructor comment.
	 */
	public AbstractCssTraverser() {
		super();
	}

	/**
	 * @param model
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSModel
	 */
	public final void apply(ICSSModel model) {
		apply(model.getDocument());
	}

	public final void apply(ICSSNode root) {
		travStack = new Stack();
		if (fTraverseImported) {
			traversedSheets = new Vector();
			if (root != null && root.getOwnerDocument() != null && root.getOwnerDocument().getNodeType() == ICSSNode.STYLESHEET_NODE) {
				traversedSheets.add(root.getOwnerDocument());
			}
		}

		// first call begin()
		begin(root);

		// traverse
		traverse(root);

		// last call end()
		end(root);
	}


	protected void begin(ICSSNode node) {
	}


	protected void end(ICSSNode node) {
	}

	/**
	 * @return boolean
	 */
	public final boolean isTraverseImported() {
		return fTraverseImported;
	}


	protected short postNode(ICSSNode node) {
		return (short) 0;
	}


	protected short preNode(ICSSNode node) {
		return (short) 0;
	}

	/**
	 * @param newTraverseImported
	 *            boolean
	 */
	public final void setTraverseImported(boolean newTraverseImported) {
		fTraverseImported = newTraverseImported;
	}

	/**
	 * @param newTraverseImportFirst
	 *            boolean
	 */
	public final void setTraverseImportFirst(boolean newTraverseImportFirst) {
		fTraverseImportFirst = newTraverseImportFirst;
	}


	private final short traverse(ICSSNode node) {
		if (node == null)
			return TRAV_CONT;

		travStack.push(node);

		// pre-action
		short ret = preNode(node);

		if (ret == TRAV_CONT) {
			if (fTraverseImported && (node.getNodeType() == ICSSNode.IMPORTRULE_NODE)) {
				ICSSImportRule rule = (ICSSImportRule) node;
				// traverse external style-sheet
				ICSSStyleSheet sheet = (ICSSStyleSheet) rule.getStyleSheet();
				if (sheet != null && !traversedSheets.contains(sheet)) { // prevent
																			// loop
					traversedSheets.add(sheet);
					short retExt = traverse(sheet);
					if (retExt == TRAV_STOP) {
						travStack.pop();
						return retExt;
					}
				}
			}

			// collect children
			ArrayList children = new ArrayList();
			ICSSNode child = node.getFirstChild();
			if (fTraverseImportFirst) {
				ArrayList others = new ArrayList();

				while (child != null) {
					if (child.getNodeType() == ICSSNode.IMPORTRULE_NODE)
						children.add(child);
					else
						others.add(child);
					child = child.getNextSibling();
				}
				children.addAll(others);
			}
			else {
				while (child != null) {
					children.add(child);
					child = child.getNextSibling();
				}
			}

			// traverse children
			for (int i = 0; i < children.size(); i++) {
				child = (ICSSNode) children.get(i);
				short retChild = traverse(child);
				if (retChild == TRAV_STOP) {
					travStack.pop();
					return retChild;
				}
			}
		}
		else if (ret == TRAV_STOP) {
			travStack.pop();
			return ret;
		}

		// post-action
		ret = postNode(node);

		travStack.pop();
		return (ret == TRAV_PRUNE) ? TRAV_CONT : ret;
	}
}
