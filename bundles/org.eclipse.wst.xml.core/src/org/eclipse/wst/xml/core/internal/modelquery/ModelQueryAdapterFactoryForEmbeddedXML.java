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
package org.eclipse.wst.xml.core.internal.modelquery;



import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;


public class ModelQueryAdapterFactoryForEmbeddedXML extends ModelQueryAdapterFactoryForXML {


	/**
	 * Constructor for ModelQueryAdapterFactoryForEmbeddedXML.
	 */
	public ModelQueryAdapterFactoryForEmbeddedXML() {
		this(ModelQueryAdapter.class, false);
	}

	/**
	 * Constructor for ModelQueryAdapterFactoryForEmbeddedXML.
	 * 
	 * @param adapterKey
	 * @param registerAdapters
	 */
	protected ModelQueryAdapterFactoryForEmbeddedXML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 * @see org.eclipse.wst.sse.core.INodeAdapterFactory#adapt(INodeNotifier)
	 */
	public INodeAdapter adapt(INodeNotifier object) {
		return adaptNew(object);
	}

	protected void configureDocumentManager(CMDocumentManager mgr) {
		super.configureDocumentManager(mgr);
		mgr.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
	}
}
