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

package org.eclipse.wst.xml.core.tests.model;

import java.io.IOException;
import java.io.StringBufferInputStream;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.core.tests.document.UnzippedProjectTester;



public class TestModelsFromFiles extends UnzippedProjectTester {
	private static final String NONEXISTENT_FILENAME1 = "testfiles/xml/testnonexistent1.xml";
	private static final String NONEXISTENT_FILENAME2 = "testfiles/xml/testnonexistent2.xml";
	private static final String NONEXISTENT_FILENAME3 = "testfiles/xml/testnonexistent3.xml";
	private static final String EXISTENT_FILENAME1 = "testfiles/xml/testNormalCase.xml";
	private final static boolean DEBUG = false;

	public void testNonExistentFileWithBuffer() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreateBuffer(NONEXISTENT_FILENAME1, BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testNonExistentXML() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate(NONEXISTENT_FILENAME2, BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testNonExistentXMLIDs() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreateIDs(NONEXISTENT_FILENAME3, BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testSimpleCase() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate(EXISTENT_FILENAME1, BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	private void addContentsAndCreateFile(IFile file) throws CoreException {
		StringBufferInputStream stringStream = new StringBufferInputStream("testing testing testing");
		file.create(stringStream, false, null);
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
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		try {
			// count as success if gets to heer without exception
			assertTrue(true);
			String fileLocation = model.getBaseLocation();
			String id = model.getId();
			if (DEBUG) {
				System.out.println("fileLocation: " + fileLocation);
				System.out.println("id: " + id);
			}
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	private void doTestCreateBuffer(String filePath, Class class1, Class class2) throws CoreException {
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}

		ITextFileBuffer fileBufferBeforeExists = getTextBuffer(file);

		addContentsAndCreateFile(file);

		ITextFileBuffer fileBufferAfterExists = getTextBuffer(file);

		boolean buffersIdentical = fileBufferBeforeExists == fileBufferAfterExists;
		assertTrue(buffersIdentical);
		if (DEBUG) {
			System.out.println(fileBufferBeforeExists.getLocation());
			System.out.println(fileBufferAfterExists.getLocation());
		}
		


	}

	private void doTestCreateIDs(String filePath, Class class1, Class class2) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);

		try {
			assertNotNull(model);
			if (DEBUG) {
				System.out.println("ID: " + model.getId());
				System.out.println("BaseLocation" + model.getBaseLocation());
			}
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	private ITextFileBuffer getTextBuffer(IFile file) throws CoreException {
		ITextFileBufferManager fileBufferManager = FileBuffers.getTextFileBufferManager();
		IPath fileIPath = file.getFullPath();
		IPath normalizedPath = FileBuffers.normalizeLocation(fileIPath);
		fileBufferManager.connect(normalizedPath, null);
		ITextFileBuffer buffer = fileBufferManager.getTextFileBuffer(normalizedPath);
		return buffer;
	}

}
