/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.validation;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by DTD Validation
 * 
 * @plannedfor 1.0
 */
public class DTDValidationMessages extends NLS {
	public static String _ERROR_REF_ELEMENT_UNDEFINED;
	public static String _ERROR_DUPLICATE_ELEMENT_DECLARATION;

	private static final String BUNDLE_NAME = "org.eclipse.wst.dtd.core.internal.validation.DTDValidationResources";//$NON-NLS-1$

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DTDValidationMessages.class);
	}
}
