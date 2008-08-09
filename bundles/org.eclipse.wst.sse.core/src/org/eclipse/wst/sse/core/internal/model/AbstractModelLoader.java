/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTagParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandlerExtension;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParserExtension;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.util.Assert;


/**
 * This class reads a file and creates an Structured Model.
 */
public abstract class AbstractModelLoader implements IModelLoader {
	protected static final int encodingNameSearchLimit = 1000;

	private static long computeMem() {
		for (int i = 0; i < 5; i++) {
			System.gc();
			System.runFinalization();
		}
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	private boolean DEBUG = false;
	protected IDocumentLoader documentLoaderInstance;

	/**
	 * AbstractLoader constructor also initializes encoding converter/mapper
	 */
	public AbstractModelLoader() {
		super();
	}

	protected void addFactories(IStructuredModel model, List factoryList) {
		Assert.isNotNull(model);
		FactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry, "IStructuredModel " + model.getId() + " has a null FactoryRegistry"); //$NON-NLS-1$ //$NON-NLS-2$
		if (factoryList != null) {
			Iterator iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) iterator.next();
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
			initEmbeddedTypePre(model, (IStructuredDocument) structuredDocument);
			initEmbeddedTypePost(model);
			// For types with propagating adapters, its important
			// that the propagating adapter be in place before the contents
			// are set.
			preLoadAdapt(model);
		}
		return model;
	}

	public IStructuredModel createModel(IStructuredDocument structuredDocument, String baseLocation, IModelHandler handler) {
		documentLoaderInstance = null;
		IStructuredModel model = newModel();
		model.setBaseLocation(baseLocation);
		
		// handler must be set early, incase a re-init is 
		// required during creation.
		model.setModelHandler(handler);
		
		addFactories(model, getAdapterFactories());
		// For types with propagating adapters, it's important
		// that the propagating adapter be in place before the contents
		// are set.
		preLoadAdapt(model);
		initEmbeddedTypePre(model, structuredDocument);

		model.setStructuredDocument(structuredDocument);
		//
		initEmbeddedTypePost(model);

		return model;
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
		if (newModel instanceof AbstractStructuredModel) {
			((AbstractStructuredModel) newModel).setContentTypeIdentifier(oldModel.getContentTypeIdentifier());
		}
		// addFactories(newModel, oldModel);
		initEmbeddedType(oldModel, newModel);
		// For types with propagating adapters, its important
		// that the propagating adapter be in place before the contents
		// are set.
		preLoadAdapt(newModel);
		return newModel;
	}

	private void duplicateFactoryRegistry(IStructuredModel newModel, IStructuredModel oldModel) {
		List oldAdapterFactories = oldModel.getFactoryRegistry().getFactories();
		List newAdapterFactories = new ArrayList();
		Iterator oldListIterator = oldAdapterFactories.iterator();
		while (oldListIterator.hasNext()) {
			INodeAdapterFactory oldAdapterFactory = (INodeAdapterFactory) oldListIterator.next();
			// now "clone" the adapterfactory
			newAdapterFactories.add(oldAdapterFactory.copy());
		}
		// now that we have the "cloned" list, add to new model
		addFactories(newModel, newAdapterFactories);
	}

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	public List getAdapterFactories() {
		// abstract method returns none
		return new ArrayList(0);
	}

	abstract public IDocumentLoader getDocumentLoader();

	/**
	 * Method initEmbeddedType, "pre"-stage. Nothing to do here in super class.
	 * 
	 * @param model
	 */
	protected void initEmbeddedTypePre(IStructuredModel model) {
	}

	/**
	 * Method initEmbeddedType, "pre"-stage. By default simply calls the
	 * version of this method that uses only the structured model.
	 * 
	 * @param model
	 *            the model for which to initialize
	 * @param structuredDocument
	 *            The structured document containing the text content for the
	 *            model, which may be a different instance than what is in the
	 *            model at this stage.
	 */
	protected void initEmbeddedTypePre(IStructuredModel model, IStructuredDocument structuredDocument) {
		initEmbeddedTypePre(model);
	}
		
	protected void initEmbeddedTypePost(IStructuredModel model) {
	}

	/**
	 * Method initEmbeddedType. Nothing to do here in super class.
	 * 
	 * @param oldModel
	 * @param newModel
	 */
	protected void initEmbeddedType(IStructuredModel oldModel, IStructuredModel newModel) {
	}

	public void load(IFile file, IStructuredModel model) throws IOException, CoreException {
		IEncodedDocument structuredDocument = model.getStructuredDocument();
		if (file == null)
			structuredDocument = getDocumentLoader().createNewStructuredDocument();
		else
			structuredDocument = getDocumentLoader().createNewStructuredDocument(file);

		// TODO: need to straighten out IEncodedDocument mess
		if (structuredDocument instanceof IStructuredDocument)
			transformInstance(model.getStructuredDocument(), (IStructuredDocument) structuredDocument);
		else
			model.getStructuredDocument().set(structuredDocument.get());

		// original hack
		// model.setStructuredDocument((IStructuredDocument)
		// structuredDocument);
		// ((IStructuredDocument) structuredDocument).fireNewDocument(this);
		documentLoaderInstance = null;
		// technicq of future
		// model.setStructuredDocument((IStructuredDocument)
		// structuredDocument);
		// documentLoaderInstance = null;
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
			// we want to move to this "set" method, but the 'fire' was needed
			// as
			// a work around for strucutredModel not handling 'set' right, but
			// that 'fireNewDocument' method was causing unbalance
			// "aboutToChange" and "changed"
			// events.
			// model.setStructuredDocument((IStructuredDocument)
			// structuredDocument);
			// ((IStructuredDocument)
			// structuredDocument).fireNewDocument(this);
			model.getStructuredDocument().set(structuredDocument.get());

		}
		documentLoaderInstance = null;

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

	public void load(String filename, InputStream inputStream, IStructuredModel model, String junk, String dummy) throws UnsupportedEncodingException, java.io.IOException {

		long memoryUsed = 0;
		if (DEBUG) {
			memoryUsed = computeMem();
			System.out.println("measuring heap memory for " + filename); //$NON-NLS-1$
			// System.out.println("heap memory used before load: " +
			// memoryUsed);
		}

		// during an initial load, we expect the olddocument to be empty
		// during re-load, however, it would be full.
		IEncodedDocument newstructuredDocument = null;
		IEncodedDocument oldStructuredDocument = model.getStructuredDocument();

		// get new document
		if (inputStream == null) {
			newstructuredDocument = getDocumentLoader().createNewStructuredDocument();
		}
		else {
			newstructuredDocument = getDocumentLoader().createNewStructuredDocument(filename, inputStream);
		}
		if (DEBUG) {
			long memoryAtEnd = computeMem();
			// System.out.println("heap memory used after loading new
			// document: " + memoryAtEnd);
			System.out.println("    heap memory implied used by document: " + (memoryAtEnd - memoryUsed)); //$NON-NLS-1$
		}


		// TODO: need to straighten out IEncodedDocument mess
		if (newstructuredDocument instanceof IStructuredDocument) {
			transformInstance((IStructuredDocument) oldStructuredDocument, (IStructuredDocument) newstructuredDocument);
		}
		else {
			// we don't really expect this case, just included for safety
			oldStructuredDocument.set(newstructuredDocument.get());
		}
		// original hack
		// model.setStructuredDocument((IStructuredDocument)
		// structuredDocument);
		// ((IStructuredDocument) structuredDocument).fireNewDocument(this);
		documentLoaderInstance = null;
		// technicq of future
		// model.setStructuredDocument((IStructuredDocument)
		// structuredDocument);
		// documentLoaderInstance = null;
		if (DEBUG) {
			long memoryAtEnd = computeMem();
			// System.out.println("heap memory used after setting to model: "
			// + memoryAtEnd);
			System.out.println("    heap memory implied used by document and model: " + (memoryAtEnd - memoryUsed)); //$NON-NLS-1$
		}

	}

	/**
	 * required by interface, being declared here abstractly just as another
	 * reminder.
	 */
	abstract public IStructuredModel newModel();

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
			// temp solution ... we should be able to do better (more
			// efficient) in future.
			// Adapters will (probably) need to be sensitive to the fact that
			// the document instance changed
			// (by being life cycle listeners)
			load(inputStream, structuredModel, EncodingRule.CONTENT_BASED);

			// // Note: we apparently read the data (and encoding) correctly
			// // before, we just need to make sure we followed the same rule
			// as
			// // before.
			// EncodingMemento previousMemento =
			// structuredModel.getStructuredDocument().getEncodingMemento();
			// EncodingRule previousRule = previousMemento.getEncodingRule();
			// //IFile file = ResourceUtil.getFileFor(structuredModel);
			// // Note: there's opportunity here for some odd behavior, if the
			// // settings have changed from the first load to the reload.
			// But,
			// // hopefully,
			// // will result in the intended behavior.
			// Reader allTextReader =
			// getDocumentLoader().readInputStream(inputStream, previousRule);
			//
			// // TODO: avoid use of String instance
			// getDocumentLoader().reload(structuredModel.getStructuredDocument(),
			// allTextReader);
			// // and now "reset" encoding memento to keep it current with the
			// // one
			// // that was just determined.
			// structuredModel.getStructuredDocument().setEncodingMemento(getDocumentLoader().getEncodingMemento());
			// structuredModel.setDirtyState(false);
			// StructuredTextUndoManager undoMgr =
			// structuredModel.getUndoManager();
			// if (undoMgr != null) {
			// undoMgr.reset();
			// }
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
	 * this work is done better elsewhere, but done here for this version to
	 * reduce changes. especially since the need for it should go away once we
	 * no longer need to re-use old document instance.
	 */
	private void transformInstance(IStructuredDocument oldInstance, IStructuredDocument newInstance) {
		/**
		 * https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4920
		 * 
		 * JSP taglib support broken, correct by duplicating extended setup
		 * information (BlockTagParser extension,
		 * StructuredDocumentRegionParser extensions)
		 */
		RegionParser oldParser = oldInstance.getParser();
		RegionParser newParser = newInstance.getParser().newInstance();
		// Register all of the old StructuredDocumentRegionHandlers on the new
		// parser
		if (oldParser instanceof StructuredDocumentRegionParserExtension && newParser instanceof StructuredDocumentRegionParserExtension) {
			List oldHandlers = ((StructuredDocumentRegionParserExtension) oldParser).getStructuredDocumentRegionHandlers();
			for (int i = 0; i < oldHandlers.size(); i++) {
				StructuredDocumentRegionHandler handler = ((StructuredDocumentRegionHandler) oldHandlers.get(i));
				if (handler instanceof StructuredDocumentRegionHandlerExtension) {
					/**
					 * Skip the transferring here, the handler will do this
					 * after everything else but the source is transferred.
					 */
				}
				else {
					((StructuredDocumentRegionParser) oldParser).removeStructuredDocumentRegionHandler(handler);
					((StructuredDocumentRegionParser) newParser).addStructuredDocumentRegionHandler(handler);
					handler.resetNodes();
				}
			}
		}
		// Add any global BlockMarkers to the new parser
		if (oldParser instanceof BlockTagParser && newParser instanceof BlockTagParser) {
			List oldBlockMarkers = ((BlockTagParser) oldParser).getBlockMarkers();
			for (int i = 0; i < oldBlockMarkers.size(); i++) {
				BlockMarker blockMarker = ((BlockMarker) oldBlockMarkers.get(i));
				if (blockMarker.isGlobal()) {
					((BlockTagParser) newParser).addBlockMarker(blockMarker);
				}
			}
		}

		((BasicStructuredDocument) oldInstance).setParser(newParser);

		((BasicStructuredDocument) oldInstance).setReParser(newInstance.getReParser().newInstance());

		if (newInstance.getDocumentPartitioner() instanceof StructuredTextPartitioner) {
			StructuredTextPartitioner partitioner = null;
			if (oldInstance instanceof IDocumentExtension3 && newInstance instanceof IDocumentExtension3) {
				partitioner = ((StructuredTextPartitioner) ((IDocumentExtension3) newInstance).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING));
				if (partitioner != null) {
					partitioner = (StructuredTextPartitioner) partitioner.newInstance();
				}
				((IDocumentExtension3) oldInstance).setDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, partitioner);
			}
			if (partitioner == null) {
				partitioner = (StructuredTextPartitioner) ((StructuredTextPartitioner) newInstance.getDocumentPartitioner()).newInstance();
				oldInstance.setDocumentPartitioner(partitioner);
			}
			if (partitioner != null) {
				partitioner.connect(oldInstance);
			}
		}

		String existingLineDelimiter = null;
		try {
			existingLineDelimiter = newInstance.getLineDelimiter(0);
		}
		catch (BadLocationException e) {
			// if empty file, assume platform default
			// TODO: should be using user set preference, per content type?
			existingLineDelimiter = System.getProperty("line.separator"); //$NON-NLS-1$
		}

		oldInstance.setLineDelimiter(existingLineDelimiter); //$NON-NLS-1$);
		if (newInstance.getEncodingMemento() != null) {
			oldInstance.setEncodingMemento((EncodingMemento) newInstance.getEncodingMemento().clone());
		}

		/**
		 * https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4920
		 * 
		 * JSP taglib support broken, correct by duplicating extended setup
		 * information (BlockTagParser extension,
		 * StructuredDocumentRegionParser extensions)
		 */
		if (oldParser instanceof StructuredDocumentRegionParserExtension && newParser instanceof StructuredDocumentRegionParserExtension) {
			List oldHandlers = ((StructuredDocumentRegionParserExtension) oldParser).getStructuredDocumentRegionHandlers();
			for (int i = 0; i < oldHandlers.size(); i++) {
				StructuredDocumentRegionHandler handler = ((StructuredDocumentRegionHandler) oldHandlers.get(i));
				if (handler instanceof StructuredDocumentRegionHandlerExtension) {
					StructuredDocumentRegionHandlerExtension handlerExtension = (StructuredDocumentRegionHandlerExtension) handler;
					handlerExtension.setStructuredDocument(oldInstance);
				}
			}
		}
		String holdString = newInstance.get();
		newInstance = null;
		oldInstance.set(holdString);
	}
}
