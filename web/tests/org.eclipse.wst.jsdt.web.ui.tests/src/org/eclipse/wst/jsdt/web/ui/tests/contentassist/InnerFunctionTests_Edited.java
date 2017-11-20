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

public class InnerFunctionTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Inner Functions JavaScript Content Assist after Edit";
	
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
	public InnerFunctionTests_Edited() {
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
	public InnerFunctionTests_Edited(String name) {
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
		TestSuite ts = new TestSuite(InnerFunctionTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {

			public void additionalSetUp() throws Exception {
				this.editFile("InnerFunctions.js", 1, 22, 0, "Edit");
				this.editFile("InnerFunctions.js", 5, 17, 0, "Edit");
				this.editFile("InnerFunctions.js", 9, 13, 0, "Edit");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testFindInnerFunctions_le_EmptyLine() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTen(paramEleven, paramTwelve) - Global",
						"funcTenInnerEdit2 : Function - Global", "funcTenInnerEdit2(param1, param2) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerFunctions.html", 6, 0, expectedProposals);
	}

	public void testFindInnerFunctions_ExpressionStarted() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTen(paramEleven, paramTwelve) - Global",
						"funcTenInnerEdit2 : Function - Global", "funcTenInnerEdit2(param1, param2) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerFunctions.html", 8, 5, expectedProposals);
	}

	public void testFindInnerFunctions_CamelCase() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTen(paramEleven, paramTwelve) - Global",
						"funcTenInnerEdit2 : Function - Global", "funcTenInnerEdit2(param1, param2) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerFunctions.html", 10, 2, expectedProposals);
	}

	public void testFindInnerFunctions_EmptyLine_NegativeTest() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTenInnerEdit : Function - Global", "funcTenInnerEdit1 : Function - Global",
						"funcTenInnerEdit(newParam111, newParam222) : String - Global",
						"funcTenInnerEdit1(param1) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerFunctions.html", 6, 0, expectedProposals, true, false);
	}

	public void testFindInnerFunctions_ExpresionStarted_NegativeTest() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "funcTenInnerEdit : Function - Global", "funcTenInnerEdit1 : Function - Global",
						"funcTenInnerEdit(newParam111, newParam222) : String - Global",
						"funcTenInnerEdit1(param1) - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerFunctions.html", 8, 5, expectedProposals, true, false);
	}

	public void testFindDuplicateInnerFunctions_EmptyLine() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "InnerFunctions.html", 6, 0);
	}

	public void testFindDuplicateInnerFunctions_ExpressionStarted() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "InnerFunctions.html", 8, 5);
	}

	public void testFindDuplicateInnerFunctions_CamelCase() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "InnerFunctions.html", 10, 2);
	}
}