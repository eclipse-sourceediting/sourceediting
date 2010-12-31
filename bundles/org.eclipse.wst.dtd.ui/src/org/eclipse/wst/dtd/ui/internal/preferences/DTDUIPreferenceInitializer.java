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
package org.eclipse.wst.dtd.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.style.IStyleConstantsDTD;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

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
	}
}
