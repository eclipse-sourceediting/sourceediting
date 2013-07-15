/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.contentassist;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.ui.tests.internal.TestProjectSetup;

public class DuplicatesTests extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test for Duplicate Content Assist Proposals";

	/**
	 * <p>
	 * Test project setup for this test.
	 * </p>
	 */
	private static TestProjectSetup fTestProjectSetup;
	
	/**
	 * <p>
	 * Default constructor
	 * <p>
	 * <p>
	 * Use {@link #suite()}
	 * </p>
	 * 
	 * @see #suite()
	 */
	public DuplicatesTests() {
		super(TEST_NAME);
	}

	/**
	 * <p>
	 * Constructor that takes a test name.
	 * </p>
	 * <p>
	 * Use {@link #suite()}
	 * </p>
	 * 
	 * @param name
	 *            The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public DuplicatesTests(String name) {
		super(name);
	}

	/**
	 * <p>
	 * Use this method to add these tests to a larger test suite so set up and tear down can be
	 * performed
	 * </p>
	 * 
	 * @return a {@link TestSetup} that will run all of the tests in this class
	 *         with set up and tear down.
	 */

	public static Test suite() {
		TestSuite ts = new TestSuite(DuplicatesTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}

	public void testForDuplicates_Expression1() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "ConstructorCamelCase.html", 10, 7);
	}

	public void testForDuplicates_Expression2() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "ConstructorCamelCase.html", 12, 8);
	}

	public void testForDuplicates_Expression3() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "JSClasses.html", 10, 6);
	}

	public void testForDuplicates_Expression4() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "JSClasses.html", 14, 10);
	}

	public void testForDuplicates_Expression5() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "JSClasses.html", 18, 5);
	}

	public void testForDuplicates_Expression6() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "Test.html", 8, 7);
	}

	public void testForDuplicates_Expression7() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "ReferenceInMemberAndStaticFunctions.html", 7, 6);
	}

	public void testForDuplicates_Expression8() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "InnerOuter.html", 10, 1);
	}

	public void testForDuplicates_Expression9() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalFunctions.html", 10, 3);
	}

	public void testForDuplicates_Expression10() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalFunctions.html", 12, 5);
	}

	public void testForDuplicates_Expression11() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 8, 1);
	}

	public void testForDuplicates_Expression12() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 8, 0);
	}

	public void testForDuplicates_Expression13() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "Global.html", 6, 0);
	}

	public void testForDuplicates_Expression14() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "Global.html", 10, 7);
	}

	public void testForDuplicates_Expression15() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "Crazy.html", 10, 4);
	}
}