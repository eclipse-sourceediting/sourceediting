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
package org.eclipse.wst.xml.core.modelquery;



import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.w3c.dom.Document;


/**
 * This class is used to associate ModelQuery (and related data) with a
 * Document (or IStructuredModel).
 */
public class ModelQueryUtil {

	public static CMDocumentCache getCMDocumentCache(Document node) {
		ModelQueryAdapter modelQueryAdapter = getModelQueryAdapter(node);
		return modelQueryAdapter != null ? modelQueryAdapter.getCMDocumentCache() : null;
	}

	public static IdResolver getIdResolver(Document node) {
		ModelQueryAdapter modelQueryAdapter = getModelQueryAdapter(node);
		return modelQueryAdapter != null ? modelQueryAdapter.getIdResolver() : null;
	}

	public static ModelQuery getModelQuery(Document node) {
		ModelQueryAdapter modelQueryAdapter = getModelQueryAdapter(node);
		return modelQueryAdapter != null ? modelQueryAdapter.getModelQuery() : null;
	}

	public static ModelQuery getModelQuery(IStructuredModel model) {
		if ((!(model instanceof XMLModel)) || model == null)
			return null;
		return getModelQuery(((XMLModel) model).getDocument());
	}

	public static ModelQueryAdapter getModelQueryAdapter(Document node) {
		ModelQueryAdapter result = null;

		if (node instanceof INodeNotifier) {
			INodeNotifier notifier = (INodeNotifier) node;
			result = (ModelQueryAdapter) notifier.getAdapterFor(ModelQueryAdapter.class);
		}

		return result;
	}
}
