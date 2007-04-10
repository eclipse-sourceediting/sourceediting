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
import org.w3c.dom.css.RGBColor;


/**
 * 
 */
class RGBColorImpl extends CSSPrimitiveContainer implements RGBColor {

	private CSSPrimitiveValueImpl fRed;
	private CSSPrimitiveValueImpl fGreen;
	private CSSPrimitiveValueImpl fBlue;

	/**
	 * 
	 */
	RGBColorImpl() {
		super(CSS_RGBCOLOR);
	}

	RGBColorImpl(RGBColorImpl that) {
		super(that);

		fRed = new CSSPrimitiveValueImpl(that.fRed);
		appendChild(fRed);

		fGreen = new CSSPrimitiveValueImpl(that.fGreen);
		appendChild(fGreen);

		fBlue = new CSSPrimitiveValueImpl(that.fBlue);
		appendChild(fBlue);
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param deep
	 *            boolean
	 */
	public ICSSNode cloneNode(boolean deep) {
		RGBColorImpl cloned = new RGBColorImpl(this);

		return cloned;
	}

	/**
	 * This attribute is used for the blue value of the RGB color.
	 */
	public CSSPrimitiveValue getBlue() {
		return fBlue;
	}

	/**
	 * This attribute is used for the green value of the RGB color.
	 */
	public CSSPrimitiveValue getGreen() {
		return fGreen;
	}

	/**
	 * This attribute is used for the red value of the RGB color.
	 */
	public CSSPrimitiveValue getRed() {
		return fRed;
	}

	/**
	 * @return org.w3c.dom.css.RGBColor
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public RGBColor getRGBColorValue() throws org.w3c.dom.DOMException {
		return this;
	}

	/**
	 * 
	 */
	protected void initPrimitives() {

		fRed = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fRed);

		fGreen = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fGreen);

		fBlue = (CSSPrimitiveValueImpl) getOwnerDocument().createCSSPrimitiveValue(CSS_NUMBER);
		appendChild(fBlue);
	}
}
