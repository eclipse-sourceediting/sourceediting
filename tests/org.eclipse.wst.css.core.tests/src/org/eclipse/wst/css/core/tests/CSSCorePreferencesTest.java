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
package org.eclipse.wst.css.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The purpose of this test is to verify the validity of the XML Source
 * editor preferences. Tests include setting/getting preferences.
 * 
 * NOTE: This test should be preformed on a clean workspace. If performed on
 * an existing workspace, this test will manipulate preferences in the
 * workspace, though attempts will be made to restore original values after
 * testing.
 */
public class CSSCorePreferencesTest extends TestCase {
	/**
	 * Tests existance of preference values when getting preference values
	 * through Platform.getPreferencesService()
	 */
	public void testBundleGetPreferences() {
		final String bundleName = "org.eclipse.wst.xml.core";

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

		bundleGetPreference(bundleName, CSSCorePreferenceNames.INDENTATION_SIZE);
		bundleGetPreference(bundleName, CSSCorePreferenceNames.LINE_WIDTH);
	}

	private void bundleGetPreference(String bundleName, String prefKey) {
		int defaultValue = -1;

		int value = Platform.getPreferencesService().getInt(bundleName, prefKey, defaultValue, null);
		assertTrue("Get preference value failed using Platform.getPreferencesService. Key: " + prefKey, defaultValue != value);
	}

	/**
	 * Tests default values of preferences.
	 * 
	 * NOTE: Expected default values are hard-coded, so if default values do
	 * get changed, assertions need to be updated as well
	 */
	public void testPluginGetDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(CSSCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginGetDefaultPreference(node, CSSCorePreferenceNames.CASE_IDENTIFIER, "2");
		pluginGetDefaultPreference(node, CSSCorePreferenceNames.INDENTATION_CHAR, CSSCorePreferenceNames.TAB);
	}

	private void pluginGetDefaultPreference(IEclipsePreferences node, String prefKey, String expected) {
		String defaultValue = Long.toString(System.currentTimeMillis()); // random
		// string
		
		String theDefaultValue = node.get(prefKey, defaultValue);
		assertEquals("Get default preference value failed using plugin.getPreferenceStore. Key: " + prefKey, expected, theDefaultValue);
	}

	/**
	 * Tests setting preference values by setting preference value to be a
	 * certain value, then getting the preference value to verify it was set.
	 */
	public void testPluginSetPreferences() {
		IEclipsePreferences node = new InstanceScope().getNode(CSSCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginSetPreferenceBoolean(node, CSSCorePreferenceNames.WRAPPING_ONE_PER_LINE);
		pluginSetPreferenceInt(node, CSSCorePreferenceNames.CASE_PROPERTY_NAME);
	}

	private void pluginSetPreferenceBoolean(IEclipsePreferences node, String prefKey) {
		boolean originalValue = node.getBoolean(prefKey, false);
		boolean expectedValue = !originalValue;
		node.putBoolean(prefKey, expectedValue);
		boolean foundValue = node.getBoolean(prefKey, true);
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		node.putBoolean(prefKey, originalValue);
	}

	private void pluginSetPreferenceInt(IEclipsePreferences node, String prefKey) {
		int originalValue = node.getInt(prefKey, -999);
		int expectedValue = -888;
		node.putInt(prefKey, expectedValue);
		int foundValue = node.getInt(prefKey, -999);
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		node.putInt(prefKey, originalValue);
	}
}
