package org.eclipse.wst.xsl.ui.internal.preferences;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO: Add Javadoc
 * @author dcarver
 *
 */
public class XSLPreferencesMessages {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.ui.internal.preferences.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private XSLPreferencesMessages() {
	}

	/**
	 * TODO: Add Javadoc
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
