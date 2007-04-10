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
import org.eclipse.wst.css.core.internal.metamodel.CSSMMSelector;


abstract class CSSMMSelectorImpl extends CSSMMNodeImpl implements CSSMMSelector {

	/**
	 * Constructor for CSSMMSelectorImpl.
	 */
	public CSSMMSelectorImpl() {
		super();
	}

	/*
	 * @see CSSMMNode#getName()
	 */
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
	 * @see CSSMMNode#getType()
	 */
	public String getType() {
		return TYPE_SELECTOR;
	}

	/*
	 * @see CSSMMNodeImpl#canContain(CSSMMNode)
	 */
	boolean canContain(CSSMMNode child) {
		return false;
	}

	/*
	 * @see CSSMMNodeImpl#getError()
	 */
	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}

	/*
	 * @see CSSMMSelector#getSelectorString()
	 */
	public String getSelectorString() {
		return fValue;
	}

	/*
	 * @see Object#toString()
	 */
	public String toString() {
		return getSelectorString();
	}


	protected String fValue = null;
}
