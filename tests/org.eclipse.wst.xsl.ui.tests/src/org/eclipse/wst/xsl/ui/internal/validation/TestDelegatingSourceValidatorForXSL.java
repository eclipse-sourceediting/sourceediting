/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
import org.eclipse.wst.xsl.ui.tests.XSLUITestsPlugin;

/**
 * Test the XML delegating source validator.
 *
 */
public class TestDelegatingSourceValidatorForXSL extends TestCase 
{
	IValidator sourceValidator = new DelegatingSourceValidatorForXSL().getDelegateValidator();
	
	/**
	 * Test that files that contain non-8bit chars are validated
	 * correctly. i.e. Do not produce incorrect validation messages.
	 */
	@SuppressWarnings("restriction")
	public void testDocbookStylesheetHTML()
	{
		String projName = "net.sourceforge.docbook";
		String fileName1 = "docbook.xsl";
		
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "html" + File.separator + fileName1;
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(validateFilePath));
		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate Docbook HTML stylesheet.");
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
		
		assertFalse("Messages were reported on valid file 1.", reporter.isMessageReported());		
	}
	
	private class TestReporter implements IReporter
	{
		protected boolean messageReported = false;
		
		public TestReporter(){
			
		}
		
		public void addMessage(IValidator origin, IMessage message) {
			messageReported = true;
			
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
