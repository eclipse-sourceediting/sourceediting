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
import org.eclipse.wst.xml.core.internal.document.SourceValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

public class SourceTest extends ModelTest {
	/**
	 * Constructor for SourceTest.
	 * 
	 * @param name
	 */
	public SourceTest(String name) {
		super(name);
	}

	public SourceTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new SourceTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Text t = document.createTextNode("t");
			document.appendChild(t);
			SourceValidator validator = new SourceValidator(t);

			String source = null;
			String result = null;

			source = "aaa<bbb>ccc";
			fOutputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			fOutputWriter.writeln("result: " + result);

			source = "aaa&amp;\"'bbb&gt;&lt;ccc&quot;&#64;";
			fOutputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			fOutputWriter.writeln("result: " + result);

			source = "&amp;&&";
			fOutputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			fOutputWriter.writeln("result: " + result);

			source = "&bbb&<>ccc";
			fOutputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			fOutputWriter.writeln("result: " + result);

			source = "&unk;&unk";
			fOutputWriter.writeln("source: " + source);
			try {
				validator.validateSource(source);
			}
			catch (InvalidCharacterException ex) {
				fOutputWriter.writeln(ex.getMessage());
			}
			result = validator.convertSource(source);
			fOutputWriter.writeln("result: " + result);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
