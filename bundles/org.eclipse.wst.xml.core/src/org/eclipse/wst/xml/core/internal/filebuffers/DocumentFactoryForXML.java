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
package org.eclipse.wst.xml.core.internal.filebuffers;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;



public class DocumentFactoryForXML implements IDocumentFactory {

	public DocumentFactoryForXML() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filebuffers.IDocumentFactory#createDocument()
	 */
	public IDocument createDocument() {
		IStructuredDocument structuredDocument = new BasicStructuredDocument(new XMLSourceParser());
		return structuredDocument;
	}

}
