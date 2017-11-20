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
package org.eclipse.wst.xml.ui.internal.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.tests.ProjectUtil;
import org.eclipse.wst.xml.ui.tests.XMLUITestsPlugin;

/**
 * Test the XML delegating source validator.
 *
 */
public class TestDelegatingSourceValidatorForXML extends TestCase 
{
	DelegatingSourceValidatorForXML sourceValidator = new DelegatingSourceValidatorForXML();
	
	/**
	 * Test that files that contain non-8bit chars are validated
	 * correctly. i.e. Do not produce incorrect validation messages.
	 */
	public void testNon8BitChars()
	{
		String projName = "Project";
		String fileName1 = "international-instance.xml";
		String fileName2 = "international.xsd";
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		if (!project.isAccessible()) {
			IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

			try {
				project.create(description, new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(projName + "/" + fileName1));
		if (file != null && !file.exists()) {
			try {
				file.create(FileLocator.openStream(XMLUITestsPlugin.getDefault().getBundle(), new Path("/testresources/Non8BitChars/international-instance.xml"), false), true, new NullProgressMonitor());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(projName + "/" + fileName2));
		if (file != null && !file.exists()) {
			try {
				file.create(FileLocator.openStream(XMLUITestsPlugin.getDefault().getBundle(), new Path("/testresources/Non8BitChars/international.xsd"), false), true, new NullProgressMonitor());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		WorkbenchContext context = new WorkbenchContext();
		List fileList = new ArrayList();
		fileList.add("/" + projName + "/" + fileName1);
		context.setValidationFileURIs(fileList);
		TestReporter reporter = new TestReporter();
		try{
			sourceValidator.validate(context, reporter);
		}
		catch(ValidationException e){
			e.printStackTrace();
		}
		
		assertFalse("Messages were reported on valid file 1.", reporter.isMessageReported());
		
		WorkbenchContext context2 = new WorkbenchContext();
		List fileList2 = new ArrayList();
		fileList2.add("/" + projName + "/" + fileName2);
		context2.setValidationFileURIs(fileList2);
		TestReporter reporter2 = new TestReporter();
		try{
			sourceValidator.validate(context2, reporter2);
		}
		catch(ValidationException e){
			e.printStackTrace();
		}
		
		assertFalse("Messages were reported on valid file 2.", reporter2.isMessageReported());
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
		IProject project = ProjectUtil.createProject(projectName, XMLUITestsPlugin.getDefault().getStateLocation().append(getName()), null);
		
		IFile testFile = null;
		IStructuredModel model = null;
		
		try {
			//get test file
			ProjectUtil.copyBundleEntriesIntoWorkspace("testresources/RemoveAndAddBackCommentEndTag", projectName);
			testFile = project.getFile("Test1.xml");
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
			sourceValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
			
			//need to dynamically find where the --> is because
			//its different on unix vs windows because of line endings
			String contents = document.get();
			int endCommentIndex = contents.indexOf("-->");
			
			//remove -->
			document.replace(endCommentIndex, 3, "");
			
			//validate file with error
			reporter = new TestReporter();
			sourceValidator.validate(context, reporter);
			assertTrue("There should be validation errors on " + testFile, reporter.isMessageReported());
		
			//replace -->
			document.replace(endCommentIndex, 0, "-->");
			
			//validate clean file
			reporter = new TestReporter();
			sourceValidator.validate(context, reporter);
			assertFalse("There should be no validation errors on " + testFile, reporter.isMessageReported());
		} catch(ValidationException e) {
			fail("Could not validate test file " + testFile + ": " + e.getMessage());
		} finally {
			if(model != null) {
				model.releaseFromEdit();
			}
		}
		project.delete(true, null);
	}
	
	/**
	 * Regression test for Bug 276337
	 */
	public void testValidateAgainstDTD() throws Exception{
		String projectName = "TestValidateAgainstDTD";
		IProject project = ProjectUtil.createProject(projectName, XMLUITestsPlugin.getDefault().getStateLocation().append(getName()), null);
		
		IFile testFile = null;
		try {
			//get test file
			ProjectUtil.copyBundleEntriesIntoWorkspace("testresources/TestValidateAgainstDTD", projectName);
			testFile = project.getFile("simple.xml");
			assertTrue("Test file " + testFile + " does not exist", testFile.exists());
			
			//set up for validator
			WorkbenchContext context = new WorkbenchContext();
			List fileList = new ArrayList();
			fileList.add(testFile.getFullPath().toPortableString());
			context.setValidationFileURIs(fileList);
			
			//validate file, there should be one error
			TestReporter reporter = new TestReporter();
			sourceValidator.validate(context, reporter);
			assertFalse("There should be an error message reported for not conforming to the DTD " + testFile, !reporter.isMessageReported());
		} catch(ValidationException e) {
			fail("Could not validate test file " + testFile + ": " + e.getMessage());
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
