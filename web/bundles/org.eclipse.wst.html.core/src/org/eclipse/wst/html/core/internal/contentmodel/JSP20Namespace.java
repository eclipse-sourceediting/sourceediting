/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

/**
 * New names for JSP 2.0 spec.
 * 
 * @deprecated - use
 *             org.eclipse.jst.jsp.core.internal.provisional.JSP20Namespace
 */

public interface JSP20Namespace extends JSP11Namespace {
	public static String JSP20_URI = "";//$NON-NLS-1$

	/**
	 * New elements for JSP 2.0 spec.
	 */
	public static interface ElementName extends JSP11Namespace.ElementName {
		String DIRECTIVE_TAG = "jsp:directive.tag"; //$NON-NLS-1$
		String DIRECTIVE_ATTRIBUTE = "jsp:directive.attribute"; //$NON-NLS-1$
		String DIRECTIVE_VARIABLE = "jsp:directive.variable"; //$NON-NLS-1$
		String BODY = "jsp:body"; //$NON-NLS-1$
		String ATTRIBUTE = "jsp:attribute"; //$NON-NLS-1$
		String ELEMENT = "jsp:element"; //$NON-NLS-1$
		String DOBODY = "jsp:doBody";
		String INVOKE = "jsp:invoke";
		String OUTPUT = "jsp:output";
	}

	String ATTR_NAME_TAGDIR = "tagdir"; //$NON-NLS-1$

	String ATTR_NAME_DISPLAY_NAME = "display-name"; //$NON-NLS-1$
	String ATTR_NAME_BODY_CONTENT = "body-content"; //$NON-NLS-1$
	String ATTR_NAME_SMALL_ICON = "small-icon"; //$NON-NLS-1$
	String ATTR_NAME_LARGE_ICON = "large-icon"; //$NON-NLS-1$
	String ATTR_NAME_DESCRIPTION = "description"; //$NON-NLS-1$
	String ATTR_NAME_EXAMPLE = "example"; //$NON-NLS-1$
	String ATTR_NAME_LANGUAGE = "language"; //$NON-NLS-1$
	String ATTR_NAME_ISELIGNORED = "isELIgnored"; //$NON-NLS-1$

	String ATTR_NAME_REQUIRED = "required"; //$NON-NLS-1$
	String ATTR_NAME_FRAGMENT = "fragment"; //$NON-NLS-1$
	String ATTR_NAME_RTEXPRVALUE = "rtexprvalue"; //$NON-NLS-1$
	String ATTR_NAME_TYPE = "type"; //$NON-NLS-1$

	String ATTR_NAME_NAME_GIVEN = "name-given"; //$NON-NLS-1$
	String ATTR_NAME_NAME_FROM_ATTRIBUTE = "name-from-attribute"; //$NON-NLS-1$
	String ATTR_NAME_ALIAS = "alias"; //$NON-NLS-1$
	String ATTR_NAME_VARIABLE_CLASS = "variable-class"; //$NON-NLS-1$
	String ATTR_NAME_DECLARE = "declare"; //$NON-NLS-1$

	String ATTR_VALUE_SCRIPTLESS = "scriptless"; //$NON-NLS-1$
	String ATTR_VALUE_TAGDEPENDENT = "tagdependent"; //$NON-NLS-1$
	String ATTR_VALUE_EMPTY = "empty"; //$NON-NLS-1$

	String ATTR_NAME_TRIM = "trim"; //$NON-NLS-1$

	String ATTR_NAME_VAR = "var"; //$NON-NLS-1$
	String ATTR_NAME_VARREADER = "varReader"; //$NON-NLS-1$

	String ATTR_NAME_OMIT_XML_DECL = "omit-xml-declaration"; //$NON-NLS-1$
	String ATTR_NAME_DOCTYPE_ROOT_ELEMENT = "doctype-root-element"; //$NON-NLS-1$
	String ATTR_NAME_DOCTYPE_SYSTEM = "doctype-system"; //$NON-NLS-1$
	String ATTR_NAME_DOCTYPE_PUBLIC = "doctype-public"; //$NON-NLS-1$

	String ATTR_VALUE_NO = "no"; //$NON-NLS-1$
	String ATTR_VALUE_YES = "yes"; //$NON-NLS-1$

	String ATTR_VALUE_SCOPE_AT_END = "AT_END"; //$NON-NLS-1$
	String ATTR_VALUE_SCOPE_AT_BEGIN = "AT_BEGIN"; //$NON-NLS-1$
	String ATTR_VALUE_SCOPE_NESTED = "NESTED"; //$NON-NLS-1$

	String ATTR_NAME_DYNAMIC_ATTRIBUTES = "dynamic-attributes"; //$NON-NLS-1$
	
	String ATTR_VALUE_ENCODING_DEFAULT = "ISO-8859-1"; //$NON-NLS-1$
	
	String ATTR_VALUE_VARIABLE_CLASS_DEFAULT = "java.lang.String"; //$NON-NLS-1$
	
	String ATTR_NAME_MAYSCRIPT = "mayscript"; //$NON-NLS-1$
	
	String ATTR_VALUE_JVER12 = "1.2"; //$NON-NLS-1$
	
	String ATTR_VALUE_JSP_VER_20 = "2.0"; //$NON-NLS-1$
	
	// JSP 2.1 attributes
//	String ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL = "deferredSyntaxAllowedAsLiteral"; //$NON-NLS-1$
//	String ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES = "trimDirectiveWhitespaces"; //$NON-NLS-1$
	
}
