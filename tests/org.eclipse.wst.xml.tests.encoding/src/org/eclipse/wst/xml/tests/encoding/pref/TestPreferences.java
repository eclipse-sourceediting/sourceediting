/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.pref;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.sse.core.internal.encoding.ContentBasedPreferenceGateway;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class TestPreferences extends TestCase {

	/**
	 * @param preferences
	 * @throws BackingStoreException
	 */
	private static void printChildren(Preferences preferences) throws BackingStoreException {
		System.out.println("\t" + preferences.absolutePath());
		String [] keys = preferences.keys();
		printKeys(keys);
		String[] children = preferences.childrenNames();
		printChildren(children); 
		for (int i = 0; i < children.length; i++) {
			String child = children[i];
			Preferences subPreferences = preferences.node(child);
			String [] subkeys = subPreferences.keys();
			System.out.println();
			System.out.println(child);
			System.out.println();
			printKeys(subkeys);
		}
	}
	static private void printChildren(String[] children) {
		printStringArray(children, "\t");
	}

	/**
	 * @param keys
	 */
	private static void printKeys(String[] keys) {
		printStringArray(keys, "\t\t");
		
	}

	static private void printStringArray(String[] array, String tabChars) {
		for (int i = 0; i < array.length; i++) {
			String string = array[i];
			System.out.println(tabChars + string);
		}
	}

	private boolean DEBUG = true;

	private void displayPreferenceTree() {
		IEclipsePreferences eclipsePreferences = Platform.getPreferencesService().getRootNode();


		try {
			String[] children = eclipsePreferences.childrenNames();
			System.out.println(eclipsePreferences.absolutePath());
			printChildren(children);

			for (int i = 0; i < children.length; i++) {
				String string = children[i];
				Preferences preferences = eclipsePreferences.node(string);
				printChildren(preferences);


			}

		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	public void testAccess() {
		if (DEBUG) { 
			displayPreferenceTree();
		}
		// always ok if no exceptions thrown
		assertTrue(true);
	}

	public void testContentBasedPrefHTML() {
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor("test.html");
		Preferences preferences = ContentBasedPreferenceGateway.getPreferences(contentType);
		assertNotNull(preferences);
	}

	public void testContentBasedPrefXML() {
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor("test.xml");
		Preferences preferences = ContentBasedPreferenceGateway.getPreferences(contentType);
		assertNotNull(preferences);
	}
}
