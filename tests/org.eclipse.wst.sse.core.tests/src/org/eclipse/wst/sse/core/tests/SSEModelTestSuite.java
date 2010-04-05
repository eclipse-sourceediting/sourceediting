/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.wst.sse.core.tests.document.TestObjects;
import org.eclipse.wst.sse.core.tests.document.TestRegionList;
import org.eclipse.wst.sse.core.tests.events.TestAboutToBeChangedEvent;
import org.eclipse.wst.sse.core.tests.events.TestNewDocumentContentEvent;
import org.eclipse.wst.sse.core.tests.events.TestNewDocumentEvent;
import org.eclipse.wst.sse.core.tests.events.TestNoChangeEvent;
import org.eclipse.wst.sse.core.tests.events.TestRegionChangedEvent;
import org.eclipse.wst.sse.core.tests.events.TestRegionsReplacedEvent;
import org.eclipse.wst.sse.core.tests.events.TestStructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.tests.modelhandler.ModelHandlerRegistryTest;
import org.eclipse.wst.sse.core.tests.util.TestJarUtilities;


public class SSEModelTestSuite extends TestSuite {

	/**
	 * to get picked up by BVT
	 * 
	 * @return
	 */
	public static Test suite() {
		return new SSEModelTestSuite();
	}

	// not ready for threaded tests, yet.
	// private static Class[] classes = new
	// Class[]{TestOfThreadLocalImprovement.class,
	// TestOfThreadLocalWithChanges.class, TestCreation.class};

	// private static Class[] classes = new Class[]{TestCreation.class,
	// TestDocumentReader.class};
	// private static Class[] classes = new Class[]{TestCreation.class,
	// TestDocumentReader.class, TestCharSequenceReader.class,
	// TestRegionMatches.class};

	private static Class[] classes = new Class[]{ExistenceTest.class, TestObjects.class, TestNewDocumentContentEvent.class, TestAboutToBeChangedEvent.class, TestNewDocumentEvent.class, TestNoChangeEvent.class, TestRegionChangedEvent.class,TestRegionsReplacedEvent.class,TestStructuredDocumentRegionsReplacedEvent.class, TestJarUtilities.class, TestUnsupportedContentType.class, ModelHandlerRegistryTest.class, TestRegionList.class};

	public SSEModelTestSuite() {
		super("SSE Model Basic Test Suite");
		for (int i = 0; i < classes.length; i++) {
			addTest(new TestSuite(classes[i], classes[i].getName()));
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
