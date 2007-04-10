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

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMUnit;

/**
 * CSSMMUnit : embedded type
 */
class CSSMMUnitImpl extends CSSMMNodeImpl implements CSSMMUnit {

	public CSSMMUnitImpl() {
		super();
	}

	public String getType() {
		return TYPE_UNIT;
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

	/*
	 * @see CSSMMUnit#getUnitString()
	 */
	public String getUnitString() {
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
	// Map map = new HashMap();
	// map.put(CSSUnitID.U_CM.toLowerCase(), "cm");//$NON-NLS-1$
	// map.put(CSSUnitID.U_DEG.toLowerCase(), "deg");//$NON-NLS-1$
	// map.put(CSSUnitID.U_EMS.toLowerCase(), "em");//$NON-NLS-1$
	// map.put(CSSUnitID.U_EXS.toLowerCase(), "ex");//$NON-NLS-1$
	// map.put(CSSUnitID.U_GRAD.toLowerCase(), "grad");//$NON-NLS-1$
	// map.put(CSSUnitID.U_HASH.toLowerCase(), "#");//$NON-NLS-1$
	// map.put(CSSUnitID.U_HZ.toLowerCase(), "Hz");//$NON-NLS-1$
	// map.put(CSSUnitID.U_IN.toLowerCase(), "in");//$NON-NLS-1$
	// // map.put(CSSUnitID.U_INTEGER.toLowerCase(),
	// "(integer)");//$NON-NLS-1$
	// map.put(CSSUnitID.U_KHZ.toLowerCase(), "kHz");//$NON-NLS-1$
	// map.put(CSSUnitID.U_MM.toLowerCase(), "mm");//$NON-NLS-1$
	// map.put(CSSUnitID.U_MS.toLowerCase(), "ms");//$NON-NLS-1$
	// // map.put(CSSUnitID.U_NUMBER.toLowerCase(), "(number)");//$NON-NLS-1$
	// map.put(CSSUnitID.U_PC.toLowerCase(), "pc");//$NON-NLS-1$
	// map.put(CSSUnitID.U_PERCENTAGE.toLowerCase(), "%");//$NON-NLS-1$
	// map.put(CSSUnitID.U_PT.toLowerCase(), "pt");//$NON-NLS-1$
	// map.put(CSSUnitID.U_PX.toLowerCase(), "px");//$NON-NLS-1$
	// map.put(CSSUnitID.U_RAD.toLowerCase(), "rad");//$NON-NLS-1$
	// map.put(CSSUnitID.U_S.toLowerCase(), "s");//$NON-NLS-1$
	// fValue = (String)map.get(name.toLowerCase());
	// if (fValue == null) { throw new BadInitializationException(); }
	// }
	/*
	 * @see Object#toString()
	 */
	public String toString() {
		return getUnitString();
	}

	void setUnitString(String value) {
		fValue = value;
	}

	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else if (getUnitString() == null) {
			return MetaModelErrors.ERROR_NO_CHILD;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}

	private String fValue = null;
}
