/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

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
import org.eclipse.wst.sse.core.internal.encoding.util.Assert;
import org.eclipse.wst.sse.core.internal.encoding.util.BufferedLimitedStream;
import org.eclipse.wst.sse.core.internal.encoding.util.Logger;
import org.eclipse.wst.sse.core.internal.encoding.util.NullInputStream;
import org.eclipse.wst.sse.core.internal.encoding.util.UnicodeBOMEncodingDetector;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;


/**
 * The purpose of this class is to centralize analysis of a file to determine
 * the most appropriate rules of decoding it. The intended use is to set the
 * input, then get the reader for that input which will have its encoding set
 * appropriately. Additionally, there is an EncodingMemento provided, which
 * will be required, in some cases, to later determine the most appropriate
 * form of encoded output.
 */
public class CodedReaderCreator extends CodedIO {


	private boolean fClientSuppliedStream;


	private EncodingMemento fEncodingMemento;

	private EncodingRule fEncodingRule;

	private String fFilename;

	private IFile fIFile;


	private InputStream fInputStream;
	
	private static final String CHARSET_UTF_16= "UTF-16"; //$NON-NLS-1$
	
	private static final String CHARSET_UTF_16LE= "UTF-16LE"; //$NON-NLS-1$

	public CodedReaderCreator() {

		super();
	}

	public CodedReaderCreator(IFile file) throws CoreException, IOException {

		this();
		set(file);
		setEncodingRule(EncodingRule.CONTENT_BASED);
	}

	public CodedReaderCreator(IFile file, EncodingRule encodingRule) throws CoreException, IOException {

		this();
		set(file);
		setEncodingRule(encodingRule);
	}

	public CodedReaderCreator(String filename, InputStream inputStream) {

		this();
		set(filename, inputStream);
		setEncodingRule(EncodingRule.CONTENT_BASED);
	}

	public CodedReaderCreator(String filename, InputStream inputStream, EncodingRule encodingRule) {

		this();
		set(filename, inputStream);
		setEncodingRule(encodingRule);
	}

	private EncodingMemento checkForEncodingInContents(InputStream limitedStream) throws CoreException, IOException {
		EncodingMemento result = null;

		// if encoding memento already set, then iFile must
		// have been set, and no need to get again.
		if (fEncodingMemento != null) {
			result = fEncodingMemento;
		}
		else {
			if (fClientSuppliedStream) {
				try {
					limitedStream.reset();
					IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
					IContentDescription contentDescription = contentTypeManager.getDescriptionFor(limitedStream, fFilename, IContentDescription.ALL);
					if (contentDescription != null) {
						fEncodingMemento = createMemento(contentDescription);
					}
					result = fEncodingMemento;
				}
				finally {
					limitedStream.reset();
				}
			}
			else {
				// throw new IllegalStateException("unexpected state:
				// encodingMemento was null but no input stream supplied by
				// client"); //$NON-NLS-1$
				result = null;
			}
		}

		if (result != null && !result.isValid() && !forceDefault()) {
			throw new UnsupportedCharsetExceptionWithDetail(result);
		}

		return result;
	}

	/**
	 * @param resettableLimitedStream
	 */
	private EncodingMemento checkStreamForBOM(InputStream resettableLimitedStream) {
		EncodingMemento result = null;
		UnicodeBOMEncodingDetector unicodeBOMEncodingDetector = new UnicodeBOMEncodingDetector();
		unicodeBOMEncodingDetector.set(resettableLimitedStream);
		result = unicodeBOMEncodingDetector.getEncodingMemento();
		return result;
	}

	/**
	 * @param iFile
	 * @throws CoreException
	 * @throws IOException
	 */
	private EncodingMemento findMementoFromFileCase() throws CoreException, IOException {
		EncodingMemento result = null;
		IContentDescription contentDescription = null;
		try {
			// This method provides possible improved performance at the
			// cost of sometimes returning null
			if (fIFile.exists())
				contentDescription = fIFile.getContentDescription();
		}
		catch (CoreException e) {
			// Assume if core exception occurs, we can still try more
			// expensive
			// discovery options.
			Logger.logException(e);
		}
		if (contentDescription == null && fIFile.isAccessible()) {
			InputStream contents = null;
			try {
				contents = fIFile.getContents();
				contentDescription = Platform.getContentTypeManager().getDescriptionFor(contents, fIFile.getName(), IContentDescription.ALL);
			}
			catch (CoreException e1) {
				// Assume if core exception occurs, we can't really do much
				// with
				// determining encoding, etc.
				Logger.logException(e1);
				throw e1;
			}
			catch (IOException e2) {
				// We likely couldn't get the contents of the file, something
				// is really wrong
				Logger.logException(e2);
				throw e2;
			}
			if (contents != null) {
				try {
					contents.close();
				}
				catch (IOException e2) {
					Logger.logException(e2);
				}
			}
		}
		if (contentDescription != null) {
			result = createMemento(contentDescription);
		}

		return result;
	}

