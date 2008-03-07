package org.eclipse.wst.xsl.internal.ui.contentassist;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author dcarver
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.bpel.ui.contentassist.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * TODO:  Add Javadoc
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
