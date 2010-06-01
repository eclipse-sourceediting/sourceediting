/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *  *     Frank Zigler/Web Performance, Inc. - 288196 - Deadlock in ModelManagerImpl after IOException
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.NullMemento;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.CodedStreamCreator;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.encoding.ContentBasedPreferenceGateway;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.exceptions.MalformedOutputExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.ProjectResolver;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.internal.util.Utilities;

/**
 * <p>Not intended to be subclassed, referenced or instantiated by clients.
 * Clients should obtain an instance of the IModelManager interface through
 * {@link StructuredModelManager#getModelManager()}.</p>
 * 
 * <p>This class is responsible for creating, retrieving, and caching
 * StructuredModels It retrieves the cached objects by an id which is
 * typically a String representing the resources URI. Note: Its important that
 * all clients that share a resource do so using <b>identical </b>
 * identifiers, or else different instances will be created and retrieved,
 * even if they all technically point to the same resource on the file system.
 * This class also provides a convenient place to register Model Loaders and
 * Dumpers based on 'type'.</p>
 */
public class ModelManagerImpl implements IModelManager {

	
	
	static class ReadEditType {
		ReadEditType(String type) {
		}
	}

	class SharedObject {
		int referenceCountForEdit;
		int referenceCountForRead;
		volatile IStructuredModel theSharedModel;
		final ILock LOAD_LOCK = Job.getJobManager().newLock();
		volatile boolean initializing = true;
		volatile boolean doWait = true;
		// The field 'id' is only meant for debug
		final String id;

		SharedObject(String id) {
			this.id=id;
			// be aware, this lock will leak and cause the deadlock detector to be horrible if we never release it
			LOAD_LOCK.acquire();
		}

		/**
		 * Waits until this shared object has been attempted to be loaded. The
		 * load is "attempted" because not all loads result in a model. However,
		 * upon leaving this method, theShareModel variable is up-to-date.
		 */
		public void waitForLoadAttempt() {
			final boolean allowInterrupt = PrefUtil.ALLOW_INTERRUPT_WAITING_THREAD;
			final long timeLimit = (PrefUtil.WAIT_DELAY==0) ? Long.MAX_VALUE : PrefUtil.now() + PrefUtil.WAIT_DELAY;
			final Job current = Job.getJobManager().currentJob();
			boolean interrupted = false;
			try {
				while (initializing) {
					if (current!=null) {
						current.yieldRule(null);
					}
					try {
						loop();
					} catch (InterruptedException e) {
						if (allowInterrupt) {
							throw new OperationCanceledException("Waiting thread interrupted while waiting for model id: "+id + " to load");
						} else {
							interrupted=true;
						}
					}
					if (PrefUtil.now() >= timeLimit	)
						throw new OperationCanceledException("Waiting thread timeout exceeded while waiting for model id: "+id + " to load");
				}
			}
			finally {
				if (interrupted) {
					Thread.currentThread().interrupt();
				}
			}
		}

		private void loop() throws InterruptedException {	
			if (initializing) {
				if (LOAD_LOCK.acquire(PrefUtil.WAIT_INTERVAL_MS)) {
					// if we got the lock, but initializing is still not true the deadlock detector gave us
					// the lock and caused reentrancy into this critical section. This is invalid and the 
					// sign of a cyclical load attempt. In this case, we through an 
					// OperationCanceledException in lew of entering a spin-loop. 
					if (initializing) {
						LOAD_LOCK.release();
						throw new OperationCanceledException("Aborted cyclic load attempt for model with id: "+ id );
					} else {
						LOAD_LOCK.release();
					}
				}
			}
		}

		/**
		 * Flags this model as loaded. All waiting methods on
		 * {@link #waitForLoadAttempt()} will proceed after this method returns.
		 */
		public void setLoaded() {
			initializing = false;
			LOAD_LOCK.release();
		}
	}

	private Exception debugException = null;

	/**
	 * Our singleton instance
	 */
	private static ModelManagerImpl instance;
	private final static int READ_BUFFER_SIZE = 4096;

	/**
	 * Not to be called by clients, will be made restricted access.
	 * 
	 * @return
	 */
	public synchronized static IModelManager getInstance() {

		if (instance == null) {
			instance = new ModelManagerImpl();
		}
		return instance;
	}

	/**
	 * Our cache of managed objects
	 */
	private Map fManagedObjects;

	private ModelHandlerRegistry fModelHandlerRegistry;
	private final ReadEditType READ = new ReadEditType("read"); //$NON-NLS-1$
	private final ReadEditType EDIT = new ReadEditType("edit"); //$NON-NLS-1$
	
	private final ILock SYNC = Job.getJobManager().newLock();
	/**
	 * Intentionally default access only.
	 * 
	 */
	ModelManagerImpl() {
		super();
		fManagedObjects = new HashMap();
		// To prevent deadlocks:  always acquire multiple locks in this order: SYNC, sharedObject. 
		// DO NOT acquire a SYNC within a sharedObject lock, unless you already own the SYNC lock
		// Tip: Try to hold the smallest number of locks you can
	}

	private IStructuredModel _commonCreateModel(IFile file, String id, IModelHandler handler, URIResolver resolver, ReadEditType rwType, EncodingRule encodingRule) throws IOException,CoreException {
		SharedObject sharedObject = null;
		
		SYNC.acquire();
		sharedObject = (SharedObject) fManagedObjects.get(id);
		SYNC.release();
		
		while(true) {
			if (sharedObject!=null) {
				sharedObject.waitForLoadAttempt();
			}
			SYNC.acquire();
			// we know this object's model has passed the load, however, we don't know 
			// it's reference count status. It might have already been disposed. Or it could have 
			// been disposed and a concurrent thread has already begun loading it, in which case
			// we should use the sharedobject they are loading. 
			// NOTE: This pattern is applied 3 times in this class, but only doc'd once. The logic is 
			// exactly the same. 
			SharedObject testObject = (SharedObject) fManagedObjects.get(id);
			if (testObject==null) {
				// null means it's been disposed, we need to do the work to reload it.
				sharedObject = new SharedObject(id);
				fManagedObjects.put(id, sharedObject);
				SYNC.release();
				_doCommonCreateModel(file, id, handler, resolver, rwType, encodingRule,
						sharedObject);
				break;
			} else if (sharedObject == testObject) {
				// if nothing happened, just increment the could and return the shared model
				synchronized(sharedObject) {
					if (sharedObject.theSharedModel!=null) {
						_incrCount(sharedObject, rwType);
					}
				}
				SYNC.release();
				break;
			} else {
				// sharedObject != testObject which means the object we were waiting on has been disposed
				// a replacement has already been placed in the managedObjects table. Through away our
				// stale sharedObject and continue on with the one we got from the queue. Note: We don't know its
				// state, so continue the waitForLoad-check loop. 
				SYNC.release();
				sharedObject = testObject;
			}
		}
		
		// we expect to always return something
		if (sharedObject == null) {
			debugException = new Exception("instance only for stack trace"); //$NON-NLS-1$
			Logger.logException("Program Error: no model recorded for id " + id, debugException); //$NON-NLS-1$
		}
		
		// note: clients must call release for each time they call get.
		return sharedObject==null ? null : sharedObject.theSharedModel;
	}

