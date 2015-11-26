/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.format;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategyImpl;
import org.eclipse.wst.css.core.internal.cleanup.CleanupProcessorCSS;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.tests.util.StringCompareUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class TestCleanupProcessorCSS extends TestCase {
	private static final String UTF_8 = "UTF-8";
	private StringCompareUtil fStringCompareUtil;
	private CleanupProcessorCSS fCleanupProcessor;

	private boolean fOldClearBlankLinesPref;
	private int fOldMaxLineWidthPref;
	private String fOldIndentationCharPref;
	private int fOldIndentationSizePref;

	protected void setUp() throws Exception {
		// set up preferences
		Preferences prefs = CSSCorePlugin.getDefault().getPluginPreferences();
		fOldClearBlankLinesPref = prefs.getBoolean(CSSCorePreferenceNames.CLEAR_ALL_BLANK_LINES);
		fOldMaxLineWidthPref = prefs.getInt(CSSCorePreferenceNames.LINE_WIDTH);
		fOldIndentationCharPref = prefs.getString(CSSCorePreferenceNames.INDENTATION_CHAR);
		fOldIndentationSizePref = prefs.getInt(CSSCorePreferenceNames.INDENTATION_SIZE);

		prefs.setValue(CSSCorePreferenceNames.CLEAR_ALL_BLANK_LINES, false);
		prefs.setValue(CSSCorePreferenceNames.LINE_WIDTH, 72);
		prefs.setValue(CSSCorePreferenceNames.INDENTATION_CHAR, CSSCorePreferenceNames.TAB);
		prefs.setValue(CSSCorePreferenceNames.INDENTATION_SIZE, 1);

		fCleanupProcessor = new CleanupProcessorCSS();
		fStringCompareUtil = new StringCompareUtil();
	}

	protected void tearDown() throws Exception {
		// restore old preferences
		Preferences prefs = CSSCorePlugin.getDefault().getPluginPreferences();
		prefs.setValue(CSSCorePreferenceNames.CLEAR_ALL_BLANK_LINES, fOldClearBlankLinesPref);
		prefs.setValue(CSSCorePreferenceNames.LINE_WIDTH, fOldMaxLineWidthPref);
		prefs.setValue(CSSCorePreferenceNames.INDENTATION_CHAR, fOldIndentationCharPref);
		prefs.setValue(CSSCorePreferenceNames.INDENTATION_SIZE, fOldIndentationSizePref);
	}

	/**
	 * must release model (from edit) after
	 * 
	 * @param filename
	 *            relative to this class (TestCleanupProcessorCSS)
	 */
	private IStructuredModel getModelForEdit(final String filename) {

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			InputStream inStream = getClass().getResourceAsStream(filename);
			if (inStream == null)
				throw new FileNotFoundException("Can't file resource stream " + filename);
			final String baseFile = getClass().getResource(filename).toString();
			model = modelManager.getModelForEdit(baseFile, inStream, null);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return model;
	}

	private void cleanupAndAssertEquals(String beforePath, String afterPath) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			fCleanupProcessor.cleanupModel(beforeModel);

			ByteArrayOutputStream cleanedupBytes = new ByteArrayOutputStream();
			beforeModel.save(cleanedupBytes); // "beforeModel" should now be
			// after the cleanup processor

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String cleanedupContents = new String(afterBytes.toByteArray(), UTF_8);
			String expectedContents = new String(cleanedupBytes.toByteArray(), UTF_8);
			assertTrue("Cleanup document differs from the expected", fStringCompareUtil.equalsIgnoreLineSeperator(cleanedupContents, expectedContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	public void testBUG166909urlCaseCleanup() throws UnsupportedEncodingException, IOException, CoreException {
		// set up cleanup preferences for this test
		CSSCleanupStrategy currentStrategy = CSSCleanupStrategyImpl.getInstance();
		short oldCaseIdentifier = currentStrategy.getIdentCase();
		short oldCasePropertyName = currentStrategy.getPropNameCase();
		short oldCasePropertyValue = currentStrategy.getPropValueCase();
		short oldCaseSelector = currentStrategy.getSelectorTagCase();
		boolean oldFormatSource = currentStrategy.isFormatSource();
		boolean oldFormatQuote = currentStrategy.isQuoteValues();

		currentStrategy.setIdentCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setPropNameCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setPropValueCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setSelectorTagCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setFormatSource(true);
		currentStrategy.setQuoteValues(false);

		cleanupAndAssertEquals("testfiles/bug166909-urlcase.css", "testfiles/bug166909-urlcase-cleaned.css");

		currentStrategy.setIdentCase(oldCaseIdentifier);
		currentStrategy.setPropNameCase(oldCasePropertyName);
		currentStrategy.setPropValueCase(oldCasePropertyValue);
		currentStrategy.setSelectorTagCase(oldCaseSelector);
		currentStrategy.setFormatSource(oldFormatSource);
		currentStrategy.setQuoteValues(oldFormatQuote);
	}
	
	public void testBug218993NoFormatCleanup() throws UnsupportedEncodingException, IOException, CoreException {
		// set up cleanup preferences for this test
		CSSCleanupStrategy currentStrategy = CSSCleanupStrategyImpl.getInstance();
		short oldCaseIdentifier = currentStrategy.getIdentCase();
		short oldCasePropertyName = currentStrategy.getPropNameCase();
		short oldCasePropertyValue = currentStrategy.getPropValueCase();
		short oldCaseSelector = currentStrategy.getSelectorTagCase();
		boolean oldFormatSource = currentStrategy.isFormatSource();
		boolean oldFormatQuote = currentStrategy.isQuoteValues();

		currentStrategy.setIdentCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setPropNameCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setPropValueCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setSelectorTagCase(CSSCleanupStrategy.LOWER);
		currentStrategy.setFormatSource(false);
		currentStrategy.setQuoteValues(false);

		cleanupAndAssertEquals("testfiles/bug218993-noformat.css", "testfiles/bug218993-noformat-cleaned.css");

		currentStrategy.setIdentCase(oldCaseIdentifier);
		currentStrategy.setPropNameCase(oldCasePropertyName);
		currentStrategy.setPropValueCase(oldCasePropertyValue);
		currentStrategy.setSelectorTagCase(oldCaseSelector);
		currentStrategy.setFormatSource(oldFormatSource);
		currentStrategy.setQuoteValues(oldFormatQuote);
	}
}
