/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDetection;

public class TestContentTypeDetectionForJSP extends TestContentTypeDetection {

	public void testFile100() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsFormB.jsp", null);
	}

	public void testFile101() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect224293/testshiftjisXmlSyntax.jsp", null);
	}

	public void testFile102() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect229667/audi.jsp", null);
	}

	public void testFile65() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/EmptyFile.jsp", null);
	}

	public void testFile66() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/IllformedNormalNonDefault.jsp", null);
	}

	public void testFile67() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/inValidEncodingValue.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile68() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/javaEncodingValue.jsp", null);
	}

	public void testFile69() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/MalformedNoEncoding.jsp", null);
	}

	public void testFile70() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/MalformedNoEncodingXSL.jsp", null);
	}

	public void testFile71() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/noEncoding.jsp", null);
	}

	public void testFile72() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NoEncodinginXMLDecl.jsp", null);
	}

	public void testFile73() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCase.jsp", null);
	}

	public void testFile74() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseNoEncoding.jsp", null);
	}

	public void testFile75() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseUsingCharset.jsp", null);
	}

	public void testFile76() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/nomalDirectiveCaseUsingXMLSyntax.jsp", null);
	}

	public void testFile77() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalNonDefault.jsp", null);
	}

	public void testFile78() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalNonDefaultWithXMLDecl.jsp", null);
	}

	public void testFile79() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/NormalPageCaseNonDefault.jsp", null);
	}

	public void testFile80() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/SelColBeanRow12ResultsForm.jsp", null);
	}

	public void testFile81() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testBrokenLine.jsp", null);
	}

	public void testFile82() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testDefaultEncoding.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile83() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testDefaultEncodingWithJunk.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile84() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testExtraJunk.jsp", null);
	}

	public void testFile85() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testExtraValidStuff.jsp", null);
	}

	public void testFile86() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testIllFormed.jsp", null);
	}

	public void testFile87() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testIllFormed2.jsp", null);
	}

	public void testFile88() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoEncodingValue.jsp", null);
	}

	public void testFile89() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirective.jsp", null);
	}


	public void testFile90() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirectiveAtFirst.jsp", null);
	}

	public void testFile91() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNoPageDirectiveInLargeFile.jsp", null);
	}

	public void testFile92() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testNormalCase.jsp", null);
	}

	public void testFile93() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/testUTF16.jsp", null);
	}

	public void testFile94() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeader2.jsp", null);
	}

	public void testFile95() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeaderBE.jsp", null);
	}

	public void testFile96() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/utf16WithJapaneseChars.jsp", null);
	}

	public void testFile97() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/UTF8With3ByteBOM.jsp", null);
	}

	public void testFile98() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/jsp/WellFormedNormalNonDefault.jsp", null);
	}

	public void testFile99() throws CoreException, IOException {
		doTest("org.eclipse.jst.jsp.core.jspsource", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", null);
	}


}
