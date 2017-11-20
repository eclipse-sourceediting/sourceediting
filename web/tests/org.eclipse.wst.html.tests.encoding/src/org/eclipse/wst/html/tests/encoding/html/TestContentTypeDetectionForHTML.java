/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.html.tests.encoding.html;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.tests.encoding.read.TestContentTypeDetection;

public class TestContentTypeDetectionForHTML extends TestContentTypeDetection {
	public void testFile57() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/EmptyFile.html", null);
	}

	public void testFile58() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/IllformedNormalNonDefault.html", null);
	}

	public void testFile59() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/LargeNoEncoding.html", null);
	}


	public void testFile60() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/LargeNonDefault.html", null);
	}

	public void testFile61() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/MultiNonDefault.html", null);
	}

	public void testFile62() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/NoEncoding.html", null);
	}

	public void testFile63() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/noquotes.html", null);
	}

	public void testFile64() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/NormalNonDefault.html", null);
	}
	
	public void testFile65() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/utf16be.html", null);
	}
	
	public void testFile66() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/utf16le.html", null);
	}
	
	public void testFile67() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/noquotesUTF16le.html", null);
	}
	
	public void testFile68() throws CoreException, IOException {
		doTest("org.eclipse.wst.html.core.htmlsource", "testfiles/html/utf16BOM.html", null);
	}
	
}
