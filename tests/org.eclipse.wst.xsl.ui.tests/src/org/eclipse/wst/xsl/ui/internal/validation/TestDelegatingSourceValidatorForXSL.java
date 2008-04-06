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
import org.eclipse.wst.xsl.ui.tests.UnzippedProjectTester;
import org.eclipse.wst.xsl.ui.tests.XSLUITestsPlugin;
//import org.eclipse.wst.xsl.docbook.core.DocbookPlugin;

/**
 * Test the XML delegating source validator.
 *
 */
public class TestDelegatingSourceValidatorForXSL extends UnzippedProjectTester 
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
	
	/**
	 * Test that files that the DocbookStylesheet for HTML output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetHTML()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "docbook.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "html" + File.separator + fileName1;

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
	
	/**
	 * Test that files that the DocbookStylesheet for FO output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetFO()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "docbook.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "fo" + File.separator + fileName1;

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
	
	/**
	 * Test that files that the DocbookStylesheet for Eclipse Help output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetEclipse()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "eclipse.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "eclipse" + File.separator + fileName1;

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
	
	/**
	 * Test that files that the DocbookStylesheet for Eclipse Help output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetJavaHelp()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "javahelp.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "javahelp" + File.separator + fileName1;

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
		
		assertFalse("Messages were reported on valid " + fileName1, reporter.isMessageReported());		
	}
	
	/**
	 * Test that files that the DocbookStylesheet for Eclipse Help output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetXHTML()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "docbook.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "xhtml" + File.separator + fileName1;

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
		
		assertFalse("Messages were reported on valid " + fileName1, reporter.isMessageReported());		
	}
	
	/**
	 * Test that files that the DocbookStylesheet for Eclipse Help output is valid.
	 * i.e. Do not produce incorrect validation messages.
	 */
	public void testDocbookStylesheetXHTMLChunk()
	{
		String projName = "net.sourceforge.docbook.stylesheets";
		String fileName1 = "chunk.xsl";

		String validateFilePath = projName + File.separator + "docbook-xsl-1.73.2" + File.separator + "xhtml" + File.separator + fileName1;

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
		
		assertFalse("Messages were reported on valid " + fileName1, reporter.isMessageReported());		
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
