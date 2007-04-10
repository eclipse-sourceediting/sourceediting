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
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.Rect;


/**
 * 
 */
class RectImpl extends CSSPrimitiveContainer implements Rect {

	private CSSPrimitiveValueImpl fTop;
	private CSSPrimitiveValueImpl fRight;
	private CSSPrimitiveValueImpl fBottom;
	private CSSPrimitiveValueImpl fLeft;

	/**
	 * 
	 */
	RectImpl() {
		super(CSS_RECT);
	}

	RectImpl(RectImpl that) {
		super(that);

		fTop = new CSSPrimitiveValueImpl(that.fTop);
		appendChild(fTop);

		fRight = new CSSPrimitiveValueImpl(that.fRight);
		appendChild(fRight);

		fBottom = new CSSPrimitiveValueImpl(that.fBottom);
		appendChild(fBottom);

		fLeft = new CSSPrimitiveValueImpl(that.fLeft);
		appendChild(fLeft);
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param deep
	 *            boolean
	 */
	public ICSSNode cloneNode(boolean deep) {
		RectImpl cloned = new RectImpl(this);

		return cloned;
	}

	/**
	 * This attribute is used for the bottom of the rect.
	 */
	public CSSPrimitiveValue getBottom() {
		return fBottom;
	}

	/**
	 * This attribute is used for the left of the rect.
	 */
	public CSSPrimitiveValue getLeft() {
		return fLeft;
	}

	/**
	 * @return org.w3c.dom.css.Rect
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public Rect getRectValue() throws org.w3c.dom.DOMException {
		return this;
	}

	/**
	 * This attribute is used for the right of the rect.
	 */
	public CSSPrimitiveValue getRight() {
		return fRight;
	}

	/**
	 * This attribute is used for the top of the rect.
	 */
	public CSSPrimitiveValue getTop() {
		return fTop;
	}

	/**
	 * 
	 */
	protected void initPrimitives() {

		fTop = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fTop);

		fRight = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fRight);

		fBottom = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fBottom);

		fLeft = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fLeft);
	}
}
