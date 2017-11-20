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

public class ClosureTests extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Elements Defined in Closures JavaScript Content Assist";
	
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
	public ClosureTests() {
		super(TEST_NAME);
	}

	/**
	 * <p> 
	 * Constructor that takes a test name.
	 * </p>
	 * <p>
	 * Use {@link #suite()}
	 * </p>
	 * d
	 * @param name
	 *            The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public ClosureTests(String name) {
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
		TestSuite ts = new TestSuite(ClosureTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}

	public void testClosures_EmptyLine() throws Exception {
		String[][] expectedProposals = new String[][] { { "closure : {} - Global", "closure2 : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalVarsDefinedInClosure.html", 7, 0, expectedProposals);
	}

	public void testClosures_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "closure : {} - Global", "closure2 : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalVarsDefinedInClosure.html", 9, 3, expectedProposals);
	}

	public void _testClosures_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals = new String[][] { { "nifty : Number - {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalVarsDefinedInClosure.html", 11, 9, expectedProposals);
	}

	public void _testClosures_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals = new String[][] { { "burg : String - {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "GlobalVarsDefinedInClosure.html", 13, 10, expectedProposals);
	}
}