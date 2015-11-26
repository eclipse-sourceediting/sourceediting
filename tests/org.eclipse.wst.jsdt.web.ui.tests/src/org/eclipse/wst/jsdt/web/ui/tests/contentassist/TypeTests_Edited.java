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

public class TypeTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Edited JavaScript Type Content Asist Edited";
	
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
	public TypeTests_Edited() {
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
	public TypeTests_Edited(String name) {
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
		TestSuite ts = new TestSuite(TypeTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			/**
			 * @see org.eclipse.wst.jsdt.ui.tests.contentassist.ContentAssistTestUtilities.ContentAssistTestsSetup#additionalSetUp()
			 */
			public void additionalSetUp() throws Exception {
				/* file -> JSClassesII.js
				 * IntelQuadCore -> IntelCentrino
				 * IntelDualCore -> IntelXeonProc
				 * Installed -> Developed */
				this.editFile("JSClassesII.js", 15, 19, 8, "Centrino");
				this.editFile("JSClassesII.js", 9, 19, 8, "XeonProc");
				this.editFile("JSClassesII.js", 12, 19, 8, "XeonProc");
				this.editFile("JSClassesII.js", 7, 4, 9, "Developed");
				this.editFile("JSClassesII.js", 22, 0, 9, "Developed");
				this.editFile("JSClassesII.js", 27, 1, 1, "X");
				this.editFile("JSClassesII.js", 31, 1, 2, "XP");
				this.editFile("JSClassesII.html", 8, 1, 1, "X");
				this.editFile("JSClassesII.js", 35, 0, 3, "Dev");
				this.editFile("JSClassesII.html", 12, 1, 2, "XP");
				this.editFile("JSClassesII.html", 16, 0, 3, "Dev");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testCameCasing_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.IntelXeonProc1 - Computer.proc",
						"Computer.proc.IntelXeonProc2 - Computer.proc" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 8, 2, expectedProposals);
	}

	public void testCamelCasing_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.CeleronXSeries - Computer.proc",
						"Developed.CorelXSoftware - Developed" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 10, 2, expectedProposals);
	}

	public void testCamelCasing_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "Computer.proc.IntelXeonProc1 - Computer.proc",
						"Computer.proc.IntelXeonProc2 - Computer.proc" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 12, 3, expectedProposals);
	}

	public void testClassProperties_AfterEdit_ExpressionStarted_3() throws Exception {
		String[][] expectedProposals = new String[][] { { "Computer : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 14, 4, expectedProposals);
	}

	public void testClassProperties_AfterEdit_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals = new String[][] { { "Developed : {} - Global" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClassesII.html", 16, 3, expectedProposals);
	}
}