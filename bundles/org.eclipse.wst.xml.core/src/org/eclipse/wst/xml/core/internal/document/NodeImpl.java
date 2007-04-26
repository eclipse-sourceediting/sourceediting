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



import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.AbstractNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;


/**
 * NodeImpl class
 */
public abstract class NodeImpl extends AbstractNotifier implements Node, IDOMNode {
	// define one empty nodelist, for repeated use
	private final static NodeList EMPTY_NODE_LIST = new NodeListImpl();

	private boolean fDataEditable = true;
	private IStructuredDocumentRegion flatNode = null;
	private NodeImpl nextSibling = null;

	private DocumentImpl ownerDocument = null;
	private NodeImpl parentNode = null;
	private NodeImpl previousSibling = null;

	/**
	 * NodeImpl constructor
	 */
	protected NodeImpl() {
		super();
	}

	/**
	 * NodeImpl constructor
	 * 
	 * @param that
	 *            NodeImpl
	 */
	protected NodeImpl(NodeImpl that) {
		if (that != null) {
			this.ownerDocument = that.ownerDocument;
		}
	}

	/**
	 * appendChild method
	 * 
	 * @return org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 */
	public Node appendChild(Node newChild) throws DOMException {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, new String());
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
	protected String createDOMExceptionMessage(short s, String tagName) {
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
		DocumentImpl document = (DocumentImpl) getOwnerDocument();
		if (document == null)
			return null;
		return document.getCharValue(name);
	}

	/**
	 * getChildNodes method
	 * 
	 * @return org.w3c.dom.NodeList
	 */
	public NodeList getChildNodes() {
		// As per DOM spec, correct behavior for getChildNodes is to return a
		// zero length NodeList, not null, when there are no children.
		// We'll use a common instance of an empty node list, just to prevent
		// creating a trival object many many times.

		return EMPTY_NODE_LIST;
	}

	/**
	 * getCommonAncestor method
	 * 
	 * @return org.w3c.dom.Node
	 * @param node
	 *            org.w3c.dom.Node
	 */
	public Node getCommonAncestor(Node node) {
		if (node == null)
			return null;

		for (Node na = node; na != null; na = na.getParentNode()) {
			for (Node ta = this; ta != null; ta = ta.getParentNode()) {
				if (ta == na)
					return ta;
			}
		}

		return null; // not found
	}

	/**
	 * getContainerDocument method
	 * 
	 * @return org.w3c.dom.Document
	 */
	public Document getContainerDocument() {
		for (Node node = this; node != null; node = node.getParentNode()) {
			if (node.getNodeType() == Node.DOCUMENT_NODE) {
				return (Document) node;
			}
		}
		return null;
	}

