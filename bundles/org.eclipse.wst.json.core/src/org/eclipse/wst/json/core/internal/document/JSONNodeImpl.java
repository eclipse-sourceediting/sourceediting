/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     
 *     David Carver (STAR) - bug 295127 - implement isSameNode and compareDocumentPosition methods.
 *                                        Unit Tests covered in wst.xsl XPath 2.0 tests.
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.NodeImpl
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.json.schema.IJSONPath;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.JSONException;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.AbstractNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

/**
 * NodeImpl class
 */
public abstract class JSONNodeImpl extends AbstractNotifier implements
		IJSONNode, IAdaptable {
	// define one empty nodelist, for repeated use
	// private final static NodeList EMPTY_NODE_LIST = new NodeListImpl();
	// DocumentPosition
	// private final static short DOCUMENT_POSITION_DISCONNECTED = 0x01;
	private final static short DOCUMENT_POSITION_PRECEDING = 0x02;
	private final static short DOCUMENT_POSITION_FOLLOWING = 0x04;
	// private final static short DOCUMENT_POSITION_CONTAINS = 0x08;
	// private final static short DOCUMENT_POSITION_CONTAINED_BY = 0x10;
	private final static short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 0x20;

	private boolean fDataEditable = true;
	private IStructuredDocumentRegion flatNode = null;
	private JSONNodeImpl nextSibling = null;

	private JSONDocumentImpl ownerDocument = null;
	private JSONNodeImpl parentNode = null;
	private JSONNodeImpl previousSibling = null;
	// define one empty String constant for repeated use
	static final String EMPTY_STRING = "";

	/**
	 * NodeImpl constructor
	 */
	protected JSONNodeImpl() {
		super();
	}

	/**
	 * NodeImpl constructor
	 * 
	 * @param that
	 *            NodeImpl
	 */
	protected JSONNodeImpl(JSONNodeImpl that) {
		if (that != null) {
			this.ownerDocument = that.ownerDocument;
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		final IStructuredModel model = getModel();
		return model != null ? Platform.getAdapterManager().getAdapter(model,
				adapter) : null;
	}

	/**
	 * contains method
	 * 
	 * @return boolean
	 * @param offset
	 *            int
	 */
	public boolean contains(int offset) {
		return (offset >= getStartOffset() && offset < getEndOffset());
	}

	/**
	 * @param s
	 * @param tagName
	 * @return
	 */
	protected String createJSONExceptionMessage(short s, String tagName) {
		String result = null;
		// TODO: Should localize these messages, and provide /u escaped
		// version of tagName
		result = lookupMessage(s) + " " + tagName; //$NON-NLS-1$
		return result;
	}

	/**
	 * getAttributes method
	 * 
	 * @return org.w3c.dom.NamedNodeMap
	 */
	public NamedNodeMap getAttributes() {
		return null;
	}

	/**
	 */
	protected String getCharValue(String name) {
		JSONDocumentImpl document = (JSONDocumentImpl) getOwnerDocument();
		if (document == null)
			return null;
		return document.getCharValue(name);
	}

	/**
	 * getChildNodes method
	 * 
	 * @return org.w3c.dom.NodeList
	 */
	// public NodeList getChildNodes() {
	// // As per JSON spec, correct behavior for getChildNodes is to return a
	// // zero length NodeList, not null, when there are no children.
	// // We'll use a common instance of an empty node list, just to prevent
	// // creating a trival object many many times.
	//
	// return EMPTY_NODE_LIST;
	// }

	/**
	 * getContainerDocument method
	 * 
	 * @return org.w3c.dom.Document
	 */
	public IJSONDocument getContainerDocument() {
		IJSONNode parent = null;
		for (IJSONNode node = this; node != null; node = parent) {
			if (node.getNodeType() == IJSONNode.DOCUMENT_NODE) {
				return (IJSONDocument) node;
			}
			/* Break out of a bad hierarchy */
			if ((parent = node.getParentNode()) == node)
				break;
		}
		return null;
	}

	/**
	 * getEndOffset method
	 * 
	 * @return int
	 */
	public int getEndOffset() {
		IJSONNode node = this;
		while (node != null) {
			if (node.getNodeType() == IJSONNode.OBJECT_NODE) {
				JSONObjectImpl element = (JSONObjectImpl) node;
				IStructuredDocumentRegion endStructuredDocumentRegion = element
						.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null)
					return endStructuredDocumentRegion.getEnd();
			}

			IJSONNode last = node.getLastChild();
			if (last != null) { // dig into the last
				node = last;
				continue;
			}

			IStructuredDocumentRegion lastStructuredDocumentRegion = ((JSONNodeImpl) node)
					.getStructuredDocumentRegion();
			if (lastStructuredDocumentRegion != null)
				return lastStructuredDocumentRegion.getEnd();

			IJSONNode prev = node.getPreviousSibling();
			if (prev != null) { // move to the previous
				node = prev;
				continue;
			}

			IJSONNode parent = node.getParentNode();
			node = null;
			while (parent != null) {
				if (parent.getNodeType() == IJSONNode.OBJECT_NODE) {
					JSONObjectImpl element = (JSONObjectImpl) parent;
					IStructuredDocumentRegion startStructuredDocumentRegion = element
							.getStartStructuredDocumentRegion();
					if (startStructuredDocumentRegion != null)
						return startStructuredDocumentRegion.getEnd();
				}
				IJSONNode parentPrev = parent.getPreviousSibling();
				if (parentPrev != null) { // move to the previous
					node = parentPrev;
					break;
				}
				parent = parent.getParentNode();
			}
		}
		return 0;
	}

	public IStructuredDocumentRegion getEndStructuredDocumentRegion() {
		return null;
	}

	/**
	 */
	public FactoryRegistry getFactoryRegistry() {
		IJSONModel model = getModel();
		if (model != null) {
			FactoryRegistry reg = model.getFactoryRegistry();
			if (reg != null)
				return reg;
		}
		return null;
	}

	@Override
	public IJSONNode getFirstChild() {
		return null;
	}

	/**
	 * getFirstStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil
				.getStructuredDocumentRegion(this.flatNode);
	}

	/**
	 */
	public int getIndex() {
		IJSONNode parent = getParentNode();
		if (parent == null)
			return -1; // error
		int index = 0;
		for (IJSONNode child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child == this)
				return index;
			index++;
		}
		return -1; // error
	}

	public IJSONNode getLastChild() {
		return null;
	}

	/**
	 * getLastStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil
				.getStructuredDocumentRegion(this.flatNode);
	}

	/**
	 */
	public String getLocalName() {
		return null;
	}

	/**
	 * the default implementation can just refer to the owning document
	 */
	public IJSONModel getModel() {
		if (this.ownerDocument == null)
			return null;
		return this.ownerDocument.getModel();
	}

	/**
	 * all but attr return null
	 */
	public ITextRegion getNameRegion() {
		return null;
	}

	/**
	 */
	public String getNamespaceURI() {
		return null;
	}

	@Override
	public IJSONNode getNextSibling() {
		return this.nextSibling;
	}

	/**
	 * getNodeAt method
	 * 
	 * @return org.w3c.dom.Node
	 * @param offset
	 *            int
	 */
	IJSONNode getNodeAt(int offset) {
		IJSONNode parent = this;
		IJSONNode child = (IJSONNode) getFirstChild();
		while (child != null) {
			if (child.getEndOffset() == offset) {
				return child;
			}
			if (child.getEndOffset() <= offset) {
				child = (IJSONNode) child.getNextSibling();
				continue;
			}
			if (child.getStartOffset() > offset) {
				break;
			}

			IStructuredDocumentRegion startStructuredDocumentRegion = child
					.getStartStructuredDocumentRegion();
			if (startStructuredDocumentRegion != null) {
				if (startStructuredDocumentRegion.getEnd() > offset)
					return child;
			}

			// dig more
			parent = child;
			child = (IJSONNode) parent.getFirstChild();
		}

		return parent;
	}

	@Override
	public IJSONDocument getOwnerDocument() {
		return this.ownerDocument;
	}

	@Override
	public IJSONNode getParentNode() {
		return this.parentNode;
	}

	/**
	 */
	public String getPrefix() {
		return null;
	}

	@Override
	public IJSONNode getPreviousSibling() {
		return this.previousSibling;
	}

	/**
	 */
	public String getSource() {
		if (this.flatNode == null)
			return JSONNodeImpl.EMPTY_STRING;
		return this.flatNode.getText();
	}

	/**
	 * getStartOffset method
	 * 
	 * @return int
	 */
	public int getStartOffset() {
		if (this.flatNode != null)
			return this.flatNode.getStart();
		JSONNodeImpl prev = (JSONNodeImpl) getPreviousSibling();
		if (prev != null)
			return prev.getEndOffset();
		IJSONNode parent = getParentNode();
		if (parent != null && parent.getNodeType() == IJSONNode.OBJECT_NODE) {
			JSONObjectImpl element = (JSONObjectImpl) parent;
			if (element.hasStartTag())
				return element.getStartEndOffset();
			return element.getStartOffset();
		}
		// final fallback to look into first child
		JSONNodeImpl child = (JSONNodeImpl) getFirstChild();
		while (child != null) {
			IStructuredDocumentRegion childStructuredDocumentRegion = child
					.getStructuredDocumentRegion();
			if (childStructuredDocumentRegion != null)
				return childStructuredDocumentRegion.getStart();
			child = (JSONNodeImpl) child.getFirstChild();
		}
		return 0;
	}

	public IStructuredDocumentRegion getStartStructuredDocumentRegion() {
		return getFirstStructuredDocumentRegion();
	}

	/**
	 * Every node (indirectly) knows its structuredDocument
	 */
	public IStructuredDocument getStructuredDocument() {
		return getModel().getStructuredDocument();
	}

	/**
	 */
	IStructuredDocumentRegion getStructuredDocumentRegion() {
		return this.flatNode;
	}

	/**
	 * all but attr return null
	 */
	public ITextRegion getValueRegion() {
		return null;
	}

	/**
	 * @throws JSONException
	 */
	public String getValueSource() throws JSONException {
		return getNodeValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#hasAttributes()
	 */
	public boolean hasAttributes() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#hasChildNodes()
	 */
	public boolean hasChildNodes() {
		return false;
	}

	/**
	 * hasProperties method
	 * 
	 * @return boolean
	 */
	public boolean hasProperties() {
		return false;
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
		// throw new JSONException(JSONException.HIERARCHY_REQUEST_ERR,
		// JSONMessages.HIERARCHY_REQUEST_ERR);
		throw new JSONException();
	}

	public boolean isChildEditable() {
		return false;
	}

	/**
	 * 
	 */
	public boolean isClosed() {
		return true;
	}

	/**
	 * isContainer method
	 * 
	 * @return boolean
	 */
	public boolean isContainer() {
		return false;
	}

	public boolean isDataEditable() {
		if (!fDataEditable) {
			JSONModelImpl model = (JSONModelImpl) getModel();
			if (model != null && model.isReparsing()) {
				return true;
			}
		}
		return fDataEditable;
	}

	/**
	 * @param s
	 * @return
	 */
	private String lookupMessage(short s) {
		// TODO: make localized version
		String result = null;
		switch (s) {
		// case JSONException.JSONSTRING_SIZE_ERR:
		// result = JSONMessages.JSONSTRING_SIZE_ERR;
		// break;
		// case JSONException.HIERARCHY_REQUEST_ERR:
		// result = JSONMessages.HIERARCHY_REQUEST_ERR;
		// break;
		// case JSONException.INDEX_SIZE_ERR:
		// result = JSONMessages.INDEX_SIZE_ERR;
		// break;
		// case JSONException.INUSE_ATTRIBUTE_ERR:
		// result = JSONMessages.INUSE_ATTRIBUTE_ERR;
		// break;
		// case JSONException.INVALID_ACCESS_ERR:
		// result = JSONMessages.INVALID_ACCESS_ERR;
		// break;
		// case JSONException.INVALID_CHARACTER_ERR:
		// result = JSONMessages.INVALID_CHARACTER_ERR;
		// break;
		// case JSONException.INVALID_MODIFICATION_ERR:
		// result = JSONMessages.INVALID_MODIFICATION_ERR;
		// break;
		// case JSONException.INVALID_STATE_ERR:
		// result = JSONMessages.INVALID_STATE_ERR;
		// break;
		// case JSONException.NAMESPACE_ERR:
		// result = JSONMessages.NAMESPACE_ERR;
		// break;
		// case JSONException.NO_DATA_ALLOWED_ERR:
		// result = JSONMessages.NO_DATA_ALLOWED_ERR;
		// break;
		// case JSONException.NO_MODIFICATION_ALLOWED_ERR:
		// result = JSONMessages.NO_MODIFICATION_ALLOWED_ERR;
		// break;
		// case JSONException.NOT_FOUND_ERR:
		// result = JSONMessages.NOT_FOUND_ERR;
		// break;
		// case JSONException.NOT_SUPPORTED_ERR:
		// result = JSONMessages.NOT_SUPPORTED_ERR;
		// break;
		// case JSONException.SYNTAX_ERR:
		// result = JSONMessages.SYNTAX_ERR;
		// break;
		// case 17:// JSONException.TYPE_MISMATCH_ERR :
		// result = JSONMessages.TYPE_MISMATCH_ERR;
		// break;
		// case 16:// JSONException.VALIDATION_ERR :
		// result = JSONMessages.VALIDATION_ERR;
		// break;
		// case JSONException.WRONG_DOCUMENT_ERR:
		// result = JSONMessages.WRONG_DOCUMENT_ERR;
		// break;
		default:
			result = JSONNodeImpl.EMPTY_STRING;
			break;
		}
		return result;
	}

	// protected void notifyEditableChanged() {
	// JSONDocumentImpl document = (JSONDocumentImpl) getContainerDocument();
	// if (document == null)
	// return;
	// JSONModelImpl model = (JSONModelImpl) document.getModel();
	// if (model == null)
	// return;
	// model.editableChanged(this);
	// }

	/**
	 * notifyValueChanged method
	 */
	protected void notifyValueChanged() {
		JSONDocumentImpl document = (JSONDocumentImpl) getContainerDocument();
		if (document == null)
			return;

		// syncDataEditableState();

		JSONModelImpl model = (JSONModelImpl) document.getModel();
		if (model == null)
			return;
		model.valueChanged(this);
	}

	/**
	 * removeChild method
	 * 
	 * @return org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	public IJSONNode removeChild(IJSONNode oldChild) throws JSONException {
		throw new JSONException();
		// throw new JSONException(JSONException.NOT_FOUND_ERR,
		// JSONMessages.NOT_FOUND_ERR);
	}

	/**
	 * removeChildNodes method
	 */
	public void removeChildNodes() {
	}

	/**
	 * removeChildNodes method
	 * 
	 * @return org.w3c.dom.DocumentFragment
	 * @param firstChild
	 *            org.w3c.dom.Node
	 * @param lastChild
	 *            org.w3c.dom.Node
	 */
	// public DocumentFragment removeChildNodes(Node firstChild, Node lastChild)
	// {
	// return null;
	// }

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
		// throw new JSONException(JSONException.HIERARCHY_REQUEST_ERR,
		// JSONMessages.HIERARCHY_REQUEST_ERR);
		return null;
	}

	/**
	 * Resets children values from IStructuredDocumentRegion.
	 */
	void resetStructuredDocumentRegions() {
		for (JSONNodeImpl child = (JSONNodeImpl) getFirstChild(); child != null; child = (JSONNodeImpl) child
				.getNextSibling()) {
			child.resetStructuredDocumentRegions();
		}
		this.flatNode = null;
	}

	public void setChildEditable(boolean editable) {
		// nop
	}

	// public void setDataEditable(boolean editable) {
	// if (fDataEditable == editable) {
	// return;
	// }
	//
	// ReadOnlyController roc = ReadOnlyController.getInstance();
	// if (editable) {
	// roc.unlockData(this);
	// } else {
	// roc.lockData(this);
	// }
	//
	// fDataEditable = editable;
	//
	// notifyEditableChanged();
	// }

	// public void setEditable(boolean editable, boolean deep) {
	// if (deep) {
	// IJSONNode node = (IJSONNode) getFirstChild();
	// while (node != null) {
	// node.setEditable(editable, deep);
	// node = (IJSONNode) node.getNextSibling();
	// }
	// }
	// setChildEditable(editable);
	// setDataEditable(editable);
	// }

	/**
	 * setNextSibling method
	 * 
	 * @param nextSibling
	 *            org.w3c.dom.Node
	 */
	protected void setNextSibling(IJSONNode nextSibling) {
		this.nextSibling = (JSONNodeImpl) nextSibling;
	}

	/**
	 * setNodeValue method
	 * 
	 * @param nodeValue
	 *            java.lang.String
	 */
	public void setNodeValue(String nodeValue) {
	}

	protected void setOwnerDocument(IJSONDocument ownerDocument) {
		this.ownerDocument = (JSONDocumentImpl) ownerDocument;
	}

	/**
	 */
	protected void setOwnerDocument(IJSONDocument ownerDocument, boolean deep) {
		this.ownerDocument = (JSONDocumentImpl) ownerDocument;

		if (deep) {
			for (JSONNodeImpl child = (JSONNodeImpl) getFirstChild(); child != null; child = (JSONNodeImpl) child
					.getNextSibling()) {
				child.setOwnerDocument(ownerDocument, deep);
			}
		}
	}

	/**
	 * setParentNode method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 */
	protected void setParentNode(IJSONNode parentNode) {
		this.parentNode = (JSONNodeImpl) parentNode;
	}

	/**
	 * setPreviousSibling method
	 * 
	 * @param previousSibling
	 *            org.w3c.dom.Node
	 */
	protected void setPreviousSibling(IJSONNode previousSibling) {
		this.previousSibling = (JSONNodeImpl) previousSibling;
	}

	/**
	 */
	public void setSource(String source) {
		// not supported
	}

	/**
	 */
	void setStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		this.flatNode = flatNode;
	}

	/**
	 */
	public void setValueSource(String source) {
		setNodeValue(source);
	}

	// protected void syncDataEditableState() {
	// ReadOnlyController roc = ReadOnlyController.getInstance();
	// if (fDataEditable) {
	// roc.unlockData(this);
	// } else {
	// roc.lockData(this);
	// }
	// }

	/**
	 * toString method
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return getNodeName();
	}

	public int getLength() {
		int result = -1;
		int start = getStartOffset();
		if (start >= 0) {
			int end = getEndOffset();
			if (end >= 0) {
				result = end - start;
				if (result < -1) {
					result = -1;
				}
			}
		}
		return result;
	}

	@Override
	public IJSONPath getPath() {
		return null;
	}
}
