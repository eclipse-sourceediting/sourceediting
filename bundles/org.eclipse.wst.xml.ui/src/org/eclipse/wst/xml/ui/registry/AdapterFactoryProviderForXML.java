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
package org.eclipse.wst.xml.ui.registry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.DOMObserver;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xml.ui.internal.properties.XMLPropertySourceAdapterFactory;

/**
 *  
 */
public class AdapterFactoryProviderForXML implements AdapterFactoryProvider {

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {

		// add the normal content based factories to model's registry
		addContentBasedFactories(structuredModel);
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;
		// == this list came from the previous "XML only" list

		// what was this still here? (6/4/03)
		// I commented out on 6/4/03) but may have been something "extra"
		// initializing
		// old content assist adapter unnecessarily?
		//factory =
		// factoryRegistry.getFactoryFor(com.ibm.sed.edit.adapters.ContentAssistAdapter.class);

		factory = factoryRegistry.getFactoryFor(IPropertySource.class);
		if (factory == null) {
			factory = new XMLPropertySourceAdapterFactory();
			factoryRegistry.addFactory(factory);
		}
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactory();
			factoryRegistry.addFactory(factory);
		}

		// cs... added for inferred grammar support
		//
		if (structuredModel != null) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(structuredModel);
			if (modelQuery != null) {
				CMDocumentManager documentManager = modelQuery.getCMDocumentManager();
				if (documentManager != null) {
					IPreferenceStore store = XMLUIPlugin.getDefault().getPreferenceStore();
					boolean useInferredGrammar = (store != null) ? store.getBoolean(XMLUIPreferenceNames.USE_INFERRED_GRAMMAR) : true;

					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, false);
					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_USE_CACHED_RESOLVED_URI, true);
					DOMObserver domObserver = new DOMObserver(structuredModel);
					domObserver.setGrammarInferenceEnabled(useInferredGrammar);
					domObserver.init();
				}
			}
		}
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForXML);
	}

	public void reinitializeFactories(IStructuredModel structuredModel) {
		// nothing to do, since no embedded type
	}
}
