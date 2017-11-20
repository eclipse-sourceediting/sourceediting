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
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;

public interface ModelQueryAdapter extends INodeAdapter {

	public CMDocumentCache getCMDocumentCache();

	public URIResolver getIdResolver();

	public ModelQuery getModelQuery();

	void release();

	void setIdResolver(URIResolver newIdResolver);
}
