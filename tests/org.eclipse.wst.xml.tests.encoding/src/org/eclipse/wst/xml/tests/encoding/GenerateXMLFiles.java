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
package org.eclipse.wst.xml.tests.encoding;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentDescription;

/**
 * The purpose and logic of this class is to create small "XML files" of
 * various, known encodings, write them to files, and in later tests, be sure
 * appropriate encoding can be detected, and read in and intact characters.
 */
public class GenerateXMLFiles extends GenerateFiles {
	private String LF = "\n";
	private String CR = "\r";
	private String CRLF = CR + LF;
	// different text strings for comparisons
	private String textUS_ASCII_LF = "abcdefghijklmnopqrstuvwxyz\n1234567890\nABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String textUS_ASCII_CRLF = "abcdefghijklmnopqrstuvwxyz\r\n1234567890\r\nABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private boolean DEBUG = true;
	private boolean DEBUGCRLF = false;
	private boolean DEBUGINFO = true;


	public GenerateXMLFiles() {
		super();
	}

	public static void main(String[] args) {
		//junit.textui.TestRunner.run(GenerateXMLFiles.class);
		GenerateXMLFiles thisApp = new GenerateXMLFiles();
		try {
			//thisApp.generateOriginalFiles();
			thisApp.generateAllFilesForCurrentVM();
		}
		catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void generateAllFilesForCurrentVM() throws IOException {
		Map allCharsetMap = Charset.availableCharsets();
		Set allKeys = allCharsetMap.keySet();
		Object[] allcharsets = allKeys.toArray();
		String[] allcharsetNames = new String[allcharsets.length];
		for (int i = 0; i < allcharsets.length; i++) {
			allcharsetNames[i] = allcharsets[i].toString();

		}
		//createFiles(allcharsetNames, false);
		createFiles(allcharsetNames, true);

	}

	private void createFiles(String[] charsetnames, boolean useCRLF) throws FileNotFoundException, IOException {

		String charsetName = null;
		Writer out = null;
		String mainDirectory = getMainDirectoryBasedOnVMNameAndFileExtension();
		List charsetFilesWritten = new ArrayList();
		for (int i = 0; i < charsetnames.length; i++) {
			try {


				charsetName = charsetnames[i];

				Charset charset = Charset.forName(charsetName);
				CharsetEncoder charsetEncoder = charset.newEncoder();
				charsetEncoder.onMalformedInput(CodingErrorAction.REPORT);
				charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);

				String header = getHeaderStart() + charsetName + getHeaderEnd();
				String fulltext = null;
				if (useCRLF) {
					fulltext = header + textUS_ASCII_CRLF;
				}
				else {
					fulltext = header + textUS_ASCII_LF;
				}

				if (!isEbcidic(charsetName, charsetEncoder)) {
					if (charsetEncoder.canEncode(fulltext)) {
						//						if (canEncodeCRLF(charsetName, charsetEncoder)
						// &&
						// canEncodeSimpleString(charsetName,
						// charsetEncoder, "<?") &&
						// charsetEncoder.canEncode(fulltext)) {
						String outputfilename = "test-" + charsetName + ".xml";
						File outFile = FileUtil.makeFileFor(mainDirectory, outputfilename, null);
						//System.out.println(outFile.getAbsolutePath());
						OutputStream outputStream = new FileOutputStream(outFile);
						ByteArrayOutputStream bytesout = new ByteArrayOutputStream();

						Writer fileWriter = new OutputStreamWriter(outputStream, charsetEncoder);
						// this byte writer is created just to be able to
						// count precise bytes.
						Writer byteWriter = new OutputStreamWriter(bytesout, charsetEncoder);

						supplyBOMs(charsetName, outputStream, bytesout);

						out = new BufferedWriter(fileWriter);


						out.write(fulltext);
						byteWriter.write(fulltext);
						out.close();
						byteWriter.flush();
						// if we made is this far, with no exceptions,
						// etc.,
						// then
						// must have been
						// really written.
						String writtenRecord = charsetName;
						charsetFilesWritten.add(writtenRecord);
						if (DEBUG) {
							printDebugInfo(useCRLF, header, outputfilename, bytesout);
						}
					}
					else {
						if (DEBUGINFO) {
							System.out.println(" *** could not convert sample ascii text for " + charsetName);
						}
					}
				}
			}

			catch (IOException e) {
				if (DEBUGINFO) {
					System.out.println(" ***** could not generate for " + charsetName);
					String msg = e.getMessage();
					if (msg == null)
						msg = "";
					System.out.println("          due to " + e.getClass().getName() + "  " + msg);
				}
			}
			catch (Exception e) {
				if (DEBUGINFO) {
					System.out.println(" ***** could not generate for " + charsetName);
					String msg = e.getMessage();
					if (msg == null)
						msg = "";
					System.out.println("          due to " + e.getClass().getName() + "  " + msg);
				}
			}
			finally {
				if (out != null) {
					out.close();
				}
			}
		}


		// now create file that summarizes what was written
		// suitable to paste as method in test class
		File outFile = FileUtil.makeFileFor(mainDirectory, "testMethods.text", null);
		FileWriter outproperties = new FileWriter(outFile);
		outproperties.write(charsetFilesWritten.size() + CRLF);
		Iterator items = charsetFilesWritten.iterator();
		int n = 0;
		while (items.hasNext()) {
			String itemCreated = (String) items.next();
			String testMethod = createMethod(n++, itemCreated);
			outproperties.write(testMethod + CRLF);
		}
		outproperties.close();

	}

