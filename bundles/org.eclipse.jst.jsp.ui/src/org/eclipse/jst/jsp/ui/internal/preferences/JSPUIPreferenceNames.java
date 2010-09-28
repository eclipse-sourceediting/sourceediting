/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences;

/**
 * Preference keys for JSP UI
 */
public class JSPUIPreferenceNames {
	/**
	 * A named preference that controls if code assist gets auto activated.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String AUTO_PROPOSE = getAutoProposeKey();

	private static String getAutoProposeKey() {
		return "autoPropose";//$NON-NLS-1$
	}

	/**
	 * A named preference that holds the characters that auto activate code
	 * assist.
	 * <p>
	 * Value is of type <code>String</code>. All characters that trigger
	 * auto code assist.
	 * </p>
	 */
	public static final String AUTO_PROPOSE_CODE = getAutoProposeCodeKey();

	private static String getAutoProposeCodeKey() {
		return "autoProposeCode";//$NON-NLS-1$
	}

	/**
	 * The key to store customized templates.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String TEMPLATES_KEY = getTemplatesKey();

	private static String getTemplatesKey() {
		return "org.eclipse.wst.sse.ui.custom_templates"; //$NON-NLS-1$
	}

	/**
	 * The key to store the last template name used in new JSP file wizard.
	 * Template name is stored instead of template id because user-created
	 * templates do not have template ids.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String NEW_FILE_TEMPLATE_NAME = "newFileTemplateName"; //$NON-NLS-1$

	/**
	 * The initial template ID to be used in the new JSP file wizard. In the absence
	 * of {@link NEW_FILE_TEMPLATE_NAME}, this ID is used to find a template name
	 */
	public static final String NEW_FILE_TEMPLATE_ID = "newFileTemplateId"; //$NON-NLS-1$

	/**
	 * The key to store the last template name used in new JSP Tag file wizard.
	 * Template name is stored instead of template id because user-created
	 * templates do not have template ids.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String NEW_TAG_FILE_TEMPLATE_NAME = "newTagFileTemplateName"; //$NON-NLS-1$

	/**
	 * The initial template ID to be used in the new JSP file wizard. In the absence
	 * of {@link NEW_FILE_TEMPLATE_NAME}, this ID is used to find a template name
	 */
	public static final String NEW_TAG_FILE_TEMPLATE_ID = "newTagFileTemplateId"; //$NON-NLS-1$

	/**
	 * The key to store the option for auto-completing EL braces after entering
	 * <code>${</code>
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_EL_BRACES = "typingCompleteElBraces"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for auto-completing scriptlets after entering
	 * <code>&lt;%</code>
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_SCRIPTLETS = "typingCompleteScriptlets"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for auto-completing JSP comments after entering
	 * <code>&lt;%--</code>
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_COMMENTS = "typingCompleteComments"; //$NON-NLS-1$

	/**
	 * The key to store the option for auto-completing strings (" and ') while
	 * typing.
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_CLOSE_STRINGS = "closeStrings"; //$NON-NLS-1$

	/**
	 * The key to store the option for auto-completing brackets ([ and () while
	 * typing.
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_CLOSE_BRACKETS = "closeBrackets"; //$NON-NLS-1$

	public static final String SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH = "supplyJSPSearchResultsToJavaSearch"; //$NON-NLS-1$

	/**
	 * <p>preference key used for saving which categories should not display on the default page</p>
	 * 
	 * <p>Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s separated by the null
	 * character (<tt>\0</tt>), ordered is ignored</p>
	 */
	public static final String CONTENT_ASSIST_DO_NOT_DISPLAY_ON_DEFAULT_PAGE = "jsp_content_assist_display_on_default_page"; //$NON-NLS-1$
	
	/**
	 * <p>preference key used for saving which categories should not display on their own page</p>
	 * 
	 * <p>Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s separated by the null
	 * character (<tt>\0</tt>), order is ignored</p>
	 */
	public static final String CONTENT_ASSIST_DO_NOT_DISPLAY_ON_OWN_PAGE = "jsp_content_assist_display_on_own_page"; //$NON-NLS-1$
	
	/**
	 * <p>preference key for saving the sort order of the categories when displaying them on their own page</p>
	 * 
	 * <p>Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s separated by the null
	 * character (<tt>\0</tt>) in the desired sort order.</p>
	 */
	public static final String CONTENT_ASSIST_OWN_PAGE_SORT_ORDER= "jsp_content_assist_own_page_sort_order"; //$NON-NLS-1$
	
	/**
	 * <p>preference key for saving the sort order of the categories when displaying them on the default page</p>
	 * 
	 * <p>Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s separated by the null
	 * character (<tt>\0</tt>) in the desired sort order.</p>
	 */
	public static final String CONTENT_ASSIST_DEFAULT_PAGE_SORT_ORDER= "jsp_content_assist_default_page_sort_order"; //$NON-NLS-1$
	
	/**
	 * <p>preference key to store the option for auto insertion of page imports</p>
	 * 
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String AUTO_IMPORT_INSERT = "autoImportinsertion"; //$NON-NLS-1$
}
