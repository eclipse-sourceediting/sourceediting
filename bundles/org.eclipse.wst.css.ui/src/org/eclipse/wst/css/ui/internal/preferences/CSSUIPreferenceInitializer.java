package org.eclipse.wst.css.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.style.IStyleConstantsCSS;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

/**
 * Sets default values for CSS UI preferences
 */
public class CSSUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CSSUIPlugin.getDefault().getPreferenceStore();
		// CSS Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsCSS.NORMAL, styleValue);

		styleValue = ColorHelper.getColorString(63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.ATMARK_RULE, styleValue);
		store.setDefault(IStyleConstantsCSS.SELECTOR, styleValue);

		styleValue = ColorHelper.getColorString(42, 0, 225) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.MEDIA, styleValue);

		styleValue = ColorHelper.getColorString(63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.COMMENT, styleValue);

		styleValue = ColorHelper.getColorString(127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.PROPERTY_NAME, styleValue);

		styleValue = ColorHelper.getColorString(42, 0, 225) + JUSTITALIC;
		store.setDefault(IStyleConstantsCSS.PROPERTY_VALUE, styleValue);
		store.setDefault(IStyleConstantsCSS.URI, styleValue);
		store.setDefault(IStyleConstantsCSS.STRING, styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsCSS.COLON, styleValue);
		store.setDefault(IStyleConstantsCSS.SEMI_COLON, styleValue);
		store.setDefault(IStyleConstantsCSS.CURLY_BRACE, styleValue);

		styleValue = ColorHelper.getColorString(191, 63, 63) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.ERROR, styleValue);
		
		// set default new css file template to use in new file wizard
		/*
		 * Need to find template name that goes with default template id (name
		 * may change for differnt language)
		 */
		String templateName = ""; //$NON-NLS-1$
		Template template = CSSUIPlugin.getDefault().getTemplateStore().findTemplateById("org.eclipse.wst.css.ui.internal.templates.newcss"); //$NON-NLS-1$
		if (template != null)
			templateName = template.getName();
		store.setDefault(CSSUIPreferenceNames.NEW_FILE_TEMPLATE_NAME, templateName);
	}

}