	/**
	 * I thought this used to be automatic, but doesn't seem to be now?!
	 */
	private void supplyBOMs(String charsetName, OutputStream outputStream, ByteArrayOutputStream bytesout) throws IOException {
		byte[] nullBytes = new byte[]{0x00, 0x00};
		if (charsetName.equals("UTF-16")) {
			outputStream.write(IContentDescription.BOM_UTF_16LE);
			bytesout.write(IContentDescription.BOM_UTF_16LE);
		}
		if (charsetName.equals("UTF-16LE")) {
			outputStream.write(IContentDescription.BOM_UTF_16LE);
			bytesout.write(IContentDescription.BOM_UTF_16LE);
		}
		if (charsetName.equals("X-UnicodeLittle")) {
			outputStream.write(IContentDescription.BOM_UTF_16LE);
			bytesout.write(IContentDescription.BOM_UTF_16LE);
		}
		if (charsetName.equals("UTF-16BE")) {
			outputStream.write(IContentDescription.BOM_UTF_16BE);
			bytesout.write(IContentDescription.BOM_UTF_16BE);
		}
		if (charsetName.equals("X-UnicodeBig")) {
			outputStream.write(IContentDescription.BOM_UTF_16BE);
			bytesout.write(IContentDescription.BOM_UTF_16BE);
		}
		if (charsetName.equals("UTF-32")) {
			outputStream.write(nullBytes);
			outputStream.write(IContentDescription.BOM_UTF_16LE);
			bytesout.write(nullBytes);
			bytesout.write(IContentDescription.BOM_UTF_16LE);
		}
		if (charsetName.equals("UTF-32LE")) {
			outputStream.write(nullBytes);
			outputStream.write(IContentDescription.BOM_UTF_16LE);
			bytesout.write(nullBytes);
			bytesout.write(IContentDescription.BOM_UTF_16LE);
		}
		if (charsetName.equals("UTF-32BE")) {
			outputStream.write(nullBytes);
			outputStream.write(IContentDescription.BOM_UTF_16BE);
			bytesout.write(nullBytes);
			bytesout.write(IContentDescription.BOM_UTF_16BE);
		}
	}

	/**
	 * @param i
	 * @param itemCreated
	 */
	private String createMethod(int i, String itemCreated) {
		String template = "	public void testFile" + i + "() throws CoreException, IOException  {\r\n" + "		String charsetName = \"" + itemCreated + "\";\r\n" + "		doGenTest(charsetName);\r\n" + "	}";
		return template;
	}

	private void printDebugInfo(boolean useCRLF, String header, String outputfilename, ByteArrayOutputStream bytesout) {
		byte[] bytes = bytesout.toByteArray();
		int nBytes = bytes.length;
		int nChars = 0;
		if (useCRLF) {
			nChars = header.length() + textUS_ASCII_CRLF.length();
		}
		else {
			nChars = header.length() + textUS_ASCII_LF.length();
		}

		System.out.println("Wrote " + nChars + " characters and " + nBytes + " bytes to " + outputfilename);
	}

