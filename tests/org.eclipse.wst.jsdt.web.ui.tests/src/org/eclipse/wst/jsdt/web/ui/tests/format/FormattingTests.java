/*******************************************************************************
 * Copyright (c) 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.format;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContext;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.jsdt.web.ui.StructuredTextViewerConfigurationJSDT;
import org.eclipse.wst.jsdt.web.ui.tests.internal.ProjectUtil;
import org.eclipse.wst.jsdt.web.ui.tests.internal.StringUtils;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

/**
 *
 */
public class FormattingTests extends TestCase {
	/**
	 * The name of the project that all of these tests will use
	 */
	private static final String PROJECT_NAME = "FormattingTests";
	
	/**
	 * The location of the testing files
	 */
	private static final String PROJECT_FILES = "/testFiles/formatting";
	
	/**
	 * The project that all of the tests use
	 */
	static IProject fProject;
	static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
	static String previousWTPAutoTestNonInteractivePropValue = null;
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public FormattingTests() {
		super("Formatting Tests");
	}
	
	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public FormattingTests(String name) {
		super(name);
	}
	
	/**
	 * <p>Use this method to add these tests to a larger test suite so set up
	 * and tear down can be performed</p>
	 * 
	 * @return a {@link TestSetup} that will run all of the tests in this class
	 * with set up and tear down.
	 */
	public static Test suite() {
		TestSuite ts = new TestSuite(FormattingTests.class, "Formatting Tests");
		return new FormattingTestsSetup(ts);
	}
	
	
	public void testFormatHTMLScriptEvent() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test0.html", "test0.html", new StructuredTextViewerConfigurationJSDT());
	}
	
