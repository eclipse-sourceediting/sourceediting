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
package org.eclipse.wst.sse.ui.internal.preferences;

import org.eclipse.wst.sse.ui.preferences.AppearancePreferenceNames;

/**
 * Preference keys for SSE UI
 */
public class EditorPreferenceNames {
	/**
	 * @deprecated
	 */
	public static final String EDITOR_TEXT_HOVER_MODIFIERS = AppearancePreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS;

	/**
	 * A named preference that defines read only contrast scale.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public static final String READ_ONLY_FOREGROUND_SCALE = getReadOnlyForegroundScaleKey();

	private static String getReadOnlyForegroundScaleKey() {
		return "readOnlyForegroundScale"; //$NON-NLS-1$
	}

	/**
	 * A name preference that holds the auto activation delay time in
	 * milliseconds.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public final static String CODEASSIST_AUTOACTIVATION_DELAY = "content_assist_autoactivation_delay"; //$NON-NLS-1$
}
