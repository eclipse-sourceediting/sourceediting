/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.common.encoding.CodedIO;
import org.eclipse.wst.common.encoding.CodedReaderCreator;
import org.eclipse.wst.common.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.common.encoding.EncodingMemento;
import org.eclipse.wst.common.encoding.EncodingRule;
import org.eclipse.wst.common.encoding.exceptions.MalformedInputExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.document.NullStructuredDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.document.TextUtilities;



/**
 * This class reads a file and creates an Structured Model.
 */
public abstract class AbstractDocumentLoader implements IDocumentLoader {

	private CodedReaderCreator fCodedReaderCreator;
	protected IDocumentCharsetDetector fDocumentEncodingDetector;
	//private boolean fPropertiesObtained;

	protected EncodingMemento fEncodingMemento;
	protected Reader fFullPreparedReader;

	/**
	 * AbstractLoader constructor also initializes encoding converter/mapper
	 */
	public AbstractDocumentLoader() {
		super();
	}

	protected final StringBuffer convertLineDelimiters(StringBuffer allTextBuffer, String lineDelimiterToUse) {
		// TODO: avoid use of String instance
		String allText = allTextBuffer.toString();
		IDocument tempDoc = new Document(allText);
		if (lineDelimiterToUse == null)
			lineDelimiterToUse = System.getProperty("line.separator"); //$NON-NLS-1$
		StringBuffer newText = new StringBuffer();
		int lineCount = tempDoc.getNumberOfLines();
		for (int i = 0; i < lineCount; i++) {
			try {
				org.eclipse.jface.text.IRegion lineInfo = tempDoc.getLineInformation(i);
				int lineStartOffset = lineInfo.getOffset();
				int lineLength = lineInfo.getLength();
				int lineEndOffset = lineStartOffset + lineLength;
				newText.append(allText.substring(lineStartOffset, lineEndOffset));
				if ((i < lineCount - 1) && (tempDoc.getLineDelimiter(i) != null))
					newText.append(lineDelimiterToUse);
			} catch (org.eclipse.jface.text.BadLocationException exception) {
				// should fix up to either throw nothing, or the right thing,
				// but
				// in the course of refactoring, this was easiest "quick fix".
				throw new RuntimeException(exception);
			}
		}
		return newText;
	}

	/**
	 * This method must return a new instance of IEncodedDocument, that has
	 * been initialized with appropriate parser. For many loaders, the
	 * (default) parser used is known for any input. For others, the correct
	 * parser (and its initialization) is normally dependent on the content of
	 * the file. This no-argument method should assume "empty input" and would
	 * therefore return the default parser for the default contentType.
	 */
	public IEncodedDocument createNewStructuredDocument() {
		IEncodedDocument structuredDocument = newEncodedDocument();
		// make sure every structuredDocument has an encodoing memento,
		// which is the default one for "empty" structuredDocuments
		String charset = ContentTypeEncodingPreferences.useDefaultNameRules(getDocumentEncodingDetector());
		String specDefaultCharset = getDocumentEncodingDetector().getSpecDefaultEncoding();
		structuredDocument.setEncodingMemento(CodedIO.createEncodingMemento(charset, EncodingMemento.DEFAULTS_ASSUMED_FOR_EMPTY_INPUT, specDefaultCharset));
		structuredDocument.setDocumentPartitioner(getDefaultDocumentPartitioner());
		return structuredDocument;
	}

	/**
	 * This abstract version should handle most cases, but won't if
	 * contentType is sensitive to encoding, and/or embedded types
	 */
	public IEncodedDocument createNewStructuredDocument(IFile iFile) throws IOException, CoreException {
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		try {

			CodedReaderCreator creator = getCodedReaderCreator();
			creator.set(iFile);
			fEncodingMemento = creator.getEncodingMemento();
			structuredDocument.setEncodingMemento(fEncodingMemento);
			fFullPreparedReader = getCodedReaderCreator().getCodedReader();

			setDocumentContentsFromReader(structuredDocument, fFullPreparedReader);
		} finally {
			if (fFullPreparedReader != null) {
				fFullPreparedReader.close();
			}
		}
		return structuredDocument;
	}

