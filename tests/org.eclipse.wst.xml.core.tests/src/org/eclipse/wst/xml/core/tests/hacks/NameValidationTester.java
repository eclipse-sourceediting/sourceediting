/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.hacks;

import org.eclipse.wst.xml.core.NameValidator;

public final class NameValidationTester {

	private static int nTrials = 100000;

	public NameValidationTester() {

		super();
	}

	public static void main(String[] args) {
		boolean isValid = false;
		long start = 0;
		long stop = 0;
		String[] testees = new String[]{"initial", "foo", "4", "9999", "f9999", "", "got space", " spacebefore", "spaceafter ", "ns:namespace", ":funnyns", "endns:", "us_underscore" , "_underscore", "underscore_", 
					"averylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersizeaverylongnamethatshouldgooverbuffersize"};
		for (int i = 0; i < testees.length; i++) {
			System.out.println();
			System.out.println();
			System.out.println("input: [" + testees[i] + "]");
			start = System.currentTimeMillis();
			for (int j = 0; j < nTrials; j++) {
				isValid = NameValidator.isValid(testees[i]);
			}
			stop = System.currentTimeMillis();
			System.out.println("\tNameValidator: " + isValid);
			System.out.println("\ttime: " + (stop - start));
		}
	}
}