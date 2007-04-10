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
package org.eclipse.jst.jsp.core.internal.provisional;


/**
 * JSP 1.1 Namespace
 */
public interface JSP11Namespace {

	public static interface ElementName {
		// Element names
		public static final String SCRIPTLET = "jsp:scriptlet"; //$NON-NLS-1$
		public static final String EXPRESSION = "jsp:expression"; //$NON-NLS-1$
		public static final String DECLARATION = "jsp:declaration"; //$NON-NLS-1$
		public static final String DIRECTIVE_PAGE = "jsp:directive.page"; //$NON-NLS-1$
		public static final String DIRECTIVE_INCLUDE = "jsp:directive.include"; //$NON-NLS-1$
		public static final String DIRECTIVE_TAGLIB = "jsp:directive.taglib"; //$NON-NLS-1$
		public static final String USEBEAN = "jsp:useBean"; //$NON-NLS-1$
		public static final String SETPROPERTY = "jsp:setProperty"; //$NON-NLS-1$
		public static final String GETPROPERTY = "jsp:getProperty"; //$NON-NLS-1$
		public static final String INCLUDE = "jsp:include"; //$NON-NLS-1$
		public static final String FORWARD = "jsp:forward"; //$NON-NLS-1$
		public static final String PLUGIN = "jsp:plugin"; //$NON-NLS-1$
		public static final String PARAMS = "jsp:params"; //$NON-NLS-1$
		public static final String FALLBACK = "jsp:fallback"; //$NON-NLS-1$
		public static final String PARAM = "jsp:param"; //$NON-NLS-1$
		public static final String ROOT = "jsp:root"; //$NON-NLS-1$
		public static final String TEXT = "jsp:text"; //$NON-NLS-1$
	}

	public static final String JSP11_URI = ""; //$NON-NLS-1$
	public static final String JSP_TAG_PREFIX = "jsp"; //$NON-NLS-1$
	// attribute names
	//   directive.page
	public static final String ATTR_NAME_LANGUAGE = "language"; //$NON-NLS-1$
	public static final String ATTR_NAME_EXTENDS = "extends"; //$NON-NLS-1$
	public static final String ATTR_NAME_CONTENT_TYPE = "contentType"; //$NON-NLS-1$
	public static final String ATTR_NAME_IMPORT = "import"; //$NON-NLS-1$
	public static final String ATTR_NAME_SESSION = "session"; //$NON-NLS-1$
	public static final String ATTR_NAME_BUFFER = "buffer"; //$NON-NLS-1$
	public static final String ATTR_NAME_AUTOFLUSH = "autoFlush"; //$NON-NLS-1$
	public static final String ATTR_NAME_IS_THREAD_SAFE = "isThreadSafe"; //$NON-NLS-1$
	public static final String ATTR_NAME_INFO = "info"; //$NON-NLS-1$
	public static final String ATTR_NAME_ERROR_PAGE = "errorPage"; //$NON-NLS-1$
	public static final String ATTR_NAME_IS_ERROR_PAGE = "isErrorPage"; //$NON-NLS-1$
	public static final String ATTR_NAME_PAGE_ENCODING = "pageEncoding"; //$NON-NLS-1$
	//   directive.include
	public static final String ATTR_NAME_FILE = "file"; //$NON-NLS-1$
	//   directive.taglib
	public static final String ATTR_NAME_URI = "uri"; //$NON-NLS-1$
	public static final String ATTR_NAME_PREFIX = "prefix"; //$NON-NLS-1$
	//   useBean
	public static final String ATTR_NAME_ID = "id"; //$NON-NLS-1$
	public static final String ATTR_NAME_SCOPE = "scope"; //$NON-NLS-1$
	public static final String ATTR_NAME_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTR_NAME_BEAN_NAME = "beanName"; //$NON-NLS-1$
	public static final String ATTR_NAME_TYPE = "type"; //$NON-NLS-1$
	//   setProperty
	public static final String ATTR_NAME_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_NAME_PROPERTY = "property"; //$NON-NLS-1$
	public static final String ATTR_NAME_VALUE = "value"; //$NON-NLS-1$
	public static final String ATTR_NAME_PARAM = "param"; //$NON-NLS-1$
	//   include
	public static final String ATTR_NAME_PAGE = "page"; //$NON-NLS-1$
	public static final String ATTR_NAME_FLUSH = "flush"; //$NON-NLS-1$
	//   plugin
	public static final String ATTR_NAME_CODE = "code"; //$NON-NLS-1$
	public static final String ATTR_NAME_CODEBASE = "codebase"; //$NON-NLS-1$
	public static final String ATTR_NAME_ALIGN = "align"; //$NON-NLS-1$
	public static final String ATTR_NAME_ARCHIVE = "archive"; //$NON-NLS-1$
	public static final String ATTR_NAME_HEIGHT = "height"; //$NON-NLS-1$
	public static final String ATTR_NAME_HSPACE = "hspace"; //$NON-NLS-1$
	public static final String ATTR_NAME_JREVERSION = "jreversion"; //$NON-NLS-1$
	public static final String ATTR_NAME_VSPACE = "vspace"; //$NON-NLS-1$
	public static final String ATTR_NAME_WIDTH = "width"; //$NON-NLS-1$
	public static final String ATTR_NAME_NSPLUGINURL = "nspluginurl"; //$NON-NLS-1$
	public static final String ATTR_NAME_IEPLUGINURL = "iepluginurl"; //$NON-NLS-1$
	//   root
	public static final String ATTR_NAME_XMLNS_JSP = "xmlns:jsp"; //$NON-NLS-1$
	public static final String ATTR_NAME_VERSION = "version"; //$NON-NLS-1$
	// attribute values
	public static final String ATTR_VALUE_TRUE = "true"; //$NON-NLS-1$
	public static final String ATTR_VALUE_FALSE = "false"; //$NON-NLS-1$
	public static final String ATTR_VALUE_JAVA = "java"; //$NON-NLS-1$
	public static final String ATTR_VALUE_CT_DEFAULT = "text/html; charset=ISO-8859-1";//D195366 //$NON-NLS-1$
	public static final String ATTR_VALUE_BUFSIZ_DEFAULT = "8kb"; //$NON-NLS-1$
	public static final String ATTR_VALUE_PAGE = "page"; //$NON-NLS-1$
	public static final String ATTR_VALUE_SESSION = "session"; //$NON-NLS-1$
	public static final String ATTR_VALUE_REQUEST = "request"; //$NON-NLS-1$
	public static final String ATTR_VALUE_APPLICATION = "application"; //$NON-NLS-1$
	public static final String ATTR_VALUE_BEAN = "bean"; //$NON-NLS-1$
	public static final String ATTR_VALUE_APPLET = "applet"; //$NON-NLS-1$
	public static final String ATTR_VALUE_TOP = "top"; //$NON-NLS-1$
	public static final String ATTR_VALUE_MIDDLE = "middle"; //$NON-NLS-1$
	public static final String ATTR_VALUE_BOTTOM = "bottom"; //$NON-NLS-1$
	public static final String ATTR_VALUE_LEFT = "left"; //$NON-NLS-1$
	public static final String ATTR_VALUE_RIGHT = "right"; //$NON-NLS-1$
	public static final String ATTR_VALUE_JVER11 = "1.1"; //$NON-NLS-1$
	public static final String ATTR_VALUE_XMLNS_JSP = "http://java.sun.com/JSP/Page"; //$NON-NLS-1$
}
