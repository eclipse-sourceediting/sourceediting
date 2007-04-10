/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.provisional.NameValidator;

public class NameValidatorTests extends TestCase {

	private int nTrials = 100;

	public NameValidatorTests(String name) {
		super(name);
	}

	private void doTest(int testNumber, String testString, Boolean expectedValidity) {
		boolean isValid = NameValidator.isValid(testString);
		assertEquals("testNumber: " + testNumber, expectedValidity.booleanValue(), isValid);
	}
	
	public void testIsValid() {
		Object[][] testees = new Object[][]{
					{"initial",Boolean.TRUE},
					{"foo",Boolean.TRUE},
					{"4",Boolean.FALSE},
					{"9999", Boolean.FALSE},
					{"f9999", Boolean.TRUE},
					{"", Boolean.FALSE},
					{"got space", Boolean.FALSE},
					{" spacebefore", Boolean.FALSE},
					{"spaceafter ", Boolean.FALSE},
					{"ns:namespace", Boolean.TRUE},
					{":funnyns", Boolean.TRUE},
					/* ISSUE: is "endns:" really valid name */
					{"endns:", Boolean.TRUE},
					{"us_underscore", Boolean.TRUE},
					{"_underscore", Boolean.TRUE},
					{"underscore_", Boolean.TRUE},
					{"averylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersize", Boolean.TRUE},
					};
		for (int i = 0; i < testees.length; i++) {
			for (int j = 0; j < nTrials; j++) {
				doTest(i, (String)testees[i][0], (Boolean) testees[i][1]);
			}
		}
	}

}
