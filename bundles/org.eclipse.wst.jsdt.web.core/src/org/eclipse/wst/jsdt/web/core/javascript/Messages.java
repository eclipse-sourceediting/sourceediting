/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.javascript;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author childsb
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.wst.jsdt.web.core.internal.java.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
