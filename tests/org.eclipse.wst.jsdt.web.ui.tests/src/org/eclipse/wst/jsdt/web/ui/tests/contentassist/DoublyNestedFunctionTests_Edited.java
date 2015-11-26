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

public class DoublyNestedFunctionTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Doubly Nested Functions JavaScript Content Assist after Edit";

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
	public DoublyNestedFunctionTests_Edited() {
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
	public DoublyNestedFunctionTests_Edited(String name) {
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
		TestSuite ts =
				new TestSuite(DoublyNestedFunctionTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			public void additionalSetUp() throws Exception {
				this.editFile("InnerOuter.js", 7, 11, 6, "edited");
				this.editFile("InnerOuter.js", 0, 9, 9, "editedFunc");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testFindInnerFunctions2_AfterEdit_ExpressionNotStarted() throws Exception {
		String[][] expectedProposals = new String[][] { { "editedFunc() - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerOuter.html", 6, 0, expectedProposals);
	}

	public void testMustFail_FindInnerFunctions2_AfterEdit_ExpressionStarted_1() throws Exception {
		String[][] unexpectedProposals =
				new String[][] { { "innerFunc()", "editedInnerFunc()", "localInnerFunc : Function",
						"localInnerFunc(param1)" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "InnerOuter.html", 10, 1, unexpectedProposals, true, false);
	}
}