/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NodeListImpl class
 */
public class NodeListImpl implements NodeList {

	private Vector nodes = null;

	/**
	 * NodeListImpl constructor
	 */
	public NodeListImpl() {
		super();
	}

	/**
	 * appendNode method
	 * @return org.w3c.dom.Node
	 * @param node org.w3c.dom.Node
	 */
	protected Node appendNode(Node node) {
		if (node == null)
			return null;
		if (this.nodes == null)
			this.nodes = new Vector();
		this.nodes.addElement(node);
		return node;
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		if (this.nodes == null)
			return 0;
		return this.nodes.size();
	}

	/**
	 */
	protected Node insertNode(Node node, int index) {
		if (node == null)
			return null;
		if (this.nodes == null || index >= this.nodes.size()) {
			return appendNode(node);
		}
		this.nodes.insertElementAt(node, index);
		return node;
	}

	/**
	 * item method
	 * @return org.w3c.dom.Node
	 */
	public Node item(int index) {
		if (this.nodes == null)
			return null;
		if (index < 0 || index >= this.nodes.size())
			return null;
		return (Node) this.nodes.elementAt(index);
	}

	/**
	 * removeNode method
	 * @return org.w3c.dom.Node
	 * @param index int
	 */
	protected Node removeNode(int index) {
		if (this.nodes == null)
			return null; // no node
		if (index < 0 || index >= this.nodes.size())
			return null; // invalid parameter

		Node removed = (Node) this.nodes.elementAt(index);
		this.nodes.removeElementAt(index);
		return removed;
	}
}
