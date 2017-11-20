/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.html.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.preferences.HTMLUIPreferenceNames;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The purpose of this test is to verify the validity of the HTML Source
 * editor preferences. Tests include setting/getting preferences.
 * 
 * NOTE: This test should be preformed on a clean workspace. If performed on
 * an existing workspace, this test will manipulate preferences in the
 * workspace, though attempts will be made to restore original values after
 * testing.
 */
public class HTMLUIPreferencesTest extends TestCase {
	/**
	 * Tests existance of preference values when getting preference values
	 * through Platform.getPreferencesService()
	 */
	public void testBundleGetPreferences() {
		final String bundleName = "org.eclipse.wst.html.ui";

		// need to start up bundle for default values to be loaded
		Bundle bundle = Platform.getBundle(bundleName);
		try {
			if (bundle != null)
				bundle.start();
			else
				fail("Get preference value failed because could not find bundle: " + bundleName);
		} catch (BundleException e) {
			fail("Get preference value failed because of exception starting bundle: " + bundleName + " exception: " + e);
		}

		bundleGetPreference(bundleName, HTMLUIPreferenceNames.AUTO_PROPOSE);
		bundleGetPreference(bundleName, HTMLUIPreferenceNames.AUTO_PROPOSE_CODE);
	}

	private void bundleGetPreference(String bundleName, String prefKey) {
		String defaultValue = Long.toString(System.currentTimeMillis()); // random
																			// string

		String value = Platform.getPreferencesService().getString(bundleName, prefKey, defaultValue, null);
		assertNotSame("Get preference value failed using Platform.getPreferencesService. Key: " + prefKey, defaultValue, value);
	}

	/**
	 * Tests default values of preferences.
	 * 
	 * NOTE: Expected default values are hard-coded, so if default values do
	 * get changed, assertions need to be updated as well
	 */
	public void testPluginGetDefaultPreferences() {
		IPreferenceStore store = HTMLUIPlugin.getDefault().getPreferenceStore();

		pluginGetDefaultPreference(store, HTMLUIPreferenceNames.AUTO_PROPOSE, Boolean.toString(true));
		pluginGetDefaultPreference(store, HTMLUIPreferenceNames.AUTO_PROPOSE_CODE, "<=");
	}

	private void pluginGetDefaultPreference(IPreferenceStore store, String prefKey, String expected) {
		String defaultValue = store.getDefaultString(prefKey);
		assertEquals("Get default preference value failed using plugin.getPreferenceStore. Key: " + prefKey, expected, defaultValue);
	}

	/**
	 * Tests setting preference values by setting preference value to be a
	 * certain value, then getting the preference value to verify it was set.
	 */
	public void testPluginSetPreferences() {
		IPreferenceStore store = HTMLUIPlugin.getDefault().getPreferenceStore();

		pluginSetPreferenceBoolean(store, HTMLUIPreferenceNames.AUTO_PROPOSE);
		pluginSetPreferenceString(store, HTMLUIPreferenceNames.AUTO_PROPOSE_CODE);
	}

	private void pluginSetPreferenceBoolean(IPreferenceStore store, String prefKey) {
		boolean originalValue = store.getBoolean(prefKey);
		boolean expectedValue = !originalValue;
		store.setValue(prefKey, expectedValue);
		boolean foundValue = store.getBoolean(prefKey);
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		store.setValue(prefKey, originalValue);
	}

	private void pluginSetPreferenceString(IPreferenceStore store, String prefKey) {
		String originalValue = store.getString(prefKey);
		String expectedValue = Long.toString(System.currentTimeMillis()); // random
																			// string
		store.setValue(prefKey, expectedValue);
		String foundValue = store.getString(prefKey);
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		store.setValue(prefKey, originalValue);
	}
}
