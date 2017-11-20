/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
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

public class StaticTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Edited for Static vs Non Static JavaScript Content Assist";
	
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
	public StaticTests_Edited() {
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
	public StaticTests_Edited(String name) {
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
		TestSuite ts = new TestSuite(StaticTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			public void additionalSetUp() throws Exception {
				/* file -> StaticTests.js
				 * getServerIP -> getRouterIP
				 * serverIP -> routerIP
				 * gSIP -> gRIP
				 * client -> switch
				 * Client -> Switch
				 * cIP -> sIP */
				this.editFile("StaticTests.js", 7, 10, 6, "Router");
				this.editFile("StaticTests.js", 10, 7, 8, "routerIP");
				this.editFile("StaticTests.js", 8, 15, 8, "routerIP");
				this.editFile("StaticTests.js", 28, 8, 1, "R");
				this.editFile("StaticTests.js", 14, 20, 6, "Switch");
				this.editFile("StaticTests.js", 17, 17, 8, "switchIP");
				this.editFile("StaticTests.js", 18, 20, 8, "SwitchIP");
				this.editFile("StaticTests.js", 19, 13, 8, "switchIP");
				this.editFile("StaticTests.js", 32, 12, 1, "s");

				/* file -> StaticTests.html */
				this.editFile("StaticTests.html", 8, 8, 1, "R");
				this.editFile("StaticTests.html", 12, 12, 1, "s");
			}
		};
		
		return fTestProjectSetup;
	}

//	public void testStatic_CamelCase_AfterEdit_ExpressionStarted_1() throws Exception {
//		String[][] expectedProposals = new String[][] { { "getRouterIP() : String - Server" } };
//		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 8, 11, expectedProposals);
//	}

	public void testStatic_CamelCase_AfterEdit_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals = new String[][] { { "yahooDotCom : Server - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 10, 3, expectedProposals);
	}

	public void testStatic_CamelCase_AfterEdit_ExpressionStarted_3() throws Exception {
		String[][] expectedProposals = new String[][] { { "switchIP : String - Server" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 12, 15, expectedProposals);
	}

	public void testStatic_OtherFile_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "switchIP : String - Server", "port - Server", "prototype - Server",
						"getSwitchIP() : String - Server", "getSwitchPort() - Server" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 14, 12, expectedProposals);
	}

//	public void testStatic_AfterEdit_ExpressionStarted_5() throws Exception {
//		String[][] expectedProposals = new String[][] { { "routerIP : String - Server", "getRouterIP() : String - Server" } };
//		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 16, 7, expectedProposals);
//	}

	public void testStatic_NegativeTest_AfterEdit_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals = new String[][] { { "getRouterIP() - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 14, 12, expectedProposals, true, false);
	}

	public void testStatic_NegativeTest_AfterEdit_ExpressionStarted_5() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "switchIP : String - Server", "getSwitchIP() - Global", "getSwitchPort() - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "StaticTests.html", 16, 7, expectedProposals, true, false);
	}
}