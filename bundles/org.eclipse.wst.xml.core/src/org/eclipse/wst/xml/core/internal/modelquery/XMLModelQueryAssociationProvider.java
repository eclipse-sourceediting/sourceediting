/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
 * 
 * This added and/or made public specifically for experimentation. It will
 * change as this functionality becomes API. See
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=119084
 * 
 */
public class XMLModelQueryAssociationProvider extends XMLAssociationProvider {

	protected URIResolver idResolver;

	public XMLModelQueryAssociationProvider(CMDocumentCache cache, URIResolver idResolver) {
		super(cache);
		this.idResolver = idResolver;
	}

	protected String resolveGrammarURI(Document document, String publicId, String systemId) {

		// CS : spooky code alert!
		// this look really strange because we're passing null in as the first
		// argument
		// however we're assuming the use of a 'fudged' URIResolver that knows
		// the
		// correct baseLocation and will call to the URIResolver framework
		// properly

		// CS : note that we should never call resolvePhysical at this point.
		// Physical resolution should only occur when we're interesting to
		// opening the actual stream.
		// The CMDocumentFactory implementation would be responsible for
		// calling resolvePhysical.
		// All we need to do here is return a 'logical' URI

		if (idResolver == null)
			return null;
		return idResolver.resolve(null, publicId, systemId);
	}
}
