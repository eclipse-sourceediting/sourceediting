/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NodeListImpl class
 */
public class NodeListImpl implements NodeList {

	Object lockObject = new byte[0];

	private List nodes = null;

	/**
	 * NodeListImpl constructor
	 */
	public NodeListImpl() {
		super();
	}

	/**
	 * appendNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param node
	 *            org.w3c.dom.Node
	 */
	protected Node appendNode(Node node) {
		if (node == null)
			return null;
		if (this.nodes == null)
			this.nodes = new ArrayList();
		this.nodes.add(node);
		return node;
	}

	/**
	 * getLength method
	 * 
	 * @return int
	 */
	public int getLength() {
		synchronized (lockObject) {
			if (this.nodes == null)
				return 0;
			return this.nodes.size();
		}
	}

	/**
	 */
	protected Node insertNode(Node node, int index) {
		if (node == null)
			return null;
		if (this.nodes == null || index >= this.nodes.size()) {
			return appendNode(node);
		}
		this.nodes.add(index, node);
		return node;
	}

	/**
	 * item method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node item(int index) {
		synchronized (lockObject) {
			if (this.nodes == null)
				return null;
			if (index < 0 || index >= this.nodes.size())
				return null;
			return (Node) this.nodes.get(index);
		}
	}

	/**
	 * removeNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param index
	 *            int
	 */
	protected Node removeNode(int index) {
		if (this.nodes == null)
			return null; // no node

		synchronized (lockObject) {
			if (index < 0 || index >= this.nodes.size())
				return null; // invalid parameter

			Node removed = (Node) this.nodes.remove(index);
			return removed;
		}
	}
}
