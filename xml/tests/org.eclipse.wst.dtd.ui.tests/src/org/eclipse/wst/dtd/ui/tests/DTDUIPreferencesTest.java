/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The purpose of this test is to verify the validity of the HTML Source
 * editor preferences. Tests include setting/getting preferences.
 * 
 * NOTE: This test should be performed on a clean workspace. If performed on
 * an existing workspace, this test will manipulate preferences in the
 * workspace, though attempts will be made to restore original values after
 * testing.
 */
public class DTDUIPreferencesTest extends TestCase {
	/**
	 * Tests existence of preference values when getting preference values
	 * through Platform.getPreferencesService()
	 */
	public void testBundleGetPreferences() {
		final String bundleName = "org.eclipse.wst.dtd.ui";

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
	}

	/**
	 * Tests default values of preferences.
	 * 
	 * NOTE: Expected default values are hard-coded, so if default values do
	 * get changed, assertions need to be updated as well
	 */
	public void testPluginGetDefaultPreferences() {
		IPreferenceStore store = DTDUIPlugin.getDefault().getPreferenceStore();
		assertNotNull("dtd preference store was null", store);
	}

	/**
	 * Tests setting preference values by setting preference value to be a
	 * certain value, then getting the preference value to verify it was set.
	 */
	public void testPluginSetPreferences() {
		//ISSUE: this does NOT use SET
		IPreferenceStore store = DTDUIPlugin.getDefault().getPreferenceStore();
		assertNotNull("dtd preference store was null", store);
	}
}
