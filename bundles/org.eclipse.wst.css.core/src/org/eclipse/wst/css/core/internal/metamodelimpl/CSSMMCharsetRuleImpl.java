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

import org.eclipse.wst.css.core.internal.metamodel.CSSMMCharsetRule;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;

class CSSMMCharsetRuleImpl extends CSSMMNodeImpl implements CSSMMCharsetRule {


	public CSSMMCharsetRuleImpl() {
		super();
	}

	public String getType() {
		return TYPE_CHARSET_RULE;
	}

	public String getName() {
		return NAME_NOT_AVAILABLE;
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
		return MetaModelErrors.NO_ERROR;
	}

}
