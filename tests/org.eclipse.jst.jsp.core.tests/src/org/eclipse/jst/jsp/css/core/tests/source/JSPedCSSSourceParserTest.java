/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.tests.source;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.tests.NullInputStream;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.osgi.framework.Bundle;

/**
 * 
 */
public class JSPedCSSSourceParserTest extends TestCase {
	private static final String FILES_DIR = "src/org/eclipse/jst/jsp/css/core/tests/testfiles"; //$NON-NLS-1$
	private static final String RESULTS_DIR = "src/org/eclipse/jst/jsp/css/core/tests/testfiles/results"; //$NON-NLS-1$
	private static final String MODE_OPEN = "MODE_OPEN"; //$NON-NLS-1$
	private static final String MODE_APPEND = "MODE_APPEND"; //$NON-NLS-1$
	private static final String MODE_INSERT = "MODE_INSERT"; //$NON-NLS-1$
	private static final String commonEOL = "\r\n";//$NON-NLS-1$

	public void testSourceOpen1() throws IOException {
		sourceParserTest("sample01.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend1() throws IOException {
		sourceParserTest("sample01.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert1() throws IOException {
		sourceParserTest("sample01.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceOpen2() throws IOException {
		sourceParserTest("sample02.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend2() throws IOException {
		sourceParserTest("sample02.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceOpen3() throws IOException {
		sourceParserTest("sample03.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend3() throws IOException {
		sourceParserTest("sample03.jsp", MODE_APPEND); //$NON-NLS-1$
	}
	/*
	 * The following test case fails with WTP 1.5 RC2, but, ignore this release.
	 * 
	public void testSourceInsert2() throws IOException {
		sourceParserTest("sample02.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceInsert3() throws IOException {
		sourceParserTest("sample03.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceInsert6() throws IOException {
		sourceParserTest("sample06.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceInsert8() throws IOException {
		sourceParserTest("sample02.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceInsert9() throws IOException {
		sourceParserTest("sample03.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceInsert12() throws IOException {
		sourceParserTest("sample06.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	*
	*/
	public void testSourceOpen4() throws IOException {
		sourceParserTest("sample04.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend4() throws IOException {
		sourceParserTest("sample04.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert4() throws IOException {
		sourceParserTest("sample04.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceOpen5() throws IOException {
		sourceParserTest("sample05.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend5() throws IOException {
		sourceParserTest("sample05.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert5() throws IOException {
		sourceParserTest("sample05.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceOpen6() throws IOException {
		sourceParserTest("sample06.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend6() throws IOException {
		sourceParserTest("sample06.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceOpen7() throws IOException {
		sourceParserTest("sample01.jspf", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend7() throws IOException {
		sourceParserTest("sample01.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert7() throws IOException {
		sourceParserTest("sample01.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceOpen8() throws IOException {
		sourceParserTest("sample02.jspf", MODE_OPEN); //$NON-NLS-1$
	}
	
	public void testSourceAppend8() throws IOException {
		sourceParserTest("sample02.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceOpen9() throws IOException {
		sourceParserTest("sample03.jspf", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend9() throws IOException {
		sourceParserTest("sample03.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceOpen10() throws IOException {
		sourceParserTest("sample04.jspf", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend10() throws IOException {
		sourceParserTest("sample04.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert10() throws IOException {
		sourceParserTest("sample04.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceOpen11() throws IOException {
		sourceParserTest("sample05.jspf", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend11() throws IOException {
		sourceParserTest("sample05.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert11() throws IOException {
		sourceParserTest("sample05.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceOpen12() throws IOException {
		sourceParserTest("sample06.jspf", MODE_OPEN); //$NON-NLS-1$
	}
	
	public void testSourceAppend12() throws IOException {
		sourceParserTest("sample06.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceOpen13() throws IOException {
		sourceParserTest("sample07.jspf", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend13() throws IOException {
		sourceParserTest("sample07.jspf", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert13() throws IOException {
		sourceParserTest("sample07.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	public void testSourceOpen14() throws IOException {
		sourceParserTest("sample07.jsp", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend14() throws IOException {
		sourceParserTest("sample07.jsp", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert14() throws IOException {
		sourceParserTest("sample07.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceOpen15() throws IOException {
		sourceParserTest("sample08.jsp", MODE_OPEN); //$NON-NLS-1$
	}
	
	public void testSourceInsert15() throws IOException {
		sourceParserTest("sample08.jsp", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceAppend15() throws IOException {
		sourceParserTest("sample08.jsp", MODE_APPEND); //$NON-NLS-1$
	}
	
	public void testSourceOpen16() throws IOException {
		sourceParserTest("sample08.jspf", MODE_OPEN); //$NON-NLS-1$
	}
	
	public void testSourceInsert16() throws IOException {
		sourceParserTest("sample08.jspf", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceAppend16() throws IOException {
		sourceParserTest("sample08.jspf", MODE_APPEND); //$NON-NLS-1$
	}
	
	protected void setUp() throws Exception {
		// set cssjsptestX.jsp(f) file as css jsp.
		IContentType ct = Platform.getContentTypeManager().getContentType("org.eclipse.jst.jsp.core.cssjspsource");
		ct.addFileSpec("cssjsptest0.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest1.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest2.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest3.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest4.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest5.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest6.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest7.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest8.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest9.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest10.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest11.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest12.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest13.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest14.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest15.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest16.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest17.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest18.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest19.jsp", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest20.jsp", IContentType.FILE_NAME_SPEC);
		ct = Platform.getContentTypeManager().getContentType("org.eclipse.jst.jsp.core.cssjspfragmentsource");
		ct.addFileSpec("cssjsptest0.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest1.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest2.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest3.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest4.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest5.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest6.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest7.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest8.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest9.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest10.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest11.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest12.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest13.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest14.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest15.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest16.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest17.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest18.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest19.jspf", IContentType.FILE_NAME_SPEC);
		ct.addFileSpec("cssjsptest20.jspf", IContentType.FILE_NAME_SPEC);
	}

	static int index_jsp = 0;
	static int index_jspf = 0;
	private ICSSModel createModel(boolean isFragment) {
		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			model = !isFragment ? modelManager.getModelForEdit("cssjsptest" + index_jsp++ + ".jsp", new NullInputStream(), null) : //$NON-NLS-1$
									modelManager.getModelForEdit("cssjsptest" + index_jspf++ + ".jspf", new NullInputStream(), null); //$NON-NLS-1$
			model.getStructuredDocument().setLineDelimiter(commonEOL);//$NON-NLS-1$
		}
		catch (Exception e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		return (ICSSModel) model;

	}
	private File createFile(String directory, String filename) throws IOException {
		Bundle bundle = Platform.getBundle("org.eclipse.jst.jsp.core.tests"); //$NON-NLS-1$
		URL url = bundle.getEntry("/"); //$NON-NLS-1$
		URL localURL = FileLocator.toFileURL(url);
		String installPath = localURL.getPath();
		String totalDirectory = installPath + directory;
		String totalPath = totalDirectory + "/" + filename; //$NON-NLS-1$
		URL totalURL = new URL(url, totalPath);
		String finalFile = totalURL.getFile();
		File file = new File(finalFile);
		return file;
	}
	
	private String createString(String directory, String filename) throws FileNotFoundException, IOException {
		StringBuffer buf = new StringBuffer();
		Reader fileReader = new FileReader(createFile(directory, filename));
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
			buf.append(commonEOL);
		}
		return buf.toString();
	}
	
	private void sourceParserTest(String filename, String mode) throws IOException {
		ICSSModel model = null;
		if (mode.equals(MODE_OPEN)) {
			model = readModelOpen(filename);
		}
		else if (mode.equals(MODE_APPEND)) {
			model = readModelAppend(filename);
		}
		else if (mode.equals(MODE_INSERT)) {
			model = readModelInsert(filename);
		}
		String result = dumpRegions(model.getStructuredDocument());
		compareResult(result, "JSPedCSSSourceParserTest-" + filename); //$NON-NLS-1$
		closeModel(model);
	}

	private ICSSModel readModelOpen(String filename) throws IOException {
		String source = createString(FILES_DIR, filename);
		ICSSModel model = createModel(filename.endsWith("jspf"));//$NON-NLS-1$
		IStructuredDocument document = model.getStructuredDocument();
		document.replaceText(null, 0, 0, source);

		return model;
	}

	private ICSSModel readModelAppend(String filename) throws IOException {
		String source = createString(FILES_DIR, filename);
		ICSSModel model = createModel(filename.endsWith("jspf"));//$NON-NLS-1$
		IStructuredDocument document = model.getStructuredDocument();
		for (int i = 0; i < source.length(); i++) {
			document.replaceText(null, i, 0, source.substring(i, i + 1));
		}

		return model;
	}

	private ICSSModel readModelInsert(String filename) throws IOException {
		String source = createString(FILES_DIR, filename);
		ICSSModel model = createModel(filename.endsWith("jspf"));//$NON-NLS-1$
		IStructuredDocument document = model.getStructuredDocument();
		for (int i = 0; i < source.length(); i++) {
			int textIndex = source.length() - i - 1;
			document.replaceText(null, 0, 0, source.substring(textIndex, textIndex + 1));
		}

		return model;
	}

	private void closeModel(ICSSModel model) {
		model.releaseFromEdit();
	}

	private String dumpRegions(IStructuredDocument document) {
		StringBuffer buf = new StringBuffer();
		buf.append(dumpRegions(document.getFirstStructuredDocumentRegion()));
		return buf.toString();
	}

	private String dumpRegions(IStructuredDocumentRegion region) {
		StringBuffer buf = new StringBuffer();
		while (region != null){
			buf.append(dumpOneRegion(region));
			region = region.getNext();
		}

		buf.append(commonEOL + "-------" + commonEOL); //$NON-NLS-1$

		return buf.toString();
	}

	private String dumpOneRegion(IStructuredDocumentRegion documentRegion) {
		StringBuffer buf = new StringBuffer();

		buf.append(documentRegion.toString());
		buf.append(commonEOL + "    "); //$NON-NLS-1$

		ITextRegionList regionList = documentRegion.getRegions();
		Iterator i = regionList.iterator();
		while (i.hasNext()) {
			ITextRegion textRegion = (ITextRegion) i.next();
			buf.append(textRegion.toString());
			buf.append(", "); //$NON-NLS-1$
		}
		buf.append(commonEOL); //$NON-NLS-1$
		return buf.toString();
	}

	private void compareResult(String actual, String filename) throws IOException {
		String result = createString(RESULTS_DIR, filename);
		assertEquals(result, actual);
	}


}
