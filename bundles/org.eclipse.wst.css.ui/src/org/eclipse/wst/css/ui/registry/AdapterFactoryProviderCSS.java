/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.registry;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.css.core.modelhandler.ModelHandlerForCSS;
import org.eclipse.wst.css.ui.views.contentoutline.JFaceNodeAdapterFactoryCSS;
import org.eclipse.wst.css.ui.views.properties.CSSPropertySourceAdapterFactory;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;

public class AdapterFactoryProviderCSS implements AdapterFactoryProvider {
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForCSS);
	}

	public void addAdapterFactories(IStructuredModel structuredModel) {
		// add the normal content based factories to model's registry
		addContentBasedFactories(structuredModel);
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		AdapterFactory factory = null;

		factory = factoryRegistry.getFactoryFor(IPropertySource.class);
		if (factory == null) {
			factory = new CSSPropertySourceAdapterFactory(IPropertySource.class, true);
			factoryRegistry.addFactory(factory);
		}

		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactoryCSS(IJFaceNodeAdapter.class, true);
			factoryRegistry.addFactory(factory);
		}
	}

	public void reinitializeFactories(IStructuredModel structuredModel) {

	}

}