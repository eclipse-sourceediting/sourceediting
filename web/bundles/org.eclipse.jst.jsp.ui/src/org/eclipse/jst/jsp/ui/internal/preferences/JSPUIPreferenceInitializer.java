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
package org.eclipse.jst.jsp.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.style.IStyleConstantsJSP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.ui.internal.style.IStyleConstantsHTML;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.preferences.AppearancePreferenceNames;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

/**
 * Sets default values for JSP UI preferences
 */
public class JSPUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JSPUIPlugin.getDefault().getPreferenceStore();
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();

		// setting the same as HTML
		store.setDefault(JSPUIPreferenceNames.AUTO_PROPOSE, true);
		store.setDefault(JSPUIPreferenceNames.AUTO_PROPOSE_CODE, "<=");//$NON-NLS-1$

		// JSP Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.TAG_ATTRIBUTE_NAME, 127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, 42, 0, 255) + JUSTITALIC;
		store.setDefault(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$

		// specified value is black; leaving as widget default
		store.setDefault(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.COMMENT_BORDER, 63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsXML.COMMENT_BORDER, styleValue);
		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.COMMENT_TEXT, 63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsXML.COMMENT_TEXT, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.TAG_BORDER, 0, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsXML.TAG_BORDER, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXML.TAG_NAME, 63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsXML.TAG_NAME, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsHTML.SCRIPT_AREA_BORDER, 191, 95, 63) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsHTML.SCRIPT_AREA_BORDER, styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsJSP.JSP_CONTENT, styleValue);
		
		// set default new jsp file template to use in new file wizard
		/*
		 * Need to find template name that goes with default template id (name
		 * may change for different language)
		 */
		store.setDefault(JSPUIPreferenceNames.NEW_FILE_TEMPLATE_ID, "org.eclipse.jst.jsp.ui.templates.jsphtml5"); //$NON-NLS-1$
		
		// set default new jsp tag file template to use in new tag file wizard
		store.setDefault(JSPUIPreferenceNames.NEW_TAG_FILE_TEMPLATE_ID, "org.eclipse.jst.jsp.ui.templates.simpletag"); //$NON-NLS-1$
		
		store.setDefault(JSPUIPreferenceNames.TYPING_COMPLETE_EL_BRACES, true);
		store.setDefault(JSPUIPreferenceNames.TYPING_COMPLETE_SCRIPTLETS, true);
		store.setDefault(JSPUIPreferenceNames.TYPING_COMPLETE_COMMENTS, true);
		store.setDefault(JSPUIPreferenceNames.SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH, true);

		/*
		String strategy = Platform.getProduct().getProperty(IProductConstants.DEFAULT_JSP_STRATUM_PATH_STRATEGY);
		if (strategy != null) {
			switch (strategy) {
				case "simple" : {
					store.setDefault(JSPUIPreferenceNames.STORE_PATH_IN_BREAKPOINTS, false);
				}
				case "deployment" :
				default :
					store.setDefault(JSPUIPreferenceNames.STORE_PATH_IN_BREAKPOINTS, true);
			}
		}
		else {
			store.setDefault(JSPUIPreferenceNames.STORE_PATH_IN_BREAKPOINTS, false);
		}
		*/

		store.setDefault(JSPUIPreferenceNames.TYPING_CLOSE_STRINGS, true);
		store.setDefault(JSPUIPreferenceNames.TYPING_CLOSE_BRACKETS, true);
		
		// Defaults for Content Assist preference page
		store.setDefault(JSPUIPreferenceNames.CONTENT_ASSIST_DO_NOT_DISPLAY_ON_DEFAULT_PAGE, "");
		store.setDefault(JSPUIPreferenceNames.CONTENT_ASSIST_DO_NOT_DISPLAY_ON_OWN_PAGE, "");
		store.setDefault(JSPUIPreferenceNames.CONTENT_ASSIST_DEFAULT_PAGE_SORT_ORDER,
				"org.eclipse.wst.html.ui.proposalCategory.htmlTags\0" +
				"org.eclipse.wst.xml.ui.proposalCategory.xmlTags\0" +
				"org.eclipse.wst.css.ui.proposalCategory.css\0" +
				"org.eclipse.jst.jsp.ui.proposalCategory.jsp\0" +
				"org.eclipse.jst.jsp.ui.proposalCategory.jspJava\0" +
				"org.eclipse.jst.jsp.ui.proposalCategory.jspTemplates\0" +
				"org.eclipse.wst.html.ui.proposalCategory.htmlTemplates\0" +
				"org.eclipse.wst.xml.ui.proposalCategory.xmlTemplates\0" +
				"org.eclipse.wst.css.ui.proposalCategory.cssTemplates");
		store.setDefault(JSPUIPreferenceNames.CONTENT_ASSIST_OWN_PAGE_SORT_ORDER,
				"org.eclipse.jst.jsp.ui.proposalCategory.jspTemplates\0" +
				"org.eclipse.wst.html.ui.proposalCategory.htmlTemplates\0" +
				"org.eclipse.wst.xml.ui.proposalCategory.xmlTemplates\0" +
				"org.eclipse.wst.css.ui.proposalCategory.cssTemplates\0" +
				"org.eclipse.wst.html.ui.proposalCategory.htmlTags\0" +
				"org.eclipse.wst.xml.ui.proposalCategory.xmlTags\0" +
				"org.eclipse.jst.jsp.ui.proposalCategory.jsp\0" +
				"org.eclipse.jst.jsp.ui.proposalCategory.jspJava\0" +
				"org.eclipse.wst.css.ui.proposalCategory.css");
		store.setDefault(JSPUIPreferenceNames.AUTO_IMPORT_INSERT, true);
		store.setDefault(JSPUIPreferenceNames.INSERT_SINGLE_SUGGESTION, true);
		store.setDefault(JSPUIPreferenceNames.USE_HTML_FORMATTER, true);

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
