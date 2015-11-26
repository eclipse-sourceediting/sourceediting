/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
//		System.out.println();
		boolean isValid = NameValidator.isValid(testString);
		assertEquals("testNumber: " + testNumber, expectedValidity.booleanValue(), isValid);
//		System.out.println();
	}
	
	public void testIsValid() {
		Object[][] testees = new Object[][]{
					{"initial",Boolean.TRUE}, //0
					{"foo",Boolean.TRUE},	  //1
					{"4",Boolean.FALSE},	  //2
					{"9999", Boolean.FALSE},  //3
					{"f9999", Boolean.TRUE},  //4
					{"", Boolean.FALSE},      //5
					{"got space", Boolean.FALSE}, //6
					{" spacebefore", Boolean.FALSE}, //7
					{"spaceafter ", Boolean.FALSE},  //8
					{"ns:namespace", Boolean.TRUE},  //9
					{":funnyns", Boolean.TRUE},     //10
					/* ISSUE: is "endns:" really valid name */
					{"endns:", Boolean.TRUE},   //11
					{"us_underscore", Boolean.TRUE},  //12
					{"_underscore", Boolean.TRUE},   //13
					{"underscore_", Boolean.TRUE},    //14
					{"averylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersize", Boolean.TRUE}, //15
					{"<bracket", Boolean.FALSE}, //16
					{"bracket<", Boolean.FALSE}, //17
					{"bracket", Boolean.TRUE}, //18
					{"per.iod", Boolean.TRUE}, //19
					};
		for (int i = 0; i < testees.length; i++) {
			for (int j = 0; j < nTrials; j++) {
				doTest(i, (String)testees[i][0], (Boolean) testees[i][1]);
			}
		}
	}

}
