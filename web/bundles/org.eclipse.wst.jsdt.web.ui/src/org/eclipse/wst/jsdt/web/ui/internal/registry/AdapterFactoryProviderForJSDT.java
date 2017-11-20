/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.registry;

import org.eclipse.wst.html.core.internal.modelhandler.ModelHandlerForHTML;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JFaceNodeAdapterFactoryForJSDT;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.internal.util.Assert;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class AdapterFactoryProviderForJSDT implements AdapterFactoryProvider {
	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {
		// these are the main factories, on model's factory registry
		addContentBasedFactories(structuredModel);
		// -------
		// Must update/add to propagating adapters here too
		addPropagatingAdapters(structuredModel);
	}
	
	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (!(factory instanceof JFaceNodeAdapterFactoryForJSDT)) {
			factoryRegistry.removeFactoriesFor(IJFaceNodeAdapter.class);
			factory = new JFaceNodeAdapterFactoryForJSDT(IJFaceNodeAdapter.class, true);
			factoryRegistry.addFactory(factory);
		}
		
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
	}
	
	protected void addPropagatingAdapters(IStructuredModel structuredModel) {}
	
	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		// return (contentTypeDescription instanceof ModelHandlerForJSP);
		return (contentTypeDescription instanceof ModelHandlerForHTML);
	}
	
	public void reinitializeFactories(IStructuredModel structuredModel) {
		addAdapterFactories(structuredModel);
	}
}
