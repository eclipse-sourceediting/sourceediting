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

public class ConstructorTests_Edited extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Constructor JavaScript Content Assist after Edit";

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
	public ConstructorTests_Edited() {
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
	public ConstructorTests_Edited(String name) {
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
				new TestSuite(ConstructorTests_Edited.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			public void additionalSetUp() throws Exception {
				/* file -> JSClasses.js
				 * Awesome -> Awesomeness
				 * Class1 -> ClassOne
				 * Class2 -> ClassTwo
				 * Class3 -> ClassThree */
				this.editFile("JSClasses.js", 0, 9, 7, "Awesomeness");
				this.editFile("JSClasses.js", 10, 4, 6, "ClassOne");
				this.editFile("JSClasses.js", 11, 4, 6, "ClassOne");
				this.editFile("JSClasses.js", 12, 4, 6, "ClassTwo");
				this.editFile("JSClasses.js", 13, 4, 6, "ClassTwo");
				this.editFile("JSClasses.js", 14, 8, 6, "ClassThree");
				this.editFile("JSClasses.js", 15, 8, 6, "ClassThree");

				/* file -> ClassTest2.js
				 * MyClass1 -> MyClassEdit1
				 * MyClass2 -> MyClassEdit2 */
				this.editFile("ClassTest1.js", 0, 9, 8, "MyClassEdit1");
				this.editFile("ClassTest1.js", 4, 4, 8, "MyClassEdit2");
				this.editFile("ClassTest1.js", 5, 0, 8, "MyClassEdit2");

				/* file -> Test.js
				 * test -> testEdit */
				this.editFile("Test.js", 0, 4, 4, "testEdit");
				this.editFile("Test.js", 1, 0, 4, "testEdit");
			}
		};
		
		return fTestProjectSetup;
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "Awesomeness(param1, param2) - Awesomeness" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 8, 6, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.ClassOne(a, b) - bar.ClassOne", "bar.ClassTwo(c, d, e) - bar.ClassTwo",
						"bar.foo.ClassThree(param1, param2, param3, param4) - bar.foo.ClassThree" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 10, 6, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.ClassOne(a, b) - bar.ClassOne", "bar.ClassTwo(c, d, e) - bar.ClassTwo" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 12, 9, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_3() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.foo.ClassThree(param1, param2, param3, param4) - bar.foo.ClassThree" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 14, 10, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.foo.ClassThree(param1, param2, param3, param4) - bar.foo.ClassThree" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 16, 13, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_5() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.ClassOne(a, b) - bar.ClassOne", "bar.ClassTwo(c, d, e) - bar.ClassTwo",
						"bar.foo.ClassThree(param1, param2, param3, param4) - bar.foo.ClassThree" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 18, 5, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ExpressionStarted_6() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.ClassOne(a, b) - bar.ClassOne", "bar.ClassTwo(c, d, e) - bar.ClassTwo",
						"bar.foo.ClassThree(param1, param2, param3, param4) - bar.foo.ClassThree" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 20, 9, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_VarDeclaration_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "MyClassEdit1(a) - MyClassEdit1", "MyClassEdit2() - MyClassEdit2" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ClassTest2.html", 7, 8, expectedProposals);
	}

	public void testFindConstructors_AfterEdit_ArrayReferenceDeclaration_ExpressionStarted_0()
			throws Exception {
		String[][] expectedProposals = new String[][] { { "testEdit.Foo(x, y, z) - testEdit.Foo" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Test.html", 8, 7, expectedProposals);
	}
}
