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
package org.eclipse.jst.jsp.core.tests.application;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.core.tests.CleanupFormatTests;
import org.eclipse.jst.jsp.core.tests.ModelCloneTests;
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestContentTypeHandlers;
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestModelHandlers;
import org.eclipse.jst.jsp.core.tests.model.TestModelsFromFiles;



public class SSEModelJSPTestSuite extends TestSuite {
	public static Test suite() {
		return new SSEModelJSPTestSuite();
	}

	public SSEModelJSPTestSuite() {
		super("SSEModelTestSuiteForJSP");

		addTest(CleanupFormatTests.suite());
		addTest(ModelCloneTests.suite());
		addTest(new TestSuite(TestModelHandlers.class, "TestModelHandlers"));
		addTest(new TestSuite(TestContentTypeHandlers.class, "TestContentTypeHandlers"));
		addTest(new TestSuite(TestModelsFromFiles.class, "TestModelsFromFiles"));
	}
}