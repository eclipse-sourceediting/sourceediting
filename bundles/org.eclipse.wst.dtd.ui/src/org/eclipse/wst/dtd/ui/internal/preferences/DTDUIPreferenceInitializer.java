package org.eclipse.wst.dtd.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.style.IStyleConstantsDTD;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;

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

		// DTD Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String styleValue = ColorHelper.getColorString(0, 0, 0)
				+ NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_DEFAULT, styleValue); // black

		styleValue = ColorHelper.getColorString(63, 63, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_TAG, styleValue); // blue
		store.setDefault(IStyleConstantsDTD.DTD_TAGNAME, styleValue); // blue

		styleValue = ColorHelper.getColorString(127, 127, 127)
				+ NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_COMMENT, styleValue); // grey

		styleValue = ColorHelper.getColorString(128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_KEYWORD, styleValue); // dark
		// red

		styleValue = ColorHelper.getColorString(63, 159, 95) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_STRING, styleValue); // green

		styleValue = ColorHelper.getColorString(191, 95, 95) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_DATA, styleValue); // light
		// red

		styleValue = ColorHelper.getColorString(128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsDTD.DTD_SYMBOL, styleValue); // dark
		// red
	}
}
