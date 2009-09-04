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
package org.eclipse.wst.sse.core.internal.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;


/**
 * Provides methods for the creation of an IStructuredDocument correctly
 * prepared to work with a particular type of content.
 */
public interface IDocumentLoader {

	/**
	 * @return a new IStructuredDocument prepared by this loader
	 */
	IEncodedDocument createNewStructuredDocument();

	/**
	 * This API is like createNewStructuredDocument, except it should populate
	 * the structuredDocument with the contents of IFile. Also, those
	 * StructuredDocuments which are sensitive to the input (that is, the
	 * parser or parser initialization my require the input) should
	 * additionally initialize the parser, etc., appropriate to the input.
	 * 
	 * As always, the appropriate decoding should be used.
	 */
	IEncodedDocument createNewStructuredDocument(IFile iFile) throws java.io.IOException, CoreException;

	/**
	 * This method must return a new instance of IEncodedDocument, that has
	 * been initialized with appropriate parser. For many loaders, the
	 * (default) parser used is known for any input. For others, the correct
	 * parser (and its initialization) is normally dependent on the content of
	 * the file. This no-argument method should assume "empty input" and would
	 * therefore return the default parser for the default contentType.
	 */
	IEncodedDocument createNewStructuredDocument(String filename, InputStream istream) throws java.io.IOException;

	IEncodedDocument createNewStructuredDocument(String filename, InputStream istream, EncodingRule encodingRule) throws java.io.IOException;

	/**
	 * @return the document partitioner
	 */
	IDocumentPartitioner getDefaultDocumentPartitioner();

	IDocumentCharsetDetector getDocumentEncodingDetector();

	/**
	 * A utility method, but depends on subclasses to implement the preferred
	 * end of line for a particular content type. Note: subclasses should not
	 * re-implement this method (there's no reason to, even though its part of
	 * interface). This method not only converts end-of-line characters, if
	 * needed, but sets the correct end-of-line delimiter in
	 * structuredDocument. The returned value is either the original string,
	 * if no conversion is needed, or a new string with end-of-lines
	 * converted.
	 * 
	 * @deprecated - the content's line delimiters should be preserved
	 */
	StringBuffer handleLineDelimiter(StringBuffer originalString, IEncodedDocument theStructuredDocument);

	void reload(IEncodedDocument document, Reader reader) throws IOException;
}
