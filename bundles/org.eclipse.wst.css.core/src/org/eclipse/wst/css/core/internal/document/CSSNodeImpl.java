/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceGenerator;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNamedNodeMap;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNodeList;
import org.eclipse.wst.css.core.internal.util.ImportRuleCollector;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.AbstractNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.w3c.dom.DOMException;


/**
 * 
 */
public abstract class CSSNodeImpl extends AbstractNotifier implements ICSSNode, IndexedRegion, IAdaptable {

	private CSSDocumentImpl fOwnerDocument = null;
	private CSSNodeImpl fParentNode = null;
	private CSSNodeImpl fNextSibling = null;
	private CSSNodeImpl fPreviousSibling = null;
	private CSSNodeImpl fFirstChild = null;
	private CSSNodeImpl fLastChild = null;
	protected CSSNamedNodeMapImpl fAttrs = null;

	/**
	 * CSSNodeImpl constructor comment.
	 */
	CSSNodeImpl() {
		super();
	}

	CSSNodeImpl(CSSNodeImpl that) {
		if (that != null) {
			this.fOwnerDocument = that.fOwnerDocument;
			if (that.fAttrs != null) {
				int nAttrs = that.fAttrs.getLength();
				for (int i = 0; i < nAttrs; i++) {
					CSSAttrImpl attr = (CSSAttrImpl) that.fAttrs.item(i);
					setAttribute(attr.getName(), attr.getValue());
				}
			}
		}
	}

