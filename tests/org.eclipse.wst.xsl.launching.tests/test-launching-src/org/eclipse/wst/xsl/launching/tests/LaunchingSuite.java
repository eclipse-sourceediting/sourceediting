package org.eclipse.wst.xsl.launching.tests;

import junit.framework.TestSuite;

import org.eclipse.wst.xsl.launching.tests.testcase.XSLLaunchingTests;
import org.eclipse.wst.xsl.launching.ui.tests.TestInputFileBlock;

/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 * This class does specifies all the classes in this bundle
 * that provide tests. It is primarily for the convenience of 
 * the AllTestsSuite.
 *  
 */
public class LaunchingSuite extends TestSuite {

	private static Class[] classes = new Class[]{XSLLaunchingTests.class};

	public static TestSuite suite() {
		return new LaunchingSuite();
	}

	public LaunchingSuite() {
		super("XSL Launching Test Suite");
		for (int i = 0; i < classes.length; i++) {
			addTest(new TestSuite(classes[i], classes[i].getName()));
		}
		addTest(new TestSuite(TestInputFileBlock.class));
	}

	public LaunchingSuite(Class theClass, String name) {
		super(theClass, name);
	}

	public LaunchingSuite(Class theClass) {
		super(theClass);
	}

	public LaunchingSuite(String name) {
		super(name);
	}
}
