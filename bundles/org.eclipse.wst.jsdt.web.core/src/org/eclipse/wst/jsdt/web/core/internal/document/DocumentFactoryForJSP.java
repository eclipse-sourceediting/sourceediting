/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.document;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.jface.text.IDocument;

import org.eclipse.wst.jsdt.web.core.internal.provisional.JSP11Namespace;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;

public class DocumentFactoryForJSP implements IDocumentFactory {

	public DocumentFactoryForJSP() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filebuffers.IDocumentFactory#createDocument()
	 */
	public IDocument createDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory
				.getNewStructuredDocumentInstance(new XMLSourceParser());
		return structuredDocument;
	}

	public RegionParser getParser() {
		// remember, the Loader
		// will need to finish initialization of parser
		// based on "embedded content"
		XMLSourceParser parser = new XMLSourceParser();
		// add default nestable tag list
		
		return parser;
	}



}