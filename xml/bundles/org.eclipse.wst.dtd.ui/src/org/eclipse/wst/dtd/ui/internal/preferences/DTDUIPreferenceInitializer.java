/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.style.IStyleConstantsDTD;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.preferences.AppearancePreferenceNames;

/**
 * Sets default values for DTD UI preferences
 */
public class DTDUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DTDUIPlugin.getDefault().getPreferenceStore();
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		
		// DTD Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_DEFAULT, 0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_DEFAULT, styleValue); // black

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_TAG, 63, 63, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_TAG, styleValue); // blue
		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_TAGNAME, 63, 63, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_TAGNAME, styleValue); // blue

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_COMMENT, 127, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_COMMENT, styleValue); // grey

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_KEYWORD, 128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_KEYWORD, styleValue); // dark
		// red

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_STRING, 63, 159, 95) + JUSTITALIC;
		store.setDefault(IStyleConstantsDTD.DTD_STRING, styleValue); // green

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_DATA, 191, 95, 95) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_DATA, styleValue); // light
		// red

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsDTD.DTD_SYMBOL, 128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_SYMBOL, styleValue); // dark
		// red

		// set default new xml file template to use in new file wizard
		/*
		 * Need to find template name that goes with default template id (name
		 * may change for differnt language)
		 */
		String templateName = ""; //$NON-NLS-1$
		Template template = DTDUIPlugin.getDefault().getTemplateStore().findTemplateById("org.eclipse.wst.dtd.ui.internal.templates.xmldeclaration"); //$NON-NLS-1$
		if (template != null)
			templateName = template.getName();
		store.setDefault(DTDUIPreferenceNames.NEW_FILE_TEMPLATE_NAME, templateName);
		
		store.setDefault(DTDUIPreferenceNames.ACTIVATE_PROPERTIES, true);

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

		// hover help preferences are not part of base text editor preference
		String mod2Name = Action.findModifierString(SWT.MOD2);
		/*
		 * SWT.MOD2 is currently SWT.COMMAND on Mac; SWT.CONTROL elsewhere
		 */
		store.setDefault(AppearancePreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS, "combinationHover|true|0;problemHover|false|0;documentationHover|false|0;annotationHover|true|" + mod2Name); //$NON-NLS-1$
	}
}
