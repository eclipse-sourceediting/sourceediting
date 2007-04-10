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
package org.eclipse.wst.xml.core.internal.ssemodelquery;



import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;


public class ModelQueryAdapterImpl implements ModelQueryAdapter {
	protected CMDocumentCache cmDocumentCache;
	protected URIResolver idResolver;

	protected ModelQuery modelQuery;

	public ModelQueryAdapterImpl(CMDocumentCache cmDocumentCache, ModelQuery modelQuery, URIResolver idResolver) {
		this.cmDocumentCache = cmDocumentCache;
		this.modelQuery = modelQuery;
		this.idResolver = idResolver;
	}

	public CMDocumentCache getCMDocumentCache() {
		return cmDocumentCache;
	}

	public URIResolver getIdResolver() {
		return idResolver;
	}

	public ModelQuery getModelQuery() {
		return modelQuery;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(ModelQueryAdapter.class);
	}

	/**
	 */

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	/**
	 * @see ModelQueryAdapter#release()
	 */
	public void release() {
	}

	/**
	 * @see ModelQueryAdapter#setIdResolver(URIResolver)
	 */

	public void setIdResolver(URIResolver newIdResolver) {
		idResolver = newIdResolver;
		if (modelQuery instanceof MovableModelQuery)
			((MovableModelQuery) modelQuery).setIdResolver(newIdResolver);
	}
}
