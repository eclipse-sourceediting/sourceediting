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
/*
 * Created on Aug 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.sse.core.tests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.DocumentInputStream;
import org.eclipse.wst.xml.tests.encoding.read.TestCodedReader;


/**
 * @author davidw
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TestDocumentReader extends TestCodedReader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#doCoreTest(java.lang.String,
	 *      java.lang.String, org.eclipse.core.resources.IFile)
	 */
	protected Reader doCoreTest(String expectedJavaCharset, String expectedDetectedCharset, IFile file) throws CoreException, IOException {

		IStructuredDocument document = getDocument(file);
		Reader result = new InputStreamReader(new DocumentInputStream(document));
		return result;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile1()
	 */
	public void testFile1() throws CoreException, IOException {

		super.testFile1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile100()
	 */
	public void testFile100() throws CoreException, IOException {

		super.testFile100();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile101()
	 */
	public void testFile101() throws CoreException, IOException {

		super.testFile101();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile102()
	 */
	public void testFile102() throws CoreException, IOException {

		super.testFile102();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile103()
	 */
	public void testFile103() throws CoreException, IOException {

		super.testFile103();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile104()
	 */
	public void testFile104() throws CoreException, IOException {

		super.testFile104();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile105()
	 */
	public void testFile105() throws CoreException, IOException {

		super.testFile105();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile106()
	 */
	public void testFile106() throws CoreException, IOException {

		super.testFile106();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile107()
	 */
	public void testFile107() throws CoreException, IOException {

		super.testFile107();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile108()
	 */
	public void testFile108() throws CoreException, IOException {

		super.testFile108();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile109()
	 */
	public void testFile109() throws CoreException, IOException {

		super.testFile109();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile110()
	 */
	public void testFile110() throws CoreException, IOException {

		super.testFile110();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile111()
	 */
	public void testFile111() throws CoreException, IOException {

		super.testFile111();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile112()
	 */
	public void testFile112() throws CoreException, IOException {

		super.testFile112();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile113()
	 */
	public void testFile113() throws CoreException, IOException {

		super.testFile113();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile114()
	 */
	public void testFile114() throws CoreException, IOException {

		super.testFile114();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile115()
	 */
	public void testFile115() throws CoreException, IOException {

		super.testFile115();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile116()
	 */
	public void testFile116() throws CoreException, IOException {

		super.testFile116();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile117()
	 */
	public void testFile117() throws CoreException, IOException {

		super.testFile117();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile118()
	 */
	public void testFile118() throws CoreException, IOException {

		super.testFile118();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile119()
	 */
	public void testFile119() throws CoreException, IOException {

		super.testFile119();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile120()
	 */
	public void testFile120() throws CoreException, IOException {

		super.testFile120();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile121()
	 */
	public void testFile121() throws CoreException, IOException {

		super.testFile121();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile122()
	 */
	public void testFile122() throws CoreException, IOException {

		super.testFile122();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile123()
	 */
	public void testFile123() throws CoreException, IOException {

		super.testFile123();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile124()
	 */
	public void testFile124() throws CoreException, IOException {

		super.testFile124();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile125()
	 */
	public void testFile125() throws CoreException, IOException {

		super.testFile125();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile126()
	 */
	public void testFile126() throws CoreException, IOException {

		super.testFile126();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile127()
	 */
	public void testFile127() throws CoreException, IOException {

		super.testFile127();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile128()
	 */
	public void testFile128() throws CoreException, IOException {

		super.testFile128();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile2()
	 */
	public void testFile2() throws CoreException, IOException {

		super.testFile2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile3()
	 */
	public void testFile3() throws CoreException, IOException {

		super.testFile3();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile4()
	 */
	public void testFile4() throws CoreException, IOException {

		super.testFile4();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile5()
	 */
	public void testFile5() throws CoreException, IOException {

		super.testFile5();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile57()
	 */
	public void testFile57() throws CoreException, IOException {

		super.testFile57();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile58()
	 */
	public void testFile58() throws CoreException, IOException {

		super.testFile58();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile59()
	 */
	public void testFile59() throws CoreException, IOException {

		super.testFile59();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile6()
	 */
	public void testFile6() throws CoreException, IOException {

		super.testFile6();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile60()
	 */
	public void testFile60() throws CoreException, IOException {

		super.testFile60();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile61()
	 */
	public void testFile61() throws CoreException, IOException {

		super.testFile61();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile62()
	 */
	public void testFile62() throws CoreException, IOException {

		super.testFile62();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile63()
	 */
	public void testFile63() throws CoreException, IOException {

		super.testFile63();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile64()
	 */
	public void testFile64() throws CoreException, IOException {

		super.testFile64();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile65()
	 */
	public void testFile65() throws CoreException, IOException {

		super.testFile65();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile66()
	 */
	public void testFile66() throws CoreException, IOException {

		super.testFile66();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile67()
	 */
	public void testFile67() throws CoreException, IOException {

		super.testFile67();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile68()
	 */
	public void testFile68() throws CoreException, IOException {

		super.testFile68();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile69()
	 */
	public void testFile69() throws CoreException, IOException {

		super.testFile69();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile7()
	 */
	public void testFile7() throws CoreException, IOException {

		super.testFile7();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile70()
	 */
	public void testFile70() throws CoreException, IOException {

		super.testFile70();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile71()
	 */
	public void testFile71() throws CoreException, IOException {

		super.testFile71();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile72()
	 */
	public void testFile72() throws CoreException, IOException {

		super.testFile72();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile73()
	 */
	public void testFile73() throws CoreException, IOException {

		super.testFile73();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile74()
	 */
	public void testFile74() throws CoreException, IOException {

		super.testFile74();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile75()
	 */
	public void testFile75() throws CoreException, IOException {

		super.testFile75();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile76()
	 */
	public void testFile76() throws CoreException, IOException {

		super.testFile76();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile77()
	 */
	public void testFile77() throws CoreException, IOException {

		super.testFile77();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile78()
	 */
	public void testFile78() throws CoreException, IOException {

		super.testFile78();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile79()
	 */
	public void testFile79() throws CoreException, IOException {

		super.testFile79();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile8()
	 */
	public void testFile8() throws CoreException, IOException {

		super.testFile8();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile80()
	 */
	public void testFile80() throws CoreException, IOException {

		super.testFile80();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile81()
	 */
	public void testFile81() throws CoreException, IOException {

		super.testFile81();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile82()
	 */
	public void testFile82() throws CoreException, IOException {

		super.testFile82();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile83()
	 */
	public void testFile83() throws CoreException, IOException {

		super.testFile83();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile84()
	 */
	public void testFile84() throws CoreException, IOException {

		super.testFile84();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile85()
	 */
	public void testFile85() throws CoreException, IOException {

		super.testFile85();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile86()
	 */
	public void testFile86() throws CoreException, IOException {

		super.testFile86();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile87()
	 */
	public void testFile87() throws CoreException, IOException {

		super.testFile87();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile88()
	 */
	public void testFile88() throws CoreException, IOException {

		super.testFile88();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile89()
	 */
	public void testFile89() throws CoreException, IOException {

		super.testFile89();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile90()
	 */
	public void testFile90() throws CoreException, IOException {

		super.testFile90();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile91()
	 */
	public void testFile91() throws CoreException, IOException {

		super.testFile91();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile92()
	 */
	public void testFile92() throws CoreException, IOException {

		super.testFile92();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile93()
	 */
	public void testFile93() throws CoreException, IOException {

		super.testFile93();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile94()
	 */
	public void testFile94() throws CoreException, IOException {

		super.testFile94();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile95()
	 */
	public void testFile95() throws CoreException, IOException {

		super.testFile95();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile96()
	 */
	public void testFile96() throws CoreException, IOException {

		super.testFile96();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile97()
	 */
	public void testFile97() throws CoreException, IOException {

		super.testFile97();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile98()
	 */
	public void testFile98() throws CoreException, IOException {

		super.testFile98();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.tests.encoding.read.TestCodedReader#testFile99()
	 */
	public void testFile99() throws CoreException, IOException {

		super.testFile99();
	}

	private IStructuredDocument getDocument(IFile file) throws IOException, CoreException {
		IStructuredDocument document = null;
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		document = modelManager.createStructuredDocumentFor(file);

		return document;
	}
}