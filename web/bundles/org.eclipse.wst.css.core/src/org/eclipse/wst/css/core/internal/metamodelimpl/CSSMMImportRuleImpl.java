/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMImportRule;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;

class CSSMMImportRuleImpl extends CSSMMNodeImpl implements CSSMMImportRule {


	public CSSMMImportRuleImpl() {
		super();
	}

	public String getType() {
		return TYPE_IMPORT_RULE;
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
