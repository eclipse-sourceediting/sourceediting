/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;


public class JSONUIPreferenceNames {

	/**
	 * The key to store customized templates.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String TEMPLATES_KEY = "org.eclipse.wst.json.ui.custom_templates"; //$NON-NLS-1$

	/**
	 * <p>
	 * preference key used for saving which categories should not display on the
	 * default page
	 * </p>
	 *
	 * <p>
	 * Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s
	 * separated by the null character (<tt>\0</tt>), ordered is ignored
	 * </p>
	 */
	public static final String CONTENT_ASSIST_DO_NOT_DISPLAY_ON_DEFAULT_PAGE = "json_content_assist_display_on_default_page"; //$NON-NLS-1$

	/**
	 * <p>
	 * preference key used for saving which categories should not display on
	 * their own page
	 * </p>
	 *
	 * <p>
	 * Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s
	 * separated by the null character (<tt>\0</tt>), order is ignored
	 * </p>
	 */
	public static final String CONTENT_ASSIST_DO_NOT_DISPLAY_ON_OWN_PAGE = "json_content_assist_display_on_own_page"; //$NON-NLS-1$

	/**
	 * <p>
	 * preference key for saving the sort order of the categories when
	 * displaying them on their own page
	 * </p>
	 *
	 * <p>
	 * Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s
	 * separated by the null character (<tt>\0</tt>) in the desired sort order.
	 * </p>
	 */
	public static final String CONTENT_ASSIST_OWN_PAGE_SORT_ORDER = "json_content_assist_own_page_sort_order"; //$NON-NLS-1$

	/**
	 * <p>
	 * preference key for saving the sort order of the categories when
	 * displaying them on the default page
	 * </p>
	 *
	 * <p>
	 * Value is of type {@link String} consisting of
	 * <tt>org.eclipse.wst.sse.ui.completionProposal/proposalCategory/@id</tt>s
	 * separated by the null character (<tt>\0</tt>) in the desired sort order.
	 * </p>
	 */
	public static final String CONTENT_ASSIST_DEFAULT_PAGE_SORT_ORDER = "json_content_assist_default_page_sort_order"; //$NON-NLS-1$

	public final static String SUGGESTION_STRATEGY_VALUE_LAX = "Lax"; //$NON-NLS-1$
	public final static String SUGGESTION_STRATEGY_VALUE_STRICT = "Strict"; //$NON-NLS-1$

	/**
	 * A named preference that holds the characters that auto activate code
	 * assist.
	 * <p>
	 * Value is of type <code>String</code>. All characters that trigger auto
	 * code assist.
	 * </p>
	 */
	public static final String SUGGESTION_STRATEGY = getSuggestionStrategeyKey();

	private static String getSuggestionStrategeyKey() {
		return "suggestionStrategy";//$NON-NLS-1$
	}

	/**
	 * A named preference that controls time before code assist gets auto
	 * activated.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String AUTO_PROPOSE_DELAY = "autoProposeDelay";//$NON-NLS-1$

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
	 * Value is of type <code>String</code>. All characters that trigger auto
	 * code assist.
	 * </p>
	 */
	public static final String AUTO_PROPOSE_CODE = getAutoProposeCodeKey();

	private static String getAutoProposeCodeKey() {
		return "autoProposeCode";//$NON-NLS-1$
	}

	/**
	 * <p>
	 * preference key to store the option for auto insertion of single
	 * suggestions
	 * </p>
	 * <p>
	 * Value is of type <code>boolean</code>
	 * </p>
	 */
	public static final String INSERT_SINGLE_SUGGESTION = "insertSingleSuggestion"; //$NON-NLS-1$

}
