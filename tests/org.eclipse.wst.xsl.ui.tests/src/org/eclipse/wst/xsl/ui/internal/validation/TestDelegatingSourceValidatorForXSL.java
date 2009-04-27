/*******************************************************************************
 * Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - initial API and implementation, based off of
 *                    XML Source Delgating Validator tests.
 *     IBM Corporation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.validation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalHelper;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;

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

		String projName = getxslTestFilesProjectName();
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			if(!project.isAccessible())
				project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
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
	public void testXSLT2XPathPasses()
	{
		String projName = getxslTestFilesProjectName();
		String fileName1 = "ChangeRequestsByResponsibility.xsl";

		String validateFilePath = projName + File.separator + fileName1;
		
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
		
		assertFalse("Messages were reported on file with valid XPath 2.0", reporter.isMessageReported());
	}
	
	public void testValidXSLT()
	{
		String projName = getxslTestFilesProjectName();
		String fileName1 = "ListAllChangeRequests.xsl";

		String validateFilePath = projName + File.separator + fileName1;

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
//	public void testDelegatingSourceValidatorNPEwithNoAttributeValue() throws CoreException, ValidationException, UnsupportedEncodingException, IOException {
//		  String testContent = 
//		  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//		  "<!--\n" + 
//		  "	Copyright (c) IBM Corporation and others 2009. This page is made available under license. For full details see the LEGAL in the documentation book that contains this page.\n" + 
//		  "	\n" + 
//		  "	All Platform Debug contexts, those for org.eclipse.debug.ui, are located in this file\n" + 
//		  "	All contexts are grouped by their relation, with all relations grouped alphabetically.\n" + 
//		  "-->\n" + 
//		  "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" + 
//		  "<xsl:output method=\"html\" encoding=\"iso-8859-1\" doctype-public=\"-//W3C//DTD XHTML 1.0 Transitional//EN\" doctype-system=\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"/>\n" + 
//		  "<xsl:template match=\"/\">\n" + 
//		  "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
//		  "	<head>\n" + 
//		  "	<title>Compare Details</title>\n" + 
//		  "	</head>\n" + 
//		  "	<body>\n" + 
//		  "		<div align=\"left\" class=\"main\">\n" + 
//		  "			<xsl:apply-templates select=\"deltas\"/>\n" + 
//		  "		</div>\n" + 
//		  "		<p>\n" + 
//		  "			<a href=\"http://validator.w3.org/check?uri=referer\">\n" + 
//		  "				<img src=\"http://www.w3.org/Icons/valid-xhtml10-blue\" alt=\"Valid XHTML 1.0 Strict\" height=\"31\" width=\"88\" />\n" + 
//		  "			</a>\n" + 
//		  "		</p>\n" + 
//		  "	</body>\n" + 
//		  "</html>\n" + 
//		  "</xsl:template>\n" + 
//		  "<xsl:template match=\"deltas\">\n" + 
//		  "	<table border=\"1\" width=\"90%\">\n" + 
//		  "		<tr bgcolor=\"#CC9933\">\n" + 
//		  "			<td>\n" + 
//		  "				<h3>\n" + 
//		  "					<a href=\"javascript:void(0)\" class=\"typeslnk\" onclick=\"expand(this)\">\n" +
//		  // faulty source line follows
//		  "						<b>List of <xsl:value-of select= > Details</b>\n" + 
//		  "					</a>\n" + 
//		  "				</h3>\n" + 
//		  "			</td>\n" + 
//		  "		</tr>\n" + 
//		  "		<xsl:for-each select=\"deltas/delta[@compatible='true']\">\n" + 
//		  "		<xsl:sort select=\"@compatible\"/>\n" + 
//		  "			<tr>\n" + 
//		  "				<td>\n" + 
//		  "					<xsl:value-of disable-output-escaping=\"yes\" select=\"@message\"/>\n" + 
//		  "				</td>\n" + 
//		  "			</tr>\n" + 
//		  "		</xsl:for-each>\n" + 
//		  "	</table>\n" + 
//		  "</xsl:template>\n" + 
//		  "</xsl:stylesheet>\n";
//		  
//		String projectName = "testDelegatingSourceValidatorNPEwithNoAttributeValue";
//		String fileName = "testNPE.xsl";
//		DelegatingSourceValidatorForXSL delegatingSourceValidatorForXSL = new DelegatingSourceValidatorForXSL();
//		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
//		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//		project.create(description, new NullProgressMonitor());
//		project.open(new NullProgressMonitor());
//		IFile file = project.getFile(fileName);
//		file.create(new ByteArrayInputStream(testContent.getBytes("utf8")), true, null);
//		IDOMModel model = null;
//		try {
//			model = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(file);
//			assertTrue("missing content in test file", model.getStructuredDocument().getLength() > 1200);
//			IncrementalHelper incrementalHelper = new IncrementalHelper(model.getStructuredDocument(), project);
//			incrementalHelper.setURI(file.getFullPath().toString());
//			TestReporter reporter = new TestReporter();
//			try {
//				delegatingSourceValidatorForXSL.validate(incrementalHelper, reporter);
//				delegatingSourceValidatorForXSL.cleanup(reporter);
//			}
//			catch (NullPointerException e) {
//				StringWriter out = new StringWriter();
//				e.printStackTrace(new PrintWriter(out));
//				fail(out.toString());
//			}
//		}
//		finally {
//			if (model != null)
//				model.releaseFromRead();
//		}
//		project.delete(true, null);
//	}
}