	// TODO: never used
	 boolean canEncodeSimpleString(String charsetName, CharsetEncoder charsetEncocder, String simpleString) {
		// this method added since some encoders don't report that they can't
		// encode something, but they obviously
		// can't, at least in the normal meaning of the word.
		// This seems to mostly apply to some IBM varieties where, apparently,
		// the input can't be interpreted at all without knowing encoding
		// (that is
		// could not be used for content based encoding).
		boolean result = false;

		String newAsciiString = null;
		byte[] translatedBytes = null;
		try {
			translatedBytes = simpleString.getBytes(charsetName);
			newAsciiString = new String(translatedBytes, "ascii");
		}
		catch (UnsupportedEncodingException e) {
			// impossible, since checked already
			throw new Error(e);
		}
		result = simpleString.equals(newAsciiString);
		if (!result) {
			if (charsetEncocder.maxBytesPerChar() != 1) {
				// don't check mulitbyte encoders, just assume true (for now).
				result = true;
				if (charsetEncocder.maxBytesPerChar() == 4) {
					//except, let's just exclude four byte streams, for now.
					result = false;
					if (charsetEncocder.averageBytesPerChar() == 2) {
						// except, for some reason UTF has max bytes of 4
						// (average bytes of 2).
						result = false;
					}
				}
			}
		}

		return result;
	}

	/**
	 * A very heuristic method. Should have table, someday.
	 */
	private boolean isEbcidic(String charsetName, CharsetEncoder charsetEncocder) {
		boolean result = false;
		String simpleString = "<?";
		String newAsciiString = null;
		byte[] translatedBytes = null;
		try {
			translatedBytes = simpleString.getBytes(charsetName);
			newAsciiString = new String(translatedBytes, "ascii");
		}
		catch (UnsupportedEncodingException e) {
			// impossible, since checked already
			throw new Error(e);
		}
		// experimenting/debugging showed the known ebcidic onces always
		// "mis" tranlated to characters L and o.
		result = "Lo".equals(newAsciiString);
		if (result) {
			System.out.println(charsetName + " assumed to be Edcidic");
		}
		return result;
	}

	/**
	 * @param charset
	 */
	 boolean canEncodeCRLF(String charsetName, CharsetEncoder charsetEncoder) {
		boolean result = true;
		//String charsetCononicalName = charsetEncoder.charset().name();
		if (!charsetEncoder.canEncode(LF)) {
			if (DEBUGCRLF) {
				String stringName = "LF";
				String testString = LF;
				exploreConversion(charsetName, stringName, testString);
				System.out.println("can not encode LF for " + charsetEncoder.charset().name());
			}
			result = false;
		}
		if (!charsetEncoder.canEncode(CR)) {
			if (DEBUGCRLF) {
				String stringName = "CR";
				String testString = CR;
				exploreConversion(charsetName, stringName, testString);
				System.out.println("can not encode CR for " + charsetEncoder.charset().name());
			}
			result = false;
		}
		if (!charsetEncoder.canEncode(CRLF)) {
			if (DEBUGCRLF) {
				String stringName = "CRLF";
				String testString = CRLF;
				exploreConversion(charsetName, stringName, testString);
				System.out.println("can not encode CRLF for " + charsetEncoder.charset().name());
			}
			result = false;
		}
		return result;

	}

	private void exploreConversion(String charsetName, String stringName, String testString) throws Error {
		try {
			String newLF = new String(testString.getBytes(charsetName));
			System.out.print("old " + stringName + " (dec): ");
			dumpString(System.out, testString);
			System.out.println();
			System.out.print("new " + stringName + " (dec): ");
			dumpString(System.out, newLF);
			System.out.println();
		}
		catch (UnsupportedEncodingException e) {
			//should never happen, already checked
			throw new Error(e);
		}
	}

	/**
	 * @param out
	 * @param lf2
	 */
	private void dumpString(PrintStream out, String lf2) {
		for (int i = 0; i < lf2.length(); i++) {
			out.print((int) lf2.charAt(i) + " ");
		}

	}

	public final static String getMainDirectoryBasedOnVMNameAndFileExtension() {
		String mainDirectory = getMainDirectoryBasedOnVMName() + "/xml";
		return mainDirectory;
	}

	private String getHeaderStart() {
		return "<?xml version=\"1.0\" encoding=\"";
	}

	private String getHeaderEnd() {
		return "\"?>";
	}

}
