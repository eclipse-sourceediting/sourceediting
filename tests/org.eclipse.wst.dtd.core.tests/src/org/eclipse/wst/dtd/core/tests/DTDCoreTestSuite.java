/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DTDCoreTestSuite extends TestSuite {
	public DTDCoreTestSuite() {
		super("DTD Core TestSuite");
		addTest(new TestSuite(VerifyPlugin.class));
		addTest(new TestSuite(DTDParserTest.class));
		addTestSuite(DTDValidationTest.class);
		addTestSuite(DTDFileTest.class);
		addTestSuite(DTDModelTests.class);
	}
	public static Test suite() {
		return new DTDCoreTestSuite();
	}
}
