/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.core.tests.taglibindex.TestIndex;

public class ManyTestIndex extends TestSuite {
	public static Test suite() {
		return new ManyTestIndex();
	}

	public ManyTestIndex() {
		super("SSE JSP Core Test Suite");

		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		String wtp_autotest_noninteractive = null;
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		for (int i = 0; i < 25; i++) {
			addTest(new TestSuite(TestIndex.class, "TaglibIndex Tests " + (i + 1)));
		}

		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}
}
