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
package org.eclipse.jst.jsp.core.tests.validation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Tests JSP Java Validator
 */
public class JSPJavaValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "bug_87351";

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

	/**
	 * Tests if jsp fragments are validated when preference is set to true
	 * 
	 * @throws Exception
	 */
	public void testValidatingFragments() throws Exception {
		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/ihaveerrors.jspf";
		helper.setURI(filePath);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		String validate = JSPFContentProperties.getProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, false);
		JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, Boolean.toString(true));
		validator.validate(helper, reporter);

		if (validate != null) {
			JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, validate);
		}
		assertTrue("jspf was not validated when it should have been", !reporter.getMessages().isEmpty());
	}

	/**
	 * Tests if jsp fragments are not validated when preference is set to
	 * false
	 * 
	 * @throws Exception
	 */
	public void testNoValidatingFragments() throws Exception {
		JSPValidator validator = new JSPValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/ihaveerrors.jspf";
		helper.setURI(filePath);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		String validate = JSPFContentProperties.getProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, false);
		JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, Boolean.toString(false));
		validator.validate(helper, reporter);

		if (validate != null) {
			JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, validate);
		}
		assertTrue("jspf was validated when it should not have been", reporter.getMessages().isEmpty());
	}
}