	/**
	 * The primary method which contains the highest level rules for how to
	 * decide appropriate decoding rules: 1. first check for unicode stream 2.
	 * then looked for encoding specified in content (according to the type of
	 * content that is it ... xml, html, jsp, etc. 3. then check for various
	 * settings: file settings first, if null check project settings, if null,
	 * check user preferences. 4. lastly (or, what is the last user
	 * preference) is to use "workbench defaults".
	 * 
	 * @throws IOException
	 * @throws CoreException
	 */
	private EncodingMemento findMementoFromStreamCase() throws CoreException, IOException {

		EncodingMemento result = null;
		InputStream resettableLimitedStream = null;
		try {
			resettableLimitedStream = getLimitedStream(getResettableStream());
			if (resettableLimitedStream != null) {
				// first check for unicode stream
				result = checkStreamForBOM(resettableLimitedStream);
				// if not that, then check contents
				if (result == null) {
					resettableLimitedStream.reset();
					result = checkForEncodingInContents(resettableLimitedStream);
				}

			}
			else {
				// stream null, may name's not.
				if (fFilename != null) {
					// filename not null
					IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
					IContentDescription contentDescription = contentTypeManager.getDescriptionFor(new NullInputStream(), fFilename, IContentDescription.ALL);
					if (contentDescription != null) {
						result = createMemento(contentDescription);
					}
				}
			}
		}
		finally {
			if (resettableLimitedStream != null) {
				handleStreamClose(resettableLimitedStream);
			}
		}
		return result;
	}

	private boolean forceDefault() {

		boolean result = false;
		if (fEncodingRule != null && fEncodingRule == EncodingRule.FORCE_DEFAULT)
			result = true;
		return result;
	}

	public Reader getCodedReader() throws CoreException, IOException {

		Reader result = null;
		// we make a local copy of encoding memento so
		// stream won't
		// be accessed simultaneously.
		EncodingMemento encodingMemento = getEncodingMemento();
		Assert.isNotNull(encodingMemento, "Appears reader requested before file or stream set"); //$NON-NLS-1$
		InputStream streamToReturn = getResettableStream();
		streamToReturn.reset();
		// if UTF 3 byte BOM is used (or UTF-16LE), the
		// built in converters
		// don't
		// correct skip all three bytes ... so skip
		// remaining one to leave
		// stream transparently ready for client.
		// see ... TODO look up bug number
		if (encodingMemento.isUnicodeStream()) {
			streamToReturn.skip(2);
		}
		else if (encodingMemento.isUTF83ByteBOMUsed()) {
			streamToReturn.skip(3);
		}
		String charsetName = encodingMemento.getJavaCharsetName();
		if (charsetName == null) {
			charsetName = encodingMemento.getDetectedCharsetName();
		}
		if (!encodingMemento.isValid() && !forceDefault()) {
			throw new UnsupportedCharsetExceptionWithDetail(encodingMemento);
		}

		if (fEncodingRule == EncodingRule.FORCE_DEFAULT) {
			charsetName = encodingMemento.getAppropriateDefault();
		}
		
		// [228366] For files that have a unicode BOM, and a charset name of UTF-16, the charset decoder needs "UTF-16LE"
		if(CHARSET_UTF_16.equals(charsetName) && encodingMemento.getUnicodeBOM() == IContentDescription.BOM_UTF_16LE)
			charsetName = CHARSET_UTF_16LE;
		
		Charset charset = Charset.forName(charsetName);
		CharsetDecoder charsetDecoder = charset.newDecoder();
		if (fEncodingRule == EncodingRule.IGNORE_CONVERSION_ERROR) {
			charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
			charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		}
		else {
			charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
			charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		}
		// more efficient to be buffered, and I know of no
		// reason not to return
		// that directly.
		result = new BufferedReader(new InputStreamReader(streamToReturn, charsetDecoder), CodedIO.MAX_BUF_SIZE);
		result.mark(CodedIO.MAX_BUF_SIZE);
		return result;
	}

