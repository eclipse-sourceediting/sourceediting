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
package org.eclipse.wst.xml.core.internal.contenttype;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;


public abstract class AbstractResourceEncodingDetector implements IResourceCharsetDetector {

	protected EncodingMemento fEncodingMemento;

	protected boolean fHeaderParsed;

	protected Reader fReader;

	/**
	 *  
	 */
	public AbstractResourceEncodingDetector() {
		super();
	}

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	private void createEncodingMemento(String detectedCharsetName) {
		fEncodingMemento = new EncodingMemento();
		fEncodingMemento.setJavaCharsetName(getAppropriateJavaCharset(detectedCharsetName));
		fEncodingMemento.setDetectedCharsetName(detectedCharsetName);
		// TODO: if detectedCharset and spec default is
		// null, need to use "work
		// bench based" defaults.
		fEncodingMemento.setAppropriateDefault(getSpecDefaultEncoding());
	}

	/**
	 * convience method all subclasses can use (but not override)
	 * 
	 * @param detectedCharsetName
	 * @param reason
	 */
	final protected void createEncodingMemento(String detectedCharsetName, String reason) {
		createEncodingMemento(detectedCharsetName);
	}

	/**
	 * convience method all subclasses can use (but not override)
	 */
	final protected void ensureInputSet() {
		if (fReader == null) {
			throw new IllegalStateException("input must be set before use"); //$NON-NLS-1$
		}
	}

	/**
	 * This method can return null, if invalid charset name (in which case
	 * "appropriateDefault" should be used, if a name is really need for some
	 * "save anyway" cases).
	 * 
	 * @param detectedCharsetName
	 * @return
	 */
	private String getAppropriateJavaCharset(String detectedCharsetName) {
		String result = null;
		// 1. Check explicit mapping overrides from
		// property file -- its here we pick up "rules" for cases
		// that are not even in Java
		result = CodedIO.checkMappingOverrides(detectedCharsetName);
		// 2. Use the "canonical" name from JRE mappings
		// Note: see Charset JavaDoc, the name you get one
		// with can be alias,
		// the name you get back is "standard" name.
		Charset javaCharset = null;
		try {
			javaCharset = Charset.forName(detectedCharsetName);
		} catch (UnsupportedCharsetException e) {
			// only set invalid, if result is same as detected -- they won't
			// be equal if
			// overridden
			if (result != null && result.equals(detectedCharsetName)) {
				fEncodingMemento.setInvalidEncoding(detectedCharsetName);
			}
		} catch (IllegalCharsetNameException e) {
			// only set invalid, if result is same as detected -- they won't
			// be equal if
			// overridden
			if (result != null && result.equals(detectedCharsetName)) {
				fEncodingMemento.setInvalidEncoding(detectedCharsetName);
			}
		}
		// give priority to java cononical name, if present
		if (javaCharset != null) {
			result = javaCharset.name();
			// but still allow overrides
			result = CodedIO.checkMappingOverrides(result);
		}
		return result;
	}

	public String getEncoding() throws IOException {
		return getEncodingMemento().getDetectedCharsetName();
	}

	// to ensure consist overall rules used, we'll mark as
	// final,
	// and require subclasses to provide certain pieces of
	// the
	// implementation
	public EncodingMemento getEncodingMemento() throws IOException {
		ensureInputSet();
		if (!fHeaderParsed) {
			parseInput();
			// we keep track of if header's already been
			// parse, so can make
			// multiple 'get' calls, without causing
			// reparsing.
			fHeaderParsed = true;
			// Note: there is a "hidden assumption" here
			// that an empty
			// string in content should be treated same as
			// not present.
		}
		if (fEncodingMemento == null) {
			handleSpecDefault();
		}
		if (fEncodingMemento == null) {
			// safty net
			fEncodingMemento = new NullMemento();
		}
		return fEncodingMemento;
	}

	/**
	 * This is to return a default encoding -- as specified by an industry
	 * content type spec -- when not present in the stream, for example, XML
	 * specifies UTF-8, JSP specifies ISO-8859-1. This method should return
	 * null if there is no such "spec default".
	 */
	abstract public String getSpecDefaultEncoding();

	public EncodingMemento getSpecDefaultEncodingMemento() {
		resetAll();
		EncodingMemento result = null;
		String enc = getSpecDefaultEncoding();
		if (enc != null) {
			createEncodingMemento(enc, EncodingMemento.DEFAULTS_ASSUMED_FOR_EMPTY_INPUT);
			fEncodingMemento.setAppropriateDefault(enc);
			result = fEncodingMemento;
		}
		return result;
	}

	private void handleSpecDefault() {
		String encodingName;
		encodingName = getSpecDefaultEncoding();
		if (encodingName != null) {
			//createEncodingMemento(encodingName,
			// EncodingMemento.USED_CONTENT_TYPE_DEFAULT);
			fEncodingMemento = new EncodingMemento();
			fEncodingMemento.setJavaCharsetName(encodingName);
			fEncodingMemento.setAppropriateDefault(encodingName);
		}
	}

	/**
	 * Every subclass must provide a way to parse the input. This method has
	 * several critical responsibilities:
	 * <li>set the fEncodingMemento field appropriately, according to the
	 * results of the parse of fReader.</li>
	 * <li>set fHarderParsed to true, to avoid wasted re-parsing.</li>
	 */
	abstract protected void parseInput() throws IOException;

	/**
	 *  
	 */
	protected void resetAll() {
		fReader = null;
		fHeaderParsed = false;
		fEncodingMemento = null;
	}

	/**
	 *  
	 */
	public void set(InputStream inputStream) {
		resetAll();
		fReader = new ByteReader(inputStream);
		try {
			fReader.mark(CodedIO.MAX_MARK_SIZE);
		} catch (IOException e) {
			// impossible, since we know ByteReader
			// supports marking
			throw new Error(e);
		}
	}

	/**
	 *  
	 */
	public void set(IStorage iStorage) throws CoreException {
		resetAll();
		InputStream inputStream = iStorage.getContents();
		InputStream resettableStream = new BufferedInputStream(inputStream, CodedIO.MAX_BUF_SIZE);
		resettableStream.mark(CodedIO.MAX_MARK_SIZE);
		set(resettableStream);
		// TODO we'll need to "remember" IFile, or
		// get its (or its project's) settings, in case
		// those are needed to handle cases when the
		// encoding is not in the file stream.
	}

	/**
	 * Note: this is not part of interface to help avoid confusion ... it
	 * expected this Reader is a well formed character reader ... that is, its
	 * all ready been determined to not be a unicode marked input stream. And,
	 * its assumed to be in the correct position, at position zero, ready to
	 * read first character.
	 */
	public void set(Reader reader) {
		resetAll();
		fReader = reader;
		if (!fReader.markSupported()) {
			fReader = new BufferedReader(fReader);
		}
		try {
			fReader.mark(CodedIO.MAX_MARK_SIZE);
		} catch (IOException e) {
			// impossble, since we just checked if markable
			throw new Error(e);
		}
	}
}
