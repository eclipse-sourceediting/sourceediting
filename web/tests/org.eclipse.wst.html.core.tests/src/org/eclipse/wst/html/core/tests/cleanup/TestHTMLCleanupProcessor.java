/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.cleanup;

import java.io.IOException;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.core.internal.cleanup.HTMLCleanupProcessorImpl;
import org.eclipse.wst.html.core.tests.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;

public class TestHTMLCleanupProcessor extends TestCase {
	/**
	 * The name of the project that all of these tests will use
	 */
	private static final String PROJECT_NAME = "TestHTMLCleanupProcessor";
	
	/**
	 * The location of the testing files
	 */
	private static final String PROJECT_FILES = "/testresources/HTMLCleanupProcessor";
	
	/**
	 * The project that all of the tests use
	 */
	private static IProject fProject;
	
	/**
	 * Default constructor
	 */
	public TestHTMLCleanupProcessor() {
		super("Test HTML Cleanup Processor");
	}
	
	/**
	 * Constructor that takes a test name.
	 * 
	 * @param name The name this test run should have.
	 */
	public TestHTMLCleanupProcessor(String name) {
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
		TestSuite ts = new TestSuite(TestHTMLCleanupProcessor.class);
		return new TestHTMLCleanupProcessorSetup(ts);

	}
	
	/**
	 * <p><b>TEST:</b> collapsing empty tags in an html document</p>
	 */
	public void testCollapseEmptyTagsHTML()throws Exception  {
		HTMLCleanupProcessorImpl cleanupProcessor = getProcessorForForEmptyTagsTest();
		runTest("test1.html", "test1-expected.html", cleanupProcessor);
	}
	
	/**
	 * <p><b>TEST:</b> collapsing empty tags in an xhtml document</p>
	 */
	public void testCollapseEmptyTagsXHTML()throws Exception  {
		HTMLCleanupProcessorImpl cleanupProcessor = getProcessorForForEmptyTagsTest();
		runTest("test2.html", "test2-expected.html", cleanupProcessor);
	}
	
	/**
	 * @return a configured {@link HTMLCleanupProcessorImpl} for testing compressing empty tags
	 */
	private static HTMLCleanupProcessorImpl getProcessorForForEmptyTagsTest() {
		HTMLCleanupProcessorImpl cleanupProcessor = new HTMLCleanupProcessorImpl();
		cleanupProcessor.getCleanupPreferences().setCompressEmptyElementTags(true);
		cleanupProcessor.getCleanupPreferences().setInsertRequiredAttrs(false);
		cleanupProcessor.getCleanupPreferences().setInsertMissingTags(true);
		cleanupProcessor.getCleanupPreferences().setQuoteAttrValues(false);
		cleanupProcessor.getCleanupPreferences().setFormatSource(false);
		cleanupProcessor.getCleanupPreferences().setConvertEOLCodes(false);
		
		return cleanupProcessor;
	}
	
	/**
	 * <p>Runs an {@link AbstractStructuredCleanupProcessor} test</p>
	 * 
	 * @param originalFile file to clean up
	 * @param expectedResultsFile expected results of cleaning up the original file
	 * @param configuredCleanupProcessor the processor to use to do the cleaning
	 * 
	 * @throws Exception tests can throw exceptions now and then
	 */
	private void runTest(String originalFile, String expectedResultsFile,
			AbstractStructuredCleanupProcessor configuredCleanupProcessor) throws Exception {
		
		IStructuredModel model = null;
		IStructuredModel expectedModel = null;
		try {
			model = getModelForEdit(originalFile);
			expectedModel = getModelForEdit(expectedResultsFile);
			
			configuredCleanupProcessor.refreshCleanupPreferences = false;
			configuredCleanupProcessor.cleanupModel(model);
			configuredCleanupProcessor.refreshCleanupPreferences = true;
			
			model.save();
			
			standardizeLineEndings(model.getStructuredDocument());
			standardizeLineEndings(expectedModel.getStructuredDocument());
			
			assertEquals("Clean up results did not match expected results",
					expectedModel.getStructuredDocument().get(),
					model.getStructuredDocument().get());
		} finally {
			if(model != null) {
				model.releaseFromEdit();
			}
			
			if(expectedModel != null) {
				expectedModel.releaseFromEdit();
			}
		}
	}
	
	/**
	 * <p>Given a file name in <code>fProject</code> attempts to get a model
	 * for it, if the file doesn't exist or it can't get the model the test fails.</p>
	 * 
	 * @param filename
	 * @return
	 * @throws CoreException 
	 * @throws IOException 
	 */
	private IStructuredModel getModelForEdit(final String filename) throws IOException, CoreException {
		IFile file = fProject.getFile(filename);
		assertTrue("Test file " + file + " can not be found", file.exists());
		
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(file);
		assertNotNull("Could not get model for " + file, model);
		
		return model;
	}
	
	
	/**
	 * <p>Line endings can be an issue when running tests on different OSs.
	 * This function standardizes the line endings to use <code>\n</code></p>
	 * 
	 * <p>It will get the text from the given editor, change the line endings,
	 * and then save the editor</p>
	 * 
	 * @param editor standardize the line endings of the text presented in this
	 * editor.
	 */
	private void standardizeLineEndings(IDocument doc) {
		String contents = doc.get();
		contents = StringUtils.replace(contents, "\r\n", "\n");
		contents = StringUtils.replace(contents, "\r", "\n");
		doc.set(contents);
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after (respectively) all tests in the inclosing class have run.</p>
	 */
	private static class TestHTMLCleanupProcessorSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public TestHTMLCleanupProcessorSetup(Test test) {
			super(test);
		}

		/**
		 * <p>This is run once before all of the tests</p>
		 * 
		 * @see junit.extensions.TestSetup#setUp()
		 */
		public void setUp() throws Exception {
			fProject = ProjectUtil.createProject(PROJECT_NAME, null, null);
			ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
			
			String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
			if (noninteractive != null) {
				previousWTPAutoTestNonInteractivePropValue = noninteractive;
			} else {
				previousWTPAutoTestNonInteractivePropValue = "false";
			}
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			fProject.delete(true, new NullProgressMonitor());
			
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
		}
	}
}
