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
package org.eclipse.wst.xml.core.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.encoding.CodedIO;
import org.eclipse.wst.encoding.CodedReaderCreator;
import org.eclipse.wst.encoding.EncodingMemento;
import org.eclipse.wst.encoding.EncodingRule;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentLoaderForFileBuffers;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentLoader;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.filebuffers.DocumentFactoryForXML;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;



public class DocumentLoaderForXML extends StructuredDocumentLoader implements IDocumentLoader, IDocumentLoaderForFileBuffers {
	/*
	 * (non-Javadoc)
	 * 
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
	 */
	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream) throws IOException {
		return createNewStructuredDocument(filename, inputStream, EncodingRule.CONTENT_BASED);
	}

	/*
	 * (non-Javadoc)
	 * 
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
		}
		catch (CoreException e) {
			// impossible in this context
			throw new Error(e);
		}
		finally {
			if (fullPreparedReader != null) {
				fullPreparedReader.close();
			}
		}

		return structuredDocument;
	}



	/*
	 * (non-Javadoc)
	 * 
	 */
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForXML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	/**
	 * deprecated - no longer used/needed - may no longer work.
	 */
	public EncodingMemento getEncodingMemento() {

		return CodedIO.createEncodingMemento("UTF-8"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public StringBuffer handleLineDelimiter(StringBuffer originalString, IEncodedDocument theStructuredDocument) {
		// TODO Auto-generated method stub
		return originalString;
	}

	/**
	 * @deprecated
	 */
	public IEncodedDocument newEncodedDocument() {
		BasicStructuredDocument structuredDocument = new BasicStructuredDocument(new XMLSourceParser());
		structuredDocument.setReParser(new XMLStructuredDocumentReParser());
		return structuredDocument;
	}
}

