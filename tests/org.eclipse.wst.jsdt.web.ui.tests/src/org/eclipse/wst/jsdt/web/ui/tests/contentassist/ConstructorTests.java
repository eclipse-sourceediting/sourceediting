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

public class ConstructorTests extends TestCase {
	/**
	 * <p>
	 * This tests name
	 * </p>
	 */
	private static final String TEST_NAME = "Test Constructor JavaScript Content Assist";

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
	public ConstructorTests() {
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
	public ConstructorTests(String name) {
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
		TestSuite ts = new TestSuite(ConstructorTests.class, TEST_NAME);
		
		fTestProjectSetup = new TestProjectSetup(ts, "JSDTWebContentAssist", "WebContent", false) {
			/**
			 * @see org.eclipse.wst.jsdt.ui.tests.contentassist.ContentAssistTestUtilities.ContentAssistTestsSetup#additionalSetUp()
			 */
			public void additionalSetUp() throws Exception {
				// for some reason this test suite wants an extra second before running otherwise
				// the first test fails...
				Thread.sleep(1000);
			}
		};
		
		return fTestProjectSetup;
	}

	public void testFindConstructors_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "Awesome(param1, param2) - Awesome" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 8, 5, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_1() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.Class1(a, b) - bar.Class1", "bar.Class2(c, d, e) - bar.Class2",
						"bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 10, 6, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_2() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.Class1(a, b) - bar.Class1", "bar.Class2(c, d, e) - bar.Class2" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 12, 9, expectedProposals);
	}

	public void testFindConstructors_Expression_2_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "bar : {}" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 12, 9, expectedProposals, true, false);
	}

	public void testFindConstructors_ExpressionStarted_3() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 14, 10, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_4() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 16, 13, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_5() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.Class1(a, b) - bar.Class1", "bar.Class2(c, d, e) - bar.Class2",
						"bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 18, 5, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_6() throws Exception {
		String[][] expectedProposals =
				new String[][] { { "bar.Class1(a, b) - bar.Class1", "bar.Class2(c, d, e) - bar.Class2",
						"bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 20, 8, expectedProposals);
	}

	public void testFindConstructors_ExpressionStarted_7_NegativeTest() throws Exception {
		String[][] proposals = new String[][] { { "bar.foo.Class3(param1, param2, param3, param4) - bar.foo.Class3" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "JSClasses.html", 12, 9, proposals, true, false);
	}

	public void testDuplicateFindConstructors_ExpressionStarted_6() throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "JSClasses.html", 20, 8);
	}

	public void testFindConstructors_VarDeclaration_ExpressionStarted_0() throws Exception {
		String[][] expectedProposals = new String[][] { { "MyClass1(a) - MyClass1", "MyClass2() - MyClass2" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ClassTest2.html", 7, 8, expectedProposals);
	}

	public void testFindConstructors_ArrayReferenceDeclaration_ExpressionStarted_0()
			throws Exception {
		String[][] expectedProposals = new String[][] { { "test.Foo(x, y, z) - test.Foo" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "Test.html", 8, 7, expectedProposals);
	}

	public void testFindConstructors_ThisReferenceInStaticFunction() throws Exception {
		String[][] expectedProposals = new String[][] { { "ParentType0.func2(b) - ParentType0.func2" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ReferenceInMemberAndStaticFunctions.html", 7, 6,
				expectedProposals);
	}

	public void testFindConstructors_ThisReferenceInMemberFunction_NegativeTest() throws Exception {
		String[][] expectedProposals = new String[][] { { "ParentType0.func1(a)" } };
		ContentAssistTestUtilities.runProposalTest(fTestProjectSetup, "ReferenceInMemberAndStaticFunctions.html", 7, 6,
				expectedProposals, true, false);
	}

	public void testFindDuplicateConstructors_AnonymousConstructorFunctionAssignedToSingleNameReference()
			throws Exception {
		ContentAssistTestUtilities.verifyNoDuplicates(fTestProjectSetup, "ReferenceInMemberAndStaticFunctions.html", 9, 7);
	}
}