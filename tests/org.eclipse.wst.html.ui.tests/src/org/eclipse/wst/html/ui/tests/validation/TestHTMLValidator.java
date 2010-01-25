/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.tests.validation;


import java.util.ArrayList;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.html.internal.validation.HTMLValidator;
import org.eclipse.wst.html.ui.tests.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * Test for the HTMLValidator
 * 
 * @see org.eclipse.wst.html.internal.validation.HTMLValidator
 */
public class TestHTMLValidator extends TestCase {

	/** The name of the project that all of these tests will use */
	private static final String PROJECT_NAME = "TestHTMLValidator";
	
	/** The location of the testing files */
	private static final String PROJECT_FILES = "/testresources/TestHTMLValidator";
	
	/** The project that all of the tests use */
	private static IProject fProject;
	
	private HTMLValidator fValidator = new HTMLValidator();
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public TestHTMLValidator() {
		super("Test HTMLValidator");
	}

	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public TestHTMLValidator(String name) {
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
		TestSuite ts = new TestSuite(TestHTMLValidator.class, "Test HTMLValidator");
		return new TestHTMLValidatorSetup(ts);
	}
	
	/**
	 * Regression test for Bug 285285
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.validation.TestDelegatingSourceValidatorForXML#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.wst.html.ui.tests.validation.TestHTMLValidator#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.jst.jsp.ui.tests.validation.JSPHTMLValidatorTest#testRemoveAndAddBackCommentEndTag
	 */
	public void testRemoveAndAddBackCommentEndTag() throws Exception{
		IFile testFile = null;
		IStructuredModel model = null;
		
		try {
			//get test file
			testFile = fProject.getFile("RemoveAndAddBackCommentEndTag.html");
			assertTrue("Test file " + testFile + " does not exist", testFile.exists());
			
			//get the document
			model = StructuredModelManager.getModelManager().getModelForEdit(testFile);
			IStructuredDocument document = model.getStructuredDocument();
			
			//set up for fValidator
			WorkbenchContext context = new WorkbenchContext();
			List fileList = new ArrayList();
			fileList.add(testFile.getFullPath().toPortableString());
			context.setValidationFileURIs(fileList);
			
			//validate clean file
			TestReporter reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
			
			//need to dynamically find where the --> is because
			//its different on unix vs windows because of line endings
			String contents = document.get();
			int endCommentIndex = contents.indexOf("-->");
			
			//remove -->
			document.replace(endCommentIndex, 3, "");
			
			//validate file with error
			reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertTrue("There should be validation errors on " + testFile, reporter.isMessageReported());
		
			//replace -->
			document.replace(endCommentIndex, 0, "-->");
			
			//validate clean file
			reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
		} finally {
			if(model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	/**
	 * Regression test for Bug 298472
	 * 
	 */
	public void testInvalidateTagNameThenFix() throws Exception{
		IFile testFile = null;
		IStructuredModel model = null;
		
		try {
			//get test file
			testFile = fProject.getFile("InvalidateTagNameThenFix.html");
			assertTrue("Test file " + testFile + " does not exist", testFile.exists());
			
			//get the document
			model = StructuredModelManager.getModelManager().getModelForEdit(testFile);
			IStructuredDocument document = model.getStructuredDocument();
			
			//set up for validator
			WorkbenchContext context = new WorkbenchContext();
			List fileList = new ArrayList();
			fileList.add(testFile.getFullPath().toPortableString());
			context.setValidationFileURIs(fileList);
			
			//validate clean file
			TestReporter reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
			
			//need to dynamically find where the --> is because
			//its different on unix vs windows because of line endings
			String contents = document.get();
			int endOfStartTagIndex = contents.indexOf("></div>");
			
			//change to <divx></div>
			document.replace(endOfStartTagIndex, 0, "x");
			
			//validate file with error
			reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertTrue("There should be validation errors on " + testFile, reporter.isMessageReported());
		
			//change back to <div></div>
			document.replace(endOfStartTagIndex, 1, "");
			
			//validate clean file
			reporter = new TestReporter();
			fValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
		} finally {
			if(model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after all tests in the inclosing class have run.</p>
	 */
	private static class TestHTMLValidatorSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public TestHTMLValidatorSetup(Test test) {
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
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
			
			fProject.delete(true, new NullProgressMonitor());
		}
	}
	
	/**
	 * A <code>IReporter</code> for testing validators
	 */
	private class TestReporter implements IReporter {
		private boolean messageReported = false;
		
		public TestReporter(){}
		
		public void addMessage(IValidator origin, IMessage message) {
			messageReported = true;
		}
		
		public boolean isMessageReported() {
			return messageReported;
		}

		public void displaySubtask(IValidator validator, IMessage message) {}

		public List getMessages() {
			return null;
		}

		public boolean isCancelled() {
			return false;
		}

		public void removeAllMessages(IValidator origin, Object object) {}

		public void removeAllMessages(IValidator origin) {}

		public void removeMessageSubset(IValidator validator, Object obj, String groupName) {}
	}
}
