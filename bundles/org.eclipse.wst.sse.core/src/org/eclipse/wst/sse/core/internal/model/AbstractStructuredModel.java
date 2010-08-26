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
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ILockable;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.DocumentChanged;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.AboutToBeChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.internal.util.Utilities;


public abstract class AbstractStructuredModel implements IStructuredModel {

	private static final String MODEL_MANAGER_NULL = "Warning: AbstractStructuredModel::close:  model manager was null during a close of a model (which should be impossible)"; //$NON-NLS-1$

	class DirtyStateWatcher implements IStructuredDocumentListener {

		public void newModel(NewDocumentEvent structuredDocumentEvent) {

			// I don't think its safe to assume a new model
			// is always "fresh", so we'll leave dirty state
			// unchanged;
			// but we'll tell everyone about it.
			setDirtyState(fDirtyState);
		}

		public void noChange(NoChangeEvent structuredDocumentEvent) {

			// don't change dirty state
		}

		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {

			setDirtyState(true);
			// no need to listen any more
			if (fStructuredDocument != null) {
				fStructuredDocument.removeDocumentChangedListener(fDirtyStateWatcher);
			}
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {

			setDirtyState(true);
			// no need to listen any more
			if (fStructuredDocument != null) {
				fStructuredDocument.removeDocumentChangedListener(fDirtyStateWatcher);
			}
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {

			setDirtyState(true);
			// no need to listen any more
			if (fStructuredDocument != null) {
				fStructuredDocument.removeDocumentChangedListener(fDirtyStateWatcher);
			}
		}
	}

	class DocumentToModelNotifier implements IStructuredDocumentListener, IModelAboutToBeChangedListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IModelAboutToBeChangedListener#modelAboutToBeChanged(org.eclipse.wst.sse.core.events.AboutToBeChangedEvent)
		 */
		public void modelAboutToBeChanged(AboutToBeChangedEvent structuredDocumentEvent) {
			// If we didn't originate the change, take note we are about to
			// change based on our underlying document changing.
			// If we did originate the change, we, or client, should have
			// already called aboutToChangeModel.
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				aboutToChangeModel();
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#newModel(org.eclipse.wst.sse.core.events.NewDocumentEvent)
		 */
		public void newModel(NewDocumentEvent structuredDocumentEvent) {
			// if we didn't originate the change, take note we have changed
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				changedModel();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#noChange(org.eclipse.wst.sse.core.events.NoChangeEvent)
		 */
		public void noChange(NoChangeEvent structuredDocumentEvent) {
			// if we didn't originate the change, take note we have changed
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				changedModel();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#nodesReplaced(org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent)
		 */
		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			// if we didn't originate the change, take note we have changed
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				changedModel();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#regionChanged(org.eclipse.wst.sse.core.events.RegionChangedEvent)
		 */
		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			// if we didn't originate the change, take note we have changed
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				changedModel();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#regionsReplaced(org.eclipse.wst.sse.core.events.RegionsReplacedEvent)
		 */
		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			// if we didn't originate the change, take note we have changed
			if (structuredDocumentEvent.getOriginalRequester() != this) {
				changedModel();
			}
		}

	}

	private FactoryRegistry factoryRegistry;
	private String fBaseLocation;
	boolean fDirtyState;
	DirtyStateWatcher fDirtyStateWatcher;
	DocumentToModelNotifier fDocumentToModelNotifier;
	private String fExplicitContentTypeIdentifier;
	private String fId;

	private LifecycleNotificationManager fLifecycleNotificationManager;

	private final Object fListenerLock = new byte[0];
	protected ILock fLockObject;
	// private String fLineDelimiter;
	// private Object fType;
	private IModelHandler fModelHandler;
	// issue: we should not "hold on" to model manager, can 
	// easily get with StructuredModelManager.getModelManager();
	// but will need to add more null checks.
	private IModelManager fModelManager;
	private int fModelStateChanging;
	private Object[] fModelStateListeners;
	private boolean fNewState = false;
	private URIResolver fResolver;
	protected IStructuredDocument fStructuredDocument;
	/**
	 * The time stamp of the underlying resource's modification date, at the
	 * time this model was created, or the last time it was saved. Note: for
	 * this version, this variable is not set automatically, be needs to be
	 * managed by client. The FileModelProvider does this for most cases, but
	 * if client do not use FileModelProvider, they must set this variable
	 */
	public long fSynchronizationStamp = IResource.NULL_STAMP;
	private boolean reinitializationNeeded;
	private Object reinitializeStateData;

	/**
	 * AbstractStructuredModel constructor comment.
	 */
	public AbstractStructuredModel() {

		super();
		fDirtyStateWatcher = new DirtyStateWatcher();
		fDocumentToModelNotifier = new DocumentToModelNotifier();
	}


	/**
	 * This method is just for getting an instance of the model manager of the
	 * right Impl type, to be used "internally" for making protected calls
	 * directly to the impl class.
	 */
	private ModelManagerImpl _getModelManager() {
		// TODO_future: redesign so we don't need this 'Impl' version
		if (fModelManager == null) {
			fModelManager = StructuredModelManager.getModelManager();
		}

		return (ModelManagerImpl) fModelManager;
	}

	/**
	 * This API allows clients to declare that they are about to make a
	 * "large" change to the model. This change might be in terms of content
	 * or it might be in terms of the model id or base location. Note that in
	 * the case of embedded calls, notification to listeners is sent only
	 * once. Note that the client who is making these changes has the
	 * responsibility to restore the models state once finished with the
	 * changes. See getMemento and restoreState. The method
	 * isModelStateChanging can be used by a client to determine if the model
	 * is already in a change sequence.
	 */
	public void aboutToChangeModel() {


		// notice this is just a public avenue to our protected method
		internalAboutToBeChanged();
	}


	public void aboutToReinitializeModel() {



		// notice this is just a public avenue to our protected method
		fireModelAboutToBeReinitialized();
	}


	public void addModelLifecycleListener(IModelLifecycleListener listener) {

		synchronized (fListenerLock) {
			if (fLifecycleNotificationManager == null) {
				fLifecycleNotificationManager = new LifecycleNotificationManager();
			}
			fLifecycleNotificationManager.addListener(listener);
		}
	}

	public void addModelStateListener(IModelStateListener listener) {

		synchronized (fListenerLock) {

			if (!Utilities.contains(fModelStateListeners, listener)) {
				int oldSize = 0;
				if (fModelStateListeners != null) {
					// normally won't be null, but we need to be sure, for
					// first
					// time through
					oldSize = fModelStateListeners.length;
				}
				int newSize = oldSize + 1;
				Object[] newListeners = new Object[newSize];
				if (fModelStateListeners != null) {
					System.arraycopy(fModelStateListeners, 0, newListeners, 0, oldSize);
				}
				// add listener to last position
				newListeners[newSize - 1] = listener;
				//
				// now switch new for old
				fModelStateListeners = newListeners;
			}
		}
	}

	/**
	 * This lock to lock the small bits of data and operations in the models
	 * themselfes. this lock is "shared" with document, so, eventually,
	 * changes can be made safefly from either side.
	 */
	protected final void beginLock() {

		// if we get a different lock object
		// than we had before, besure to release
		// old one first before losing it.
		// ISSUE: this smells like an error condition,
		// when would this happen? better to check in set document?
		ILock documentLock = getLockObjectFromDocument();

		if (fLockObject != null && fLockObject != documentLock) {
			fLockObject.release();
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "Model lock released early" + fLockObject + " apparently document switched?"); //$NON-NLS-1$ //$NON-NLS-2$
			}

		}
		fLockObject = documentLock;
		if (fLockObject != null) {
			fLockObject.acquire();
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "Model lock acquired: " + fLockObject); //$NON-NLS-1$
			}
		}
	}

	public void beginRecording(Object requester) {

		beginRecording(requester, null, null);
	}

	public void beginRecording(Object requester, int cursorPosition, int selectionLength) {

		beginRecording(requester, null, null, cursorPosition, selectionLength);
	}

	public void beginRecording(Object requester, String label) {

		beginRecording(requester, label, null);
	}

	public void beginRecording(Object requester, String label, int cursorPosition, int selectionLength) {

		beginRecording(requester, label, null, cursorPosition, selectionLength);
	}

	public void beginRecording(Object requester, String label, String description) {

		if (getUndoManager() != null)
			getUndoManager().beginRecording(requester, label, description);
	}

	public void beginRecording(Object requester, String label, String description, int cursorPosition, int selectionLength) {

		if (getUndoManager() != null)
			getUndoManager().beginRecording(requester, label, description, cursorPosition, selectionLength);
	}

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and *must* be called after aboutToChangeModel
	 * ... or some listeners could be left waiting indefinitely for the
	 * changed event. So, its suggested that changedModel always be in a
	 * finally clause. Likewise, a client should never call changedModel
	 * without calling aboutToChangeModel first. In the case of embedded
	 * calls, the notification is just sent once.
	 */
	public void changedModel() {


		// notice this is just a public avenue to our protected method
		internalModelChanged();
		// also note!
		// if we've been "changed" by a client, we might still need
		// to be re-initialized, so we'll check and handle that here.
		// Note only does this provide a solution to some "missed"
		// re-inits, in provides a built in way for clients to
		// "force" the model to handle itself, by bracketing any
		// changes with aboutToChange and changed, the model itself
		// will check. But only call re-init if all other pending
		// modelChanged states have been handled.
		if (fModelStateChanging == 0 && isReinitializationNeeded()) {
			reinit();
		}
	}


	/**
	 * Based on similar method in FileDocumentProvider. It will provide what
	 * the modificationStamp would be if resetSynchronzationStamp(resource)
	 * were used, although for this 'compute' API, no changes to the instance
	 * are made.
	 */
	public long computeModificationStamp(IResource resource) {


		long modificationStamp = resource.getModificationStamp();
		IPath path = resource.getLocation();
		if (path == null) {
			return modificationStamp;
		}
		// Note: checking existence of file is a little different than
		// impl in
		// the FileDocumentProvider. See defect number 223790.
		File file = path.toFile();
		if (!file.exists()) {
			return modificationStamp;
		}
		modificationStamp = file.lastModified();
		return modificationStamp;
	}


	/**
	 * Provides a copy of the model, but a new ID must be provided. The
	 * principle of this copy is not to copy fields, etc., as is typically
	 * done in a clone method, but to return a model with the same content in
	 * the structuredDocument. Note: It is the callers responsibility to
	 * setBaseLocation, listners, etc., as appropriate. Type and Encoding are
	 * the only fields set by this method. If the newId provided already exist
	 * in the model manager, a ResourceInUse exception is thrown.
	 */
	public IStructuredModel copy(String newId) throws ResourceInUse {


		IStructuredModel newModel = null;
		// this first one should fail, if not, its treated as an error
		// If the caller wants to use an existing one, they can call
		// getExisting
		// after this failure
		newModel = getModelManager().getExistingModelForEdit(newId);
		if (newModel != null) {
			// be sure to release the reference we got "by accident" (and
			// no
			// longer need)
			newModel.releaseFromEdit();
			throw new ResourceInUse();
		}
		newModel = getModelManager().copyModelForEdit(getId(), newId);
		return newModel;
	}


	/**
	 * Disable undo management.
	 */
	public void disableUndoManagement() {

		if (getUndoManager() != null)
			getUndoManager().disableUndoManagement();
	}

	/**
	 * Enable undo management.
	 */
	public void enableUndoManagement() {

		if (getUndoManager() != null)
			getUndoManager().enableUndoManagement();
	}

	/**
	 * endLock is protected only for a very special purpose. So subclasses can
	 * call it to end the lock after updates have been made, but before
	 * notifications are sent
	 * 
	 */
	protected final void endLock() {
		if (fLockObject != null) {
			fLockObject.release();
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "Model lock released: " + fLockObject); //$NON-NLS-1$
			}

		}
	}

	public void endRecording(Object requester) {

		if (getUndoManager() != null)
			getUndoManager().endRecording(requester);
	}

	public void endRecording(Object requester, int cursorPosition, int selectionLength) {

		if (getUndoManager() != null)
			getUndoManager().endRecording(requester, cursorPosition, selectionLength);
	}

	/**
	 * Informs all registered model state listeners that the the model is
	 * about to under go a change. This change might be in terms of contents
	 * or might be in terms of the model's id or base location.
	 */
	private void fireModelAboutToBeChanged() {

		// we must assign listeners to local variable, since the add and
		// remove listner
		// methods can change the actual instance of the listener array
		// from another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelAboutToBeChanged(this);
			}
		}

	}

	protected void fireModelAboutToBeReinitialized() {

		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelAboutToBeReinitialized"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				// NOTE: trick for transition. We actual use the same
				// listeners
				// as modelState, but only send this to those that have
				// implemented ModelStateExtended.
				IModelStateListener listener = (IModelStateListener) holdListeners[i];
				listener.modelAboutToBeReinitialized(this);
			}
		}
	}

	private void fireModelChanged() {
		// we must assign listeners
		// to local variable, since the add
		// and remove listner
		// methods can change the actual instance of the listener
		// array from another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				try {
					((IModelStateListener) holdListeners[i]).modelChanged(this);
				}
				// its so criticial that the begin/end arrive in
				// pairs,
				// if there happends to be an error in one of the
				// modelChanged,
				// they we want to be sure rest complete ok.
				catch (Exception e) {
					Logger.logException(e);
				}
			}

		}
	}

	/**
	 * Informs all registered model state listeners about a change in the
	 * dirty state of the model. The dirty state is entirely about changes in
	 * the content of the model (not, for example, about changes to id, or
	 * base location -- see modelMoved).
	 */
	protected void fireModelDirtyStateChanged(IStructuredModel element, boolean isDirty) {

		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelDirtyStateChanged"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelDirtyStateChanged(element, isDirty);
			}
		}
	}

	protected void fireModelReinitialized() {

		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelReinitialized"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				IModelStateListener listener = (IModelStateListener) holdListeners[i];
				listener.modelReinitialized(this);
			}
		}
	}

	/**
	 * Informs all registered model state listeners about the deletion of a
	 * model's underlying resource.
	 */
	protected void fireModelResourceDeleted(IStructuredModel element) {

		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelResourceDeleted"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelResourceDeleted(element);
			}
		}
	}

	/**
	 * Informs all registered model state listeners that the resource
	 * underlying a model has been moved. This is typically reflected in a
	 * change to the id, baseLocation, or both.
	 */
	protected void fireModelResourceMoved(IStructuredModel originalElement, IStructuredModel movedElement) {

		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelResourceMoved"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelResourceMoved(originalElement, movedElement);
			}
		}
	}

	public Object getAdapter(Class adapter) {

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getBaseLocation() {

		return fBaseLocation;
	}

	/**
	 * @see org.eclipse.wst.sse.core.internal.provisional.IStructuredModel#getContentTypeIdentifier()
	 */
	public String getContentTypeIdentifier() {
		if (fExplicitContentTypeIdentifier != null)
			return fExplicitContentTypeIdentifier;
		return fModelHandler.getAssociatedContentTypeId();
	}

	/**
	 * 
	 */
	public FactoryRegistry getFactoryRegistry() {
		if (factoryRegistry == null) {
			factoryRegistry = new FactoryRegistry();
		}
		return factoryRegistry;
	}

	/**
	 * The id is the id that the model manager uses to identify this model
	 * 
	 * @ISSUE - no one should need to know ID, so this should be default access eventually. 
	 * If clients believe they do need ID, be sure to let us know (open a bug). 
	 */
	public String getId() {

		return fId;
	}

	public abstract IndexedRegion getIndexedRegion(int offset);

	/**
	 * @return
	 */
	private ILock getLockObjectFromDocument() {

		// we always "get afresh" the lock object from our document,
		// just in case the instance of the document changes.
		ILock result = null;
		IStructuredDocument doc = fStructuredDocument;
		if (doc != null) {
			if (doc instanceof ILockable) {
				// remember, more than one client can get the
				// lock object, its during the aquire that the
				// lock on the thread is obtained.
				result = ((ILockable) doc).getLockObject();
			}
		}
		return result;
	}

	/**
	 * Gets the contentTypeDescription.
	 * 
	 * @return Returns a ContentTypeDescription
	 */
	public IModelHandler getModelHandler() {

		return fModelHandler;
	}


	public IModelManager getModelManager() {

		return _getModelManager();
	}

	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public int getReferenceCount() {


		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCount(getId());
	}


	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public int getReferenceCountForEdit() {



		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCountForEdit(getId());
	}


	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public int getReferenceCountForRead() {



		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCountForRead(getId());
	}

	public Object getReinitializeStateData() {

		return reinitializeStateData;
	}



	public URIResolver getResolver() {

		return fResolver;
	}


	public IStructuredDocument getStructuredDocument() {

		IStructuredDocument result = null;
		result = fStructuredDocument;
		return result;
	}

	/**
	 * Insert the method's description here. Creation date: (9/7/2001 2:30:26
	 * PM)
	 * 
	 * @return long
	 */
	public long getSynchronizationStamp() {

		return fSynchronizationStamp;
	}

	public IStructuredTextUndoManager getUndoManager() {

		IStructuredTextUndoManager structuredTextUndoManager = null;
		IStructuredDocument structuredDocument = getStructuredDocument();
		if (structuredDocument == null) {
			structuredTextUndoManager = null;
		}
		else {
			structuredTextUndoManager = structuredDocument.getUndoManager();
		}
		return structuredTextUndoManager;
	}

	public void initId(String id) {
		fId = id;
	}

	final protected void internalAboutToBeChanged() {

		// notice we only fire this event if we are not
		// already in a model state changing sequence
		if (fModelStateChanging == 0) {

			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelAboutToBeChanged"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			try {
				fireModelAboutToBeChanged();
			}
			catch (Exception e) {
				Logger.logException("Exception while notifying model state listers of about to change", e); //$NON-NLS-1$
			}
			finally {
				// begin lock after listeners notified, otherwise
				// deadlock could occur if they call us back.
				beginLock();
			}

		}
		// we always increment counter, for every request (so *must* receive
		// corresponding number of 'changedModel' requests)
		fModelStateChanging++;
	}

	/**
	 * Informs all registered model state listeners that an impending change
	 * is now complete. This method must only be called by 'modelChanged'
	 * since it keeps track of counts.
	 */
	final protected void internalModelChanged() {

		// always decrement
		fModelStateChanging--;


		// Check integrity
		// to be less than zero is a programming error,
		// but we'll reset to zero
		// and try to continue
		if (fModelStateChanging < 0) {
			fModelStateChanging = 0;
			// should not be locked, but just in case
			endLock();
			throw new IllegalStateException("Program Error: modelStateChanging was less than zero"); //$NON-NLS-1$
		}


		// We only fire this event if all pending requests are done.
		// That is, if we've received the same number of modelChanged as
		// we have aboutToChangeModel.
		if (fModelStateChanging == 0) {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "IModelStateListener event for " + getId() + " : modelChanged"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			endLock();
			// notifify listeners outside of locked state (or deadlock
			// can occur if one of them calls us back.
			fireModelChanged();
		}
	}

	public boolean isDirty() {

		return fDirtyState;
	}

	/**
	 * This method has very special purpose, its used in subclass
	 * 'changedModel' to know when to do "ending" sorts of things, right
	 * before a call to super.ChangedModel would in deed put the model in
	 * 'end' state. Put another way, at the beginning of the subclasses's
	 * changedModel, the isModelStateChanging is true, but at end, it will be
	 * false. So, this method allows a small "peek ahead".
	 */
	protected boolean isModelChangeStateOnVergeOfEnding() {


		return fModelStateChanging == 1;
	}

	/**
	 * This method can be called to determine if the model is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public boolean isModelStateChanging() {


		return fModelStateChanging > 0;
	}

	public boolean isNew() {

		return fNewState;
	}

	public boolean isReinitializationNeeded() {

		return reinitializationNeeded;
	}

	public boolean isSaveNeeded() {


		if (!isSharedForEdit())
			return isDirty();
		else
			return false;
	}


	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public boolean isShared() {
		if (getModelManager() == null)
			return false;
		return getModelManager().isShared(getId());
	}


	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public boolean isSharedForEdit() {


		if (getModelManager() == null)
			return false;
		return getModelManager().isSharedForEdit(getId());
	}


	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public boolean isSharedForRead() {


		if (getModelManager() == null)
			return false;
		return getModelManager().isSharedForRead(getId());
	}


	public void modelReinitialized() {


		// notice this is just a public avenue to our protected method
		fireModelReinitialized();
	}

	public IStructuredModel newInstance() throws IOException {

		IStructuredModel newModel = null;
		// we delegate to the model manager, so loader, etc., can be
		// used.
		newModel = getModelManager().createNewInstance(this);
		return newModel;
	}

	public IStructuredModel reinit() {


		IStructuredModel result = null;
		if (fModelStateChanging == 0) {
			try {
				aboutToChangeModel();
				aboutToReinitializeModel();
				result = _getModelManager().reinitialize(this);
			}
			finally {
				setReinitializeNeeded(false);
				setReinitializeStateData(null);
				modelReinitialized();
				changedModel();
			}
		}
		else {
			if (Logger.DEBUG_MODELSTATE) {
				Logger.log(Logger.INFO, "indeed!!!"); //$NON-NLS-1$
			}
		}
		return result;
	}


	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 */
	public void releaseFromEdit() {


		if (getModelManager() == null) {
			throw new IllegalStateException(MODEL_MANAGER_NULL); //$NON-NLS-1$
		}
		else {
			/*
			 * Be sure to check the shared state before releasing. (Since
			 * isShared assumes a count of 1 means not shared ... and we want
			 * our '1' to be that one.) The problem, of course, is that
			 * between pre-cycle notification and post-release notification,
			 * the model could once again have become shared, rendering the
			 * release notification incorrect.
			 */
			boolean isShared = isShared();

			if (!isShared) {
				signalPreLifeCycleEventRelease(this);
			}

			_getModelManager().releaseFromEdit(this);
			if (!isShared) {
				signalPostLifeCycleListenerRelease(this);
			}
		}

	}

	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 */
	public void releaseFromRead() {

		if (getModelManager() == null) {
			throw new IllegalStateException(MODEL_MANAGER_NULL); //$NON-NLS-1$
		}
		else {
			/*
			 * Be sure to check the shared state before releasing. (Since
			 * isShared assumes a count of 1 means not shared ... and we want
			 * our '1' to be that one.) The problem, of course, is that
			 * between pre-cycle notification and post-release notification,
			 * the model could once again have become shared, rendering the
			 * release notification incorrect.
			 */
			boolean isShared = isShared();

			if (!isShared) {
				signalPreLifeCycleEventRelease(this);
			}

			_getModelManager().releaseFromRead(this);

			if (!isShared) {
				signalPostLifeCycleListenerRelease(this);
			}
		}
	}


	/**
	 * This function replenishes the model with the resource without saving
	 * any possible changes. It is used when one editor may be closing, and
	 * specifially says not to save the model, but another "display" of the
	 * model still needs to hang on to some model, so needs a fresh copy.
	 */
	public IStructuredModel reload(InputStream inputStream) throws IOException {
		IStructuredModel result = null;
		try {
			aboutToChangeModel();
			result = _getModelManager().reloadModel(getId(), inputStream);
		}
		catch (UnsupportedEncodingException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		finally {
			changedModel();
		}
		return result;
	}

	public void removeModelLifecycleListener(IModelLifecycleListener listener) {

		// if manager is null, then none have been added, so
		// no need to remove any
		if (fLifecycleNotificationManager == null)
			return;
		synchronized (fListenerLock) {
			fLifecycleNotificationManager.removeListener(listener);
		}
	}


	public void removeModelStateListener(IModelStateListener listener) {

		if (listener == null)
			return;
		if (fModelStateListeners == null)
			return;
		// if its not in the listeners, we'll ignore the request
		synchronized (fListenerLock) {
			if (Utilities.contains(fModelStateListeners, listener)) {
				int oldSize = fModelStateListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelStateListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are
						// removing
						newListeners[index++] = fModelStateListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the
				// old
				// one
				fModelStateListeners = newListeners;
			}
		}
	}


	/**
	 * A method that modifies the model's synchronization stamp to match the
	 * resource. Turns out there's several ways of doing it, so this ensures a
	 * common algorithm.
	 */
	public void resetSynchronizationStamp(IResource resource) {


		setSynchronizationStamp(computeModificationStamp(resource));
	}


	/**
	 * This API allows a client to initiate notification to all interested
	 * parties that a model's underlying resource has been deleted.
	 */
	public void resourceDeleted() {


		// notice this is just a public avenue to our protected method
		fireModelResourceDeleted(this);
	}


	/**
	 * This method allows a model client to initiate notification to all
	 * interested parties that a model's underlying resource location has
	 * changed. Note: we assume caller has already changed baseLocation, Id,
	 * etc., since its really up to the client to determine what's "new" about
	 * a moved model. Caution: 'this' and 'newModel' may be the same object.
	 * This is the case for current working with FileModelProvider, but have
	 * left the dual argument for future possibilities.
	 */
	public void resourceMoved(IStructuredModel newModel) {


		// notice this is just a public avenue to our protected method
		fireModelResourceMoved(this, newModel);
	}


	public void save() throws UnsupportedEncodingException, IOException, CoreException {

		int type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.PRE_EVENT;
		ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
		signalLifecycleEvent(modelLifecycleEvent);

		try {
			String stringId = getId();
			_getModelManager().saveModel(stringId, EncodingRule.CONTENT_BASED);
		}

		finally {
			// we put end notification in finally block, so even if
			// error occurs during save, listeners are still notified,
			// since their code could depend on receiving, to clean up
			// some state, or coordinate other resources.
			type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}


	public void save(EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		int type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.PRE_EVENT;
		ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
		signalLifecycleEvent(modelLifecycleEvent);

		try {
			String stringId = getId();
			_getModelManager().saveModel(stringId, encodingRule);
		}
		finally {
			// we put end notification in finally block, so even if
			// error occurs during save, listeners are still notified,
			// since their code could depend on receiving, to clean up
			// some state, or coordinate other resources.
			type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}


	public void save(IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {

		int type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.PRE_EVENT;
		ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
		signalLifecycleEvent(modelLifecycleEvent);

		try {
			String stringId = getId();
			_getModelManager().saveModel(iFile, stringId, EncodingRule.CONTENT_BASED);
		}

		finally {
			// we put end notification in finally block, so even if
			// error occurs during save, listeners are still notified,
			// since their code could depend on receiving, to clean up
			// some state, or coordinate other resources.
			type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}


	public void save(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		int type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.PRE_EVENT;
		ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
		signalLifecycleEvent(modelLifecycleEvent);

		try {
			String stringId = getId();
			_getModelManager().saveModel(iFile, stringId, encodingRule);
		}
		finally {
			// we put end notificatioon in finally block, so even if
			// error occurs during save, listeners are still notified,
			// since their code could depend on receiving, to clean up
			// some state, or coordinate other resources.
			type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}


	public void save(OutputStream outputStream) throws UnsupportedEncodingException, CoreException, IOException {

		int type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.PRE_EVENT;
		ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
		signalLifecycleEvent(modelLifecycleEvent);

		try {
			String stringId = getId();
			_getModelManager().saveModel(stringId, outputStream, EncodingRule.CONTENT_BASED);
		}

		finally {
			// we put end notification in finally block, so even if
			// error occurs during save, listeners are still notified,
			// since their code could depend on receiving, to clean up
			// some state, or coordinate other resources.
			type = ModelLifecycleEvent.MODEL_SAVED | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}


	/**
	 * This attribute is typically used to denote the model's underlying
	 * resource.
	 */
	public void setBaseLocation(java.lang.String newBaseLocation) {
		fBaseLocation = newBaseLocation;
		if (fResolver != null) {
			fResolver.setFileBaseLocation(newBaseLocation);
		}
	}

	public void setContentTypeIdentifier(String contentTypeIdentifier) {
		fExplicitContentTypeIdentifier = contentTypeIdentifier;
	}

	/**
	 * 
	 */
	public void setDirtyState(boolean dirtyState) {

		// no need to process (set or fire event), if same value
		if (fDirtyState != dirtyState) {
			// pre-change notification
			int type = ModelLifecycleEvent.MODEL_DIRTY_STATE | ModelLifecycleEvent.PRE_EVENT;
			ModelLifecycleEvent modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);


			// the actual change
			fDirtyState = dirtyState;

			// old notification
			// TODO: C3 remove old notification
			if (fDirtyState == false) {
				// if we are being set to not dirty (such as just been saved)
				// then we need to start listening for changes
				// again to know when to set state to true;
				getStructuredDocument().addDocumentChangedListener(fDirtyStateWatcher);
			}
			fireModelDirtyStateChanged(this, dirtyState);


			// post change notification
			type = ModelLifecycleEvent.MODEL_DIRTY_STATE | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}

	/**
	 * @deprecated - will likely be deprecated soon, in favor of direct 'adds'
	 *             ... but takes some redesign.
	 */
	public void setFactoryRegistry(FactoryRegistry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	/**
	 * The id is the id that the model manager uses to identify this model. If
	 * it is being set here, it means the model manger is already managing the
	 * model with another id, so we have to keep it in sync. This method calls
	 * notifies listners, if they haven't been notified already, that a "model
	 * state change" is about to occur.
	 */
	public void setId(String newId) throws ResourceInUse {


		// It makes no sense, I don't think, to have an id of null, so
		// we'll throw an illegal argument exception if someone trys. Note:
		// the IModelManager could not manage a model with an id of null,
		// since it uses hashtables, and you can't have a null id for a
		// hashtable.
		if (newId == null)
			throw new IllegalArgumentException(SSECoreMessages.A_model_s_id_can_not_be_nu_EXC_); //$NON-NLS-1$ = "A model's id can not be null"
		// To guard against throwing a spurious ResourceInUse exception,
		// which can occur when two pieces of code both want to change the id,
		// so the second request is spurious, we'll ignore any requests that
		// attempt to change the id to what it already is ... note, we use
		// 'equals', not identity ('==') so that things like
		// strings can be used. This is the same criteria that ids are
		// found in model manager -- well, actually, I just checked, and for
		// the hashtable impl, the criteria uses .equals AND the condition
		// that the hash values be identical (I'm assuming this is always
		// true, if equals is true, for now, I'm not sure
		// we can assume that hashtable will always be used, but in
		// general, should match.)
		//
		if (newId.equals(fId))
			return;
		// we must guard against reassigning an id to one that we already
		// are managing.
		if (getModelManager() != null) {
			boolean inUse = ((ModelManagerImpl)getModelManager()).isIdInUse(newId);
			if (inUse) {
				throw new ResourceInUse();
			}
		}
		try {
			// normal code path
			aboutToChangeModel();
			String oldId = fId;
			fId = newId;
			if (getModelManager() != null) {
				// if managed and the id has changed, notify to
				// IModelManager
				// TODO: try to refine the design not to do that
				if (oldId != null && newId != null && !newId.equals(oldId)) {
					getModelManager().moveModel(oldId, newId);
				}
			}
		}
		finally {
			// make sure this finally is only executed if 'about to Change
			// model' has
			// been executed.
			changedModel();
		}
	}

	/**
	 * Sets the contentTypeDescription.
	 * 
	 * @param contentTypeDescription
	 *            The contentTypeDescription to set
	 */
	public void setModelHandler(IModelHandler modelHandler) {

		// no need to fire events if modelHandler has been null
		// for this model --
		// this is an attempt at initialization optimization and may need
		// to change in future.
		boolean trueChange = false;
		if (fModelHandler != null)
			trueChange = true;
		if (trueChange) {
			internalAboutToBeChanged();
		}
		fModelHandler = modelHandler;
		if (trueChange) {
			internalModelChanged();
		}
	}



	public void setModelManager(IModelManager newModelManager) {

		fModelManager = newModelManager;
	}

	/**
	 * 
	 */
	public void setNewState(boolean newState) {

		fNewState = newState;
	}

	/**
	 * Sets a "flag" that reinitialization is needed.
	 */
	public void setReinitializeNeeded(boolean needed) {

		reinitializationNeeded = needed;
	}

	/**
	 * Holds any data that the reinit procedure might find useful in
	 * reinitializing the model. This is handy, since the reinitialization may
	 * not take place at once, and some "old" data may be needed to properly
	 * undo previous settings. Note: the parameter was intentionally made to
	 * be of type 'Object' so different models can use in different ways.
	 */
	public void setReinitializeStateData(Object object) {

		reinitializeStateData = object;
	}


	public void setResolver(URIResolver newResolver) {

		fResolver = newResolver;
	}


	public void setStructuredDocument(IStructuredDocument newStructuredDocument) {
		boolean lifeCycleNotification = false;
		if (fStructuredDocument != null) {
			fStructuredDocument.removeDocumentChangedListener(fDirtyStateWatcher);
			fStructuredDocument.removeDocumentAboutToChangeListener(fDocumentToModelNotifier);
			fStructuredDocument.removeDocumentChangedListener(fDocumentToModelNotifier);
			// prechange notification
			lifeCycleNotification = true;
			ModelLifecycleEvent modelLifecycleEvent = new DocumentChanged(ModelLifecycleEvent.PRE_EVENT, this, fStructuredDocument, newStructuredDocument);
			signalLifecycleEvent(modelLifecycleEvent);
		}

		// hold for life cycle notification
		IStructuredDocument previousDocument = fStructuredDocument;
		// the actual change
		fStructuredDocument = newStructuredDocument;


		// at the super class level, we'll listen for structuredDocument
		// changes
		// so we can set our dirty state flag
		if (fStructuredDocument != null) {
			fStructuredDocument.addDocumentChangedListener(fDirtyStateWatcher);
			fStructuredDocument.addDocumentAboutToChangeListener(fDocumentToModelNotifier);
			fStructuredDocument.addDocumentChangedListener(fDocumentToModelNotifier);
		}

		if (lifeCycleNotification) {
			// post change notification
			ModelLifecycleEvent modelLifecycleEvent = new DocumentChanged(ModelLifecycleEvent.POST_EVENT, this, previousDocument, newStructuredDocument);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}

	/**
	 * Insert the method's description here. Creation date: (9/7/2001 2:30:26
	 * PM)
	 * 
	 * @param newSynchronizationStamp
	 *            long
	 */
	protected void setSynchronizationStamp(long newSynchronizationStamp) {

		fSynchronizationStamp = newSynchronizationStamp;
	}

	public void setUndoManager(IStructuredTextUndoManager undoManager) {

		IStructuredDocument structuredDocument = getStructuredDocument();
		if (structuredDocument == null) {
			throw new IllegalStateException("document was null when undo manager set on model"); //$NON-NLS-1$
		}
		structuredDocument.setUndoManager(undoManager);
	}

	/**
	 * To be called only by "friendly" classes, such as ModelManager, and
	 * subclasses.
	 */
	void signalLifecycleEvent(ModelLifecycleEvent event) {
		if (fLifecycleNotificationManager == null)
			return;
		fLifecycleNotificationManager.signalLifecycleEvent(event);
	}

	private void signalPostLifeCycleListenerRelease(IStructuredModel structuredModel) {
		int type = ModelLifecycleEvent.MODEL_RELEASED | ModelLifecycleEvent.POST_EVENT;
		// what's wrong with this design that a cast is needed here!?
		ModelLifecycleEvent event = new ModelLifecycleEvent(structuredModel, type);
		((AbstractStructuredModel) structuredModel).signalLifecycleEvent(event);
	}

	private void signalPreLifeCycleEventRelease(IStructuredModel structuredModel) {
		int type = ModelLifecycleEvent.MODEL_RELEASED | ModelLifecycleEvent.PRE_EVENT;
		// what's wrong with this design that a cast is needed here!?
		ModelLifecycleEvent event = new ModelLifecycleEvent(structuredModel, type);
		((AbstractStructuredModel) structuredModel).signalLifecycleEvent(event);
	}
}
