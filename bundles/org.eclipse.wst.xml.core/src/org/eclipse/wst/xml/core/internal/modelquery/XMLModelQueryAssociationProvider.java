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

        /*
         * Fix for bug 110356
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=110356
         * 
         * TODO: XMLCatalogURIResolverExtension was contributed to "postnormalization" 
         * stage, but not to "physical" stage.  We have to reconcile that.
         */		
		String resolvedId = idResolver.resolve(null, publicId, systemId); // initially we had only this call
		if(resolvedId == null){
			String location = systemId;
			if(location == null){
				location = publicId;
			}
			// try physical location
			resolvedId = idResolver.resolvePhysicalLocation(null, publicId, location);
		}
		return resolvedId;
	}
}
