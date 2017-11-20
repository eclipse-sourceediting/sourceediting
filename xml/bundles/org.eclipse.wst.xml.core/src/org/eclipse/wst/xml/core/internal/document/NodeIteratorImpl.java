/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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



import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * NodeIteratorImpl class
 */
public class NodeIteratorImpl implements NodeIterator {
	private NodeFilter filter = null;
	private Node nextNode = null;

	private Node rootNode = null;
	private int whatToShow = NodeFilter.SHOW_ALL;

	/**
	 * NodeIteratorImpl constructor
	 * 
	 * @param rootNode
	 *            org.w3c.dom.Node
	 */
	NodeIteratorImpl(Node rootNode, int whatToShow, NodeFilter filter) {
		this.rootNode = rootNode;
		this.nextNode = rootNode;
		this.whatToShow = whatToShow;
		this.filter = filter;
	}

	/**
	 */
	private final boolean acceptNode(Node node) {
		if (this.whatToShow != NodeFilter.SHOW_ALL) {
			if (node == null)
				return false;
			short nodeType = node.getNodeType();
			switch (this.whatToShow) {
				case NodeFilter.SHOW_ELEMENT :
					if (nodeType != Node.ELEMENT_NODE)
						return false;
					break;
				case NodeFilter.SHOW_ATTRIBUTE :
					if (nodeType != Node.ATTRIBUTE_NODE)
						return false;
					break;
				case NodeFilter.SHOW_TEXT :
					if (nodeType != Node.TEXT_NODE)
						return false;
					break;
				case NodeFilter.SHOW_CDATA_SECTION :
					if (nodeType != Node.CDATA_SECTION_NODE)
						return false;
					break;
				case NodeFilter.SHOW_ENTITY_REFERENCE :
					if (nodeType != Node.ENTITY_REFERENCE_NODE)
						return false;
					break;
				case NodeFilter.SHOW_ENTITY :
					if (nodeType != Node.ENTITY_NODE)
						return false;
					break;
				case NodeFilter.SHOW_PROCESSING_INSTRUCTION :
					if (nodeType != Node.PROCESSING_INSTRUCTION_NODE)
						return false;
					break;
				case NodeFilter.SHOW_COMMENT :
					if (nodeType != Node.COMMENT_NODE)
						return false;
					break;
				case NodeFilter.SHOW_DOCUMENT :
					if (nodeType != Node.DOCUMENT_NODE)
						return false;
					break;
				case NodeFilter.SHOW_DOCUMENT_TYPE :
					if (nodeType != Node.DOCUMENT_TYPE_NODE)
						return false;
					break;
				case NodeFilter.SHOW_DOCUMENT_FRAGMENT :
					if (nodeType != Node.DOCUMENT_FRAGMENT_NODE)
						return false;
					break;
				case NodeFilter.SHOW_NOTATION :
					if (nodeType != Node.NOTATION_NODE)
						return false;
					break;
				default :
					return false;
			}
		}
		if (this.filter != null) {
			return (this.filter.acceptNode(node) == NodeFilter.FILTER_ACCEPT);
		}
		return true;
	}

	/**
	 * Detaches the <code>NodeIterator</code> from the set which it iterated
	 * over, releasing any computational resources and placing the iterator in
	 * the INVALID state. After <code>detach</code> has been invoked, calls
	 * to <code>nextNode</code> or <code>previousNode</code> will raise
	 * the exception INVALID_STATE_ERR.
	 */
	public void detach() {
		this.rootNode = null;
		this.nextNode = null;
		this.filter = null;
	}

	/**
	 * The value of this flag determines whether the children of entity
	 * reference nodes are visible to the iterator. If false, they and their
	 * descendants will be rejected. Note that this rejection takes precedence
	 * over <code>whatToShow</code> and the filter. Also note that this is
	 * currently the only situation where <code>NodeIterators</code> may
	 * reject a complete subtree rather than skipping individual nodes. <br>
	 * <br>
	 * To produce a view of the document that has entity references expanded
	 * and does not expose the entity reference node itself, use the
	 * <code>whatToShow</code> flags to hide the entity reference node and
	 * set <code>expandEntityReferences</code> to true when creating the
	 * iterator. To produce a view of the document that has entity reference
	 * nodes but no entity expansion, use the <code>whatToShow</code> flags
	 * to show the entity reference node and set
	 * <code>expandEntityReferences</code> to false.
	 */
	public boolean getExpandEntityReferences() {
		// not supported
		return false;
	}

	/**
	 * The <code>NodeFilter</code> used to screen nodes.
	 */
	public NodeFilter getFilter() {
		return this.filter;
	}

	/**
	 */
	private final Node getNextNode() {
		if (this.nextNode == null)
			return null;
		Node oldNext = this.nextNode;
		Node child = this.nextNode.getFirstChild();
		if (child != null) {
			this.nextNode = child;
			return oldNext;
		}
		for (Node node = this.nextNode; node != null && node != this.rootNode; node = node.getParentNode()) {
			Node next = node.getNextSibling();
			if (next != null) {
				this.nextNode = next;
				return oldNext;
			}
		}
		this.nextNode = null;
		return oldNext;
	}

	/**
	 */
	private final Node getPreviousNode() {
		if (this.nextNode == this.rootNode)
			return null;
		Node prev = null;
		if (this.nextNode == null) {
			prev = this.rootNode; // never null
		} else {
			prev = this.nextNode.getPreviousSibling();
			if (prev == null) {
				this.nextNode = this.nextNode.getParentNode();
				return this.nextNode;
			}
		}
		Node last = prev.getLastChild();
		while (last != null) {
			prev = last;
			last = prev.getLastChild();
		}
		this.nextNode = prev;
		return this.nextNode;
	}

	/**
	 * The root node of the <code>NodeIterator</code>, as specified when it
	 * was created.
	 */
	public Node getRoot() {
		return this.rootNode;
	}

	/**
	 * This attribute determines which node types are presented via the
	 * iterator. The available set of constants is defined in the
	 * <code>NodeFilter</code> interface. Nodes not accepted by
	 * <code>whatToShow</code> will be skipped, but their children may still
	 * be considered. Note that this skip takes precedence over the filter, if
	 * any.
	 */
	public int getWhatToShow() {
		return this.whatToShow;
	}

	/**
	 * Returns the next node in the set and advances the position of the
	 * iterator in the set. After a <code>NodeIterator</code> is created,
	 * the first call to <code>nextNode()</code> returns the first node in
	 * the set.
	 * 
	 * @return The next <code>Node</code> in the set being iterated over, or
	 *         <code>null</code> if there are no more members in that set.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if this method is called after
	 *                the <code>detach</code> method was invoked.
	 */
	public Node nextNode() throws DOMException {
		for (Node node = getNextNode(); node != null; node = getNextNode()) {
			if (acceptNode(node))
				return node;
		}
		return null;
	}

	/**
	 * Returns the previous node in the set and moves the position of the
	 * <code>NodeIterator</code> backwards in the set.
	 * 
	 * @return The previous <code>Node</code> in the set being iterated
	 *         over, or <code>null</code> if there are no more members in
	 *         that set.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if this method is called after
	 *                the <code>detach</code> method was invoked.
	 */
	public Node previousNode() throws DOMException {
		for (Node node = getPreviousNode(); node != null; node = getPreviousNode()) {
			if (acceptNode(node))
				return node;
		}
		return null;
	}
}
