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
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by XML Validation
 */
public class XMLValidationMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xml.core.internal.validation.xmlvalidation";//$NON-NLS-1$

	public static String _UI_PROBLEMS_VALIDATING_UNKNOWN_HOST;
	public static String _UI_PROBLEMS_VALIDATING_FILE_NOT_FOUND;
	public static String _UI_PROBLEMS_CONNECTION_REFUSED;
	public static String _UI_REF_FILE_ERROR_MESSAGE;
	public static String _WARN_NO_GRAMMAR;
	public static String _NO_DOCUMENT_ELEMENT;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, XMLValidationMessages.class);
	}

	private XMLValidationMessages() {
		// cannot create new instance
	}
}
