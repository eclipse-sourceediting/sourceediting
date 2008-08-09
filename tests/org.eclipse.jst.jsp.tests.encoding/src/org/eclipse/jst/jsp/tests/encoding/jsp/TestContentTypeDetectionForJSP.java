/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.tests.encoding.jsp;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.IContentDescriptionForJSP;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDetection;

public class TestContentTypeDetectionForJSP extends TestContentTypeDetection {

	protected void doTest(String expectedContentType, String filePath, Class expectedException, String expectedJSPContentType) throws CoreException, IOException {
		super.doTest(expectedContentType, filePath, expectedException);
		
		IFile file = (IFile) getTestProject().findMember(filePath);
		assertNotNull("Error in test case: file not found: " + filePath, file);


		IContentDescription streamContentDescription = doGetContentTypeBasedOnStream(file);
		IContentDescription fileContentDescription = doGetContentTypeBasedOnFile(file);

		String streamJSPContentType = (String) streamContentDescription.getProperty(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE);

		checkResults(expectedJSPContentType, streamJSPContentType);
		
		
		String fileJSPContentType = (String) fileContentDescription.getProperty(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE);
		checkResults(expectedJSPContentType, fileJSPContentType);


	}

	private void checkResults(String expectedJSPContentType, String foundJSPContentType) {
		if (expectedJSPContentType == null) {
			assertTrue("expected no contentType, but found: " + foundJSPContentType, foundJSPContentType == null);
		}
		else {
			assertTrue("expected " + expectedJSPContentType + " but found " + foundJSPContentType, expectedJSPContentType.equals(foundJSPContentType));
		}
	}
	
	public void testFile100() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsFormB.jsp", null, "text/html");
	}

	public void testFile101() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect224293/testshiftjisXmlSyntax.jsp", null, "text/html");
	}

	public void testFile102() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect229667/audi.jsp", null, "text/html");
	}

	public void testFile65() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/EmptyFile.jsp", null, null);
	}

	public void testFile66() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/IllformedNormalNonDefault.jsp", null, "text/html");
	}

	public void testFile67() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/inValidEncodingValue.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile68() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/javaEncodingValue.jsp", null, "text/html");
	}

	public void testFile69() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/MalformedNoEncoding.jsp", null, null);
	}

	public void testFile70() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/MalformedNoEncodingXSL.jsp", null, null);
	}

	public void testFile71() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/noEncoding.jsp", null, "text/html");
	}

	public void testFile72() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NoEncodinginXMLDecl.jsp", null, null);
	}

	public void testFile73() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCase.jsp", null, "text/html");
	}

	public void testFile74() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseNoEncoding.jsp", null, null);
	}

	public void testFile75() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseUsingCharset.jsp", null, "text/html");
	}

	public void testFile76() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseUsingXMLSyntax.jsp", null, "text/html");
	}

	public void testFile77() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalNonDefault.jsp", null, null);
	}

	public void testFile78() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalNonDefaultWithXMLDecl.jsp", null, "text/html");
	}
	public void nomalDirectiveCasewithXMLDecl() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCasewithXMLDecl.jsp", null, "text/html");
	}
	public void testFile79() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalPageCaseNonDefault.jsp", null, "text/html");
	}

	public void testFile80() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/SelColBeanRow12ResultsForm.jsp", null, "text/html");
	}

	public void testFile81() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testBrokenLine.jsp", null, null);
	}

	public void testFile82() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testDefaultEncoding.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class, "text/html");
	}

	public void testFile83() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testDefaultEncodingWithJunk.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class, "text/html");
	}

	public void testFile84() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testExtraJunk.jsp", null, null);
	}

	public void testFile85() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testExtraValidStuff.jsp", null, null);
	}

	public void testFile86() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testIllFormed.jsp", null, null);
	}

	public void testFile87() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testIllFormed2.jsp", null, null);
	}

	public void testFile88() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoEncodingValue.jsp", null, null);
	}

	public void testFile89() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirective.jsp", null, null);
	}


	public void testFile90() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirectiveAtFirst.jsp", null, "text/html");
	}

	public void testFile91() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirectiveInLargeFile.jsp", null, null);
	}

	public void testFile92() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNormalCase.jsp", null, null);
	}

	public void testFile93() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testUTF16.jsp", null, null);
	}

	public void testFile94() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeader2.jsp", null, null);
	}

	public void testFile95() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeaderBE.jsp", null, null);
	}

	public void testFile96() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16WithJapaneseChars.jsp", null, null);
	}

	public void testFile97() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/UTF8With3ByteBOM.jsp", null, null);
	}

	public void testFile98() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/WellFormedNormalNonDefault.jsp", null, "text/html");
	}

	public void testFile99() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", null, "text/html");
	}
	
	public void testFile103() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16le.jsp", null, "text/html");
	}
	
	public void testFile104() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16be.jsp", null, "text/html");
	}
	
	public void testFile105() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16BOM.jsp", null, null);
	}

	public void testFile106() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16le_xmlStyle.jsp", null, null);
	}

}
