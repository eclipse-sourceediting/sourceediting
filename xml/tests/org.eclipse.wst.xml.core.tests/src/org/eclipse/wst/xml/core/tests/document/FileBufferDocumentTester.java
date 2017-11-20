/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.document;

import java.io.IOException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;



public class FileBufferDocumentTester extends UnzippedProjectTester {

	private void doTestCreate(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws CoreException, IOException {
		IFile file = (IFile) fTestProject.findMember(filePath);
		assertNotNull("Test Case in error. Could not find file " + filePath, file);
		IPath fullPath = file.getFullPath();
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		bufferManager.connect(fullPath, LocationKind.IFILE, null);
		ITextFileBuffer buffer = bufferManager.getTextFileBuffer(fullPath);
		IDocument document = buffer.getDocument();
		assertNotNull(document);
		assertTrue("wrong class of document: " + (document != null ? document.getClass() : null), expectedDocumentClass.isInstance(document));
		assertTrue("document does not implement IDocumentExtension3", document instanceof IDocumentExtension3);
		IDocumentPartitioner actualPartitioner = ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		assertTrue("wrong partitioner in document: " + actualPartitioner, expectedPartioner.isInstance(actualPartitioner));
		bufferManager.disconnect(fullPath, LocationKind.IFILE, null);

//		doTestCreateWithFacade(file, expectedDocumentClass, expectedPartioner);
		 
	}

	/**
	 * @param file
	 * @param expectedDocumentClass
	 * @param expectedPartioner
	 * @throws CoreException
	 * @throws IOException
	 */
//	private void doTestCreateWithFacade(IFile file, Class expectedDocumentClass, Class expectedPartioner) throws IOException, CoreException {
//		DocumentLoaderForXML documentLoaderForXML = new DocumentLoaderForXML();
//		IDocument document = documentLoaderForXML.createNewStructuredDocument(file, null);
//		Assert.assertNotNull(document);
//	}

//	private void doTestCreateEmpty(String filePath, Class expectedDocumentClass, Class expectedPartioner) throws CoreException {
//		IFile file = (IFile) fTestProject.findMember(filePath);
//		assertTrue("Test Case in error. Non-existent file existed! " + filePath, file == null);
//		IPath locationPath = new Path(filePath);
//		IFile nonExistingFile = fTestProject.getFile(locationPath);
//		locationPath = nonExistingFile.getFullPath();
//		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
//		// can not "connect" to non-existant location
//		//bufferManager.connect(locationPath, null);
//		IDocument document = bufferManager.createEmptyDocument(locationPath, LocationKind.IFILE);
//		assertNotNull(document);
//		assertTrue("wrong class of document", expectedDocumentClass.isInstance(document));
//		assertTrue("wrong partitioner in document: " + document.getDocumentPartitioner(), expectedPartioner.isInstance(document.getDocumentPartitioner()));
//		bufferManager.disconnect(locationPath, LocationKind.IFILE, null);
//
//	}



	public void testFile103() throws CoreException, IOException {
		doTestCreate("testfiles/xml/EmptyFile.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile104() throws CoreException, IOException {
		doTestCreate("testfiles/xml/eucjp.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile105() throws CoreException, IOException {
		doTestCreate("testfiles/xml/IllformedNormalNonDefault.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile106() throws CoreException, IOException {
		doTestCreate("testfiles/xml/MalformedNoEncoding.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile107() throws CoreException, IOException {
		doTestCreate("testfiles/xml/MalformedNoEncoding.xsl", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile108() throws CoreException, IOException {
		doTestCreate("testfiles/xml/NoEncoding.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile109() throws CoreException, IOException {
		doTestCreate("testfiles/xml/NormalNonDefault.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile110() throws CoreException, IOException {
		doTestCreate("testfiles/xml/shiftjis.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile111() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testExtraJunk.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile112() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testExtraValidStuff.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile113() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testIllFormed.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile114() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testIllFormed2.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile115() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testIllFormed3.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile116() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testIllFormed4.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile117() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testMultiLine.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile118() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNoEncodingValue.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile119() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNormalCase.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile120() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNoXMLDecl.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile121() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNoXMLDeclAtFirst.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile122() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNoXMLDeclInLargeFile.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile123() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testUTF16.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile124() throws CoreException, IOException {
		doTestCreate("testfiles/xml/UTF16LEAtStartOfLargeFile.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile125() throws CoreException, IOException {
		doTestCreate("testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeader2.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile126() throws CoreException, IOException {
		doTestCreate("testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeaderBE.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile127() throws CoreException, IOException {
		doTestCreate("testfiles/xml/utf16WithJapaneseChars.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	public void testFile128() throws CoreException, IOException {
		doTestCreate("testfiles/xml/UTF8With3ByteBOM.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

//	public void testNonExistentXML() throws CoreException {
//		doTestCreateEmpty("testfiles/xml/testnonexistent.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
//	}

	public void testSimpleCase() throws CoreException, IOException {
		doTestCreate("testfiles/xml/testNormalCase.xml", BasicStructuredDocument.class, StructuredTextPartitionerForXML.class);
	}

	/*
	public void testFile1() throws CoreException, IOException {
		doTestCreate("testfiles/css/emptyFile.css", BasicStructuredDocument.class, StructuredTextPartitionerForCSS.class);
	}



	public void testFile70() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/MalformedNoEncodingXSL.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile81() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testBrokenLine.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile82() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testDefaultEncoding.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile83() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testDefaultEncodingWithJunk.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile84() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testExtraJunk.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile85() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testExtraValidStuff.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile86() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testIllFormed.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile87() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testIllFormed2.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile88() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testNoEncodingValue.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile89() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testNoPageDirective.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile90() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testNoPageDirectiveAtFirst.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile91() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testNoPageDirectiveInLargeFile.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile92() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testNormalCase.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile93() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/testUTF16.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile94() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeader2.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile95() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeaderBE.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile96() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/utf16WithJapaneseChars.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile97() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/UTF8With3ByteBOM.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile98() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/WellFormedNormalNonDefault.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile99() throws CoreException, IOException {
		doTestCreate("testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile100() throws CoreException, IOException {
		doTestCreate("testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsFormB.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile101() throws CoreException, IOException {
		doTestCreate("testfiles/regressionTestFiles/defect224293/testshiftjisXmlSyntax.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile102() throws CoreException, IOException {
		doTestCreate("testfiles/regressionTestFiles/defect229667/audi.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}


	public void testFile71() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/noEncoding.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile72() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/NoEncodinginXMLDecl.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile73() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/nomalDirectiveCase.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile74() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/nomalDirectiveCaseNoEncoding.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile75() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/nomalDirectiveCaseUsingCharset.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile76() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/nomalDirectiveCaseUsingXMLSyntax.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile77() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/NormalNonDefault.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile78() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/NormalNonDefaultWithXMLDecl.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile79() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/NormalPageCaseNonDefault.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}

	public void testFile80() throws CoreException, IOException {
		doTestCreate("testfiles/jsp/SelColBeanRow12ResultsForm.jsp", BasicStructuredDocument.class, StructuredTextPartitionerForJSP.class);
	}
*/
}
