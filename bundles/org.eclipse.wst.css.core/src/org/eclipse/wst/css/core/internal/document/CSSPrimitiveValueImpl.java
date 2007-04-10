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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;


/**
 * 
 */
class CSSPrimitiveValueImpl extends CSSRegionContainer implements ICSSPrimitiveValue {

	protected short fPrimitiveType = CSS_UNKNOWN;
	private float fFloatValue = 0.0f;
	private String fStringValue = null;

	CSSPrimitiveValueImpl(CSSPrimitiveValueImpl that) {
		super(that);

		this.fPrimitiveType = that.fPrimitiveType;
		this.fFloatValue = that.fFloatValue;
		this.fStringValue = that.fStringValue;
	}

	CSSPrimitiveValueImpl(short primitiveType) {
		super();
		fPrimitiveType = primitiveType;
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSPrimitiveValueImpl cloned = new CSSPrimitiveValueImpl(this);

		return cloned;
	}

	/**
	 * This method is used to get the Counter value. If this CSS value doesn't
	 * contain a counter value, a <code>DOMException</code> is raised.
	 * Modification to the corresponding style property can be achieved using
	 * the <code>Counter</code> interface.
	 * 
	 * @return The Counter value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a Counter value (e.g. this is not
	 *                <code>CSS_COUNTER</code>).
	 */
	public Counter getCounterValue() throws DOMException {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getCSSValueText() {
		return getCssText();
	}

	/**
	 * A code defining the type of the value as defined above.
	 */
	public short getCssValueType() {
		if (getPrimitiveType() == CSS_INHERIT_PRIMITIVE) {
			return CSS_INHERIT;
		}
		else {
			return CSS_PRIMITIVE_VALUE;
		}
	}

	/**
	 * This method is used to get a float value in a specified unit. If this
	 * CSS value doesn't contain a float value or can't be converted into the
	 * specified unit, a <code>DOMException</code> is raised.
	 * 
	 * @param unitType
	 *            A unit code to get the float value. The unit code can only
	 *            be a float unit type (i.e. <code>CSS_NUMBER</code>,
	 *            <code>CSS_PERCENTAGE</code>,<code>CSS_EMS</code>,
	 *            <code>CSS_EXS</code>,<code>CSS_PX</code>,
	 *            <code>CSS_CM</code>,<code>CSS_MM</code>,
	 *            <code>CSS_IN</code>,<code>CSS_PT</code>,
	 *            <code>CSS_PC</code>,<code>CSS_DEG</code>,
	 *            <code>CSS_RAD</code>,<code>CSS_GRAD</code>,
	 *            <code>CSS_MS</code>,<code>CSS_S</code>,
	 *            <code>CSS_HZ</code>,<code>CSS_KHZ</code>,
	 *            <code>CSS_DIMENSION</code>).
	 * @return The float value in the specified unit.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a float value or if the float value can't be
	 *                converted into the specified unit.
	 */
	public float getFloatValue(short unitType) throws DOMException {
		switch (fPrimitiveType) {
			case CSS_NUMBER :
			case CSS_PERCENTAGE :
			case CSS_EMS :
			case CSS_EXS :
			case CSS_PX :
			case CSS_CM :
			case CSS_MM :
			case CSS_IN :
			case CSS_PT :
			case CSS_PC :
			case CSS_DEG :
			case CSS_RAD :
			case CSS_GRAD :
			case CSS_MS :
			case CSS_S :
			case CSS_HZ :
			case CSS_KHZ :
			case CSS_DIMENSION :
			case CSS_INTEGER :
			case CSS_HASH :
				return fFloatValue;
			default :
				throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * Insert the method's description here. Creation date: (2001/01/17
	 * 12:12:18)
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return PRIMITIVEVALUE_NODE;
	}

	/**
	 * The type of the value as defined by the constants specified above.
	 */
	public short getPrimitiveType() {
		return fPrimitiveType;
	}

	/**
	 * This method is used to get the Rect value. If this CSS value doesn't
	 * contain a rect value, a <code>DOMException</code> is raised.
	 * Modification to the corresponding style property can be achieved using
	 * the <code>Rect</code> interface.
	 * 
	 * @return The Rect value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a Rect value. (e.g. this is not
	 *                <code>CSS_RECT</code>).
	 */
	public Rect getRectValue() throws DOMException {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
	}

	/**
	 * This method is used to get the RGB color. If this CSS value doesn't
	 * contain a RGB color value, a <code>DOMException</code> is raised.
	 * Modification to the corresponding style property can be achieved using
	 * the <code>RGBColor</code> interface.
	 * 
	 * @return the RGB color value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the attached property
	 *                can't return a RGB color value (e.g. this is not
	 *                <code>CSS_RGBCOLOR</code>).
	 */
	public RGBColor getRGBColorValue() throws DOMException {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
	}

	/**
	 * This method is used to get the string value. If the CSS value doesn't
	 * contain a string value, a <code>DOMException</code> is raised. Some
	 * properties (like 'font-family' or 'voice-family') convert a whitespace
	 * separated list of idents to a string.
	 * 
	 * @return The string value in the current unit. The current
	 *         <code>primitiveType</code> can only be a string unit type
	 *         (i.e. <code>CSS_STRING</code>,<code>CSS_URI</code>,
	 *         <code>CSS_IDENT</code> and <code>CSS_ATTR</code>).
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a string value.
	 */
	public String getStringValue() throws DOMException {
		switch (fPrimitiveType) {
			case CSS_STRING :
			case CSS_URI :
			case CSS_IDENT :
			case CSS_ATTR :
			case CSS_URANGE :
			case CSS_FORMAT :
			case CSS_LOCAL :
			case CSS_HASH :
			case CSS_COMMA :
			case CSS_SLASH :
			case CSS_INHERIT_PRIMITIVE :
				return fStringValue;
			default :
				throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	protected void notifyValueChanged(String oldValue) {
		// for model
		ICSSDocument doc = getContainerDocument();
		if (doc == null)
			return;
		CSSModelImpl model = (CSSModelImpl) doc.getModel();
		if (model == null)
			return;
		model.valueChanged(this, oldValue);

		// for adapters
		notify(CHANGE, new Short(fPrimitiveType), null, null, getStartOffset());
	}

	/**
	 * A method to set the float value with a specified unit. If the property
	 * attached with this value can not accept the specified unit or the float
	 * value, the value will be unchanged and a <code>DOMException</code>
	 * will be raised.
	 * 
	 * @param unitType
	 *            A unit code as defined above. The unit code can only be a
	 *            float unit type (i.e. <code>CSS_NUMBER</code>,
	 *            <code>CSS_PERCENTAGE</code>,<code>CSS_EMS</code>,
	 *            <code>CSS_EXS</code>,<code>CSS_PX</code>,
	 *            <code>CSS_CM</code>,<code>CSS_MM</code>,
	 *            <code>CSS_IN</code>,<code>CSS_PT</code>,
	 *            <code>CSS_PC</code>,<code>CSS_DEG</code>,
	 *            <code>CSS_RAD</code>,<code>CSS_GRAD</code>,
	 *            <code>CSS_MS</code>,<code>CSS_S</code>,
	 *            <code>CSS_HZ</code>,<code>CSS_KHZ</code>,
	 *            <code>CSS_DIMENSION</code>).
	 * @param floatValue
	 *            The new float value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the attached property
	 *                doesn't support the float value or the unit type. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public void setFloatValue(short unitType, float floatValue) throws DOMException {
		switch (unitType) {
			case CSS_NUMBER :
			case CSS_PERCENTAGE :
			case CSS_EMS :
			case CSS_EXS :
			case CSS_PX :
			case CSS_CM :
			case CSS_MM :
			case CSS_IN :
			case CSS_PT :
			case CSS_PC :
			case CSS_DEG :
			case CSS_RAD :
			case CSS_GRAD :
			case CSS_MS :
			case CSS_S :
			case CSS_HZ :
			case CSS_KHZ :
			case CSS_DIMENSION :
			case CSS_INTEGER :
				String oldValue = getCSSValueText();
				fPrimitiveType = unitType;
				fFloatValue = floatValue;
				notifyValueChanged(oldValue);
				break;
			default :
				throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * A method to set the string value with the specified unit. If the
	 * property attached to this value can't accept the specified unit or the
	 * string value, the value will be unchanged and a
	 * <code>DOMException</code> will be raised.
	 * 
	 * @param stringType
	 *            A string code as defined above. The string code can only be
	 *            a string unit type (i.e. <code>CSS_STRING</code>,
	 *            <code>CSS_URI</code>,<code>CSS_IDENT</code>, and
	 *            <code>CSS_ATTR</code>).
	 * @param stringValue
	 *            The new string value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a string value or if the string value can't be
	 *                converted into the specified unit. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public void setStringValue(short stringType, String stringValue) throws DOMException {
		switch (stringType) {
			case CSS_STRING :
			case CSS_URI :
			case CSS_IDENT :
			case CSS_ATTR :
			case CSS_URANGE :
			case CSS_FORMAT :
			case CSS_LOCAL :
			case CSS_HASH :
			case CSS_COMMA :
			case CSS_SLASH :
			case CSS_INHERIT_PRIMITIVE :
				String oldValue = getCSSValueText();
				fPrimitiveType = stringType;
				fStringValue = stringValue;
				notifyValueChanged(oldValue);
				break;
			default :
				throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * Insert the method's description here. Creation date: (2001/01/24
	 * 15:06:25)
	 * 
	 * @param floatValue
	 *            float
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setValue(float floatValue) throws org.w3c.dom.DOMException {
		setFloatValue(getPrimitiveType(), floatValue);
	}

	/**
	 * Insert the method's description here. Creation date: (2001/01/24
	 * 15:06:25)
	 * 
	 * @param stringValue
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setValue(java.lang.String stringValue) throws org.w3c.dom.DOMException {
		setStringValue(getPrimitiveType(), stringValue);
	}
}
