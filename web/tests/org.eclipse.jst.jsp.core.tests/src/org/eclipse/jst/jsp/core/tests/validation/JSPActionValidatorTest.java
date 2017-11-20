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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.validation.JSPActionValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public class JSPActionValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "testvalidatejspactions";
	private static final String UNDEFINED_ATTR_IDONT = "idont";
	private static final String REQUIRED_ATTR_NAME = "name";
	private static final String NONEMPTY_INLINE_TAG_NAME = "libtags:emptybodycontent";

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

		boolean foundError1 = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPDirectiveValidator_6, UNDEFINED_ATTR_IDONT), 4);
		if (foundError1)
			foundError1 = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPDirectiveValidator_6, UNDEFINED_ATTR_IDONT), 12);
		if (foundError1)
			foundError1 = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPDirectiveValidator_6, UNDEFINED_ATTR_IDONT), 13);

		assertTrue("jsp action validator did not detect undefined attributes", foundError1);
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

		boolean foundError = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPDirectiveValidator_5, REQUIRED_ATTR_NAME), 11);
		if (foundError)
			foundError = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPDirectiveValidator_5, REQUIRED_ATTR_NAME), 12);

		assertTrue("jsp action validator did not detect missing required attributes", foundError);
	}

	private boolean errorMessageFound(IReporter reporter, String errorMessage, int errorLineNumber) {
		boolean foundError = false;
		List messages = reporter.getMessages();
		Iterator iter = messages.iterator();
		while (iter.hasNext() && !foundError) {
			IMessage message = (IMessage) iter.next();
			int lineNumber = message.getLineNumber();
			String messageText = message.getText();

			if (lineNumber == errorLineNumber && messageText.startsWith(errorMessage))
				foundError = true;
		}
		return foundError;
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
		
		StringBuffer error = new StringBuffer("jsp action validator found errors when it should not have");
		List messages = reporter.getMessages();
		for (int i = 0; i < messages.size(); i++) {
			error.append('\n');
			error.append(((IMessage) messages.get(i)).getText());
		}

		assertTrue(error.toString(), reporter.getMessages().isEmpty());
	}

	/**
	 * Tests if non-empty inline tags are flagged as warnings
	 * 
	 * @throws Exception
	 */
	public void testNonEmptyInlineTag() throws Exception {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=203254
		JSPActionValidator validator = new JSPActionValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath = "/" + PROJECT_NAME + "/WebContent/nonemptyinlinetag.jsp";
		assertTrue("unable to find file: " + filePath, ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath)).exists());

		helper.setURI(filePath);
		validator.validate(helper, reporter);

		boolean foundError = errorMessageFound(reporter, NLS.bind(JSPCoreMessages.JSPActionValidator_0, NONEMPTY_INLINE_TAG_NAME), 10);

		assertTrue("jsp action validator had problems detecting an error with content in an inline tag", foundError);
	}
}
