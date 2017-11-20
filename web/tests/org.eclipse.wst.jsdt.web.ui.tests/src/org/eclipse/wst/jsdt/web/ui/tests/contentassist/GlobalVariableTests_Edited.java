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

public class GlobalVariableTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Global Field Variables JavaScript Content Assist after Edit";

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
	public GlobalVariableTests_Edited() {
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
	public GlobalVariableTests_Edited(String name) {
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
		TestSuite ts = new TestSuite(GlobalVariableTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			public void additionalSetUp() throws Exception {
				/* file -> Global.js
				 * globalNum -> globalEditedNumber
				 * globalString -> globalEditedString */
				this.editFile("Global.js", 1, 4, 12, "globalEditedNumber");
				this.editFile("Global.js", 7, 0, 12, "globalEditedString");

				/* file -> Global.js
				 * globalV -> globalE */
				this.editFile("Global.js", 11, 6, 1, "E");
				this.editFile("Global.js", 15, 1, 1, "E");
				this.editFile("Global.js", 17, 1, 1, "E");

				/* file -> Global.html
				 * globalV -> globalE */
				this.editFile("Global.html", 10, 6, 1, "E");
				this.editFile("Global.html", 12, 1, 1, "E");
				this.editFile("Global.html", 14, 1, 1, "E");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testFindFieldSuggestions_AfterEdit_Expression_NotStarted() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "globalNum : Number - Global", "globalEditedString : String - Global",
						"globalEditedNumber : Number - Global", "globalVarObject : {} - Global",
						"globalVarString : String - Global", "globalVar - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 6, 0, expectedProposals);
	}

	public void testFindFieldSuggestions_AfterEdit_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "globalNum : Number - Global", "globalEditedString : String - Global",
						"globalVar - Global", "globalEditedNumber : Number - Global", "globalVarObject : {} - Global",
						"globalVarString : String - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 8, 1, expectedProposals);
	}

	public void testFindFieldSuggestions_AfterEdit_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "globalEditedNumber : Number - Global", "globalEditedString : String - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 10, 6, expectedProposals);
	}
}