	public EncodingMemento getEncodingMemento() throws CoreException, IOException {
		// figure out encoding memento from encoding strategy
		if (fEncodingMemento == null) {
			if (fClientSuppliedStream) {
				fEncodingMemento = findMementoFromStreamCase();
			}
			else if (fIFile != null) {
				fEncodingMemento = findMementoFromFileCase();
			}
		}

		// if encoding stratagy doesn't provide answer,
		// then try file settings, project settings,
		// user preferences, and
		// finally workbench default.
		//
		if (fEncodingMemento == null || fEncodingMemento.getDetectedCharsetName() == null) {
			fEncodingMemento = getEncodingMementoFromResourceAndPreference();
		}

		// use DefaultNameRules from NonContentBasedEncodingRules as the final
		// default
		if (fEncodingMemento == null) {
			fEncodingMemento = handleNotProvidedFromContentCase();
		}

		return fEncodingMemento;
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

	/**
	 * Ensures that an InputStream has mark/reset support, is readlimit is
	 * set, and that the stream is "limitable" (that is, reports "end of
	 * input" rather than allow going past mark). This is very specialized
	 * stream introduced to overcome
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=67211. See also
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=68565
	 */
	private InputStream getLimitedStream(InputStream original) {
		if (original == null)
			return null;
		if (original instanceof BufferedLimitedStream)
			return original;
		InputStream s = new BufferedLimitedStream(original, CodedIO.MAX_MARK_SIZE);
		s.mark(CodedIO.MAX_MARK_SIZE);
		return s;
	}

	private InputStream getResettableStream() throws CoreException, IOException {

		InputStream resettableStream = null;

		if (fIFile != null) {
			InputStream inputStream = null;
			try {
				// note we always get contents, even if out of synch
				inputStream = fIFile.getContents(true);
			}
			catch (CoreException e) {
				// SHOULD actually check for existence of
				// fIStorage, but
				// for now will just assume core exception
				// means it
				// doesn't exist on file system, yet.
				// and we'll log, just in case its a noteable error
				Logger.logException(e);
				inputStream = new NullInputStream();
			}
			resettableStream = new BufferedInputStream(inputStream, CodedIO.MAX_BUF_SIZE);
		}
		else {
			if (fInputStream != null) {
				if (fInputStream.markSupported()) {
					resettableStream = fInputStream;
					// try {
					resettableStream.reset();
					// }
					// catch (IOException e) {
					// // assumed just hasn't been marked yet, so ignore
					// }
				}
				else {
					resettableStream = new BufferedInputStream(fInputStream, CodedIO.MAX_BUF_SIZE);
				}
			}
		}

		if (resettableStream == null) {
			resettableStream = new NullInputStream();
		}

		// mark this once, stream at "zero" position
		resettableStream.mark(MAX_MARK_SIZE);
		return resettableStream;
	}

	private EncodingMemento handleNotProvidedFromContentCase() {

		EncodingMemento result = null;
		String specDefault = null;
		// try {
		// specDefault = getEncodingDetector().getSpecDefaultEncoding();
		// }
		// catch (CoreException e) {
		// // If this exception occurs, assumes there is
		// // no specDefault
		// }
		// catch (IOException e) {
		// // If this exception occurs, assumes there is
		// // no specDefault
		// }
		// finally {
		// try {
		// handleStreamClose(fEncodingDetectorStream);
		// }
		// catch (IOException e1) {
		// // severe error, not much to do here
		// }
		// }
		// this logic should be moved to 'detection' if not already
		String charset = NonContentBasedEncodingRules.useDefaultNameRules(specDefault);
		Assert.isNotNull(charset, "post condition failed"); //$NON-NLS-1$
		result = CodedIO.createEncodingMemento(charset);
		return result;
	}

	/**
	 * @param resettableInputStream
	 * @throws IOException
	 */
	private void handleStreamClose(InputStream resettableInputStream) throws IOException {

		if (resettableInputStream != null) {
			if (fClientSuppliedStream) {
				resettableInputStream.reset();
			}
			else {

				resettableInputStream.close();
			}
		}
	}

	// TODO We just copy the content properties encoding to current resource's
	// encoding for now. May improve the UI later by setting an informational
	// message and/or disable the content properties encoding field.
	// TODO: remake private else remove
	void migrateContentPropertiesEncoding(String encoding) throws CoreException {
		final IFile file = fIFile;
		final String charset = encoding;
		// TODO: externalize string later
		Job migrater = new Job(SSECoreMessages.Migrate_Charset) { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				if (file != null) {
					try {
						file.setCharset(charset, null);
					}
					catch (CoreException e) {
						Logger.logException(e);
					}
				}
				return Status.OK_STATUS;
			}
		};
		migrater.setSystem(true);
		migrater.schedule();
	}

	private void resetAll() {

		fEncodingRule = null;
		fIFile = null;
		fFilename = null;
		fInputStream = null;
		fEncodingMemento = null;
		fClientSuppliedStream = false;
	}

	public void set(IFile iFile) throws CoreException, IOException {
		Assert.isNotNull(iFile, "illegal argument"); //$NON-NLS-1$
		resetAll();
		fIFile = iFile;
	}

	public void set(String filename, InputStream inputStream) {

		resetAll();
		fFilename = filename;
		fInputStream = inputStream;
		fClientSuppliedStream = true;
	}

	public void setEncodingRule(EncodingRule encodingRule) {

		fEncodingRule = encodingRule;
	}
}
