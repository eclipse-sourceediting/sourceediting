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
package org.eclipse.wst.sse.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


public class SSEModelTestSuite extends TestSuite {

	/**
	 * to get picked up by BVT
	 * @return
	 */
	public static Test suite() {
		return new SSEModelTestSuite();
	}

	// not ready for threaded tests, yet.
//	private static Class[] classes = new Class[]{TestOfThreadLocalImprovement.class, TestOfThreadLocalWithChanges.class, TestCreation.class};

//	private static Class[] classes = new Class[]{TestCreation.class, TestDocumentReader.class};
//	private static Class[] classes = new Class[]{TestCreation.class, TestDocumentReader.class, TestCharSequenceReader.class, TestRegionMatches.class};
	
	private static Class[] classes = new Class[]{ExistenceTest.class};
	
	public SSEModelTestSuite() {
		super("SSE Model Basic Test Suite");
		for (int i = 0; i < classes.length; i++) {
			//addTest(new TestSuite(classes[i], classes[i].getName()));
			addTestSuite(ExistenceTest.class);
		}
	}

	public SSEModelTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	public SSEModelTestSuite(Class theClass) {
		super(theClass);
	}

	public SSEModelTestSuite(String name) {
		super(name);
	}
}