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
package org.eclipse.jst.jsp.core.tests.validation;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.validation.JSPBatchValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPDirectiveValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.util.Sorter;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;

public class JSPDirectiveValidatorTest extends TestCase {
	
	private String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "testvalidatejspdirectives";
	private static final String FRAGMENT_NAME = "fragmentThatDoesntExist.jspf";

	protected void setUp() throws Exception {
		super.setUp();
		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists()) {
			BundleResourceUtil.createSimpleProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}
	
	public void testBug265710Expression() throws Exception {
		JSPDirectiveValidator validator = new JSPDirectiveValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/bug265710expression.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());
		helper.setURI(filePath);
		validator.validate(helper, reporter);
		
		if (reporter.getMessages().size() > 0) {
			Iterator it = reporter.getMessages().iterator();
			while (it.hasNext()) {
				IMessage message = (IMessage) it.next();
				if (message.getLineNumber() == 14 && message.getSeverity() == IMessage.HIGH_SEVERITY) {
					fail("JSP Directive Validator flagged a JSP expression in the import directive");
				}
			}
		}
	}
	
	public void testBug265710El() throws Exception {
		JSPDirectiveValidator validator = new JSPDirectiveValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/bug265710el.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());
		helper.setURI(filePath);
		validator.validate(helper, reporter);
		
		if (reporter.getMessages().size() > 0) {
			Iterator it = reporter.getMessages().iterator();
			while (it.hasNext()) {
				IMessage message = (IMessage) it.next();
				if (message.getLineNumber() == 11 && message.getSeverity() == IMessage.HIGH_SEVERITY) {
					fail("JSP Directive Validator flagged JSP EL in the import directive");
				}
			}
		}
	}
	
	public void testIncludeDirective() throws Exception {
		JSPDirectiveValidator validator = new JSPDirectiveValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/testinclude.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());
		helper.setURI(filePath);
		validator.validate(helper, reporter);
		
		boolean foundMissingInclude = false;
		boolean foundIncludeWithError = false;
		if (reporter.getMessages().size() > 0) {
			Iterator it = reporter.getMessages().iterator();
			boolean foundError = false;
			while (it.hasNext() && !foundError) {
				IMessage message = (IMessage) it.next();
				if (message.getLineNumber() == 11)
					foundIncludeWithError = true;
				else if (message.getLineNumber() == 12) {
					String expectedMsg = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_4, new String[] { FRAGMENT_NAME, "/" + PROJECT_NAME + "/WebContent/" + FRAGMENT_NAME });
					if (!expectedMsg.equals(message.getText()))
						fail("Error found on line 12, but was not a missing fragment error.");
					foundMissingInclude = true;
					break;
				}
			}
		}
		
		assertFalse("JSP Directive Validator reported an error for a fragment that should be locatable.", foundIncludeWithError);
		assertTrue("JSP Directive Validator did not report the missing fragment.", foundMissingInclude);
	}
	
	public void testIncludeDirectiveXML() throws Exception {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
		"<jsp:root xmlns:jsp=\"http://java.sun.com/JSP/Page\" version=\"2.0\">\n"+
		"<jsp:directive.include  file=\"missing.jspf\"/>\n";
		runNegativeTest("/"+ getName() + "/test.jsp", contents, new JSPBatchValidator(), "Fragment \"missing.jspf\" was not found at expected path /testIncludeDirectiveXML/missing.jspf");
	}
	
	public void testIncludeDirective2() throws Exception {
		String contents = "<%@include file=\"missing.jspf\"%>\n";
		runNegativeTest("/"+ getName() + "/test.jsp", contents, new JSPBatchValidator(), "Fragment \"missing.jspf\" was not found at expected path /testIncludeDirective2/missing.jspf");
	}
	
	public void testIncludeAction() throws Exception {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
		"<jsp:root xmlns:jsp=\"http://java.sun.com/JSP/Page\" version=\"2.0\">\n"+
		"<jsp:include page=\"missing.jspf\"/>\n";
		runNegativeTest("/"+ getName() + "/test.jsp", contents, new JSPBatchValidator(), "Fragment \"missing.jspf\" was not found at expected path /testIncludeAction/missing.jspf");
	}
	
	public void testIncludeMappedURL() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("delos");
		if (!project.exists()) {
			BundleResourceUtil.createSimpleProject("delos", null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/delos", "/delos");
		}
		assertTrue("project could not be created", project.exists());

		JSPDirectiveValidator validator = new JSPDirectiveValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/delos/WebContent/1.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());
		helper.setURI(filePath);
		validator.validate(helper, reporter);
		assertTrue("problems were found in JSP file", reporter.getMessages().isEmpty());
		
		try {
			project.delete(true, null);
		}
		catch (CoreException e) {
			// failure to clean up shouldn't fail the test
		}
	}

	/**
	 * The purpose of a validator is to generate messages.  Let us make sure the expected messages are generated.
	 * @param filePath
	 * @param content
	 * @param validator
	 * @param expectedProblemMessages
	 * @throws Exception
	 */
	protected void runNegativeTest(String filePath, String content, AbstractValidator validator, String expectedProblemMessages) throws Exception {
		IPath path = new Path (filePath);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
		if (!project.exists()) {
			BundleResourceUtil.createSimpleProject(path.segment(0), null, new String[]{JavaCore.NATURE_ID});
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		file.create(new ByteArrayInputStream(content.getBytes("utf8")), true, null);
		ValidationResult result = validator.validate(file, IResourceDelta.ADDED, new ValidationState(), new NullProgressMonitor());
		assertEquals(expectedProblemMessages, sortMessages(result.getReporter(new NullProgressMonitor()).getMessages()));
	}
	
	static class ValidatorMessageSorter extends Sorter {
		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.Sorter#compare(java.lang.Object, java.lang.Object)
		 */
		public boolean compare(Object elementOne, Object elementTwo) {
			if (elementOne instanceof ValidationMessage)
				return ((ValidationMessage) elementTwo).getMessage().compareTo(((ValidationMessage) elementOne).getMessage()) > 0;
			return ((IMessage) elementTwo).getOffset() > ((IMessage) elementOne).getOffset();
		}
	}

	/**
	 * @param messages
	 * @return
	 */
	private String sortMessages(List messages) {
		Object[] sorted = new ValidatorMessageSorter().sort(messages.toArray());
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < sorted.length; i++) {
			if (sorted[i] instanceof ValidationMessage)
				s.append(((ValidationMessage) sorted[i]).getMessage());
			else
				s.append(((IMessage) sorted[i]).getText());
		}
		return s.toString();
	}
}