	private void _decrCount(SharedObject sharedObject, ReadEditType type) {
		if (type == READ) {
			sharedObject.referenceCountForRead--;
			FileBufferModelManager.getInstance().disconnect(sharedObject.theSharedModel.getStructuredDocument());
		}
		else if (type == EDIT) {
			sharedObject.referenceCountForEdit--;
			FileBufferModelManager.getInstance().disconnect(sharedObject.theSharedModel.getStructuredDocument());
		}
		else
			throw new IllegalArgumentException();
	}

	private void _doCommonCreateModel(IFile file, String id, IModelHandler handler,
			URIResolver resolver, ReadEditType rwType, EncodingRule encodingRule,
			SharedObject sharedObject) throws CoreException, IOException {
		// XXX: Does not integrate with FileBuffers
		boolean doRemove = true;
		try {
		synchronized(sharedObject) {
			InputStream inputStream = null;
			IStructuredModel model = null;
			try {
				model = _commonCreateModel(id, handler, resolver);
				IModelLoader loader = handler.getModelLoader();
				inputStream = Utilities.getMarkSupportedStream(file.getContents(true));
				loader.load(Utilities.getMarkSupportedStream(inputStream), model, encodingRule);
			}
			catch (ResourceInUse e) {
				// impossible, since we've already found
				handleProgramError(e);
			} finally {
				if (inputStream!=null) {
					try { 
						inputStream.close();
					} catch(IOException e) {
					}
				}
			}
			if (model != null) {
				// add to our cache
				sharedObject.theSharedModel=model;
				_initCount(sharedObject, rwType);
				doRemove = false;
			}
		}
		}
		finally{
		if (doRemove) {
			SYNC.acquire();	
			fManagedObjects.remove(id);	
			SYNC.release();
		}
		sharedObject.setLoaded();
		}
	}

	private IStructuredModel _commonCreateModel(InputStream inputStream, String id, IModelHandler handler, URIResolver resolver, ReadEditType rwType, String encoding, String lineDelimiter) throws IOException {
		
		if (id == null) {
			throw new IllegalArgumentException("Program Error: id may not be null"); //$NON-NLS-1$
		}
		SharedObject sharedObject = null;
	
		SYNC.acquire();
		sharedObject = (SharedObject) fManagedObjects.get(id);
		SYNC.release();
		
		while(true) {
			if (sharedObject!=null) {
				sharedObject.waitForLoadAttempt();
			}
			SYNC.acquire();
			SharedObject testObject = (SharedObject) fManagedObjects.get(id);
			if (testObject==null) {
				// it was removed ,so lets create it
				sharedObject = new SharedObject(id);
				fManagedObjects.put(id, sharedObject);
				SYNC.release();
				_doCommonCreateModel(inputStream, id, handler, resolver, rwType,
						encoding, lineDelimiter, sharedObject);
				break;
			} else if (sharedObject == testObject) {
				synchronized(sharedObject) {
					if (sharedObject.theSharedModel!=null) {
						_incrCount(sharedObject, rwType);
					}
				}
				SYNC.release();
				break;
			} else {
				SYNC.release();
				sharedObject = testObject;
			}
		}
		
		// we expect to always return something
		Assert.isNotNull(sharedObject, "Program Error: no model recorded for id " + id); //$NON-NLS-1$
		// note: clients must call release for each time they call get.
		return sharedObject.theSharedModel;
	
	}

