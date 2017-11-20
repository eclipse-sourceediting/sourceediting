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

public class CamelCasingTests_Edited extends TestCase {

	private static final String TEST_NAME = "Test Camel Casing JavaScript Content Assist";
	
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
	public CamelCasingTests_Edited() {
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
	public CamelCasingTests_Edited(String name) {
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
		TestSuite ts = new TestSuite(CamelCasingTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			/**
			 * @see org.eclipse.wst.jsdt.web.ui.tests.contentassist.ContentAssistTestUtilities.ContentAssistTestsSetup#additionalSetUp()
			 */
			public void additionalSetUp() throws Exception {
				/* file -> ConstructorCamelCase.js
				 * iGotMessage -> iSentMessage */
				this.editFile("ConstructorCamelCase.js", 0, 4, 11, "iGotSentMessage");
				this.editFile("ConstructorCamelCase.js", 0, 31, 11, "iGotSentMessage");

				this.editFile("Global.js", 1, 4, 12, "globalEditedNumber");
				this.editFile("Global.js", 7, 0, 12, "globalEditedString");
				this.editFile("Global.js", 11, 6, 1, "E");
				this.editFile("Global.js", 15, 1, 1, "E");
				this.editFile("Global.js", 17, 1, 1, "E");

				this.editFile("Global.html", 10, 6, 1, "E");
				this.editFile("Global.html", 12, 1, 1, "E");
				this.editFile("Global.html", 14, 1, 1, "E");

				this.editFile("InnerOuter.js", 7, 11, 6, "edited");
				this.editFile("InnerOuter.js", 0, 9, 9, "editedFunc");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testCamelCasing_AfterEdit_Expression1_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "iGotMessage(param1)[]" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 8, 6, expectedProposals, true,
				false);
	}

	public void testCamelCasing_AterEdit_Expression2_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "iGotMessage(param1)" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 10, 7, expectedProposals, true,
				false);
	}

	public void testCamelCasing_AfterEdit_Expression3_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "iGotMessage(param1)" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 12, 8, expectedProposals, true,
				false);
	}

	public void testCamelCasing_AfterEdit_Expression1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "mail.inbox.iGotStarredFun(param1) - mail.inbox.iGotStarredFun",
						"mail.iGotSpam(a, b) - mail.iGotSpam", "iGotSentMessage(param1) - iGotSentMessage" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ConstructorCamelCase.html", 8, 6, expectedProposals);
	}
	
	public void testCamelCasing_AfterEdit_Expresssion1() throws Exception {
		String[][] expectedProposals = new String[][] { { "globalEditedNumber : Number - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 12, 3, expectedProposals);
	}

	public void testCamelCasing_AfterEdit_Expresssion2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "globalEditedString : String - Global", "globalEditedNumber : Number - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Global.html", 14, 2, expectedProposals);
	}

	public void testCamelCasing_AfterEdit_Expression3() throws Exception {
		String[][] expectedProposals = new String[][] { { "editedFunc() - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerOuter.html", 12, 2, expectedProposals);
	}
}