/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.tests.rules;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @since 3.0
 */
public class RulesTestSuite {

	public static Test suite() {
		TestSuite suite= new TestSuite(
				"Test Suite org.eclipse.jface.text.tests.rules");
		// $JUnit-BEGIN$
		suite.addTestSuite(FastPartitionerTest.class);
		suite.addTestSuite(DefaultPartitionerTest.class);
		suite.addTestSuite(DefaultPartitionerZeroLengthTest.class);
		suite.addTestSuite(WordRuleTest.class);
		suite.addTestSuite(ScannerColumnTest.class);
		// $JUnit-END$
		return suite;
	}
}
