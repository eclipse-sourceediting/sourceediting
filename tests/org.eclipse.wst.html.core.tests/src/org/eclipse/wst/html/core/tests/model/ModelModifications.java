/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.html.core.tests.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.tests.utils.TestWriter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.util.NullInputStream;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;

public class ModelModifications extends TestCase {

	public void testPlainDocument() throws BadLocationException {
		IDocument document = new Document();
		document.set("");
		document.replace(0, 0, "test");
		document.set("");
		document.replace(0, 0, "test2");
		assertTrue(true);

	}

	public void testRepDocumentHTMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createHTMLModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "<tag>text</tag>");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testSetDocumentXMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createXMLModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testRepDocumentJSPMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createJSPModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "<tag>text</tag>");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testSetDocumentHTMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createHTMLModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testSetDocumentJSPMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createJSPModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testRepDocumentXMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file

		model = createXMLModel();

		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "<tag>text</tag>");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testDocumentHTMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file
	
		model = createHTMLModel();
	
		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testDocumentJSPMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file
	
		model = createJSPModel();
	
		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testDocumentXMLMods() throws UnsupportedEncodingException, IOException {
		IDOMModel model = null; // assumes 0-byte html empty file
	
		model = createXMLModel();
	
		try {
			IDOMDocument doc = model.getDocument();
			Element ele = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele);
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.replaceText(this, 0, structuredDocument.getLength(), "");
			Element ele2 = doc.createElement(HTML40Namespace.ElementName.P);
			doc.appendChild(ele2);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	/**
	 * Be sure to release any models obtained from this method.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	private static IDOMModel createXMLModel() throws UnsupportedEncodingException, IOException {

		IStructuredModel model = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();

		model = modelManager.getModelForEdit("test.xml", new NullInputStream(), null);

		// always use the same line delimiter for these tests, regardless
		// of
		// plaform or preference settings
		model.getStructuredDocument().setLineDelimiter(TestWriter.commonEOL);
		return (IDOMModel) model;

	}

	/**
	 * Be sure to release any models obtained from this method.
	 * 
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static IDOMModel createHTMLModel() throws UnsupportedEncodingException, IOException {

		IStructuredModel model = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();

		model = modelManager.getModelForEdit("test.html", new NullInputStream(), null);

		// always use the same line delimiter for these tests, regardless
		// of
		// plaform or preference settings
		model.getStructuredDocument().setLineDelimiter(TestWriter.commonEOL);
		return (IDOMModel) model;

	}

	/**
	 * Be sure to release any models obtained from this method.
	 * 
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static IDOMModel createJSPModel() throws UnsupportedEncodingException, IOException {

		IStructuredModel model = null;

		IModelManager modelManager = StructuredModelManager.getModelManager();

		model = modelManager.getModelForEdit("test.xml", new NullInputStream(), null);

		// always use the same line delimiter for these tests, regardless
		// of
		// plaform or preference settings
		model.getStructuredDocument().setLineDelimiter(TestWriter.commonEOL);


		return (IDOMModel) model;

	}

}
