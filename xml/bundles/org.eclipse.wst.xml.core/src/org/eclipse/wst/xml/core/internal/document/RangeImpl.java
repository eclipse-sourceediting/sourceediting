/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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



import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.ranges.RangeException;


/**
 */
public class RangeImpl implements Range {
	private Node endContainer = null;
	private int endOffset = 0;

	private Node startContainer = null;
	private int startOffset = 0;

	/**
	 */
	protected RangeImpl() {
		super();
	}

	/**
	 */
	protected RangeImpl(RangeImpl that) {
		super();

		if (that != null) {
			this.startContainer = that.startContainer;
			this.startOffset = that.startOffset;
			this.endContainer = that.endContainer;
			this.endOffset = that.endOffset;
		}
	}

	/**
	 * Duplicates the contents of a Range
	 * 
	 * @return A DocumentFragment that contains content equivalent to this
	 *         Range.
	 * @exception DOMException
	 *                HIERARCHY_REQUEST_ERR: Raised if a DocumentType node
	 *                would be extracted into the new DocumentFragment. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public DocumentFragment cloneContents() throws DOMException {
		// not supported
		return null;
	}

	/**
	 * Produces a new Range whose boundary-points are equal to the
	 * boundary-points of the Range.
	 * 
	 * @return The duplicated Range.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public Range cloneRange() throws DOMException {
		return new RangeImpl(this);
	}

	/**
	 * Collapse a Range onto one of its boundary-points
	 * 
	 * @param toStartIf
	 *            TRUE, collapses the Range onto its start; if FALSE,
	 *            collapses it onto its end.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void collapse(boolean toStart) throws DOMException {
		if (toStart) {
			this.endContainer = this.startContainer;
			this.endOffset = this.startOffset;
		} else {
			this.startContainer = this.endContainer;
			this.startOffset = this.endOffset;
		}
	}

	/**
	 * Compare the boundary-points of two Ranges in a document.
	 * 
	 * @param howA
	 *            code representing the type of comparison, as defined above.
	 * @param sourceRangeThe
	 *            <code>Range</code> on which this current
	 *            <code>Range</code> is compared to.
	 * @return -1, 0 or 1 depending on whether the corresponding
	 *         boundary-point of the Range is respectively before, equal to,
	 *         or after the corresponding boundary-point of
	 *         <code>sourceRange</code>.
	 * @exception DOMException
	 *                WRONG_DOCUMENT_ERR: Raised if the two Ranges are not in
	 *                the same Document or DocumentFragment. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public short compareBoundaryPoints(short how, Range sourceRange) throws DOMException {
		if (sourceRange == null)
			return (short) 0; // error

		Node container1 = null;
		int offset1 = 0;
		Node container2 = null;
		int offset2 = 0;

		switch (how) {
			case START_TO_START :
				container1 = this.startContainer;
				offset1 = this.startOffset;
				container2 = sourceRange.getStartContainer();
				offset2 = sourceRange.getStartOffset();
				break;
			case START_TO_END :
				container1 = this.startContainer;
				offset1 = this.startOffset;
				container2 = sourceRange.getEndContainer();
				offset2 = sourceRange.getEndOffset();
				break;
			case END_TO_END :
				container1 = this.endContainer;
				offset1 = this.endOffset;
				container2 = sourceRange.getEndContainer();
				offset2 = sourceRange.getEndOffset();
				break;
			case END_TO_START :
				container1 = this.endContainer;
				offset1 = this.endOffset;
				container2 = sourceRange.getStartContainer();
				offset2 = sourceRange.getStartOffset();
				break;
			default :
				return (short) 0; // error
		}

		return comparePoints(container1, offset1, container2, offset2);
	}

	/*
	 */
	protected short comparePoints(Node container1, int offset1, Node container2, int offset2) {
		if (container1 == null || container2 == null)
			return (short) 0; // error

		if (container1 == container2) {
			if (offset1 > offset2)
				return (short) 1;
			if (offset1 < offset2)
				return (short) -1;
			return 0;
		}

		// get node offsets
		IDOMNode node1 = null;
		if (container1.hasChildNodes()) {
			Node child = container1.getFirstChild();
			for (int i = 0; i < offset1; i++) {
				Node next = child.getNextSibling();
				if (next == null)
					break;
				child = next;
			}
			node1 = (IDOMNode) child;
			offset1 = 0;
		} else {
			node1 = (IDOMNode) container1;
		}
		int nodeOffset1 = node1.getStartOffset();
		IDOMNode node2 = null;
		if (container2.hasChildNodes()) {
			Node child = container2.getFirstChild();
			for (int i = 0; i < offset2; i++) {
				Node next = child.getNextSibling();
				if (next == null)
					break;
				child = next;
			}
			node2 = (IDOMNode) child;
			offset2 = 0;
		} else {
			node2 = (IDOMNode) container2;
		}
		int nodeOffset2 = node2.getStartOffset();

		if (nodeOffset1 > nodeOffset2)
			return (short) 1;
		if (nodeOffset1 < nodeOffset2)
			return (short) -1;
		if (offset1 > offset2)
			return (short) 1;
		if (offset1 < offset2)
			return (short) -1;
		return (short) 0;
	}

