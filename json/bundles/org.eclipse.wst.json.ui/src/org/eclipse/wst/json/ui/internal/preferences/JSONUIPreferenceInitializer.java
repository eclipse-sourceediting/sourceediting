/*******************************************************************************
 * Copyright (c) 2006, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceInitializer
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.style.IStyleConstantsJSON;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.preferences.AppearancePreferenceNames;

/**
 * Sets default values for JSON UI preferences
 */
public class JSONUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JSONUIPlugin.getDefault().getPreferenceStore();
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager()
				.getCurrentTheme().getColorRegistry();

		// JSON Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String JUSTBOLD = " | null | true"; //$NON-NLS-1$
		String styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsJSON.NORMAL, styleValue);

		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.COLON, 0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.COLON, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.CURLY_BRACE, 0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.CURLY_BRACE, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.COMMA, 0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.COMMA, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.COMMENT, 63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.COMMENT, styleValue);

		// JSON Key
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.OBJECT_KEY, 127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.OBJECT_KEY, styleValue);

		// JSON Value
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.VALUE_STRING, 42, 0, 255) + JUSTITALIC;
		store.setDefault(IStyleConstantsJSON.VALUE_STRING, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.VALUE_NUMBER, 255, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.VALUE_NUMBER, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.VALUE_BOOLEAN, 255, 140, 0)
				+ NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.VALUE_BOOLEAN, styleValue);
		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsJSON.VALUE_NULL, 150, 150, 150)
				+ NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsJSON.VALUE_NULL, styleValue);


		initAppearancePreferences(store, registry);
	}

	private void initAppearancePreferences(IPreferenceStore store, ColorRegistry registry) {
		/* these annotation preferences are not part of base text editor
		 preference */
		store.setDefault(AppearancePreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
		// matching brackets is not part of base text editor preference
		// set default enable folding value
		store.setDefault(AppearancePreferenceNames.FOLDING_ENABLED, true);
		
		// set default for show message dialog when unknown content type in editor
		store.setDefault(AppearancePreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG, false);

		store.setDefault(AppearancePreferenceNames.SEMANTIC_HIGHLIGHTING, true);

		// matching brackets enablement and color
		store.setDefault(AppearancePreferenceNames.MATCHING_BRACKETS, true);
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR, ColorHelper.findRGB(registry, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR, new RGB(192, 192, 192)));

		// set content assist defaults
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND, new RGB(255, 255, 255)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND, new RGB(0, 0, 0)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND, new RGB(255, 255, 255)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND, new RGB(0, 0, 0)));

		store.setDefault(JSONUIPreferenceNames.TYPING_CLOSE_BRACES, true);
		store.setDefault(JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS, false);
		store.setDefault(JSONUIPreferenceNames.TYPING_CLOSE_STRINGS, true);

		// hover help preferences are not part of base text editor preference
		String mod2Name = Action.findModifierString(SWT.MOD2);
		/*
		 * SWT.MOD2 is currently SWT.COMMAND on Mac; SWT.CONTROL elsewhere
		 */
		store.setDefault(AppearancePreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS, "combinationHover|true|0;problemHover|false|0;documentationHover|false|0;annotationHover|true|" + mod2Name); //$NON-NLS-1$
	}

}
