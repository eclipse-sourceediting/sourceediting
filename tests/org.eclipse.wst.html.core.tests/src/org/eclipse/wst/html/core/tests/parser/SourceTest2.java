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

import org.eclipse.wst.xml.core.document.InvalidCharacterException;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.internal.document.SourceValidator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SourceTest2 extends ModelTest {
	/**
	 * Constructor for SourceTest2.
	 * 
	 * @param name
	 */
	public SourceTest2(String name) {
		super(name);
	}

	public SourceTest2() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new SourceTest2().testModel();
	}

	public void testModel() {
		XMLModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			Attr b = document.createAttribute("b");
			a.setAttributeNode(b);
			document.appendChild(a);
			SourceValidator validator = new SourceValidator(b);

			String source = null;
			String result = null;

			source = "aaa<bbb>ccc";
			outputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				outputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			outputWriter.writeln("result: " + result);

			source = "aaa&amp;bbb&gt;&lt;ccc&quot;&#64;";
			outputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				outputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			outputWriter.writeln("result: " + result);

			source = "&amp;&&";
			outputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				outputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			outputWriter.writeln("result: " + result);

			source = "\"aaa\"";
			outputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				outputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			outputWriter.writeln("result: " + result);

			source = "\"a'a\"";
			outputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				outputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			outputWriter.writeln("result: " + result);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}