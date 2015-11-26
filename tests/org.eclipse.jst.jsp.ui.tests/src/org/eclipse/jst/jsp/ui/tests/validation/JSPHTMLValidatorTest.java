/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.jst.jsp.ui.internal.validation.JSPContentSourceValidator;
import org.eclipse.jst.jsp.ui.tests.JSPUITestsPlugin;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * Tests HTML validator on jsp file
 */
public class JSPHTMLValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "bug_143209";

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
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}

	/**
	 * Tests jsp expression in html attributes in jsp file
	 * 
	 * @throws Exception
	 */
	public void testJSPinAttributes() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/usejspinattribute.jsp";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp in attributes are errors when they should not be (in .jsp)", reporter.getMessages().isEmpty());
	}

	/**
	 * Tests jsp expression in html attributes in html file
	 * 
	 * @throws Exception
	 */
	public void testJSPinAttributesHTML() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/usejspinattribute.html";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp in attributes are not errors when they should be (in .html)", !reporter.getMessages().isEmpty());
	}

	/**
	 * Tests bad attribute names in jsp file false
	 * 
	 * @throws Exception
	 */
	public void testBadAttributeName() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/badattributenames.jsp";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("bad attribute name is not error when it should be", !reporter.getMessages().isEmpty());
	}
	
	/**
	 * Regression test for Bug 285285
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.validation.TestDelegatingSourceValidatorForXML#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.wst.html.ui.tests.validation.TestHTMLValidator#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.jst.jsp.ui.tests.validation.JSPHTMLValidatorTest#testRemoveAndAddBackCommentEndTag
	 */
	public void testRemoveAndAddBackCommentEndTag() throws Exception{
		JSPContentSourceValidator fValidator = new JSPContentSourceValidator();
		
		String projectName = "RemoveAndAddBackCommentEndTag";
		IProject project = ProjectUtil.createProject(projectName, JSPUITestsPlugin.getDefault().getStateLocation().append(getName()), null);
		
		IFile testFile = null;
		IStructuredModel model = null;
		
		try {
			//get test file
			ProjectUtil.copyBundleEntriesIntoWorkspace("testfiles/RemoveAndAddBackCommentEndTag", projectName);
			testFile = project.getFile("Test1.jsp");
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
		project.delete(true, null);
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
