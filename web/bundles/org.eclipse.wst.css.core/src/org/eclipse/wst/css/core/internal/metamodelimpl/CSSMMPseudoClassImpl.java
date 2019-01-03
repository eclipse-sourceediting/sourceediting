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

class CSSMMPseudoClassImpl extends CSSMMSelectorImpl {


	/**
	 * Constructor for CSSMMPseudoClassImpl.
	 */
	public CSSMMPseudoClassImpl() {
		super();
	}

	/*
	 * @see CSSMMSelector#getSelectorType()
	 */
	public String getSelectorType() {
		return TYPE_PSEUDO_CLASS;
	}

	void setSelectorString(String value) {
		fValue = ":" + value; //$NON-NLS-1$		
	}
}
