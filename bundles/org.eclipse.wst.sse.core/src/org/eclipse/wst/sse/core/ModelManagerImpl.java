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
package org.eclipse.wst.sse.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.common.encoding.CodedIO;
import org.eclipse.wst.common.encoding.CodedStreamCreator;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.common.encoding.ContentBasedPreferenceGateway;
import org.eclipse.wst.common.encoding.EncodingMemento;
import org.eclipse.wst.common.encoding.EncodingRule;
import org.eclipse.wst.common.encoding.NullMemento;
import org.eclipse.wst.common.encoding.exceptions.MalformedOutputExceptionWithDetail;
import org.eclipse.wst.sse.core.document.DocumentReader;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.nls.ResourceHandler1;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.core.util.ProjectResolver;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.core.util.Utilities;



/**
 * Not intended to be subclassed.
 * 
 * This class is responsible for creating, retriving, and caching
 * StructuredModels It retrieves the cached objects by an id which is
 * typically a String representing the resources URI. Note: Its important that
 * all clients that share a resource do so using <b>identical </b>
 * identifiers, or else different instances will be created and retrieved,
 * even if they all technically point to the same resource on the file system.
 * This class also provides a convenient place to register Model Loaders and
 * Dumpers based on 'type'.
 */
class ModelManagerImpl implements IModelManager {

	static class EnumeratedModelIds implements Enumeration {

		Enumeration fSharedObjectKeys;

		protected EnumeratedModelIds(Dictionary sharedObjects) {

			if (sharedObjects == null) {
				// if no shared objects yet, return empty enumeration
				fSharedObjectKeys = null;
			} else {
				fSharedObjectKeys = sharedObjects.keys();
			}
		}

		public boolean hasMoreElements() {

			boolean result = false;
			if (fSharedObjectKeys != null)
				result = fSharedObjectKeys.hasMoreElements();
			return result;
		}

		public Object nextElement() {

			if (fSharedObjectKeys == null)
				throw new NoSuchElementException();
			return fSharedObjectKeys.nextElement();
		}
	}

	static class ReadEditType {

		//private String fType;

		ReadEditType(String type) {

			//fType = type;
		}
	}

	/**
	 * A Data class to track our shared objects
	 */
	static class SharedObject {

		int referenceCountForEdit;
		int referenceCountForRead;
		IStructuredModel theSharedObject;

		SharedObject(IStructuredModel sharedObject) {

			theSharedObject = sharedObject;
			referenceCountForRead = 0;
			referenceCountForEdit = 0;
		}
	}

	private static final String IMODELMANAGER_TRACE_CATEGORY = "IModelManager"; //$NON-NLS-1$

	/**
	 * Our singleton instance
	 */
	private static ModelManagerImpl instance;
	private final static int READ_BUFFER_SIZE = 4096;

	synchronized static IModelManager getInstance() {

		if (instance == null) {
			instance = new ModelManagerImpl();
		}
		return instance;
	}

	private final ReadEditType EDIT = new ReadEditType("edit"); //$NON-NLS-1$
	/**
	 * Our cache of managed objects
	 */
	private Dictionary fManagedObjects;

	private ModelHandlerRegistry fModelHandlerRegistry;
	private Object[] fModelManagerListeners;
	private int modelManagerStateChanging;
	private final ReadEditType READ = new ReadEditType("read"); //$NON-NLS-1$

	ModelManagerImpl() {

		super();
		fManagedObjects = new Hashtable();
	}

	private IStructuredModel _commonCreateModel(InputStream inputStream, String id, IModelHandler handler, URIResolver resolver, ReadEditType rwType, EncodingRule encodingRule) throws IOException {

		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		IStructuredModel model = null;
		if (sharedObject == null) {
			try {
				model = _commonCreateModel(id, handler, resolver);
				ModelLoader loader = handler.getModelLoader();
				loader.load(Utilities.getMarkSupportedStream(inputStream), model, encodingRule);
			} catch (ResourceInUse e) {
				// impossible, since we've already found
				handleProgramError(e);
			}
			if (model != null) {
				// add to our cache
				sharedObject = new SharedObject(model);
				_initCount(sharedObject, rwType);
				fManagedObjects.put(id, sharedObject);
			}
		} else {
			// if shared object is initially in our cache, then simply
			// increment its ref count,
			// and return the object.
			_incrCount(sharedObject, rwType);
		}
		// we expect to always return something
		Assert.isNotNull(sharedObject, "Program Error: no model recorded for id " + id); //$NON-NLS-1$
		// note: clients must call release for each time they call get.
		return sharedObject.theSharedObject;
	}

	private IStructuredModel _commonCreateModel(InputStream inputStream, String id, IModelHandler handler, URIResolver resolver, ReadEditType rwType, String encoding, String lineDelimiter) throws IOException {

		if (id == null) {
			throw new IllegalArgumentException("Program Error: id may not be null"); //$NON-NLS-1$
		}
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		IStructuredModel model = null;
		if (sharedObject == null) {
			try {
				model = _commonCreateModel(id, handler, resolver);
				ModelLoader loader = handler.getModelLoader();
				loader.load(id, Utilities.getMarkSupportedStream(inputStream), model, encoding, lineDelimiter);
			} catch (ResourceInUse e) {
				// impossible, since we've already found
				handleProgramError(e);
			}
			if (model != null) {
				// add to our cache
				sharedObject = new SharedObject(model);
				_initCount(sharedObject, rwType);
				fManagedObjects.put(id, sharedObject);
			}
		} else {
			// if shared object is initially in our cache, then simply
			// increment its ref count,
			// and return the object.
			_incrCount(sharedObject, rwType);
		}
		// we expect to always return something
		Assert.isNotNull(sharedObject, "Program Error: no model recorded for id " + id); //$NON-NLS-1$
		// note: clients must call release for each time they call get.
		return sharedObject.theSharedObject;
	}

	private IStructuredModel _commonCreateModel(String id, IModelHandler handler, URIResolver resolver) throws ResourceInUse {

		Assert.isNotNull(handler, "model handler can not be null"); //$NON-NLS-1$
		ModelLoader loader = handler.getModelLoader();
		IStructuredModel result = loader.createModel();
		// in the past, id was null for "unmanaged" case, so we won't
		// try and set it
		if (id != null) {
			result.setId(id);
		}
		result.setModelHandler(handler);
		result.setResolver(resolver);
		// some obvious redunancy here that maybe could be improved
		// in future, but is necessary for now
		result.setBaseLocation(id);
		if (resolver != null) {
			resolver.setFileBaseLocation(id);
		}
		addFactories(result, handler);
		return result;
	}

