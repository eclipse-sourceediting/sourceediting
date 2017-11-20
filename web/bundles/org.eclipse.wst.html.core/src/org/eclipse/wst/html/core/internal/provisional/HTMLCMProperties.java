/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.provisional;


/**
 * HTMLCMProperties defines all property names and pre-defined values in HTML CM.
 * All of those properties can be retreived from element declarations of HTML CM
 * via CMNode#getProperty(String propName).
 */
public interface HTMLCMProperties {

	/**
	 * "shouldIgnoreCase" returns java.lang.Boolean object.
	 */
	public static final String SHOULD_IGNORE_CASE = "shouldIgnoreCase";//$NON-NLS-1$
	/**
	 * "shouldKeepSpace" returns java.lang.Boolean object.
	 */
	public static final String SHOULD_KEEP_SPACE = "shouldKeepSpace";//$NON-NLS-1$
	/**
	 * "indentChildSource" returns java.lang.Boolean object.
	 */
	public static final String SHOULD_INDENT_CHILD_SOURCE = "shouldIndentChildSource";//$NON-NLS-1$
	/**
	 * "terminators" returns java.util.Iterator (an array of String objects).
	 */
	public static final String TERMINATORS = "terminators";//$NON-NLS-1$
	/**
	 * "contentHint" returns CMElementDeclaration instnace.
	 */
	public static final String CONTENT_HINT = "contentHint";//$NON-NLS-1$
	/**
	 * "prohibitedAncestors" returns org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap.
	 */
	public static final String PROHIBITED_ANCESTORS = "prohibitedAncestors";//$NON-NLS-1$
	/**
	 * "isJSP" returns java.lang.Boolean object.
	 */
	public static final String IS_JSP = "isJSP";//$NON-NLS-1$
	/**
	 * "isXHTML" returns java.lang.Boolean object.
	 */
	public static final String IS_XHTML = "isXHTML";//$NON-NLS-1$
	/**
	 * "isSSI" returns java.lang.Boolean object.
	 */
	public static final String IS_SSI = "isSSI";//$NON-NLS-1$
	/**
	 * "lineBreakHint" returns String (one of pre-defined values in Values).
	 */
	public static final String LINE_BREAK_HINT = "lineBreakHint";//$NON-NLS-1$
	/**
	 * "layoutType" returns String (one of pre-defined values in Values).
	 */
	public static final String LAYOUT_TYPE = "layoutType";//$NON-NLS-1$
	/**
	 * "tagInfo" returns String (documentation for this element).
	 */
	public static final String TAGINFO = "tagInfo";//$NON-NLS-1$
	/**
	 * "omitType" returns String (one of pre-defined values in Values).
	 */
	public static final String OMIT_TYPE = "omitType";//$NON-NLS-1$
	/**
	 * "inclusion" returns org.eclipse.wst.xml.core.internal.contentmodel.CMContent.
	 */
	public static final String INCLUSION = "inclusion";//$NON-NLS-1$
	/**
	 * "isScriptable" returns java.lang.Boolean object.
	 */
	public static final String IS_SCRIPTABLE = "isScriptable"; //$NON-NLS-1$
	/**
	 * "isObsolete" returns java.lang.Boolean object.
	 */
	public static final String IS_OBSOLETE = "isObsolete";//$NON-NLS-1$
	

	public static interface Values {
		/*
		 * for LINE_BREAK_HINT = "lineBreakHint".
		 */
		public static final String BREAK_NONE = "breakNone";//$NON-NLS-1$
		public static final String BREAK_AFTER_START = "breakAfterStart";//$NON-NLS-1$
		public static final String BREAK_BEFORE_START_AND_AFTER_END = "breakBeforeStartAndAfterEnd";//$NON-NLS-1$
		/*
		 * for LAYOUT_TYPE = "layoutType"
		 */
		public static final String LAYOUT_BLOCK = "layoutBlock";//$NON-NLS-1$
		/** BR */
		public static final String LAYOUT_BREAK = "layoutBreak";//$NON-NLS-1$
		/** Hidden object; like HTML or HEAD */
		public static final String LAYOUT_HIDDEN = "layoutHidden";//$NON-NLS-1$
		public static final String LAYOUT_NONE = "layoutNone";//$NON-NLS-1$
		/** No wrap object; like IMG, APPLET,... */
		public static final String LAYOUT_OBJECT = "layoutObject";//$NON-NLS-1$
		public static final String LAYOUT_WRAP = "layoutWrap";//$NON-NLS-1$
		/*
		 * for OMIT_TYPE = "omitType"
		 */
		/** Not ommisible. */
		public static final String OMIT_NONE = "omitNone";//$NON-NLS-1$
		/** Both tags are ommisible. */
		public static final String OMIT_BOTH = "omitBoth";//$NON-NLS-1$
		/** The end tag is ommisible. */
		public static final String OMIT_END = "omitEnd";//$NON-NLS-1$
		/** The end tag is ommitted when created. */
		public static final String OMIT_END_DEFAULT = "omitEndDefault";//$NON-NLS-1$
		/** The end tag must be omitted. */
		public static final String OMIT_END_MUST = "omitEndMust";//$NON-NLS-1$

	}
}
