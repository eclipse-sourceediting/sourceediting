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
package org.eclipse.wst.sse.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.encoding.EncodingRule;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.Assert;


/**
 * This class reads a file and creates an Structured Model.
 */
public abstract class AbstractModelLoader implements ModelLoader {
	protected IDocumentLoader documentLoaderInstance;
	protected static final int encodingNameSearchLimit = 1000;

	/**
	 * AbstractLoader constructor also initializes encoding converter/mapper
	 */
	public AbstractModelLoader() {
		super();
	}

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	public List getAdapterFactories() {
		// abstract method returns none
		return new ArrayList(0);
	}

	protected void addFactories(IStructuredModel model, List factoryList) {
		Assert.isNotNull(model);
		IFactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry);
		if (factoryList != null) {
			Iterator iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				AdapterFactory factory = (AdapterFactory) iterator.next();
				registry.addFactory(factory);
			}
		}
	}

	/**
	 * This method should perform all the model initialization required before
	 * it contains content, namely, it should call newModel, the
	 * createNewStructuredDocument(), then add those adapter factories which
	 * must be set before content is applied. This method should be called by
	 * "load" method. (this is tentative API)
	 */
	public IStructuredModel createModel() {
		documentLoaderInstance = null;
		IStructuredModel model = newModel();
		IEncodedDocument structuredDocument = getDocumentLoader().createNewStructuredDocument();
		if (structuredDocument instanceof IStructuredDocument) {
			model.setStructuredDocument((IStructuredDocument) structuredDocument);
			addFactories(model, getAdapterFactories());
			//
			initEmbeddedType(model);
			// For types with propagating adapters, its important
			// that the propagating adapter be in place before the contents
			// are set.
			preLoadAdapt(model);
		}
		return model;
	}

	abstract public IDocumentLoader getDocumentLoader();

	/**
	 * Method initEmbeddedType. Nothing to do here in super class.
	 * 
	 * @param model
	 */
	protected void initEmbeddedType(IStructuredModel model) {
	}

	/**
	 * This method is used for cloning models.
	 */
	public IStructuredModel createModel(IStructuredModel oldModel) {
		documentLoaderInstance = null;
		IStructuredModel newModel = newModel();
		IStructuredDocument oldStructuredDocument = oldModel.getStructuredDocument();
		IStructuredDocument newStructuredDocument = oldStructuredDocument.newInstance();
		newModel.setStructuredDocument(newStructuredDocument);
		// NOTE: we DO NOT simply add the standard ones to the new model
		// addFactories(newModel, getAdapterFactories());
		// Now, we take the opportunity to add Factories from the oldModel's
		// registry to the new model's registry .. if they do not already
		// exist there.
		duplicateFactoryRegistry(newModel, oldModel);
		//addFactories(newModel, oldModel);
		initEmbeddedType(oldModel, newModel);
		// For types with propagating adapters, its important
		// that the propagating adapter be in place before the contents
		// are set.
		preLoadAdapt(newModel);
		return newModel;
	}

	/**
	 * Method initEmbeddedType. Nothing to do here in super class.
	 * 
	 * @param oldModel
	 * @param newModel
	 */
	protected void initEmbeddedType(IStructuredModel oldModel, IStructuredModel newModel) {
	}

	/**
	 * deprecated -- use EncodingRule form
	 */
	synchronized public void load(InputStream inputStream, IStructuredModel model, String encodingName, String lineDelimiter) throws UnsupportedEncodingException, java.io.IOException {
		// note we don't open the stream, so we don't close it
		// TEMP work around to maintain previous function,
		// until everyone can change to EncodingRule.FORCE_DEFAULT
		if (encodingName != null && encodingName.trim().length() == 0) {
			// redirect to new method
			load(inputStream, model, EncodingRule.FORCE_DEFAULT);
		}
		else {
			load(inputStream, model, EncodingRule.CONTENT_BASED);
		}
	}

	public void load(InputStream inputStream, IStructuredModel model, EncodingRule encodingRule) throws UnsupportedEncodingException, java.io.IOException {
		// note we don't open the stream, so we don't close it
		IEncodedDocument structuredDocument = model.getStructuredDocument();
		if (inputStream == null) {
			structuredDocument = getDocumentLoader().createNewStructuredDocument();
		}
		else {
			// assume's model has been initialized already with base location
			structuredDocument = getDocumentLoader().createNewStructuredDocument(model.getBaseLocation(), inputStream, encodingRule);
			// TODO: model's not designed for this!
			model.setStructuredDocument((IStructuredDocument) structuredDocument);
			((IStructuredDocument) structuredDocument).fireNewDocument(this);
		}
		documentLoaderInstance = null;

	}

	public void load(String filename, InputStream inputStream, IStructuredModel model, String junk, String dummy) throws UnsupportedEncodingException, java.io.IOException {
		IEncodedDocument structuredDocument = model.getStructuredDocument();
		if (inputStream == null) {
			structuredDocument = getDocumentLoader().createNewStructuredDocument();
		}
		else {
			structuredDocument = getDocumentLoader().createNewStructuredDocument(filename, inputStream);
		}
		// TODO: model's not designed for this!
		model.setStructuredDocument((IStructuredDocument) structuredDocument);
		((IStructuredDocument) structuredDocument).fireNewDocument(this);
		documentLoaderInstance = null;

	}

	/**
	 * required by interface, being declared here abstractly just as another
	 * reminder.
	 */
	abstract public IStructuredModel newModel();

	/**
	 * This method gets a fresh copy of the data, and repopulates the models
	 * ... by a call to setText on the structuredDocument. This method is
	 * needed in some cases where clients are sharing a model and then changes
	 * canceled. Say for example, one editor and several "displays" are
	 * sharing a model, if the editor is closed without saving changes, then
	 * the displays still need a model, but they should revert to the original
	 * unsaved version.
	 */
	synchronized public void reload(InputStream inputStream, IStructuredModel structuredModel) {
		documentLoaderInstance = null;
		try {
			// temp solution ... we should be able to do better (more efficient) in future. 
			// Adapters will (probably) need to be sensitive to the fact that the document instance changed
			// (by being life cycle listeners)
			load(inputStream, structuredModel, EncodingRule.CONTENT_BASED);

			//			// Note: we apparently read the data (and encoding) correctly
			//			// before, we just need to make sure we followed the same rule as
			//			// before.
			//			EncodingMemento previousMemento = structuredModel.getStructuredDocument().getEncodingMemento();
			//			EncodingRule previousRule = previousMemento.getEncodingRule();
			//			//IFile file = ResourceUtil.getFileFor(structuredModel);
			//			// Note: there's opportunity here for some odd behavior, if the
			//			// settings have changed from the first load to the reload. But,
			//			// hopefully,
			//			// will result in the intended behavior.
			//			Reader allTextReader = getDocumentLoader().readInputStream(inputStream, previousRule);
			//
			//			// TODO: avoid use of String instance
			//			getDocumentLoader().reload(structuredModel.getStructuredDocument(), allTextReader);
			//			// and now "reset" encoding memento to keep it current with the
			//			// one
			//			// that was just determined.
			//			structuredModel.getStructuredDocument().setEncodingMemento(getDocumentLoader().getEncodingMemento());
			//			structuredModel.setDirtyState(false);
			//			StructuredTextUndoManager undoMgr = structuredModel.getUndoManager();
			//			if (undoMgr != null) {
			//				undoMgr.reset();
			//			}
		}
		catch (UnsupportedEncodingException e) {
			// couldn't happen. The program has apparently
			// read the model once, and there'd be no reason the encoding
			// could not be used again.
			Logger.logException("Warning:  XMLLoader::reload.  This exception really should not have happened!! But will attemp to continue after dumping stack trace", e); //$NON-NLS-1$
			throw new Error("Program Error", e); //$NON-NLS-1$
		}
		catch (IOException e) {
			// couldn't happen. The program has apparently
			// read the model once, and there'd be no (common) reason it
			// couldn't be loaded again.
			Logger.logException("Warning:  XMLLoader::reload.  This exception really should not have happened!! But will attemp to continue after dumping stack trace", e); //$NON-NLS-1$
			throw new Error("Program Error", e); //$NON-NLS-1$
		}
	}

	/**
	 * There's nothing to do here in abstract class for initializing adapters.
	 * Subclasses can and should override this method and provide proper
	 * intialization (For example, to get DOM document and 'getAdapter' on it,
	 * so that the first node/notifier has the adapter on it.)
	 */
	protected void preLoadAdapt(IStructuredModel structuredModel) {
	}

	/**
	 * Normally, here in the abstact class, there's nothing to do, but we will
	 * reset text, since this MIGHT end up being called to recover from error
	 * conditions (e.g. IStructuredDocument exceptions) And, can be called by
	 * subclasses.
	 */
	public IStructuredModel reinitialize(IStructuredModel model) {
		// Note: the "minimumization" routines
		// of 'replaceText' allow many old nodes to pass through, when
		// really its assumed they are created anew.
		// so we need to use 'setText' (I think "setText' ends up
		// throwing a 'newModel' event though, that may have some
		// implications.
		model.getStructuredDocument().setText(this, model.getStructuredDocument().get());
		return model;
	}

	private void duplicateFactoryRegistry(IStructuredModel newModel, IStructuredModel oldModel) {
		List oldAdapterFactories = oldModel.getFactoryRegistry().getFactories();
		List newAdapterFactories = new ArrayList();
		Iterator oldListIterator = oldAdapterFactories.iterator();
		while (oldListIterator.hasNext()) {
			AdapterFactory oldAdapterFactory = (AdapterFactory) oldListIterator.next();
			// now "clone" the adapterfactory
			newAdapterFactories.add(oldAdapterFactory.copy());
		}
		// now that we have the "cloned" list, add to new model
		addFactories(newModel, newAdapterFactories);
	}

}
