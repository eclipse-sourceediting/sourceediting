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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;



/**
 * Provide all names defined in the HTML 4.0? specification.
 */
public interface CHTMLNamespace {

	// Element names
	public static interface ElementName {
		public static final String A = HTML40Namespace.ElementName.A;
		public static final String ADDRESS = HTML40Namespace.ElementName.ADDRESS;
		public static final String BASE = HTML40Namespace.ElementName.BASE;
		public static final String BLOCKQUOTE = HTML40Namespace.ElementName.BLOCKQUOTE;
		public static final String BODY = HTML40Namespace.ElementName.BODY;
		public static final String BR = HTML40Namespace.ElementName.BR;
		public static final String CENTER = HTML40Namespace.ElementName.CENTER;
		public static final String DD = HTML40Namespace.ElementName.DD;
		public static final String DIR = HTML40Namespace.ElementName.DIR;
		public static final String DIV = HTML40Namespace.ElementName.DIV;
		public static final String DL = HTML40Namespace.ElementName.DL;
		public static final String DT = HTML40Namespace.ElementName.DT;
		public static final String FORM = HTML40Namespace.ElementName.FORM;
		public static final String H1 = HTML40Namespace.ElementName.H1;
		public static final String H2 = HTML40Namespace.ElementName.H2;
		public static final String H3 = HTML40Namespace.ElementName.H3;
		public static final String H4 = HTML40Namespace.ElementName.H4;
		public static final String H5 = HTML40Namespace.ElementName.H5;
		public static final String H6 = HTML40Namespace.ElementName.H6;
		public static final String HEAD = HTML40Namespace.ElementName.HEAD;
		public static final String HR = HTML40Namespace.ElementName.HR;
		public static final String HTML = HTML40Namespace.ElementName.HTML;
		public static final String IMG = HTML40Namespace.ElementName.IMG;
		public static final String INPUT = HTML40Namespace.ElementName.INPUT;
		public static final String LI = HTML40Namespace.ElementName.LI;
		public static final String MENU = HTML40Namespace.ElementName.MENU;
		public static final String META = HTML40Namespace.ElementName.META;
		public static final String OL = HTML40Namespace.ElementName.OL;
		public static final String OPTION = HTML40Namespace.ElementName.OPTION;
		public static final String P = HTML40Namespace.ElementName.P;
		public static final String PRE = HTML40Namespace.ElementName.PRE;
		public static final String SELECT = HTML40Namespace.ElementName.SELECT;
		public static final String TEXTAREA = HTML40Namespace.ElementName.TEXTAREA;
		public static final String TITLE = HTML40Namespace.ElementName.TITLE;
		public static final String UL = HTML40Namespace.ElementName.UL;
		public static final String SSI_CONFIG = HTML40Namespace.ElementName.SSI_CONFIG;
		public static final String SSI_ECHO = HTML40Namespace.ElementName.SSI_ECHO;
		public static final String SSI_EXEC = HTML40Namespace.ElementName.SSI_EXEC;
		public static final String SSI_FSIZE = HTML40Namespace.ElementName.SSI_FSIZE;
		public static final String SSI_FLASTMOD = HTML40Namespace.ElementName.SSI_FLASTMOD;
		public static final String SSI_INCLUDE = HTML40Namespace.ElementName.SSI_INCLUDE;
		public static final String SSI_PRINTENV = HTML40Namespace.ElementName.SSI_PRINTENV;
		public static final String SSI_SET = HTML40Namespace.ElementName.SSI_SET;
	}

	// Character Entities
	public static interface EntityName {
	}

	// global attribute names
	public static final String ATTR_NAME_VERSION = HTML40Namespace.ATTR_NAME_VERSION;
	public static final String ATTR_NAME_SRC = HTML40Namespace.ATTR_NAME_SRC;
	public static final String ATTR_NAME_ALT = HTML40Namespace.ATTR_NAME_ALT;
	public static final String ATTR_NAME_HEIGHT = HTML40Namespace.ATTR_NAME_HEIGHT;
	public static final String ATTR_NAME_WIDTH = HTML40Namespace.ATTR_NAME_WIDTH;
	public static final String ATTR_NAME_ALIGN = HTML40Namespace.ATTR_NAME_ALIGN;
	public static final String ATTR_NAME_BORDER = HTML40Namespace.ATTR_NAME_BORDER;
	public static final String ATTR_NAME_HSPACE = HTML40Namespace.ATTR_NAME_HSPACE;
	public static final String ATTR_NAME_VSPACE = HTML40Namespace.ATTR_NAME_VSPACE;
	public static final String ATTR_NAME_NAME = HTML40Namespace.ATTR_NAME_NAME;
	public static final String ATTR_NAME_CLEAR = HTML40Namespace.ATTR_NAME_CLEAR;
	public static final String ATTR_NAME_NOSHADE = HTML40Namespace.ATTR_NAME_NOSHADE;
	public static final String ATTR_NAME_CHECKED = HTML40Namespace.ATTR_NAME_CHECKED;
	public static final String ATTR_NAME_MAXLENGTH = HTML40Namespace.ATTR_NAME_MAXLENGTH;
	public static final String ATTR_NAME_ISTYLE = HTML40Namespace.ATTR_NAME_ISTYLE;
	public static final String ATTR_NAME_HTTP_EQUIV = HTML40Namespace.ATTR_NAME_HTTP_EQUIV;
	public static final String ATTR_NAME_CONTENT = HTML40Namespace.ATTR_NAME_CONTENT;

