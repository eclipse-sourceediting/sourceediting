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
package org.eclipse.wst.html.core.modelquery;



import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;

public class ModelQueryAdapterFactoryForEmbeddedHTML extends ModelQueryAdapterFactoryForHTML {


	/**
	 * Constructor for ModelQueryAdapterFactoryForEmbeddedHTML.
	 */
	public ModelQueryAdapterFactoryForEmbeddedHTML() {
		this(ModelQueryAdapter.class, false);
	}

	/**
	 * Constructor for ModelQueryAdapterFactoryForEmbeddedHTML.
	 * @param adapterKey
	 * @param registerAdapters
	 */
	protected ModelQueryAdapterFactoryForEmbeddedHTML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 * @see AdapterFactory#adapt(INodeNotifier)
	 */
	public INodeAdapter adapt(INodeNotifier object) {
		if (object == null)
			return null;
		return adaptNew(object);
	}

	public Object clone() throws CloneNotSupportedException {

		return new ModelQueryAdapterFactoryForEmbeddedHTML(this.adapterKey, this.shouldRegisterAdapter);
	}
}