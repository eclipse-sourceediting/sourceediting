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
package org.eclipse.jst.jsp.core.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;

/**
 * This class tests basic creation of IModelManager
 * plugin and the ModelManger.
 * 
 * Appropriate for BVT.
 */
public class TestModelManager extends TestCase {

	/**
	 * Constructor for TestModelManager.
	 * @param name
	 */
	public TestModelManager(String name) {
		super(name);
	}

	public void testModelManager() throws IOException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		assertTrue("modelManager must not be null", modelManager != null);

		IStructuredModel model = modelManager.getModelForEdit("test.xml", null, null);
		assertTrue("basic XML empty model could not be created", model != null);

		Exception e = null;
		try {
			model = modelManager.getModelForEdit((String) null, null, null);
		}
		catch (IllegalArgumentException exception) {
			e = exception;
		}
		assertTrue("illegal argument failed to throw IllegalArgumentException", e instanceof IllegalArgumentException);
	}
}