	public static final String ATTR_NAME_HREF = HTML40Namespace.ATTR_NAME_HREF;
	public static final String ATTR_NAME_MULTIPLE = HTML40Namespace.ATTR_NAME_MULTIPLE;
	public static final String ATTR_NAME_SELECTED = HTML40Namespace.ATTR_NAME_SELECTED;
	public static final String ATTR_NAME_ROWS = HTML40Namespace.ATTR_NAME_ROWS;
	public static final String ATTR_NAME_COLS = HTML40Namespace.ATTR_NAME_COLS;
	public static final String ATTR_NAME_ACTION = HTML40Namespace.ATTR_NAME_ACTION;
	public static final String ATTR_NAME_METHOD = HTML40Namespace.ATTR_NAME_METHOD;
	public static final String ATTR_NAME_ENCTYPE = HTML40Namespace.ATTR_NAME_ENCTYPE;
	public static final String ATTR_NAME_SIZE = HTML40Namespace.ATTR_NAME_SIZE;
	public static final String ATTR_NAME_TYPE = HTML40Namespace.ATTR_NAME_TYPE;
	public static final String ATTR_NAME_VALUE = HTML40Namespace.ATTR_NAME_VALUE;



	public static final String ATTR_NAME_ERRMSG = HTML40Namespace.ATTR_NAME_ERRMSG;
	public static final String ATTR_NAME_SIZEFMT = HTML40Namespace.ATTR_NAME_SIZEFMT;
	public static final String ATTR_NAME_TIMEFMT = HTML40Namespace.ATTR_NAME_TIMEFMT;
	public static final String ATTR_NAME_VAR = HTML40Namespace.ATTR_NAME_VAR;
	public static final String ATTR_NAME_CGI = HTML40Namespace.ATTR_NAME_CGI;
	public static final String ATTR_NAME_CMD = HTML40Namespace.ATTR_NAME_CMD;
	public static final String ATTR_NAME_FILE = HTML40Namespace.ATTR_NAME_FILE;
	public static final String ATTR_NAME_VIRTUAL = HTML40Namespace.ATTR_NAME_VIRTUAL;

	// global attribute values; mainly used in enumeration.
	public static final String ATTR_VALUE_VERSION_TRANSITIONAL = "-//W3C//DTD Compact HTML 1.0 Draft//EN"; //$NON-NLS-1$
	//   for align (top|middle|bottom|left|right)
	public static final String ATTR_VALUE_TOP = HTML40Namespace.ATTR_VALUE_TOP;
	public static final String ATTR_VALUE_MIDDLE = HTML40Namespace.ATTR_VALUE_MIDDLE;
	public static final String ATTR_VALUE_BOTTOM = HTML40Namespace.ATTR_VALUE_BOTTOM;
	public static final String ATTR_VALUE_LEFT = HTML40Namespace.ATTR_VALUE_LEFT;
	public static final String ATTR_VALUE_CENTER = HTML40Namespace.ATTR_VALUE_CENTER;
	public static final String ATTR_VALUE_RIGHT = HTML40Namespace.ATTR_VALUE_RIGHT;
	//   for clear (left|all|right|none): left and right are already defined above.
	public static final String ATTR_VALUE_ALL = HTML40Namespace.ATTR_VALUE_ALL;
	public static final String ATTR_VALUE_NONE = HTML40Namespace.ATTR_VALUE_NONE;
	//   for type of INPUT
	//       (text | password | checkbox | radio | submit | reset |
	//        file | hidden | image | button)
	public static final String ATTR_VALUE_TEXT = HTML40Namespace.ATTR_VALUE_TEXT;
	public static final String ATTR_VALUE_PASSWORD = HTML40Namespace.ATTR_VALUE_PASSWORD;
	public static final String ATTR_VALUE_CHECKBOX = HTML40Namespace.ATTR_VALUE_CHECKBOX;
	public static final String ATTR_VALUE_RADIO = HTML40Namespace.ATTR_VALUE_RADIO;
	public static final String ATTR_VALUE_SUBMIT = HTML40Namespace.ATTR_VALUE_SUBMIT;
	public static final String ATTR_VALUE_RESET = HTML40Namespace.ATTR_VALUE_RESET;
	public static final String ATTR_VALUE_HIDDEN = HTML40Namespace.ATTR_VALUE_HIDDEN;
	//   for span, colspan, rowspan
	public static final String ATTR_VALUE_1 = HTML40Namespace.ATTR_VALUE_1;
	//   for frameborder
	public static final String ATTR_VALUE_0 = HTML40Namespace.ATTR_VALUE_0;
	//   for method of FORM
	public static final String ATTR_VALUE_GET = HTML40Namespace.ATTR_VALUE_GET;
	public static final String ATTR_VALUE_POST = HTML40Namespace.ATTR_VALUE_POST;
	public static final String ATTR_VALUE_WWW_FORM_URLENCODED = HTML40Namespace.ATTR_VALUE_WWW_FORM_URLENCODED;
	//   for behaviro of MARQUEE
	public static final String ATTR_VALUE_SCROLL = HTML40Namespace.ATTR_VALUE_SCROLL;
	public static final String ATTR_VALUE_SLIDE = HTML40Namespace.ATTR_VALUE_SLIDE;
	public static final String ATTR_VALUE_ALTERNATE = HTML40Namespace.ATTR_VALUE_ALTERNATE;
	//   for direction of MARQUEE
	public static final String ATTR_VALUE_UP = HTML40Namespace.ATTR_VALUE_UP;
	public static final String ATTR_VALUE_DOWN = HTML40Namespace.ATTR_VALUE_DOWN;
}