	private void _doCommonCreateModel(InputStream inputStream, String id, IModelHandler handler,
			URIResolver resolver, ReadEditType rwType, String encoding, String lineDelimiter,
			SharedObject sharedObject) throws IOException {
		boolean doRemove = true;
		try {
		synchronized(sharedObject) {
			IStructuredModel model = null;
			try {
				model = _commonCreateModel(id, handler, resolver);
				IModelLoader loader = handler.getModelLoader();
				if (inputStream == null) {
					Logger.log(Logger.WARNING, "model was requested for id " + id + " without a content InputStream"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				loader.load(id, Utilities.getMarkSupportedStream(inputStream), model, encoding, lineDelimiter);
			}
			catch (ResourceInUse e) {
				// impossible, since we've already found
				handleProgramError(e);
			}
			if (model != null) {
				/**
				 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=264228
				 * 
				 * Ensure that the content type identifier field of the model
				 * is properly set. This is normally handled by the
				 * FileBufferModelManager when working with files as it knows
				 * the content type in advance; here is where we handle it for
				 * streams.
				 */
				if (model instanceof AbstractStructuredModel) {
					DocumentReader reader = new DocumentReader(model.getStructuredDocument());
					IContentDescription description = Platform.getContentTypeManager().getDescriptionFor(reader, id, new QualifiedName[0]);
					reader.close();
					if (description != null && description.getContentType() != null) {
						((AbstractStructuredModel) model).setContentTypeIdentifier(description.getContentType().getId());
					}
				}

				sharedObject.theSharedModel = model;
				_initCount(sharedObject, rwType);
				doRemove = false;
			}
		}
		}
		finally {
		if (doRemove) {
			SYNC.acquire();
			// remove it if we didn't get one back
			fManagedObjects.remove(id);
			SYNC.release();
		}
		sharedObject.setLoaded();
		}
	}

	private IStructuredModel _commonCreateModel(String id, IModelHandler handler, URIResolver resolver) throws ResourceInUse {

		IModelLoader loader = handler.getModelLoader();
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
			model = _commonCreateModel(iFile, id, handler, resolver, rwType, encodingRule);
		}

		return model;
	}

	private IStructuredModel _commonGetModel(IFile iFile, ReadEditType rwType, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {
		String id = calculateId(iFile);
		IStructuredModel model = _commonGetModel(iFile, id, rwType, encoding, lineDelimiter);

		return model;
	}

	private IStructuredModel _commonGetModel(IFile file, String id, ReadEditType rwType, String encoding, String lineDelimiter) throws IOException, CoreException {
		if (id == null)
			throw new IllegalArgumentException("Program Error: id may not be null"); //$NON-NLS-1$

		SharedObject sharedObject = null;
		if (file != null && file.exists()) {
			SYNC.acquire();
			sharedObject = (SharedObject) fManagedObjects.get(id);
			SYNC.release();
			
			while(true) {
				if (sharedObject!=null) {
					sharedObject.waitForLoadAttempt();
				}
				SYNC.acquire();
				SharedObject testObject = (SharedObject) fManagedObjects.get(id);
				if (testObject==null) {
					// it was removed ,so lets create it
					sharedObject = new SharedObject(id);
					fManagedObjects.put(id, sharedObject);
					
					SYNC.release();
					_doCommonGetModel(file, id, sharedObject,rwType);
					break;
				} else if (sharedObject == testObject) {
					synchronized(sharedObject) {
						if (sharedObject.theSharedModel!=null) {
							_incrCount(sharedObject, rwType);
						}
					}
					SYNC.release();
					break;
				} else {
					// we got a different object than what we were expecting
					SYNC.release();
					// two threads were interested in models for the same id. 
					// The other thread one, so lets back off and try again. 
					sharedObject = testObject; 
				}
			}
		}
		
		// if we don't know how to create a model
		// for this type of file, return null
	
		// note: clients must call release for each time they call
		// get.
			
		return sharedObject==null ? null : sharedObject.theSharedModel;
	}

	private void _doCommonGetModel(IFile file, String id, SharedObject sharedObject,ReadEditType rwType) {
		boolean doRemove = true;
		synchronized(sharedObject) {
			sharedObject.doWait=false;
			IStructuredModel model = FileBufferModelManager.getInstance().getModel(file);
			sharedObject.doWait=true;
			if (model != null) {
				sharedObject.theSharedModel=model;
				_initCount(sharedObject, rwType);
				doRemove = false;
			}
		}
		if (doRemove) {
			SYNC.acquire();
			fManagedObjects.remove(id);
			SYNC.release();
		}
		sharedObject.setLoaded();
	}

	private SharedObject _commonNewModel(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		IStructuredModel aSharedModel = null;
		// First, check if resource already exists on file system.
		// if is does, then throw Resource in Use iff force==false

		if (iFile.exists() && !force) {
			throw new ResourceAlreadyExists();
		}
		
		SharedObject sharedObject = null;
		String id = calculateId(iFile);
		try {
			SYNC.acquire();
		
			 sharedObject = (SharedObject) fManagedObjects.get(id);
	
			if (sharedObject != null && !force) {
				// if in cache already, and force is not true, then this is an
				// error
				// in call
				throw new ResourceInUse();
			}
			
			sharedObject = new SharedObject(id);
			fManagedObjects.put(id, sharedObject);
			
		} finally {
			SYNC.release();
		}
		
		// if we get to here without above exceptions, then all is ok
		// to get model like normal, but set 'new' attribute (where the
		// 'new' attribute means this is a model without a corresponding
		// underlying resource.
		aSharedModel = FileBufferModelManager.getInstance().getModel(iFile);
		aSharedModel.setNewState(true);
		
		sharedObject.theSharedModel=aSharedModel;
		// when resource is provided, we can set
		// synchronization stamp ... otherwise client should
		// Note: one client which does this is FileModelProvider.
		aSharedModel.resetSynchronizationStamp(iFile);
		return sharedObject;
	}

	public IStructuredModel _getModelFor(IStructuredDocument document, ReadEditType accessType) {

		String id = FileBufferModelManager.getInstance().calculateId(document);
		if (id == null) {
			if (READ == accessType)
				return getExistingModelForRead(document);
			if (EDIT == accessType)
				return getExistingModelForEdit(document);
			Assert.isNotNull(id, "unknown IStructuredDocument " + document); //$NON-NLS-1$
		}
		
		SharedObject sharedObject = null;
		SYNC.acquire();
		sharedObject = (SharedObject) fManagedObjects.get(id);
		SYNC.release();
		
		while(true) {
			if (sharedObject!=null) {
				sharedObject.waitForLoadAttempt();
			}
			SYNC.acquire();
			SharedObject testObject = (SharedObject) fManagedObjects.get(id);
			if (testObject==null) {
				sharedObject = new SharedObject(id);
				fManagedObjects.put(id, sharedObject);
				SYNC.release();
				synchronized(sharedObject) {
					sharedObject.theSharedModel = FileBufferModelManager.getInstance().getModel(document);
					_initCount(sharedObject, accessType);
					sharedObject.setLoaded();
				}
				break;
			} else if (sharedObject == testObject) {
				synchronized(sharedObject) {
					Assert.isTrue(sharedObject.referenceCountForEdit + sharedObject.referenceCountForRead > 0, "reference count was less than zero");
					if (sharedObject.theSharedModel!=null) {
						_incrCount(sharedObject, accessType);
					}
				}
				SYNC.release();
				break;
			} else {
				SYNC.release();
				sharedObject = testObject;
			}
		}
		
		return sharedObject==null ? null : sharedObject.theSharedModel;
	}

	private void _incrCount(SharedObject sharedObject, ReadEditType type) {
		synchronized(sharedObject) {
			if (type == READ) {
				sharedObject.referenceCountForRead++;
				FileBufferModelManager.getInstance().connect(sharedObject.theSharedModel.getStructuredDocument());
			}
			else if (type == EDIT) {
				sharedObject.referenceCountForEdit++;
				FileBufferModelManager.getInstance().connect(sharedObject.theSharedModel.getStructuredDocument());
			}
			else
				throw new IllegalArgumentException();
		}
	}

	private void _initCount(SharedObject sharedObject, ReadEditType type) {
		synchronized(sharedObject) {
			if (type == READ) {
				FileBufferModelManager.getInstance().connect(sharedObject.theSharedModel.getStructuredDocument());
				sharedObject.referenceCountForRead = 1;
			}
			else if (type == EDIT) {
				FileBufferModelManager.getInstance().connect(sharedObject.theSharedModel.getStructuredDocument());
				sharedObject.referenceCountForEdit = 1;
			}
			else
				throw new IllegalArgumentException();
		}
	}

	private void addFactories(IStructuredModel model, IModelHandler handler) {
		Assert.isNotNull(model, "model can not be null"); //$NON-NLS-1$
		FactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry, "model's Factory Registry can not be null"); //$NON-NLS-1$
		List factoryList = handler.getAdapterFactories();
		addFactories(model, factoryList);
	}

	private void addFactories(IStructuredModel model, List factoryList) {
		Assert.isNotNull(model, "model can not be null"); //$NON-NLS-1$
		FactoryRegistry registry = model.getFactoryRegistry();
		Assert.isNotNull(registry, "model's Factory Registry can not be null"); //$NON-NLS-1$
		// Note: we add all of them from handler, even if
		// already exists. May need to reconsider this.
		if (factoryList != null) {
			Iterator iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) iterator.next();
				registry.addFactory(factory);
			}
		}
	}


	/**
	 * Calculate id provides a common way to determine the id from the input
	 * ... needed to get and save the model. It is a simple class utility, but
	 * is an instance method so can be accessed via interface.
	 */
	public String calculateId(IFile file) {
		return FileBufferModelManager.getInstance().calculateId(file);
	}

	private IModelHandler calculateType(IFile iFile) throws CoreException {
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
		Object location = file.getLocation();
		if (location == null)
			location = file.getLocationURI();
		if (location != null)
			resolver.setFileBaseLocation(location.toString());
		return resolver;
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
			}
			catch (BadLocationException exception) {
				// just adding generic runtime here, until whole method
				// deleted.
				throw new RuntimeException(exception.getMessage());
			}
		}
	}

	/**
	 * this used to be in loader, but has been moved here
	 */
	private IStructuredModel copy(IStructuredModel model, String newId) throws ResourceInUse {
		IStructuredModel newModel = null;
		IStructuredModel oldModel = model;
		IModelHandler modelHandler = oldModel.getModelHandler();
		IModelLoader loader = modelHandler.getModelLoader();
		// newModel = loader.newModel();
		newModel = loader.createModel(oldModel);
		// newId, oldModel.getResolver(), oldModel.getModelManager());
		newModel.setModelHandler(modelHandler);
		// IStructuredDocument oldStructuredDocument =
		// oldModel.getStructuredDocument();
		// IStructuredDocument newStructuredDocument =
		// oldStructuredDocument.newInstance();
		// newModel.setStructuredDocument(newStructuredDocument);
		newModel.setResolver(oldModel.getResolver());
		newModel.setModelManager(oldModel.getModelManager());
		// duplicateFactoryRegistry(newModel, oldModel);
		newModel.setId(newId);
		// set text of new one after all initialization is done
		String contents = oldModel.getStructuredDocument().getText();
		newModel.getStructuredDocument().setText(this, contents);
		return newModel;
	}

	/**
	 */
	public IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse {
		IStructuredModel newModel = null;
		// get the existing model associated with this id
		IStructuredModel model = getExistingModel(oldId);
		// if it doesn't exist, ignore request (though this would normally
		// be a programming error.
		if (model == null)
			return null;
		SharedObject sharedObject = null;
		try {
			SYNC.acquire();
			// now be sure newModel does not exist
			sharedObject = (SharedObject) fManagedObjects.get(newId);
			if (sharedObject != null) {
				throw new ResourceInUse();
			}
			sharedObject = new SharedObject(newId);
			fManagedObjects.put(newId,sharedObject);
		} finally {
			SYNC.release();
		}
		// get loader based on existing type (note the type assumption)
		// Object type = ((IStructuredModel) model).getType();
		// IModelHandler type = model.getModelHandler();
		// IModelLoader loader = (IModelLoader) getModelLoaders().get(type);
		// IModelLoader loader = (IModelLoader) getModelLoaders().get(type);
		// ask the loader to copy
		synchronized(sharedObject) {
			sharedObject.doWait = false;
			newModel = copy(model, newId);
			sharedObject.doWait = true;
		}
		if (newModel != null) {
			// add to our cache
			synchronized(sharedObject) {
				sharedObject.theSharedModel=newModel;
				sharedObject.referenceCountForEdit = 1;
				trace("copied model", newId, sharedObject.referenceCountForEdit); //$NON-NLS-1$
			}
		} else {
			SYNC.acquire();
			fManagedObjects.remove(newId);
			SYNC.release();
		}
		sharedObject.setLoaded();
		return newModel;
	}

	/**
	 * Similar to clone, except the new instance has no content. Note: this
	 * produces an unmanaged model, for temporary use. If a true shared model
	 * is desired, use "copy".
	 */
	public IStructuredModel createNewInstance(IStructuredModel oldModel) throws IOException {
		IModelHandler handler = oldModel.getModelHandler();
		IModelLoader loader = handler.getModelLoader();
		IStructuredModel newModel = loader.createModel(oldModel);
		newModel.setModelHandler(handler);
		if (newModel instanceof AbstractStructuredModel) {
			((AbstractStructuredModel) newModel).setContentTypeIdentifier(oldModel.getContentTypeIdentifier());
		}
		URIResolver oldResolver = oldModel.getResolver();
		newModel.setResolver(oldResolver);
		try {
			newModel.setId(DUPLICATED_MODEL);
		}
		catch (ResourceInUse e) {
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
	public  IStructuredDocument createNewStructuredDocumentFor(IFile iFile) throws ResourceAlreadyExists, IOException, CoreException {
		if (iFile.exists()) {
			throw new ResourceAlreadyExists(iFile.getFullPath().toOSString());
		}
		// Will reconsider in future version
		// String id = calculateId(iFile);
		// if (isResourceInUse(id)) {
		// throw new ResourceInUse(iFile.getFullPath().toOSString());
		// }
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
	public  IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException {
		if (!iFile.exists()) {
			throw new FileNotFoundException(iFile.getFullPath().toOSString());
		}
		// Will reconsider in future version
		// String id = calculateId(iFile);
		// if (isResourceInUse(id)) {
		// throw new ResourceInUse(iFile.getFullPath().toOSString());
		// }
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
	public  IStructuredDocument createStructuredDocumentFor(String contentTypeId) {
		IDocumentLoader loader = null;
		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler handler = cr.getHandlerForContentTypeId(contentTypeId);
		if (handler == null)
			Logger.log(Logger.ERROR, "Program error: no model handler found for " + contentTypeId); //$NON-NLS-1$
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
		}
		else {
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
	public  IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String encoding) throws IOException {
		String content = readInputStream(inputStream, encoding);
		IStructuredDocument result = createStructuredDocumentFor(filename, content, resolver);
		return result;
	}

	/**
	 * Convenience method. This method can be used when the resource does not
	 * really exist (e.g. when content is being created, but hasn't been
	 * written to disk yet). Note that since the content is being provided as
	 * a String, it is assumed to already be decoded correctly so no
	 * transformation is done.
	 */
	public  IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException {
		// TODO: avoid all these String instances
		StringBuffer contentBuffer = new StringBuffer(content);
		IDocumentLoader loader = null;
		IModelHandler handler = calculateType(filename, null);
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
		}
		catch (ResourceInUse e) {
			// impossible, since we're not sharing
			// (even if it really is in use ... we don't care)
			// this may need to be re-examined.
			if (Logger.DEBUG_MODELMANAGER)
				Logger.log(Logger.INFO, "ModelMangerImpl::createUnManagedStructuredModelFor. Model unexpectedly in use."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return result;
	}

	/**
	 * Conveience method. It depends on the loaders newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 */
	public IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException {
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
	public  IStructuredModel createUnManagedStructuredModelFor(String contentTypeId) {
		return createUnManagedStructuredModelFor(contentTypeId, null);
	}

	/**
	 * Conveience method. It depends on the loaders newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 */
	public  IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver) {
		IStructuredModel result = null;
		ModelHandlerRegistry cr = getModelHandlerRegistry();
		IModelHandler handler = cr.getHandlerForContentTypeId(contentTypeId);
		try {
			result = _commonCreateModel(UNMANAGED_MODEL, handler, resolver); //$NON-NLS-1$
		}
		catch (ResourceInUse e) {
			// impossible, since we're not sharing
			// (even if it really is in use ... we don't care)
			// this may need to be re-examined.
			if (Logger.DEBUG_MODELMANAGER)
				Logger.log(Logger.INFO, "ModelMangerImpl::createUnManagedStructuredModelFor. Model unexpectedly in use."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}

	private IStructuredModel getExistingModel(Object id) {
		IStructuredModel result = null;
		
		SYNC.acquire();
		/**
		 * While a good check in theory, it's possible for an event fired to
		 * cause a listener to access a method that calls this one.
		 */
		//Assert.isTrue(SYNC.getDepth()==1, "depth not equal to 1");
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		// if not, then we'll simply return null
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			result = sharedObject.theSharedModel;
		} else {
			SYNC.release();
		}
		
		return result;
	}

	/**
	 * Note: users of this 'model' must still release it when finished.
	 * Returns null if there's not a model corresponding to document.
	 */
	public IStructuredModel getExistingModelForEdit(IDocument document) {
		IStructuredModel result = null;
		
		SYNC.acquire();		
		// create a snapshot
		Set ids = new HashSet(fManagedObjects.keySet());
		SYNC.release();
		for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
			Object potentialId = iterator.next();
			SYNC.acquire();	
			if (fManagedObjects.containsKey(potentialId)) {
				// check to see if still valid
				SYNC.release();
				IStructuredModel tempResult = getExistingModel(potentialId);
				if (tempResult!=null && document == tempResult.getStructuredDocument()) {
					result = getExistingModelForEdit(potentialId);
					break;
				}
			} else {
				SYNC.release();
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
	public  IStructuredModel getExistingModelForEdit(IFile iFile) {

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
	public IStructuredModel getExistingModelForEdit(Object id) {

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		IStructuredModel result = null;
		boolean doRelease = true;
		// let's see if we already have it in our cache
		try {
			SYNC.acquire();
			SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
			// if not, then we'll simply return null
			if (sharedObject != null) {
				// if shared object is in our cache, then simply increment its ref
				// count,
				// and return the object.
				SYNC.release();
				doRelease=false;
				synchronized(sharedObject) {
					if (sharedObject.doWait) {
						sharedObject.waitForLoadAttempt();
					}
					if (sharedObject.theSharedModel!=null) {
						_incrCount(sharedObject, EDIT);
					}
					result = sharedObject.theSharedModel;
				}
				trace("got existing model for Edit: ", id); //$NON-NLS-1$
				trace("   incremented referenceCountForEdit ", id, sharedObject.referenceCountForEdit); //$NON-NLS-1$
			}
		} finally {
			if (doRelease) {
				SYNC.release();
			}
		}
		
		return result;
	}

	/**
	 * Note: users of this 'model' must still release it when finished.
	 * Returns null if there's not a model corresponding to document.
	 */
	public IStructuredModel getExistingModelForRead(IDocument document) {
		IStructuredModel result = null;
		
		SYNC.acquire();		
		// create a snapshot
		Set ids = new HashSet(fManagedObjects.keySet());
		SYNC.release();
		for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
			Object potentialId = iterator.next();
			SYNC.acquire();	
			if (fManagedObjects.containsKey(potentialId)) {
				// check to see if still valid
				SYNC.release();
				IStructuredModel tempResult = getExistingModel(potentialId);
				if (tempResult!=null && document == tempResult.getStructuredDocument()) {
					result = getExistingModelForRead(potentialId);
					break;
				}
			} else {
				SYNC.release();
			}
		}
		
		return result;
	}

	public IStructuredModel getExistingModelForRead(IFile iFile) {

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
	public  IStructuredModel getExistingModelForRead(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		IStructuredModel result = null;
		boolean doRelease = true;
		// let's see if we already have it in our cache
		try {
			SYNC.acquire();
			SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
			// if not, then we'll simply return null
			if (sharedObject != null) {
				// if shared object is in our cache, then simply increment its ref
				// count,
				// and return the object.
				SYNC.release();
				doRelease=false;

				synchronized(sharedObject) {
					if (sharedObject.doWait) {
						sharedObject.waitForLoadAttempt();
					}
					if (sharedObject.theSharedModel!=null) {
						_incrCount(sharedObject, READ);
					}
					result = sharedObject.theSharedModel;
				}
			}
		} finally {
			if (doRelease)
				SYNC.release();
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
	public  Enumeration getExistingModelIds() {
		try {
			SYNC.acquire();
			// create a copy
			Vector keys = new Vector( fManagedObjects.keySet() );
			return keys.elements();
		} finally {
			SYNC.release();
		}
	}

	// TODO: replace (or supplement) this is a "model info" association to the
	// IFile that created the model
	private IFile getFileFor(IStructuredModel model) {
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
	public  IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, null, null);
	}

	public  IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, encodingRule);
	}

	public  IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, EDIT, encoding, lineDelimiter);
	}

	public  IStructuredModel getModelForEdit(IStructuredDocument document) {
		return _getModelFor(document, EDIT);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile or String form
	 */
	public  IStructuredModel getModelForEdit(Object id, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {

		Assert.isNotNull(id, "requested model id can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForEdit(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	/**
	 * @see IModelManager
	 * @deprecated - use IFile or String form
	 */
	public  IStructuredModel getModelForEdit(Object id, Object modelType, String encodingName, String lineDelimiter, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForEdit(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	public  IStructuredModel getModelForEdit(String id, InputStream inputStream, URIResolver resolver) throws IOException {
		if (id == null) {
			throw new IllegalArgumentException("Program Error: id may not be null"); //$NON-NLS-1$
		}
		IStructuredModel result = null;

		InputStream istream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler handler = calculateType(id, istream);
		if (handler != null) {
			result = _commonCreateModel(istream, id, handler, resolver, EDIT, null, null);
		}
		else {
			Logger.log(Logger.INFO, "no model handler found for id"); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * One of the primary forms to get a managed model
	 */
	public  IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, null, null);
	}

	public  IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, encodingRule);
	}

	public  IStructuredModel getModelForRead(IFile iFile, String encodingName, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		return _commonGetModel(iFile, READ, encodingName, lineDelimiter);
	}

	public  IStructuredModel getModelForRead(IStructuredDocument document) {
		return _getModelFor(document, READ);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile or String form
	 */
	public  IStructuredModel getModelForRead(Object id, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForRead(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	/**
	 * @see IModelManager
	 * @deprecated use IFile form
	 */
	public  IStructuredModel getModelForRead(Object id, Object modelType, String encodingName, String lineDelimiter, InputStream inputStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		String stringId = id.toString();
		return getModelForRead(stringId, Utilities.getMarkSupportedStream(inputStream), resolver);
	}

	public  IStructuredModel getModelForRead(String id, InputStream inputStream, URIResolver resolver) throws IOException {
		InputStream istream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler handler = calculateType(id, istream);
		IStructuredModel result = null;
		result = _commonCreateModel(istream, id, handler, resolver, READ, null, null);
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
	public  IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = _commonNewModel(iFile, force);
		synchronized(sharedObject) {
			sharedObject.referenceCountForEdit = 1;
		}
		sharedObject.setLoaded();
		return sharedObject.theSharedModel;
	}

	/**
	 * @see IModelManager#getNewModelForRead(IFile, boolean)
	 */
	public  IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {

		Assert.isNotNull(iFile, "IFile parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = _commonNewModel(iFile, force);
		SYNC.acquire();
		synchronized(sharedObject) {
			if (sharedObject.theSharedModel!=null) {
				sharedObject.referenceCountForRead = 1;
			}
		}
		SYNC.release();
		sharedObject.setLoaded();
		return sharedObject.theSharedModel;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public  int getReferenceCount(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
	
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			SYNC.acquire();
			synchronized (sharedObject) {
				count = sharedObject.referenceCountForRead + sharedObject.referenceCountForEdit;
			}
		}
		SYNC.release();
		return count;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public int getReferenceCountForEdit(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			synchronized(sharedObject) {
				count = sharedObject.referenceCountForEdit;
			}
		} else {
			SYNC.release();
		}
		return count;
	}

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	public int getReferenceCountForRead(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			SYNC.acquire();
			synchronized(sharedObject) {
				count = sharedObject.referenceCountForRead;
			}
		}
		SYNC.release();
		return count;
	}

	private void handleConvertLineDelimiters(IStructuredDocument structuredDocument, IFile iFile, EncodingRule encodingRule, EncodingMemento encodingMemento) throws CoreException, MalformedOutputExceptionWithDetail, UnsupportedEncodingException {
		if (structuredDocument.getNumberOfLines() > 1) {
			convertLineDelimiters(structuredDocument, iFile);
		}
	}

	private void handleProgramError(Throwable t) {

		Logger.logException("Impossible Program Error", t); //$NON-NLS-1$
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public  boolean isShared(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		boolean result = false;
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			SYNC.acquire();
			synchronized(sharedObject) {
				count = sharedObject.referenceCountForRead + sharedObject.referenceCountForEdit;
			}
		}
		SYNC.release();
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
	public  boolean isSharedForEdit(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		boolean result = false;
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			synchronized(sharedObject) {
				count = sharedObject.referenceCountForEdit;
			}
		} else {
			SYNC.release();
		}
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
	public  boolean isSharedForRead(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		int count = 0;
		boolean result = false;
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject != null) {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			SYNC.acquire();
			synchronized(sharedObject) {
				count = sharedObject.referenceCountForRead;
			}
		}
		SYNC.release();
		result = count > 1;
		return result;
	}

	/**
	 * This method can be called to determine if the model manager is within a
	 * "aboutToChange" and "changed" sequence.
	 * 
	 * @deprecated the manager does not otherwise interact with these states
	 * @return false
	 */
	public boolean isStateChanging() {
		// doesn't seem to be used anymore
		return false;
	}

	/**
	 * This method changes the id of the model. TODO: try to refine the design
	 * not to use this function
	 */
	public void moveModel(Object oldId, Object newId) {
		Assert.isNotNull(oldId, "old id parameter can not be null"); //$NON-NLS-1$
		Assert.isNotNull(newId, "new id parameter can not be null"); //$NON-NLS-1$
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(oldId);
		// if not found in cache, ignore request.
		// this would normally be a program error
		if (sharedObject != null) {
			fManagedObjects.remove(oldId);
			fManagedObjects.put(newId, sharedObject);
		}
		SYNC.release();
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
		}
		else {
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
	public IStructuredModel reinitialize(IStructuredModel model) {

		// getHandler (assume its the "new one")
		IModelHandler handler = model.getModelHandler();
		// getLoader for that new one
		IModelLoader loader = handler.getModelLoader();
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

	 void releaseFromEdit(IStructuredModel structuredModel) {
		Object id = structuredModel.getId();
		if (id.equals(UNMANAGED_MODEL) || id.equals(DUPLICATED_MODEL)) {
			cleanupDiscardedModel(structuredModel);
		}
		else {
			releaseFromEdit(id);
		}

	}
	
	 void releaseFromRead(IStructuredModel structuredModel) {
		Object id = structuredModel.getId();
		if (id.equals(UNMANAGED_MODEL) || id.equals(DUPLICATED_MODEL)) {
			cleanupDiscardedModel(structuredModel);
		}
		else {
			releaseFromRead(id);
		}

	}
	/**
	 * default for use in same package, not subclasses
	 * 
	 */
	 private void releaseFromEdit(Object id) {
		// ISSUE: many of these asserts should be changed to "logs"
		// and continue to limp along?

		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = null;

		// ISSUE: here we need better "spec" what to do with
		// unmanaged or duplicated models. Release still needs
		// to be called on them, for now, but the model manager
		// doesn't need to do anything.
		if (id.equals(UNMANAGED_MODEL) || id.equals(DUPLICATED_MODEL)) {
			throw new IllegalArgumentException("Ids of UNMANAGED_MODEL or DUPLICATED_MODEL are illegal here");
		}
		else {
			SYNC.acquire();
			sharedObject = (SharedObject) fManagedObjects.get(id);
			SYNC.release();
			
			Assert.isNotNull(sharedObject, "release was requested on a model that was not being managed"); //$NON-NLS-1$
			sharedObject.waitForLoadAttempt();
			SYNC.acquire();
			synchronized(sharedObject) {
				_decrCount(sharedObject, EDIT);
				if ((sharedObject.referenceCountForRead == 0) && (sharedObject.referenceCountForEdit == 0)) {
					discardModel(id, sharedObject);
				}
			}
			SYNC.release();
			// if edit goes to zero, but still open for read,
			// then we should reload here, so we are in synch with
			// contents on disk.
			// ISSUE: should we check isDirty here?
			// ANSWER: here, for now now. model still has its own dirty
			// flag for some reason.
			// we need to address * that * too.

			synchronized(sharedObject) {
				if ((sharedObject.referenceCountForRead > 0) && (sharedObject.referenceCountForEdit == 0) && sharedObject.theSharedModel.isDirty()) {
					signalPreLifeCycleListenerRevert(sharedObject.theSharedModel);
					revertModel(id, sharedObject);
					/*
					 * Because model events are fired to notify about the
					 * revert's changes, and listeners can still get/release
					 * the model from this thread (locking prevents it being
					 * done from other threads), the reference counts could
					 * have changed since we entered this if block, and the
					 * model could have been discarded.  Check the counts again.
					 */
					if (sharedObject.referenceCountForRead > 0 && sharedObject.referenceCountForEdit == 0) {
						sharedObject.theSharedModel.setDirtyState(false);
					}
					signalPostLifeCycleListenerRevert(sharedObject.theSharedModel);
				}
			}
			
		}
	}

	// private for now, though public forms have been requested, in past.
	private void revertModel(Object id, SharedObject sharedObject) {
		IStructuredDocument structuredDocument = sharedObject.theSharedModel.getStructuredDocument();
		FileBufferModelManager.getInstance().revert(structuredDocument);
	}

	private void signalPreLifeCycleListenerRevert(IStructuredModel structuredModel) {
		int type = ModelLifecycleEvent.MODEL_REVERT | ModelLifecycleEvent.PRE_EVENT;
		// what's wrong with this design that a cast is needed here!?
		ModelLifecycleEvent event = new ModelLifecycleEvent(structuredModel, type);
		((AbstractStructuredModel) structuredModel).signalLifecycleEvent(event);
	}

	private void signalPostLifeCycleListenerRevert(IStructuredModel structuredModel) {
		int type = ModelLifecycleEvent.MODEL_REVERT | ModelLifecycleEvent.POST_EVENT;
		// what's wrong with this design that a cast is needed here!?
		ModelLifecycleEvent event = new ModelLifecycleEvent(structuredModel, type);
		((AbstractStructuredModel) structuredModel).signalLifecycleEvent(event);
	}

	private void discardModel(Object id, SharedObject sharedObject) {
		SYNC.acquire();
		fManagedObjects.remove(id);
		SYNC.release();
		IStructuredDocument structuredDocument = sharedObject.theSharedModel.getStructuredDocument();

		if (structuredDocument == null) {
			Platform.getLog(SSECorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, SSECorePlugin.ID, IStatus.ERROR, "Attempted to discard a structured model but the underlying document has already been set to null: " + sharedObject.theSharedModel.getBaseLocation(), null));
		}

		cleanupDiscardedModel(sharedObject.theSharedModel);
	}

	private void cleanupDiscardedModel(IStructuredModel structuredModel) {
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
		/*
		 * This call (and setting the StructuredDocument to null) were
		 * previously done within the model itself, but for concurrency it
		 * must be done here during a synchronized release.
		 */
		structuredModel.getFactoryRegistry().release();

		/*
		 * For structured documents originating from file buffers, disconnect
		 * us from the file buffer, now.
		 */
		FileBufferModelManager.getInstance().releaseModel(structuredDocument);

		/*
		 * Setting the document to null is required since some subclasses of
		 * model might have "cleanup" of listeners, etc., to remove, which
		 * were initialized during the initial setStructuredDocument.
		 * 
		 * The model itself in particular may have internal listeners used to
		 * coordinate the document with its own "structure".
		 */
		structuredModel.setStructuredDocument(null);
	}

	
	/**
	 * default for use in same package, not subclasses
	 * 
	 */
	 private void releaseFromRead(Object id) {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$
		SharedObject sharedObject = null;

		if (id.equals(UNMANAGED_MODEL) || id.equals(DUPLICATED_MODEL)) {
			throw new IllegalArgumentException("Ids of UNMANAGED_MODEL or DUPLICATED_MODEL are illegal here");
		}
		else {
			SYNC.acquire();
			sharedObject = (SharedObject) fManagedObjects.get(id);
			SYNC.release();
			Assert.isNotNull(sharedObject, "release was requested on a model that was not being managed"); //$NON-NLS-1$
			sharedObject.waitForLoadAttempt();
		}
		SYNC.acquire();
		synchronized(sharedObject) {
			_decrCount(sharedObject, READ);
			if ((sharedObject.referenceCountForRead == 0) && (sharedObject.referenceCountForEdit == 0)) {
				discardModel(id, sharedObject);
			}
		}
		SYNC.release();
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
	public  IStructuredModel reloadModel(Object id, java.io.InputStream inputStream) throws java.io.UnsupportedEncodingException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$

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
			IModelLoader loader = handler.getModelLoader();
			// ask the loader to re-load
			loader.reload(Utilities.getMarkSupportedStream(inputStream), structuredModel);
			trace("re-loading model", id); //$NON-NLS-1$
		}
		return structuredModel;
	}

	public void saveModel(IFile iFile, String id, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		Assert.isNotNull(iFile, "file parameter can not be null"); //$NON-NLS-1$
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$

		// let's see if we already have it in our cache
	
		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null || sharedObject.theSharedModel == null) {
			SYNC.release();
			throw new IllegalStateException(SSECoreMessages.Program_Error__ModelManage_EXC_); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		} 
		else {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			
			/**
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=221610
			 * 
			 * Sync removed from here to prevent deadlock. Although the model
			 * instance may disappear or be made invalid while the save is
			 * happening, the document itself still has the contents we're
			 * trying to save. Simultaneous saves should be throttled by
			 * resource locking without our intervention.
			 */
			boolean saved = false;
			// if this model was based on a File Buffer and we're writing back
			// to the same location, use the buffer to do the writing
			if (FileBufferModelManager.getInstance().isExistingBuffer(sharedObject.theSharedModel.getStructuredDocument())) {
				ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(sharedObject.theSharedModel.getStructuredDocument());
				IPath fileLocation = FileBuffers.normalizeLocation(iFile.getFullPath());
				if (fileLocation.equals(buffer.getLocation())) {
					buffer.commit(new NullProgressMonitor(), true);
					saved = true;
				}
			}
			if (!saved) {
				IStructuredModel model = sharedObject.theSharedModel;
				IStructuredDocument document = model.getStructuredDocument();
				saveStructuredDocument(document, iFile, encodingRule);
				trace("saving model", id); //$NON-NLS-1$
			}
			sharedObject.theSharedModel.setDirtyState(false);
			sharedObject.theSharedModel.setNewState(false);
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
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$

		// let's see if we already have it in our cache

		SYNC.acquire();
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null) {
			SYNC.release();
			throw new IllegalStateException(SSECoreMessages.Program_Error__ModelManage_EXC_); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		}
		else {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			/**
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=221610
			 * 
			 * Sync removed from here to prevent deadlock. Although the model
			 * instance may disappear or be made invalid while the save is
			 * happening, the document itself still has the contents we're
			 * trying to save. Simultaneous saves should be throttled by
			 * resource locking without our intervention.
			 */
			/*
			 * if this model was based on a File Buffer and we're writing back
			 * to the same location, use the buffer to do the writing
			 */
			if (FileBufferModelManager.getInstance().isExistingBuffer(sharedObject.theSharedModel.getStructuredDocument())) {
				ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(sharedObject.theSharedModel.getStructuredDocument());
				buffer.commit(new NullProgressMonitor(), true);
			}
			else {
				IFile iFile = getFileFor(sharedObject.theSharedModel);
				IStructuredModel model = sharedObject.theSharedModel;
				IStructuredDocument document = model.getStructuredDocument();
				saveStructuredDocument(document, iFile);
				trace("saving model", id); //$NON-NLS-1$
			}
			sharedObject.theSharedModel.setDirtyState(false);
			sharedObject.theSharedModel.setNewState(false);
		}
	}

	/**
	 * @deprecated - this method is less efficient than IFile form, since it
	 *             requires an extra "copy" of byte array, and should be avoid
	 *             in favor of the IFile form.
	 */
	public void saveModel(String id, OutputStream outputStream, EncodingRule encodingRule) throws UnsupportedEncodingException, CoreException, IOException {
		Assert.isNotNull(id, "id parameter can not be null"); //$NON-NLS-1$

		SYNC.acquire();
		// let's see if we already have it in our cache
		SharedObject sharedObject = (SharedObject) fManagedObjects.get(id);
		if (sharedObject == null) {
			SYNC.release();
			throw new IllegalStateException(SSECoreMessages.Program_Error__ModelManage_EXC_); //$NON-NLS-1$ = "Program Error: ModelManagerImpl::saveModel. Model should be in the cache"
		}
		else {
			SYNC.release();
			sharedObject.waitForLoadAttempt();
			synchronized(sharedObject) {
				CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
				codedStreamCreator.set(sharedObject.theSharedModel.getId(), new DocumentReader(sharedObject.theSharedModel.getStructuredDocument()));
				codedStreamCreator.setPreviousEncodingMemento(sharedObject.theSharedModel.getStructuredDocument().getEncodingMemento());
				ByteArrayOutputStream byteArrayOutputStream = codedStreamCreator.getCodedByteArrayOutputStream(encodingRule);
				byte[] outputBytes = byteArrayOutputStream.toByteArray();
				outputStream.write(outputBytes);
				trace("saving model", id); //$NON-NLS-1$
				sharedObject.theSharedModel.setDirtyState(false);
				sharedObject.theSharedModel.setNewState(false);
			}
		}
	}

	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, CoreException, IOException {
		saveStructuredDocument(structuredDocument, iFile, EncodingRule.CONTENT_BASED);
	}

	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, CoreException, IOException {
		Assert.isNotNull(iFile, "file parameter can not be null"); //$NON-NLS-1$
		if (FileBufferModelManager.getInstance().isExistingBuffer(structuredDocument)) {
			ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(structuredDocument);
			if (buffer.getLocation().equals(iFile.getFullPath()) || buffer.getLocation().equals(iFile.getLocation())) {
				buffer.commit(new NullProgressMonitor(), true);
			}
		}
		else {
			// IModelHandler handler = calculateType(iFile);
			// IDocumentDumper dumper = handler.getDocumentDumper();
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
		if (Logger.DEBUG_MODELMANAGER) {
			Logger.log(Logger.INFO, msg + " " + Utilities.makeShortId(id)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Common trace method
	 */
	private void trace(String msg, Object id, int value) {
		if (Logger.DEBUG_MODELMANAGER) {
			Logger.log(Logger.INFO, msg + Utilities.makeShortId(id) + " (" + value + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	 boolean isIdInUse(String newId) {
			boolean inUse = false;
			SYNC.acquire();
			SharedObject object =(SharedObject) fManagedObjects.get(newId);
			if (object!=null) {
				inUse = object.theSharedModel!=null;
			}
			SYNC.release();
			return inUse;
		}
}
