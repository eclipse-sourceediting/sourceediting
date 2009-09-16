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
package org.eclipse.jst.jsp.core.tests.validation;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.validation.JSPDirectiveValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

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

}