	/**
	 * Removes the contents of a Range from the containing document or
	 * document fragment without returning a reference to the removed content.
	 * 
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if any portion of
	 *                the content of the Range is read-only or any of the
	 *                nodes that contain any of the content of the Range are
	 *                read-only. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void deleteContents() throws DOMException {
		// not supported
	}

	/**
	 * Called to indicate that the Range is no longer in use and that the
	 * implementation may relinquish any resources associated with this Range.
	 * Subsequent calls to any methods or attribute getters on this Range will
	 * result in a <code>DOMException</code> being thrown with an error code
	 * of <code>INVALID_STATE_ERR</code>.
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void detach() throws DOMException {
		this.startContainer = null;
		this.startOffset = 0;
		this.endContainer = null;
		this.endOffset = 0;
	}

	/**
	 * Moves the contents of a Range from the containing document or document
	 * fragment to a new DocumentFragment.
	 * 
	 * @return A DocumentFragment containing the extracted contents.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if any portion of
	 *                the content of the Range is read-only or any of the
	 *                nodes which contain any of the content of the Range are
	 *                read-only. <br>
	 *                HIERARCHY_REQUEST_ERR: Raised if a DocumentType node
	 *                would be extracted into the new DocumentFragment. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public DocumentFragment extractContents() throws DOMException {
		// not supported
		return null;
	}

	/**
	 * TRUE if the Range is collapsed
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public boolean getCollapsed() throws DOMException {
		if (this.startContainer == this.endContainer && this.startOffset == this.endOffset)
			return true;
		return false;
	}

	/**
	 * The deepest common ancestor container of the Range's two
	 * boundary-points.
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public Node getCommonAncestorContainer() throws DOMException {
		if (this.startContainer == null)
			return null;
		return ((NodeImpl) this.startContainer).getCommonAncestor(this.endContainer);
	}

	/**
	 * Node within which the Range ends
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public Node getEndContainer() throws DOMException {
		return this.endContainer;
	}

	/**
	 * Offset within the ending node of the Range.
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public int getEndOffset() throws DOMException {
		return this.endOffset;
	}

	/**
	 * Node within which the Range begins
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public Node getStartContainer() throws DOMException {
		return this.startContainer;
	}

	/**
	 * Offset within the starting node of the Range.
	 * 
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public int getStartOffset() throws DOMException {
		return this.startOffset;
	}

	/**
	 * Inserts a node into the Document or DocumentFragment at the start of
	 * the Range. If the container is a Text node, this will be split at the
	 * start of the Range (as if the Text node's splitText method was
	 * performed at the insertion point) and the insertion will occur between
	 * the two resulting Text nodes. Adjacent Text nodes will not be
	 * automatically merged. If the node to be inserted is a DocumentFragment
	 * node, the children will be inserted rather than the DocumentFragment
	 * node itself.
	 * 
	 * @param newNodeThe
	 *            node to insert at the start of the Range
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if an ancestor
	 *                container of the start of the Range is read-only. <br>
	 *                WRONG_DOCUMENT_ERR: Raised if <code>newNode</code> and
	 *                the container of the start of the Range were not created
	 *                from the same document. <br>
	 *                HIERARCHY_REQUEST_ERR: Raised if the container of the
	 *                start of the Range is of a type that does not allow
	 *                children of the type of <code>newNode</code> or if
	 *                <code>newNode</code> is an ancestor of the container.
	 *                <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if <code>newNode</code>
	 *                is an Attr, Entity, Notation, or Document node.
	 */
	public void insertNode(Node newNode) throws RangeException, DOMException {
		// not supported
	}