	/**
	 * getEndOffset method
	 * 
	 * @return int
	 */
	public int getEndOffset() {
		Node node = this;
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) node;
				IStructuredDocumentRegion endStructuredDocumentRegion = element.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null)
					return endStructuredDocumentRegion.getEnd();
			}

			Node last = node.getLastChild();
			if (last != null) { // dig into the last
				node = last;
				continue;
			}

			IStructuredDocumentRegion lastStructuredDocumentRegion = ((NodeImpl) node).getStructuredDocumentRegion();
			if (lastStructuredDocumentRegion != null)
				return lastStructuredDocumentRegion.getEnd();

			Node prev = node.getPreviousSibling();
			if (prev != null) { // move to the previous
				node = prev;
				continue;
			}

			Node parent = node.getParentNode();
			node = null;
			while (parent != null) {
				if (parent.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl element = (ElementImpl) parent;
					IStructuredDocumentRegion startStructuredDocumentRegion = element.getStartStructuredDocumentRegion();
					if (startStructuredDocumentRegion != null)
						return startStructuredDocumentRegion.getEnd();
				}
				Node parentPrev = parent.getPreviousSibling();
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
		IDOMModel model = getModel();
		if (model != null) {
			FactoryRegistry reg = model.getFactoryRegistry();
			if (reg != null)
				return reg;
		}
		return null;
	}

	/**
	 * getFirstChild method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node getFirstChild() {
		return null;
	}

	/**
	 * getFirstStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil.getStructuredDocumentRegion(this.flatNode);
	}

	/**
	 */
	public int getIndex() {
		Node parent = getParentNode();
		if (parent == null)
			return -1; // error
		int index = 0;
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child == this)
				return index;
			index++;
		}
		return -1; // error
	}

	/**
	 * getLastChild method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node getLastChild() {
		return null;
	}

	/**
	 * getLastStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil.getStructuredDocumentRegion(this.flatNode);
	}

	/**
	 */
	public String getLocalName() {
		return null;
	}

	/**
	 * the default implementation can just refer to the owning document
	 */
	public IDOMModel getModel() {
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

	/**
	 * getNextSibling method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node getNextSibling() {
		return this.nextSibling;
	}

	/**
	 * getNodeAt method
	 * 
	 * @return org.w3c.dom.Node
	 * @param offset
	 *            int
	 */
	Node getNodeAt(int offset) {
		IDOMNode parent = this;
		IDOMNode child = (IDOMNode) getFirstChild();
		while (child != null) {
			if (child.getEndOffset() <= offset) {
				child = (IDOMNode) child.getNextSibling();
				continue;
			}
			if (child.getStartOffset() > offset) {
				break;
			}

			IStructuredDocumentRegion startStructuredDocumentRegion = child.getStartStructuredDocumentRegion();
			if (startStructuredDocumentRegion != null) {
				if (startStructuredDocumentRegion.getEnd() > offset)
					return child;
			}

			// dig more
			parent = child;
			child = (IDOMNode) parent.getFirstChild();
		}

		return parent;
	}

	/**
	 * getNodeValue method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeValue() throws DOMException {
		return null;
	}

	/**
	 * getOwnerDocument method
	 * 
	 * @return org.w3c.dom.Document
	 */
	public Document getOwnerDocument() {
		return this.ownerDocument;
	}

	/**
	 * getParentNode method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node getParentNode() {
		return this.parentNode;
	}

	/**
	 */
	public String getPrefix() {
		return null;
	}

	/**
	 * getPreviousSibling method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node getPreviousSibling() {
		return this.previousSibling;
	}

	/**
	 */
	public String getSource() {
		if (this.flatNode == null)
			return new String();
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
		NodeImpl prev = (NodeImpl) getPreviousSibling();
		if (prev != null)
			return prev.getEndOffset();
		Node parent = getParentNode();
		if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			ElementImpl element = (ElementImpl) parent;
			if (element.hasStartTag())
				return element.getStartEndOffset();
			return element.getStartOffset();
		}
		// final fallback to look into first child
		NodeImpl child = (NodeImpl) getFirstChild();
		while (child != null) {
			IStructuredDocumentRegion childStructuredDocumentRegion = child.getStructuredDocumentRegion();
			if (childStructuredDocumentRegion != null)
				return childStructuredDocumentRegion.getStart();
			child = (NodeImpl) child.getFirstChild();
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
	 */
	public String getValueSource() {
		return getNodeValue();
	}

	/**
	 */
	public boolean hasAttributes() {
		return false;
	}

	/**
	 * hasChildNodes method
	 * 
	 * @return boolean
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
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, new String());
	}

	public boolean isChildEditable() {
		return false;
	}

	/**
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
			DOMModelImpl model = (DOMModelImpl) getModel();
			if (model != null && model.isReparsing()) {
				return true;
			}
		}
		return fDataEditable;
	}

	/**
	 */
	public boolean isSupported(String feature, String version) {
		if (this.ownerDocument == null)
			return false;
		DOMImplementation impl = this.ownerDocument.getImplementation();
		if (impl == null)
			return false;
		return impl.hasFeature(feature, version);
	}

	/**
	 * @param s
	 * @return
	 */
	private String lookupMessage(short s) {
		// TODO: make localized version
		String result = null;
		switch (s) {
			case DOMException.INVALID_CHARACTER_ERR :
				result = "INVALID_CHARACTER_ERR"; //$NON-NLS-1$
				break;

			default :
				result = new String();
				break;
		}
		return result;
	}

	/**
	 * normalize method
	 */
	public void normalize() {
		TextImpl prevText = null;
		for (Node child = getFirstChild(); child != null; child = child.getNextSibling()) {
			switch (child.getNodeType()) {
				case TEXT_NODE : {
					if (prevText == null) {
						prevText = (TextImpl) child;
						break;
					}
					Text text = (Text) child;
					removeChild(text);
					prevText.appendText(text);
					child = prevText;
					break;
				}
				case ELEMENT_NODE : {
					Element element = (Element) child;
					element.normalize();
					prevText = null;
					break;
				}
				default :
					prevText = null;
					break;
			}
		}
	}

	protected void notifyEditableChanged() {
		DocumentImpl document = (DocumentImpl) getContainerDocument();
		if (document == null)
			return;
		DOMModelImpl model = (DOMModelImpl) document.getModel();
		if (model == null)
			return;
		model.editableChanged(this);
	}

	/**
	 * notifyValueChanged method
	 */
	protected void notifyValueChanged() {
		DocumentImpl document = (DocumentImpl) getContainerDocument();
		if (document == null)
			return;

		syncDataEditableState();

		DOMModelImpl model = (DOMModelImpl) document.getModel();
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
	public Node removeChild(Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_FOUND_ERR, new String());
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
	public DocumentFragment removeChildNodes(Node firstChild, Node lastChild) {
		return null;
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
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, new String());
	}

	/**
	 * Resets children values from IStructuredDocumentRegion.
	 */
	void resetStructuredDocumentRegions() {
		for (NodeImpl child = (NodeImpl) getFirstChild(); child != null; child = (NodeImpl) child.getNextSibling()) {
			child.resetStructuredDocumentRegions();
		}
		this.flatNode = null;
	}

	public void setChildEditable(boolean editable) {
		// nop
	}

	public void setDataEditable(boolean editable) {
		if (fDataEditable == editable) {
			return;
		}

		ReadOnlyController roc = ReadOnlyController.getInstance();
		if (editable) {
			roc.unlockData(this);
		}
		else {
			roc.lockData(this);
		}

		fDataEditable = editable;

		notifyEditableChanged();
	}

	public void setEditable(boolean editable, boolean deep) {
		if (deep) {
			IDOMNode node = (IDOMNode) getFirstChild();
			while (node != null) {
				node.setEditable(editable, deep);
				node = (IDOMNode) node.getNextSibling();
			}
		}
		setChildEditable(editable);
		setDataEditable(editable);
	}

	/**
	 * setNextSibling method
	 * 
	 * @param nextSibling
	 *            org.w3c.dom.Node
	 */
	protected void setNextSibling(Node nextSibling) {
		this.nextSibling = (NodeImpl) nextSibling;
	}

	/**
	 * setNodeValue method
	 * 
	 * @param nodeValue
	 *            java.lang.String
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
	}

	/**
	 * setOwnerDocument method
	 * 
	 * @param ownerDocument
	 *            org.w3c.dom.Document
	 */
	protected void setOwnerDocument(Document ownerDocument) {
		this.ownerDocument = (DocumentImpl) ownerDocument;
	}

	/**
	 */
	protected void setOwnerDocument(Document ownerDocument, boolean deep) {
		this.ownerDocument = (DocumentImpl) ownerDocument;

		if (deep) {
			for (NodeImpl child = (NodeImpl) getFirstChild(); child != null; child = (NodeImpl) child.getNextSibling()) {
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
	protected void setParentNode(Node parentNode) {
		this.parentNode = (NodeImpl) parentNode;
	}

	/**
	 */
	public void setPrefix(String prefix) throws DOMException {
	}

	/**
	 * setPreviousSibling method
	 * 
	 * @param previousSibling
	 *            org.w3c.dom.Node
	 */
	protected void setPreviousSibling(Node previousSibling) {
		this.previousSibling = (NodeImpl) previousSibling;
	}

	/**
	 */
	public void setSource(String source) throws InvalidCharacterException {
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

	protected void syncDataEditableState() {
		ReadOnlyController roc = ReadOnlyController.getInstance();
		if (fDataEditable) {
			roc.unlockData(this);
		}
		else {
			roc.lockData(this);
		}
	}

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

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public short compareDocumentPosition(Node other) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getBaseURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getFeature(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getTextContent() throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getUserData(String key) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isDefaultNamespace(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isEqualNode(Node arg) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isSameNode(Node other) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupNamespaceURI(String prefix) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupPrefix(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public void setTextContent(String textContent) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public TypeInfo getSchemaTypeInfo() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isId() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}


}
