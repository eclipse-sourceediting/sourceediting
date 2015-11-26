/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.format;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContext;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.web.ui.tests.internal.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

/**
 * Taken from org.eclipse.jst.jsp.ui.tests.format.TestContentFormatter
 */
public class TestJSPContentFormatter extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "jsdtjspformatting";
	private static final String UTF_8 = "UTF-8";

	private IFormattingContext fContext;
	public static Test suite() {
		TestSuite ts = new TestSuite(TestJSPContentFormatter.class);
		return ts;
	}

	protected void setUp() throws Exception {
		super.setUp();

		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists()) {
			ProjectUtil.createProject(PROJECT_NAME, null, new String[]{JavaScriptCore.NATURE_ID});
			ProjectUtil.copyBundleEntriesIntoWorkspace("/testFiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists());

		fContext = new FormattingContext();
		fContext.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));
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

			SourceViewerConfiguration configuration = (SourceViewerConfiguration) ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, "org.eclipse.jst.jsp.core.jspsource");
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
			expectedContents = StringUtils.replace(expectedContents, "\r\n", "\r");
			expectedContents = StringUtils.replace(expectedContents, "\r", "\n");

			String actualContents = new String(formattedBytes.toByteArray(), UTF_8);
			actualContents = StringUtils.replace(actualContents, "\r\n", "\r");
			actualContents = StringUtils.replace(actualContents, "\r", "\n");
			
			assertTrue(onlyWhiteSpaceDiffers(expectedContents, actualContents));
			assertEquals("Formatted document differs from the expected.", expectedContents, actualContents);
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	private void formatAndAssertSignificantEquals(String beforePath, boolean resetPreferences) throws UnsupportedEncodingException, IOException, CoreException {
		StructuredTextEditor editor = (StructuredTextEditor) IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(beforePath)), "org.eclipse.jst.jsp.core.jspsource.source", true);
		try {
			String before = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
			editor.getTextViewer().doOperation(StructuredTextViewer.FORMAT_DOCUMENT);

			String after = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
			assertTrue(onlyWhiteSpaceDiffers(before, after));
		}
		finally {
			editor.close(false);
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

	/**
	 * Useful for making sure all significant content was retained.
	 * 
	 * @param expectedContents
	 * @param actualContents
	 * @return
	 */
	private boolean onlyWhiteSpaceDiffers(String expectedContents, String actualContents) {
		CharArrayWriter writer1 = new CharArrayWriter();
		char[] expected = expectedContents.toCharArray();
		for (int i = 0; i < expected.length; i++) {
			if (!Character.isWhitespace(expected[i]))
				writer1.write(expected[i]);
		}

		CharArrayWriter writer2 = new CharArrayWriter();
		char[] actual = actualContents.toCharArray();
		for (int i = 0; i < actual.length; i++) {
			if (!Character.isWhitespace(actual[i]))
				writer2.write(actual[i]);
		}
		writer1.close();
		writer2.close();

		char[] expectedCompacted = writer1.toCharArray();
		char[] actualCompacted = writer2.toCharArray();
		assertEquals("significant character differs", new String(expectedCompacted), new String(actualCompacted));

		return true;
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

	public void testFormatBug358545a() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertSignificantEquals("/" + PROJECT_NAME + "/WebContent/formatbug358545.jsp", true);
	}
	
	public void testFormatBug358545b() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertSignificantEquals("/" + PROJECT_NAME + "/WebContent/formatbug358545b.jsp", true);
	}
	public void testFormatBug384126() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertSignificantEquals("/" + PROJECT_NAME + "/WebContent/formatBug384126.jsp", true);
	}
	public void testFormatBug383387() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertSignificantEquals("/" + PROJECT_NAME + "/WebContent/formatBug383387.jsp", true);
	}
}
