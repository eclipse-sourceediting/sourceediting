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
package org.eclipse.wst.xml.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.core.tests.model.TestModelsFromFiles;



public class SSEModelXMLTestSuite extends TestSuite {
	public static Test suite() {
		return new SSEModelXMLTestSuite();
	}

	public SSEModelXMLTestSuite() {
		super("Test Suite for org.eclipse.wst.xml.core.tests");
		addTest(new TestSuite(TestModelsFromFiles.class));
		//addTest(new TestSuite(TestXMLDocumentLoader.class));
	}
}