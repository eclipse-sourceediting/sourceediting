/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.tests.encoding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.tests.encoding.jsp.JSPEncodingTests;
import org.eclipse.jst.jsp.tests.encoding.jsp.JSPHeadTokenizerTester;
import org.eclipse.jst.jsp.tests.encoding.jsp.TestContentTypeDetectionForJSP;

public class JSPEncodingTestSuite extends TestSuite {

	private static Class[] classes = new Class[]{JSPEncodingTests.class, JSPHeadTokenizerTester.class, TestContentTypeDetectionForJSP.class};

	public static Test suite() {
		return new JSPEncodingTestSuite();
	}

	public JSPEncodingTestSuite() {
		super("JSP Encoding Test Suite");
		for (int i = 0; i < classes.length; i++) {
			addTest(new TestSuite(classes[i], classes[i].getName()));
		}
	}

	/**
	 * @param theClass
	 */
	public JSPEncodingTestSuite(Class theClass) {
		super(theClass);
	}

	/**
	 * @param theClass
	 * @param name
	 */
	public JSPEncodingTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	/**
	 * @param name
	 */
	public JSPEncodingTestSuite(String name) {
		super(name);
	}
}
