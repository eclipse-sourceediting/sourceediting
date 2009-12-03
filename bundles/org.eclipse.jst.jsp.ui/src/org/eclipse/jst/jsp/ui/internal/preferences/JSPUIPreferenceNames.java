/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
}
