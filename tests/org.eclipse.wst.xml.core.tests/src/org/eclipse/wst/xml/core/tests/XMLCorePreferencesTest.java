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
package org.eclipse.wst.xml.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.encoding.ContentBasedPreferenceGateway;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.prefs.Preferences;

/**
 * The purpose of this test is to verify the validity of the XML Source editor
 * preferences. Tests include setting/getting preferences.
 * 
 * NOTE: This test should be preformed on a clean workspace. If performed on
 * an existing workspace, this test will manipulate preferences in the
 * workspace, though attempts will be made to restore original values after
 * testing.
 */
public class XMLCorePreferencesTest extends TestCase {
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
		}
		catch (BundleException e) {
			fail("Get preference value failed because of exception starting bundle: " + bundleName + " exception: " + e);
		}

		bundleGetPreference(bundleName, XMLCorePreferenceNames.INDENTATION_SIZE);
		bundleGetPreference(bundleName, XMLCorePreferenceNames.LINE_WIDTH);
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
		IEclipsePreferences node = new DefaultScope().getNode(XMLCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginGetDefaultPreference(node, XMLCorePreferenceNames.SPLIT_MULTI_ATTRS, Boolean.toString(false));
		pluginGetDefaultPreference(node, XMLCorePreferenceNames.INDENTATION_CHAR, XMLCorePreferenceNames.TAB);
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
		IEclipsePreferences node = new InstanceScope().getNode(XMLCorePlugin.getDefault().getBundle().getSymbolicName());

		pluginSetPreferenceBoolean(node, XMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES);
		pluginSetPreferenceString(node, XMLCorePreferenceNames.INDENTATION_CHAR);
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

	private void pluginSetPreferenceString(IEclipsePreferences node, String prefKey) {
		String originalValue = node.get(prefKey, "bogus");
		String expectedValue = Long.toString(System.currentTimeMillis()); // random
		// string
		node.put(prefKey, expectedValue);
		String foundValue = node.get(prefKey, "");
		assertEquals("Set preference value failed using plugin.getPreferenceStore. Key: " + prefKey + "  expected: " + expectedValue + " found: " + foundValue, expectedValue, foundValue);

		// attempt to restore original preference value
		if ("bogus".equals(originalValue))
			node.remove(prefKey);
		else
			node.put(prefKey, originalValue);
	}

	/**
	 * Tests line delimiter preferences by making sure document created
	 * follows line delimiter preference.
	 */
	public void testDelimiterPreferences() {
		// check if content type preferences match
		String preferredDelimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForXML.ContentTypeID_XML);
		Preferences prefs = ContentBasedPreferenceGateway.getPreferences(ContentTypeIdForXML.ContentTypeID_XML);
		String gatewayDelimiter = prefs.get(CommonEncodingPreferenceNames.END_OF_LINE_CODE, null);
		assertEquals("ContentTypeEncodingPreferences and ContentBasedPreferenceGateway preferences do not match", gatewayDelimiter, preferredDelimiter);

		// set a particular line delimiter
		prefs.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, CommonEncodingPreferenceNames.LF);

		// create document
		XMLDocumentLoader loader = new XMLDocumentLoader();
		IEncodedDocument document = loader.createNewStructuredDocument();
		String documentDelimiter = document.getPreferredLineDelimiter();

		// verify delimiter in document matches preference
		assertEquals("Delimiter in document does not match preference", CommonEncodingPreferenceNames.STRING_LF, documentDelimiter);

		// return to original preference
		prefs.remove(CommonEncodingPreferenceNames.END_OF_LINE_CODE);
	}
}
