/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.NodeContainer
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONStructure;
import org.eclipse.wst.json.core.document.JSONException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * Base class for JSON structure (Object and Array).
 *
 */
public abstract class JSONStructureImpl extends JSONValueImpl implements
		IJSONStructure {

	private IStructuredDocumentRegion endStructuredDocumentRegion = null;

	/**
	 */
	private class ChildNodesCache {
		private IJSONNode curChild = null;
		private int curIndex = -1;
		private int length = 0;

		ChildNodesCache() {
			initializeCache();
		}

		public int getLength() {
			// atomic
			return this.length;
		}

		private void initializeCache() {
			// note we use the outter objects lockobject
			// (since we are using their "children".
			synchronized (lockObject) {
				for (IJSONNode child = firstChild; child != null; child = child
						.getNextSibling()) {
					this.length++;
				}
			}
		}

		public IJSONNode item(int index) {
			synchronized (lockObject) {
				if (this.length == 0)
					return null;
				if (index < 0)
					return null;
				if (index >= this.length)
					return null;

				if (this.curIndex < 0) { // first time
					if (index * 2 >= this.length) { // search from the last
						this.curIndex = this.length - 1;
						this.curChild = lastChild;
					} else { // search from the first
						this.curIndex = 0;
						this.curChild = firstChild;
					}
				}

				if (index == this.curIndex)
					return this.curChild;

				if (index > this.curIndex) {
					while (index > this.curIndex) {
						this.curIndex++;
						this.curChild = this.curChild.getNextSibling();
					}
				} else { // index < this.curIndex
					while (index < this.curIndex) {
						this.curIndex--;
						this.curChild = this.curChild.getPreviousSibling();
					}
				}

				return this.curChild;
			}
		}
	}

	private ChildNodesCache childNodesCache = null;

	private boolean fChildEditable = true;
	JSONNodeImpl firstChild = null;
	JSONNodeImpl lastChild = null;

	Object lockObject = new byte[0];

	/**
	 * NodeContainer constructor
	 */
	protected JSONStructureImpl() {
		super();
	}

	/**
	 * NodeContainer constructor
	 * 
	 * @param that
	 *            NodeContainer
	 */
	protected JSONStructureImpl(JSONStructureImpl that) {
		super(that);
	}

	/**
	 * appendChild method
	 * 
	 * @return org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 */
	public IJSONNode appendChild(IJSONNode newChild) throws JSONException {
		return insertBefore(newChild, null);
	}

	/**
	 * cloneChildNodes method
	 * 
	 * @param container
	 *            org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	protected void cloneChildNodes(IJSONNode newParent, boolean deep) {
		if (newParent == null || newParent == this)
			return;
		if (!(newParent instanceof JSONStructureImpl))
			return;

		JSONStructureImpl container = (JSONStructureImpl) newParent;
		container.removeChildNodes();

		for (IJSONNode child = getFirstChild(); child != null; child = child
				.getNextSibling()) {
			IJSONNode cloned = child.cloneNode(deep);
			if (cloned != null)
				container.appendChild(cloned);
		}
	}

	/**
	 * getChildNodes method
	 * 
	 * @return org.w3c.dom.NodeList
	 */
	// public NodeList getChildNodes() {
	// return this;
	// }

	/**
	 * getFirstChild method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public IJSONNode getFirstChild() {
		return this.firstChild;
	}

	/**
	 * getLastChild method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public IJSONNode getLastChild() {
		return this.lastChild;
	}

	/**
	 * getLength method
	 * 
	 * @return int
	 */
	public int getLength() {
		if (this.firstChild == null)
			return 0;
		synchronized (lockObject) {
			if (this.childNodesCache == null)
				this.childNodesCache = new ChildNodesCache();
			return this.childNodesCache.getLength();
		}
	}

	/**
	 */
	public String getSource() {
		StringBuffer buffer = new StringBuffer();

		IStructuredDocumentRegion startStructuredDocumentRegion = getStartStructuredDocumentRegion();
		if (startStructuredDocumentRegion != null) {
			String source = startStructuredDocumentRegion.getText();
			if (source != null)
				buffer.append(source);
		}

		for (JSONNodeImpl child = firstChild; child != null; child = (JSONNodeImpl) child
				.getNextSibling()) {
			String source = child.getSource();
			if (source != null)
				buffer.append(source);
		}

		IStructuredDocumentRegion endStructuredDocumentRegion = getEndStructuredDocumentRegion();
		if (endStructuredDocumentRegion != null) {
			String source = endStructuredDocumentRegion.getText();
			if (source != null)
				buffer.append(source);
		}

		return buffer.toString();
	}

	/**
	 * hasChildNodes method
	 * 
	 * @return boolean
	 */
	public boolean hasChildNodes() {
		return (this.firstChild != null);
	}

	/**
	 * insertBefore method
	 * 
	 * @return org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param refChild
	 *            org.w3c.dom.Node
	 */
	public IJSONNode insertBefore(IJSONNode newChild, IJSONNode refChild)
			throws JSONException {
		if (newChild == null)
			return null; // nothing to do
		if (refChild != null && refChild.getParentNode() != this) {
			// throw new JSONException(JSONException.NOT_FOUND_ERR,
			// JSONMessages.NOT_FOUND_ERR);
		}
		if (!isChildEditable()) {
			// throw new
			// JSONException(JSONException.NO_MODIFICATION_ALLOWED_ERR,
			// JSONMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		if (newChild == refChild)
			return newChild; // nothing to do
		// new child can not be a parent of this, would cause cycle
		if (isParent(newChild)) {
			// throw new JSONException(JSONException.HIERARCHY_REQUEST_ERR,
			// JSONMessages.HIERARCHY_REQUEST_ERR);
		}

		// if (newChild.getNodeType() == DOCUMENT_FRAGMENT_NODE) {
		// // insert child nodes instead
		// for (IJSONNode child = newChild.getFirstChild(); child != null; child
		// = newChild.getFirstChild()) {
		// newChild.removeChild(child);
		// insertBefore(child, refChild);
		// }
		// return newChild;
		// }
		// synchronized in case another thread is getting item, or length
		synchronized (lockObject) {
			this.childNodesCache = null; // invalidate child nodes cache
		}

		JSONNodeImpl child = (JSONNodeImpl) newChild;
		JSONNodeImpl next = (JSONNodeImpl) refChild;
		JSONNodeImpl prev = null;
		IJSONNode oldParent = child.getParentNode();
		if (oldParent != null)
			oldParent.removeChild(child);
		if (next == null) {
			prev = this.lastChild;
			this.lastChild = child;
		} else {
			prev = (JSONNodeImpl) next.getPreviousSibling();
			next.setPreviousSibling(child);
		}
		if (prev == null)
			this.firstChild = child;
		else
			prev.setNextSibling(child);
		child.setPreviousSibling(prev);
		child.setNextSibling(next);
		child.setParentNode(this);
		// make sure having the same owner document
		if (child.getOwnerDocument() == null) {
			if (getNodeType() == DOCUMENT_NODE) {
				child.setOwnerDocument((IJSONDocument) this);
			} else {
				child.setOwnerDocument(getOwnerDocument());
			}
		}

		notifyChildReplaced(child, null);

		return child;
	}

	public boolean isChildEditable() {
		if (!fChildEditable) {
			JSONModelImpl model = (JSONModelImpl) getModel();
			if (model != null && model.isReparsing()) {
				return true;
			}
		}
		return fChildEditable;
	}

	/**
	 * isContainer method
	 * 
	 * @return boolean
	 */
	public boolean isContainer() {
		return true;
	}

	/**
	 * item method
	 * 
	 * @return org.w3c.dom.Node
	 * @param index
	 *            int
	 */
	public IJSONNode item(int index) {
		if (this.firstChild == null)
			return null;
		synchronized (lockObject) {
			if (this.childNodesCache == null)
				this.childNodesCache = new ChildNodesCache();
			return this.childNodesCache.item(index);
		}
	}

	/**
	 * notifyChildReplaced method
	 * 
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	protected void notifyChildReplaced(IJSONNode newChild, IJSONNode oldChild) {
		JSONDocumentImpl document = (JSONDocumentImpl) getContainerDocument();
		if (document == null)
			return;

		// syncChildEditableState(newChild);

		JSONModelImpl model = (JSONModelImpl) document.getModel();
		if (model == null)
			return;
		model.childReplaced(this, newChild, oldChild);
	}

	/**
	 * removeChild method
	 * 
	 * @return org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	public IJSONNode removeChild(IJSONNode oldChild) throws JSONException {
		if (oldChild == null)
			return null;
		if (oldChild.getParentNode() != null && oldChild.getParentNode() != this) {
			// throw new JSONException(JSONException.NOT_FOUND_ERR,
			// JSONMessages.NOT_FOUND_ERR);
			throw new JSONException();
		}

		if (!isChildEditable()) {
			// throw new
			// JSONException(JSONException.NO_MODIFICATION_ALLOWED_ERR,
			// JSONMessages.NO_MODIFICATION_ALLOWED_ERR);
			throw new JSONException();
		}

		// synchronized in case another thread is getting item, or length
		synchronized (lockObject) {
			this.childNodesCache = null; // invalidate child nodes cache
		}

		JSONNodeImpl child = (JSONNodeImpl) oldChild;
		if (oldChild.getParentNode() == this) {
			JSONNodeImpl prev = (JSONNodeImpl) child.getPreviousSibling();
			JSONNodeImpl next = (JSONNodeImpl) child.getNextSibling();
	
			// child.setEditable(true, true); // clear ReadOnly flags
	
			if (prev == null)
				this.firstChild = next;
			else
				prev.setNextSibling(next);
			if (next == null)
				this.lastChild = prev;
			else
				next.setPreviousSibling(prev);
		}
		
		child.setPreviousSibling(null);
		child.setNextSibling(null);
		child.setParentNode(null);

		notifyChildReplaced(null, child);

		return child;
	}

	/**
	 * removeChildNodes method
	 */
	public void removeChildNodes() {
		if (!isChildEditable()) {
			// throw new
			// JSONException(JSONException.NO_MODIFICATION_ALLOWED_ERR,
			// JSONMessages.NO_MODIFICATION_ALLOWED_ERR);
			throw new JSONException();
		}

		IJSONNode nextChild = null;
		for (IJSONNode child = getFirstChild(); child != null; child = nextChild) {
			nextChild = child.getNextSibling();
			removeChild(child);
		}
	}

	/**
	 * replaceChild method
	 * 
	 * @return org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	public IJSONNode replaceChild(IJSONNode newChild, IJSONNode oldChild)
			throws JSONException {
		if (!isChildEditable()) {
			// throw new
			// JSONException(JSONException.NO_MODIFICATION_ALLOWED_ERR,
			// JSONMessages.NO_MODIFICATION_ALLOWED_ERR);
			throw new JSONException();
		}

		if (oldChild == null || oldChild == newChild)
			return newChild;
		if (newChild != null)
			insertBefore(newChild, oldChild);
		return removeChild(oldChild);
	}

	// public void setChildEditable(boolean editable) {
	// if (fChildEditable == editable) {
	// return;
	// }
	//
	// ReadOnlyController roc = ReadOnlyController.getInstance();
	// IJSONNode node;
	// if (editable) {
	// for (node = getFirstChild(); node != null; node = node.getNextSibling())
	// {
	// roc.unlockNode(node);
	// }
	// } else {
	// for (node = getFirstChild(); node != null; node = node.getNextSibling())
	// {
	// roc.lockNode( node);
	// }
	// }
	//
	// fChildEditable = editable;
	// notifyEditableChanged();
	// }
	//
	// protected void syncChildEditableState(IJSONNode child) {
	// ReadOnlyController roc = ReadOnlyController.getInstance();
	// if (fChildEditable) {
	// roc.unlockNode((JSONNodeImpl) child);
	// } else {
	// roc.lockNode((JSONNodeImpl) child);
	// }
	// }

	/**
	 * <p>
	 * Checks to see if the given <code>Node</code> is a parent of
	 * <code>this</code> node
	 * </p>
	 * 
	 * @param possibleParent
	 *            the possible parent <code>Node</code> of <code>this</code>
	 *            node
	 * @return <code>true</code> if <code>possibleParent</code> is the parent of
	 *         <code>this</code>, <code>false</code> otherwise.
	 */
	private boolean isParent(IJSONNode possibleParent) {
		IJSONNode parent = this.getParentNode();
		while (parent != null && parent != possibleParent) {
			parent = parent.getParentNode();
		}

		return parent == possibleParent;
	}

	void setStartStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		setStructuredDocumentRegion(flatNode);
	}

	void setEndStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		this.endStructuredDocumentRegion = flatNode;
	}

	@Override
	public int getEndOffset() {
		if (this.endStructuredDocumentRegion != null)
			return this.endStructuredDocumentRegion.getEnd();
		return super.getEndOffset();
	}

	public IStructuredDocumentRegion getEndStructuredDocumentRegion() {
		return this.endStructuredDocumentRegion;
	}

	public int getStartEndOffset() {
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode != null)
			return flatNode.getEnd();
		return super.getStartOffset();
	}

	@Override
	public boolean isClosed() {
		return getEndStructuredDocumentRegion() != null;
	}
}
