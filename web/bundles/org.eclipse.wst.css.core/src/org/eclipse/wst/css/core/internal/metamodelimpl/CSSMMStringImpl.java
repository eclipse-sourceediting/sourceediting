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
import org.eclipse.wst.css.core.internal.metamodel.CSSMMString;

class CSSMMStringImpl extends CSSMMNodeImpl implements CSSMMString {


	public CSSMMStringImpl() {
		super();
	}

	public String getType() {
		return TYPE_STRING;
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
	 * @see CSSMMNodeImpl#canContain(CSSMMNode)
	 */
	boolean canContain(CSSMMNode child) {
		return false;
	}

	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}
	/*
	 * @see CSSMMNodeImpl#postSetAttribute()
	 */
	// void postSetAttribute() throws BadInitializationException {
	// super.postSetAttribute();
	// String name = getName();
	// if (name == null) { throw new BadInitializationException(); }
	//
	// Map map = new HashMap();
	// map.put(CSSStringID.S_ANY.toLowerCase(), "<any>"); //$NON-NLS-1$
	// map.put(CSSStringID.S_COUNTER_IDENTIFIER.toLowerCase(), "<counter>");
	// //$NON-NLS-1$
	// map.put(CSSStringID.S_FAMILY_NAME.toLowerCase(), "<family-name>");
	// //$NON-NLS-1$
	// map.put(CSSStringID.S_FONT_FACE_NAME.toLowerCase(),
	// "<font-face-name>"); //$NON-NLS-1$
	// map.put(CSSStringID.S_PAGE_IDENTIFIER.toLowerCase(),
	// "<page-identifier>"); //$NON-NLS-1$
	// map.put(CSSStringID.S_SPECIFIC_VOICE.toLowerCase(),
	// "<specific-voice>"); //$NON-NLS-1$
	// map.put(CSSStringID.S_URANGE.toLowerCase(), "<urange>"); //$NON-NLS-1$
	//
	// fValue = (String)map.get(name.toLowerCase());
	// if (fValue == null) { throw new BadInitializationException(); }
	// }
	//
	// /*
	// * @see Object#toString()
	// */
	// public String toString() {
	// return fValue;
	// }
	//
	// private String fValue = null;
}
