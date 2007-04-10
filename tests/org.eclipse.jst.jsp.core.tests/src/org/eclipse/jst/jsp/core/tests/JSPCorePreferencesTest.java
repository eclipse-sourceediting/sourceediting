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
package org.eclipse.jst.jsp.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.encoding.JSPDocumentLoader;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.encoding.ContentBasedPreferenceGateway;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.prefs.Preferences;

/**
 * The purpose of this test is to verify the validity of the JSP Source editor
 * preferences. Tests include setting/getting preferences.
 * 
 * NOTE: This test should be preformed on a clean workspace. If performed on
 * an existing workspace, this test will manipulate preferences in the
 * workspace, though attempts will be made to restore original values after
 * testing.
 */
public class JSPCorePreferencesTest extends TestCase {
	/**
	 * Tests existance of preference values when getting preference values
	 * through Platform.getPreferencesService()
	 */
	public void testBundleGetPreferences() {
		final String bundleName = "org.eclipse.jst.jsp.core";

		// need to start up bundle for default values to be loaded
		Bundle bundle = Platform.getBundle(bundleName);
		try {
			if (bundle != null)
				bundle.start();
			else
				fail("Get preference value failed because could not find bundle: " + bundleName);
		}
		catch (BundleException e) {
			fail("Get preference value failed because of exception starting bundle: " + bundleName + " exception: " + e);
		}

		bundleGetPreference(bundleName, JSPCorePreferenceNames.DEFAULT_EXTENSION);
	}

	private void bundleGetPreference(String bundleName, String prefKey) {
		String defaultValue = null;

		String value = Platform.getPreferencesService().getString(bundleName, prefKey, defaultValue, null);
		assertTrue("Get preference value failed using Platform.getPreferencesService. Key: " + prefKey, defaultValue != value);
	}

	/**
	 * Tests default values of preferences.
	 * 
	 * NOTE: Expected default values are hard-coded, so if default values do
	 * get changed, assertions need to be updated as well
	 */
	public void testPluginGetDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginGetDefaultPreference(node, JSPCorePreferenceNames.VALIDATE_FRAGMENTS, Boolean.toString(true));
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
		IEclipsePreferences node = new InstanceScope().getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginSetPreferenceBoolean(node, JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
	}

	private void pluginSetPreferenceBoolean(IEclipsePreferences node, String prefKey) {
		boolean originalValue = false;
		boolean expectedValue = true;

		String originalString = node.get(prefKey, "bogus");
		if (!"bogus".equals(originalString)) {
			originalValue = Boolean.valueOf(originalString).booleanValue();
			expectedValue = !originalValue;
		}
		node.putBoolean(prefKey, expectedValue);
		boolean foundValue = node.getBoolean(prefKey, true);
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		if ("bogus".equals(originalString))
			node.remove(prefKey);
		else
			node.put(prefKey, originalString);
	}

	/**
	 * Tests line delimiter preferences by making sure document created
	 * follows line delimiter preference.
	 */
	public void testDelimiterPreferences() {
		// check if content type preferences match
		String preferredDelimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForJSP.ContentTypeID_JSP);
		Preferences prefs = ContentBasedPreferenceGateway.getPreferences(ContentTypeIdForJSP.ContentTypeID_JSP);
		String gatewayDelimiter = prefs.get(CommonEncodingPreferenceNames.END_OF_LINE_CODE, null);
		assertEquals("ContentTypeEncodingPreferences and ContentBasedPreferenceGateway preferences do not match", gatewayDelimiter, preferredDelimiter);

		// set a particular line delimiter
		prefs.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, CommonEncodingPreferenceNames.LF);

		// create document
		JSPDocumentLoader loader = new JSPDocumentLoader();
		IEncodedDocument document = loader.createNewStructuredDocument();
		String documentDelimiter = document.getPreferredLineDelimiter();

		// verify delimiter in document matches preference
		assertEquals("Delimiter in document does not match preference", CommonEncodingPreferenceNames.STRING_LF, documentDelimiter);

		// return to original preference
		prefs.remove(CommonEncodingPreferenceNames.END_OF_LINE_CODE);
	}
}
