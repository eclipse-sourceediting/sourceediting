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
/*nlsXXX*/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

public interface JSP12TLDNames {

	String TAGLIB = JSP11TLDNames.TAGLIB;

	String CONTENT_JSP = JSP11TLDNames.CONTENT_JSP;
	String CONTENT_EMPTY = JSP11TLDNames.CONTENT_EMPTY;
	String CONTENT_TAGDEPENDENT = JSP11TLDNames.CONTENT_TAGDEPENDENT;

	String TAG = JSP11TLDNames.TAG;
	String JSP_VERSION = "jsp-version"; //$NON-NLS-1$
	String TLIB_VERSION = "tlib-version"; //$NON-NLS-1$
	String SHORT_NAME = "short-name"; //$NON-NLS-1$
	String URI = JSP11TLDNames.URI;
	String URN = JSP11TLDNames.URN;

	String NAME = JSP11TLDNames.NAME;
	String TEI_CLASS = "tei-class"; //$NON-NLS-1$
	String TAG_CLASS = "tag-class"; //$NON-NLS-1$
	String BODY_CONTENT = "body-content"; //$NON-NLS-1$
	String ATTRIBUTE = JSP11TLDNames.ATTRIBUTE;

	String ID = JSP11TLDNames.ID;
	String REQUIRED = JSP11TLDNames.REQUIRED;
	String RTEXPRVALUE = JSP11TLDNames.RTEXPRVALUE;

	String PREFIX = JSP11TLDNames.PREFIX;

	String INCLUDE = JSP11TLDNames.INCLUDE;
	String FILE = JSP11TLDNames.FILE;

	String TRUE = JSP11TLDNames.TRUE;
	String FALSE = JSP11TLDNames.FALSE;
	String YES = JSP11TLDNames.YES;
	String NO = JSP11TLDNames.NO;

	/*
	 * @see Eclipse JSP 1.2
	 */
	String DESCRIPTION = "description"; //$NON-NLS-1$
	String DISPLAY_NAME = "display-name"; //$NON-NLS-1$
	String SMALL_ICON = "small-icon"; //$NON-NLS-1$
	String LARGE_ICON = "large-icon"; //$NON-NLS-1$


	String VALIDATOR = "validator"; //$NON-NLS-1$
	String VALIDATOR_CLASS = "validator-class"; //$NON-NLS-1$
	String VALIDATOR_INIT_PARAM = "init-param"; //$NON-NLS-1$
	String VALIDATOR_PARAM_NAME = "param-name"; //$NON-NLS-1$
	String VALIDATOR_PARAM_VALUE = "param-value"; //$NON-NLS-1$


	String LISTENER = "listener"; //$NON-NLS-1$
	String LISTENER_CLASS = "listener-class"; //$NON-NLS-1$

	String VARIABLE = "variable"; //$NON-NLS-1$
	String VARIABLE_NAME_GIVEN = "name-given"; //$NON-NLS-1$
	String VARIABLE_NAME_FROM_ATTRIBUTE = "name-from-attribute"; //$NON-NLS-1$
	String VARIABLE_CLASS = "variable-class"; //$NON-NLS-1$
	String VARIABLE_DECLARE = "declare"; //$NON-NLS-1$
	String VARIABLE_SCOPE = "scope"; //$NON-NLS-1$
	String VARIABLE_SCOPE_NESTED = "NESTED"; //$NON-NLS-1$
	String VARIABLE_SCOPE_AT_BEGIN = "AT_BEGIN"; //$NON-NLS-1$
	String VARIABLE_SCOPE_AT_END = "AT_END"; //$NON-NLS-1$
}
