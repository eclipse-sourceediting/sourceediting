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

package org.eclipse.wst.css.tests.encoding.css;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDetection;

public class TestContentTypeDetectionForCSS extends TestContentTypeDetection {
	public void testFile7() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/nonStandardIllFormed.css", null);
	}

	public void testFile1001() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/emptyFile.css", null);
	}

	public void testFile1002() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/encoding_test_eucjp.css", null);
	}

	public void testFile1003() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/encoding_test_jis.css", null);
	}

	public void testFile4() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/encoding_test_sjis.css", null);
	}


	public void testFile5() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/noEncoding.css", null);
	}

	public void testFile6() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/nonStandard.css", null);
	}

	public void testFile8() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/nonStandardIllFormed2.css", null);
	}

	public void testUTF16be() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/utf16be.css", null);
	}
	
	public void testUTF16le() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/utf16le.css", null);
	}
	
	public void testUTF16beMalformed() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/utf16beMalformed.css", null);
	}
	
	public void testUTF16leMalformed() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/utf16leMalformed.css", null);
	}
	
	public void testUTF16BOM() throws CoreException, IOException {
		doTest("org.eclipse.wst.css.core.csssource", "testfiles/css/utf16BOM.css", null);
	}
}
