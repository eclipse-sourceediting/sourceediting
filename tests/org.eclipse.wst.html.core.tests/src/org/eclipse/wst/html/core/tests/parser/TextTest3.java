/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.xml.core.internal.document.InvalidCharacterException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TextTest3 extends ModelTest {
	/**
	 * Constructor for TextTest3.
	 * 
	 * @param name
	 */
	public TextTest3(String name) {
		super(name);
	}

	public TextTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new TextTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			IDOMNode text = (IDOMNode) document.createTextNode("text");
			a.appendChild(text);

			try {
				text.setSource("hello <");
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}

			printSource(model);
			printTree(model);

			try {
				text.setSource("hello &lt;");
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}

			printSource(model);
			printTree(model);

			try {
				text.setSource("hello &unk;");
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}

			printSource(model);
			printTree(model);

			try {
				text.setSource("hello &#65;");
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}

			printSource(model);
			printTree(model);

			try {
				text.setSource("hello & good-bye");
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}

			printSource(model);
			printTree(model);


			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
