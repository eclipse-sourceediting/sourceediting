/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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



import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An array-backed NodeList, used to keep the array size down
 */
public class NodeListImpl implements NodeList {

	private Object fLockObject = new byte[0];
	private final static int growthConstant = 2;
	private Node[] fNodes = null;
	private int fNodeCount = 0;

	/**
	 * NodeListImpl constructor
	 */
	public NodeListImpl() {
		super();
	}

	protected NodeListImpl(NodeListImpl list) {
		super();
		fNodeCount = list.fNodeCount;
		fNodes = list.fNodes;
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
		synchronized (fLockObject) {
			ensureCapacity(fNodeCount + 1);
			fNodes[fNodeCount++] = node;
		}
		return node;
	}

	/**
	 * Grow the node array to at least the given size while keeping the
	 * contents the same
	 * 
	 * @param needed
	 */
	private void ensureCapacity(int needed) {
		if (fNodes == null) {
			// first time
			fNodes = new Node[needed];
			return;
		}
		int oldLength = fNodes.length;
		if (oldLength < needed) {
			Node[] oldNodes = fNodes;
			Node[] newNodes = new Node[needed + growthConstant];
			System.arraycopy(oldNodes, 0, newNodes, 0, fNodeCount);
			fNodes = newNodes;
		}
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int getLength() {
		return fNodeCount;
	}
	
	protected final Node[] getNodes() {
		return fNodes;
	}

	/**
	 */
	protected Node insertNode(Node node, int index) {
		if (node == null)
			return null;
		synchronized (fLockObject) {
			if (fNodes == null) {
				// first time, ignore index
				fNodes = new Node[1];
				fNodes[fNodeCount++] = node;
				return node;
			}

			// gracefully handle out of bounds
			if (index < 0 || index > fNodeCount + 1)
				return appendNode(node);

			/*
			 * Grow a new Node array, copying the old contents around the new
			 * Node
			 */
			Node[] newNodes = new Node[fNodeCount + growthConstant];
			System.arraycopy(fNodes, 0, newNodes, 0, index);
			newNodes[index] = node;
			System.arraycopy(fNodes, index, newNodes, index + 1, fNodeCount - index);
			fNodes = newNodes;
			fNodeCount++;
		}
		return node;
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NodeList#item(int)
	 */
	public Node item(int index) {
		if (index < 0 || index >= fNodeCount)
			return null; // invalid parameter
		return fNodes[index];
	}

	/**
	 * removeNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param index
	 *            int
	 */
	protected Node removeNode(int index) {
		if (this.fNodes == null)
			return null; // no node

		synchronized (fLockObject) {
			if (index < 0 || index >= fNodeCount)
				return null; // invalid parameter

			Node removed = fNodes[index];
			Node[] newNodes = new Node[fNodeCount - 1];
			// copy around node being removed
			System.arraycopy(fNodes, 0, newNodes, 0, index);
			System.arraycopy(fNodes, index + 1, newNodes, index, fNodeCount - index - 1);
			fNodes = newNodes;
			fNodeCount--;
			return removed;
		}
	}
}
