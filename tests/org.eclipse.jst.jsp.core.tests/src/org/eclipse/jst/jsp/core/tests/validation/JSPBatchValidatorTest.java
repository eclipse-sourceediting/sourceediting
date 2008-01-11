/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
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
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Tests JSP Batch Validator
 */
public class JSPBatchValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "batchvalidation";

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
	 * Tests validating 2 jsp files. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=214441
	 * 
	 * @throws Exception
	 */
	public void testValidating2Files() throws Exception {
		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath1 = "/" + PROJECT_NAME + "/WebContent/ihaveerrors.jsp";
		IFile file1 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath1));
		assertTrue(file1.exists());

		String filePath2 = "/" + PROJECT_NAME + "/WebContent/ihaveerrors2.jsp";
		IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath1));
		assertTrue(file2.exists());

		helper.setURIs(new String[]{filePath1, filePath2});

		validator.validate(helper, reporter);
		assertTrue("jsp errors were not found in both files", reporter.getMessages().size() == 2);
	}
}
