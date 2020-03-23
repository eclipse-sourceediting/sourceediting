/*******************************************************************************
 * Copyright (c) 2005, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.ui.preferences;
/**
 * @since 1.7
 */
public final class AppearancePreferenceNames {

	/**
	 * A named preference that holds the background color used for parameter
	 * hints.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String CODEASSIST_PARAMETERS_BACKGROUND = "content_assist_parameters_background"; //$NON-NLS-1$
	/**
	 * A named preference that holds the foreground color used in the code
	 * assist selection dialog.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String CODEASSIST_PARAMETERS_FOREGROUND = "content_assist_parameters_foreground"; //$NON-NLS-1$
	/**
	 * A named preference that holds the background color used in the code
	 * assist selection dialog.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String CODEASSIST_PROPOSALS_BACKGROUND = "content_assist_proposals_background"; //$NON-NLS-1$
	/**
	 * A named preference that holds the foreground color used in the code
	 * assist selection dialog.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String CODEASSIST_PROPOSALS_FOREGROUND = "content_assist_proposals_foreground"; //$NON-NLS-1$
	/**
	 * A named preference that controls on-the-fly validation
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EVALUATE_TEMPORARY_PROBLEMS = getEvaluateTemporaryProblemsKey();
	/**
	 * A named preference that controls whether folding is enabled in the
	 * Structured Text editor.
	 */
	public final static String FOLDING_ENABLED = "foldingEnabled"; //$NON-NLS-1$
	/**
	 * A named preference that controls whether bracket matching highlighting
	 * is turned on or off.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String MATCHING_BRACKETS = getMatchingBracketsKey();
	/**
	 * A named preference that holds the color used to highlight matching
	 * brackets.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 */
	public final static String MATCHING_BRACKETS_COLOR = getMatchingBracketsColorKey();
	/**
	 * A named preference that controls whether semantic highlighting is
	 * turned on or off
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SEMANTIC_HIGHLIGHTING = "semanticHighlighting"; //$NON-NLS-1$
	/**
	 * A named preference that defines whether or not to show a message dialog
	 * informing user of unknown content type in editor.
	 */
	public static final String SHOW_UNKNOWN_CONTENT_TYPE_MSG = "showUnknownContentTypeMsg"; //$NON-NLS-1$

	private static String getEvaluateTemporaryProblemsKey() {
		return "evaluateTemporaryProblems"; //$NON-NLS-1$
	}

	private static String getMatchingBracketsColorKey() {
		return "matchingBracketsColor"; //$NON-NLS-1$
	}

	private static String getMatchingBracketsKey() {
		return "matchingBrackets"; //$NON-NLS-1$
	}

}
