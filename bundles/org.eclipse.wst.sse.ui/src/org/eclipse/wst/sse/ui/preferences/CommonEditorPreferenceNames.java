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


package org.eclipse.wst.sse.ui.preferences;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;

/**
 * Editor preferences split out from Model Preferences. Preferences defined in
 * here are dependent on a visual editor.
 */
public class CommonEditorPreferenceNames {
	/**
	 * A named preference that controls whether bracket matching highlighting is turned on or off.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String MATCHING_BRACKETS = "matchingBrackets"; //$NON-NLS-1$
	
	/**
	 * A named preference that holds the color used to highlight matching brackets.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string 
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 */
	public final static String MATCHING_BRACKETS_COLOR = "matchingBracketsColor"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls if code assist gets auto activated.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */ 
	public static final String AUTO_PROPOSE = "autoPropose";//$NON-NLS-1$
	
	/**
	 * A named preference that holds the characters that auto activate code assist.
	 * <p>
	 * Value is of type <code>String</code>. All characters that trigger auto code assist.
	 * </p>
	 */
	public static final String AUTO_PROPOSE_CODE = "autoProposeCode";//$NON-NLS-1$

	/**
	 * A named preference that defines the key for the hover modifiers.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p> 
	 */
	public static final String EDITOR_TEXT_HOVER_MODIFIERS = "hoverModifiers"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether or grammar should be inferred or not.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */ 
	public static final String EDITOR_USE_INFERRED_GRAMMAR = "useInferredGrammar"; //$NON-NLS-1$
	
	public static final String EDITOR_VALIDATION_CONTENT_MODEL = "validation_content_model"; //$NON-NLS-1$
	
	// currently can take on 3 different values
	public static final String EDITOR_VALIDATION_METHOD = "editorValidationMethod"; //$NON-NLS-1$
	public static final String EDITOR_VALIDATION_NONE = "validation_none"; //$NON-NLS-1$
	public static final String EDITOR_VALIDATION_WORKBENCH_DEFAULT = "validation_workbench_default"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls on-the-fly validation
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */ 
	public static final String EVALUATE_TEMPORARY_PROBLEMS = "evaluateTemporaryProblems"; //$NON-NLS-1$

	/**
	 * The key to store customized templates.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p> 
	 */
	public static final String TEMPLATES_KEY = "org.eclipse.wst.sse.ui.custom_templates"; //$NON-NLS-1$
	
	/**
	 * A named preference that defines read only contrast scale.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p> 
	 */
	public static final String READ_ONLY_FOREGROUND_SCALE = "readOnlyForegroundScale"; //$NON-NLS-1$
	
	/**
	 * @deprecated The function this key is used by has not been implemented
	 */
	public static final String EDITOR_SHOW_TEXT_HOVER_AFFORDANCE = "showTextHoverAffordance"; //$NON-NLS-1$
	
	/**
	 * @deprecated The function this key is used by has not been implemented
	 */
	public static final String EDITOR_ANNOTATION_ROLL_OVER = "editor_annotation_roll_over"; //$NON-NLS-1$
	
	/**
	 * @deprecated The function this key is used by has not been implemented
	 */
	public static final String SHOW_QUICK_FIXABLES = "showQuickFixables"; //$NON-NLS-1$
	
	/**
	 * @deprecated use CommonEditorPreferenceNames.AUTO_PROPOSE
	 */
	public static final String CONTENT_ASSIST_SUPPORTED = "contentAssistSupported";//$NON-NLS-1$
	
	/**
	 * @deprecated use "." instead
	 */
	public static final String DOT = ".";//$NON-NLS-1$
	
	/**
	 * @deprecated use "<" instead
	 */
	public static final String LT = "<";//$NON-NLS-1$
	
	/**
	 * @deprecated use "<%" instead
	 */
	public static final String LT_PERCENT = "<%";//$NON-NLS-1$
	
	/**
	 * @deprecated use AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED
	 */
	public final static String BROWSER_LIKE_LINKS = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED;
	/**
	 * @deprecated use AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINK_KEY_MODIFIER
	 */
	public final static String BROWSER_LIKE_LINKS_KEY_MODIFIER = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINK_KEY_MODIFIER;
	/**
	 * @deprecated use AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINK_COLOR
	 */
	public final static String LINK_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINK_COLOR;
	
	// The below are deprecated constants that were replaced by using MarkerAnnotationPreferences
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String ERROR_INDICATION = "errorIndication"; //$NON-NLS-1$
	/** 
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String ERROR_INDICATION_COLOR = "errorIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String ERROR_INDICATION_IN_OVERVIEW_RULER = "errorIndicationInOverviewRuler"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String BOOKMARK_INDICATION = "bookmarkIndication"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String BOOKMARK_INDICATION_COLOR = "bookmarkIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String BOOKMARK_INDICATION_IN_OVERVIEW_RULER = "bookmarkIndicationInOverviewRuler"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String SEARCH_RESULT_INDICATION = "searchResultIndication"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String SEARCH_RESULT_INDICATION_COLOR = "searchResultIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String SEARCH_RESULT_INDICATION_IN_OVERVIEW_RULER = "searchResultIndicationInOverviewRuler"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String TASK_INDICATION = "taskIndication"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String TASK_INDICATION_COLOR = "taskIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String TASK_INDICATION_IN_OVERVIEW_RULER = "taskIndicationInOverviewRuler"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String UNKNOWN_INDICATION = "unknownIndication"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String UNKNOWN_INDICATION_COLOR = "unknownIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String UNKNOWN_INDICATION_IN_OVERVIEW_RULER = "unknownIndicationInOverviewRuler"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String WARNING_INDICATION = "warningIndication"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String WARNING_INDICATION_COLOR = "warningIndicationColor"; //$NON-NLS-1$
	/**
	 * @deprecated see {@link org.eclipse.ui.texteditor.MarkerAnnotationPreferences}
	 */
	public final static String WARNING_INDICATION_IN_OVERVIEW_RULER = "warningIndicationInOverViewrRuler"; //$NON-NLS-1$

	/**
	 * these are preferences that should be inherited from the "embedded
	 * preference store" for example: if you ask for th OVERVIEW_RULER
	 * preference for JSP, you will automatically get the preference from the
	 * HTML preference store.
	 */
	/**
	 * @deprecated Currently not used by anyone.
	 */
	String EMBEDDED_CONTENT_TYPE_PREFERENCES[] = {CommonModelPreferenceNames.TAB_WIDTH, CommonModelPreferenceNames.LINE_WIDTH, CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, CommonModelPreferenceNames.INDENT_USING_TABS, CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, AUTO_PROPOSE, AUTO_PROPOSE_CODE, CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.ATTR_NAME_CASE,};
}
