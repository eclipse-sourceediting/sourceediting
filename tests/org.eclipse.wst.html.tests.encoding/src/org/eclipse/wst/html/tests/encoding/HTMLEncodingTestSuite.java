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
package org.eclipse.wst.html.tests.encoding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.html.tests.encoding.html.HTMLEncodingTests;
import org.eclipse.wst.html.tests.encoding.html.HTMLHeadTokenizerTester;
import org.eclipse.wst.html.tests.encoding.html.TestContentTypeDetectionForHTML;

public class HTMLEncodingTestSuite extends TestSuite {

	private static Class[] classes = new Class[]{HTMLEncodingTests.class, HTMLHeadTokenizerTester.class, TestContentTypeDetectionForHTML.class};

	public static Test suite() {
		return new HTMLEncodingTestSuite();
	}

	public HTMLEncodingTestSuite() {
		super("HTML Encoding Test Suite");
		for (int i = 0; i < classes.length; i++) {
			addTest(new TestSuite(classes[i], classes[i].getName()));
		}
	}

	/**
	 * @param theClass
	 */
	public HTMLEncodingTestSuite(Class theClass) {
		super(theClass);
	}

	/**
	 * @param theClass
	 * @param name
	 */
	public HTMLEncodingTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	/**
	 * @param name
	 */
	public HTMLEncodingTestSuite(String name) {
		super(name);
	}
}
