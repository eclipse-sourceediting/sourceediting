/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.dtd.core.internal.validation;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by DTD Validation
 * 
 * @plannedfor 1.0
 */
public class DTDValidationMessages extends NLS {
	public static String _ERROR_REF_ELEMENT_UNDEFINED;

	public static String _UI_DIALOG_DTD_INVALID_TEXT;
	public static String _UI_DIALOG_DTD_INVALID_TITLE;
	public static String _UI_DIALOG_DTD_VALID_TEXT;
	public static String _UI_DIALOG_DTD_VALID_TITLE;
	private static final String BUNDLE_NAME = "org.eclipse.wst.dtd.core.internal.validation.DTDValidationResources";//$NON-NLS-1$
	public static String Missing_required_files_1;
	public static String Missing_required_files_2;
	public static String Missing_required_files_3;
	public static String Missing_required_files_4;
	public static String MESSAGE_DTD_VALIDATION_MESSAGE_UI_;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DTDValidationMessages.class);
	}
}
