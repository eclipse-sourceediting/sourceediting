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



import org.eclipse.wst.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.modelquery.MovableModelQuery;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;

public class XMLModelQueryImpl extends ModelQueryImpl implements MovableModelQuery {

	protected CMDocumentCache fCache = null;

	public XMLModelQueryImpl(CMDocumentCache cache, IdResolver idResolver) {
		super(new XMLModelQueryAssociationProvider(cache, idResolver));
		fCache = cache;
	}

	/**
	 * @see MovableModelQuery#setIdResolver(IdResolver)
	 */
	public void setIdResolver(IdResolver newIdResolver) {
		modelQueryAssociationProvider = new XMLModelQueryAssociationProvider(fCache, newIdResolver);
	}
}
