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
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.IDOMNode;

/**
 * This class tests the sed JSPJavaTranslator class
 * @author pavery
 */
public class JSPJavaTranslatorTest extends TestCase {

	public JSPJavaTranslatorTest(String name) {
		super(name);
	}

	public void testAllFiles() {
		// this test is for PMR: 91930  CMVC:218469
		testJSPInJavascript("testfiles/jspInJavascript.jsp");
	}

	public void testJSPInJavascript(String filename) {
		IStructuredModel sm = getStructuredModelForRead(filename);
		if (sm != null) {
			IStructuredDocument structuredDocument = sm.getStructuredDocument();
			String text = structuredDocument.getText();

			IDOMNode xmlNode = (IDOMNode) sm.getIndexedRegion(0);

			if (xmlNode != null) {
				JSPTranslator jspt = new JSPTranslator();
				jspt.reset(xmlNode, null);
				//int sourceTextPos = text.indexOf("<%= testJspString") + 17;
				//jspt.setSourceCursor(sourceTextPos); // right after the text
				jspt.translate();
				String translation = jspt.getTranslation().toString();
				// offsets are found using JSPTranslation now
				//int translatedCursorPosition = jspt.getCursorPosition();
				//assertEquals("incorrect cursor position >" + translatedCursorPosition, 519, translatedCursorPosition);
				//assertEquals("translation was incorrect", "testJspString", translation.substring(519, 532));
				int cursorStart = translation.indexOf("out.print(\"\"+\n testJspString") + 14;
				assertEquals("incorrect cursor position >" + cursorStart, 585, cursorStart);
			}
			sm.releaseFromRead();
		}
	}

	/**
	 * IMPORTANT whoever calls this must releaseFromRead after they are done using it.
	 * @param filename
	 * @return
	 */
	protected IStructuredModel getStructuredModelForRead(String filename) {
		try {
			// create model
			IModelManager modelManager = StructuredModelManager.getModelManager();
			InputStream inStream = getClass().getResourceAsStream(filename);
			IStructuredModel sModel = modelManager.getModelForRead(filename, inStream, null);
			return sModel;
		}
		catch (IOException ioex) {
			System.out.println("couldn't open file > " + filename);
		}
		return null;
	}
}