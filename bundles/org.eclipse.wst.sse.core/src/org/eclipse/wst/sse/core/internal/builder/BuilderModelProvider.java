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
package org.eclipse.wst.sse.core.internal.builder;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.builder.IBuilderModelProvider;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


public class BuilderModelProvider implements IBuilderModelProvider {

	private Map fDocuments = null;
	private IModelManager fModelManager;
	private Map fModels = null;
	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/modelprovider")); //$NON-NLS-1$ //$NON-NLS-2$

	public BuilderModelProvider(IModelManager manager) {

		super();
		fModelManager = manager;
	}

	/**
	 * Creates the document for this file; current done by retrieving it from an IStructuredModel
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
	 * Creates the model for this file.  Currently, does it using the IModelManager from scratch.
	 * TODO: C4, create the model using the document (creating the document if needed, first)
	 * 
	 * @param file
	 * @return
	 * @todo Generated comment
	 */
	private IStructuredModel createModel(IFile file) {

		IStructuredModel model = null;
		try {
			if (_debug) {
				System.out.println(getClass().getName() + " created IStructuredModel for " + file.getFullPath().toString()); //$NON-NLS-1$
			}
			model = getModelManager().getExistingModelForRead(file);
			if (model != null) {
				// we have our reference, allow the
				// IModelManager to dispose if needed
				model.releaseFromRead();
			}
			else {
				model = getModelManager().createUnManagedStructuredModelFor(file);
			}
			return model;
		}
		catch (OutOfMemoryError e) {
			// This catch of no memory only marginally helps problem,
			// but thought I'd leave it in for now, just to see how
			// often its detected and recovers. The problem is that in only
			// helps
			// if occurs in this code. If instead it consumes most, but not
			// all of
			// memory, then the exception occurs elsewhere.

			// set the memory hoggers to null before doing
			// anything else.
			releaseAll();
			fModels = null;
			fDocuments = null;
			Logger.logException("Out of Memory Exception caught and models cleared for " + file.getFullPath(), e); //$NON-NLS-1$

		}
		// catch 'em all
		catch (Throwable t) {
			Logger.log(Logger.WARNING, "Exception caught creating IStructuredModel for file " + file.getFullPath().toOSString() + ":" + t); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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

	/**
	 * @return Returns the documents.
	 */
	public Map getDocuments() {

		if (fDocuments == null) {
			fDocuments = new WeakHashMap();
		}
		return fDocuments;
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
}
