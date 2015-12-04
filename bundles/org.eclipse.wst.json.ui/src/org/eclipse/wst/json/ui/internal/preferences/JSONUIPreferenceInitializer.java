/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference 
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceInitializer
 *                                           modified in order to process JSON Objects.                         
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.style.IStyleConstantsJSON;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

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

	}

}
