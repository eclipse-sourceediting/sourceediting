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

package org.eclipse.wst.xml.core.tests.model;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.tests.document.UnzippedProjectTester;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;



public class TestModelsFromFiles extends UnzippedProjectTester {
	public void testNonExistentXML() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xml/testnonexistent.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}
	public void testSimpleCase() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xml/testNormalCase.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}
	/**
	 * @param string
	 * @param class1
	 * @param class2
	 * @throws CoreException
	 * @throws IOException
	 * @throws ResourceInUse
	 * @throws ResourceAlreadyExists
	 */
	private void doTestCreate(String filePath, Class class1, Class class2) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		modelManager.getNewModelForEdit(file, true);
		// count as success if gets to heer without exception
		assertTrue(true);
	}

}
