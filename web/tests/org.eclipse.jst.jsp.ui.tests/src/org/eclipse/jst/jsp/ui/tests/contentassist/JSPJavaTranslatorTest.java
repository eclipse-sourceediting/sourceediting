/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.java.JSPTranslationUtil;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * This class tests the sed JSPJavaTranslator class
 * 
 * @author pavery
 */
public class JSPJavaTranslatorTest extends TestCase {

	public JSPJavaTranslatorTest(String name) {
		super(name);
	}

	public void testAllFiles() throws UnsupportedEncodingException, IOException {
		// this test is for PMR: 91930 CMVC:218469
		testJSPInJavascript("testfiles/jspInJavascript.jsp");
	}

	void testJSPInJavascript(String filename) throws UnsupportedEncodingException, IOException {
		IStructuredModel sm = getStructuredModelForRead(filename);
		if (sm != null) {
			IDOMNode xmlNode = (IDOMNode) sm.getIndexedRegion(0);

			if (xmlNode != null) {
				JSPTranslator jspt = new JSPTranslator();
				jspt.reset(xmlNode, null);
				// int sourceTextPos = text.indexOf("<%= testJspString") + 17;
				// jspt.setSourceCursor(sourceTextPos); // right after the
				// text
				jspt.translate();
				String translation = jspt.getTranslation().toString();
				// offsets are found using JSPTranslation now
				// int translatedCursorPosition = jspt.getCursorPosition();
				// assertEquals("incorrect cursor position >" +
				// translatedCursorPosition, 519, translatedCursorPosition);
				// assertEquals("translation was incorrect", "testJspString",
				// translation.substring(519, 532));
				int cursorStart = translation.indexOf("out.print( testJspString );" ) + 10;
				assertEquals("incorrect cursor position >" + cursorStart, 914, cursorStart);
			}
			sm.releaseFromRead();
		}
	}

	public void testMultipleJSPSectionsInJavascript() throws Exception {
		String filename = "testfiles/jspInJavascript2.jsp";
		IStructuredModel sm = getStructuredModelForRead(filename);
		assertNotNull("couldn't load JSP for test", sm);
		JSPTranslationUtil translationUtil = new JSPTranslationUtil(sm.getStructuredDocument());

		String javaText = StringUtils.replace(translationUtil.getTranslation().getJavaText(), "\r\n", "\n");
		javaText = StringUtils.replace(javaText, "\r", "\n");
		sm.releaseFromRead();

		String translatedText = loadContents("testfiles/jspInJavascript2.javasource");
		assertEquals("translated contents are not as expected", translatedText, javaText);
	}

	/**
	 * @return
	 */
	private String loadContents(String filename) throws IOException {
		Reader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
		char[] readBuffer = new char[2048];
		int n = reader.read(readBuffer);
		StringBuffer s = new StringBuffer();
		while (n > 0) {
			s.append(readBuffer, 0, n);
			n = reader.read(readBuffer);
		}

		String source = StringUtils.replace(s.toString(), "\r\n", "\n");
		source = StringUtils.replace(source, "\r", "\n");
		return source;
	}

	/**
	 * IMPORTANT whoever calls this must releaseFromRead after they are done
	 * using it.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	protected IStructuredModel getStructuredModelForRead(String filename) throws UnsupportedEncodingException, IOException {
		// create model
		IModelManager modelManager = StructuredModelManager.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);
		IStructuredModel sModel = modelManager.getModelForRead(filename, inStream, null);
		return sModel;
	}
}
