/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.other;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.core.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.tests.Logger;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.html.core.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.sse.core.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.xml.core.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author amywu
 *
 * The purpose of this test is to mainly verify the validity of 
 * the CommonPreferences.  Tests include setting/getting preferences
 * for various content types and checking for existing and nonexisting
 * preferences.
 * 
 * NOTE: This test should be preformed on a clean workspace. 
 * If performed on an existing workspace, this test will manipulate 
 * preferences in the workspace. Effort was made to restore 
 * everything back to the way they were in existing pref.ini 
 * files. However, this test may create new pref.ini files 
 * if they don't already exist.
 * 
 * # of Preference Tests: 19
 */
public class PreferencesTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PreferencesTest.class);
	}
	private String fakeContentType = "fake";
	private String fakeContentType2 = "fake2";

	/**
	 * Constructor for PreferencesTest.
	 * @param name
	 */
	public PreferencesTest(String name) {
		super(name);
		initializeEditors();
	}
	
	protected void checkPreferenceStoreDefault(String contentType, String pref, String value) {
		IPreferenceStore store = getStoreForContentType(contentType);
		assertEquals("Could not find existing preference in store! Content type=" + contentType + " preference=" + pref, store.getString(pref), value);
	}

	protected void existsInEditorPreferenceStore(String contentType, String pref) {
		IPreferenceStore store = getStoreForContentType(contentType);			
		assertTrue("Could not find existing preference in store! Content type=" + contentType + " preference=" + pref, (store.contains(pref)));
	}

	/**
	 * Tests searching for an existing preferences
	 */
	protected void existsInModelPreference(String contentType, String pref) {
		Preferences prefs = getPluginPreferencesForContentType(contentType);
		assertTrue("Could not find existing preference! Content type=" + contentType + " preference=" + pref, (prefs.contains(pref)));
	}

	protected void getDefaultEditorPreferenceBoolean(String contentType, String pref, boolean expected) {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		boolean val = store.getDefaultBoolean(PreferenceKeyGenerator.generateKey(pref, contentType));
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + pref + " expected: " + expected + " found: " + val, (val == expected));
	}

	protected void getDefaultEditorPreferenceInt(String contentType, String pref, int expected) {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		int val = store.getDefaultInt(PreferenceKeyGenerator.generateKey(pref, contentType));
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + pref + " expected: " + expected + " found: " + val, (val == expected));
	}

	// editor prefs (use PreferenceStore)
	protected void getDefaultEditorPreferenceString(String contentType, String pref, String expected) {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		String val = store.getDefaultString(PreferenceKeyGenerator.generateKey(pref, contentType));
		assertEquals("Get default pref failed! Content type=" + contentType + " preference=" + pref, val, expected);
	}

	protected void getDefaultModelPreferenceBoolean(String contentType, Preferences prefs, String pref, boolean expected) {
		boolean val = prefs.getDefaultBoolean(pref);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + pref + " expected: " + expected + " found: " + val, (val == expected));
	}

	protected void getDefaultModelPreferenceInt(String contentType, Preferences prefs, String pref, int expected) {
		int val = prefs.getDefaultInt(pref);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + pref + " expected: " + expected + " found: " + val, (val == expected));
	}

	/**
	 * Tests getting default preferences (String)
	 */
	// model prefs (use Preferences)
	protected void getDefaultModelPreferenceString(String contentType, Preferences prefs, String pref, String expected) {
		String val = prefs.getDefaultString(pref);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + pref + " expected: " + expected + " found: " + val, (val.equals(expected)));
	}

	protected void getDefaultPreferenceStoreBoolean(String contentType, String pref, boolean expected) {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		String genKey = PreferenceKeyGenerator.generateKey(pref, contentType);
		boolean val = store.getDefaultBoolean(genKey);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + genKey + " expected: " + expected + " found: " + val, (val == expected));
	}

	protected void getDefaultPreferenceStoreInt(String contentType, String pref, int expected) {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		String genKey = PreferenceKeyGenerator.generateKey(pref, contentType);
		int val = store.getDefaultInt(genKey);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + genKey + " expected: " + expected + " found: " + val, (val == expected));
	}

	protected void getHtmlEditorPreferenceStoreString(String contentType, String pref, String expected) {
		IPreferenceStore store = HTMLUIPlugin.getDefault().getPreferenceStore();
		String genKey = PreferenceKeyGenerator.generateKey(pref, contentType);
		String val = store.getDefaultString(genKey);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + genKey + " expected: " + expected + " found: " + val, (val.equals(expected)));
	}

	private Preferences getPluginPreferencesForContentType(String contentTypeId) {
		if(contentTypeId.equals(ContentTypeIdForCSS.ContentTypeID_CSS))
			return CSSCorePlugin.getDefault().getPluginPreferences();
//		if(contentTypeId.equals(IContentTypeIdentifier.ContentTypeID_DTD))
//			return DTDCorePlugin.getDefault().getPluginPreferences();
		if(contentTypeId.equals(ContentTypeIdForXML.ContentTypeID_XML) || contentTypeId.equals(ContentTypeIdForXML.ContentTypeID_SSEXML))
			return CSSCorePlugin.getDefault().getPluginPreferences();
		if(contentTypeId.equals(ContentTypeIdForJSP.ContentTypeID_JSP))
			return JSPCorePlugin.getDefault().getPluginPreferences();
		if(contentTypeId.equals(ContentTypeIdForHTML.ContentTypeID_HTML))
			return HTMLCorePlugin.getDefault().getPluginPreferences();
		return SSECorePlugin.getDefault().getPluginPreferences();
	}
	
	private IPreferenceStore getStoreForContentType(String contentTypeId) {
		if(contentTypeId.equals(ContentTypeIdForCSS.ContentTypeID_CSS))
			return CSSUIPlugin.getDefault().getPreferenceStore();
//		if(contentTypeId.equals(IContentTypeIdentifier.ContentTypeID_DTD))
//			return DTDUIPlugin.getDefault().getPreferenceStore();
		if(contentTypeId.equals(ContentTypeIdForXML.ContentTypeID_XML) || contentTypeId.equals(ContentTypeIdForXML.ContentTypeID_SSEXML))
			return XMLUIPlugin.getDefault().getPreferenceStore();
		if(contentTypeId.equals(ContentTypeIdForJSP.ContentTypeID_JSP))
			return JSPUIPlugin.getDefault().getPreferenceStore();
		if(contentTypeId.equals(ContentTypeIdForHTML.ContentTypeID_HTML))
			return HTMLUIPlugin.getDefault().getPreferenceStore();
		return SSEUIPlugin.getDefault().getPreferenceStore();
	}


	// editor prefs (use IPreferenceStore)
	protected void getXmlEditorPreferenceStoreString(String contentType, String pref, String expected) {
		IPreferenceStore store = XMLUIPlugin.getDefault().getPreferenceStore();
		String genKey = PreferenceKeyGenerator.generateKey(pref, contentType);
		String val = store.getDefaultString(genKey);
		assertTrue("Get default pref failed! Content type=" + contentType + " preference=" + genKey + " expected: " + expected + " found: " + val, (val.equals(expected)));
	}

	/**
	 * Need to be initialized or else default preferences won't be there
	 */
	private void initializeEditors() {
		Bundle sseUI = Platform.getBundle("org.eclipse.wst.sse.ui");
		Bundle xmlUI = Platform.getBundle("org.eclipse.wst.xml.ui");
		Bundle dtdUI = Platform.getBundle("org.eclipse.wst.dtd.ui");
		Bundle cssUI = Platform.getBundle("org.eclipse.wst.css.ui");
		Bundle htmlUI = Platform.getBundle("org.eclipse.wst.html.ui");
		Bundle jspUI = Platform.getBundle("org.eclipse.jst.jsp.ui");
		try {
			sseUI.start();
			xmlUI.start();
			dtdUI.start();
			cssUI.start();
			htmlUI.start();
			jspUI.start();
		} catch (BundleException e) {
			Logger.logException(e);
		}
	}

	/**
	 * Tests searching for an nonexistant preference (int)
	 */
	protected void nonExistPreference(String contentType) {
		String testPref = "unitTest.nonExist";
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		assertTrue("Nonexistant preference already exists! Content type=" + contentType + " preference=" + testPref, !(store.contains(testPref)));
		int val = store.getInt(PreferenceKeyGenerator.generateKey(testPref, contentType));
		assertTrue("Get nonexistant preference failed! Content type=" + contentType + " preference=" + testPref + " expected: 0 found: " + val, (val == 0));
	}

	/**
	 * Tests setting a default preference then getting the preference (String)
	 */
	protected void setGetDefaultPreference(String contentType) {
		String defaultVal = "OK!";
		String testPref = "unitTest.defaultPref";
		IPreferenceStore store = getStoreForContentType(contentType);
		store.setDefault(testPref, defaultVal);
		assertTrue("Set default pref failed! Content type=" + contentType + " preference=" + testPref, store.contains(testPref));
		String val = store.getString(testPref);
		assertTrue("Set/get default pref for content type " + contentType + " preference " + testPref + " expected: " + defaultVal + " found: " + val, (val.equals(defaultVal)));
	}

	/**
	 * Tests getting default preferences (boolean)
	 */
	//	protected void getDefaultPreference(String contentType, String pref, boolean expected) {
	//		IPreferenceStore store = StructuredSourceEditorPlugin.getDefault().getPreferenceStore();
	//		boolean val = store.getDefaultBoolean(PreferenceKeyGenerator.generateKey(pref, contentType));
	//		assertTrue("Get default pref failed! Content type="+contentType+ " preference="+pref +" expected: "+expected +" found: "+val, (val == expected));
	//	}
	/**
	 * Tests getting default preferences (int)
	 */
	//	protected void getDefaultPreference(String contentType, String pref, int expected) {
	//		IPreferenceStore store = StructuredSourceEditorPlugin.getDefault().getPreferenceStore();
	//		int val = store.getDefaultInt(PreferenceKeyGenerator.generateKey(pref, contentType));
	//		assertTrue("Get default pref failed! Content type="+contentType+ " preference="+pref +" expected: "+expected +" found: "+val, (val == expected));
	//	}
	
	/**
	 * Tests setting then getting a preference (boolean)
	 */
	protected void setGetPreference(String contentType) {
		String testPref = "unitTest.setGetPref";
		boolean expectedVal = true;
		IPreferenceStore store = getStoreForContentType(contentType);
		store.setValue(testPref, expectedVal);
		assertTrue("Set pref failed! Content type=" + contentType + " preference=" + testPref, store.contains(testPref));
		boolean val = store.getBoolean(testPref);
		store.setToDefault(testPref); // remove this fake preference from store
		assertTrue("Get pref failed! Content type=" + contentType + " preference=" + testPref + " expected: " + expectedVal + " found: " + val, (val == expectedVal));
	}

	public void testExistPreferenceHTML() {
		existsInModelPreference(ContentTypeIdForHTML.ContentTypeID_HTML, CommonModelPreferenceNames.TAG_NAME_CASE);
		existsInModelPreference(ContentTypeIdForHTML.ContentTypeID_HTML, CommonModelPreferenceNames.TAB_WIDTH);
		existsInModelPreference(ContentTypeIdForHTML.ContentTypeID_HTML, CommonEncodingPreferenceNames.OUTPUT_CODESET);
		existsInEditorPreferenceStore(ContentTypeIdForHTML.ContentTypeID_HTML, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE);
	}

	// Test searching for an existing preference
	public void testExistPreferenceXML() {
		existsInModelPreference(ContentTypeIdForXML.ContentTypeID_SSEXML, CommonModelPreferenceNames.INDENT_USING_TABS);
		existsInModelPreference(ContentTypeIdForXML.ContentTypeID_SSEXML, CommonEncodingPreferenceNames.END_OF_LINE_CODE);
		existsInEditorPreferenceStore(ContentTypeIdForXML.ContentTypeID_SSEXML, CommonEditorPreferenceNames.AUTO_PROPOSE);
	}

	public void testGetDefaultPreferenceHTML() {
		String contentTypeId = ContentTypeIdForHTML.ContentTypeID_HTML;
		Preferences prefs = HTMLCorePlugin.getDefault().getPluginPreferences();
		getDefaultModelPreferenceInt(contentTypeId, prefs, CommonModelPreferenceNames.TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);
		checkPreferenceStoreDefault(contentTypeId, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, CommonEditorPreferenceNames.LT);
		//getHtmlEditorPreferenceStoreString(contentTypeId, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, CommonEditorPreferenceNames.LT);
		//		getDefaultPreference(ContentType.ContentTypeID_HTML, ICommonModelPreferenceNames.LINE_WIDTH, 72);
		//		getDefaultPreference(ContentType.ContentTypeID_HTML, HTMLFilesPreferenceNames.GENERATE_DOCUMENT_TYPE, true);
		//		getDefaultPreference(ContentType.ContentTypeID_HTML, ICommonModelPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
	}

	// Test getting default preferences
	public void testGetDefaultPreferenceXML() {
		String contentTypeId = ContentTypeIdForXML.ContentTypeID_XML;
		Preferences prefs = XMLCorePlugin.getDefault().getPluginPreferences();
		getDefaultModelPreferenceInt(contentTypeId, prefs, CommonModelPreferenceNames.TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);
		checkPreferenceStoreDefault(contentTypeId, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, CommonEditorPreferenceNames.LT);
		//getXmlEditorPreferenceStoreString(contentTypeId, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, CommonEditorPreferenceNames.LT);
		//		getDefaultPreference(ContentType.ContentTypeID_XML, ICommonModelPreferenceNames.TAB_WIDTH, 4);
		//		getDefaultPreference(ContentType.ContentTypeID_XML, ICommonModelPreferenceNames.SPLIT_LINES, false);
		//		getDefaultPreference(ContentType.ContentTypeID_XML, CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, ICommonModelPreferenceNames.LT);
	}

	public void testNonExistPreferenceFake() {
		nonExistPreference(fakeContentType);
	}

	// Test searching for a nonexisting preference
	public void testNonExistPreferenceXML() {
		nonExistPreference(ContentTypeIdForXML.ContentTypeID_SSEXML);
	}

	public void testSetGetDefaultPreferenceFake() {
		setGetDefaultPreference(fakeContentType);
	}

	public void testSetGetDefaultPreferenceHTML() {
		setGetDefaultPreference(ContentTypeIdForHTML.ContentTypeID_HTML);
	}

	// Uncomment when the content-type specific preferences are implemented
	//	public void testSetGetPreferenceXHTML() {
	//		setGetPreference(ContentTypeIds.XHTML_ID);
	//	}
	//	public void testSetGetPreferenceCSS() {
	//		setGetPreference(ContentTypeIds.CSS_ID);
	//	}
	//	public void testSetGetPreferenceDTD() {
	//		setGetPreference(ContentTypeIds.DTD_ID);
	//	}
	// Test setting then getting default preference
	public void testSetGetDefaultPreferenceSSEXML() {
		setGetDefaultPreference(ContentTypeIdForXML.ContentTypeID_SSEXML);
	}

	public void testSetGetDefaultPreferenceXML() {
		setGetDefaultPreference(ContentTypeIdForXML.ContentTypeID_XML);
	}

	public void testSetGetPreferenceFake() {
		setGetPreference(fakeContentType);
	}

	public void testSetGetPreferenceHTML() {
		setGetPreference(ContentTypeIdForHTML.ContentTypeID_HTML);
	}

	public void testSetGetPreferenceJSP() {
		setGetPreference(ContentTypeIdForJSP.ContentTypeID_JSP);
	}

	// Tests that getting a non-existant preference store returns XML preference store
	// TEST IS NOT VALID ANYMORE
	//	public void testGetPreferenceFake() {
	//		IPreferenceStore XMLstore = CommonPreferencesPlugin.getDefault().getPreferenceStore(ContentType.ContentTypeID_XML);
	//		IPreferenceStore fakeStore = CommonPreferencesPlugin.getDefault().getPreferenceStore(fakeContentType);
	//		assertTrue("Get non-existant pref failed! expected="+XMLstore+ " found="+fakeStore, (XMLstore == fakeStore));
	//	}
	// Tests creating a new preference store
	//	TEST IS NOT VALID ANYMORE
	//	public void testCreateNewPreferenceStore() {
	//		CommonPreferencesPlugin.getDefault().createPreferenceStore(fakeContentType2);
	//		IPreferenceStore fakeStore = CommonPreferencesPlugin.getDefault().getPreferenceStore(fakeContentType2);
	//		IPreferenceStore XMLstore = CommonPreferencesPlugin.getDefault().getPreferenceStore(ContentType.ContentTypeID_XML);
	//		assertTrue("Create new pref failed! Default XML pref store was retreived instead.", (XMLstore != fakeStore));
	//	}
	// Test setting then getting preference
	public void testSetGetPreferenceSSEXML() {
		setGetPreference(ContentTypeIdForXML.ContentTypeID_SSEXML);
	}

	public void testSetGetPreferenceXML() {
		setGetPreference(ContentTypeIdForXML.ContentTypeID_XML);
	}
}