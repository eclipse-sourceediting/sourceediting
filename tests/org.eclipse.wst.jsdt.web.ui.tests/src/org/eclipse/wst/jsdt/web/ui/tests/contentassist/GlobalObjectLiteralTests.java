/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.contentassist;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.ui.tests.internal.TestProjectSetup;

public class GlobalObjectLiteralTests extends TestCase {

	private static final String TEST_NAME = "Test Global Object Literals JavaScript Content Assist";
	
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
	public GlobalObjectLiteralTests() {
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
	public GlobalObjectLiteralTests(String name) {
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
		TestSuite ts = new TestSuite(GlobalObjectLiteralTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}
	public void testFindGlobalObjectLiteral_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "org : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalObjectLiterals.html", 6, 0, expectedProposals);
	}

	public void testFindGlobalObjectLiteral_1() throws Exception {
		String[][] expectedProposals = new String[][] { { "org : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalObjectLiterals.html", 8, 1, expectedProposals);
	}

	public void _testFindFieldOnGlobalObjectLiteral_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "eclipse : {} - {}", "eclipse2 : {} - {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalObjectLiterals.html", 10, 4, expectedProposals);
	}

	public void _testFindFieldOnGlobalObjectLiteral_1() throws Exception {
		String[][] expectedProposals = new String[][] { { "eclipse : {} - {}", "eclipse2 : {} - {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalObjectLiterals.html", 10, 5, expectedProposals);
	}

	public void _testFindFunctionOnFieldOnGlobalObjectLiteral_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "fun() - {}", "crazy() - {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalOjectLiterals.html", 12, 12, expectedProposals);
	}
	
	public void testFindDuplicateGlobalObjectLiteral_Expression_0() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalObjectLiterals.html", 6, 0);
	}

	public void testFindDuplicateGlobalObjectLiteral_Expression_1() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalObjectLiterals.html", 8, 1);
	}

	public void testFindDuplicateFieldOnGlobalObjectLiteral_Expression_2() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalObjectLiterals.html", 10, 4);
	}

	public void testFindDuplicateFieldOnGlobalObjectLiteral_0() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalObjectLiterals.html", 10, 5);
	}

	public void testFindDuplicateFunctionOnFieldOnGlobalObjectLiteral_1() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "GlobalObjectLiterals.html", 12, 12);
	}
}