/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.source;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

/**
 * 
 */
public class CSSSourceParserTest extends TestCase {
	public void testSourceOpen1() throws IOException {
		sourceParserTest("sample01.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen2() throws IOException {
		sourceParserTest("sample02.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen3() throws IOException {
		sourceParserTest("sample03.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen4() throws IOException {
		sourceParserTest("sample04.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen5() throws IOException {
		sourceParserTest("sample05.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen6() throws IOException {
		sourceParserTest("sample06.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceOpen7() throws IOException {
		sourceParserTest("sample07.css", MODE_OPEN); //$NON-NLS-1$
	}
	
	public void testSourceOpen8() throws IOException {
		sourceParserTest("sample08.css", MODE_OPEN); //$NON-NLS-1$
	}

	public void testSourceAppend1() throws IOException {
		sourceParserTest("sample01.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend2() throws IOException {
		sourceParserTest("sample02.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend3() throws IOException {
		sourceParserTest("sample03.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend4() throws IOException {
		sourceParserTest("sample04.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend5() throws IOException {
		sourceParserTest("sample05.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend6() throws IOException {
		sourceParserTest("sample06.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceAppend7() throws IOException {
		sourceParserTest("sample07.css", MODE_APPEND); //$NON-NLS-1$
	}
	
	public void testSourceAppend8() throws IOException {
		sourceParserTest("sample08.css", MODE_APPEND); //$NON-NLS-1$
	}

	public void testSourceInsert1() throws IOException {
		sourceParserTest("sample01.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert2() throws IOException {
		sourceParserTest("sample02.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert3() throws IOException {
		sourceParserTest("sample03.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert4() throws IOException {
		sourceParserTest("sample04.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert5() throws IOException {
		sourceParserTest("sample05.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert6() throws IOException {
		sourceParserTest("sample06.css", MODE_INSERT); //$NON-NLS-1$
	}

	public void testSourceInsert7() throws IOException {
		sourceParserTest("sample07.css", MODE_INSERT); //$NON-NLS-1$
	}
	
	public void testSourceInsert8() throws IOException {
		sourceParserTest("sample08.css", MODE_INSERT); //$NON-NLS-1$
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
		compareResult(result, "CSSSourceParserTest-" + filename); //$NON-NLS-1$
		closeModel(model);
	}

	private ICSSModel readModelOpen(String filename) throws IOException {
		String source = FileUtil.createString(FILES_DIR, filename);
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		document.replaceText(null, 0, 0, source);

		return model;
	}

	private ICSSModel readModelAppend(String filename) throws IOException {
		String source = FileUtil.createString(FILES_DIR, filename);
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		for (int i = 0; i < source.length(); i++) {
			document.replaceText(null, i, 0, source.substring(i, i + 1));
		}

		return model;
	}

	private ICSSModel readModelInsert(String filename) throws IOException {
		String source = FileUtil.createString(FILES_DIR, filename);
		ICSSModel model = FileUtil.createModel();
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
		do {
			buf.append(dumpOneRegion(region));
			region = region.getNext();
		}
		while (region != null);

		buf.append(FileUtil.commonEOL + "-------" + FileUtil.commonEOL); //$NON-NLS-1$

		return buf.toString();
	}

	private String dumpOneRegion(IStructuredDocumentRegion documentRegion) {
		StringBuffer buf = new StringBuffer();
		buf.append(documentRegion.toString());
		buf.append(FileUtil.commonEOL + "    "); //$NON-NLS-1$

		ITextRegionList regionList = documentRegion.getRegions();
		Iterator i = regionList.iterator();
		while (i.hasNext()) {
			ITextRegion textRegion = (ITextRegion) i.next();
			buf.append(textRegion.toString());
			buf.append(", "); //$NON-NLS-1$
		}
		buf.append(FileUtil.commonEOL); //$NON-NLS-1$
		return buf.toString();
	}

	private boolean fDump = false;

	private void compareResult(String actual, String filename) throws IOException {
		if (fDump) {
			FileUtil.dumpString(actual, RESULTS_DIR, filename);
		}
		else {
			String result = FileUtil.createString(RESULTS_DIR, filename);
			assertEquals(result, actual);
		}
	}

	private static final String FILES_DIR = "src/org/eclipse/wst/css/core/tests/testfiles"; //$NON-NLS-1$
	private static final String RESULTS_DIR = "src/org/eclipse/wst/css/core/tests/testfiles/results"; //$NON-NLS-1$
	private static final String MODE_OPEN = "MODE_OPEN"; //$NON-NLS-1$
	private static final String MODE_APPEND = "MODE_APPEND"; //$NON-NLS-1$
	private static final String MODE_INSERT = "MODE_INSERT"; //$NON-NLS-1$
}
