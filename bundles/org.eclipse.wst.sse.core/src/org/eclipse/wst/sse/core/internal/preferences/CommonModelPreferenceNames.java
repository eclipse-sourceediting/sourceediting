/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.preferences;


/**
 * Here is a description of how each common model file preference is used.
 * 
 * tabWidth The number of spaces representing a tab. This number is also used
 * as number of spaces to indent during formatting when indentUsingTabs is
 * false.
 * 
 * splitLines Indicates if long lines should be splitted.
 * 
 * splitLinesUsingEditorsWidth Indicates if long lines should be splitted
 * using the editor's current width. The editor's current width will be used
 * when splitting long lines if splitLinesUsingEditorWidth is true. lineWidth
 * will be used when splitting long lines if splitLinesUsingEditorWidth is
 * false.
 * 
 * lineWidth The maximum width of a line before a line split is needed. This
 * number is only used when lineSplitting is true, otherwise it's ignored.
 * 
 * splitMultiAttrs Indicates if tags with multiple attributes should be
 * formatted (splitting each attr on a new line).
 * 
 * indentUsingTabs Indicates if tabs should be used for indentation during
 * formatting. The same number of spaces specified by tabWidth will be used
 * for indentation if indentUsingTabs is false.
 * 
 * clearAllBlankLines Indicates if all blanks lines should be cleared during
 * formatting. Blanks lines will be kept when clearAllBlankLines is false.
 * 
 * formattingSupported Indicates if the current content type supports
 * formatting. The splitLines, splitLinesUsingEditorsWidth, lineWidth,
 * splitMultiAttrs, and indentUsingTabs preferences will be meaningless if
 * formattingSupported is false.
 * 
 * contentAssistSupported Indicates if the current content type supports
 * content assist. The autoPropose, and autoProposeCode preferences will be
 * meaningless if contentAssistSupported is false.
 * 
 * preferredMarkupCaseSupported Indicates if the current content type supports
 * "preferred markup case for content assist, and code generation". The
 * tagNameCase, and attrNameCase preferences will be meaningless if
 * preferredMarkupCaseSupported is false.
 *  
 */
public interface CommonModelPreferenceNames {
	String TAB_WIDTH = "tabWidth";//$NON-NLS-1$
	String LINE_WIDTH = "lineWidth";//$NON-NLS-1$
	String SPLIT_MULTI_ATTRS = "splitMultiAttrs";//$NON-NLS-1$
	String INDENT_USING_TABS = "indentUsingTabs";//$NON-NLS-1$
	String CLEAR_ALL_BLANK_LINES = "clearAllBlankLines";//$NON-NLS-1$

	String TAG_NAME_CASE = "tagNameCase";//$NON-NLS-1$
	String ATTR_NAME_CASE = "attrNameCase";//$NON-NLS-1$

	String FORMATTING_SUPPORTED = "formattingSupported";//$NON-NLS-1$

	String PREFERRED_MARKUP_CASE_SUPPORTED = "preferredMarkupCaseSupported";//$NON-NLS-1$

	String TASK_TAG_TAGS = "task-tag-tags"; //$NON-NLS-1$
	String TASK_TAG_PRIORITIES = "task-tag-priorities"; //$NON-NLS-1$
	String TASK_TAG_ENABLE = "task-tags"; //$NON-NLS-1$
	String TASK_TAG_PROJECTS_IGNORED = "task-tag-projects-toIgnore"; //$NON-NLS-1$


	/**
	 * these are preferences that should be inherited from the "embedded
	 * preference store" for example: if you ask for th OVERVIEW_RULER
	 * preference for JSP, you will automatically get the preference from the
	 * HTML preference store.
	 */
	String EMBEDDED_CONTENT_TYPE_PREFERENCES[] = {TAB_WIDTH, LINE_WIDTH, SPLIT_MULTI_ATTRS, INDENT_USING_TABS, CLEAR_ALL_BLANK_LINES, TAG_NAME_CASE, ATTR_NAME_CASE,};

	String FORMATTING_PREFERENCES[] = {TAB_WIDTH, LINE_WIDTH, SPLIT_MULTI_ATTRS, INDENT_USING_TABS, CLEAR_ALL_BLANK_LINES,};

	String AUTO = "Auto";//$NON-NLS-1$
	String UTF_8 = "UTF-8";//$NON-NLS-1$
	String ISO_8859_1 = "ISO-8859-1";//$NON-NLS-1$

	int ASIS = 0;
	int LOWER = 1;
	int UPPER = 2;

	// cleanup preference names
	String CLEANUP_TAG_NAME_CASE = "cleanupTagNameCase";//$NON-NLS-1$
	String CLEANUP_ATTR_NAME_CASE = "cleanupAttrNameCase";//$NON-NLS-1$
	String COMPRESS_EMPTY_ELEMENT_TAGS = "compressEmptyElementTags";//$NON-NLS-1$
	String INSERT_REQUIRED_ATTRS = "insertRequiredAttrs";//$NON-NLS-1$
	String INSERT_MISSING_TAGS = "insertMissingTags";//$NON-NLS-1$
	String QUOTE_ATTR_VALUES = "quoteAttrValues";//$NON-NLS-1$
	String FORMAT_SOURCE = "formatSource";//$NON-NLS-1$
	String CONVERT_EOL_CODES = "convertEOLCodes";//$NON-NLS-1$
	String CLEANUP_EOL_CODE = "cleanupEOLCode";//$NON-NLS-1$

	String LAST_ACTIVE_PAGE = "lastActivePage";//$NON-NLS-1$	

	// need to put default tab width preference here so it is accessible by
	// both model and editor
	// this way, editor does not need to query model's default tab width
	// preference
	int DEFAULT_TAB_WIDTH = 4;
}
