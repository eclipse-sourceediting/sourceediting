/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.XMLCatalogIdResolver;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;

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

	public INodeAdapterFactory copy() {
		return new ModelQueryAdapterFactoryForJSP(getAdapterKey(), isShouldRegisterAdapter());
	}

	/**
	 * createAdapter method comment.
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (Debug.displayInfo)
			System.out.println("-----------------------ModelQueryAdapterFactoryForJSP.createAdapter" + target); //$NON-NLS-1$
		if (modelQueryAdapterImpl == null) {
			if (target instanceof IDOMNode) {
				IDOMNode xmlNode = (IDOMNode) target;
				IStructuredModel model = stateNotifier = xmlNode.getModel();
				if (model.getBaseLocation() != null) {
					stateNotifier.addModelStateListener(this);
				}

				org.eclipse.wst.sse.core.internal.util.URIResolver resolver = model.getResolver();
				if (Debug.displayInfo)
					System.out.println("----------------ModelQueryAdapterFactoryForJSP... baseLocation : " + resolver.getFileBaseLocation()); //$NON-NLS-1$

				/**
				 * XMLCatalogIdResolver currently requires a filesystem
				 * location string. Customarily this will be what is in the
				 * deprecated SSE URIResolver and required by the Common URI
				 * Resolver.
				 */
				URIResolver idResolver = null;
				if (resolver != null) {
					idResolver = new XMLCatalogIdResolver(resolver.getFileBaseLocation(), resolver);
				}
				else {
					/*
					 * 203649 - this block may be necessary due to ordering of
					 * setting the resolver into the model
					 */
					String baseLocation = null;
					String modelsBaseLocation = model.getBaseLocation();
					if (modelsBaseLocation != null) {
						File file = new Path(modelsBaseLocation).toFile();
						if (file.exists()) {
							baseLocation = file.getAbsolutePath();
						}
						else {
							IPath basePath = new Path(model.getBaseLocation());
							IResource derivedResource = null;
							if (basePath.segmentCount() > 1)
								derivedResource = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
							else
								derivedResource = ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0));
							IPath derivedPath = derivedResource.getLocation();
							if (derivedPath != null) {
								baseLocation = derivedPath.toString();
							}
							else {
								URI uri = derivedResource.getLocationURI();
								if (uri != null) {
									baseLocation = uri.toString();
								}
							}
						}
						if(baseLocation == null) {
							baseLocation = modelsBaseLocation;
						}
					}
					idResolver = new XMLCatalogIdResolver(baseLocation, null);
				}

				ModelQuery modelQuery = createModelQuery(model, idResolver);
				modelQuery.setEditMode(ModelQuery.EDIT_MODE_UNCONSTRAINED);
				modelQueryAdapterImpl = new JSPModelQueryAdapterImpl(new CMDocumentCache(), modelQuery, idResolver);
			}
		}
		return modelQueryAdapterImpl;
	}

	protected ModelQuery createModelQuery(IStructuredModel model, URIResolver resolver) {
		return new JSPModelQueryImpl(model, resolver);
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
		String baseLocation = model.getBaseLocation();
		IFile baseFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(model.getBaseLocation()));
		if (baseFile != null) {
			if (baseFile.getLocation() != null) {
				baseLocation = baseFile.getLocation().toString();
			}
			if (baseLocation == null && baseFile.getLocationURI() != null) {
				baseLocation = baseFile.getLocationURI().toString();
			}
			if (baseLocation == null) {
				baseLocation = baseFile.getFullPath().toString();
			}
		}
		else {
			baseLocation = model.getBaseLocation();
		}
		modelQueryAdapterImpl.setIdResolver(new XMLCatalogIdResolver(baseLocation, model.getResolver()));
	}

	public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub

	}

	public void modelReinitialized(IStructuredModel structuredModel) {
		updateResolver(structuredModel);

	}

}
