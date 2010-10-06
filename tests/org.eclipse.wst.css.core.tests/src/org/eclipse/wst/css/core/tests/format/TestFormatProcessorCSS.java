/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
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
import org.eclipse.wst.css.core.internal.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.tests.util.StringCompareUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class TestFormatProcessorCSS extends TestCase {
	private static final String UTF_8 = "UTF-8";
	private StringCompareUtil fStringCompareUtil;
	private FormatProcessorCSS formatProcessor;

	private boolean fOldClearBlankLinesPref;
	private int fOldMaxLineWidthPref;
	private String fOldIndentationCharPref;
	private int fOldIndentationSizePref;

	public TestFormatProcessorCSS(String name) {
		super(name);
	}

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

		formatProcessor = new FormatProcessorCSS();
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
	 *            relative to this class (TestFormatProcessorCSS)
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

	private void formatAndAssertEquals(String beforePath, String afterPath) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			formatProcessor.formatModel(beforeModel);

			ByteArrayOutputStream formattedBytes = new ByteArrayOutputStream();
			beforeModel.save(formattedBytes); // "beforeModel" should now be
			// after the formatter

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String formattedContents = new String(afterBytes.toByteArray(), UTF_8);
			String expectedContents = new String(formattedBytes.toByteArray(), UTF_8);
			assertTrue("Formatted document differs from the expected", fStringCompareUtil.equalsIgnoreLineSeperator(formattedContents, expectedContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	public void testBUG73990SelectorFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/BUG73990_selector_unformatted.css", "testfiles/BUG73990_selector_formatted.css");
	}

	/**
	 * Tests case when format would incorrectly add extra semicolons.
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 */
	public void testBUG111569extraSemicolonFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug111569-extrasemicolon.css", "testfiles/bug111569-extrasemicolon-formatted.css");
	}

	/**
	 * Tests case when format would incorrectly add space between selector and
	 * attribute selectors <code>input[type="hidden"]</code> would be
	 * wrongly formatted as<code>input [type="hidden"]</code> instead of
	 * being left alone.
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 */
	public void testBUG146198attributeSpecifierFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug146198-attributespecifier.css", "testfiles/bug146198-attributespecifier-formatted.css");
	}
	
	public void testBUG110539multipleClassFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug110539-multipleclass.css", "testfiles/bug110539-multipleclass-formatted.css");
	}
	
	public void testBUG248465combinedPseudoClass() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug248465-combined-pseudo-classes.css", "testfiles/bug248465-combined-pseudo-classes-fmt.css");
	}
	
	public void testBUG196476selectorPseudoclassesFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug196476-selector-pseudo-classes.css", "testfiles/bug196476-selector-pseudo-classes-fmt.css");
	}
	
	/**
	 * file should not change after format
	 */
	public void testBUG163315SlashBeforePrimative1() throws UnsupportedEncodingException, IOException, CoreException {
		//
		formatAndAssertEquals("testfiles/bug163315-slash_before_primative_1.css", "testfiles/bug163315-slash_before_primative_1.css");
	}
	
	/**
	 * space after / should be removed
	 */
	public void testBUG163315SlashBeforePrimative2() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/bug163315-slash_before_primative_2.css", "testfiles/bug163315-slash_before_primative_1.css");
	}

	/**
	 * Test case with two comments separated by whitespace. Make sure that the comments do not run together onto one line.
	 */
	public void testCollapseWhitespaceBetweenComments() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/comments.css", "testfiles/comments-fmt.css");
	}
}
