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
import org.eclipse.wst.common.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.DOMObserver;
import org.eclipse.wst.xml.ui.reconcile.ReconcilerAdapterFactoryForXML;
import org.eclipse.wst.xml.ui.views.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.views.properties.XMLPropertySourceAdapterFactory;

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
		// Must update/add to propagating adapter here too

		if (structuredModel instanceof XMLModel) {
			addPropagatingAdapters(structuredModel);
		}
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		IFactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		AdapterFactory factory = null;
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
					IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
					boolean useInferredGrammar = (store != null) ? store.getBoolean(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, IContentTypeIdentifier.ContentTypeID_SSEXML)) : true;

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

	protected void addPropagatingAdapters(IStructuredModel structuredModel) {
		AdapterFactory factory;
		XMLModel xmlModel = (XMLModel) structuredModel;
		XMLDocument document = xmlModel.getDocument();
		PropagatingAdapter propagatingAdapter = (PropagatingAdapter) document.getAdapterFor(PropagatingAdapter.class);
		if (propagatingAdapter != null) {
			// checking if we should bother adding this factory
			// if the preference says not to check validity, we don't bother
			// creating this factory
			// to improve performance...
			String contentTypeId = IContentTypeIdentifier.ContentTypeID_SSEXML;
			IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
			if (store.getString(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, contentTypeId)).equals(CommonEditorPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL)) {
				factory = new ReconcilerAdapterFactoryForXML();
				propagatingAdapter.addAdaptOnCreateFactory(factory);
				// (pa) perf:
				//propagatingAdapter.initializeForFactory(factory,
				// xmlModel.getDocument());
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
