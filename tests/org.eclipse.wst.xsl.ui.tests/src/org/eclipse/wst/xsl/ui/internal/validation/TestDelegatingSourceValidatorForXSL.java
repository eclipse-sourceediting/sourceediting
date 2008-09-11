/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - initial API and implementation, based off of
 *                    XML Source Delgating Validator tests.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;
import org.eclipse.wst.xsl.ui.tests.XSLUITestsPlugin;
//import org.eclipse.wst.xsl.docbook.core.DocbookPlugin;

/**
 * Test the XML delegating source validator.
 *
 */
public class TestDelegatingSourceValidatorForXSL extends AbstractXSLUITest 
{
	DelegatingSourceValidatorForXSL sourceValidator =  new DelegatingSourceValidatorForXSL();
	
	public TestDelegatingSourceValidatorForXSL() {
		
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	
	private String getxslTestFilesProjectName() {
		return "xsltestfiles";
	}
	/**
	 * Test XPath 2.0 validation fails
	 */
	public void testXSLT2XPath20Fails()
	{
		String projName = getxslTestFilesProjectName();
		String fileName1 = "ChangeRequestsByResponsibility.xsl";

		String validateFilePath = projName + File.separator + fileName1;

		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(validateFilePath));
		if (file != null && !file.exists()) {
			fail("Unable to locate " + fileName1 + " stylesheet.");
		}
		WorkbenchContext context = new WorkbenchContext();
		List fileList = new ArrayList();
		fileList.add(File.separator + validateFilePath);
		context.setValidationFileURIs(fileList);
		TestReporter reporter = new TestReporter();
		try{
			sourceValidator.validate(context, reporter);
		}
		catch(ValidationException e){
			e.printStackTrace();
		}
		
		assertTrue("No Messages were reported on file with invalid XPath 1.0.", reporter.isMessageReported());		
	}
	
	public void testValidXSLT()
	{
		String projName = getxslTestFilesProjectName();
		String fileName1 = "ListAllChangeRequests.xsl";

		String validateFilePath = projName + File.separator + fileName1;

		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(validateFilePath));
		if (file != null && !file.exists()) {
			fail("Unable to locate " + fileName1 + " stylesheet.");
		}
		WorkbenchContext context = new WorkbenchContext();
		List fileList = new ArrayList();
		fileList.add(File.separator + validateFilePath);
		context.setValidationFileURIs(fileList);
		TestReporter reporter = new TestReporter();
		try{
			sourceValidator.validate(context, reporter);
		}
		catch(ValidationException e){
			e.printStackTrace();
		}
		
		assertFalse("Messages were reported on " + fileName1 + ".", reporter.isMessageReported());		
	}
	
	
	
	private class TestReporter implements IReporter
	{
		protected boolean messageReported = false;
		
		public TestReporter(){
			
		}
		
		public void addMessage(IValidator origin, IMessage message) {
			if (message.getSeverity() == IMessage.HIGH_SEVERITY)
			{
				messageReported = true;
			}
		}
		
		public boolean isMessageReported()
		{
			return messageReported;
		}

		public void displaySubtask(IValidator validator, IMessage message) {
			// TODO Auto-generated method stub
			
		}

		public List getMessages() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeAllMessages(IValidator origin, Object object) {
			// TODO Auto-generated method stub
			
		}

		public void removeAllMessages(IValidator origin) {
			// TODO Auto-generated method stub
			
		}

		public void removeMessageSubset(IValidator validator, Object obj, String groupName) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
