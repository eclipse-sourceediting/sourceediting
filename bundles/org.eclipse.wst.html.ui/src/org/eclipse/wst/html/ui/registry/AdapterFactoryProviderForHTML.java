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
package org.eclipse.wst.html.ui.registry;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.html.core.modelhandler.ModelHandlerForHTML;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterFactoryForHTML;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.ui.internal.properties.XMLPropertySourceAdapterFactory;

/**
 * 
 */
public class AdapterFactoryProviderForHTML implements AdapterFactoryProvider {

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {

		// these are the normal edit side content based factories		
		addContentBasedFactories(structuredModel);
		// ===
		// Must update/add to propagating adapter here too
		if (structuredModel instanceof XMLModel) {
			addPropagatingAdapters(structuredModel);
		}
		// ===
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {

		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		IAdapterFactory factory = null;
		// == this list came from the previous "HTML only" list
		//		factory = factoryRegistry.getFactoryFor(ContentAssistAdapter.class);

		//		Doc Partion change: no more adapter factories for edit side functions

		//		if (factory == null) {
		//			factory = new HTMLContentAssistAdapterFactory();
		//			factoryRegistry.addFactory(factory);
		//		}

		//		factory = factoryRegistry.getFactoryFor(DoubleClickAdapter.class);
		//		if (factory == null) {
		//			factory = new DoubleClickAdapterFactory(DoubleClickAdapter.class, true);
		//			factoryRegistry.addFactory(factory);
		//		}
		//		// == New adapter factories for tagInfo
		//		factory = factoryRegistry.getFactoryFor(HoverHelpAdapter.class);
		//		if (factory == null) {
		//			factory = new HTMLHoverHelpAdapterFactory(HoverHelpAdapter.class, true);
		//			factoryRegistry.addFactory(factory);
		//		}
		//		factory = factoryRegistry.getFactoryFor(TagInfoProviderAdapter.class);
		//		if (factory == null) {
		//			factory = new JSTagInfoProviderAdapterFactory(TagInfoProviderAdapter.class, true);
		//			factoryRegistry.addFactory(factory);
		//		}
		// ==
		// == this list came from the previous 'for both XML and HTML' list
		factory = factoryRegistry.getFactoryFor(IPropertySource.class);
		if (factory == null) {
			factory = new XMLPropertySourceAdapterFactory();
			factoryRegistry.addFactory(factory);
		}
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactoryForHTML();
			factoryRegistry.addFactory(factory);
		}
		//		factory = factoryRegistry.getFactoryFor(com.ibm.sed.edit.adapters.LineStyleProvider.class);
		//		if (factory == null) {
		//			factory = new LineStyleProviderFactoryForHTML();
		//			factoryRegistry.addFactory(factory);
		//		}

		//		factory = factoryRegistry.getFactoryFor(ReconcilerAdapter.class);
		//		if (factory == null) {
		//			// can't use Propagating system since the Node tree is already built by now
		//			ReconcilerAdapterFactoryForXML reconcilerFactory = new ReconcilerAdapterFactoryForXML();
		//			factoryRegistry.addFactory(reconcilerFactory);
		//			reconcilerFactory.adaptAll((XMLModel) structuredModel);
		//		}
	}

	protected void addPropagatingAdapters(IStructuredModel structuredModel) {
//		XMLModel xmlModel = (XMLModel) structuredModel;
//		XMLDocument document = xmlModel.getDocument();
//		PropagatingAdapter propagatingAdapter = (PropagatingAdapter) document.getAdapterFor(PropagatingAdapter.class);
//		if (propagatingAdapter != null) {
			// no longer needs?			
			//			factory = new HTMLLineStyeUpdaterFactory();
			//			propagatingAdapter.addAdaptOnCreateFactory(factory);
			//			propagatingAdapter.initializeForFactory(factory, document);

			// checking if we should bother adding this factory
			// if the preference says not to check validity, we don't bother creating this factory
			// to improve performance...
			//if(structuredModel.getStructuredPreferenceStore().getPreferenceString(CommonPreferenceNames.EDITOR_VALIDATION_METHOD).equals(CommonPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL)) {
			//factory = new ReconcilerAdapterFactoryForXML();
			//propagatingAdapter.addAdaptOnCreateFactory(factory);
			// (pa) perf:
			//propagatingAdapter.initializeForFactory(factory, xmlModel.getDocument());
			//}

//		}
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForHTML);
	}

	public void reinitializeFactories(IStructuredModel structuredModel) {
		// nothing to do, since no embedded type
	}

}