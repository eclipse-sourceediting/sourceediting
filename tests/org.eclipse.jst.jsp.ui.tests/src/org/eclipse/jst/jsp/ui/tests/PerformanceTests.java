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
package org.eclipse.jst.jsp.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.ui.tests.performance.TestHighlighterPerformance;
import org.eclipse.jst.jsp.ui.tests.performance.TestInsertPerformance;
import org.eclipse.jst.jsp.ui.tests.performance.TestModelPerformance;
import org.eclipse.jst.jsp.ui.tests.performance.TestParserPerformance;


public class PerformanceTests extends TestSuite {
	public static Test suite() {
		return new PerformanceTests();
	}

	public PerformanceTests() {
		super("PerformanceTests"); //$NON-NLS-1$

		
		addTest(new TestSuite(TestInsertPerformance.class, "TestInsertPerformance")); //$NON-NLS-1$
		addTest(new TestSuite(TestModelPerformance.class, "TestModelPerformance")); //$NON-NLS-1$
		addTest(new TestSuite(TestHighlighterPerformance.class, "TestHighlighterPerformance")); //$NON-NLS-1$
		addTest(new TestSuite(TestParserPerformance.class, "TestParserPerformance")); //$NON-NLS-1$
	}
}