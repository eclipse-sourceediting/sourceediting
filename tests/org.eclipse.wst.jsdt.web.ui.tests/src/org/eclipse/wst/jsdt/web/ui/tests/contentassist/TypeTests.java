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

public class TypeTests extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test JavaScript Type Content Assist";
	
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
	public TypeTests() {
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
	public TypeTests(String name) {
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
		TestSuite ts = new TestSuite(TypeTests.class, TEST_NAME);

		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false);
		
		return fTestProjectSetup;
	}

	public void testCamelCase_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.IntelDualCore1 - Computer.proc",
						"Computer.proc.IntelDualCore2 - Computer.proc" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 8, 2, expectedProposals);
	}

	public void testCamelCase_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.CeleronXSeries - Computer.proc",
						"Installed.CorelXSoftware - Installed" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 10, 2, expectedProposals);
	}

	public void testCamelCase_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.IntelDualCore1 - Computer.proc",
						"Computer.proc.IntelDualCore2 - Computer.proc" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 12, 3, expectedProposals);
	}

	public void testClassProperties_ExpressionStarted_3() throws Exception {
		String[][] expectedProposals = new String[][] { { "Computer : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 14, 4, expectedProposals);
	}

	public void testClassProperties_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals = new String[][] { { "Installed : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 16, 3, expectedProposals);
	}
}