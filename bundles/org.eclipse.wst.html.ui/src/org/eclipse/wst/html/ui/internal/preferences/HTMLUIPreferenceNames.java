/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.preferences;

/**
 * Preference keys for HTML UI
 */
public class HTMLUIPreferenceNames {
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
	 * The key to store the last template name used in new HTML file wizard.
	 * Template name is stored instead of template id because user-created
	 * templates do not have template ids.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String NEW_FILE_TEMPLATE_NAME = "newFileTemplateName"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for auto-completing comments while
	 * typing.
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_COMMENTS = "completeComments"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for auto-completing end-tags after entering
	 * <code>&lt;/</code>
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_END_TAGS = "completeEndTags"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for auto-completing the element after entering
	 * <code>&gt;</code>
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_COMPLETE_ELEMENTS = "completeElements"; //$NON-NLS-1$
	
	/**
	 * The key to store the option for removing an end-tag if the start tag is
	 * converted to an empty-tag.
	 * <p>
	 * Value is of type <code>boolean</code>.
	 * </p>
	 */
	public static final String TYPING_REMOVE_END_TAGS = "removeEndTags"; //$NON-NLS-1$

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
}
