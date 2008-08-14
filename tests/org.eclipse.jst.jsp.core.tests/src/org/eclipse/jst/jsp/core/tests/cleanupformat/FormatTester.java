/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.cleanupformat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;

public class FormatTester extends TestCase {
	private static final boolean PRINT_FAILED_FORMAT_TESTS = false;
	protected IModelManager fModelManager = null;
	protected FormatProcessorXML fFormatProcessor = null;
	protected HTMLFormatProcessorImpl fHTMLFormatProcessor = null;

	public FormatTester(String name) {
		super(name);

		// get model manager
		fModelManager = StructuredModelManager.getModelManager();

		// get format processor
		fFormatProcessor = new FormatProcessorXML();

		// get HTML format processor
		fHTMLFormatProcessor = new HTMLFormatProcessorImpl();
	}

	public void testFormat261968() {
		// get model
		IStructuredModel structuredModel = getModel("261968.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "261968.afterDefaultFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatTagOpen() {
		// get model
		IStructuredModel structuredModel = getModel("tagOpen.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "tagOpen.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatTagOpenTagClose() {
		// get model
		IStructuredModel structuredModel = getModel("tagOpenTagClose.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "tagOpenTagClose.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatTags() {
		// get model
		IStructuredModel structuredModel = getModel("tags.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "tags.afterDefaultFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatEmpty() {
		// get model
		IStructuredModel structuredModel = getModel("empty.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "empty.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatOneSpace() {
		// get model
		IStructuredModel structuredModel = getModel("oneSpace.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "empty.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatOneChar() {
		// get model
		IStructuredModel structuredModel = getModel("oneChar.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "oneChar.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatSpaces() {
		// get model
		IStructuredModel structuredModel = getModel("spaces.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "empty.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatChars() {
		// get model
		IStructuredModel structuredModel = getModel("chars.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "chars.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatSpacesAndChars() {
		// get model
		IStructuredModel structuredModel = getModel("spacesAndChars.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "chars.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormat() {
		// get model
		IStructuredModel structuredModel = getModel("small.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "small.afterDefaultFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatSplitLinesSplitMultiAttrs() {
		// get model
		IStructuredModel structuredModel = getModel("small.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(true);
		formatPreferences.setLineWidth(72);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "small.afterSplitLinesSplitMultiAttrsFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatSplitLines() {
		// get model
		IStructuredModel structuredModel = getModel("small.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(72);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "small.afterSplitLinesFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatBlockComments() {
		// get model
		IStructuredModel structuredModel = getModel("blockComments.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "blockComments.afterDefaultFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatInlineComments() {
		// get model
		IStructuredModel structuredModel = getModel("inlineComments.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(999);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "inlineComments.afterDefaultFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testFormatInlineCommentsSplitLinesSplitMultiAttrs() {
		// get model
		IStructuredModel structuredModel = getModel("inlineComments.xml");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(true);
		formatPreferences.setLineWidth(72);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = false;
		fFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "inlineComments.afterSplitLinesSplitMultiAttrsFormat.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testHTMLFormat() {
		// get model
		IStructuredModel structuredModel = getModel("HitCounterIntro.html");

		// init FormatPreferences
		IStructuredFormatPreferences formatPreferences = fHTMLFormatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(false);
		formatPreferences.setLineWidth(72);
		formatPreferences.setIndent("\t");
		formatPreferences.setClearAllBlankLines(false);

		// format
		((AbstractStructuredFormatProcessor) fHTMLFormatProcessor).refreshFormatPreferences = false;
		fHTMLFormatProcessor.formatModel(structuredModel);
		((AbstractStructuredFormatProcessor) fHTMLFormatProcessor).refreshFormatPreferences = true;

		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "HitCounterIntro.afterDefaultFormat.html";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, formatted);

		// release model
		structuredModel.releaseFromRead();
	}

	protected String readFile(String fileName) {
		String inputString = null;
		InputStream fileInputStream = null;

		try {
			fileInputStream = getClass().getResourceAsStream(fileName);

			byte[] inputBuffer = new byte[1024];
			inputString = new String();
			int bytesRead = -1;

			while (true) {
				bytesRead = fileInputStream.read(inputBuffer);
				if (bytesRead == -1)
					break;
				String bufferString = new String(inputBuffer);
				bufferString = bufferString.substring(0, bytesRead);
				inputString = inputString.concat(bufferString);
			}

			if (fileInputStream != null)
				fileInputStream.close();
		}
		catch (IOException exception) {
			StringWriter s = new StringWriter();
			exception.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}

		return inputString;
	}

	protected static void printException(Exception exception) {
		exception.printStackTrace();
	}

	protected IStructuredModel getModel(String fileName) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;

		try {
			String input = getFile(fileName);
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8"));
			String id = inputStream.toString().concat(fileName);
			structuredModel = fModelManager.getModelForRead(id, inputStream, null);
		}
		catch (Exception exception) {
			StringWriter s = new StringWriter();
			exception.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		finally {
			try {
				inputStream.close();
			}
			catch (Exception exception) {
				// hopeless
				StringWriter s = new StringWriter();
				exception.printStackTrace(new PrintWriter(s));
				fail(s.toString());
			}
		}

		return structuredModel;
	}

	protected String getFile(String fileName) {
		return readFile("testfiles/".concat(fileName));
	}

	protected void compare(String testcaseName, String expected, String formatted) {
		if (formatted.compareTo(expected) != 0) {
			if (PRINT_FAILED_FORMAT_TESTS) {
				System.out.println();
				System.out.println(testcaseName + " failed");
				System.out.println("========== expected file ==========");
				System.out.println(expected);
				System.out.println("========== formatted file ==========");
				System.out.println(formatted);
				System.out.println("========== expected file ==========");
				String expectedString = StringUtils.replace(expected, "\r", "\\r");
				expectedString = StringUtils.replace(expectedString, "\n", "\\n");
				expectedString = StringUtils.replace(expectedString, "\t", "\\t");
				System.out.println(expectedString);
				System.out.println("========== formatted file ==========");
				String formattedString = StringUtils.replace(formatted, "\r", "\\r");
				formattedString = StringUtils.replace(formattedString, "\n", "\\n");
				formattedString = StringUtils.replace(formattedString, "\t", "\\t");
				System.out.println(formattedString);
				System.out.println("=======================================");
			}
			assertEquals(expected, formatted);
		}
	}
}