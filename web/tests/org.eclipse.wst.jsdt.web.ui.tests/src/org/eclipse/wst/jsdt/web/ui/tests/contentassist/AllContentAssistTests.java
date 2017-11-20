/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.ui.tests.internal.TestProjectSetup;

/**
 * <p>
 * Test suite containing all JSDT web content assist tests.
 * </p>
 */
public class AllContentAssistTests extends TestSuite {
	private static final String TEST_NAME = "All JSDT Web Content Assist Tests";
	
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
	public AllContentAssistTests() {
		this(TEST_NAME);
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
	public AllContentAssistTests(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite all = new TestSuite(TEST_NAME);
		all.addTest(BrowserLibraryTests.suite());
		all.addTest(CamelCasingTests.suite());
		all.addTest(CamelCasingTests_Edited.suite());
		all.addTest(ClosureTests.suite());
		all.addTest(ConstructorTests.suite());
		all.addTest(ConstructorTests_Edited.suite());
		all.addTest(DoublyNestedFunctionTests.suite());
		all.addTest(DoublyNestedFunctionTests_Edited.suite());
		all.addTest(DuplicatesTests.suite());
		all.addTest(GlobalFunctionTests.suite());
		all.addTest(GlobalFunctionTests_Edited.suite());
		all.addTest(GlobalObjectLiteralTests.suite());
		all.addTest(GlobalVariableTests.suite());
		all.addTest(GlobalVariableTests_Edited.suite());
		all.addTest(InnerFunctionTests.suite());
		all.addTest(InnerFunctionTests_Edited.suite());
		all.addTest(LocalVarDefinedInFunctionInObjectLiteralTests.suite());
		all.addTest(OtherContentAssistTests.suite());
		all.addTest(ProposalInfoTest.suite());
		all.addTest(ProposalInfoTest_Edited.suite());
		all.addTest(StaticTests.suite());
		all.addTest(StaticTests_Edited.suite());
		all.addTest(TemplateTests.suite());
		all.addTest(SingleLineSriptTagTests.suite());
		all.addTest(TypeTests.suite());
		all.addTest(TypeTests_Edited.suite());
	
		//delete the project after running all JSDT content assist tests
		return new TestProjectSetup(all, "JSDTWebContentAssist", "WebContent", true);
	}
}