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
package org.eclipse.wst.xml.core.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentLoaderForFileBuffers;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentLoader;
import org.eclipse.wst.sse.core.internal.encoding.CodedReaderCreator;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.xml.core.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.filebuffers.DocumentFactoryForXML;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;



public class DocumentLoaderForXML extends StructuredDocumentLoader implements IDocumentLoader, IDocumentLoaderForFileBuffers {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#createNewStructuredDocument()
	 */
	public IEncodedDocument createNewStructuredDocument() {
		DocumentFactoryForXML factory = new DocumentFactoryForXML();
		IEncodedDocument document = (IEncodedDocument) factory.createDocument();
		return document;
	}

	public IEncodedDocument createNewStructuredDocument(Reader reader) throws UnsupportedEncodingException, IOException {
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		StringBuffer allText = readInputStream(reader);
		structuredDocument.set(allText.toString());
		return structuredDocument;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#createNewStructuredDocument(java.lang.String,
	 *      java.io.InputStream)
	 */
	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream) throws IOException {
		return createNewStructuredDocument(filename, inputStream, EncodingRule.CONTENT_BASED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#createNewStructuredDocument(java.lang.String,
	 *      java.io.InputStream, com.ibm.encoding.resource.EncodingRule)
	 */
	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream, EncodingRule encodingRule) throws IOException {
		if (filename == null && inputStream == null) {
			throw new IllegalArgumentException("can not have both null filename and inputstream"); //$NON-NLS-1$
		}
		CodedReaderCreator codedReaderCreator = new CodedReaderCreator();
		Reader fullPreparedReader = null;
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		try {
			codedReaderCreator.set(filename, inputStream);
			codedReaderCreator.setEncodingRule(encodingRule);
			EncodingMemento encodingMemento = codedReaderCreator.getEncodingMemento();
			fullPreparedReader = codedReaderCreator.getCodedReader();
			structuredDocument.setEncodingMemento(encodingMemento);
			StringBuffer allText = readInputStream(fullPreparedReader);
			structuredDocument.set(allText.toString());
		} catch (CoreException e) {
			// impossible in this context
			throw new Error(e);
		} finally {
			if (fullPreparedReader != null) {
				fullPreparedReader.close();
			}
		}

		return structuredDocument;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#getDefaultDocumentPartitioner()
	 */
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForXML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#getDocumentEncodingDetector()
	 */
	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IDocumentLoader#handleLineDelimiter(java.lang.StringBuffer,
	 *      org.eclipse.wst.sse.core.document.IEncodedDocument)
	 */
	public StringBuffer handleLineDelimiter(StringBuffer originalString, IEncodedDocument theStructuredDocument) {
		// TODO Auto-generated method stub
		return originalString;
	}

}

