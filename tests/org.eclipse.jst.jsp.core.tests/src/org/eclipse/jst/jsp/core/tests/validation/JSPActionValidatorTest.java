/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.validation.JSPActionValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public class JSPActionValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "testvalidatejspactions";

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
	 * Tests if unknown attributes are detected
	 * 
	 * @throws Exception
	 */
	public void testUknownAttribute() throws Exception {
		JSPActionValidator validator = new JSPActionValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/undefinedattribute.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());
		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp action validator did not detect undefined attributes", reporter.getMessages().size() == 3);
	}

	/**
	 * Tests if missing required attributes are detected
	 * 
	 * @throws Exception
	 */
	public void testMissingRequiredAttribute() throws Exception {
		JSPActionValidator validator = new JSPActionValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/norequiredattribute.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());

		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp action validator did not detect missing required attributes", reporter.getMessages().size() == 2);
	}

	/**
	 * Tests if missing required attributes are detected
	 * 
	 * @throws Exception
	 */
	public void testAttributesCorrect() throws Exception {
		JSPActionValidator validator = new JSPActionValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/worksfine.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());

		helper.setURI(filePath);
		validator.validate(helper, reporter);

		assertTrue("jsp action validator found errors when it should not have", reporter.getMessages().isEmpty());
	}
}
