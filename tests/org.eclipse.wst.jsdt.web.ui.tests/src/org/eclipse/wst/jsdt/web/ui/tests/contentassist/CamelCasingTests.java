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

public class CamelCasingTests extends TestCase {

	private static final String TEST_NAME = "Test Camel Casing JavaScript Content Assist.";
	
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
	public CamelCasingTests() {
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
	public CamelCasingTests(String name) {
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
		TestSuite ts = new TestSuite(CamelCasingTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}
	
	
	public void testCamelCasing_Expression1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "mail.inbox.iGotStarredFun(param1) - mail.inbox.iGotStarredFun",
						"mail.iGotSpam(a, b) - mail.iGotSpam", "iGotMessage(param1) - iGotMessage" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 8, 6, expectedProposals);
	}
	
	public void testCamelCasing_Expression2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "mail.inbox.iGotStarredFun(param1) - mail.inbox.iGotStarredFun",
						"mail.iGotSpam(a, b) - mail.iGotSpam" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 10, 7, expectedProposals);
	}
	
	public void testCamelCasing_Expression2_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "iGotMessage(param1)" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 2, 7, expectedProposals, true,
				false);
	}

	public void testCamelCasing_Expression3() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "mail.inbox.iGotStarredFun(param1) - mail.inbox.iGotStarredFun" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 12, 8, expectedProposals);
	}

	public void testGlobalVar_Expresssion1() throws Exception {
		String[][] expectedProposals = new String[][] { { "globalVarNum : Number - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 12, 3, expectedProposals);
	}

	public void testGlobalVar_Expresssion2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "globalVarNum : Number - Global", "globalVar - Global",
						"globalVarObject : {} - Global", "globalVarString : String - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 14, 2, expectedProposals);
	}
	
	public void testDoublyNestedFunc_CamelCasing_Expression2() throws Exception {
		String[][] expectedProposals = new String[][] { { "outerFunc() - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerOuter.html", 8, 2, expectedProposals);
	}
}