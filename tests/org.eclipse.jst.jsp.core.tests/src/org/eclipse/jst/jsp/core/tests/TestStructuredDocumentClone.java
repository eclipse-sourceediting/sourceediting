/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * @author davidw
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestStructuredDocumentClone extends TestCase {

	/**
	 * Constructor for TestModelClone.
	 */
	public TestStructuredDocumentClone(String name) {
		super(name);
	}

	public static Test getTest() {
		return new TestStructuredDocumentClone("testCloneStructuredModelJSPXML");
	}

	public void testCloneStructuredDocumentXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredDocument structuredDocument = modelManager.createStructuredDocumentFor("dummy.xml", (InputStream) null, null);
		assertTrue("structuredDocument could not be created!", structuredDocument != null);

		IStructuredDocument clonedStructuredDocument = structuredDocument.newInstance();
		// make sure the critical variables are NOT identical, but that new instances 
		// have been made
		boolean passed = true;
		if (clonedStructuredDocument.getEncodingMemento() == structuredDocument.getEncodingMemento())
			passed = false;
		if (clonedStructuredDocument.getParser() == structuredDocument.getParser())
			passed = false;
		if (clonedStructuredDocument.getReParser() == structuredDocument.getReParser())
			passed = false;
		assertTrue("newInstance of XML structuredDocument is not correct", passed);

	}

	/**
	 * This test is most useful to check breakpoints and dig deep in object 
	 * to check clones values
	 */
	public void testCloneStructuredDocumentJSP() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredDocument structuredDocument = modelManager.createStructuredDocumentFor("dummy.jsp", (InputStream) null, null);
		assertTrue("structuredDocument could not be created!", structuredDocument != null);

		IStructuredDocument clonedStructuredDocument = structuredDocument.newInstance();
		// make sure the critical variables are NOT identical, but that new instances 
		// have been made
		boolean passed = true;
		if (clonedStructuredDocument.getEncodingMemento() == structuredDocument.getEncodingMemento())
			passed = false;
		if (clonedStructuredDocument.getParser() == structuredDocument.getParser())
			passed = false;
		if (clonedStructuredDocument.getReParser() == structuredDocument.getReParser())
			passed = false;
		assertTrue("newInstance of JSP structuredDocument is not correct", passed);

	}

	/**
	 * This test is most useful to check breakpoints and dig deep in object 
	 * to check clones values
	 */
	public void testCloneStructuredDocumentJSPXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		String testContent = "<@! page contentType=\"text/xml\" @>";
		IStructuredDocument structuredDocument = modelManager.createStructuredDocumentFor("dummy.jsp", testContent, null);
		assertTrue("structuredDocument could not be created!", structuredDocument != null);

		IStructuredDocument clonedStructuredDocument = structuredDocument.newInstance();
		// make sure the critical variables are NOT identical, but that new instances 
		// have been made
		boolean passed = true;
		if (clonedStructuredDocument.getEncodingMemento() == structuredDocument.getEncodingMemento())
			passed = false;
		if (clonedStructuredDocument.getParser() == structuredDocument.getParser())
			passed = false;
		if (clonedStructuredDocument.getReParser() == structuredDocument.getReParser())
			passed = false;
		assertTrue("newInstance of JSPXML structuredDocument is not correct", passed);

	}

}
