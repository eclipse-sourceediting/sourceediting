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

import org.eclipse.wst.css.core.internal.metamodel.CSSMMKeyword;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;

class CSSMMKeywordImpl extends CSSMMNodeImpl implements CSSMMKeyword {

	public CSSMMKeywordImpl() {
		super();
	}

	public String getType() {
		return TYPE_KEYWORD;
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

	/*
	 * @see CSSMMKeyword#getKeywordString()
	 */
	public String getKeywordString() {
		return fValue;
	}

	/*
	 * @see Object#toString()
	 */
	public String toString() {
		return getKeywordString();
	}

	void setKeywordString(String value) {
		fValue = value;
	}

	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else if (getKeywordString() == null) {
			return MetaModelErrors.ERROR_NO_CHILD;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}


	private String fValue = null;
}
