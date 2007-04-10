/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.encoding;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnmappableCharacterException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.encoding.util.Assert;
import org.eclipse.wst.sse.core.internal.encoding.util.Logger;
import org.eclipse.wst.sse.core.internal.exceptions.CharConversionErrorWithDetail;
import org.eclipse.wst.sse.core.internal.exceptions.MalformedOutputExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;


public class CodedStreamCreator extends CodedIO {

	private final static int INITIAL_BUFFER_SIZE = 1024 * 16;

	// the 32 bytes used by default by ByteOutputStream is
	// a little small
	private static final String PROGRAM_ERROR__FAILED_TO_FIND_ANY_CHARSET_ANYWHERE_ = "Program error: failed to find any charset anywhere!"; //$NON-NLS-1$

	private static final String UTF_16BE_CHARSET_NAME = "UTF-16BE"; //$NON-NLS-1$
	private static final String UTF_16LE_CHARSET_NAME = "UTF-16LE"; //$NON-NLS-1$
	//	private static final String UTF_16_CHARSET_NAME = "UTF-16";
	// //$NON-NLS-1$

	private static final String UTF_8_CHARSET_NAME = "UTF-8"; //$NON-NLS-1$

	private boolean fClientSuppliedReader;

	// future_TODO: this 'checkConversion' can be a little
	// pricey for large
	// files, chould be a user preference, or something.
	// private static final boolean checkConversion = true;
	private EncodingMemento fCurrentEncodingMemento;

	private EncodingMemento fEncodingMemento;

	private String fFilename;

	private boolean fHasBeenAnalyzed;

	private IFile fIFile;

	private EncodingMemento fPreviousEncodingMemento;

	private Reader fReader;

	private Reader fResettableReader;
	private byte[] UTF16BEBOM = new byte[]{(byte) 0xFE, (byte) 0xFF};

	private byte[] UTF16LEBOM = new byte[]{(byte) 0xFF, (byte) 0xFE};
	private byte[] UTF3BYTEBOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

	public CodedStreamCreator() {
		super();
	}

	public CodedStreamCreator(String filename, char[] characterArray) {
		super();
		fFilename = filename;
		fReader = new CharArrayReader(characterArray);
	}

	public CodedStreamCreator(String filename, Reader reader) {
		super();
		fFilename = filename;
		fReader = reader;
	}

	public CodedStreamCreator(String filename, String textString) {
		super();
		fFilename = filename;
		fReader = new StringReader(textString);
	}

	/**
	 * The primary method which contains the highest level rules for how to
	 * decide appropriate decoding rules: 1. first check for unicode stream 2.
	 * then looked for encoding specified in content (according to the type of
	 * content that is it ... xml, html, jsp, etc. 3. then check for various
	 * settings: file settings first, if null check project settings, if null,
	 * check user preferences. 4. lastly (or, what is the last user
	 * preference) is to use "workbench defaults".
	 */
	private void analyze() throws CoreException, IOException {
		Reader resettableReader = getResettableReader();
		try {
			if (fCurrentEncodingMemento == null) {
				resettableReader.reset();
				fCurrentEncodingMemento = checkForEncodingInContents();
			}
			// if encoding stratagy doesn't provide answer,
			// then try file settings, project settings,
			// user preferences, and
			// finally workbench default.
			//
			if (fCurrentEncodingMemento == null || fCurrentEncodingMemento.getDetectedCharsetName() == null) {
				resettableReader.reset();
				fCurrentEncodingMemento = getEncodingMementoFromResourceAndPreference();
			}

			// use DefaultNameRules from NonContentBasedEncodingRules as the
			// final default
			if (fEncodingMemento == null) {
				handleNotProvidedFromContentCase();
			}

			fHasBeenAnalyzed = true;
		} finally {
			if (resettableReader != null) {
				resettableReader.reset();
			}
		}
	}

