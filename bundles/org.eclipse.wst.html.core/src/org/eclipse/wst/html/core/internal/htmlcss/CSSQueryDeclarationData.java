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
package org.eclipse.wst.html.core.internal.htmlcss;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;



/**
 */
public class CSSQueryDeclarationData extends CSSQueryValueData {


	ICSSStyleDeclItem declItem;

	public CSSQueryDeclarationData(ICSSStyleDeclItem declItem, boolean imp, int specificity) {
		super(null, imp, specificity);
		this.declItem = declItem;
	}

	/**
	 */
	ICSSStyleDeclItem getDeclItem() {
		return declItem;
	}

	/**
	 */
	public String toString() {
		if (value == null && declItem != null) {
			value = declItem.getCSSValueText();
		}
		return value;
	}

}
