/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceGenerator;
import org.eclipse.wst.css.core.internal.formatter.StyleDeclItemFormatter;
import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;


/**
 * 
 */
class CSSStyleDeclItemImpl extends CSSStructuredDocumentRegionContainer implements ICSSStyleDeclItem {

	private java.lang.String fPropertyName;

	/**
	 * CSSStyleDeclItemImpl constructor comment.
	 * 
	 */
	CSSStyleDeclItemImpl(CSSStyleDeclItemImpl that) {
		super(that);
		this.fPropertyName = that.fPropertyName;
	}

	/**
	 * CSSStyleDeclItemImpl constructor comment.
	 */
	CSSStyleDeclItemImpl(String propertyName) {
		super();
		this.fPropertyName = propertyName;
	}

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param value
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 */
	public ICSSPrimitiveValue appendValue(ICSSPrimitiveValue value) throws DOMException {
		return insertValueBefore(value, null);
	}

	/**
	 * @param deep
	 *            boolean
	 */
	public ICSSNode cloneNode(boolean deep) {
		CSSStyleDeclItemImpl cloned = new CSSStyleDeclItemImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * @return java.lang.String
	 */
	String generateValueSource() {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(this);
		if (formatter != null && formatter instanceof StyleDeclItemFormatter)
			return ((StyleDeclItemFormatter) formatter).formatValue(this).toString();
		else
			return "";//$NON-NLS-1$
	}

	/**
	 * @return org.w3c.dom.css.CSSValue
	 */
	public CSSValue getCSSValue() {
		int nValue = getLength();
		if (nValue <= 0)
			return null;
		else if (nValue == 1)
			return item(0);
		else
			return this;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getCSSValueText() {
		if (getFirstChild() == null)
			return "";//$NON-NLS-1$
		// check whether children has flatnodes
		ICSSNode child = getFirstChild();
		while (child != null) {
			if (((IndexedRegion) child).getEndOffset() <= 0)
				return generateValueSource();
			child = child.getNextSibling();
		}

		IStructuredDocumentRegion node = getFirstStructuredDocumentRegion();
		int start = ((IndexedRegion) getFirstChild()).getStartOffset() - node.getStartOffset();
		int end = ((IndexedRegion) getLastChild()).getEndOffset() - node.getStartOffset();
		return node.getText().substring(start, end);
	}

	/**
	 * @return short
	 */
	public short getCssValueType() {
		return CSS_VALUE_LIST;
	}

	/**
	 * @return int
	 */
	public int getLength() {
		int i = 0;
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSPrimitiveValueImpl)
				i++;
		}
		return i;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return STYLEDECLITEM_NODE;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getPriority() {
		return getAttribute(IMPORTANT);
	}

	/**
	 * @return java.lang.String
	 */
	public String getPropertyName() {
		String name = fPropertyName.trim();
		return PropCMProperty.getInstanceOf(name) != null ? fPropertyName.trim().toLowerCase() : name;
	}

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param newValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @param refValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 */
	ICSSPrimitiveValue insertValueBefore(ICSSPrimitiveValue newValue, ICSSPrimitiveValue refValue) {
		ICSSNode node = insertBefore((CSSNodeImpl) newValue, (CSSNodeImpl) refValue);
		return (ICSSPrimitiveValue) node;
	}

	/**
	 * @return org.w3c.dom.css.CSSValue
	 * @param index
	 *            int
	 */
	public CSSValue item(int index) {
		int i = 0;
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSPrimitiveValueImpl) {
				if (i++ == index)
					return (CSSValue) node;
			}
		}
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param value
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 */
	public ICSSPrimitiveValue removeValue(ICSSPrimitiveValue value) throws DOMException {
		ICSSNode node = removeChild((CSSNodeImpl) value);
		return (ICSSPrimitiveValue) node;
	}

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 * @param newValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 * @param oldValue
	 *            org.w3c.dom.css.CSSPrimitiveValue
	 */
	public ICSSPrimitiveValue replaceValue(ICSSPrimitiveValue newValue, ICSSPrimitiveValue oldValue) throws DOMException {
		if (oldValue == null)
			return newValue;
		if (newValue != null)
			insertValueBefore(newValue, oldValue);
		return removeValue(oldValue);
	}

	/**
	 * @param value
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setCssValueText(String value) throws DOMException {
		ICSSNode child = getFirstChild();
		while (child != null) {
			ICSSNode next = child.getNextSibling();
			if (child instanceof ICSSPrimitiveValue) {
				removeChild((CSSNodeImpl) child);
			}
			child = next;
		}
		setCssValueTextCore(value);
	}

	private void setCssValueTextCore(String value) throws DOMException {
		CSSSourceParser parser = new CSSSourceParser();
		parser.setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		parser.reset(value);
		
		IStructuredDocumentRegion node = parser.getDocumentRegions();

		if (node == null) {
			return;
		}
		if (node.getNext() != null) {
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}

		CSSDeclarationItemParser itemParser = new CSSDeclarationItemParser(getOwnerDocument());
		itemParser.setStructuredDocumentTemporary(true);
		// make a copy of nodelist because setupValues will destroy list
		ITextRegionList nodeList = new TextRegionListImpl(node.getRegions());
		List nodeValuesList = new ArrayList();;
		for (int i=0;i<nodeList.size();i++){
			ITextRegion textRegion = nodeList.get(i);
			nodeValuesList.add(value.substring(textRegion.getStart(), textRegion.getTextEnd()));
		}
		itemParser.setupValues(this, node, nodeList, nodeValuesList);
	}

	/**
	 * @param newPriority
	 *            java.lang.String
	 */
	public void setPriority(java.lang.String newPriority) throws DOMException {
		setAttribute(IMPORTANT, newPriority);
	}
}
