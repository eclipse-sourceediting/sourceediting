/*
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 * 
 */
package org.eclipse.wst.xsd.core.internal.validation;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by XSD Validation
 */
public class XSDValidationMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsd.core.internal.validation.xsdvalidation";//$NON-NLS-1$

	public static String Message_XSD_validation_message_ui;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, XSDValidationMessages.class);
	}

	private XSDValidationMessages() {
		// cannot create new instance
	}
}
