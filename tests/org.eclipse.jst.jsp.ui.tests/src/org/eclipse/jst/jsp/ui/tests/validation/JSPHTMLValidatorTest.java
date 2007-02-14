/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.validation;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUtil;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Tests HTML validator on jsp file
 */
public class JSPHTMLValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "bug_143209";

	protected void setUp() throws Exception {
		super.setUp();
		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists()) {
			ProjectUtil.createProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			ProjectUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}

	/**
	 * Tests jsp expression in html attributes in jsp file
	 * 
	 * @throws Exception
	 */
	public void testJSPinAttributes() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/usejspinattribute.jsp";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp in attributes are errors when they should not be (in .jsp)", reporter.getMessages().isEmpty());
	}

	/**
	 * Tests jsp expression in html attributes in html file
	 * 
	 * @throws Exception
	 */
	public void testJSPinAttributesHTML() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/usejspinattribute.html";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp in attributes are not errors when they should be (in .html)", !reporter.getMessages().isEmpty());
	}

	/**
	 * Tests bad attribute names in jsp file false
	 * 
	 * @throws Exception
	 */
	public void testBadAttributeName() throws Exception {
		JSPContentValidator validator = new JSPContentValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/badattributenames.jsp";
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("bad attribute name is not error when it should be", !reporter.getMessages().isEmpty());
	}
}
