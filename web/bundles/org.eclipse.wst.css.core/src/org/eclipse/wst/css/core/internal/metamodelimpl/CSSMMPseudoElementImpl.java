/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;

public class CSSMMPseudoElementImpl extends CSSMMSelectorImpl {


	/**
	 * Constructor for CSSMMPseudoElementImpl.
	 */
	public CSSMMPseudoElementImpl() {
		super();
	}

	/*
	 * @see CSSMMSelector#getSelectorType()
	 */
	public String getSelectorType() {
		return TYPE_PSEUDO_ELEMENT;
	}

	void setSelectorString(String value) {
		String version = getAttribute("version"); //$NON-NLS-1$
		long v = 0;
		if (version != null) {
			try {
				v = Long.parseLong(version);
			}
			catch (Exception e) {}
		}
		// Pseudo Elements added since CSS3 are prefixed by two colons
		fValue = ((v >= 3) ? "::" : ":") + value; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
