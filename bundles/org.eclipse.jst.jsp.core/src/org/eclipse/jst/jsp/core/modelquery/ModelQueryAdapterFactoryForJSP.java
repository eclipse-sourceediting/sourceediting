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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.contentmodel.tld.URIResolverProvider;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.DeferredTaglibSupport;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IModelLifecycleListener;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.XMLCatalogIdResolver;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;

public class ModelQueryAdapterFactoryForJSP extends AbstractAdapterFactory implements IModelStateListener {

	/**
	 * This class ensures that the tabligSupport field is always connected to
	 * the correct IDocument, should the IDocument in the Structured Model
	 * change.
	 */
	protected class TaglibSupportModelLifecycleListener implements IModelLifecycleListener {
		// had to change to 'false' when implicit job support added. 
		// somehow, during document.set("") the wrong rule was in effect. 
		// of course, we shouldn't being doing document set anyway, so
		// would like to re-consider. (may not need for V6, with other 
		// workarounds in place?)
		private static final boolean doForceReloadOnDocumentChange = false;

		public TaglibSupportModelLifecycleListener() {
			if (stateNotifier != null) {
				setDocument(stateNotifier.getStructuredDocument());
			}
		}

		private void clearDocumentInformation() {
			if (taglibSupport != null) {
				taglibSupport.setStructuredDocument(null);
				taglibSupport.clearCMDocumentCache();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.sse.model.IModelLifecycleListener#processPostModelEvent(com.ibm.sse.model.ModelLifecycleEvent)
		 */
		public void processPostModelEvent(ModelLifecycleEvent event) {
			if (event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {
//				DocumentChanged documentChangedEvent = (DocumentChanged) event;
//				IDocument oldDoc = documentChangedEvent.getOldDocument();
//				IDocument newDoc = documentChangedEvent.getNewDocument();
//				IDocument debugDoc = event.getModel().getStructuredDocument();
				setDocument(event.getModel().getStructuredDocument());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.sse.model.IModelLifecycleListener#processPreModelEvent(com.ibm.sse.model.ModelLifecycleEvent)
		 */
		public void processPreModelEvent(ModelLifecycleEvent event) {
			if (event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {
				clearDocumentInformation();
			}
		}

		/**
		 * @param event
		 */
		private void setDocument(IStructuredDocument document) {
			if (taglibSupport != null) {
				taglibSupport.setStructuredDocument(document);
				// force the reparsing of the IDocument so that taglibs
				// can be recorded and enabled; while this is expensive,
				// the lack of control over document and model creation
				// makes it necessary
				if (doForceReloadOnDocumentChange && document.getLength() > 0) {
					String contents = document.get();
					// don't allow any optimizations to be used
					document.set(""); //$NON-NLS-1$
					document.set(contents);
				}
			}
		}
	}

	protected TaglibSupportModelLifecycleListener fTaglibSupportModelLifecycleListener = null;

	protected JSPModelQueryAdapterImpl modelQueryAdapterImpl;

	// delay providing a URIResolver to the taglib support
	protected URIResolverProvider resolverProvider = new URIResolverProvider() {
		public URIResolver getResolver() {
			if (stateNotifier != null)
				return stateNotifier.getResolver();
			return null;
		}
	};
	protected IStructuredModel stateNotifier = null;
	protected DeferredTaglibSupport taglibSupport = null;

	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibsupport")); //$NON-NLS-1$ //$NON-NLS-2$

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
	public ModelQueryAdapterFactoryForJSP(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
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

				// connect the taglib support package to this model, deferring
				// the URIResolver requirement
				taglibSupport = new DeferredTaglibSupport();
				taglibSupport.setStructuredDocument(model.getStructuredDocument());
				taglibSupport.setResolverProvider(resolverProvider);
				fTaglibSupportModelLifecycleListener = new TaglibSupportModelLifecycleListener();
				model.addModelLifecycleListener(fTaglibSupportModelLifecycleListener);

				ModelQuery modelQuery = new JSPModelQueryImpl(model, taglibSupport, resolver);
				modelQuery.setEditMode(ModelQuery.EDIT_MODE_UNCONSTRAINED);
				modelQueryAdapterImpl = new JSPModelQueryAdapterImpl(cmDocumentCache, modelQuery, taglibSupport, resolver);
				if(_debug) {
					System.out.println("ModelQueryAdapterFactoryForJSP.createAdapter registered Taglib Support for model with ID " + model.getId() + " at " + model.getBaseLocation()); //$NON-NLS-2$ //$NON-NLS-1$
				}
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
		/**
		 * The taglib support will automatically use the new one, so trigger a
		 * reparse
		 */
		modelQueryAdapterImpl.setIdResolver(new XMLCatalogIdResolver(model.getBaseLocation(), model.getResolver()));
		fTaglibSupportModelLifecycleListener.setDocument(model.getStructuredDocument());
	}

}