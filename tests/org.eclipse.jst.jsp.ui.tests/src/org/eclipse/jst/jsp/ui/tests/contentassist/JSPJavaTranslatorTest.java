/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
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

	public void testJSPInJavascript(String filename) throws UnsupportedEncodingException, IOException {
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
				int cursorStart = translation.indexOf("out.print(\"\"+\n testJspString") + 14;
				assertEquals("incorrect cursor position >" + cursorStart, 667, cursorStart);
			}
			sm.releaseFromRead();
		}
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