//	public void testFormatJSPScriptEvent() throws UnsupportedEncodingException, IOException, CoreException {
//		formatAndAssertEquals("test1.jsp", "test1.jsp", new JSDTStructuredTextViewerConfigurationJSP());
//	}

	public void testFormatHTMLScriptRegionWrappedWithHTMLComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test2.html", "test2-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWrappedWithHTMLCommentWithInvalidJS() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test3.html", "test3.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWithJustEndHTMLComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test4.html", "test4-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWithJustStartHTMLComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test5.html", "test5-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScript_WithHTMLTagInString() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test6.html", "test6-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}

	public void testFormatHTMLScript_FormattingHTMLBeforeScript() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test7.html", "test7-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScript_WithHTMLTagInString_WithHTMLCommentStartAndEnd_simple() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test8.html", "test8-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScript_WithHTMLTagInString_WithHTMLCommentStartAndEnd_advanded() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test9.html", "test9-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWrappedWithHTMLComment_TextInLeadingComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test10.html", "test10-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWrappedWithHTMLComment_TextInTrailingComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test11.html", "test11-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegionWrappedWithHTMLComment_TextInLeadingComment_and_TextInTrailingComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test12.html", "test12-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegion_AllOnOneLine() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test13.html", "test13-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	
	public void testFormatHTMLScriptRegion_AllOnOneLine_LeadingComment() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test14.html", "test14-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	public void testFormatHTMLScriptRegion_AfterEventHander() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test15.html", "test15-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}
	public void testFormat_NoEdits() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test16.html", "test16-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}	
	
	public void testBug383387() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test17.html", "test17-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}	
	
	public void testWI97431() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test18.html", "test18-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}	
	
	public void testCDATAPreserved() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test19.html", "test19-fmt.html", new StructuredTextViewerConfigurationJSDT());
	}	
	
	public void testBug377979() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("test20.jsp", "test20-fmt.jsp", new StructuredTextViewerConfigurationJSDT());
	}	
	
	/**
	 * @param beforePath - the path of the before file
	 * @param afterPath - the path of the after file, <b>must not be the same as the before file</b>
	 * @param configuration
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 * 
	 * @see org.eclipse.wst.xml.core.tests.format.TestPartitionFormatterXML#formatAndAssertEquals
	 */
	private void formatAndAssertEquals(String beforePath, String afterPath, SourceViewerConfiguration configuration) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		ISourceViewer viewer = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);
			JsTranslationAdapterFactory.setupAdapterFactory(beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			//normalize contents
			IStructuredDocument document = beforeModel.getStructuredDocument();
			String normalizedContents = document.get();
			normalizedContents = StringUtils.replace(normalizedContents, "\r\n", "\n");
			normalizedContents = StringUtils.replace(normalizedContents, "\r", "\n");
			document.set(normalizedContents);
			
			viewer = getConfiguredViewer(document, configuration);
			assertNotNull("Could not get viewer to run test", viewer);
			
			//do the format
			IContentFormatterExtension formatter = (IContentFormatterExtension) configuration.getContentFormatter(viewer);
			IFormattingContext fContext = new FormattingContext();
			Region region = new Region(0, document.getLength());
			fContext.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));
			fContext.setProperty(FormattingContextProperties.CONTEXT_REGION, region);
			formatter.format(document, fContext);
			
			//get the contents
			String actualContents = beforeModel.getStructuredDocument().get();
			String expectedContents = afterModel.getStructuredDocument().get();

			/* Make some adjustments to ignore cross platform line delimiter issues */
			expectedContents = StringUtils.replace(expectedContents, "\r\n", "\n");
			expectedContents = StringUtils.replace(expectedContents, "\r", "\n");
			actualContents = StringUtils.replace(actualContents, "\r\n", "\n");
			actualContents = StringUtils.replace(actualContents, "\r", "\n");
			
			onlyWhiteSpaceDiffers(expectedContents, actualContents);
			assertEquals("Formatted document differs from the expected.", expectedContents, actualContents);
		}
		finally {
			if (beforeModel != null) {
				try {
					beforeModel.releaseFromEdit();
				} catch(Exception e) {
					//ignore
				}
			}
			if (afterModel != null) {
				try {
					afterModel.releaseFromEdit();
				} catch(Exception e) {
					//ignore
				}
			}
			if (viewer != null) {
				StyledText text = viewer.getTextWidget();
				if (text != null && !text.isDisposed()) {
					text.dispose();
				}
			}
		}
	}
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
	
	/**
	 * <p>Given a file name in <code>fProject</code> attempts to get an <code>IFile</code>
	 * for it, if the file doesn't exist the test fails.</p>
	 * 
	 * @param name the name of the file to get
	 * @return the <code>IFile</code> associated with the given <code>name</code>
	 */
	private static IFile getFile(String name) {
		IFile file = fProject.getFile(name);
		assertTrue("Test file " + file + " can not be found", file.exists());
		
		return file;
	}
	
	/**
	 * must release model (from edit) after
	 * 
	 * @param filename
	 *            relative to this class (TestFormatProcessorCSS)
	 */
	private IStructuredModel getModelForEdit(final String path) {
		IFile file = getFile(path);
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
	
	protected void setUp() throws Exception {
		super.setUp();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if (!project.isAccessible()) {
			_createProject();
		}
	}
	
	static void _createProject() {
		//init testing resources
		fProject = ProjectUtil.createProject(PROJECT_NAME, null, new String[] {JavaScriptCore.NATURE_ID});
		ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
		
		//set non-interactive
		String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
		if (noninteractive != null) {
			previousWTPAutoTestNonInteractivePropValue = noninteractive;
		} else {
			previousWTPAutoTestNonInteractivePropValue = "false";
		}
		System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
	}
	
	/**
	 * @param document {@link IDocument} to display in the dummy viewer
	 * @param configuration {@link SourceViewerConfiguration} to configure the dummy viewer with
	 * @return a configured {@link ISourceViewer} using the given parameters
	 */
	private static ISourceViewer getConfiguredViewer(IDocument document, SourceViewerConfiguration configuration) {
		ISourceViewer viewer = null;
		assertNotNull("Could not get current display to run test with.", Display.getCurrent());

		Shell shell = null;

		if (PlatformUI.isWorkbenchRunning()) {
			shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		}
		else {
			shell = new Shell(Display.getCurrent());
		}
		Composite parent = new Composite(shell, SWT.NONE);
		viewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		viewer.setDocument(document);
		viewer.configure(configuration);
		
		return viewer;
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after (respectively) all tests in the inclosing class have run.</p>
	 */
	private static class FormattingTestsSetup extends TestSetup {
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public FormattingTestsSetup(Test test) {
			super(test);
		}

		/**
		 * <p>This is run once before all of the tests</p>
		 * 
		 * @see junit.extensions.TestSetup#setUp()
		 */
		public void setUp() throws Exception {
			_createProject();
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			try {
				// delete test projects
				fProject.delete(true, new NullProgressMonitor());
			}
			catch (CoreException e) {
				// problems deleting aren't a failure
			}
			finally {
				// reset non-interactive
				if (previousWTPAutoTestNonInteractivePropValue != null) {
					System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
				}
			}
		}
	}
}
