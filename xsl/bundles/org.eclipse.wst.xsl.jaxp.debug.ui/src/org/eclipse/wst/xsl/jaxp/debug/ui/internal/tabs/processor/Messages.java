package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
