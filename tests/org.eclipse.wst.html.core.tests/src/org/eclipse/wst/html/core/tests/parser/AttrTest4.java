/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import java.io.StringWriter;

import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.document.StructuredDocumentRegionChecker;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

public class AttrTest4 extends TestCase {
	private StringWriter fOutputWriter;

	public AttrTest4() {
		super();
	}

	public AttrTest4(String name) {
		super(name);
	}

	private IDOMModel createModel() {
		IDOMModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			model = (IDOMModel) modelManager.createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	public void printDOMDocument(IDOMModel model) {
		printStructuredDocument(model.getStructuredDocument());
		new StructuredDocumentRegionChecker(fOutputWriter).checkModel(model);
		fOutputWriter.write("Tree :\n");
		printNode(model.getDocument(), 0);
	}

	private void printNode(Node node, int indent) {
		StringBuffer buffer = new StringBuffer(10);
		for (int i = 0; i < indent; i++)
			buffer.append("--");
		buffer.append(StringUtils.escape(node.toString()));
		fOutputWriter.write(buffer.toString());
		fOutputWriter.write('\n');
		indent++;
		Node child = node.getFirstChild();
		while (child != null) {
			printNode(child, indent);
			child = child.getNextSibling();
		}
	}

	protected void printStructuredDocument(IStructuredDocument document) {
		fOutputWriter.write("StructuredDocument Regions :\n");
		IStructuredDocumentRegion flatnode = document.getFirstStructuredDocumentRegion();
		while (flatnode != null) {
			fOutputWriter.write(flatnode.toString());
			fOutputWriter.write('\n');
			flatnode = flatnode.getNext();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		fOutputWriter = new StringWriter();
	}

	public void testAttrName() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a href=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 11] (<a href=''>)\n[11, 15] (text)\n[15, 19] (</a>)\nTree :\n#document\n--a/a@[0, 11] (<a href&#61;''>)@[15, 19] (</a>)\n----#text(text)@[11, 15] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "href", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "href", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameAltogether() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a [(*ngFor)]=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 17] (<a [(*ngFor)]=''>)\n[17, 21] (text)\n[21, 25] (</a>)\nTree :\n#document\n--a/a@[0, 17] (<a [(*ngFor)]&#61;''>)@[21, 25] (</a>)\n----#text(text)@[17, 21] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "[(*ngFor)]", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "[(*ngFor)]", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameBracket() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a [href]=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a [href]=''>)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a [href]&#61;''>)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "[href]", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "[href]", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameLeadingHash() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a #ngFor=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a #ngFor=''>)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a #ngFor&#61;''>)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "#ngFor", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "#ngFor", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameParentheses() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a (click)=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 14] (<a (click)=''>)\n[14, 18] (text)\n[18, 22] (</a>)\nTree :\n#document\n--a/a@[0, 14] (<a (click)&#61;''>)@[18, 22] (</a>)\n----#text(text)@[14, 18] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "(click)", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "(click)", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameSplat() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a *ngFor=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a *ngFor=''>)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a *ngFor&#61;''>)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "*ngFor", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "*ngFor", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNameTrailingHash() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.setText(this, "<a ngFor#=''>text</a>");
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a ngFor#=''>)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a ngFor#&#61;''>)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "ngFor#", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "ngFor#", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNodeAltogether() {
		IDOMModel model = null;
		try {
			model = createModel();
			Document document = model.getDocument();
			Element anchor = document.createElement("a");
			anchor.setAttribute("[(*ngFor)]", "");
			anchor.appendChild(document.createTextNode("text"));
			document.appendChild(anchor);
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 17] (<a [(*ngFor)]=\"\">)\n[17, 21] (text)\n[21, 25] (</a>)\nTree :\n#document\n--a/a@[0, 17] (<a [(*ngFor)]&#61;\"\">)@[21, 25] (</a>)\n----#text(text)@[17, 21] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "[(*ngFor)]", model.getStructuredDocument().getFirstStructuredDocumentRegion().getText(model.getStructuredDocument().getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "[(*ngFor)]", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNodeBracket() {
		IDOMModel model = null;
		try {
			model = createModel();
			Document document = model.getDocument();
			Element anchor = document.createElement("a");
			anchor.setAttribute("[href]", "");
			anchor.appendChild(document.createTextNode("text"));
			document.appendChild(anchor);
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a [href]=\"\">)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a [href]&#61;\"\">)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "[href]", model.getStructuredDocument().getFirstStructuredDocumentRegion().getText(model.getStructuredDocument().getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "[href]", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAttrNodeSplat() {
		IDOMModel model = null;
		try {
			model = createModel();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();
			Element anchor = document.createElement("a");
			anchor.setAttribute("*ngFor", "");
			anchor.appendChild(document.createTextNode("text"));
			document.appendChild(anchor);
			printDOMDocument(model);
			assertEquals("Not as expected", "StructuredDocument Regions :\n[0, 13] (<a *ngFor=\"\">)\n[13, 17] (text)\n[17, 21] (</a>)\nTree :\n#document\n--a/a@[0, 13] (<a *ngFor&#61;\"\">)@[17, 21] (</a>)\n----#text(text)@[13, 17] (text)\n", fOutputWriter.toString());
			assertEquals("Wrong attribute name", "*ngFor", structuredDocument.getFirstStructuredDocumentRegion().getText(structuredDocument.getFirstStructuredDocumentRegion().getRegions().get(2)));
			assertEquals("Wrong attribute name", "*ngFor", model.getDocument().getDocumentElement().getAttributes().item(0).getNodeName());
		}
		finally {
			model.releaseFromEdit();
		}
	}
}