	private IStructuredModel _commonGetModel(IFile iFile, ReadEditType rwType, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel model = null;

		if (iFile != null && iFile.exists()) {
			String id = calculateId(iFile);
			IModelHandler handler = calculateType(iFile);
			URIResolver resolver = calculateURIResolver(iFile);
			InputStream inputStream = Utilities.getMarkSupportedStream(iFile.getContents(true));
			try {
				model = _commonCreateModel(inputStream, id, handler, resolver, rwType, encodingRule);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}

		return model;
	}

	private IStructuredModel _commonGetModel(IFile iFile, ReadEditType rwType, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {
		String id = calculateId(iFile);
		IModelHandler handler = calculateType(iFile);
		URIResolver resolver = calculateURIResolver(iFile);
		IStructuredModel model = _commonGetModel(iFile, id, handler, resolver, rwType, encoding, lineDelimiter);

		return model;
	}

	private IStructuredModel _commonGetModel(IFile file, String id, IModelHandler handler, URIResolver resolver, ReadEditType rwType, String encoding, String lineDelimiter) throws IOException, CoreException {
		if (id == null)
			throw new IllegalArgumentException("Program Error: id may not be null"); //$NON-NLS-1$

		IStructuredModel model = null;

		if (file != null && file.exists()) {
			SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
			if (sharedObject == null) {
				model = FileBufferModelManager.getInstance().getModel(file);
				if (model != null) {
					// add to our cache
					sharedObject = new SharedObject(model);
					_initCount(sharedObject, rwType);
					fManagedObjects.put(id, sharedObject);
				}
			} else {
				// if shared object is initially in our cache, then simply
				// increment its ref count,
				// and return the object.
				_incrCount(sharedObject, rwType);
			}

			// we expect to always return something
			Assert.isNotNull(sharedObject, "Program Error: no model recorded for id " + id); //$NON-NLS-1$

			// note: clients must call release for each time they call get.
			model = sharedObject.theSharedObject;
		}

		return model;
	}

	private SharedObject _commonNewModel(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		// let's check to see if resource already exists, either in our cache,
		// or on the system
		String id = calculateId(iFile);
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		IStructuredModel aSharedModel = null;
		// if shared object is null, then we do not have it already, which is
		// as normally expected.
		if (sharedObject == null) {
			// if not in cache, see if we can retrieve it
			aSharedModel = FileBufferModelManager.getInstance().getModel(iFile);
		} else {
			// if sharedObject is not null, then
			// someone has asked us to create a new model for a given id, but
			// it in fact
			// is already in our cache with that id. In this case we will
			// throw
			// an "in use" exception,
			// (unless force is set to true). Because, to do otherwise we will
			// basically be
			// over writing a model that another client is already using. Not
			// nice.
			if (!force) {
				throw new ResourceInUse();
			}
		}
		// if we get here, and result (and shared object) are still null,
		// then all is ok, and we can create it,
		if (aSharedModel == null) {
			aSharedModel = FileBufferModelManager.getInstance().getModel(iFile);
			// rembember, don't set 'true' in model init, since that's always
			// used,
			// even when not new. 'new' is intended to mean "there is not yet
			// a
			// file" for the model.
			aSharedModel.setNewState(true);
			sharedObject = addToCache(id, aSharedModel);
			// when resource is provided, we can set
			// synchronization stamp ... otherwise client should
			// Note: one client which does this is FileModelProvider.
			aSharedModel.resetSynchronizationStamp(iFile);
		} else {
			// if result is not null, then we have to check
			// if 'force' was false before deciding to
			// throw an already exists exception.
			if (force) {
				sharedObject = addToCache(id, aSharedModel);
				// when resource is provided, we can set
				// synchronization stamp ... otherwise client should
				// Note: one client which does this is FileModelProvider.
				aSharedModel.resetSynchronizationStamp(iFile);
			} else {
				throw new ResourceAlreadyExists();
			}
		}
		return sharedObject;
	}

	public IStructuredModel _getModelFor(IStructuredDocument document, ReadEditType accessType) {
		IStructuredModel model = null;
		String id = FileBufferModelManager.getInstance().calculateId(document);
		Assert.isNotNull(id, "unknown IStructuredDocument " + document);
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			sharedObject = (SharedObject) fManagedObjects.get(id);
			_incrCount(sharedObject, accessType);
			model = sharedObject.theSharedObject;
		} else {
			model = FileBufferModelManager.getInstance().getModel(document);
			sharedObject = new SharedObject(model);
			_initCount(sharedObject, accessType);
			fManagedObjects.put(id, sharedObject);
		}
		return model;
	}

	private void _incrCount(SharedObject sharedObject, ReadEditType type) {
		if (type == READ) {
			sharedObject.referenceCountForRead++;
		} else if (type == EDIT) {
			sharedObject.referenceCountForEdit++;
		} else
			throw new IllegalArgumentException();
	}

	private void _initCount(SharedObject sharedObject, ReadEditType type) {
		if (type == READ) {
			sharedObject.referenceCountForRead = 1;
		} else if (type == EDIT) {
			sharedObject.referenceCountForEdit = 1;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * This API allows clients to declare that they are about to make a
	 * "massive" change one or more models. This change might be in terms of
	 * content or it might be in terms of the model id or base location. Note
	 * that in the case of embedded calls, notification to listners is sent
	 * only once. The method isModelStateChanging can be used by a client to
	 * determine if the model is already in a change sequence.
	 */
	public synchronized void aboutToChangeModels() {
		// notice this is just a public avenue to our protected method
		fireModelsAboutToBeChanged();
	}

	protected void addFactories(IStructuredModel model, IModelHandler handler) {
		Assert.isNotNull(model, "model can not be null"); //$NON-NLS-1$
		Assert.isNotNull(handler, "model handler can not be null"); //$NON-NLS-1$
		IFactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry, "Factory Registry can not be null"); //$NON-NLS-1$
		List factoryList = handler.getAdapterFactories();
		addFactories(model, factoryList);
	}

	protected void addFactories(IStructuredModel model, List factoryList) {
		Assert.isNotNull(model, "model can not be null"); //$NON-NLS-1$
		IFactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry, "Factory Registry can not be null"); //$NON-NLS-1$
		// Note: we add all of them from handler, even if
		// already exists. May need to reconsider this.
		if (factoryList != null) {
			Iterator iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				AdapterFactory factory = (AdapterFactory) iterator.next();
				registry.addFactory(factory);
			}
		}
	}

