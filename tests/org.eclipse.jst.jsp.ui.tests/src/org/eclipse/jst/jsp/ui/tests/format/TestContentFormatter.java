/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContext;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jst.jsp.ui.StructuredTextViewerConfigurationJSP;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUtil;
import org.eclipse.jst.jsp.ui.tests.util.StringCompareUtil;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class TestContentFormatter extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "jspformatting";
	private static final String UTF_8 = "UTF-8";

	private StringCompareUtil fStringCompareUtil;
	private IFormattingContext fContext;

	protected void setUp() throws Exception {
		super.setUp();

		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists()) {
			ProjectUtil.createProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			ProjectUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists());

		fContext = new FormattingContext();
		fContext.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));

		fStringCompareUtil = new StringCompareUtil();
	}

	private void formatAndAssertEquals(String beforePath, String afterPath, boolean resetPreferences) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			if (resetPreferences) {
				resetPreferencesToDefault();
			}

			SourceViewerConfiguration configuration = new StructuredTextViewerConfigurationJSP();
			IContentFormatterExtension formatter = (IContentFormatterExtension) configuration.getContentFormatter(null);

			IDocument document = beforeModel.getStructuredDocument();
			Region region = new Region(0, document.getLength());
			fContext.setProperty(FormattingContextProperties.CONTEXT_REGION, region);
			formatter.format(document, fContext);

			ByteArrayOutputStream formattedBytes = new ByteArrayOutputStream();
			beforeModel.save(formattedBytes); // "beforeModel" should now be
			// after the formatter

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String expectedContents = new String(afterBytes.toByteArray(), UTF_8);
			String actualContents = new String(formattedBytes.toByteArray(), UTF_8);
			assertTrue("Formatted document differs from the expected.\nExpected Contents:\n" + expectedContents + "\nActual Contents:\n" + actualContents, fStringCompareUtil.equalsIgnoreLineSeperator(expectedContents, actualContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	/**
	 * must release model (from edit) after
	 * 
	 * @param filename
	 *            relative to this class (TestFormatProcessorCSS)
	 */
	private IStructuredModel getModelForEdit(final String filename) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filename));
		assertTrue("unable to find file: " + filename, file.exists());

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			model = modelManager.getModelForEdit(file);
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		return model;
	}

	private void resetPreferencesToDefault() {
		Preferences preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
		preferences.setToDefault(HTMLCorePreferenceNames.SPLIT_MULTI_ATTRS);
		preferences.setToDefault(HTMLCorePreferenceNames.LINE_WIDTH);
		preferences.setToDefault(HTMLCorePreferenceNames.INDENTATION_CHAR);
		preferences.setToDefault(HTMLCorePreferenceNames.INDENTATION_SIZE);
		preferences.setToDefault(HTMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES);
	}

	public void testFormatBug102495() throws UnsupportedEncodingException, IOException, CoreException {
		String beforePath = "/" + PROJECT_NAME + "/WebContent/formatbug102495.jsp";
		String afterPath = "/" + PROJECT_NAME + "/WebContent/formatbug102495-fmt.jsp";
		formatAndAssertEquals(beforePath, afterPath, true);
	}

	public void testFormatBug102495_1() throws UnsupportedEncodingException, IOException, CoreException {
		String beforePath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_1.jsp";
		String afterPath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_1-fmt.jsp";
		formatAndAssertEquals(beforePath, afterPath, true);
	}

	public void testFormatBug102495_2() throws UnsupportedEncodingException, IOException, CoreException {
		String beforePath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_2.jsp";
		String afterPath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_2-fmt.jsp";
		formatAndAssertEquals(beforePath, afterPath, true);
	}

	public void testFormatBug102495_3() throws UnsupportedEncodingException, IOException, CoreException {
		String beforePath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_3.jsp";
		String afterPath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_3-fmt.jsp";
		formatAndAssertEquals(beforePath, afterPath, true);
	}

	public void testFormatBug102495_4() throws UnsupportedEncodingException, IOException, CoreException {
		String beforePath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_4.jsp";
		String afterPath = "/" + PROJECT_NAME + "/WebContent/formatbug102495_4-fmt.jsp";
		formatAndAssertEquals(beforePath, afterPath, true);
	}
}
