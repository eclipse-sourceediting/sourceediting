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

import org.eclipse.wst.css.core.internal.metamodel.CSSMMFunction;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;

class CSSMMFunctionImpl extends CSSMMNodeImpl implements CSSMMFunction {

	public CSSMMFunctionImpl() {
		super();
	}

	public String getType() {
		return TYPE_FUNCTION;
	}

	public String getName() {
		String name = getAttribute(ATTR_NAME);
		if (name != null) {
			return name.toLowerCase();
		}
		else {
			return null;
		}
	}

	public String getFunctionString() {
		return fValue;
	}

	/*
	 * @see CSSMMNodeImpl#canContain(CSSMMNode)
	 */
	boolean canContain(CSSMMNode child) {
		return false;
	}

	/*
	 * @see CSSMMNodeImpl#postSetAttribute()
	 */
	// void postSetAttribute() throws BadInitializationException {
	// super.postSetAttribute();
	// String name = getName();
	// if (name == null) { throw new BadInitializationException(); }
	//
	// List list = new ArrayList();
	// list.add(CSSFunctionID.F_ATTR);
	// list.add(CSSFunctionID.F_COUNTER);
	// list.add(CSSFunctionID.F_RGB);
	// list.add(CSSFunctionID.F_RECT);
	// list.add(CSSFunctionID.F_URI);
	// list.add(CSSFunctionID.F_FORMAT);
	// list.add(CSSFunctionID.F_LOCAL);
	//
	// if (! list.contains(name.toLowerCase())) {
	// throw new BadInitializationException();
	// }
	// }
	/*
	 * @see Object#toString()
	 */
	public String toString() {
		return getFunctionString();
	}

	void setFunctionString(String value) {
		fValue = value;
	}

	/*
	 * @see CSSMMNodeImpl#getError()
	 */
	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else if (getFunctionString() == null) {
			return MetaModelErrors.ERROR_NO_CHILD;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}


	private String fValue = null;
}
