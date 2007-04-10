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



import org.eclipse.wst.css.core.internal.contentmodel.IValID;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.w3c.dom.css.Rect;


/**
 * Abstract class for clip sub-properties
 */
abstract public class ClipSubStyleAdapter implements ISubPropertyAdapter {

	/**
	 * 
	 */
	public ClipSubStyleAdapter() {
		super();
	}

	/**
	 * 
	 */
	public String get(ICSS2Properties properties) {
		String str = null;
		Object obj = properties.get(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
		if (obj != null) {
			if (obj instanceof org.w3c.dom.css.Rect) {
				str = get((org.w3c.dom.css.Rect) obj);
			}
			else {
				if (obj instanceof ICSSValue) {
					str = ((ICSSValue) obj).getCSSValueText();
				}
				else {
					str = obj.toString();
				}
				str = str.trim().toLowerCase();
				int pos = str.indexOf("rect(");//$NON-NLS-1$
				if (pos >= 0) {
					String subStr = null;
					pos += 5;
					int i = 0;
					do {
						int posEnd = str.indexOf(",", pos);//$NON-NLS-1$
						if (posEnd < 0)
							posEnd = str.indexOf(")", pos);//$NON-NLS-1$
						if (posEnd < 0 && pos < str.length())
							posEnd = str.length();
						if (posEnd >= pos) {
							subStr = str.substring(pos, posEnd);
							pos = posEnd + 1;
						}
						else
							pos = -1;
					}
					while (i++ < index() && pos > 0);

					if (pos > 0)
						str = subStr.trim();
					else
						str = null;
				}
				else
					str = null;
			}
		}
		return (str != null) ? str : "";//$NON-NLS-1$
	}

	/**
	 * 
	 */
	abstract String get(Rect rect);

	/**
	 * 
	 */
	abstract int index();

	/**
	 * 
	 */
	public void set(ICSS2Properties properties, String value) throws org.w3c.dom.DOMException {
	}

	/**
	 * Insert the method's description here. Creation date: (2001/10/04
	 * 19:25:46)
	 * 
	 * @param properties
	 *            org.eclipse.wst.css.core.util.declaration.ICSS2Properties
	 * @param top
	 *            java.lang.String
	 * @param right
	 *            java.lang.String
	 * @param bottom
	 *            java.lang.String
	 * @param left
	 *            java.lang.String
	 * @param removeCheck
	 *            boolean
	 */
	void set(ICSS2Properties properties, String top, String right, String bottom, String left, boolean removeCheck) {
		if (top == null || top.length() == 0)
			top = IValID.V_AUTO;
		if (right == null || right.length() == 0)
			right = IValID.V_AUTO;
		if (bottom == null || bottom.length() == 0)
			bottom = IValID.V_AUTO;
		if (left == null || left.length() == 0)
			left = IValID.V_AUTO;

		if (removeCheck && top.trim().equalsIgnoreCase(IValID.V_AUTO) && right.trim().equalsIgnoreCase(IValID.V_AUTO) && bottom.trim().equalsIgnoreCase(IValID.V_AUTO) && left.trim().equalsIgnoreCase(IValID.V_AUTO)) {
			properties.set(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP), null);
		}
		else
			properties.set(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP), "rect(" + top + ", " + right + ", " + bottom + ", " + left + ")");//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}
}
