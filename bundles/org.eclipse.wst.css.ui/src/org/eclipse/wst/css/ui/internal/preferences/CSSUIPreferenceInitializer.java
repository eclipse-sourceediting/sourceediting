package org.eclipse.wst.css.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ui.PlatformUI;
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
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		
		// CSS Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsCSS.NORMAL, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.ATMARK_RULE, 63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.ATMARK_RULE, styleValue);
		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.SELECTOR, 63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.SELECTOR, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.MEDIA, 42, 0, 225) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.MEDIA, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.COMMENT, 63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.COMMENT, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.PROPERTY_NAME, 127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(IStyleConstantsCSS.PROPERTY_NAME, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.PROPERTY_VALUE, 42, 0, 225) + JUSTITALIC;
		store.setDefault(IStyleConstantsCSS.PROPERTY_VALUE, styleValue);
		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.URI, 42, 0, 225) + JUSTITALIC;
		store.setDefault(IStyleConstantsCSS.URI, styleValue);
		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.STRING, 42, 0, 225) + JUSTITALIC;
		store.setDefault(IStyleConstantsCSS.STRING, styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(IStyleConstantsCSS.COLON, styleValue);
		store.setDefault(IStyleConstantsCSS.SEMI_COLON, styleValue);
		store.setDefault(IStyleConstantsCSS.CURLY_BRACE, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsCSS.ERROR, 191, 63, 63) + NOBACKGROUNDBOLD;
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
