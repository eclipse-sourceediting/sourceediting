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
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.util.HashMap;
import java.util.Map;


class CSSMMSelectorExpressionImpl extends CSSMMSelectorImpl {

	public CSSMMSelectorExpressionImpl() {
		super();
	}

	/*
	 * @see CSSMMSelector#getSelectorType()
	 */
	public String getSelectorType() {
		return TYPE_EXPRESSION;
	}

	void postSetAttribute() throws IllegalArgumentException {
		super.postSetAttribute();
		String name = getName();
		if (name == null) {
			throw new IllegalArgumentException();
		}

		Map map = new HashMap();
		map.put(EXPRESSION_ADJACENT.toLowerCase(), "+"); //$NON-NLS-1$
		map.put(EXPRESSION_ATTRIBUTE.toLowerCase(), "[]"); //$NON-NLS-1$
		map.put(EXPRESSION_CHILD.toLowerCase(), ">"); //$NON-NLS-1$
		map.put(EXPRESSION_DESCENDANT.toLowerCase(), "' '"); //$NON-NLS-1$
		map.put(EXPRESSION_UNIVERSAL.toLowerCase(), "*"); //$NON-NLS-1$

		fValue = (String) map.get(name.toLowerCase());
		if (fValue == null) {
			throw new IllegalArgumentException();
		}
	}
}
