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
package org.eclipse.jst.jsp.core.modelquery;

import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.XMLCatalogIdResolver;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;

public class ModelQueryAdapterFactoryForJSP extends AbstractAdapterFactory implements IModelStateListener {

	protected JSPModelQueryAdapterImpl modelQueryAdapterImpl;

	protected IStructuredModel stateNotifier = null;

	public ModelQueryAdapterFactoryForJSP() {
		this(ModelQueryAdapter.class, true);
	}

	/**
	 * ModelQueryAdapterFactoryForJSP constructor comment.
	 * 
	 * @param adapterKey
	 *            java.lang.Object
	 * @param registerAdapters
	 *            boolean
	 */
	public ModelQueryAdapterFactoryForJSP(Object key, boolean registerAdapters) {
		super(key, registerAdapters);
	}

	public AdapterFactory copy() {
		return new ModelQueryAdapterFactoryForJSP(this.adapterKey, this.shouldRegisterAdapter);
	}

	/**
	 * createAdapter method comment.
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {

		if (Debug.displayInfo)
			System.out.println("-----------------------ModelQueryAdapterFactoryForJSP.createAdapter" + target); //$NON-NLS-1$
		if (modelQueryAdapterImpl == null) {
			if (target instanceof XMLNode) {
				XMLNode xmlNode = (XMLNode) target;
				IStructuredModel model = stateNotifier = xmlNode.getModel();
				stateNotifier.addModelStateListener(this);
				CMDocumentCache cmDocumentCache = new CMDocumentCache();
				IdResolver resolver = new XMLCatalogIdResolver(model.getBaseLocation(), model.getResolver());

				ModelQuery modelQuery = new JSPModelQueryImpl(model, resolver);
				modelQuery.setEditMode(ModelQuery.EDIT_MODE_UNCONSTRAINED);
				modelQueryAdapterImpl = new JSPModelQueryAdapterImpl(cmDocumentCache, modelQuery, resolver);
			}
		}
		return modelQueryAdapterImpl;
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
	 * @see IModelStateListener#modelDirtyStateChanged(IStructuredModel,
	 *      boolean)
	 */
	public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
	}

	/**
	 * @see IModelStateListener#modelResourceDeleted(IStructuredModel)
	 */
	public void modelResourceDeleted(IStructuredModel model) {
	}

	/**
	 * @see IModelStateListener#modelResourceMoved(IStructuredModel,
	 *      IStructuredModel)
	 */
	public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
		// if oldModel != newModel, bad things might happen with the adapter
		stateNotifier.removeModelStateListener(this);
		stateNotifier = newModel;
		updateResolver(stateNotifier);
		stateNotifier.addModelStateListener(this);
	}


	public void release() {
		super.release();
		if (stateNotifier != null) {
			stateNotifier.removeModelStateListener(this);
		}
		stateNotifier = null;
		if (modelQueryAdapterImpl != null) {
			modelQueryAdapterImpl.release();
		}
	}

	protected void updateResolver(IStructuredModel model) {
		modelQueryAdapterImpl.setIdResolver(new XMLCatalogIdResolver(model.getBaseLocation(), model.getResolver()));
	}

}