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
package org.eclipse.wst.html.core.tests.parser;

import java.util.Iterator;

import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UpdaterTest10 extends ModelTest {
	/**
	 * Constructor for UpdaterTest10.
	 * 
	 * @param name
	 */
	public UpdaterTest10(String name) {
		super(name);
	}

	public UpdaterTest10() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest10().testModel();
	}

	public void testModel() {
		XMLModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			a.setAttribute("b", "c");
			Element b = document.createElement("b");
			a.appendChild(b);

			printSource(model);
			printTree(model);

			outputWriter.writeln("IStructuredDocumentRegion:");
			XMLNode node = (XMLNode) b;
			IStructuredDocumentRegion flatNode = node.getStartStructuredDocumentRegion();
			outputWriter.writeln(flatNode.getText());
			Iterator e = flatNode.getRegions().iterator();
			int i = 0;
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				boolean ok = true; // no longer has parent.
								   // //(region.getParent() == flatNode);
				outputWriter.writeln(i + "(" + flatNode.getText(region) + ")" + ok);
			}
			outputWriter.writeln("");

			b.setAttribute("c", "d");

			printSource(model);
			printTree(model);

			outputWriter.writeln("IStructuredDocumentRegion:");
			node = (XMLNode) b;
			flatNode = node.getStartStructuredDocumentRegion();
			outputWriter.writeln(flatNode.getText());
			e = flatNode.getRegions().iterator();
			i = 0;
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				boolean ok = true; // no longer has parent.
								   // (region.getParent() == flatNode);
				outputWriter.writeln(i + "(" + flatNode.getText(region) + ")" + ok);
			}
			outputWriter.writeln("");

			a.setAttribute("b", "cd");

			printSource(model);
			printTree(model);

			outputWriter.writeln("IStructuredDocumentRegion:");
			node = (XMLNode) b;
			flatNode = node.getStartStructuredDocumentRegion();
			outputWriter.writeln(flatNode.getText());
			e = flatNode.getRegions().iterator();
			i = 0;
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				boolean ok = true; // (region.getParent() == flatNode);
				outputWriter.writeln(i + "(" + flatNode.getText(region) + ")" + ok);
			}
			outputWriter.writeln("");

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}