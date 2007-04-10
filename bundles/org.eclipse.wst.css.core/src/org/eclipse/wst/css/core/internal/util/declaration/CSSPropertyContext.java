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
package org.eclipse.wst.css.core.internal.util.declaration;



import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMSubProperty;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.w3c.dom.css.CSSValue;


/**
 * 
 */
public class CSSPropertyContext implements ICSS2Properties {

	protected Hashtable fProperties = new Hashtable();
	HashSet fModified = null;
	private static java.util.Hashtable subPropertyAdapters;
	private static java.util.Hashtable shorthandAdapters;

	/**
	 * 
	 */
	public CSSPropertyContext() {
		super();
		initShorthandAdapters();
		initSubPropertyAdapters();
	}

	/**
	 * 
	 */
	public CSSPropertyContext(ICSSStyleDeclaration decl) {
		super();
		initShorthandAdapters();
		initSubPropertyAdapters();
		initialize(decl);
	}

	/**
	 * This function exports all property/value pairs to 'decl' declaration
	 */
	public void applyFull(ICSSStyleDeclaration decl) {
		if (decl == null)
			return;
		Enumeration keys = fProperties.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object val = fProperties.get(key);
			String value = (val instanceof ICSSValue) ? ((ICSSValue) val).getCSSValueText() : val.toString();

			if (value == null || value.length() <= 0)
				decl.removeProperty(key.toString());
			else
				decl.setProperty(key.toString(), value.trim(), (val instanceof ValueData && ((ValueData) val).important) ? "!important" : "");//$NON-NLS-2$//$NON-NLS-1$
		}
	}

	/**
	 * This function exports modified property/value pairs to 'decl'
	 * declaration
	 */
	public void applyModified(ICSSStyleDeclaration decl) {
		if (decl == null || fModified == null)
			return;
		Iterator it = fModified.iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object val = fProperties.get(key);
			String value = (val instanceof ICSSValue) ? ((ICSSValue) val).getCSSValueText() : ((val != null) ? val.toString() : null);

			if (value == null || value.length() <= 0)
				decl.removeProperty(key.toString());
			else
				decl.setProperty(key.toString(), value.trim(), (val instanceof ValueData && ((ValueData) val).important) ? "!important" : "");//$NON-NLS-2$//$NON-NLS-1$
		}
	}

	/**
	 * create clone of this context
	 */
	public Object clone() {
		CSSPropertyContext clone = new CSSPropertyContext();
		if (this.fModified != null)
			clone.fModified = (HashSet) this.fModified.clone();
		else
			clone.fModified = null;
		clone.fProperties = (Hashtable) this.fProperties.clone();
		return clone;
	}

	/**
	 * This function expands a short-hand property's value to each leaf
	 * property's value and set them to 'foreign'
	 * 
	 * For example, given [prop=border-top, value="solid 1px yellow"] will be
	 * expanded to [border-top-color=yellow, border-top-style=solid,
	 * border-top-width=1px] and they are stored to 'foreign' context.
	 * 
	 * Note that recursively shorthanded property like 'border' will be
	 * expanded to all descendant leaf properties like
	 * 'border-[top/right/bottom/left]-[color/style/width]'
	 * 
	 * @param prop
	 *            org.eclipse.wst.css.core.contentmodel.PropCMProperty
	 * @param value
	 *            java.lang.String
	 * @param foreign
	 *            org.eclipse.wst.css.core.util.declaration.CSSPropertyContext
	 */
	protected static void expandToLeaf(PropCMProperty prop, String value, CSSPropertyContext foreign) {
		// expand shorthand property
		if (value != null && value.trim().length() > 0) {
			IShorthandAdapter adapter = (IShorthandAdapter) shorthandAdapters.get(prop);
			if (adapter != null) {
				adapter.expand(value, foreign);
				foreign.set(prop.getName(), "");//$NON-NLS-1$
				for (int i = 0; i < prop.getNumChild(); i++) {
					Object obj = prop.getChildAt(i);
					if (obj instanceof PropCMProperty && !(obj instanceof PropCMSubProperty)) {
						PropCMProperty expandedProp = (PropCMProperty) obj;
						value = foreign.get(expandedProp.getName());
						expandToLeaf(expandedProp, value, foreign);
					}
				}
			}
			else if (!value.equals(foreign.get(prop.getName()))) {
				foreign.set(prop.getName(), value);
			}
		}
	}

	/**
	 * This function returns value of 'prop'. Querying value mechanism checks
	 * short-hand properties.
	 * 
	 * For example, given "background=fixed white" is set in this insatnce and
	 * param "prop=background-color", the return value will be "white".
	 * 
	 */
	public java.lang.String get(org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty prop) {
		if (prop instanceof PropCMSubProperty) {
			ISubPropertyAdapter adapter = (ISubPropertyAdapter) subPropertyAdapters.get(prop.getName());
			if (adapter != null)
				return adapter.get(this);
		}
		String str = get(prop.getName());
		if ((str == null || str.length() == 0) && prop.getShorthandContainerCount() > 0) {
			// get expanded property
			for (int i = 0; i < prop.getShorthandContainerCount(); i++) {
				PropCMProperty propParent = prop.shorthandContainerAt(i);
				String strParent = get(propParent);
				if (strParent != null && strParent.trim().length() > 0) {
					IShorthandAdapter adapter = (IShorthandAdapter) shorthandAdapters.get(propParent);
					if (adapter != null) {
						String extractedValue = adapter.extract(strParent, prop);
						return (extractedValue != null) ? extractedValue : "";//$NON-NLS-1$
					}
				}
			}
		}
		return str;
	}

	/**
	 * This function returns value of 'prop'. Querying value mechanism does
	 * not care shorthand properties.
	 */
	protected String get(String propName) {
		String str = null;
		Object obj = fProperties.get(propName);
		if (obj != null) {
			if (obj instanceof ICSSValue)
				str = ((ICSSValue) obj).getCSSValueText();
			else
				str = obj.toString();
		}
		return (str != null) ? str : "";//$NON-NLS-1$
	}

	/**
	 * See the azimuth property definition in CSS2.
	 * 
	 * @exception org.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getAzimuth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_AZIMUTH));
	}

	/**
	 * See the background property definition in CSS2.
	 * 
	 * @exception org.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackground() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG));
	}

	/**
	 * See the background-attachment property definition in CSS2.
	 * 
	 * @exception org.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackgroundAttachment() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT));
	}

	/**
	 * See the background-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackgroundColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR));
	}

	/**
	 * See the background-image property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackgroundImage() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE));
	}

	/**
	 * See the background-position property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackgroundPosition() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION));
	}

	/**
	 * 
	 */
	public java.lang.String getBackgroundPositionX() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X));
		/*
		 * String str = null; Object obj =
		 * fProperties.get(PropCMProperty.P_BG_POSITION); if (obj != null) {
		 * PropCMProperty propX =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X);
		 * PropCMProperty propY =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y);
		 * if (obj instanceof ICSSValueList) { ICSSValueList list =
		 * (ICSSValueList) obj; ICSSValue value = (ICSSValue) list.item(0); if
		 * (value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
		 * ICSSPrimitiveValue prim = (ICSSPrimitiveValue) value; if
		 * (prim.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) { // check
		 * not top or bottom if (!propX.canHave(prim.getStringValue()) &&
		 * propY.canHave(prim.getStringValue())) { // case order is vertical ->
		 * horizontal value = (ICSSValue) list.item(1); } } } str =
		 * value.getCSSValueText(); } else if (obj instanceof ICSSValue) { str =
		 * ((ICSSValue)obj).getCSSValueText(); } else str = obj.toString(); }
		 * return (str != null) ? str : "";
		 */
	}

	/**
	 * 
	 */
	public java.lang.String getBackgroundPositionY() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y));
		/*
		 * String str = null; Object obj =
		 * fProperties.get(PropCMProperty.P_BG_POSITION); if (obj != null) {
		 * PropCMProperty propX =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X);
		 * PropCMProperty propY =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y);
		 * if (obj instanceof ICSSValueList) { ICSSValueList list =
		 * (ICSSValueList) obj; int index = 1; ICSSValue value = (ICSSValue)
		 * list.item(0); if (value.getCssValueType() ==
		 * CSSValue.CSS_PRIMITIVE_VALUE) { ICSSPrimitiveValue prim =
		 * (ICSSPrimitiveValue) value; if (prim.getPrimitiveType() ==
		 * CSSPrimitiveValue.CSS_IDENT) { // check not top or bottom if
		 * (!propX.canHave(prim.getStringValue()) &&
		 * propY.canHave(prim.getStringValue())) { // case order is vertical ->
		 * horizontal index = 0; } } } str =
		 * ((ICSSValue)list.item(index)).getCSSValueText(); } else if (obj
		 * instanceof ICSSValue) { // do nothing --- value is null } else str =
		 * obj.toString(); } return (str != null) ? str : "";
		 */
	}

	/**
	 * See the background-repeat property definition in CSS2.
	 * 
	 * @exception org.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBackgroundRepeat() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT));
	}

	/**
	 * See the border property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorder() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER));
	}

	/**
	 * See the border-bottom property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderBottom() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM));
	}

	/**
	 * See the border-bottom-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderBottomColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_COLOR));
	}

	/**
	 * See the border-bottom-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderBottomStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_STYLE));
	}

	/**
	 * See the border-bottom-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderBottomWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_WIDTH));
	}

	/**
	 * See the border-collapse property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderCollapse() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLLAPSE));
	}

	/**
	 * See the border-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR));
	}

	/**
	 * See the border-left property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderLeft() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT));
	}

	/**
	 * See the border-left-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderLeftColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_COLOR));
	}

	/**
	 * See the border-left-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderLeftStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_STYLE));
	}

	/**
	 * See the border-left-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderLeftWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_WIDTH));
	}

	/**
	 * See the border-right property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderRight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT));
	}

	/**
	 * See the border-right-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderRightColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_COLOR));
	}

	/**
	 * See the border-right-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderRightStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_STYLE));
	}

	/**
	 * See the border-right-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderRightWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_WIDTH));
	}

	/**
	 * See the border-spacing property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderSpacing() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_SPACING));
	}

	/**
	 * See the border-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE));
	}

	/**
	 * See the border-top property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderTop() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP));
	}

	/**
	 * See the border-top-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderTopColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_COLOR));
	}

	/**
	 * See the border-top-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderTopStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_STYLE));
	}

	/**
	 * See the border-top-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderTopWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_WIDTH));
	}

	/**
	 * See the border-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBorderWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH));
	}

	/**
	 * See the bottom property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getBottom() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_BOTTOM));
	}

	/**
	 * See the caption-side property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCaptionSide() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CAPTION_SIDE));
	}

	/**
	 * See the clear property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getClear() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CLEAR));
	}

	/**
	 * See the clip property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getClip() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
	}

	/**
	 * 
	 */
	public String getClipBottom() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_BOTTOM));
	}

	/**
	 * 
	 */
	public String getClipLeft() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_LEFT));
	}

	/**
	 * 
	 */
	public String getClipRight() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_RIGHT));
	}

	/**
	 * 
	 */
	public String getClipTop() {
		return get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_TOP));
	}

	/**
	 * See the color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_COLOR));
	}

	/**
	 * See the content property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getContent() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CONTENT));
	}

	/**
	 * See the counter-increment property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCounterIncrement() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_INCREMENT));
	}

	/**
	 * See the counter-reset property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCounterReset() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_RESET));
	}

	/**
	 * See the float property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCssFloat() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FLOAT));
	}

	/**
	 * See the cue property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCue() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE));
	}

	/**
	 * See the cue-after property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCueAfter() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_AFTER));
	}

	/**
	 * See the cue-before property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCueBefore() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_BEFORE));
	}

	/**
	 * See the cursor property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getCursor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_CURSOR));
	}

	/**
	 * See the direction property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getDirection() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_DIRECTION));
	}

	/**
	 * See the display property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getDisplay() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_DISPLAY));
	}

	/**
	 * See the elevation property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getElevation() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_ELEVATION));
	}

	/**
	 * See the empty-cells property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getEmptyCells() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_EMPTY_CELLS));
	}

	/**
	 * See the font property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFont() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT));
	}

	/**
	 * See the font-family property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontFamily() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_FAMILY));
	}

	/**
	 * See the font-size property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontSize() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE));
	}

	/**
	 * See the font-size-adjust property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontSizeAdjust() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE_ADJUST));
	}

	/**
	 * See the font-stretch property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontStretch() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STRETCH));
	}

	/**
	 * See the font-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE));
	}

	/**
	 * See the font-variant property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontVariant() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT));
	}

	/**
	 * See the font-weight property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getFontWeight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT));
	}

	/**
	 * See the height property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getHeight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_HEIGHT));
	}

	/**
	 * See the left property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getLeft() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LEFT));
	}

	/**
	 * See the letter-spacing property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getLetterSpacing() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LETTER_SPACING));
	}

	/**
	 * See the line-height property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getLineHeight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LINE_HEIGHT));
	}

	/**
	 * See the list-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getListStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE));
	}

	/**
	 * See the list-style-image property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getListStyleImage() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE));
	}

	/**
	 * See the list-style-position property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getListStylePosition() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION));
	}

	/**
	 * See the list-style-type property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getListStyleType() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE));
	}

	/**
	 * See the margin property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMargin() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN));
	}

	/**
	 * See the margin-bottom property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarginBottom() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_BOTTOM));
	}

	/**
	 * See the margin-left property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarginLeft() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_LEFT));
	}

	/**
	 * See the margin-right property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarginRight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_RIGHT));
	}

	/**
	 * See the margin-top property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarginTop() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_TOP));
	}

	/**
	 * See the marker-offset property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarkerOffset() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARKER_OFFSET));
	}

	/**
	 * See the marks property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMarks() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MARKS));
	}

	/**
	 * See the max-height property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMaxHeight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_HEIGHT));
	}

	/**
	 * See the max-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMaxWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_WIDTH));
	}

	/**
	 * See the min-height property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMinHeight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_HEIGHT));
	}

	/**
	 * See the min-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getMinWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_WIDTH));
	}

	/**
	 * See the orphans property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOrphans() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_ORPHANS));
	}

	/**
	 * See the outline property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOutline() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE));
	}

	/**
	 * See the outline-color property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOutlineColor() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_COLOR));
	}

	/**
	 * See the outline-style property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOutlineStyle() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_STYLE));
	}

	/**
	 * See the outline-width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOutlineWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_WIDTH));
	}

	/**
	 * See the overflow property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getOverflow() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_OVERFLOW));
	}

	/**
	 * See the padding property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPadding() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING));
	}

	/**
	 * See the padding-bottom property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPaddingBottom() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_BOTTOM));
	}

	/**
	 * See the padding-left property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPaddingLeft() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_LEFT));
	}

	/**
	 * See the padding-right property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPaddingRight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_RIGHT));
	}

	/**
	 * See the padding-top property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPaddingTop() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_TOP));
	}

	/**
	 * See the page property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPage() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE));
	}

	/**
	 * See the page-break-after property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPageBreakAfter() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_AFTER));
	}

	/**
	 * See the page-break-before property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPageBreakBefore() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_BEFORE));
	}

	/**
	 * See the page-break-inside property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPageBreakInside() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_INSIDE));
	}

	/**
	 * See the pause property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPause() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE));
	}

	/**
	 * See the pause-after property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPauseAfter() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_AFTER));
	}

	/**
	 * See the pause-before property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPauseBefore() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_BEFORE));
	}

	/**
	 * See the pitch property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPitch() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH));
	}

	/**
	 * See the pitch-range property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPitchRange() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH_RANGE));
	}

	/**
	 * See the play-during property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPlayDuring() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_PLAY_DURING));
	}

	/**
	 * See the position property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getPosition() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_POSITION));
	}

	/**
	 * See the quotes property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getQuotes() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_QUOTES));
	}

	/**
	 * See the richness property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getRichness() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_RICHNESS));
	}

	/**
	 * See the right property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getRight() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_RIGHT));
	}

	/**
	 * @return org.eclipse.wst.css.core.util.declaration.IShorthandAdapter
	 * @param org.eclipse.wst.css.core.contentmodel.PropCMProperty
	 */
	public static IShorthandAdapter getShorthandAdapter(PropCMProperty prop) {
		return (IShorthandAdapter) shorthandAdapters.get(prop);
	}

	/**
	 * See the size property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSize() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SIZE));
	}

	/**
	 * See the speak property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSpeak() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK));
	}

	/**
	 * See the speak-header property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSpeakHeader() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_HEADER));
	}

	/**
	 * See the speak-numeral property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSpeakNumeral() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_NUMERAL));
	}

	/**
	 * See the speak-punctuation property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSpeakPunctuation() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_PUNCTUATION));
	}

	/**
	 * See the speech-rate property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getSpeechRate() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEECH_RATE));
	}

	/**
	 * See the stress property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getStress() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_STRESS));
	}

	/**
	 * See the table-layout property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTableLayout() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TABLE_LAYOUT));
	}

	/**
	 * See the text-align property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTextAlign() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_ALIGN));
	}

	/**
	 * See the text-decoration property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTextDecoration() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_DECORATION));
	}

	/**
	 * See the text-indent property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTextIndent() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_INDENT));
	}

	/**
	 * See the text-shadow property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTextShadow() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_SHADOW));
	}

	/**
	 * See the text-transform property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTextTransform() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_TRANSFORM));
	}

	/**
	 * See the top property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getTop() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_TOP));
	}

	/**
	 * See the unicode-bidi property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getUnicodeBidi() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_UNICODE_BIDI));
	}

	/**
	 * See the vertical-align property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getVerticalAlign() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_VERTICAL_ALIGN));
	}

	/**
	 * See the visibility property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getVisibility() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_VISIBILITY));
	}

	/**
	 * See the voice-family property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getVoiceFamily() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_VOICE_FAMILY));
	}

	/**
	 * See the volume property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getVolume() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_VOLUME));
	}

	/**
	 * See the white-space property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getWhiteSpace() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_WHITE_SPACE));
	}

	/**
	 * See the widows property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getWidows() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_WIDOWS));
	}

	/**
	 * See the width property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getWidth() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_WIDTH));
	}

	/**
	 * See the word-spacing property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getWordSpacing() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_WORD_SPACING));
	}

	/**
	 * See the z-index property definition in CSS2.
	 * 
	 * @exceptionorg.w3c.dom.DOMException
	 *                SYNTAX_ERR: Raised if the new value has a syntax error
	 *                and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this property is
	 *                readonly.
	 */
	public String getZIndex() {
		return get(PropCMProperty.getInstanceOf(PropCMProperty.P_Z_INDEX));
	}

	/**
	 * 
	 */
	public void initialize(ICSSStyleDeclaration decl) {
		fProperties.clear();
		if (fModified != null)
			fModified.clear();
		if (decl == null)
			return;

		int nProperties = decl.getLength();
		for (int i = 0; i < nProperties; i++) {
			String propName = decl.item(i);
			if (propName != null) {
				String propN = propName.trim().toLowerCase();
				if (propN.length() != 0) {
					CSSValue val = decl.getPropertyCSSValue(propName);
					if (val != null)
						fProperties.put(propN, val);
				}
			}
		}
	}

	/**
	 * 
	 */
	static void initShorthandAdapters() {
		if (shorthandAdapters == null) {
			shorthandAdapters = new Hashtable();
			// register
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BG), new BackgroundShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER), new BorderShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR), new BorderColorShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE), new BorderStyleShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH), new BorderWidthShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP), new BorderTopShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT), new BorderRightShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM), new BorderBottomShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT), new BorderLeftShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT), new FontShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE), new ListStyleShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN), new MarginShorthandAdapter());
			shorthandAdapters.put(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING), new PaddingShorthandAdapter());

		}
	}

	/**
	 * 
	 */
	static void initSubPropertyAdapters() {
		if (subPropertyAdapters == null) {
			subPropertyAdapters = new Hashtable();

			// register
			subPropertyAdapters.put(PropCMSubProperty.PSUB_BG_POSITION_X, new BackgroundPositionXSubStyleAdapter());
			subPropertyAdapters.put(PropCMSubProperty.PSUB_BG_POSITION_Y, new BackgroundPositionYSubStyleAdapter());
			subPropertyAdapters.put(PropCMSubProperty.PSUB_CLIP_TOP, new ClipTopSubStyleAdapter());
			subPropertyAdapters.put(PropCMSubProperty.PSUB_CLIP_RIGHT, new ClipRightSubStyleAdapter());
			subPropertyAdapters.put(PropCMSubProperty.PSUB_CLIP_BOTTOM, new ClipBottomSubStyleAdapter());
			subPropertyAdapters.put(PropCMSubProperty.PSUB_CLIP_LEFT, new ClipLeftSubStyleAdapter());
		}
	}

	/**
	 * 
	 */
	public boolean isModified() {
		return (fModified != null && fModified.size() != 0);
	}

	/**
	 * 
	 */
	public Enumeration properties() {
		return fProperties.keys();
	}

	/**
	 * 
	 */
	public Iterator propertiesModified() {
		if (fModified != null) {
			return ((Collection) fModified.clone()).iterator();
		}
		else
			return new Iterator() {
				public boolean hasNext() {
					return false;
				}

				public Object next() {
					return null;
				}

				public void remove() {
				}
			};
	}

	/**
	 * This function expands the value of shorthand 'prop' to 'foreign'
	 * context. Note that if this has parent shorthand properties of 'prop',
	 * they are all expanded to 'foreign' context.
	 */
	protected void recursiveExtract(PropCMProperty prop, CSSPropertyContext foreign) {
		// expand shorthand property
		for (int i = 0; i < prop.getShorthandContainerCount(); i++) {
			recursiveExtract(prop.shorthandContainerAt(i), foreign);
		}
		String str = get(prop.getName());
		if (str == null || str.trim().length() == 0)
			str = foreign.get(prop.getName());
		if (str != null && str.trim().length() > 0) {
			IShorthandAdapter adapter = (IShorthandAdapter) shorthandAdapters.get(prop);
			if (adapter != null) {
				adapter.expand(str, foreign);
				foreign.set(prop.getName(), "");//$NON-NLS-1$
			}
		}
	}

	/**
	 * 
	 */
	protected void removeDescendants(PropCMProperty prop) {
		if (prop.isShorthand()) {
			// remove properties
			int n = prop.getNumChild();
			for (int i = 0; i < n; i++) {
				Object obj = prop.getChildAt(i);
				if (obj instanceof PropCMProperty) {
					removeDescendants((PropCMProperty) obj);

					String str = get(obj.toString());
					if (str != null && str.length() > 0)
						set(obj.toString(), "");//$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * 
	 */
	public void resetModified() {
		if (fModified != null)
			fModified.clear();
	}

	/**
	 * This function sets the pair of 'prop'/'value'. If shorthand properties
	 * related to 'prop' are already defined, they will be expanded to avoid
	 * property confliction. If descendant properties of 'prop' are already
	 * defined, they will be removed to avoid property confliction.
	 * 
	 */
	public void set(org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty prop, java.lang.String value) throws org.w3c.dom.DOMException {
		if (prop instanceof PropCMSubProperty) {
			ISubPropertyAdapter adapter = (ISubPropertyAdapter) subPropertyAdapters.get(prop.getName());
			if (adapter != null) {
				adapter.set(this, value);
				return;
			}
		}
		if (prop.getShorthandContainerCount() > 0) {
			// expand shorthand property
			CSSPropertyContext context = new CSSPropertyContext();
			for (int i = 0; i < prop.getShorthandContainerCount(); i++) {
				recursiveExtract(prop.shorthandContainerAt(i), context);
			}
			Enumeration properties = context.properties();
			while (properties.hasMoreElements()) {
				String propForeign = properties.nextElement().toString();
				set(propForeign, context.get(propForeign));
			}
		}

		removeDescendants(prop);

		set(prop.getName(), value);
	}

	/**
	 * This function sets the pair of 'propName'/'value' regardless of its
	 * shorthand properties.
	 */
	protected void set(String propName, String value) throws org.w3c.dom.DOMException {
		String key = propName;
		if (value == null)
			fProperties.remove(key);
		else
			fProperties.put(key, value);
		if (fModified == null)
			fModified = new HashSet();
		fModified.add(key);
	}

	/**
	 * 
	 */
	public void setAzimuth(String azimuth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_AZIMUTH), azimuth);
	}

	/**
	 * 
	 */
	public void setBackground(String background) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG), background);
	}

	/**
	 * 
	 */
	public void setBackgroundAttachment(String backgroundAttachment) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT), backgroundAttachment);
	}

	/**
	 * 
	 */
	public void setBackgroundColor(String backgroundColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR), backgroundColor);
	}

	/**
	 * 
	 */
	public void setBackgroundImage(String backgroundImage) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE), backgroundImage);
	}

	/**
	 * 
	 */
	public void setBackgroundPosition(String backgroundPosition) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION), backgroundPosition);
	}

	/**
	 * 
	 */
	public void setBackgroundPositionX(java.lang.String backgroundPositionX) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X), backgroundPositionX);
		/*
		 * String newValue = null; String valH = backgroundPositionX; String
		 * valV = getBackgroundPositionY(); if (valV == null ||
		 * valV.length()== 0) newValue = valH; else if (valH == null ||
		 * valH.length() == 0) { Collection valX =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X).getValues();
		 * Collection valY =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y).getValues();
		 * PropCMUtil.minus(valY,valX); Iterator it = valY.iterator();
		 * while(it.hasNext()) { Object obj = it.next(); if
		 * (obj.toString().equals(valV.toLowerCase())) { // need not
		 * compensate for ... newValue = valV; break; } } // compensate for
		 * Horizontal value if (newValue == null) newValue = "0% " + valV; }
		 * else newValue = valH + " " + valV; setBackgroundPosition(newValue);
		 */
	}

	/**
	 * 
	 */
	public void setBackgroundPositionY(java.lang.String backgroundPositionY) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y), backgroundPositionY);
		/*
		 * String newValue = null; String valH = getBackgroundPositionX();
		 * String valV = backgroundPositionY; if (valV == null ||
		 * valV.length()== 0) newValue = valH; else if (valH == null ||
		 * valH.length() == 0) { Collection valX =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X).getValues();
		 * Collection valY =
		 * PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y).getValues();
		 * PropCMUtil.minus(valY,valX); Iterator it = valY.iterator();
		 * while(it.hasNext()) { Object obj = it.next(); if
		 * (obj.toString().equals(valV.toLowerCase())) { // need not
		 * compensate for ... newValue = valV; break; } } // compensate for
		 * Horizontal value if (newValue == null) newValue = "0% " + valV; }
		 * else newValue = valH + " " + valV; setBackgroundPosition(newValue);
		 */
	}

	/**
	 * 
	 */
	public void setBackgroundRepeat(String backgroundRepeat) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT), backgroundRepeat);
	}

	/**
	 * 
	 */
	public void setBorder(String border) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER), border);
	}

	/**
	 * 
	 */
	public void setBorderBottom(String borderBottom) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM), borderBottom);
	}

	/**
	 * 
	 */
	public void setBorderBottomColor(String borderBottomColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_COLOR), borderBottomColor);
	}

	/**
	 * 
	 */
	public void setBorderBottomStyle(String borderBottomStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_STYLE), borderBottomStyle);
	}

	/**
	 * 
	 */
	public void setBorderBottomWidth(String borderBottomWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_WIDTH), borderBottomWidth);
	}

	/**
	 * 
	 */
	public void setBorderCollapse(String borderCollapse) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLLAPSE), borderCollapse);
	}

	/**
	 * 
	 */
	public void setBorderColor(String borderColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR), borderColor);
	}

	/**
	 * 
	 */
	public void setBorderLeft(String borderLeft) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT), borderLeft);
	}

	/**
	 * 
	 */
	public void setBorderLeftColor(String borderLeftColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_COLOR), borderLeftColor);
	}

	/**
	 * 
	 */
	public void setBorderLeftStyle(String borderLeftStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_STYLE), borderLeftStyle);
	}

	/**
	 * 
	 */
	public void setBorderLeftWidth(String borderLeftWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_WIDTH), borderLeftWidth);
	}

	/**
	 * 
	 */
	public void setBorderRight(String borderRight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT), borderRight);
	}

	/**
	 * 
	 */
	public void setBorderRightColor(String borderRightColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_COLOR), borderRightColor);
	}

	/**
	 * 
	 */
	public void setBorderRightStyle(String borderRightStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_STYLE), borderRightStyle);
	}

	/**
	 * 
	 */
	public void setBorderRightWidth(String borderRightWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_WIDTH), borderRightWidth);
	}

	/**
	 * 
	 */
	public void setBorderSpacing(String borderSpacing) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_SPACING), borderSpacing);
	}

	/**
	 * 
	 */
	public void setBorderStyle(String borderStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE), borderStyle);
	}

	/**
	 * 
	 */
	public void setBorderTop(String borderTop) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP), borderTop);
	}

	/**
	 * 
	 */
	public void setBorderTopColor(String borderTopColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_COLOR), borderTopColor);
	}

	/**
	 * 
	 */
	public void setBorderTopStyle(String borderTopStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_STYLE), borderTopStyle);
	}

	/**
	 * 
	 */
	public void setBorderTopWidth(String borderTopWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_WIDTH), borderTopWidth);
	}

	/**
	 * 
	 */
	public void setBorderWidth(String borderWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH), borderWidth);
	}

	/**
	 * 
	 */
	public void setBottom(String bottom) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_BOTTOM), bottom);
	}

	/**
	 * 
	 */
	public void setCaptionSide(String captionSide) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CAPTION_SIDE), captionSide);
	}

	/**
	 * 
	 */
	public void setClear(String clear) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CLEAR), clear);
	}

	/**
	 * 
	 */
	public void setClip(String clip) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP), clip);
	}

	/**
	 * 
	 */
	public void setClipBottom(java.lang.String clip) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_BOTTOM), clip);
	}

	/**
	 * 
	 */
	public void setClipLeft(java.lang.String clip) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_LEFT), clip);
	}

	/**
	 * 
	 */
	public void setClipRight(java.lang.String clip) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_RIGHT), clip);
	}

	/**
	 * 
	 */
	public void setClipTop(java.lang.String clip) throws org.w3c.dom.DOMException {
		set(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_TOP), clip);
	}

	/**
	 * 
	 */
	public void setColor(String color) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_COLOR), color);
	}

	/**
	 * 
	 */
	public void setContent(String content) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CONTENT), content);
	}

	/**
	 * 
	 */
	public void setCounterIncrement(String counterIncrement) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_INCREMENT), counterIncrement);
	}

	/**
	 * 
	 */
	public void setCounterReset(String counterReset) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_RESET), counterReset);
	}

	/**
	 * 
	 */
	public void setCssFloat(String cssFloat) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FLOAT), cssFloat);
	}

	/**
	 * 
	 */
	public void setCue(String cue) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE), cue);
	}

	/**
	 * 
	 */
	public void setCueAfter(String cueAfter) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_AFTER), cueAfter);
	}

	/**
	 * 
	 */
	public void setCueBefore(String cueBefore) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_BEFORE), cueBefore);
	}

	/**
	 * 
	 */
	public void setCursor(String cursor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_CURSOR), cursor);
	}

	/**
	 * 
	 */
	public void setDirection(String direction) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_DIRECTION), direction);
	}

	/**
	 * 
	 */
	public void setDisplay(String display) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_DISPLAY), display);
	}

	/**
	 * 
	 */
	public void setElevation(String elevation) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_ELEVATION), elevation);
	}

	/**
	 * 
	 */
	public void setEmptyCells(String emptyCells) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_EMPTY_CELLS), emptyCells);
	}

	/**
	 * 
	 */
	public void setFont(String font) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT), font);
	}

	/**
	 * 
	 */
	public void setFontFamily(String fontFamily) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_FAMILY), fontFamily);
	}

	/**
	 * 
	 */
	public void setFontSize(String fontSize) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE), fontSize);
	}

	/**
	 * 
	 */
	public void setFontSizeAdjust(String fontSizeAdjust) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE_ADJUST), fontSizeAdjust);
	}

	/**
	 * 
	 */
	public void setFontStretch(String fontStretch) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STRETCH), fontStretch);
	}

	/**
	 * 
	 */
	public void setFontStyle(String fontStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE), fontStyle);
	}

	/**
	 * 
	 */
	public void setFontVariant(String fontVariant) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT), fontVariant);
	}

	/**
	 * 
	 */
	public void setFontWeight(String fontWeight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT), fontWeight);
	}

	/**
	 * 
	 */
	public void setHeight(String height) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_HEIGHT), height);
	}

	/**
	 * 
	 */
	public void setLeft(String left) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LEFT), left);
	}

	/**
	 * 
	 */
	public void setLetterSpacing(String letterSpacing) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LETTER_SPACING), letterSpacing);
	}

	/**
	 * 
	 */
	public void setLineHeight(String lineHeight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LINE_HEIGHT), lineHeight);
	}

	/**
	 * 
	 */
	public void setListStyle(String listStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE), listStyle);
	}

	/**
	 * 
	 */
	public void setListStyleImage(String listStyleImage) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE), listStyleImage);
	}

	/**
	 * 
	 */
	public void setListStylePosition(String listStylePosition) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION), listStylePosition);
	}

	/**
	 * 
	 */
	public void setListStyleType(String listStyleType) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE), listStyleType);
	}

	/**
	 * 
	 */
	public void setMargin(String margin) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN), margin);
	}

	/**
	 * 
	 */
	public void setMarginBottom(String marginBottom) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_BOTTOM), marginBottom);
	}

	/**
	 * 
	 */
	public void setMarginLeft(String marginLeft) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_LEFT), marginLeft);
	}

	/**
	 * 
	 */
	public void setMarginRight(String marginRight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_RIGHT), marginRight);
	}

	/**
	 * 
	 */
	public void setMarginTop(String marginTop) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_TOP), marginTop);
	}

	/**
	 * 
	 */
	public void setMarkerOffset(String markerOffset) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARKER_OFFSET), markerOffset);
	}

	/**
	 * 
	 */
	public void setMarks(String marks) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MARKS), marks);
	}

	/**
	 * 
	 */
	public void setMaxHeight(String maxHeight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_HEIGHT), maxHeight);
	}

	/**
	 * 
	 */
	public void setMaxWidth(String maxWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_WIDTH), maxWidth);
	}

	/**
	 * 
	 */
	public void setMinHeight(String minHeight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_HEIGHT), minHeight);
	}

	/**
	 * 
	 */
	public void setMinWidth(String minWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_WIDTH), minWidth);
	}

	/**
	 * 
	 */
	public void setOrphans(String orphans) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_ORPHANS), orphans);
	}

	/**
	 * 
	 */
	public void setOutline(String outline) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE), outline);
	}

	/**
	 * 
	 */
	public void setOutlineColor(String outlineColor) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_COLOR), outlineColor);
	}

	/**
	 * 
	 */
	public void setOutlineStyle(String outlineStyle) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_STYLE), outlineStyle);
	}

	/**
	 * 
	 */
	public void setOutlineWidth(String outlineWidth) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_WIDTH), outlineWidth);
	}

	/**
	 * 
	 */
	public void setOverflow(String overflow) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_OVERFLOW), overflow);
	}

	/**
	 * 
	 */
	public void setPadding(String padding) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING), padding);
	}

	/**
	 * 
	 */
	public void setPaddingBottom(String paddingBottom) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_BOTTOM), paddingBottom);
	}

	/**
	 * 
	 */
	public void setPaddingLeft(String paddingLeft) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_LEFT), paddingLeft);
	}

	/**
	 * 
	 */
	public void setPaddingRight(String paddingRight) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_RIGHT), paddingRight);
	}

	/**
	 * 
	 */
	public void setPaddingTop(String paddingTop) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_TOP), paddingTop);
	}

	/**
	 * 
	 */
	public void setPage(String page) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE), page);
	}

	/**
	 * 
	 */
	public void setPageBreakAfter(String pageBreakAfter) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_AFTER), pageBreakAfter);
	}

	/**
	 * 
	 */
	public void setPageBreakBefore(String pageBreakBefore) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_BEFORE), pageBreakBefore);
	}

	/**
	 * 
	 */
	public void setPageBreakInside(String pageBreakInside) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_INSIDE), pageBreakInside);
	}

	/**
	 * 
	 */
	public void setPause(String pause) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE), pause);
	}

	/**
	 * 
	 */
	public void setPauseAfter(String pauseAfter) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_AFTER), pauseAfter);
	}

	/**
	 * 
	 */
	public void setPauseBefore(String pauseBefore) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_BEFORE), pauseBefore);
	}

	/**
	 * 
	 */
	public void setPitch(String pitch) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH), pitch);
	}

	/**
	 * 
	 */
	public void setPitchRange(String pitchRange) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH_RANGE), pitchRange);
	}

	/**
	 * 
	 */
	public void setPlayDuring(String playDuring) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_PLAY_DURING), playDuring);
	}

	/**
	 * 
	 */
	public void setPosition(String position) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_POSITION), position);
	}

	/**
	 * 
	 */
	public void setQuotes(String quotes) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_QUOTES), quotes);
	}

	/**
	 * 
	 */
	public void setRichness(String richness) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_RICHNESS), richness);
	}

	/**
	 * 
	 */
	public void setRight(String right) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_RIGHT), right);
	}

	/**
	 * 
	 */
	public void setSize(String size) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SIZE), size);
	}

	/**
	 * 
	 */
	public void setSpeak(String speak) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK), speak);
	}

	/**
	 * 
	 */
	public void setSpeakHeader(String speakHeader) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_HEADER), speakHeader);
	}

	/**
	 * 
	 */
	public void setSpeakNumeral(String speakNumeral) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_NUMERAL), speakNumeral);
	}

	/**
	 * 
	 */
	public void setSpeakPunctuation(String speakPunctuation) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_PUNCTUATION), speakPunctuation);
	}

	/**
	 * 
	 */
	public void setSpeechRate(String speechRate) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_SPEECH_RATE), speechRate);
	}

	/**
	 * 
	 */
	public void setStress(String stress) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_STRESS), stress);
	}

	/**
	 * 
	 */
	public void setTableLayout(String tableLayout) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TABLE_LAYOUT), tableLayout);
	}

	/**
	 * 
	 */
	public void setTextAlign(String textAlign) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_ALIGN), textAlign);
	}

	/**
	 * 
	 */
	public void setTextDecoration(String textDecoration) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_DECORATION), textDecoration);
	}

	/**
	 * 
	 */
	public void setTextIndent(String textIndent) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_INDENT), textIndent);
	}

	/**
	 * 
	 */
	public void setTextShadow(String textShadow) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_SHADOW), textShadow);
	}

	/**
	 * 
	 */
	public void setTextTransform(String textTransform) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_TRANSFORM), textTransform);
	}

	/**
	 * 
	 */
	public void setTop(String top) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_TOP), top);
	}

	/**
	 * 
	 */
	public void setUnicodeBidi(String unicodeBidi) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_UNICODE_BIDI), unicodeBidi);
	}

	/**
	 * 
	 */
	public void setVerticalAlign(String verticalAlign) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_VERTICAL_ALIGN), verticalAlign);
	}

	/**
	 * 
	 */
	public void setVisibility(String visibility) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_VISIBILITY), visibility);
	}

	/**
	 * 
	 */
	public void setVoiceFamily(String voiceFamily) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_VOICE_FAMILY), voiceFamily);
	}

	/**
	 * 
	 */
	public void setVolume(String volume) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_VOLUME), volume);
	}

	/**
	 * 
	 */
	public void setWhiteSpace(String whiteSpace) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_WHITE_SPACE), whiteSpace);
	}

	/**
	 * 
	 */
	public void setWidows(String widows) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_WIDOWS), widows);
	}

	/**
	 * 
	 */
	public void setWidth(String width) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_WIDTH), width);
	}

	/**
	 * 
	 */
	public void setWordSpacing(String wordSpacing) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_WORD_SPACING), wordSpacing);
	}

	/**
	 * 
	 */
	public void setZIndex(String zIndex) throws org.w3c.dom.DOMException {
		set(PropCMProperty.getInstanceOf(PropCMProperty.P_Z_INDEX), zIndex);
	}
}