	/**
	 * Select a node and its contents
	 * 
	 * @param refNodeThe
	 *            node to select.
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if an ancestor of
	 *                <code>refNode</code> is an Entity, Notation or
	 *                DocumentType node or if <code>refNode</code> is a
	 *                Document, DocumentFragment, Attr, Entity, or Notation
	 *                node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void selectNode(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		Node parent = refNode.getParentNode();
		if (parent == null)
			return;
		int index = ((NodeImpl) refNode).getIndex();
		if (index < 0)
			return;
		setStart(parent, index);
		setEnd(parent, index + 1);
	}

	/**
	 * Select the contents within a node
	 * 
	 * @param refNodeNode
	 *            to select from
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code>
	 *                or an ancestor of <code>refNode</code> is an Entity,
	 *                Notation or DocumentType node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void selectNodeContents(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		if (refNode.getNodeType() == Node.TEXT_NODE) {
			Text text = (Text) refNode;
			setStart(refNode, 0);
			setEnd(refNode, text.getLength());
		} else {
			NodeList childNodes = refNode.getChildNodes();
			int length = (childNodes != null ? childNodes.getLength() : 0);
			setStart(refNode, 0);
			setEnd(refNode, length);
		}
	}

	/**
	 * Sets the attributes describing the end of a Range.
	 * 
	 * @param refNodeThe
	 *            <code>refNode</code> value. This parameter must be
	 *            different from <code>null</code>.
	 * @param offsetThe
	 *            <code>endOffset</code> value.
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code>
	 *                or an ancestor of <code>refNode</code> is an Entity,
	 *                Notation, or DocumentType node.
	 * @exception DOMException
	 *                INDEX_SIZE_ERR: Raised if <code>offset</code> is
	 *                negative or greater than the number of child units in
	 *                <code>refNode</code>. Child units are 16-bit units if
	 *                <code>refNode</code> is a type of CharacterData node
	 *                (e.g., a Text or Comment node) or a
	 *                ProcessingInstruction node. Child units are Nodes in all
	 *                other cases. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setEnd(Node refNode, int offset) throws RangeException, DOMException {
		this.endContainer = refNode;
		this.endOffset = offset;
	}

	/**
	 * Sets the end of a Range to be after a node
	 * 
	 * @param refNodeRange
	 *            ends after <code>refNode</code>.
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if the root container of
	 *                <code>refNode</code> is not an Attr, Document or
	 *                DocumentFragment node or if <code>refNode</code> is a
	 *                Document, DocumentFragment, Attr, Entity, or Notation
	 *                node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setEndAfter(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		Node parent = refNode.getParentNode();
		if (parent == null)
			return;
		int index = ((NodeImpl) refNode).getIndex();
		if (index < 0)
			return;
		setEnd(parent, index + 1);
	}

	/**
	 * Sets the end position to be before a node.
	 * 
	 * @param refNodeRange
	 *            ends before <code>refNode</code>
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if the root container of
	 *                <code>refNode</code> is not an Attr, Document, or
	 *                DocumentFragment node or if <code>refNode</code> is a
	 *                Document, DocumentFragment, Attr, Entity, or Notation
	 *                node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setEndBefore(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		Node parent = refNode.getParentNode();
		if (parent == null)
			return;
		int index = ((NodeImpl) refNode).getIndex();
		if (index < 0)
			return;
		setEnd(parent, index);
	}

	/**
	 * Sets the attributes describing the start of the Range.
	 * 
	 * @param refNodeThe
	 *            <code>refNode</code> value. This parameter must be
	 *            different from <code>null</code>.
	 * @param offsetThe
	 *            <code>startOffset</code> value.
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code>
	 *                or an ancestor of <code>refNode</code> is an Entity,
	 *                Notation, or DocumentType node.
	 * @exception DOMException
	 *                INDEX_SIZE_ERR: Raised if <code>offset</code> is
	 *                negative or greater than the number of child units in
	 *                <code>refNode</code>. Child units are 16-bit units if
	 *                <code>refNode</code> is a type of CharacterData node
	 *                (e.g., a Text or Comment node) or a
	 *                ProcessingInstruction node. Child units are Nodes in all
	 *                other cases. <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setStart(Node refNode, int offset) throws RangeException, DOMException {
		this.startContainer = refNode;
		this.startOffset = offset;
	}

	/**
	 * Sets the start position to be after a node
	 * 
	 * @param refNodeRange
	 *            starts after <code>refNode</code>
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if the root container of
	 *                <code>refNode</code> is not an Attr, Document, or
	 *                DocumentFragment node or if <code>refNode</code> is a
	 *                Document, DocumentFragment, Attr, Entity, or Notation
	 *                node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setStartAfter(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		Node parent = refNode.getParentNode();
		if (parent == null)
			return;
		int index = ((NodeImpl) refNode).getIndex();
		if (index < 0)
			return;
		setStart(parent, index + 1);
	}

	/**
	 * Sets the start position to be before a node
	 * 
	 * @param refNodeRange
	 *            starts before <code>refNode</code>
	 * @exception RangeException
	 *                INVALID_NODE_TYPE_ERR: Raised if the root container of
	 *                <code>refNode</code> is not an Attr, Document, or
	 *                DocumentFragment node or if <code>refNode</code> is a
	 *                Document, DocumentFragment, Attr, Entity, or Notation
	 *                node.
	 * @exception DOMException
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 */
	public void setStartBefore(Node refNode) throws RangeException, DOMException {
		if (refNode == null)
			return;
		Node parent = refNode.getParentNode();
		if (parent == null)
			return;
		int index = ((NodeImpl) refNode).getIndex();
		if (index < 0)
			return;
		setStart(parent, index);
	}

