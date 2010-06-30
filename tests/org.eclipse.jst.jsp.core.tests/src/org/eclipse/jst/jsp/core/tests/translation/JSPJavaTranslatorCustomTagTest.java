/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.translation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.jst.jsp.core.tests.validation.ReporterForTest;
import org.eclipse.jst.jsp.core.tests.validation.ValidationContextForTest;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public class JSPJavaTranslatorCustomTagTest extends TestCase {

	static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
//	private static byte[] creationLock = new byte[0];

	public JSPJavaTranslatorCustomTagTest() {
	}

	public JSPJavaTranslatorCustomTagTest(String name) {
		super(name);
	}

	String wtp_autotest_noninteractive = null;

	protected void setUp() throws Exception {
		super.setUp();
		String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, wtp_autotest_noninteractive);
	}

	/**
	 * Tests jsp translation when custom tag used
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=31
	 * 
	 * @throws Exception
	 */
	public void test_310085() throws Exception {
		String projectName = "bug_310085";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		IFile file = project.getFile("WebContent/test310085.jsp");
		assertTrue(file.exists());
				
		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		helper.setURI(file.getFullPath().toOSString());
		validator.validate(helper, reporter);
		
		assertTrue("found jsp java error for custom tag", reporter.getMessages().isEmpty());
		
	}

	}
