/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.modelquery;



import org.eclipse.wst.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapterImpl;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;


public class ModelQueryAdapterFactoryForXML extends AbstractAdapterFactory implements IModelStateListener {

	protected ModelQueryAdapterImpl modelQueryAdapterImpl;
	protected IStructuredModel stateNotifier = null;

	/**
	 * ModelQueryAdapterFactoryForXML constructor comment.
	 */
	public ModelQueryAdapterFactoryForXML() {
		this(ModelQueryAdapter.class, true);
	}

	/**
	 * ModelQueryAdapterFactoryForXML constructor comment.
	 * @param adapterKey java.lang.Object
	 * @param registerAdapters boolean
	 */
	public ModelQueryAdapterFactoryForXML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 * createAdapter method comment.
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {

		if (org.eclipse.wst.sse.core.util.Debug.displayInfo)
			System.out.println("-----------------------ModelQueryAdapterFactoryForXML.createAdapter" + target); //$NON-NLS-1$
		if (modelQueryAdapterImpl == null) {
			if (target instanceof XMLNode) {
				XMLNode xmlNode = (XMLNode) target;
				IStructuredModel model = stateNotifier = xmlNode.getModel();
				stateNotifier.addModelStateListener(this);
				String baseLocation = model.getBaseLocation();
				if (org.eclipse.wst.sse.core.util.Debug.displayInfo)
					System.out.println("----------------ModelQueryAdapterFactoryForXML... baseLocation : " + baseLocation); //$NON-NLS-1$

				CMDocumentCache cmDocumentCache = new CMDocumentCache();
				ModelQuery modelQuery = null;
				IdResolver idResolver = null;

				if (org.eclipse.wst.sse.core.util.Debug.displayInfo)
					System.out.println("********XMLModelQueryImpl"); //$NON-NLS-1$
				idResolver = new XMLCatalogIdResolver(baseLocation, model.getResolver());
				modelQuery = new XMLModelQueryImpl(cmDocumentCache, idResolver);

				// cs todo...
				// for now we create a CMDocumentCache on a 'per editor' basis
				// in the future we need to support a CMDocumentCache that is shared between editors
				// nsd comment: may not be appropriate depending on 
				CMDocumentManager documentManager = modelQuery.getCMDocumentManager();
				if (documentManager != null) {
					configureDocumentManager(documentManager);
				}
				modelQueryAdapterImpl = new ModelQueryAdapterImpl(cmDocumentCache, modelQuery, idResolver);
			}
		}
		return modelQueryAdapterImpl;
	}

	protected void configureDocumentManager(CMDocumentManager mgr) {
		// this depends on there being a CMDocumentRequesterFactory installed
		mgr.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, autoLoadCM());
	}

	protected boolean autoLoadCM() {
		// until the existence of a CMDocumentRequesterFactory to create the load requests,
		// return true
		return true;
	}

	protected void updateResolver(IStructuredModel model) {
		modelQueryAdapterImpl.setIdResolver(new XMLCatalogIdResolver(model.getBaseLocation(), model.getResolver()));
	}

	/**
	 * @see IModelStateListener#modelAboutToBeChanged(IStructuredModel)
	 */
	public void modelAboutToBeChanged(IStructuredModel model) {
	}

	/**
	 * @see IModelStateListener#modelChanged(IStructuredModel)
	 */
	public void modelChanged(IStructuredModel model) {
	}

	/**
	 * @see IModelStateListener#modelDirtyStateChanged(IStructuredModel, boolean)
	 */
	public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
	}

	/**
	 * @see IModelStateListener#modelResourceDeleted(IStructuredModel)
	 */
	public void modelResourceDeleted(IStructuredModel model) {
	}

	/**
	 * @see IModelStateListener#modelResourceMoved(IStructuredModel, IStructuredModel)
	 */
	public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
		stateNotifier.removeModelStateListener(this);
		stateNotifier = newModel;
		updateResolver(stateNotifier);
		stateNotifier.addModelStateListener(this);
	}

	public void release() {
		super.release();
		if (stateNotifier != null)
			stateNotifier.removeModelStateListener(this);
		stateNotifier = null;
		if (modelQueryAdapterImpl != null)
			modelQueryAdapterImpl.release();
	}

	public AdapterFactory copy() {

		return new ModelQueryAdapterFactoryForXML(this.adapterKey, this.shouldRegisterAdapter);
	}
}