	public IEncodedDocument createNewStructuredDocument(Reader reader) throws UnsupportedEncodingException, IOException {
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		StringBuffer allText = readInputStream(fFullPreparedReader);
		structuredDocument.set(allText.toString());
		return structuredDocument;
	}

	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream) throws UnsupportedEncodingException, IOException {
		return createNewStructuredDocument(filename, inputStream, EncodingRule.CONTENT_BASED);
	}

	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException {
		if (filename == null && inputStream == null) {
			throw new IllegalArgumentException("can not have both null filename and inputstream"); //$NON-NLS-1$
		}
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		CodedReaderCreator codedReaderCreator = getCodedReaderCreator();
		try {
			codedReaderCreator.set(filename, inputStream);
			codedReaderCreator.setEncodingRule(encodingRule);
			fEncodingMemento = codedReaderCreator.getEncodingMemento();
			fFullPreparedReader = codedReaderCreator.getCodedReader();
			structuredDocument.setEncodingMemento(fEncodingMemento);
			setDocumentContentsFromReader(structuredDocument, fFullPreparedReader);
		} catch (CoreException e) {
			// impossible in this context
			throw new Error(e);
		} finally {
			if (fFullPreparedReader != null) {
				fFullPreparedReader.close();
			}
		}

		return structuredDocument;
	}

	private int getCharPostionOfFailure(BufferedReader inputStream) {
		int charPosition = 1;
		int charRead = -1;
		boolean errorFound = false;
		do {
			try {
				charRead = inputStream.read();
				charPosition++;
			} catch (IOException e) {
				// this is expected, since we're expecting failure,
				// so no need to do anything.
				errorFound = true;
				break;
			}
		} while (!(charRead == -1 || errorFound));

		if (errorFound)
			// dmw, blindly modified to +1 to get unit tests to work, moving
			// from Java 1.3, to 1.4
			// not sure how/why this behavior would have changed. (Its as if
			// 'read' is reporting error
			// one character early).
			return charPosition + 1;
		else
			return -1;
	}

	/**
	 * @return Returns the codedReaderCreator.
	 */
	protected CodedReaderCreator getCodedReaderCreator() {
		if (fCodedReaderCreator == null) {
			fCodedReaderCreator = new CodedReaderCreator();
		}
		return fCodedReaderCreator;
	}

	/**
	 * Method getDefaultDocumentPartitioner.
	 * 
	 * @return IDocumentPartitioner
	 */
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		// be default, we'll provide XML (though could be NOOP?
		// 7/2003 .... changed during re-org ... to be Null instead of XML,
		// so there would be no minor dependancies from higher plugins.
		return new NullStructuredDocumentPartitioner();
	}

	/**
	 * Returns the encodingMemento.
	 * 
	 * @return EncodingMemento
	 */
	public EncodingMemento getEncodingMemento() {
		if (fEncodingMemento == null) {
			throw new IllegalStateException("Program Error: encodingMemento was accessed before it was set"); //$NON-NLS-1$
		}
		return fEncodingMemento;
	}

	/**
	 * @return Returns the fullPreparedReader.
	 */
	protected Reader getFullPreparedReader() throws UnsupportedEncodingException, CoreException, IOException {
		if (fFullPreparedReader == null) {
			fFullPreparedReader = getCodedReaderCreator().getCodedReader();
		}
		return fFullPreparedReader;
	}

	/**
	 * If subclass doesn't implement, return null
	 */
	protected String getPreferredNewLineDelimiter() {
		return null;
	}

	/**
	 * A utility method, but depends on subclasses to impliment the preferred
	 * end of line for a particular content type. Note: subclasses should not
	 * re-implement this method (there's no reason to, even though its part of
	 * interface). This method not only converts end-of-line characters, if
	 * needed, but sets the correct end-of-line delimiter in
	 * structuredDocument. Minor note: can't use this exact method in dumpers,
	 * since the decision to change or not is a little different, and since
	 * there we have to change text of structuredDocument if found to need
	 * conversion. (Where as for loading, we assume we haven't yet set text in
	 * structuredDocument, but will be done by other method just a tiny biy
	 * later). Needs to be public to handle interface. It is in the interface
	 * just so ModelManagerImpl can use it in a special circumstance.
	 */
	public StringBuffer handleLineDelimiter(StringBuffer originalString, IEncodedDocument theFlatModel) {
		// TODO: need to handle line delimiters so Marker Positions are
		// updated
		StringBuffer convertedText = null;
		// based on text, make a guess on what's being used as
		// line delimiter
		String probableLineDelimiter = TextUtilities.determineLineDelimiter(originalString, theFlatModel.getLegalLineDelimiters(), System.getProperty("line.separator")); //$NON-NLS-1$
		String preferredLineDelimiter = getPreferredNewLineDelimiter();
		if (preferredLineDelimiter == null) {
			// when preferredLineDelimiter is null, it means "leave alone"
			// so no conversion needed.
			// set here, only if null (should already be set, but if not,
			// we'll set so any subsequent editing inserts what we're
			// assuming)
			if (!theFlatModel.getLineDelimiter().equals(probableLineDelimiter)) {
				theFlatModel.setLineDelimiter(probableLineDelimiter);
			}
			convertedText = originalString;
		} else {
			if (!preferredLineDelimiter.equals(probableLineDelimiter)) {
				// technically, wouldn't have to convert line delimiters
				// here at beginning, but when we save, if the preferred
				// line delimter is "leave alone" then we do leave alone,
				// so best to be right from beginning.
				convertedText = convertLineDelimiters(originalString, preferredLineDelimiter);
				theFlatModel.setLineDelimiter(preferredLineDelimiter);
			} else {
				// they are already the same, no conversion needed
				theFlatModel.setLineDelimiter(preferredLineDelimiter);
				convertedText = originalString;
			}
		}
		return convertedText;
	}

	protected abstract IEncodedDocument newEncodedDocument();

	/**
	 * Very mechanical method, just to read the characters, once the reader is
	 * correctly created. Can throw MalFormedInputException.
	 */
	private StringBuffer readInputStream(Reader reader) throws IOException {

		int fBlocksRead = 0;
		StringBuffer buffer = new StringBuffer();
		int numRead = 0;
		try {
			char tBuff[] = new char[CodedIO.MAX_BUF_SIZE];
			while (numRead != -1) {
				numRead = reader.read(tBuff, 0, tBuff.length);
				if (numRead > 0) {
					buffer.append(tBuff, 0, numRead);
					fBlocksRead++;
				}
			}
		} catch (MalformedInputException e) {
			throw new MalformedInputExceptionWithDetail(fEncodingMemento.getJavaCharsetName(), fBlocksRead * CodedIO.MAX_BUF_SIZE + numRead + e.getInputLength());
		} catch (UnmappableCharacterException e) {
			throw new MalformedInputExceptionWithDetail(fEncodingMemento.getJavaCharsetName(), fBlocksRead * CodedIO.MAX_BUF_SIZE + numRead + e.getInputLength());

		}
		return buffer;
	}

	public void reload(IEncodedDocument encodedDocument, Reader inputStreamReader) throws IOException {
		if (inputStreamReader == null) {
			throw new IllegalArgumentException("stream reader can not be null"); //$NON-NLS-1$
		}
		int READ_BUFFER_SIZE = 8192;
		int MAX_BUFFERED_SIZE_FOR_RESET_MARK = 200000;
		// temp .... eventually we'lll only read as needed
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader, MAX_BUFFERED_SIZE_FOR_RESET_MARK);
		bufferedReader.mark(MAX_BUFFERED_SIZE_FOR_RESET_MARK);
		StringBuffer buffer = new StringBuffer();
		try {
			int numRead = 0;
			char tBuff[] = new char[READ_BUFFER_SIZE];
			while ((numRead = bufferedReader.read(tBuff, 0, tBuff.length)) != -1) {
				buffer.append(tBuff, 0, numRead);
			}
			// remember -- we didn't open stream ... so we don't close it
		} catch (MalformedInputException e) {
			int pos = e.getInputLength();
			EncodingMemento localEncodingMemento = getEncodingMemento();
			boolean couldReset = true;
			String encodingNameInError = localEncodingMemento.getJavaCharsetName();
			if (encodingNameInError == null) {
				encodingNameInError = localEncodingMemento.getDetectedCharsetName();
			}
			try {
				bufferedReader.reset();
			} catch (IOException resetException) {
				// the only errro that can occur during reset is an
				// IOException
				// due to already being past the rest mark. In that case, we
				// throw more generic message
				couldReset = false;
			}
			// -1 can be used by UI layer as a code that "position could not
			// be
			// determined"
			int charPostion = -1;
			if (couldReset) {

				charPostion = getCharPostionOfFailure(bufferedReader);
				//getCharPostionOfFailure(new InputStreamReader(inStream,
				// javaEncodingNameInError));
			}
			// all of that just to throw more accurate error
			// note: we do the conversion to ianaName, instead of using the
			// local
			// variable,
			// because this is ultimately only for the user error message
			// (that
			// is,
			// the error occurred
			// in context of javaEncodingName no matter what ianaEncodingName
			// is
			throw new MalformedInputExceptionWithDetail(encodingNameInError, CodedIO.getAppropriateJavaCharset(encodingNameInError), charPostion, !couldReset, MAX_BUFFERED_SIZE_FOR_RESET_MARK);
		}
		StringBuffer stringbuffer = buffer;
		encodedDocument.set(stringbuffer.toString());

	}

	protected void setDocumentContentsFromReader(IEncodedDocument structuredDocument, Reader reader) throws IOException {

		StringBuffer allText = readInputStream(reader);
		structuredDocument.set(allText.toString());
	}
}
