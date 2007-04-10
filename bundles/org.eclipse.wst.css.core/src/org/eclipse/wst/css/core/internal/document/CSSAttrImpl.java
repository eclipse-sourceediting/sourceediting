/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNamedNodeMap;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;

/**
 * 
 */
class CSSAttrImpl extends CSSRegionContainer implements ICSSAttr {

	private String fName = null;
	private String fValue = null;
	private CSSNodeImpl ownerCSSNode = null;

	CSSAttrImpl(CSSAttrImpl that) {
		super(that);
		this.fName = that.fName;
		this.setValue(that.fValue);
	}

	/**
	 * CSSAttr constructor comment.
	 */
	CSSAttrImpl(String name) {
		super();
		this.fName = name;
	}

	public ICSSNode cloneNode(boolean deep) {
		return new CSSAttrImpl(this);
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNamedNodeMap
	 */
	public ICSSNamedNodeMap getAttributes() {
		return null;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return fName;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return ATTR_NODE;
	}

	public ICSSNode getOwnerCSSNode() {
		return ownerCSSNode;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getValue() {
		return fValue;
	}

	/**
	 * @return boolean
	 * @param name
	 *            java.lang.String
	 */
	protected boolean matchName(String name) {
		if (name == null)
			return /* (this.name == null) */false;
		if (this.fName == null)
			return false;
		return this.fName.equals(name);
	}

	/**
	 * 
	 */
	protected void notifyValueChanged(String oldValue) {
		if (this.ownerCSSNode == null)
			return;

		// for model
		ICSSDocument doc = ownerCSSNode.getContainerDocument();
		if (doc == null)
			return;
		CSSModelImpl model = (CSSModelImpl) doc.getModel();
		if (model == null)
			return;
		model.valueChanged(this, oldValue);

		// for adapters
		String value = getValue();
		this.ownerCSSNode.notify(CHANGE, this, oldValue, value, this.ownerCSSNode.getStartOffset());
	}

	/**
	 * @param newName
	 *            java.lang.String
	 */
	protected void setName(java.lang.String newName) {
		fName = newName;
	}

	protected void setOwnerCSSNode(CSSNodeImpl newOwnerCSSNode) {
		ownerCSSNode = newOwnerCSSNode;
	}

	/**
	 * @param newValue
	 *            java.lang.String
	 */
	public void setValue(java.lang.String newValue) {
		String oldValue = fValue;
		fValue = newValue;

		notifyValueChanged(oldValue);
	}
}
