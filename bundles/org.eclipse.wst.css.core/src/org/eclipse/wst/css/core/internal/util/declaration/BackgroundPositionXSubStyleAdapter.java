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
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.contentmodel.IValID;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMSubProperty;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMUtil;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValueList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;


/**
 * for horizontal value of 'background-position'
 */
public class BackgroundPositionXSubStyleAdapter implements ISubPropertyAdapter {

	/**
	 * 
	 */
	public BackgroundPositionXSubStyleAdapter() {
		super();
	}

	/**
	 * 
	 */
	protected CSSTextToken[] correctMeaningToken(CSSTextToken[] src) {
		java.util.ArrayList list = new java.util.ArrayList();
		for (int i = 0; i < src.length; i++) {
			if (src[i].kind != CSSRegionContexts.CSS_S && src[i].kind != CSSRegionContexts.CSS_DECLARATION_VALUE_S && src[i].kind != CSSRegionContexts.CSS_COMMENT)
				list.add(src[i]);
		}
		CSSTextToken[] ret = new CSSTextToken[list.size()];
		list.toArray(ret);
		return ret;
	}

	/**
	 * 
	 */
	public String get(ICSS2Properties properties) {
		String str = null;
		Object obj = properties.get(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION));
		if (obj != null) {
			PropCMProperty propX = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X);
			PropCMProperty propY = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y);
			if (obj instanceof ICSSValueList) {
				ICSSValueList list = (ICSSValueList) obj;
				ICSSValue value = (ICSSValue) list.item(0);
				if (value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
					ICSSPrimitiveValue prim = (ICSSPrimitiveValue) value;
					if (prim.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
						// check not top or bottom
						if (!propX.canHave(prim.getStringValue()) && propY.canHave(prim.getStringValue())) {
							// case order is vertical -> horizontal
							value = (ICSSValue) list.item(1);
						}
					}
				}
				str = value.getCSSValueText();
			}
			else if (obj instanceof ICSSValue) {
				str = ((ICSSValue) obj).getCSSValueText();
				if (str.equalsIgnoreCase(IValID.V_BOTTOM) || str.equalsIgnoreCase(IValID.V_TOP))
					str = "";//$NON-NLS-1$
			}
			else {
				str = obj.toString();
				CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_DECLARATION_VALUE, str);
				CSSTextToken[] tokens = parser.getTokens();

				tokens = correctMeaningToken(tokens);
				if (tokens.length == 0)
					str = "";//$NON-NLS-1$
				else if (tokens.length == 1 && (tokens[0].image.equalsIgnoreCase(IValID.V_BOTTOM) || tokens[0].image.equalsIgnoreCase(IValID.V_TOP)))
					str = "";//$NON-NLS-1$
				else
					str = tokens[0].image;
			}
		}
		return (str != null) ? str : "";//$NON-NLS-1$
	}

	/**
	 * 
	 */
	public void set(ICSS2Properties properties, String value) throws org.w3c.dom.DOMException {
		String newValue = null;
		String valH = value;
		String valV = properties.getBackgroundPositionY();
		if (valV == null || valV.length() == 0)
			newValue = valH;
		else if (valH == null || valH.length() == 0) {
			Collection valX = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X).getValues();
			Collection valY = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y).getValues();
			PropCMUtil.minus(valY, valX);
			Iterator it = valY.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj.toString().equals(valV.toLowerCase())) {
					// need not compensate for ...
					newValue = valV;
					break;
				}
			}
			// compensate for Horizontal value
			if (newValue == null) {
				// check valV is length or not
				CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_DECLARATION_VALUE, valV.trim());
				CSSTextToken[] tokens = parser.getTokens();
				if (tokens != null && tokens.length > 0 && tokens[0].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
					newValue = IValID.V_LEFT + " " + valV;//$NON-NLS-1$
				}
				else
					newValue = "0% " + valV;//$NON-NLS-1$
			}
		}
		else
			newValue = valH + " " + valV;//$NON-NLS-1$
		properties.setBackgroundPosition(newValue);
	}
}
