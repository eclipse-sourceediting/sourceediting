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

import org.eclipse.wst.jsdt.web.ui.tests.internal.TestProjectSetup;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GlobalFunctionTests extends TestCase {

	private static final String TEST_NAME = "Test Global Functions JavaScript Content Assist";
	
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
	public GlobalFunctionTests() {
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
	public GlobalFunctionTests(String name) {
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
		TestSuite ts = new TestSuite(GlobalFunctionTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}

	public void testFindFunctions_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcOne() - Global", "funcTwo() : String - Global", "funcThree(paramOne) - Global",
						"funcFour(paramOne, paramTwo) : Number - Global",
						"funcFive(Number paramOne, String paramTwo) : String - Global",
						"funcSix(paramOne, String paramTwo) : Number - Global",
						"funcSeven(String paramOne, paramTwo) : Number - Global",
						"funcEight(paramOne) : String - Global", "funcNine(paramOne) : Number - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalFunctions.html", 6, 0, expectedProposals);
	}

	public void testFindFunctions_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcOne() - Global", "funcTwo() : String - Global", "funcThree(paramOne) - Global",
						"funcFour(paramOne, paramTwo) : Number - Global",
						"funcFive(Number paramOne, String paramTwo) : String - Global",
						"funcSix(paramOne, String paramTwo) : Number - Global",
						"funcSeven(String paramOne, paramTwo) : Number - Global",
						"funcEight(paramOne) : String - Global", "funcNine(paramOne) : Number - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalFunctions.html", 8, 1, expectedProposals);
	}

	public void testFindFunctions_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTwo() : String - Global", "funcThree(paramOne) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalFunctions.html", 10, 5, expectedProposals);
	}
	
	public void testFindFunctions_ExpressionStarted_CamelCase() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTwo() : String - Global", "funcThree(paramOne) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalFunctions.html", 12, 2, expectedProposals);
	}
	

	public void testNamedFunctionsAssignedToVariables_ExpressionNotStarted() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "foo1 : Function - Global", "foo1(param2) - Global", "foo2 : Function - Global",
						"foo2(param3, param4) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 6, 0,
				expectedProposals);
	}

	public void testNamedFunctionsAssignedToVariables_ExpressionStarted1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "foo1 : Function - Global", "foo1(param2) - Global", "foo2 : Function - Global",
						"foo2(param3, param4) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 8, 1,
				expectedProposals);
	}

	public void testNamedFunctionsAssignedToVariables_ExpressionNotStarted_NegativeTest()
			throws Exception {
		String[][] expectedProposals = new String[][] { { "foo1Ignored", "foo2Ignored" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 6, 0,
				expectedProposals, true, false);
	}

	public void testNamedFunctionsAssignedToVariables_ExpressionStarted1_NegativeTest()
			throws Exception {
		String[][] expectedProposals = new String[][] { { "foo1Ignored", "foo2Ignored" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "NamedFunctionAssignedToVariables.html", 8, 1,
				expectedProposals, true, false);
	}
}