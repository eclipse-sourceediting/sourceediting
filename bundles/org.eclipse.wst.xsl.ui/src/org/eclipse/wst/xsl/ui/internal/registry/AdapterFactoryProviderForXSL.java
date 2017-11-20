/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - based on work for org.eclipse.wst.xml.ui
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.registry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.DOMObserver;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xsl.core.internal.modelhandler.ModelHandlerForXSL;
import org.eclipse.wst.xsl.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML;

/**
 * 
 */
public class AdapterFactoryProviderForXSL extends AdapterFactoryProviderForXML
		implements AdapterFactoryProvider {

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	@Override
	public void addAdapterFactories(IStructuredModel structuredModel) {

		// add the normal content based factories to model's registry
		addContentBasedFactories(structuredModel);
	}

	@Override
	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert
				.isNotNull(factoryRegistry,
						"Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;

		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactory();
			factoryRegistry.addFactory(factory);
		}

		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(structuredModel);
		if (modelQuery != null) {
			CMDocumentManager documentManager = modelQuery
					.getCMDocumentManager();
			if (documentManager != null) {
				IPreferenceStore store = XMLUIPlugin.getDefault()
						.getPreferenceStore();
				boolean useInferredGrammar = (store != null) ? store
						.getBoolean(XMLUIPreferenceNames.USE_INFERRED_GRAMMAR)
						: true;

				documentManager.setPropertyEnabled(
						CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
				documentManager.setPropertyEnabled(
						CMDocumentManager.PROPERTY_AUTO_LOAD, false);
				documentManager.setPropertyEnabled(
						CMDocumentManager.PROPERTY_USE_CACHED_RESOLVED_URI,
						true);
				DOMObserver domObserver = new DOMObserver(structuredModel);
				domObserver.setGrammarInferenceEnabled(useInferredGrammar);
				domObserver.init();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryProvider
	 * #
	 * isFor(org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler
	 * )
	 */
	@Override
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForXSL);
	}

	@Override
	public void reinitializeFactories(IStructuredModel structuredModel) {
		// nothing to do, since no embedded type
	}
}