	public Object getAdapter(Class adapter) {
		final IStructuredModel model = fOwnerDocument != null ? fOwnerDocument.getModel() : null;
		return model != null ? Platform.getAdapterManager().getAdapter(model, adapter) : null;
	}

	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSNodeImpl appendChild(CSSNodeImpl newChild) throws org.w3c.dom.DOMException {
		return insertBefore(newChild, null);
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	protected void cloneChildNodes(ICSSNode newParent, boolean deep) {
		if (newParent == null || newParent == this)
			return;

		CSSNodeImpl container = (CSSNodeImpl) newParent;
		container.removeChildNodes();

		for (ICSSNode child = getFirstChild(); child != null; child = child.getNextSibling()) {
			CSSNodeImpl cloned = (CSSNodeImpl) child.cloneNode(deep);
			if (cloned != null)
				container.appendChild(cloned);
		}
	}

	/**
	 * @return boolean
	 * @param offset
	 *            int
	 */
	public boolean contains(int offset) {
		return (getStartOffset() <= offset && offset < getEndOffset());
	}

	/**
	 *
	 * currently public but may be made default access protected in future.
	 *
	 * @return java.lang.String
	 */
	protected String generateSource() {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(this);
		return formatter.format(this).toString();
	}

	/**
	 *
	 * currently public but may be made default access protected in future.
	 *
	 * @return java.lang.String
	 * @param name
	 *            java.lang.String
	 */
	protected String getAttribute(String name) {
		CSSAttrImpl attr = getAttributeNode(name);
		if (attr != null)
			return attr.getValue();
		return null;
	}

	protected CSSAttrImpl getAttributeNode(String name) {
		if (fAttrs == null)
			return null;

		int nAttrs = fAttrs.getLength();
		for (int i = 0; i < nAttrs; i++) {
			CSSAttrImpl attr = (CSSAttrImpl) fAttrs.item(i);
			if (attr.matchName(name))
				return attr;
		}
		return null;
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNamedNodeMap
	 */
	public ICSSNamedNodeMap getAttributes() {
		if (fAttrs == null)
			fAttrs = new CSSNamedNodeMapImpl();
		return fAttrs;
	}

	public ICSSNodeList getChildNodes() {
		CSSNodeListImpl list = new CSSNodeListImpl();
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			list.appendNode(node);
		}
		return list;
	}

	ICSSNode getCommonAncestor(ICSSNode node) {
		if (node == null)
			return null;

		for (ICSSNode na = node; na != null; na = na.getParentNode()) {
			for (ICSSNode ta = this; ta != null; ta = ta.getParentNode()) {
				if (ta == na)
					return ta;
			}
		}

		return null; // not found
	}

	CSSDocumentImpl getContainerDocument() {
		for (ICSSNode node = this; node != null; node = node.getParentNode()) {
			if (node instanceof CSSDocumentImpl) {
				CSSDocumentImpl doc = (CSSDocumentImpl) node;
				if (doc.isDocument())
					return doc;
			}
		}
		return null;
	}

	CSSNodeImpl getContainerNode(int offset) {
		if (!contains(offset))
			return null;

		for (ICSSNode child = getFirstChild(); child != null; child = child.getNextSibling()) {
			ICSSNode found = ((CSSNodeImpl) child).getContainerNode(offset);
			if (found != null)
				return (CSSNodeImpl) found;
		}

		return this;
	}

	/**
	 */
	public FactoryRegistry getFactoryRegistry() {
		ICSSModel model = getOwnerDocument().getModel();
		if (model != null) {
			FactoryRegistry reg = model.getFactoryRegistry();
			if (reg != null)
				return reg;
		}
		return null;
	}

	public ICSSNode getFirstChild() {
		return this.fFirstChild;
	}

	public ICSSNode getLastChild() {
		return this.fLastChild;
	}

	public ICSSNode getNextSibling() {
		return this.fNextSibling;
	}

	ICSSNode getNodeAt(int offset) {
		// the same as getContainerNode()
		return getContainerNode(offset);
	}

	public ICSSDocument getOwnerDocument() {
		return this.fOwnerDocument;
	}

	public ICSSNode getParentNode() {
		return this.fParentNode;
	}

	public ICSSNode getPreviousSibling() {
		return this.fPreviousSibling;
	}

	ICSSNode getRootNode() {
		CSSNodeImpl parent = (CSSNodeImpl) getParentNode();
		if (parent == null)
			return this;
		return parent.getRootNode();
	}

	/**
	 * @return boolean
	 */
	public boolean hasChildNodes() {
		return (this.fFirstChild != null);
	}

	/**
	 * @return boolean
	 */
	public boolean hasProperties() {
		return false;
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSNodeImpl insertBefore(CSSNodeImpl newChild, CSSNodeImpl refChild) throws org.w3c.dom.DOMException {
		if (newChild == null)
			return null;

		CSSNodeImpl child = newChild;
		CSSNodeImpl next = refChild;
		CSSNodeImpl prev = null;
		if (next == null) {
			prev = this.fLastChild;
			this.fLastChild = child;
		}
		else {
			prev = (CSSNodeImpl) next.getPreviousSibling();
			next.setPreviousSibling(child);
		}

		if (prev == null)
			this.fFirstChild = child;
		else
			prev.setNextSibling(child);
		child.setPreviousSibling(prev);
		child.setNextSibling(next);
		child.setParentNode(this);

		notifyChildReplaced(child, null);

		return newChild;
	}

	protected void notifyAttrReplaced(CSSNodeImpl newAttr, CSSNodeImpl oldAttr) {
		// for model
		ICSSDocument doc = getContainerDocument();
		if (doc == null)
			return;
		CSSModelImpl model = (CSSModelImpl) doc.getModel();
		if (model == null)
			return;
		model.attrReplaced(this, newAttr, oldAttr);

		// for adapters
		int type = CHANGE;
		if (newAttr == null)
			type = REMOVE;
		else if (oldAttr == null)
			type = ADD;
		notify(type, oldAttr, oldAttr, newAttr, getStartOffset());
	}

	protected void notifyChildReplaced(CSSNodeImpl newChild, CSSNodeImpl oldChild) {
		// for model
		ICSSDocument doc = getContainerDocument();
		if (doc == null)
			return;
		CSSModelImpl model = (CSSModelImpl) doc.getModel();
		if (model == null)
			return;
		model.childReplaced(this, newChild, oldChild);

		// for adapters
		int type = CHANGE;
		if (newChild == null)
			type = REMOVE;
		else if (oldChild == null)
			type = ADD;
		notify(type, oldChild, oldChild, newChild, getStartOffset());
	}

	void removeAttributeNode(CSSNodeImpl attr) {
		// find
		int nAttrs = fAttrs.getLength();
		for (int i = 0; i < nAttrs; i++) {
			if (fAttrs.item(i) == attr) {
				fAttrs.removeNode(i);
				notifyAttrReplaced(null, attr);
				return;
			}
		}
	}

	protected CSSNodeImpl removeChild(CSSNodeImpl oldChild) throws org.w3c.dom.DOMException {
		if (oldChild == null)
			return null;
		if (oldChild.getParentNode() != this)
			return null;

		// close import rules
		ImportRuleCollector trav = new ImportRuleCollector();
		trav.apply(oldChild);
		Iterator it = trav.getRules().iterator();
		while (it.hasNext()) {
			((CSSImportRuleImpl) it.next()).closeStyleSheet();
		}

		CSSNodeImpl child = oldChild;
		CSSNodeImpl prev = (CSSNodeImpl) child.getPreviousSibling();
		CSSNodeImpl next = (CSSNodeImpl) child.getNextSibling();

		if (prev == null)
			this.fFirstChild = next;
		else
			prev.setNextSibling(next);

		if (next == null)
			this.fLastChild = prev;
		else
			next.setPreviousSibling(prev);

		child.setPreviousSibling(null);
		child.setNextSibling(null);
		child.setParentNode(null);

		notifyChildReplaced(null, child);

		return child;
	}

	/**
	 * 
	 */
	void removeChildNodes() {
		ICSSNode nextChild = null;
		for (ICSSNode child = getFirstChild(); child != null; child = nextChild) {
			nextChild = child.getNextSibling();
			removeChild((CSSNodeImpl) child);
		}
	}

	protected CSSNodeImpl replaceChild(CSSNodeImpl newChild, CSSNodeImpl oldChild) throws org.w3c.dom.DOMException {
		if (oldChild == null)
			return newChild;
		if (newChild != null)
			insertBefore(newChild, oldChild);
		return removeChild(oldChild);
	}

	/**
	 *
	 * currently public but may be made default access protected in future.
	 *
	 * @param name
	 *            java.lang.String
	 * @param value
	 *            java.lang.String
	 */
	public void setAttribute(String name, String value) {
		if (name == null)
			return;

		CSSAttrImpl attr = getAttributeNode(name);
		if (attr != null) {
			String oldValue = attr.getValue();
			if (value != null && value.equals(oldValue))
				return;
			if (value == null) {
				if (oldValue != null) {
					removeAttributeNode(attr);
				}
				return;
			}
		}
		else {
			if (value == null)
				return;
			if (fAttrs == null)
				fAttrs = new CSSNamedNodeMapImpl();
			CSSDocumentImpl doc = (CSSDocumentImpl) getOwnerDocument();
			if (doc == null)
				return;
			attr = (CSSAttrImpl) doc.createCSSAttr(name);
			attr.setOwnerCSSNode(this);
			fAttrs.appendNode(attr);
			notifyAttrReplaced(attr, null);
		}
		attr.setValue(value);
	}

	/**
	 * @param cssText
	 *            java.lang.String
	 */
	public void setCssText(String cssText) {
		// TODO : call flat model parser and replace myself with new three!!
		throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
	}

	private void setNextSibling(ICSSNode nextSibling) {
		this.fNextSibling = (CSSNodeImpl) nextSibling;
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	public void setOwnerDocument(ICSSDocument ownerDocument) {
		this.fOwnerDocument = (CSSDocumentImpl) ownerDocument;
	}

	private void setParentNode(ICSSNode parentNode) {
		this.fParentNode = (CSSNodeImpl) parentNode;
	}

	private void setPreviousSibling(ICSSNode previousSibling) {
		this.fPreviousSibling = (CSSNodeImpl) previousSibling;
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
}
