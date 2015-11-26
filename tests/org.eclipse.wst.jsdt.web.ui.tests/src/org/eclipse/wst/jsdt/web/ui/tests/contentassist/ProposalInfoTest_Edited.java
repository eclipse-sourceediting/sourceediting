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

public class ProposalInfoTest_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Edited JavaScript Content Assist Proposal Info";
	
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
	public ProposalInfoTest_Edited() {
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
	public ProposalInfoTest_Edited(String name) {
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
		TestSuite ts = new TestSuite(ProposalInfoTest_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {

			public void additionalSetUp() throws Exception {
				/* File Edited : ProposalInfo.js
				 * World -> Earth
				 * State -> NC
				 * City -> Wake
				 * nodeOne = test -> nodeOne = Edit */
				this.editFile("ProposalInfo.js", 1, 9, 5, "Earth");
				this.editFile("ProposalInfo.js", 8, 10, 5, "NC");
				this.editFile("ProposalInfo.js", 18, 13, 4, "Edit");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testProposalInfo_AfterEdit_Expression_NotStarted() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "HelloWorld() - Global", "HelloAmerica(State, City) - Global" } };
		String[][] expectedInfo = new String[][] { { "Hello Earth", "NC" } };
		ContentAssistTestUtilities.runProposalInfoTest(fTestProjectSetup, "ProposalInfo.html", 7, 0, expectedProposals, expectedInfo);
	}

	public void testProposalInfo_AfterEdit_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "HelloWorld() - Global", "HelloAmerica(State, City)  - Global" } };
		String[][] expectedInfo = new String[][] { { "Hello Earth", "Wake" } };
		ContentAssistTestUtilities.runProposalInfoTest(fTestProjectSetup, "ProposalInfo.html", 9, 3, expectedProposals, expectedInfo);
	}

	public void _testProposalInfo_AfterEdit_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals = new String[][] { { "nodeOne : String - Global" } };
		String[][] expectedInfo = new String[][] { { "nodeOne = Edit" } };
		ContentAssistTestUtilities.runProposalInfoTest(fTestProjectSetup, "ProposalInfo.html", 11, 3, expectedProposals, expectedInfo);
	}
}