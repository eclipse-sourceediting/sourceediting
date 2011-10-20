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
package org.eclipse.wst.jsdt.web.core.javascript;


import org.eclipse.wst.html.core.text.IHTMLPartitions;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public interface JsDataTypes extends HTML40Namespace {
	/*
	 * remove when when we refactor (need to add this content type to many
	 * project types in wst)
	 */
	public static final String BASE_FILE_EXTENSION = ".js"; //$NON-NLS-1$
	public static String[] CONSTANTS = { "false", "null", "true" }; //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	public static final String[] EVENTS = { HTML40Namespace.ATTR_NAME_ONCLICK, HTML40Namespace.ATTR_NAME_ONDBLCLICK, HTML40Namespace.ATTR_NAME_ONMOUSEDOWN,
			HTML40Namespace.ATTR_NAME_ONMOUSEUP, HTML40Namespace.ATTR_NAME_ONMOUSEOVER, HTML40Namespace.ATTR_NAME_ONMOUSEMOVE,
			HTML40Namespace.ATTR_NAME_ONMOUSEOUT, HTML40Namespace.ATTR_NAME_ONKEYPRESS, HTML40Namespace.ATTR_NAME_ONKEYDOWN, HTML40Namespace.ATTR_NAME_ONKEYUP,
			HTML40Namespace.ATTR_NAME_ONHELP };
	public static final String[] HTMLATREVENTS = { "onload", "onunload", "onclick", "onmousedown", "onmouseup", "onmouseover", "onmousemove", "onmouseout", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			"onfocus", "onblur", "onkeypress", "onkeydown", "onkeyup", "onsubmit", "onreset", "onselect", "onchange", }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	public static final String[] JSVALIDDATATYPES = { "JAVASCRIPT", "TEXT/JAVASCRIPT" }; //$NON-NLS-1$ //$NON-NLS-2$
	public static String[] KEYWORDS = { "abstract", "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete", "do", "else", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
			"enum", "export", "extends", "final", "finally", "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "interface", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
			"native", "new", "package", "private", "protected", "public", "return", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			"static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "typeof", "volatile", "while", "with" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
	// public static final String
	// NEW_PARTITION_TYPE="org.eclipse.wst.jsdt.StructuredJs";
	// public static final String NEW_PARTITION_TYPE=IHTMLPartitions.SCRIPT;
	public static final String NEW_PARTITION_TYPE = IHTMLPartitions.SCRIPT;
	public static final String[] TAKEOVER_PARTITION_TYPES = { "none" }; //$NON-NLS-1$
	public static String[] TYPES = { "boolean", "byte", "char", "double", "int", "long", "short", "float", "var", "void" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
}
