/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.provisional.preferences;

public interface CSSModelPreferenceNames {
	public static final String CASE_IDENTIFIER = "identifierCase"; //$NON-NLS-1$
	public static final String CASE_PRESERVE_CASE = "preserveCase"; //$NON-NLS-1$
	public static final String CASE_PROPERTY_NAME = "propNameCase"; //$NON-NLS-1$
	public static final String CASE_PROPERTY_VALUE = "propValueCase"; //$NON-NLS-1$
	public static final String FORMAT_BETWEEN_VALUE = "betweenValue"; //$NON-NLS-1$
	public static final String FORMAT_PROP_POST_DELIM = "postDelim"; //$NON-NLS-1$
	public static final String FORMAT_PROP_PRE_DELIM = "preDelim"; //$NON-NLS-1$
	public static final String FORMAT_QUOTE = "quote"; //$NON-NLS-1$
	public static final String FORMAT_QUOTE_IN_URI = "quoteInURI"; //$NON-NLS-1$
	public static final String WRAPPING_NEWLINE_ON_OPEN_BRACE = "newLineOnOpenBrace"; //$NON-NLS-1$
	public static final String WRAPPING_ONE_PER_LINE = "onePropertyPerLine"; //$NON-NLS-1$
	public static final String WRAPPING_PROHIBIT_WRAP_ON_ATTR = "prohibitWrapOnAttr"; //$NON-NLS-1$

	// CSS cleanup preference names
	public static final String CLEANUP_CASE_IDENTIFIER = "cleanupIdentifierCase"; //$NON-NLS-1$
	public static final String CLEANUP_CASE_PROPERTY_NAME = "cleanupPropNameCase"; //$NON-NLS-1$
	public static final String CLEANUP_CASE_PROPERTY_VALUE = "cleanupPropValueCase"; //$NON-NLS-1$
	public static final String CLEANUP_CASE_SELECTOR = "cleanupSelectorCase"; //$NON-NLS-1$
}