	/**
	 * Need to check conversion early on. There's some danger than old
	 * contents of a file are set to empty, if an exception occurs.
	 * 
	 * @param allText
	 * @param encoding
	 * @param encodingRule
	 * @throws java.io.UnsupportedEncodingException
	 * @throws MalformedOutputExceptionWithDetail
	 * @deprecated - we need to find "cheaper" way to to this functionality so
	 *             likely to go away in future
	 */
	private void checkConversion(EncodingMemento memento, EncodingRule encodingRule) throws IOException {
		String javaEncoding = memento.getJavaCharsetName();
		String detectedEncoding = memento.getDetectedCharsetName();
		Charset charset = Charset.forName(javaEncoding);
		CharsetEncoder charsetEncoder = charset.newEncoder();
		charsetEncoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		Reader reader = getResettableReader();
		reader.reset();
		int currentChar = reader.read();
		int currentPos = 1;
		try {
			while (currentChar != -1) {
				// note: this can probably be made more
				// efficient later to
				// check buffer by buffer, instead of
				// character by character.
				try {
					boolean canConvert = charsetEncoder.canEncode((char) currentChar);
					if (!canConvert) {
						if (encodingRule == EncodingRule.IGNORE_CONVERSION_ERROR) {
							// if we're told to ignore the
							// encoding conversion
							// error,
							// notice we still want to detect
							// and log it. We simply
							// don't throw the exception, and
							// we do continue with
							// the
							// save.
							Logger.log(Logger.ERROR, "Encoding Conversion Error during save"); //$NON-NLS-1$
						} else {
							throw new MalformedOutputExceptionWithDetail(javaEncoding, detectedEncoding, currentPos);
						}
					}
					currentChar = reader.read();
					currentPos++;
				}
				// IBM's JRE seems to throw NPE when DBCS char is given to
				// SBCS charsetEncoder
				catch (NullPointerException e) {
					throw new CharConversionErrorWithDetail(javaEncoding); //$NON-NLS-1$
				}
			}
			// if we get all the way through loop without throwing exception,
			// then there must
			// be an error not detectable when going character by character.
			throw new CharConversionErrorWithDetail(javaEncoding); //$NON-NLS-1$
		} finally {
			reader.reset();
		}
	}

	private EncodingMemento checkForEncodingInContents() throws CoreException, IOException {
		EncodingMemento result = null;

		// if encoding memento already set, and no need to get again.
		if (fEncodingMemento != null) {
			result = fEncodingMemento;
		} else {
			if (fClientSuppliedReader) {
				fReader.reset();
				IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
				try {
					IContentDescription contentDescription = contentTypeManager.getDescriptionFor(fReader, fFilename, IContentDescription.ALL);
					if (contentDescription != null) {
						fEncodingMemento = createMemento(contentDescription);
					} else {
						fEncodingMemento = CodedIO.createEncodingMemento("UTF-8"); //$NON-NLS-1$
					}
				} catch (NullPointerException e) {
					// TODO: work around for 5/14 bug in base, should be
					// removed when move up to 5/21
					// just created a simple default one
					fEncodingMemento = CodedIO.createEncodingMemento("UTF-8"); //$NON-NLS-1$
				}
				result = fEncodingMemento;
			} else {
				throw new IllegalStateException("unexpected state: encodingMemento was null but no input stream supplied"); //$NON-NLS-1$
			}
		}
		//		try {
		//			result = getEncodingDetector().getEncodingMemento();
		//			if (result != null && !result.isValid() && !forceDefault()) {
		//				throw new UnsupportedCharsetExceptionWithDetail(result);
		//			}
		//		}
		//		finally {
		//			handleStreamClose(fEncodingDetectorStream);
		//		}
		return result;
	}


