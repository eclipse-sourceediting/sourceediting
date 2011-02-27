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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the XML delegating source validator.
 * 
 */
public class TestDelegatingSourceValidatorForXSL extends AbstractXSLUITest {
	DelegatingSourceValidatorForXSL sourceValidator = new DelegatingSourceValidatorForXSL();

	public TestDelegatingSourceValidatorForXSL() {

	}

	private String getxslTestFilesProjectName() {
		return "xsltestfiles";
	}

	@Test
	public void testXSLT2XPathPasses() throws Exception {
		String fileName1 = "ChangeRequestsByResponsibility.xsl";
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), fileName1);
		
		TestReporter reporter = new TestReporter();
		sourceValidator.validate(context, reporter);

		assertFalse("Messages were reported on file with valid XPath 2.0",
				reporter.isMessageReported());
	}
	
	@Test
	public void testXSLT2_2Passes() throws Exception {
		String fileName1 = "ChangeRequestsByStatus.xsl";
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), fileName1);
		
		TestReporter reporter = new TestReporter();
		sourceValidator.validate(context, reporter);

		assertFalse("Messages were reported on file with valid XSLT 2.0",
				reporter.isMessageReported());
	}
	

	@Test
	public void testValidXSLT() throws Exception {
		String fileName1 = "ListAllChangeRequests.xsl"; 
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), fileName1);
		TestReporter reporter = new TestReporter();
		sourceValidator.validate(context, reporter);

		assertFalse("Messages were reported on " + fileName1 + ".", reporter
				.isMessageReported());
	}
	
	@Test
	public void testXSLFunctionsWithParms() throws Exception {
		String fileName = "bug290286.xsl";
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), fileName);
		TestReporter reporter = new TestReporter();
		sourceValidator.validate(context, reporter);
		assertFalse("Errors reported with XSLT 2.0 with Functions: " + fileName + ".", reporter.isMessageReported());
	}
	
	@Test
	public void testXSL20Transform() throws Exception {
		String fileName = "XSLT20Transform.xsl";
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), fileName);
		TestReporter reporter = new TestReporter();
		sourceValidator.validate(context, reporter);
		assertFalse("Errors reported with XSLT 2.0 with Transform statement: " + fileName + ".", reporter.isMessageReported());
	}
	

	private class TestReporter implements IReporter {
		protected boolean messageReported = false;

		public TestReporter() {

		}

		public void addMessage(IValidator origin, IMessage message) {
			if (message.getSeverity() == IMessage.HIGH_SEVERITY) {
				messageReported = true;
			}
		}

		public boolean isMessageReported() {
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

		public void removeMessageSubset(IValidator validator, Object obj,
				String groupName) {
			// TODO Auto-generated method stub

		}

	}

	@Test
	public void testDelegatingSourceValidatorNPEwithNoAttributeValue() throws Exception {
		WorkbenchContext context = setupFile(getxslTestFilesProjectName(), "bug272760.xsl");
		TestReporter reporter = new TestReporter();
		try {
			sourceValidator.validate(context, reporter);
		} catch (ValidationException e) {

		} catch (NullPointerException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			fail(out.toString());
		}
	}

	protected WorkbenchContext setupFile(String projName, String fileName1) {
		String validateFilePath = projName + File.separator + fileName1;

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(validateFilePath));
		if (file != null && !file.exists()) {
			fail("Unable to locate " + fileName1 + " stylesheet.");
		}
		WorkbenchContext context = new WorkbenchContext();
		List fileList = new ArrayList();
		fileList.add(File.separator + validateFilePath);
		context.setValidationFileURIs(fileList);
		return context;
	}
}
