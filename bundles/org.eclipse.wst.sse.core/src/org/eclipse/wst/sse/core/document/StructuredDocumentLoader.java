/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.document;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.MalformedInputException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.encoding.CodedIO;
import org.eclipse.wst.encoding.EncodingMemento;
import org.eclipse.wst.encoding.IContentDescriptionExtended;
import org.eclipse.wst.encoding.exceptions.MalformedInputExceptionWithDetail;

public class StructuredDocumentLoader {

	private ITextFileBufferManager fBufferManager;

	public IEncodedDocument createNewStructuredDocument(IFile iFile, IProgressMonitor monitor) throws IOException, CoreException {
		IDocument document = null;
		IPath locationPath = iFile.getFullPath();
	
		if (iFile.exists()) {
	
			getBufferManager().connect(locationPath, monitor);
			ITextFileBuffer buffer = getBufferManager().getTextFileBuffer(locationPath);
			document = buffer.getDocument();
		}
		else {
			document = getBufferManager().createEmptyDocument(locationPath);
		}
	
		return (IEncodedDocument) document;
	}

	/**
	 * @return
	 */
	protected ITextFileBufferManager getBufferManager() {
		if (fBufferManager == null) {
			fBufferManager = FileBuffers.getTextFileBufferManager();
		}
		return fBufferManager;
	}

	/**
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#createNewStructuredDocument(org.eclipse.core.resources.IFile)
	 * @deprecated - use form with progress monitor
	 */
	public IEncodedDocument createNewStructuredDocument(IFile iFile) throws IOException, CoreException {
		return createNewStructuredDocument(iFile, null);
	}

	public IEncodedDocument createNewStructuredDocument(IPath locationPath, IProgressMonitor monitor) throws IOException, CoreException {
		IDocument document = null;
		File file = null;
		file = FileBuffers.getSystemFileAtLocation(locationPath);
		if (file.exists()) {
			getBufferManager().connect(locationPath, monitor);
			ITextFileBuffer buffer = getBufferManager().getTextFileBuffer(locationPath);
			document = buffer.getDocument();
		}
		else {
			document = getBufferManager().createEmptyDocument(locationPath);
		}
		return (IEncodedDocument) document;
	}

	public void release(IFile iFile, IProgressMonitor monitor) throws CoreException {
		IPath locationPath = iFile.getFullPath();
		getBufferManager().disconnect(locationPath, monitor);
	
	}

	/**
	 * @param reader
	 * @return @throws
	 *         IOException
	 */
	private EncodingMemento getEncodingMemento(Reader reader) throws IOException {
		IContentDescription description = Platform.getContentTypeManager().getDescriptionFor(reader, null, new QualifiedName[]{IContentDescriptionExtended.ENCODING_MEMENTO});
		EncodingMemento encodingMemento = null;
		if (description != null) {
			encodingMemento = (EncodingMemento) description.getProperty(IContentDescriptionExtended.ENCODING_MEMENTO);
		}
		return encodingMemento;
	}

	/**
	 * Very mechanical method, just to read the characters, once the reader is
	 * correctly created. Can throw MalFormedInputException.
	 */
	protected StringBuffer readInputStream(Reader reader) throws IOException {
	
		int fBlocksRead = 0;
		StringBuffer buffer = new StringBuffer();
		int numRead = 0;
		try {
			char tBuff[] = new char[CodedIO.MAX_BUF_SIZE];
			while ((numRead = reader.read(tBuff, 0, tBuff.length)) != -1) {
				buffer.append(tBuff, 0, numRead);
				fBlocksRead++;
			}
		}
		catch (MalformedInputException e) {
			EncodingMemento encodingMemento = getEncodingMemento(reader);
			throw new MalformedInputExceptionWithDetail(encodingMemento.getJavaCharsetName(), fBlocksRead * CodedIO.MAX_BUF_SIZE + e.getInputLength());
		}
		return buffer;
	}

	protected void setDocumentContentsFromReader(IEncodedDocument structuredDocument, Reader reader) throws IOException {
	
		StringBuffer allText = readInputStream(reader);
		structuredDocument.set(allText.toString());
	}

	public void reload(IEncodedDocument document, Reader reader) throws IOException {
		setDocumentContentsFromReader(document, reader);
	
	}

}
