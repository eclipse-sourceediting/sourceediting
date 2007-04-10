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
package org.eclipse.wst.css.core.internal.document;

import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;


/**
 * 
 */
abstract class AbstractCSSNodeList {

	protected Vector nodes = null;

	ICSSNode appendNode(ICSSNode node) {
		if (node == null)
			return null;
		if (this.nodes == null)
			this.nodes = new Vector();
		this.nodes.addElement(node);
		return node;
	}

	/**
	 * @return int
	 */
	public int getLength() {
		if (this.nodes == null)
			return 0;
		return this.nodes.size();
	}


	protected ICSSNode itemImpl(int index) {
		if (this.nodes == null)
			return null;
		if (index < 0 || index >= this.nodes.size())
			return null;
		return (ICSSNode) this.nodes.elementAt(index);
	}


	ICSSNode removeNode(int index) {
		if (this.nodes == null)
			return null; // no node
		if (index < 0 || index >= this.nodes.size())
			return null; // invalid parameter

		ICSSNode removed = (ICSSNode) this.nodes.elementAt(index);
		this.nodes.removeElementAt(index);
		return removed;
	}
}
