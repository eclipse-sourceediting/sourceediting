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
package org.eclipse.jst.jsp.ui.tests.performance;

import java.io.IOException;

public class TestModelPerformance extends AbstractTestPerformance {

	public TestModelPerformance() {
		super();
		try {
			readPreviousResults(); // populates previous test data vector (for comparison later)
		}
		catch (IOException ex) {
		}
	}

	public void testStructuredModel100JSP() throws IOException {
		doStructuredModelTest("Test100K.jsp"); //$NON-NLS-1$
	}

	public void testStructuredModel100HTML() throws IOException {
		doStructuredModelTest("Test100K.html"); //$NON-NLS-1$
	}

	public void testStructuredModel100XML() throws IOException {
		doStructuredModelTest("Test100K.xml"); //$NON-NLS-1$
	}

	public void testStructuredModel300JSP() throws IOException {
		doStructuredModelTest("Test300K.jsp"); //$NON-NLS-1$
	}

	public void testStructuredModel300HTML() throws IOException {
		doStructuredModelTest("Test300K.html"); //$NON-NLS-1$
	}

	public void testStructuredModel300XML() throws IOException {
		doStructuredModelTest("Test300K.xml"); //$NON-NLS-1$
	}


	//    public void testManyNodes1M() throws IOException {
	//    	doStructuredModelTest("vocabulary1M.xml");
	//    }
	public void testManyNodes500K() throws IOException {
		doStructuredModelTest("vocabulary500K.xml"); //$NON-NLS-1$
	}

	public void testManyNodes44K() throws IOException {
		doStructuredModelTest("vocabulary44K.xml"); //$NON-NLS-1$
	}

	// this file is deep, but not that large
	public void testDeepNested170() throws IOException {
		doStructuredModelTest("nested170.xml"); //$NON-NLS-1$
	}

	public void testDeepNested85() throws IOException {
		doStructuredModelTest("nested85.xml"); //$NON-NLS-1$
	}

	public void testDeepNested13() throws IOException {
		doStructuredModelTest("nested13.xml"); //$NON-NLS-1$
	}

	// this file has many attributes on one node
	public void testManyAttributes600() throws IOException {
		doStructuredModelTest("attributes600.xml"); //$NON-NLS-1$
	}



}