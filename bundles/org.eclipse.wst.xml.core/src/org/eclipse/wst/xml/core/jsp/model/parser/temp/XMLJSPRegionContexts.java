/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.jsp.model.parser.temp;

import org.eclipse.wst.xml.core.parser.XMLRegionContext;


/**
 *	//TODO @deprecated - marked as deprecated to warn of pending changes. 
 * See the package.html file for full explaination. 
 */

public interface XMLJSPRegionContexts extends XMLRegionContext {

	public static final String JSP_SCRIPTLET_OPEN = "JSP_SCRIPTLET_OPEN"; //$NON-NLS-1$
	public static final String JSP_EXPRESSION_OPEN = "JSP_EXPRESSION_OPEN"; //$NON-NLS-1$
	public static final String JSP_DECLARATION_OPEN = "JSP_DECLARATION_OPEN"; //$NON-NLS-1$

	public static final String JSP_DIRECTIVE_OPEN = "JSP_DIRECTIVE_OPEN"; //$NON-NLS-1$
	public static final String JSP_DIRECTIVE_NAME = "JSP_DIRECTIVE_NAME"; //$NON-NLS-1$
	public static final String JSP_DIRECTIVE_CLOSE = "JSP_DIRECTIVE_CLOSE"; //$NON-NLS-1$

	public static final String JSP_COMMENT_OPEN = "JSP_COMMENT_OPEN"; //$NON-NLS-1$
	public static final String JSP_COMMENT_TEXT = "JSP_COMMENT_TEXT"; //$NON-NLS-1$
	public static final String JSP_COMMENT_CLOSE = "JSP_COMMENT_CLOSE"; //$NON-NLS-1$

	public static final String XML_TAG_ATTRIBUTE_VALUE_SQUOTE = "XML_TAG_ATTRIBUTE_VALUE_SQUOTE"; //$NON-NLS-1$
	public static final String XML_TAG_ATTRIBUTE_VALUE_DQUOTE = "XML_TAG_ATTRIBUTE_VALUE_DQUOTE"; //$NON-NLS-1$

	public static final String JSP_CONTENT = "JSP_CONTENT"; //$NON-NLS-1$
	public static final String JSP_CLOSE = "JSP_CLOSE"; //$NON-NLS-1$

	public static final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$

	public static final String JSP_EL_OPEN = "JSP_EL_OPEN"; //$NON-NLS-1$
	public static final String JSP_EL_CONTENT = "JSP_EL_CONTENT"; //$NON-NLS-1$
	public static final String JSP_EL_SQUOTE = "JSP_EL_SQUOTE"; //$NON-NLS-1$
	public static final String JSP_EL_DQUOTE = "JSP_EL_DQUOTE"; //$NON-NLS-1$
	public static final String JSP_EL_QUOTED_CONTENT = "JSP_EL_QUOTED_CONTENT"; //$NON-NLS-1$
	public static final String JSP_EL_CLOSE = "JSP_EL_CLOSE"; //$NON-NLS-1$
}
