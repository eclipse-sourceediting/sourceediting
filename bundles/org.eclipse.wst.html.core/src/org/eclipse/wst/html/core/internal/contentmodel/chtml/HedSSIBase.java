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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;




/**
 * Base class for SSI declarations.
 */
abstract class HedSSIBase extends HedEmpty {

	/**
	 */
	public HedSSIBase(String elementName, ElementCollection collection) {
		super(elementName, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 */
	public int getFormatType() {
		return HTMLElementDeclaration.FORMAT_SSI;
	}

	/**
	 */
	public boolean supports(String propName) {
		if (propName.equals(HTMLCMProperties.IS_SSI))
			return true;
		return super.supports(propName);
	}

	/*
	 */
	public Object getProperty(String propName) {
		if (propName.equals(HTMLCMProperties.IS_SSI))
			return new Boolean(true);
		return super.getProperty(propName);
	}
}
