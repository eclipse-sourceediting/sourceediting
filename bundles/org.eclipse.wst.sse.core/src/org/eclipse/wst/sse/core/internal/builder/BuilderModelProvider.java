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
package org.eclipse.wst.sse.core.internal.builder;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.ltk.builder.IBuilderModelProvider;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


public class BuilderModelProvider implements IBuilderModelProvider {
	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/modelprovider")); //$NON-NLS-1$ //$NON-NLS-2$

	private Map fDocuments = null;
	private IModelManager fModelManager;
	private Map fModels = null;

	public BuilderModelProvider(IModelManager manager) {

		super();
		fModelManager = manager;
	}

	/**
	 * Creates the document for this file; current done by retrieving it from
	 * an IStructuredModel
	 * 
	 * @param file
	 * @return
	 * @todo Generated comment
	 */
	private IStructuredDocument createDocument(IFile file) {

		// For now, we have to retrieve the IStructuredDocument from the
		// IStructuredModel.
		IStructuredModel model = getModel(file);
		if (model != null) {
			if (_debug) {
				System.out.println(getClass().getName() + " created IStructuredDocument for " + file.getFullPath().toString()); //$NON-NLS-1$
			}
			return model.getStructuredDocument();
		}
		return null;
	}


	/**
	 * Creates the model for this file. Currently, does it using the
	 * IModelManager from scratch. TODO: C4, create the model using the
	 * document (creating the document if needed, first)
	 * 
	 * @param file
	 * @return
	 * @todo Generated comment
	 */
	private IStructuredModel createModel(IFile file) {
		try {
			IStructuredModel model = null;
			if (_debug) {
				System.out.println(getClass().getName() + " created IStructuredModel for " + file.getFullPath().toString()); //$NON-NLS-1$
			}
			model = getModelManager().getExistingModelForRead(file);
			if (model != null) {
				// we have our reference, allow the
				// IModelManager to dispose if needed
				model.releaseFromRead();
			} else {
				model = getModelManager().createUnManagedStructuredModelFor(file);
			}
			return model;
		}
		// catch 'em all
		catch (Exception t) {
			Logger.log(Logger.WARNING, "Exception caught creating IStructuredModel for file " + file.getFullPath().toOSString() + ":" + t); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderModelProvider#getDocument(org.eclipse.core.resources.IFile)
	 */
	public IStructuredDocument getDocument(IFile file) {

		if (file == null) {
			return null;
		}
		IStructuredDocument document = (IStructuredDocument) getDocuments().get(file);
		if (document == null) {
			document = createDocument(file);
			if (document != null) {
				getDocuments().put(file, document);
			}
		}
		return document;
	}

	/**
	 * @return Returns the documents.
	 */
	public Map getDocuments() {

		if (fDocuments == null) {
			fDocuments = new WeakHashMap();
		}
		return fDocuments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderModelProvider#getModel(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getModel(IFile file) {

		if (file == null) {
			return null;
		}
		IStructuredModel model = (IStructuredModel) getModels().get(file);
		if (model == null) {
			model = createModel(file);
			if (model != null) {
				getModels().put(file, model);
			}
		}
		return model;
	}

	protected IModelManager getModelManager() {

		return fModelManager;
	}

	/**
	 * @return Returns the models.
	 */
	public Map getModels() {

		if (fModels == null) {
			fModels = new WeakHashMap();
		}
		return fModels;
	}

	public void release(IFile file) {

		if (_debug) {
			System.out.println(getClass().getName() + " released resources for " + file.getProjectRelativePath().toString()); //$NON-NLS-1$
		}
		getDocuments().remove(file);
		getModels().remove(file);
	}

	public void releaseAll() {

		if (_debug) {
			System.out.println(getClass().getName() + " released all resources"); //$NON-NLS-1$
		}
		getDocuments().clear();
		getModels().clear();
	}
}
