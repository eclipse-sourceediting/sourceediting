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
package org.eclipse.wst.xml.core.internal.modelquery;



import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.w3c.dom.Document;

/**
 * XMLModelQueryAssociationProvider
 */
class XMLModelQueryAssociationProvider extends XMLAssociationProvider {

	protected URIResolver idResolver;

	public XMLModelQueryAssociationProvider(CMDocumentCache cache, URIResolver idResolver) {
		super(cache);
		this.idResolver = idResolver;
	}

	protected String resolveGrammarURI(Document document, String publicId, String systemId) {
		return idResolver.resolve(null, publicId, systemId);
	}
}
