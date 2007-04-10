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
package org.eclipse.wst.xml.core.internal.modelquery;



import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.ssemodelquery.MovableModelQuery;


public class XMLModelQueryImpl extends ModelQueryImpl implements MovableModelQuery {

	protected CMDocumentCache fCache = null;

	public XMLModelQueryImpl(CMDocumentCache cache, URIResolver idResolver) {
		super(new XMLModelQueryAssociationProvider(cache, idResolver));
		fCache = cache;
	}

	/**
	 * @see MovableModelQuery#setIdResolver(URIResolver)
	 */
	public void setIdResolver(URIResolver newIdResolver) {
		modelQueryAssociationProvider = new XMLModelQueryAssociationProvider(fCache, newIdResolver);
	}
}
