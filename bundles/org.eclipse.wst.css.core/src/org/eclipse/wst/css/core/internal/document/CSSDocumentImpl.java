/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.document.ICSSCharsetRule;
import org.eclipse.wst.css.core.document.ICSSDocument;
import org.eclipse.wst.css.core.document.ICSSImportRule;
import org.eclipse.wst.css.core.document.ICSSMediaRule;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.document.ICSSPageRule;
import org.eclipse.wst.css.core.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.document.ICSSStyleRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
abstract class CSSDocumentImpl extends CSSStructuredDocumentRegionContainer implements ICSSDocument {

	private CSSModelImpl fModel = null;

	/**
	 * CSSDocumentImpl constructor comment.
	 */
	CSSDocumentImpl() {
		super();
	}

	/**
	 * @param that
	 *            com.ibm.sed.css.treemodel.CSSDocumentImpl
	 */
	CSSDocumentImpl(CSSDocumentImpl that) {
		super(that);
	}

	/**
	 * @return com.ibm.sed.css.interfaces.ICSSNode
	 * @param name
	 *            java.lang.String
	 */
	ICSSNode createCSSAttr(String name) {
		CSSAttrImpl attr = new CSSAttrImpl(name);
		attr.setOwnerDocument(this);

		return attr;
	}

	/**
	 * @return org.w3c.dom.css.CSSCharsetRule
	 */
	public ICSSCharsetRule createCSSCharsetRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSFontFaceRule
	 */
	public CSSFontFaceRule createCSSFontFaceRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSImportRule
	 */
	public ICSSImportRule createCSSImportRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.ICSSMediaRule
	 */
	public ICSSMediaRule createCSSMediaRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSPageRule
	 */
	public ICSSPageRule createCSSPageRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSPrimitiveValue
	 */
	public ICSSPrimitiveValue createCSSPrimitiveValue(short primitiveType) {
		CSSPrimitiveValueImpl value = null;
		if (primitiveType == CSSPrimitiveValue.CSS_COUNTER)
			value = new CounterImpl();
		else if (primitiveType == CSSPrimitiveValue.CSS_RECT)
			value = new RectImpl();
		else if (primitiveType == CSSPrimitiveValue.CSS_RGBCOLOR)
			value = new RGBColorImpl();
		else
			value = new CSSPrimitiveValueImpl(primitiveType);
		value.setOwnerDocument(this);

		return value;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            java.lang.String
	 */
	public CSSRule createCSSRule(String rule) {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSStyleDeclaration
	 */
	public ICSSStyleDeclaration createCSSStyleDeclaration() {
		return null;
	}

	/**
	 * @return com.ibm.sed.treemodel.cei.CSSStyleDeclItem
	 * @param propertyName
	 *            java.lang.String
	 */
	public ICSSStyleDeclItem createCSSStyleDeclItem(String propertyName) {
		CSSStyleDeclItemImpl item = new CSSStyleDeclItemImpl(propertyName);
		item.setOwnerDocument(this);

		return item;
	}

	/**
	 * @return org.w3c.dom.css.CSSStyleRule
	 */
	public ICSSStyleRule createCSSStyleRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSUnknownRule
	 */
	public CSSUnknownRule createCSSUnknownRule() {
		return null;
	}

	/**
	 * @return org.w3c.dom.stylesheets.MediaList
	 */
	public MediaList createMediaList() {
		return null;
	}

	/**
	 * @return com.ibm.sed.css.interfaces.ICSSModel
	 */
	public ICSSModel getModel() {
		return fModel;
	}

	/**
	 * @param model
	 *            com.ibm.sed.css.treemodel.CSSModelImpl
	 */
	void setModel(CSSModelImpl model) {
		this.fModel = model;
	}
}