	/**
	 * Reparents the contents of the Range to the given node and inserts the
	 * node at the position of the start of the Range.
	 * 
	 * @param newParentThe
	 *            node to surround the contents with.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if an ancestor
	 *                container of either boundary-point of the Range is
	 *                read-only. <br>
	 *                WRONG_DOCUMENT_ERR: Raised if <code> newParent</code>
	 *                and the container of the start of the Range were not
	 *                created from the same document. <br>
	 *                HIERARCHY_REQUEST_ERR: Raised if the container of the
	 *                start of the Range is of a type that does not allow
	 *                children of the type of <code>newParent</code> or if
	 *                <code>newParent</code> is an ancestor of the container
	 *                or if <code>node</code> would end up with a child node
	 *                of a type not allowed by the type of <code>node</code>.
	 *                <br>
	 *                INVALID_STATE_ERR: Raised if <code>detach()</code> has
	 *                already been invoked on this object.
	 * @exception RangeException
	 *                BAD_BOUNDARYPOINTS_ERR: Raised if the Range partially
	 *                selects a non-text node. <br>
	 *                INVALID_NODE_TYPE_ERR: Raised if <code> node</code> is
	 *                an Attr, Entity, DocumentType, Notation, Document, or
	 *                DocumentFragment node.
	 */
	public void surroundContents(Node newParent) throws RangeException, DOMException {
		// not supported
	}
}