	private void dump(OutputStream outputStream, EncodingRule encodingRule, boolean use3ByteBOMifUTF8) throws CoreException, IOException {
		getCurrentEncodingMemento();
		String javaEncodingName = null;
		if (encodingRule == EncodingRule.CONTENT_BASED) {
			if (fCurrentEncodingMemento.isValid()) {
				javaEncodingName = fCurrentEncodingMemento.getJavaCharsetName();
			} else {
				throw new UnsupportedCharsetExceptionWithDetail(fCurrentEncodingMemento);
			}
		} else if (encodingRule == EncodingRule.IGNORE_CONVERSION_ERROR)
			javaEncodingName = fCurrentEncodingMemento.getJavaCharsetName();
		else if (encodingRule == EncodingRule.FORCE_DEFAULT)
			javaEncodingName = fCurrentEncodingMemento.getAppropriateDefault();
		// write appropriate "header" unicode BOM bytes
		// Note: Java seems to write appropriate header for
		// UTF-16, but not
		// UTF-8 nor UTF-16BE. This
		// may vary by JRE version, so need to test well.
		// Note: javaEncodingName can be null in invalid
		// cases, so we no hard
		// to skip whole check if that's the case.
		if (javaEncodingName != null) {
			if ((javaEncodingName.equals(UTF_8_CHARSET_NAME) && use3ByteBOMifUTF8) || (javaEncodingName.equals(UTF_8_CHARSET_NAME) && fCurrentEncodingMemento.isUTF83ByteBOMUsed())) {
				outputStream.write(UTF3BYTEBOM);
			} else if (javaEncodingName.equals(UTF_16LE_CHARSET_NAME)) {
				outputStream.write(UTF16LEBOM);
			} else if (javaEncodingName.equals(UTF_16BE_CHARSET_NAME)) {
				outputStream.write(UTF16BEBOM);
			}
		}
		// TODO add back in line delimiter handling the
		// "right" way (updating
		// markers, not requiring string, etc. .. may need
		// to move to document
		// level)
		//allTextBuffer =
		// handleLineDelimiter(allTextBuffer, document);
		Reader reader = getResettableReader();
		// be sure to test large "readers" ... we'll need
		// to make sure they all
		// can reset to initial position (StringReader,
		// CharArrayReader, and
		// DocumentReader should all work ok).
		reader.reset();
		// There must be cleaner logic somehow, but the
		// idea is that
		// javaEncodingName can be null
		// if original detected encoding is not valid (and
		// if FORCE_DEFAULT was
		// not specified). Hence, we WANT the first
		// Charset.forName to
		// throw appropriate exception.
		Charset charset = null;

		// this call checks "override" properties file
		javaEncodingName = CodedIO.getAppropriateJavaCharset(javaEncodingName);

		if (javaEncodingName == null) {
			charset = Charset.forName(fCurrentEncodingMemento.getDetectedCharsetName());
		} else {
			charset = Charset.forName(javaEncodingName);
		}
		CharsetEncoder charsetEncoder = charset.newEncoder();
		if (!(encodingRule == EncodingRule.IGNORE_CONVERSION_ERROR)) {
			charsetEncoder.onMalformedInput(CodingErrorAction.REPORT);
			charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		} else {
			charsetEncoder.onMalformedInput(CodingErrorAction.REPLACE);
			charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

		}
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, charsetEncoder);
		//TODO: this may no longer be needed (and is at
		// least wrong spot for
		// it).
		//		if (checkConversion && (!(encodingRule ==
		// EncodingRule.IGNORE_CONVERSION_ERROR))) {
		//			checkConversion(fCurrentEncodingMemento,
		// encodingRule);
		//		}
		char[] charbuf = new char[CodedIO.MAX_BUF_SIZE];
		int nRead = 0;
		try {
			while (nRead != -1) {
				nRead = reader.read(charbuf, 0, MAX_BUF_SIZE);
				if (nRead > 0) {
					outputStreamWriter.flush();
					outputStreamWriter.write(charbuf, 0, nRead);
				}
			}
		} catch (UnmappableCharacterException e) {
			checkConversion(fCurrentEncodingMemento, encodingRule);
		} finally {
			// since we don't own the original output stream, we
			// won't close it ours.
			// the caller who passed it to us must close original one
			// when appropriate.
			// (but we do flush to be sure all up-to-date)
			outputStreamWriter.flush();
		}
	}

	private boolean get3ByteBOMPreference() {
		return SSECorePlugin.getDefault().getPluginPreferences().getBoolean(CommonEncodingPreferenceNames.USE_3BYTE_BOM_WITH_UTF8);
	}

	public ByteArrayOutputStream getCodedByteArrayOutputStream() throws CoreException, IOException {
		return getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
	}

	public ByteArrayOutputStream getCodedByteArrayOutputStream(EncodingRule encodingRule) throws CoreException, IOException {
		//Assert.isNotNull(fPreviousEncodingMemento,
		// "previousEncodingMemento
		// needs to be set first");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE);
		dump(byteArrayOutputStream, encodingRule, get3ByteBOMPreference());
		return byteArrayOutputStream;
	}

	public EncodingMemento getCurrentEncodingMemento() throws CoreException, IOException {
		//Assert.isNotNull(fPreviousEncodingMemento,
		// "previousEncodingMemento
		// needs to be set first");
		if (!fHasBeenAnalyzed) {
			analyze();
		}
		// post condition
		Assert.isNotNull(fCurrentEncodingMemento, "illegal post condition state"); //$NON-NLS-1$
		// be sure to carry over appropriate encoding
		// "state" that may be
		// relevent.
		if (fPreviousEncodingMemento != null) {
			fCurrentEncodingMemento.setUTF83ByteBOMUsed(fPreviousEncodingMemento.isUTF83ByteBOMUsed());
		}
		return fCurrentEncodingMemento;
	}

	/*
	 * This method is called only when encoding is not detected in the file.
	 * 
	 * Here is encoding lookup order we will try: - try resource content
	 * description (Eclipse Text file encoding) - try resource content
	 * properties (for JSP only) - try content type encoding preferences (for
	 * HTML only) - try resource content description (Eclipse Text file
	 * encoding, implicit check)
	 * 
	 * Note: This method appears in both CodedReaderCreator and
	 * CodedStreamCreator (with just a minor difference). They should be kept
	 * the same.
	 */
	private EncodingMemento getEncodingMementoFromResourceAndPreference() throws IOException, CoreException {
		EncodingMemento encodingMemento = fEncodingMemento;

		// Follow Eclipse Platform's direction. Get the charset from IFile.
		if (fIFile != null) {
			String charset = fIFile.getCharset();
			encodingMemento = CodedIO.createEncodingMemento(charset);
		}

		return encodingMemento;
	}

	private Reader getResettableReader() {
		if (fResettableReader == null) {
			if (fReader.markSupported()) {
				fResettableReader = fReader;
			} else {
				fResettableReader = new BufferedReader(fReader);
				try {
					fResettableReader.mark(MAX_MARK_SIZE);
				} catch (IOException e) {
					// impossible, since we just checked if
					// markable
					throw new Error(e);
				}

			}
		}
		return fResettableReader;
	}

	protected void handleNotProvidedFromContentCase() {
		// move to "detectors" if not already
		String specDefault = null;
		//specDefault = getEncodingDetector().getSpecDefaultEncoding();
		String charset = NonContentBasedEncodingRules.useDefaultNameRules(specDefault);
		Assert.isNotNull(charset, PROGRAM_ERROR__FAILED_TO_FIND_ANY_CHARSET_ANYWHERE_);
		fCurrentEncodingMemento = CodedIO.createEncodingMemento(charset);
	}

	// TODO We just copy the content properties encoding to current resource's
	// encoding for now. May improve the UI later by setting an informational
	// message and/or disable the content properties encoding field.
	// TODO make priviate if needed, else remove
	void migrateContentPropertiesEncoding(String encoding) throws CoreException {
		if (fIFile != null)
			fIFile.setCharset(encoding, null);
		final IFile file = fIFile;
		final String charset = encoding;
		// TODO: externalize string later
		Job migrater = new Job(SSECoreMessages.Migrate_Charset) { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				if (file != null) {
					try {
						file.setCharset(charset, null);
					} catch (CoreException e) {
						Logger.logException(e);
					}
				}
				return Status.OK_STATUS;
			}
		};
		migrater.setSystem(true);
		migrater.schedule();

	}

	/**
	 *  
	 */
	private void resetAll() {
		fFilename = null;
		fReader = null;
		fPreviousEncodingMemento = null;
		fCurrentEncodingMemento = null;
		fHasBeenAnalyzed = false;
		fClientSuppliedReader = false;
	}

	public void set(IFile file, Reader reader) {
		fIFile = file;
		set(file.getName(), reader);
	}

	public void set(String filename, char[] characterArray) {
		resetAll();
		fFilename = filename;
		fReader = new CharArrayReader(characterArray);
	}

	public void set(String filename, Reader reader) {
		resetAll();
		fFilename = filename;
		fReader = reader;
		fClientSuppliedReader = true;
	}

	public void set(String filename, String textString) {
		set(filename, new StringReader(textString));
	}

	public void setPreviousEncodingMemento(EncodingMemento previousEncodingMemento) {
		fPreviousEncodingMemento = previousEncodingMemento;
	}
}
