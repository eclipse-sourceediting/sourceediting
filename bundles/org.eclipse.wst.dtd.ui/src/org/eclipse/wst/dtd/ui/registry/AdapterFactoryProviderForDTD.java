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
package org.eclipse.wst.dtd.ui.registry;

import org.eclipse.wst.dtd.core.modelhandler.ModelHandlerForDTD;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * @author nitin
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AdapterFactoryProviderForDTD implements AdapterFactoryProvider {

	/**
	 *  
	 */
	public AdapterFactoryProviderForDTD() {
		super();
	}

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {
		IFactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		//		AdapterFactory factory = null;

		//		factory = factoryRegistry.getFactoryFor(JFaceNodeAdapter.class);
		//		if (factory == null) {
		//			factory = new JFaceNodeAdapterFactory(JFaceNodeAdapter.class,
		// true);
		//			factoryRegistry.addFactory(factory);
		//		}
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForDTD);
	}

	public void reinitializeFactories(IStructuredModel structuredModel) {
		// nothing to do, since no embedded type
	}
}
