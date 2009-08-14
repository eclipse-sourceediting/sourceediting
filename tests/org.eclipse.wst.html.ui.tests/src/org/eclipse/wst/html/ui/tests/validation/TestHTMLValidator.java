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
package org.eclipse.wst.html.ui.tests.validation;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

	private HTMLValidator fValidator = new HTMLValidator();
	
	/**
	 * 
	 */
	public TestHTMLValidator() {
		super("Test HTMLValidator");
	}

	/**
	 * @param name
	 */
	public TestHTMLValidator(String name) {
		super(name);
	}
	
	/**
	 * Regression test for Bug 285285
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.validation.TestDelegatingSourceValidatorForXML#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.wst.html.ui.tests.validation.TestHTMLValidator#testRemoveAndAddBackCommentEndTag
	 * @see org.eclipse.jst.jsp.ui.tests.validation.JSPHTMLValidatorTest#testRemoveAndAddBackCommentEndTag
	 */
	public void testRemoveAndAddBackCommentEndTag() throws Exception{
		String projectName = "RemoveAndAddBackCommentEndTag";
		IProject project = ProjectUtil.createProject(projectName, null, null);
		
		IFile testFile = null;
		IStructuredModel model = null;
		
		try {
			//get test file
			ProjectUtil.copyBundleEntriesIntoWorkspace("testresources/RemoveAndAddBackCommentEndTag", projectName);
			testFile = project.getFile("Test1.html");
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
