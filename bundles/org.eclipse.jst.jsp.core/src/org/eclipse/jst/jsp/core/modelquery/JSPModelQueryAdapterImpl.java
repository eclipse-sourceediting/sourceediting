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
package org.eclipse.jst.jsp.core.modelquery;



import org.eclipse.jst.jsp.core.contentmodel.tld.TaglibSupport;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapterImpl;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;


public class JSPModelQueryAdapterImpl extends ModelQueryAdapterImpl {

	TaglibSupport support = null;

	public JSPModelQueryAdapterImpl(CMDocumentCache cmDocumentCache, ModelQuery modelQuery, TaglibSupport support, IdResolver idResolver) {
		super(cmDocumentCache, modelQuery, idResolver);
		this.support = support;
	}

	public void release() {
		super.release();
		if (support != null)
			support.setStructuredDocument(null);
	}

	/**
	 * @see com.ibm.sse.model.modelquery.ModelQueryAdapter#setIdResolver(IdResolver)
	 */
	public void setIdResolver(IdResolver newIdResolver) {
		super.setIdResolver(newIdResolver);
		// new location, everything's stale unless the model's been rebuilt
		support.clearCMDocumentCache();
	}

}