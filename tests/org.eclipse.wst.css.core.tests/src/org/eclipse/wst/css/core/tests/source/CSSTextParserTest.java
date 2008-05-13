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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.css.core.tests.util.FileUtil;


public class CSSTextParserTest extends TestCase {
	public void testText1() throws IOException {
		textParserTest("sample01.css"); //$NON-NLS-1$
	}

	public void testText2() throws IOException {
		textParserTest("sample02.css"); //$NON-NLS-1$
	}

	public void testText3() throws IOException {
		textParserTest("sample03.css"); //$NON-NLS-1$
	}

	public void testText4() throws IOException {
		textParserTest("sample04.css"); //$NON-NLS-1$
	}

	public void testText5() throws IOException {
		textParserTest("sample05.css"); //$NON-NLS-1$
	}

	public void testText6() throws IOException {
		textParserTest("sample06.css"); //$NON-NLS-1$
	}

	public void testText7() throws IOException {
		textParserTest("sample07.css"); //$NON-NLS-1$
	}
	
	public void testText8() throws IOException {
		textParserTest("sample08.css"); //$NON-NLS-1$
	}

	private void textParserTest(String filename) throws IOException {
		String source = createString(filename); //$NON-NLS-1$
		CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_STYLESHEET, source);
		String result = dumpRegions(parser.getTokenList());
		compareResult(result, "CSSTextParserTest-" + filename); //$NON-NLS-1$
	}

	private String createString(String filename) throws FileNotFoundException, IOException {
		return FileUtil.createString(FILES_DIR, filename);
	}

	private String dumpRegions(List tokens) {
		StringBuffer buf = new StringBuffer();
		Iterator i = tokens.iterator();
		while (i.hasNext()) {
			buf.append(dumpOneRegion((CSSTextToken) i.next()));
		}

		buf.append(FileUtil.commonEOL + "-------" + FileUtil.commonEOL); //$NON-NLS-1$

		return buf.toString();
	}

	private String dumpOneRegion(CSSTextToken token) {
		StringBuffer buf = new StringBuffer();

		buf.append("["); //$NON-NLS-1$
		buf.append(token.image);
		buf.append("] "); //$NON-NLS-1$
		buf.append(token.kind);
		buf.append(" - "); //$NON-NLS-1$
		buf.append(token.start);
		buf.append(", "); //$NON-NLS-1$
		buf.append(token.length);

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
	private static final String RESULTS_DIR = "src/org/eclipse/wst/css/core/tests/testfiles/results";
}
