/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
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
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.tests.util.StringCompareUtil;
import org.eclipse.wst.html.core.internal.cleanup.HTMLCleanupProcessorImpl;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.cleanup.CleanupProcessorXML;

public class CleanupTester extends TestCase {
//	private static final boolean PRINT_FAILED_FORMAT_TESTS = false;
	protected IModelManager fModelManager = null;
	protected CleanupProcessorXML fCleanupProcessor = null;
	protected HTMLCleanupProcessorImpl fHTMLCleanupProcessor = null;
	private StringCompareUtil fStringCompareUtil;

	public CleanupTester(String name) {
		super(name);
		// get model manager
		fModelManager = StructuredModelManager.getModelManager();

		// get cleanup processor
		fCleanupProcessor = new CleanupProcessorXML();

		// get HTML cleanup processor
		fHTMLCleanupProcessor = new HTMLCleanupProcessorImpl();
	}

	protected void setUp() throws Exception {
		fStringCompareUtil = new StringCompareUtil();
	}

	public void testCleanupInsertTagsQuoteAttrs() throws UnsupportedEncodingException, IOException {
		// get model
		IStructuredModel structuredModel = getModel("invoice.xml");

		// use for debugging
		// String precleaned = structuredModel.getStructuredDocument().get();

		// init CleanupPreferences
		IStructuredCleanupPreferences cleanupPreferences = fCleanupProcessor.getCleanupPreferences();
		cleanupPreferences.setCompressEmptyElementTags(false);
		cleanupPreferences.setInsertRequiredAttrs(false);
		cleanupPreferences.setInsertMissingTags(true);
		cleanupPreferences.setQuoteAttrValues(true);
		cleanupPreferences.setFormatSource(false);
		cleanupPreferences.setConvertEOLCodes(false);

		// cleanup
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = false;
		fCleanupProcessor.cleanupModel(structuredModel);
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = true;

		// compare
		String cleaned = structuredModel.getStructuredDocument().get();
		String expectedFileName = "invoice.afterCleanupInsertTagsQuoteAttrs.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, cleaned);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testCleanupInsertTags() throws IOException, IOException {
		// get model
		IStructuredModel structuredModel = getModel("invoice.xml");

		// init CleanupPreferences
		IStructuredCleanupPreferences cleanupPreferences = fCleanupProcessor.getCleanupPreferences();
		cleanupPreferences.setCompressEmptyElementTags(false);
		cleanupPreferences.setInsertRequiredAttrs(false);
		cleanupPreferences.setInsertMissingTags(true);
		cleanupPreferences.setQuoteAttrValues(false);
		cleanupPreferences.setFormatSource(false);
		cleanupPreferences.setConvertEOLCodes(false);

		// cleanup
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = false;
		fCleanupProcessor.cleanupModel(structuredModel);
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = true;

		// compare
		String cleaned = structuredModel.getStructuredDocument().get();
		String expectedFileName = "invoice.afterCleanupInsertTags.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, cleaned);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testCleanupCompressEmptyElementTags() throws UnsupportedEncodingException, IOException {
		// get model
		IStructuredModel structuredModel = getModel("small2.xml");

		// init CleanupPreferences
		IStructuredCleanupPreferences cleanupPreferences = fCleanupProcessor.getCleanupPreferences();
		cleanupPreferences.setCompressEmptyElementTags(true);
		cleanupPreferences.setInsertRequiredAttrs(false);
		cleanupPreferences.setInsertMissingTags(false);
		cleanupPreferences.setQuoteAttrValues(false);
		cleanupPreferences.setFormatSource(true);
		cleanupPreferences.setConvertEOLCodes(false);

		// cleanup
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = false;
		fCleanupProcessor.cleanupModel(structuredModel);
		((AbstractStructuredCleanupProcessor) fCleanupProcessor).refreshCleanupPreferences = true;

		// compare
		String cleaned = structuredModel.getStructuredDocument().get();
		String expectedFileName = "small2.afterCompressEmptyElementTags-newfmt.xml";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, cleaned);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testCleanupHTML() throws UnsupportedEncodingException, IOException {
		// get model
		IStructuredModel structuredModel = getModel("cleanup.html");

		// init CleanupPreferences
		IStructuredCleanupPreferences cleanupPreferences = fHTMLCleanupProcessor.getCleanupPreferences();
		cleanupPreferences.setTagNameCase(HTMLCorePreferenceNames.UPPER);
		cleanupPreferences.setAttrNameCase(HTMLCorePreferenceNames.LOWER);
		cleanupPreferences.setCompressEmptyElementTags(false);
		cleanupPreferences.setInsertRequiredAttrs(false);
		cleanupPreferences.setInsertMissingTags(true);
		cleanupPreferences.setQuoteAttrValues(true);
		cleanupPreferences.setFormatSource(false);
		cleanupPreferences.setConvertEOLCodes(false);

		// cleanup
		((AbstractStructuredCleanupProcessor) fHTMLCleanupProcessor).refreshCleanupPreferences = false;
		fHTMLCleanupProcessor.cleanupModel(structuredModel);
		((AbstractStructuredCleanupProcessor) fHTMLCleanupProcessor).refreshCleanupPreferences = true;

		// compare
		String cleaned = structuredModel.getStructuredDocument().get();
		String expectedFileName = "cleanup.afterCleanup.html";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, cleaned);

