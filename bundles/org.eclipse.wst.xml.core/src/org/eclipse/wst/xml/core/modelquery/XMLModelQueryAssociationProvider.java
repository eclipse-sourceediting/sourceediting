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
package org.eclipse.wst.xml.core.modelquery;



import org.eclipse.wst.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.w3c.dom.Document;

/**
 * XMLModelQueryAssociationProvider
 */
class XMLModelQueryAssociationProvider extends XMLAssociationProvider {

	protected IdResolver idResolver;

	public XMLModelQueryAssociationProvider(CMDocumentCache cache, IdResolver idResolver) {
		super(cache);
		this.idResolver = idResolver;
	}

	protected String resolveGrammarURI(Document document, String publicId, String systemId) {
		return idResolver.resolveId(publicId, systemId);
	}
}
