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
package org.eclipse.jst.jsp.ui.tests.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.ui.tests.document.UnzippedProjectTester;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Utilities;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;


public class TestModelsFromFiles extends UnzippedProjectTester {
	public void testNonExistentXML() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xml/testnonexistent.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testNonExistentXMLDocument() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNonExistentDocument("testfiles/xml/testnonexistent.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testNonExistentXMLModel() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreateModelWithNonExistIFile("testfiles/xml/testnonexistent.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testNonExistentJSP() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/jsp/testnonexistent.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testNonExistentJSPDocument() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNonExistentDocument("testfiles/jsp/testnonexistent.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testNonExistentJSPModel() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreateModelWithNonExistIFile("testfiles/jsp/testnonexistent.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testSimpleCase() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNotEmpty("testfiles/xml/testNormalCase.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testSimpleDocumentCaseXML() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNotEmptyDocument("testfiles/xml/testNormalCase.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testSimpleDocumentCaseJSP() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNotEmptyDocument("testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testComplexCase() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestNotEmpty("testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testReload() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestReload("testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testJSPWithXMLOutput() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestReload("testfiles/jsp/jsp-xml-output.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testXHTMLTransitional1() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xhtml/testx.html", BasicStructuredDocument.class, StructuredTextPartitionerForHTML.class);
	}

	public void testXHTMLTransitional2() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xhtml/testx.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testXHTMLTransitional3() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/xhtml/testx.xhtml", BasicStructuredDocument.class, StructuredTextPartitionerForHTML.class);
	}

	public void testHTMLTransitional1() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/html/testh.html", BasicStructuredDocument.class, StructuredTextPartitionerForHTML.class);
	}

	public void testHTMLTransitional2() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		doTestCreate("testfiles/html/testh.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	/*
	 * TODO: these test files are missing, for some reason when running on
	 * build machine ... need to re-construct public void testNearEmptyJSP()
	 * throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
	 * doTestNotEmpty("testfiles/jsp/nearEmpty.jsp",
	 * BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class); }
	 * 
	 * public void testNearEmptyXML() throws ResourceAlreadyExists,
	 * ResourceInUse, IOException, CoreException {
	 * doTestNotEmpty("testfiles/xml/nearEmpty.xml",
	 * BasicStructuredDocument.class, StructuredTextPartitionerForXML.class); }
	 * 
	 * public void testNearEmptyHTML() throws ResourceAlreadyExists,
	 * ResourceInUse, IOException, CoreException {
	 * doTestNotEmpty("testfiles/html/nearEmpty.html",
	 * BasicStructuredDocument.class, StructuredTextPartitionerForHTML.class); }
	 */
	private void doTestNotEmpty(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		String contents = doTestCreate(filePath, expectedDocumentClass, expectedPartioner);
		assertNotNull("contents were null", contents);
		assertTrue("contents were empty", contents.length() > 0);

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
	private void doTestNotEmptyDocument(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		String contents = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredDocument document = modelManager.createStructuredDocumentFor(file);
		assertNotNull(document);
		assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
		IDocumentPartitioner setupPartitioner = null;
		if (Utilities.contains(expectedDocumentClass.getInterfaces(), IDocumentExtension3.class)) {
			setupPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		}
		else {
			setupPartitioner = document.getDocumentPartitioner();
		}
		assertTrue("wrong partitioner in document.", expectedPartioner.isInstance(setupPartitioner));
		contents = document.get();
		assertNotNull("contents were null", contents);
		assertTrue("contents were empty", contents.length() > 0);

	}

	private void doTestNonExistentDocument(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		String contents = null;
		boolean expectedExceptionCaught = false;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredDocument document = null;
		try {
			document = modelManager.createStructuredDocumentFor(file);
		}
		catch (FileNotFoundException e) {
			expectedExceptionCaught = true;
		}

		if (expectedExceptionCaught) {
			document = modelManager.createNewStructuredDocumentFor(file);
			assertNotNull(document);
			assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
			IDocumentPartitioner setupPartitioner = null;
			if (Utilities.contains(expectedDocumentClass.getInterfaces(), IDocumentExtension3.class)) {
				setupPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
			}
			else {
				setupPartitioner = document.getDocumentPartitioner();
			}
			assertTrue("wrong partitioner in document.", expectedPartioner.isInstance(setupPartitioner));
			contents = document.get();
			assertNotNull("contents were null", contents);
			assertTrue("contents were *not* empty as expected", contents.length() == 0);
		}
		else {
			assertTrue("FileNotFound exception was *not* thrown as expected", false);
		}

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
	private void doTestReload(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		try {
			assertNotNull(model);
			IStructuredDocument document = model.getStructuredDocument();
			assertNotNull(document);
			assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
			IDocumentPartitioner setupPartitioner = null;
			if (Utilities.contains(expectedDocumentClass.getInterfaces(), IDocumentExtension3.class)) {
				setupPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
			}
			else {
				setupPartitioner = document.getDocumentPartitioner();
			}
			assertTrue("wrong partitioner in document.", expectedPartioner.isInstance(setupPartitioner));

			InputStream inStream = null;
			try {
				inStream = file.getContents();
				model.reload(inStream);
				assertNotNull(model);
				IStructuredDocument documentReloaded = model.getStructuredDocument();
				// note: NOT same instance of document
				// FIXME: this test has to be changed back, once the reload
				// API is
				// fixed to work with different document.
				// assertFalse(document == documentReloaded);
				assertTrue(document == documentReloaded);
			}
			finally {
				if (inStream != null) {
					inStream.close();
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
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
	private String doTestCreate(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		String contents = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		// file will be null if the resource does not exist
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		try {
			assertNotNull(model);
			IStructuredDocument document = model.getStructuredDocument();
			assertNotNull(document);
			contents = document.get();
			assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
			IDocumentPartitioner setupPartitioner = null;
			if (Utilities.contains(expectedDocumentClass.getInterfaces(), IDocumentExtension3.class)) {
				setupPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
			}
			else {
				setupPartitioner = document.getDocumentPartitioner();
			}
			assertTrue("wrong partitioner in document.", expectedPartioner.isInstance(setupPartitioner));

			testClone(model);

		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
		return contents;
	}

	private void testClone(IStructuredModel structuredModel) throws IOException {
		IDOMDocument document = ((IDOMModel) structuredModel).getDocument();
		INodeNotifier notifier = document;
		ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter) notifier.getAdapterFor(ModelQueryAdapter.class);
		assertNotNull("initial modelQueryAdapter should not be null", modelQueryAdapter);
		
		IStructuredModel newModel = structuredModel.newInstance();
		IDOMDocument newDocument = ((IDOMModel) newModel).getDocument();
		INodeNotifier newNotifier = newDocument;
		ModelQueryAdapter result = (ModelQueryAdapter) newNotifier.getAdapterFor(ModelQueryAdapter.class);
		assertNotNull("newInstance modelQueryAdapter should not be null", result);


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
	private String doTestCreateModelWithNonExistIFile(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		String contents = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IFile file = (IFile) fTestProject.findMember(filePath);
		// file will be null if the resource does not exist
		if (file == null) {
			file = fTestProject.getFile(filePath);
		}
		IStructuredModel model = modelManager.getNewModelForEdit(file, false);
		try {
			assertNotNull(model);
			IStructuredDocument document = model.getStructuredDocument();
			assertNotNull(document);
			contents = document.get();
			assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
			IDocumentPartitioner setupPartitioner = null;
			if (Utilities.contains(expectedDocumentClass.getInterfaces(), IDocumentExtension3.class)) {
				setupPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
			}
			else {
				setupPartitioner = document.getDocumentPartitioner();
			}
			assertTrue("wrong partitioner in document.", expectedPartioner.isInstance(setupPartitioner));
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
		return contents;
	}

}
