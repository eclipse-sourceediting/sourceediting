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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;


/**
 * 
 */
class CSSStyleDeclarationImpl extends CSSDocumentImpl implements ICSSStyleDeclaration {

	private boolean fIsDocument;

	CSSStyleDeclarationImpl(CSSStyleDeclarationImpl that) {
		super(that);
		this.fIsDocument = that.fIsDocument;
		if (fIsDocument) {
			setOwnerDocument(this);
		}
	}

	CSSStyleDeclarationImpl(boolean isDocument) {
		super();
		fIsDocument = isDocument;
		if (fIsDocument) {
			setOwnerDocument(this);
		}
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSStyleDeclarationImpl cloned = new CSSStyleDeclarationImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	public ICSSStyleDeclItem getDeclItemNode(String propertyName) {
		ICSSNode node = getLastChild();
		propertyName = propertyName.trim();
		while (node != null) {
			if (node instanceof CSSStyleDeclItemImpl) {
				ICSSStyleDeclItem item = (ICSSStyleDeclItem) node;
				if (propertyName.compareToIgnoreCase(item.getPropertyName().trim()) == 0)
					return item;
			}
			node = node.getPreviousSibling();
		}
		return null;
	}

	/**
	 * @return int
	 */
	public int getLength() {
		int i = 0;
		ICSSNode node = getFirstChild();
		while (node != null) {
			if (node instanceof CSSStyleDeclItemImpl)
				i++;
			node = node.getNextSibling();
		}
		return i;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return STYLEDECLARATION_NODE;
	}

	/**
	 * The CSS rule that contains this declaration block or <code>null</code>
	 * if this <code>CSSStyleDeclaration</code> is not attached to a
	 * <code>CSSRule</code>.
	 */
	public CSSRule getParentRule() {
		ICSSNode parent = getParentNode();
		if (parent instanceof CSSRule)
			return (CSSRule) parent;
		return null;
	}

	/**
	 * Used to retrieve the object representation of the value of a CSS
	 * property if it has been explicitly set within this declaration block.
	 * This method returns <code>null</code> if the property is a shorthand
	 * property. Shorthand property values can only be accessed and modified
	 * as strings, using the <code>getPropertyValue</code> and
	 * <code>setProperty</code> methods.
	 * 
	 * @param propertyName
	 *            The name of the CSS property. See the CSS property index.
	 * @return Returns the value of the property if it has been explicitly set
	 *         for this declaration block. Returns <code>null</code> if the
	 *         property has not been set.
	 */
	public CSSValue getPropertyCSSValue(String propertyName) {
		ICSSStyleDeclItem item = getDeclItemNode(propertyName);
		if (item != null)
			return item.getCSSValue();
		return null;
	}

	/**
	 * Used to retrieve the priority of a CSS property (e.g. the
	 * <code>"important"</code> qualifier) if the property has been
	 * explicitly set in this declaration block.
	 * 
	 * @param propertyName
	 *            The name of the CSS property. See the CSS property index.
	 * @return A string representing the priority (e.g.
	 *         <code>"important"</code>) if one exists. The empty string if
	 *         none exists.
	 */
	public String getPropertyPriority(String propertyName) {
		ICSSStyleDeclItem item = getDeclItemNode(propertyName);
		if (item != null)
			return item.getPriority();
		return null;
	}

	/**
	 * Used to retrieve the value of a CSS property if it has been explicitly
	 * set within this declaration block.
	 * 
	 * @param propertyName
	 *            The name of the CSS property. See the CSS property index.
	 * @return Returns the value of the property if it has been explicitly set
	 *         for this declaration block. Returns the empty string if the
	 *         property has not been set.
	 */
	public String getPropertyValue(String propertyName) {
		CSSValue value = getPropertyCSSValue(propertyName);
		if (value != null)
			return ((ICSSValue) value).getCSSValueText();
		return null;
	}

	/**
	 * @return boolean
	 */
	public boolean isDocument() {
		return fIsDocument;
	}

	/**
	 * Used to retrieve the properties that have been explicitly set in this
	 * declaration block. The order of the properties retrieved using this
	 * method does not have to be the order in which they were set. This
	 * method can be used to iterate over all properties in this declaration
	 * block.
	 * 
	 * @param index
	 *            Index of the property name to retrieve.
	 * @return The name of the property at this ordinal position. The empty
	 *         string if no property exists at this position.
	 */
	public String item(int index) {
		if (index < 0)
			return null;

		int i = 0;
		ICSSNode node = getFirstChild();
		while (node != null) {
			if (node instanceof CSSStyleDeclItemImpl) {
				if (i != index)
					i++;
				else {
					CSSStyleDeclItemImpl item = (CSSStyleDeclItemImpl) node;
					return item.getPropertyName();
				}
			}
			node = node.getNextSibling();
		}
		return null;
	}

	public ICSSStyleDeclItem removeDeclItemNode(ICSSStyleDeclItem oldDecl) throws DOMException {
		return (ICSSStyleDeclItem) removeChild((CSSNodeImpl) oldDecl);
	}

	/**
	 * Used to remove a CSS property if it has been explicitly set within this
	 * declaration block.
	 * 
	 * @param propertyName
	 *            The name of the CSS property. See the CSS property index.
	 * @return Returns the value of the property if it has been explicitly set
	 *         for this declaration block. Returns the empty string if the
	 *         property has not been set or the property name does not
	 *         correspond to a known CSS property.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this declaration
	 *                is readonly or the property is readonly.
	 */
	public String removeProperty(String propertyName) throws DOMException {
		ICSSStyleDeclItem item = getDeclItemNode(propertyName);
		if (item != null) {
			removeChild((CSSNodeImpl) item);
			return item.getCssText();
		}
		return null;
	}

	public ICSSStyleDeclItem setDeclItemNode(ICSSStyleDeclItem newDecl) throws DOMException {
		if (newDecl == null)
			return null;

		ICSSStyleDeclItem item = getDeclItemNode(newDecl.getPropertyName());
		if (item != null)
			return (ICSSStyleDeclItem) replaceChild((CSSNodeImpl) newDecl, (CSSNodeImpl) item);
		else
			return (ICSSStyleDeclItem) appendChild((CSSNodeImpl) newDecl);
	}

	/**
	 * Used to set a property value and priority within this declaration
	 * block.
	 * 
	 * @param propertyName
	 *            The name of the CSS property. See the CSS property index.
	 * @param value
	 *            The new value of the property.
	 * @param priority
	 *            The new priority of the property (e.g.
	 *            <code>"important"</code>).
	 * @exception DOMException
	 *                SYNTAX_ERR: Raised if the specified value has a syntax
	 *                error and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this declaration
	 *                is readonly or the property is readonly.
	 */
	public void setProperty(String propertyName, String value, String priority) throws DOMException {
		if (propertyName.equals("")) //$NON-NLS-1$
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, ""); //$NON-NLS-1$

		// You can use this only if CSSValue.setCssText() is implemented
		CSSStyleDeclItemImpl item = (CSSStyleDeclItemImpl) getDeclItemNode(propertyName);
		if (item == null) {
			item = (CSSStyleDeclItemImpl) getOwnerDocument().createCSSStyleDeclItem(propertyName);
			appendChild(item);
		}

		// ICSSNode next = item.getNextSibling();
		// removeChild(item);

		item.setCssValueText(value);
		item.setPriority(priority);

		// insertBefore(item, (CSSNodeImpl)next);
	}
}
