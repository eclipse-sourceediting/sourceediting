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
package org.eclipse.wst.sse.core.modelquery;

import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;


public interface ModelQueryAdapter extends INodeAdapter {

	public CMDocumentCache getCMDocumentCache();

	public IdResolver getIdResolver();

	public ModelQuery getModelQuery();

	void release();

	void setIdResolver(IdResolver newIdResolver);
}