	public synchronized void addModelManagerListener(IModelManagerListener listener) {

		if (!Utilities.contains(fModelManagerListeners, listener)) {
			int oldSize = 0;
			if (fModelManagerListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fModelManagerListeners.length;
			}
			int newSize = oldSize + 1;
			Object[] newListeners = new Object[newSize];
			if (fModelManagerListeners != null) {
				System.arraycopy(fModelManagerListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fModelManagerListeners = newListeners;
			//
		}
	}

	private SharedObject addToCache(String id, IStructuredModel aSharedModel) {

		SharedObject sharedObject;
		sharedObject = new SharedObject(aSharedModel);
		fManagedObjects.put(id, sharedObject);
		return sharedObject;
	}

	/**
	 * Calculate id provides a common way to determine the id from the input
	 * ... needed to get and save the model. It is a simple class utility, but
	 * is an instance method so can be accessed via interface.
	 */
	public String calculateId(IFile file) {
		return FileBufferModelManager.getInstance().calculateId(file);
	}

	/**
	 * CalculateId provides a common way to determine the id from the provided
	 * filename.
	 */
	public String calculateId(String filename) {

		// providing common method for consistency.
		// May eventually need to "clean up"
		// any initial "file://" protocols, etc., but currently don't
		// know of anyone doing that.
		String id = filename;
		return id;
	}

	protected IModelHandler calculateType(IFile iFile) throws CoreException {

		// IModelManager mm = ((ModelManagerPlugin)
		// Platform.getPlugin(ModelManagerPlugin.ID)).getModelManager();
		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler cd = cr.getHandlerFor(iFile);
		return cd;
	}

	private IModelHandler calculateType(String filename, InputStream inputStream) throws IOException {

		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler cd = cr.getHandlerFor(filename, inputStream);
		return cd;
	}

	/**
	 *  
	 */
	private URIResolver calculateURIResolver(IFile file) {

		// Note: see comment in plugin.xml for potentially
		// breaking change in behavior.

		IProject project = file.getProject();
		URIResolver resolver = (URIResolver) project.getAdapter(URIResolver.class);
		if (resolver == null)
			resolver = new ProjectResolver(project);
		resolver.setFileBaseLocation(file.getLocation().toString());
		return resolver;
	}

	/**
	 * This API allows a client controlled way of notifying all
	 * IModelManagerEvent listners that several models have changed. This
	 * method is a matched pair to aboutToChangeModels, and must be called
	 * after aboutToChangeModel ... or some listeners could be left waiting
	 * indefinitely for the changed event. So, its suggested that
	 * changedModels always be in a finally clause. Likewise, a client should
	 * never call changedModel without calling aboutToChangeModel first. In
	 * the case of embedded calls, the notification is just sent once.
	 */
	public synchronized void changedModels() {

		// notice this is just a public avenue to our protected method
		fireModelsChanged();
	}

	/*
	 * Note: This method appears in both ModelManagerImpl and JSEditor (with
	 * just a minor difference). They should be kept the same.
	 * 
	 * @deprecated - handled by platform
	 */
	private void convertLineDelimiters(IDocument document, IFile iFile) throws CoreException {
		// Note: calculateType(iFile) returns a default xml model handler if
		// content type is null.
		String contentTypeId = calculateType(iFile).getAssociatedContentTypeId();
		String endOfLineCode = ContentBasedPreferenceGateway.getPreferencesString(contentTypeId, CommonEncodingPreferenceNames.END_OF_LINE_CODE);
		// endOfLineCode == null means the content type does not support this
		// function (e.g. DTD)
		// endOfLineCode == "" means no translation
		if (endOfLineCode != null && endOfLineCode.length() > 0) {
			String lineDelimiterToUse = System.getProperty("line.separator"); //$NON-NLS-1$
			if (endOfLineCode.equals(CommonEncodingPreferenceNames.CR))
				lineDelimiterToUse = CommonEncodingPreferenceNames.STRING_CR;
			else if (endOfLineCode.equals(CommonEncodingPreferenceNames.LF))
				lineDelimiterToUse = CommonEncodingPreferenceNames.STRING_LF;
			else if (endOfLineCode.equals(CommonEncodingPreferenceNames.CRLF))
				lineDelimiterToUse = CommonEncodingPreferenceNames.STRING_CRLF;

			TextEdit multiTextEdit = new MultiTextEdit();
			int lineCount = document.getNumberOfLines();
			try {
				for (int i = 0; i < lineCount; i++) {
					IRegion lineInfo = document.getLineInformation(i);
					int lineStartOffset = lineInfo.getOffset();
					int lineLength = lineInfo.getLength();
					int lineEndOffset = lineStartOffset + lineLength;

					if (i < lineCount - 1) {
						String currentLineDelimiter = document.getLineDelimiter(i);
						if (currentLineDelimiter != null && currentLineDelimiter.compareTo(lineDelimiterToUse) != 0)
							multiTextEdit.addChild(new ReplaceEdit(lineEndOffset, currentLineDelimiter.length(), lineDelimiterToUse));
					}
				}

				if (multiTextEdit.getChildrenSize() > 0)
					multiTextEdit.apply(document);
			} catch (BadLocationException exception) {
				throw new SourceEditingRuntimeException(exception);
			}
		}
	}

	/**
	 * this used to be in loader, but has been moved here
	 */
	protected IStructuredModel copy(IStructuredModel model, String newId) throws ResourceInUse {

		IStructuredModel newModel = null;
		IStructuredModel oldModel = model;
		IModelHandler modelHandler = oldModel.getModelHandler();
		ModelLoader loader = modelHandler.getModelLoader();
		//		newModel = loader.newModel();
		newModel = loader.createModel(oldModel);
		//newId, oldModel.getResolver(), oldModel.getModelManager());
		newModel.setModelHandler(modelHandler);
		//		IStructuredDocument oldStructuredDocument =
		// oldModel.getStructuredDocument();
		//		IStructuredDocument newStructuredDocument =
		// oldStructuredDocument.newInstance();
		//		newModel.setStructuredDocument(newStructuredDocument);
		newModel.setResolver(oldModel.getResolver());
		newModel.setModelManager(oldModel.getModelManager());
		//duplicateFactoryRegistry(newModel, oldModel);
		newModel.setId(newId);
		// set text of new one after all initialization is done
		String contents = oldModel.getStructuredDocument().getText();
		newModel.getStructuredDocument().setText(this, contents);
		return newModel;
	}

	/**
	 */
	public synchronized IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse {

		IStructuredModel newModel = null;
		// get the existing model associated with this id
		IStructuredModel model = getExistingModel(oldId);
		// if it doesn't exist, ignore request (though this would normally
		// be a programming error.
		if (model == null)
			return null;
		// now be sure newModel does not exist
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(newId);
		if (sharedObject != null) {
			throw new ResourceInUse();
		}
		// get loader based on existing type (note the type assumption)
		//Object type = ((IStructuredModel) model).getType();
		//IModelHandler type = model.getModelHandler();
		//ModelLoader loader = (ModelLoader) getModelLoaders().get(type);
		// ModelLoader loader = (ModelLoader) getModelLoaders().get(type);
		// ask the loader to copy
		newModel = copy(model, newId);
		if (newModel != null) {
			// add to our cache
			sharedObject = new SharedObject(newModel);
			sharedObject.referenceCountForEdit = 1;
			fManagedObjects.put(newId, sharedObject);
			trace("copied model", newId, sharedObject.referenceCountForEdit); //$NON-NLS-1$
		}
		return newModel;
	}

	/**
	 * Similar to clone, except the new instance has no content. Note: this
	 * produces an unmanaged model, for temporary use. If a true shared model
	 * is desired, use "copy".
	 */
	public synchronized IStructuredModel createNewInstance(IStructuredModel oldModel) throws IOException {

		IModelHandler handler = oldModel.getModelHandler();
		ModelLoader loader = handler.getModelLoader();
		IStructuredModel newModel = loader.createModel(oldModel);
		newModel.setModelHandler(handler);
		URIResolver oldResolver = oldModel.getResolver();
		newModel.setResolver(oldResolver);
		try {
			newModel.setId(DUPLICATED_MODEL);
		} catch (ResourceInUse e) {
			// impossible, since this is an unmanaged model
		}
		// base location should be null, but we'll set to
		// null to be sure.
		newModel.setBaseLocation(null);
		return newModel;
	}

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: its assume that IFile does not actually exist as
	 * a resource yet. If it does, ResourceAlreadyExists exception is thrown.
	 * If the resource does already exist, then createStructuredDocumentFor is
	 * the right API to use.
	 * 
	 * @throws ResourceInUse
	 *  
	 */
	public synchronized IStructuredDocument createNewStructuredDocumentFor(IFile iFile) throws ResourceAlreadyExists, IOException, CoreException {
		if (iFile.exists()) {
			throw new ResourceAlreadyExists(iFile.getFullPath().toOSString());
		}
		//      Will reconsider in future version
		//		String id = calculateId(iFile);
		//		if (isResourceInUse(id)) {
		//			throw new ResourceInUse(iFile.getFullPath().toOSString());
		//		}
		IDocumentLoader loader = null;
		IModelHandler handler = calculateType(iFile);
		loader = handler.getDocumentLoader();
		// for this API, "createNew" we assume the IFile does not exist yet
		// as checked above, so just create empty document.
		IStructuredDocument result = (IStructuredDocument) loader.createNewStructuredDocument();
		return result;
	}

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: clients should verify IFile exists before using
	 * this method. If this IFile does not exist, then
	 * createNewStructuredDocument is the correct API to use.
	 * 
	 * @throws ResourceInUse
	 */
	public synchronized IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException {

		if (!iFile.exists()) {
			throw new FileNotFoundException(iFile.getFullPath().toOSString());
		}
		//		Will reconsider in future version
		//		String id = calculateId(iFile);
		//		if (isResourceInUse(id)) {
		//			throw new ResourceInUse(iFile.getFullPath().toOSString());
		//		}
		IDocumentLoader loader = null;
		IModelHandler handler = calculateType(iFile);
		loader = handler.getDocumentLoader();
		IStructuredDocument result = (IStructuredDocument) loader.createNewStructuredDocument(iFile);
		return result;
	}

	/**
	 * Conveience method, since a proper IStructuredDocument must have a
	 * proper parser assigned. It should only be used when an empty
	 * structuredDocument is needed. Otherwise, use IFile form.
	 * 
	 * @deprecated - TODO: to be removed by C4 do we really need this? I
	 *             recommend to - use createStructuredDocumentFor(filename,
	 *             null, null) - the filename does not need to represent a
	 *             real - file, but can take for form of dummy.jsp, test.xml,
	 *             etc. - That way we don't hard code the handler, but specify
	 *             we - want the handler that "goes with" a certain type of -
	 *             file.
	 */
	public synchronized IStructuredDocument createStructuredDocumentFor(String contentTypeId) {
		IDocumentLoader loader = null;
		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler handler = cr.getHandlerForContentTypeId(contentTypeId);
		loader = handler.getDocumentLoader();
		IStructuredDocument result = (IStructuredDocument) loader.createNewStructuredDocument();
		return result;
	}

	/**
	 * Conveience method, since a proper IStructuredDocument must have a
	 * proper parser assigned.
	 * 
	 * @deprecated -- - TODO: to be removed by C4 I marked as deprecated to
	 *             discouage use of this method. It does not really work for
	 *             JSP fragments, since JSP Fragments need an IFile to
	 *             correctly look up the content settings. Use IFile form
	 *             instead.
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver) throws IOException {
		IDocumentLoader loader = null;
		InputStream istream = Utilities.getMarkSupportedStream(inputStream);
		if (istream != null) {
			istream.reset();
		}
		IModelHandler handler = calculateType(filename, istream);
		loader = handler.getDocumentLoader();
		IStructuredDocument result = null;
		if (inputStream == null) {
			result = (IStructuredDocument) loader.createNewStructuredDocument();
		} else {
			result = (IStructuredDocument) loader.createNewStructuredDocument(filename, istream);
		}
		return result;
	}

	/**
	 * Special case method. This method was created for the special case where
	 * there is an encoding for input stream that should override all the
	 * normal rules for encoding. For example, if there is an encoding
	 * (charset) specified in HTTP response header, then that encoding is used
	 * to translate the input stream to a string, but then the normal encoding
	 * rules are ignored, so that the string is not translated twice (for
	 * example, if its an HTML "file", then even if it contains a charset in
	 * meta tag, its ignored since its assumed its all correctly decoded by
	 * the HTTP charset.
	 */
	public synchronized IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String encoding) throws IOException {
		String content = readInputStream(inputStream, encoding);
		IStructuredDocument result = createStructuredDocumentFor(filename, content, resolver);
		return result;
	}

	/**
	 * Conveience method. This method can be used when the resource does not
	 * really exist (e.g. when content is being created, but hasn't been
	 * written to disk yet). Note that since the content is being provided as
	 * a String, it is assumed to already be decoded correctly so no
	 * transformation is done.
	 */
	public synchronized IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException {
		// TODO: avoid all these String instances
		StringBuffer contentBuffer = new StringBuffer(content);
		IDocumentLoader loader = null;
		IModelHandler handler = calculateType(filename, null); //inputStream);
		loader = handler.getDocumentLoader();
		IStructuredDocument result = (IStructuredDocument) loader.createNewStructuredDocument();
		StringBuffer convertedContent = loader.handleLineDelimiter(contentBuffer, result);
		result.setEncodingMemento(new NullMemento());
		result.setText(this, convertedContent.toString());
		return result;
	}

	/**
	 * @param iFile
	 * @param result
	 * @return
	 * @throws CoreException
	 */
	private IStructuredModel createUnManagedEmptyModelFor(IFile iFile) throws CoreException {

		IStructuredModel result = null;
		IModelHandler handler = calculateType(iFile);
		String id = calculateId(iFile);
		URIResolver resolver = calculateURIResolver(iFile);

		try {
			result = _commonCreateModel(id, handler, resolver);
		} catch (ResourceInUse e) {
			// impossible, since we're not sharing
			// (even if it really is in use ... we don't care)
			// this may need to be re-examined.
			Logger.trace(IMODELMANAGER_TRACE_CATEGORY, "ModelMangerImpl::createUnManagedStructuredModelFor. Model unexpectedly in use."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return result;
	}

	/**
	 * Conveience method. It depends on the loaders newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 */
	public synchronized IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException {

		IStructuredModel result = null;
		result = createUnManagedEmptyModelFor(iFile);

		IDocumentLoader loader = result.getModelHandler().getDocumentLoader();
		IEncodedDocument document = loader.createNewStructuredDocument(iFile);

		result.getStructuredDocument().setText(this, document.get());

		return result;
	}

	/**
	 * Conveience method. It depends on the loaders newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 */
	public synchronized IStructuredModel createUnManagedStructuredModelFor(String contentTypeId) {

		return createUnManagedStructuredModelFor(contentTypeId, null);
	}

	/**
	 * Conveience method. It depends on the loaders newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 */
	public synchronized IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver) {

		IStructuredModel result = null;
		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler handler = cr.getHandlerForContentTypeId(contentTypeId);
		try {
			result = _commonCreateModel(UNMANAGED_MODEL, handler, resolver); //$NON-NLS-1$
		} catch (ResourceInUse e) {
			// impossible, since we're not sharing
			// (even if it really is in use ... we don't care)
			// this may need to be re-examined.
			Logger.trace(IMODELMANAGER_TRACE_CATEGORY, "ModelMangerImpl::createUnManagedStructuredModelFor. Model unexpectedly in use."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}

	void dump(IStructuredModel model, OutputStream outputStream, EncodingRule encodingRule, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {

		//IFile iFile = getFileFor(model);

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
		Reader reader = new DocumentReader(structuredDocument);
		codedStreamCreator.set(iFile, reader);
		codedStreamCreator.setPreviousEncodingMemento(structuredDocument.getEncodingMemento());
		ByteArrayOutputStream codedByteStream = codedStreamCreator.getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
		InputStream codedStream = new ByteArrayInputStream(codedByteStream.toByteArray());
		iFile.setContents(codedStream, true, true, null);

		//getDocumentDumper().dump(outputStream, structuredDocument,
		// encodingRule, use3ByteBOM, file);
		model.setDirtyState(false);
		model.setNewState(false);
	}

	/**
	 * Informs all registered model state listeners that the the model is
	 * about to under go a "large" change. This change might be interms of
	 * contents, in might be in terms of the model id or base location.
	 */
	protected void fireModelsAboutToBeChanged() {

		// notice we only fire this event if we are not already in a model
		// state changing sequence
		if (modelManagerStateChanging == 0) {
			// we must assign listeners to local variable, since the add and
			// remove listner
			// methods can change the actual instance of the listener array
			// from another thread
			if (fModelManagerListeners != null) {
				Object[] holdListeners = fModelManagerListeners;
				for (int i = 0; i < holdListeners.length; i++) {
					((IModelManagerListener) holdListeners[i]).modelsAboutToBeChanged();
				}
			}
		}
		// we always increment counter, for every request (so must receive
		// corresponding number of 'changedModel' requests)
		modelManagerStateChanging++;
	}

	/**
	 * Informs all registered model state listeners that an impending change
	 * is now complete. This method must only be called by 'modelChanged'
	 * since it keeps track of counts.
	 */
	protected void fireModelsChanged() {

		// always decrement
		modelManagerStateChanging--;
		// to be less than zero is a programming error, but we'll reset to
		// zero
		// with no error messages.
		if (modelManagerStateChanging < 0)
			modelManagerStateChanging = 0;
		// We only fire this event if all pending requests are done.
		// That is, if we've received the same number of fireModelChanged as
		// we
		// have fireModelAboutToBeChanged.
		if (modelManagerStateChanging == 0) {
			// we must assign listeners to local variable, since the add and
			// remove listner
			// methods can change the actual instance of the listener array
			// from another thread
			if (fModelManagerListeners != null) {
				Object[] holdListeners = fModelManagerListeners;
				for (int i = 0; i < holdListeners.length; i++) {
					((IModelManagerListener) holdListeners[i]).modelsChanged();
				}
			}
		}
	}

	private EnumeratedModelIds getEnumeratedModelIds() {
		// return new instance each time so will "act like" proper enumeration
		// to client
		// (if we cached, may not be at beginning).
		return new EnumeratedModelIds(fManagedObjects);
	}

	private IStructuredModel getExistingModel(Object id) {

		IStructuredModel result = null;
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not, then we'll simply return null
		if (sharedObject != null) {
			result = sharedObject.theSharedObject;
		}
		return result;
	}

	/**
	 * Note: users of this 'model' must still release it when finished.
	 * Returns null if there's not a model corresponding to document.
	 */
	public synchronized IStructuredModel getExistingModelForEdit(IDocument document) {
		IStructuredModel result = null;
		Enumeration ids = getEnumeratedModelIds();
		while (ids.hasMoreElements()) {
			Object potentialId = ids.nextElement();
			IStructuredModel tempResult = getExistingModel(potentialId);
			if (document == tempResult.getStructuredDocument()) {
				result = getExistingModelForEdit(potentialId);
				break;
			}
		}
		return result;
	}

	/**
	 * This is similar to the getModel method, except this method does not
	 * create a model. This method does increment the reference count (if it
	 * exists). If the model does not already exist in the cache of models,
	 * null is returned.
	 */
	public synchronized IStructuredModel getExistingModelForEdit(IFile iFile) {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		Object id = calculateId(iFile);
		IStructuredModel result = getExistingModelForEdit(id);
		return result;
	}

	/**
	 * This is similar to the getModel method, except this method does not
	 * create a model. This method does increment the reference count (if it
	 * exists). If the model does not already exist in the cache of models,
	 * null is returned.
	 * 
	 * @deprecated use IFile form - this one will become protected or private
	 */
	public synchronized IStructuredModel getExistingModelForEdit(Object id) {

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		IStructuredModel result = null;
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not, then we'll simply return null
		if (sharedObject != null) {
			// if shared object is in our cache, then simply increment its ref
			// count,
			// and return the object.
			sharedObject.referenceCountForEdit++;
			result = sharedObject.theSharedObject;
			trace("got existing model for Edit: ", id); //$NON-NLS-1$
			trace("   incremented referenceCountForEdit ", id, sharedObject.referenceCountForEdit); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Note: users of this 'model' must still release it when finished.
	 * Returns null if there's not a model corresponding to document.
	 */
	public synchronized IStructuredModel getExistingModelForRead(IDocument document) {
		IStructuredModel result = null;
		Enumeration ids = getEnumeratedModelIds();
		while (ids.hasMoreElements()) {
			Object potentialId = ids.nextElement();
			IStructuredModel tempResult = getExistingModel(potentialId);
			if (document == tempResult.getStructuredDocument()) {
				result = getExistingModelForRead(potentialId);
				break;
			}
		}
		return result;
	}

	public synchronized IStructuredModel getExistingModelForRead(IFile iFile) {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		Object id = calculateId(iFile);
		IStructuredModel result = getExistingModelForRead(id);
		return result;
	}

	/**
	 * This is similar to the getModel method, except this method does not
	 * create a model. This method does increment the reference count (if it
	 * exists). If the model does not already exist in the cache of models,
	 * null is returned.
	 * 
	 * @deprecated use IFile form - this one will become protected or private
	 */
	public synchronized IStructuredModel getExistingModelForRead(Object id) {

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		IStructuredModel result = null;
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not, then we'll simply return null
		if (sharedObject != null) {
			// if shared object is in our cache, then simply increment its ref
			// count,
			// and return the object.
			sharedObject.referenceCountForRead++;
			result = sharedObject.theSharedObject;
		}
		return result;
	}

	/**
	 * @deprecated DMW: Tom, this is "special" for links builder Assuming its
	 *             still needed, wouldn't it be better to change to
	 *             getExistingModels()? -- will be removed. Its not thread
	 *             safe for one thread to get the Enumeration, when underlying
	 *             data could be changed in another thread.
	 */
	public synchronized Enumeration getExistingModelIds() {

		Enumeration result = getEnumeratedModelIds();
		return result;
	}

	// TODO: replace (or suplement) this is a "model info" association to the
	// IFile that created the model
	protected IFile getFileFor(IStructuredModel model) {
		if (model == null)
			return null;
		String path = model.getBaseLocation();
		if (path == null || path.length() == 0) {
			Object id = model.getId();
			if (id == null)
				return null;
			path = id.toString();
		}
		// TOODO needs rework for linked resources
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFileForLocation(new Path(path));
		return file;
	}

	/**
	 * One of the primary forms to get a managed model
	 */
	public synchronized IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, null, null);
	}

	/**
	 * @see com.ibm.sed.model.IModelManager#getModelForEdit(IFile,
	 *      EncodingRule)
	 */
	public synchronized IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, encodingRule);
	}

	public synchronized IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, encoding, lineDelimiter);
	}

	public synchronized IStructuredModel getModelForEdit(IStructuredDocument document) {
		return _getModelFor(document, EDIT);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile or String form
	 */
	public synchronized IStructuredModel getModelForEdit(Object id, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {

		Assert.isNotNull(id, "IFile parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForEdit(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	/**
	 * @see IModelManager
	 * @deprecated - use IFile or String form
	 */
	public synchronized IStructuredModel getModelForEdit(Object id, Object modelType, String encodingName, String lineDelimiter, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForEdit(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	public synchronized IStructuredModel getModelForEdit(String filename, InputStream inputStream, URIResolver resolver) throws IOException {

		InputStream istream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler handler = calculateType(filename, istream);
		Assert.isNotNull(handler, "model handler can not be null"); //$NON-NLS-1$
		IStructuredModel result = null;
		result = _commonCreateModel(istream, filename, handler, resolver, EDIT, null, null);
		return result;
	}

	/**
	 * One of the primary forms to get a managed model
	 */
	public synchronized IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, null, null);
	}

	public synchronized IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, encodingRule);
	}

	public synchronized IStructuredModel getModelForRead(IFile iFile, String encodingName, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, encodingName, lineDelimiter);
	}

	public synchronized IStructuredModel getModelForRead(IStructuredDocument document) {
		return _getModelFor(document, READ);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile or String form
	 */
	public synchronized IStructuredModel getModelForRead(Object id, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForRead(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile form
	 */
	public synchronized IStructuredModel getModelForRead(Object id, Object modelType, String encodingName, String lineDelimiter, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForRead(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	public synchronized IStructuredModel getModelForRead(String filename, InputStream inputStream, URIResolver resolver) throws IOException {
		InputStream istream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler handler = calculateType(filename, istream);
		IStructuredModel result = null;
		result = _commonCreateModel(istream, filename, handler, resolver, READ, null, null);
		return result;
	}

	/**
	 * @deprecated - only temporarily visible
	 */
	public ModelHandlerRegistry getModelHandlerRegistry() {
		if (fModelHandlerRegistry == null) {
			fModelHandlerRegistry = ModelHandlerRegistry.getInstance();
		}
		return fModelHandlerRegistry;
	}

	/**
	 * @see IModelManager#getNewModelForEdit(IFile, boolean)
	 */
	public synchronized IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = _commonNewModel(iFile, force);
		sharedObject.referenceCountForEdit = 1;
		return sharedObject.theSharedObject;
	}

	/**
	 * @see IModelManager#getNewModelForRead(IFile, boolean)
	 */
	public synchronized IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = _commonNewModel(iFile, force);
		sharedObject.referenceCountForRead = 1;
		return sharedObject.theSharedObject;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public synchronized int getReferenceCount(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForRead + sharedObject.referenceCountForEdit;
		return count;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public synchronized int getReferenceCountForEdit(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForEdit;
		return count;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public synchronized int getReferenceCountForRead(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForRead;
		return count;
	}

	private void handleConvertLineDelimiters(IStructuredDocument structuredDocument, IFile iFile, EncodingRule encodingRule, EncodingMemento encodingMemento) throws CoreException, MalformedOutputExceptionWithDetail, UnsupportedEncodingException {
		if (structuredDocument.getNumberOfLines() > 1) {
			String originalText = structuredDocument.get();
			convertLineDelimiters(structuredDocument, iFile);
		}
	}

	private void handleProgramError(Throwable t) {

		Logger.logException("Impossible Program Error", t); //$NON-NLS-1$
	}

	/**
	 * Register adapters for resources using an extension point. Required to
	 * ensure that calculateURIResolver has the necessary factories registered
	 * before any models are loaded.
	 * 
	 * @deprecated - wrong place to do this
	 */
	protected void initResourceAdapters() {

		// Note: see comment in plugin.xml for potentially
		// breaking change in behavior.

		// new URIResolverAdapterFactoryRegistryReader().loadRegistry();
	}

//	private boolean isResourceInUse(String id) {
//
//		boolean result = false;
//		IStructuredModel model = null;
//		Enumeration ids = getEnumeratedModelIds();
//		while (ids.hasMoreElements()) {
//			Object inUseID = ids.nextElement();
//			if (id.equals(inUseID)) {
//				result = true;
//				break;
//			}
//		}
//		return result;
//	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public synchronized boolean isShared(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		boolean result = false;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForRead + sharedObject.referenceCountForEdit;
		result = count > 1;
		return result;
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	public synchronized boolean isSharedForEdit(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		boolean result = false;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForEdit;
		result = count > 1;
		return result;
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	public synchronized boolean isSharedForRead(Object id) {
		Assert.isNotNull(id);
		int count = 0;
		boolean result = false;
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null)
			count = sharedObject.referenceCountForRead;
		result = count > 1;
		return result;
	}

	/**
	 * This method can be called to determine if the model manager is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public synchronized boolean isStateChanging() {

		return modelManagerStateChanging > 0;
	}

	/**
	 * This method changes the id of the model. TODO: try to refine the design
	 * not to use this function
	 */
	public synchronized void moveModel(Object oldId, Object newId) {
		org.eclipse.wst.sse.core.util.Assert.isNotNull(oldId, "id parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(oldId);
		// if not found in cache, ignore request.
		// this would normally be a program error
		if (sharedObject != null) {
			fManagedObjects.remove(oldId);
			fManagedObjects.put(newId, sharedObject);
		}
	}

	private String readInputStream(InputStream inputStream, String ianaEncodingName) throws UnsupportedEncodingException, IOException {

		String allText = null;
		if ((ianaEncodingName != null) && (ianaEncodingName.length() != 0)) {
			String enc = CodedIO.getAppropriateJavaCharset(ianaEncodingName);
			if (enc == null) {
				// if no conversion was possible, let's assume that
				// the encoding is already a java encoding name, so we'll
				// proceed with that assumption. This is the case, for
				// example,
				// for the reload() procedure.
				// If in fact it is not a valid java encoding, then
				// the "allText=" line will cause an
				// UnsupportedEncodingException
				enc = ianaEncodingName;
			}
			allText = readInputStream(new InputStreamReader(inputStream, enc));
		} else {
			// we normally assume encoding is provided for this method, but if
			// not,
			// we'll use platform default
			allText = readInputStream(new InputStreamReader(inputStream));
		}
		return allText;
	}

	private String readInputStream(InputStreamReader inputStream) throws IOException {

		int numRead = 0;
		StringBuffer buffer = new StringBuffer();
		char tBuff[] = new char[READ_BUFFER_SIZE];
		while ((numRead = inputStream.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		// remember -- we didn't open stream ... so we don't close it
		return buffer.toString();
	}

	/*
	 * @see IModelManager#reinitialize(IStructuredModel)
	 */
	public synchronized IStructuredModel reinitialize(IStructuredModel model) {

		// getHandler (assume its the "new one")
		IModelHandler handler = model.getModelHandler();
		// getLoader for that new one
		ModelLoader loader = handler.getModelLoader();
		// ask it to reinitialize
		model = loader.reinitialize(model);
		// the loader should check to see if the one it received
		// is the same type it would normally create.
		// if not, it must "start from scratch" and create a whole
		// new one.
		// if it is of the same type, it should just 'replace text'
		// replacing all the existing text with the new text.
		// the important one is the JSP loader ... it should go through
		// its embedded content checking and initialization
		return model;
	}

	/**
	 * protected for use in same package, not subclasses
	 *  
	 */
	protected synchronized void releaseFromEdit(Object id) {
		Assert.isNotNull(id);
		SharedObject sharedObject = null;

		sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not found in cache, ignore request, for now.
		// this would normally be a program error
		// TODO: uncomment this assert in next version
		// and remove if null check
		//Assert.isNotNull(sharedObject);
		if (sharedObject != null) {
			sharedObject.referenceCountForEdit--;
			if ((sharedObject.referenceCountForRead == 0) && (sharedObject.referenceCountForEdit == 0)) {
				fManagedObjects.remove(id);
				FileBufferModelManager.getInstance().releaseModel(sharedObject.theSharedObject.getStructuredDocument());
			}
		}
	}

	/**
	 * protected for use in same package, not subclasses
	 *  
	 */
	protected synchronized void releaseFromRead(Object id) {
		Assert.isNotNull(id);
		SharedObject sharedObject = null;

		sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not found in cache, ignore request, for now.
		// this would normally be a program error
		// TODO: uncomment this assert in next version
		// and remove if null check
		//Assert.isNotNull(sharedObject);
		if (sharedObject != null) {
			sharedObject.referenceCountForRead--;
			if ((sharedObject.referenceCountForRead == 0) && (sharedObject.referenceCountForEdit == 0)) {
				fManagedObjects.remove(id);
				FileBufferModelManager.getInstance().releaseModel(sharedObject.theSharedObject.getStructuredDocument());
			}
		}
	}

	/**
	 * This is similar to the getModel method, except this method does not use
	 * the cached version, but forces the cached version to be replaced with a
	 * fresh, unchanged version. Note: this method does not change any
	 * reference counts. Also, if there is not already a cached version of the
	 * model, then this call is essentially ignored (that is, it does not put
	 * a model in the cache) and returns null.
	 * 
	 * @deprecated - will become protected, use reload directly on model
	 */
	public synchronized IStructuredModel reloadModel(Object id, java.io.InputStream inputStream) throws java.io.UnsupportedEncodingException {

		// get the existing model associated with this id
		IStructuredModel structuredModel = getExistingModel(id);
		// for the model to be null is probably an error (that is,
		// reload should not have been called, but we'll guard against
		// a null pointer example and return null if we are no longer managing
		// that model.
		if (structuredModel != null) {
			// get loader based on existing type
			// dmwTODO evaluate when reload should occur
			// with potentially new type (e.g. html 'save as' jsp).
			IModelHandler handler = structuredModel.getModelHandler();
			ModelLoader loader = handler.getModelLoader();
			// ask the loader to re-load
			loader.reload(Utilities.getMarkSupportedStream(inputStream), structuredModel);
			trace("re-loading model", id); //$NON-NLS-1$
		}
		return structuredModel;
	}

	public synchronized void removeModelManagerListener(IModelManagerListener listener) {

		if ((fModelManagerListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fModelManagerListeners, listener)) {
				int oldSize = fModelManagerListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelManagerListeners[i] == listener) { // ignore
					} else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fModelManagerListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fModelManagerListeners = newListeners;
			}
		}
	}

	public void saveModel(IFile iFile, String id, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null || sharedObject.theSharedObject == null) {
			throw new SourceEditingRuntimeException(ResourceHandler1.getString("Program_Error__ModelManage_EXC_")); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		} else {
			boolean saved = false;
			// if this model was based on a File Buffer and we're writing back
			// to the same location, use the buffer to do the writing
			if (FileBufferModelManager.getInstance().isExistingBuffer(sharedObject.theSharedObject.getStructuredDocument())) {
				ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(sharedObject.theSharedObject.getStructuredDocument());
				if (iFile.getLocation().equals(buffer.getLocation())) {
					buffer.commit(new NullProgressMonitor(), true);
				}
			}
			if (!saved) {
				IStructuredModel model = sharedObject.theSharedObject;
				IStructuredDocument document = model.getStructuredDocument();
				saveStructuredDocument(document, iFile, encodingRule);
				trace("saving model", id); //$NON-NLS-1$
			}
			sharedObject.theSharedObject.setDirtyState(false);
			sharedObject.theSharedObject.setNewState(false);
		}
	}

	/**
	 * Saving the model really just means to save it's structured document.
	 * 
	 * @param id
	 * @param outputStream
	 * @param encodingRule
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 */
	public void saveModel(String id, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null) {
			throw new SourceEditingRuntimeException(ResourceHandler1.getString("Program_Error__ModelManage_EXC_")); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		} else {
			// if this model was based on a File Buffer and we're writing back
			// to the same location, use the buffer to do the writing
			if (FileBufferModelManager.getInstance().isExistingBuffer(sharedObject.theSharedObject.getStructuredDocument())) {
				ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(sharedObject.theSharedObject.getStructuredDocument());
				buffer.commit(new NullProgressMonitor(), true);
			} else {
				IFile iFile = getFileFor(sharedObject.theSharedObject);
				IStructuredModel model = sharedObject.theSharedObject;
				IStructuredDocument document = model.getStructuredDocument();
				saveStructuredDocument(document, iFile);
				trace("saving model", id); //$NON-NLS-1$
			}
		}
		sharedObject.theSharedObject.setDirtyState(false);
		sharedObject.theSharedObject.setNewState(false);
	}

	/**
	 * @deprecated - this method is less efficient than IFile form, since it
	 *             requires an extra "copy" of byte array, and should be avoid
	 *             in favor of the IFile form.
	 */
	public void saveModel(String id, OutputStream outputStream, EncodingRule encodingRule) throws UnsupportedEncodingException, CoreException, IOException {

		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null) {
			throw new SourceEditingRuntimeException(ResourceHandler1.getString("Program_Error__ModelManage_EXC_")); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		} else {
			CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
			codedStreamCreator.set(sharedObject.theSharedObject.getId(), new DocumentReader(sharedObject.theSharedObject.getStructuredDocument()));
			codedStreamCreator.setPreviousEncodingMemento(sharedObject.theSharedObject.getStructuredDocument().getEncodingMemento());
			ByteArrayOutputStream byteArrayOutputStream = codedStreamCreator.getCodedByteArrayOutputStream(encodingRule);
			byte[] outputBytes = byteArrayOutputStream.toByteArray();
			outputStream.write(outputBytes);
			trace("saving model", id); //$NON-NLS-1$
		}
		sharedObject.theSharedObject.setDirtyState(false);
		sharedObject.theSharedObject.setNewState(false);
	}

	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, CoreException, IOException {
		saveStructuredDocument(structuredDocument, iFile, EncodingRule.CONTENT_BASED);
	}

	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, CoreException, IOException {
		if (FileBufferModelManager.getInstance().isExistingBuffer(structuredDocument)) {
			ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(structuredDocument);
			if (iFile.getLocation().equals(buffer.getLocation())) {
				buffer.commit(new NullProgressMonitor(), true);
			}
		} else {
			//IModelHandler handler = calculateType(iFile);
			//IDocumentDumper dumper = handler.getDocumentDumper();
			CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
			Reader reader = new DocumentReader(structuredDocument);
			codedStreamCreator.set(iFile, reader);
			codedStreamCreator.setPreviousEncodingMemento(structuredDocument.getEncodingMemento());
			EncodingMemento encodingMemento = codedStreamCreator.getCurrentEncodingMemento();

			// be sure document's is updated, in case exception is thrown in
			// getCodedByteArrayOutputStream
			structuredDocument.setEncodingMemento(encodingMemento);

			// Convert line delimiters after encoding memento is figured out,
			// but
			// before writing to output stream.
			handleConvertLineDelimiters(structuredDocument, iFile, encodingRule, encodingMemento);

			ByteArrayOutputStream codedByteStream = codedStreamCreator.getCodedByteArrayOutputStream(encodingRule);
			InputStream codedStream = new ByteArrayInputStream(codedByteStream.toByteArray());
			if (iFile.exists())
				iFile.setContents(codedStream, true, true, null);
			else
				iFile.create(codedStream, false, null);
			codedByteStream.close();
			codedStream.close();
		}
	}

	/**
	 * Common trace method
	 */
	private void trace(String msg, Object id) {
		if (Logger.isTracing(IMODELMANAGER_TRACE_CATEGORY)) {
			Logger.trace(IMODELMANAGER_TRACE_CATEGORY, msg + " " + Utilities.makeShortId(id)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Common trace method
	 */
	private void trace(String msg, Object id, int value) {
		if (Logger.isTracing(IMODELMANAGER_TRACE_CATEGORY)) {
			Logger.trace(IMODELMANAGER_TRACE_CATEGORY, msg + Utilities.makeShortId(id) + " (" + value + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
}
