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
package org.eclipse.wst.xml.core.document;



/**
 * JSPTag interface
 */
public interface JSPTag {

	static final String JSP_SCRIPTLET = "jsp:scriptlet";//$NON-NLS-1$
	static final String JSP_EXPRESSION = "jsp:expression";//$NON-NLS-1$
	static final String JSP_DECLARATION = "jsp:declaration";//$NON-NLS-1$
	static final String JSP_DIRECTIVE = "jsp:directive";//$NON-NLS-1$
	static final String JSP_ROOT = "jsp:root";//$NON-NLS-1$
	static final String TAG_OPEN = "<%";//$NON-NLS-1$
	static final String TAG_CLOSE = "%>";//$NON-NLS-1$
	static final String COMMENT_OPEN = "<%--";//$NON-NLS-1$
	static final String COMMENT_CLOSE = "--%>";//$NON-NLS-1$
	static final String EXPRESSION_TOKEN = "=";//$NON-NLS-1$
	static final String DECLARATION_TOKEN = "!";//$NON-NLS-1$
	static final String DIRECTIVE_TOKEN = "@";//$NON-NLS-1$
}
