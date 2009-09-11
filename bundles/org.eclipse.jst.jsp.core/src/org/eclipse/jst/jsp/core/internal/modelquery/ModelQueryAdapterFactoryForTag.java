/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.SimpleAssociationProvider;

public class ModelQueryAdapterFactoryForTag extends ModelQueryAdapterFactoryForJSP {

	public ModelQueryAdapterFactoryForTag() {
	}

	public ModelQueryAdapterFactoryForTag(Object key, boolean registerAdapters) {
		super(key, registerAdapters);
	}

	public INodeAdapterFactory copy() {
		return new ModelQueryAdapterFactoryForTag(getAdapterKey(), isShouldRegisterAdapter());
	}

	protected ModelQuery createModelQuery(IStructuredModel model, URIResolver resolver) {
		return new TagModelQuery(new SimpleAssociationProvider(new TagModelQueryCMProvider()));
	}
}