		// release model
		structuredModel.releaseFromRead();
	}

	public void testCleanupHTMLtwice() throws UnsupportedEncodingException, IOException {
		// get model
		IStructuredModel structuredModel = getModel("cleanup.afterCleanup.html");

		// init CleanupPreferences
		IStructuredCleanupPreferences cleanupPreferences = fHTMLCleanupProcessor.getCleanupPreferences();
		cleanupPreferences.setTagNameCase(HTMLCorePreferenceNames.UPPER);
		cleanupPreferences.setAttrNameCase(HTMLCorePreferenceNames.LOWER);
		cleanupPreferences.setCompressEmptyElementTags(false);
		cleanupPreferences.setInsertRequiredAttrs(false);
		cleanupPreferences.setInsertMissingTags(true);
		cleanupPreferences.setQuoteAttrValues(true);
		cleanupPreferences.setFormatSource(false);
		cleanupPreferences.setConvertEOLCodes(false);

		// cleanup
		((AbstractStructuredCleanupProcessor) fHTMLCleanupProcessor).refreshCleanupPreferences = false;
		fHTMLCleanupProcessor.cleanupModel(structuredModel);
		((AbstractStructuredCleanupProcessor) fHTMLCleanupProcessor).refreshCleanupPreferences = true;

		// compare
		String cleaned = structuredModel.getStructuredDocument().get();
		String expectedFileName = "cleanup.afterCleanup.html";
		String expected = getFile(expectedFileName);
		compare(expectedFileName, expected, cleaned);

		// release model
		structuredModel.releaseFromRead();
	}

	 public void testCleanupJSPEmptyTags1() throws
		 UnsupportedEncodingException, IOException {
		 // get model
		 IStructuredModel structuredModel = getModel("login.jsp");
		
		 // init CleanupPreferences
		 IStructuredCleanupPreferences cleanupPreferences =
		 fHTMLCleanupProcessor.getCleanupPreferences();
		 cleanupPreferences.setTagNameCase(CommonModelPreferenceNames.UPPER);
		 cleanupPreferences.setAttrNameCase(CommonModelPreferenceNames.LOWER);
		 cleanupPreferences.setCompressEmptyElementTags(false);
		 cleanupPreferences.setInsertRequiredAttrs(false);
		 cleanupPreferences.setInsertMissingTags(true);
		 cleanupPreferences.setQuoteAttrValues(true);
		 cleanupPreferences.setFormatSource(true);
		 cleanupPreferences.setConvertEOLCodes(false);
		
		 // cleanup
		 ((AbstractStructuredCleanupProcessor)
		 fHTMLCleanupProcessor).refreshCleanupPreferences = false;
		 fHTMLCleanupProcessor.cleanupModel(structuredModel);
		 ((AbstractStructuredCleanupProcessor)
		 fHTMLCleanupProcessor).refreshCleanupPreferences = true;
		
		 // compare
		 String cleaned = structuredModel.getStructuredDocument().get();
		 String expectedFileName = "login.afterCleanup.jsp";
		 String expected = getFile(expectedFileName);
		 compare(expectedFileName, expected, cleaned);
		
		 // release model
		 structuredModel.releaseFromRead();
	 }
	
	 public void testCleanupJSPEmptyTags2() throws
		 UnsupportedEncodingException, IOException {
		 // get model
		 IStructuredModel structuredModel = getModel("subscription.jsp");
		
		 // init CleanupPreferences
		 IStructuredCleanupPreferences cleanupPreferences =
		 fHTMLCleanupProcessor.getCleanupPreferences();
		 cleanupPreferences.setTagNameCase(CommonModelPreferenceNames.UPPER);
		 cleanupPreferences.setAttrNameCase(CommonModelPreferenceNames.LOWER);
		 cleanupPreferences.setCompressEmptyElementTags(false);
		 cleanupPreferences.setInsertRequiredAttrs(false);
		 cleanupPreferences.setInsertMissingTags(true);
		 cleanupPreferences.setQuoteAttrValues(true);
		 cleanupPreferences.setFormatSource(true);
		 cleanupPreferences.setConvertEOLCodes(false);
		
		 // cleanup
		 ((AbstractStructuredCleanupProcessor)
		 fHTMLCleanupProcessor).refreshCleanupPreferences = false;
		 fHTMLCleanupProcessor.cleanupModel(structuredModel);
		 ((AbstractStructuredCleanupProcessor)
		 fHTMLCleanupProcessor).refreshCleanupPreferences = true;
		
		 // compare
		 String cleaned = structuredModel.getStructuredDocument().get();
		 String expectedFileName = "subscription.afterCleanup.jsp";
		 String expected = getFile(expectedFileName);
		 compare(expectedFileName, expected, cleaned);
		
		 // release model
		 structuredModel.releaseFromRead();
	 }

	protected String readFile(String fileName) throws IOException {
		String inputString = null;
		InputStream fileInputStream = null;

		try {
			fileInputStream = getClass().getResourceAsStream(fileName);

			byte[] inputBuffer = new byte[2048];
			inputString = new String();
			int bytesRead = -1;

			while (true) {
				bytesRead = fileInputStream.read(inputBuffer);
				if (bytesRead == -1)
					break;
				String bufferString = new String(inputBuffer, 0, bytesRead);
				// bufferString = bufferString.substring(0, bytesRead);
				inputString = inputString.concat(bufferString);
			}
		}
		finally {
			if (fileInputStream != null)
				fileInputStream.close();
		}

		return inputString;
	}

	protected static void printException(Exception exception) {
		exception.printStackTrace();
	}

	protected IStructuredModel getModel(String fileName) throws UnsupportedEncodingException, IOException {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;

		try {
			String input = getFile(fileName);
			inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
			String id = inputStream.toString().concat(fileName);
			structuredModel = fModelManager.getModelForRead(id, inputStream, null);
		}
		// since in test code, no need to catch this
		// catch (Exception exception) {
		// Logger.logException(exception);
		// }
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

	protected String getFile(String fileName) throws IOException {
		return readFile("testfiles/".concat(fileName));
	}

	protected void compare(String testcaseName, String expected, String cleaned) {
		assertTrue("Cleaned up document differs from the expected.\nExpected Contents:\n" + expected + "\nActual Contents:\n" + cleaned, fStringCompareUtil.equalsIgnoreLineSeperator(expected, cleaned));

//		String expected2 = StringUtils.replace(expected, "\r\n", "\r");
//		expected2 = StringUtils.replace(expected2, "\r", "\n");
//		String cleaned2 = StringUtils.replace(cleaned, "\r\n", "\r");
//		cleaned2 = StringUtils.replace(cleaned2, "\r", "\n");
//		if (!fStringCompareUtil.equalsIgnoreLineSeperator(expected, cleaned))
//			assertEquals("Cleaned up document differs from the expected.", expected, cleaned);
//		else
//			assertEquals("Cleaned up document differs from the expected.", expected2, cleaned2);
//		if (cleaned.compareTo(expected) != 0) {
//			if (PRINT_FAILED_FORMAT_TESTS) {
//				System.out.println();
//				System.out.println(testcaseName + " failed");
//				System.out.println("========== expected file ==========");
//				System.out.println(expected);
//				System.out.println("========== cleaned file ==========");
//				System.out.println(cleaned);
//				System.out.println("========== expected file ==========");
//				String expectedString = StringUtils.replace(expected, "\r", "\\r");
//				expectedString = StringUtils.replace(expectedString, "\n", "\\n");
//				expectedString = StringUtils.replace(expectedString, "\t", "\\t");
//				System.out.println(expectedString);
//				System.out.println("========== cleaned file ==========");
//				String cleanedString = StringUtils.replace(cleaned, "\r", "\\r");
//				cleanedString = StringUtils.replace(cleanedString, "\n", "\\n");
//				cleanedString = StringUtils.replace(cleanedString, "\t", "\\t");
//				System.out.println(cleanedString);
//				System.out.println("=======================================");
//			}
//			assertTrue(false);
//		}
	}
}
