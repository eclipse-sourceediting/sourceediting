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
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
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

	public static URIResolver getIdResolver(Document node) {
		ModelQueryAdapter modelQueryAdapter = getModelQueryAdapter(node);
		return modelQueryAdapter != null ? modelQueryAdapter.getIdResolver() : null;
	}

	public static ModelQuery getModelQuery(Document node) {
		ModelQueryAdapter modelQueryAdapter = getModelQueryAdapter(node);
		return modelQueryAdapter != null ? modelQueryAdapter.getModelQuery() : null;
	}

	public static ModelQuery getModelQuery(IStructuredModel model) {
		if ((!(model instanceof IDOMModel)) || model == null)
			return null;
		return getModelQuery(((IDOMModel) model).getDocument());
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
