/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

/**
 * Analog of dom.NodeList for CM.
 * So, the implementation is very similar to
 * NodeListImpl<br>
 */
class CMNodeListImpl implements CMNodeList {

	private java.util.Vector nodes = null;

	/**
	 * CMNodeListImpl constructor comment.
	 */
	public CMNodeListImpl() {
		super();
		nodes = new java.util.Vector();
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 * @param node org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	protected CMNode appendNode(CMNode node) {
		nodes.addElement(node);
		return node;
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		return nodes.size();
	}

	/**
	 * item method
	 * @return CMNode
	 * @param index int
	 */
	public CMNode item(int index) {
		if (index < 0 || index >= nodes.size())
			return null;
		return (CMNode) nodes.elementAt(index);
	}
}
