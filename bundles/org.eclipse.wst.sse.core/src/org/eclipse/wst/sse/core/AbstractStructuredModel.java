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
import org.eclipse.wst.encoding.EncodingRule;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.nls.ResourceHandler1;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.undo.StructuredTextUndoManager;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.core.util.Utilities;


public abstract class AbstractStructuredModel implements IStructuredModel {

	/**
	 * @deprecated - will likely be deprecated soon, in favor of direct 'adds'
	 *             ... but takes some redesign.
	 */
	public void setFactoryRegistry(IFactoryRegistry factoryRegistry) {

		this.factoryRegistry = factoryRegistry;
	}

	class DirtyStateWatcher implements IStructuredDocumentListener {

		public void newModel(NewModelEvent structuredDocumentEvent) {

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
				fStructuredDocument.removeModelChangedListener(fDirtyStateWatcher);
			}
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {

			setDirtyState(true);
			// no need to listen any more
			if (fStructuredDocument != null) {
				fStructuredDocument.removeModelChangedListener(fDirtyStateWatcher);
			}
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {

			setDirtyState(true);
			// no need to listen any more
			if (fStructuredDocument != null) {
				fStructuredDocument.removeModelChangedListener(fDirtyStateWatcher);
			}
		}
	}

	private LifecycleNotificationManager fLifecycleNotificationManager;
	private Object[] fModelStateListeners;
	private IStructuredDocument fStructuredDocument;
	private String fId;
	//	private String fLineDelimiter;
	//	private Object fType;
	private IModelHandler fModelHandler;
	private String fBaseLocation;
	private IModelManager fModelManager;
	private StructuredTextUndoManager fUndoManager = null;
	private IFactoryRegistry factoryRegistry;
	private boolean fDirtyState;
	private DirtyStateWatcher fDirtyStateWatcher;
	private URIResolver fResolver;
	private boolean fNewState = false;
	private int modelStateChanging;
	//	private final static String platformLineDelimiter =
	// System.getProperty("line.separator"); //$NON-NLS-1$
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
	}

	/**
	 * This API allows clients to declare that they are about to make a "large"
	 * change to the model. This change might be in terms of content or it
	 * might be in terms of the model id or base location. Note that in the
	 * case of embedded calls, notification to listners is sent only once. Note
	 * that the client who is making these changes has the responsibility to
	 * restore the models state once finished with the changes. See getMemento
	 * and restoreState. The method isModelStateChanging can be used by a
	 * client to determine if the model is already in a change sequence.
	 */
	public synchronized void aboutToChangeModel() {

		// notice this is just a public avenue to our protected method
		fireModelAboutToBeChanged();
	}

	public synchronized void aboutToReinitializeModel() {

		// notice this is just a public avenue to our protected method
		fireModelAboutToBeReinitialized();
	}

	public synchronized void modelReinitialized() {

		// notice this is just a public avenue to our protected method
		fireModelReinitialized();
	}

	public synchronized void addModelLifecycleListener(IModelLifecycleListener listener) {

		if (fLifecycleNotificationManager == null) {
			fLifecycleNotificationManager = new LifecycleNotificationManager();
		}
		fLifecycleNotificationManager.addListener(listener);
	}

	/**
	 * to be called only be "friendly" classes, such as ModelManger, 
	 * and subclasses. 
	 */
	protected void signalLifecycleEvent(ModelLifecycleEvent event) {

		if (fLifecycleNotificationManager == null)
			return;
		fLifecycleNotificationManager.signalLifecycleEvent(event);
	}

	public void removeModelLifecycleListener(IModelLifecycleListener listener) {

		// if manager is null, then none have been added, so
		// no need to remove it.
		if (fLifecycleNotificationManager == null)
			return;
		fLifecycleNotificationManager.removeListener(listener);
	}

	public synchronized void addModelStateListener(IModelStateListener listener) {

		if (!Utilities.contains(fModelStateListeners, listener)) {
			int oldSize = 0;
			if (fModelStateListeners != null) {
				// normally won't be null, but we need to be sure, for first
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
			//
			// SIDE EFFECT
			// Tell listener just added, the current state
			//listener.elementDirtyStateChanged(this, isDirty());
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

		if (fUndoManager != null)
			fUndoManager.beginRecording(requester, label, description);
	}

	public void beginRecording(Object requester, String label, String description, int cursorPosition, int selectionLength) {

		if (fUndoManager != null)
			fUndoManager.beginRecording(requester, label, description, cursorPosition, selectionLength);
	}

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and must be called after aboutToChangeModel ...
	 * or some listeners could be left waiting indefinitely for the changed
	 * event. So, its suggested that changedModel always be in a finally
	 * clause. Likewise, a client should never call changedModel without
	 * calling aboutToChangeModel first. In the case of embedded calls, the
	 * notification is just sent once.
	 */
	public synchronized void changedModel() {

		// notice this is just a public avenue to our protected method
		fireModelChanged();
		// also note!
		// if we've been "changed" by a client, we might still need
		// to be re-initialized, so we'll check and handle that here.
		// Note only does this provide a solution to some "missed"
		// re-inits, in provides a built in way for clients to
		// "force" the model to handle itself, by bracketing any
		// changes with aboutToChange and changed, the model itself
		// will check. But only call re-init if all other pending
		// modelChanged states have been handled.
		if (modelStateChanging == 0 && isReinitializationNeeded()) {
			reinit();
		}
	}

	/**
	 * Based on similar method in FileDocumentProvider. It will provide what
	 * the modificationStamp would be if resetSynchronzationStamp(resource)
	 * were used, although for this 'compute' API, no changes to the instance
	 * are made.
	 */
	public synchronized long computeModificationStamp(IResource resource) {

		long modificationStamp = resource.getModificationStamp();
		IPath path = resource.getLocation();
		if (path == null) {
			return modificationStamp;
		}
		// Note: checking existence of file is a little different than impl in
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
	 * principle of this copy is not to copy fields, etc., as is typically done
	 * in a clone method, but to return a model with the same content in the
	 * structuredDocument. Note: It is the callers responsibility to
	 * setBaseLocation, listners, etc., as appropriate. Type and Encoding are
	 * the only fields set by this method. If the newId provided already exist
	 * in the model manager, a ResourceInUse exception is thrown.
	 */
	public synchronized IStructuredModel copy(String newId) throws ResourceInUse {

		IStructuredModel newModel = null;
		// this first one should fail, if not, its treated as an error
		// If the caller wants to use an existing one, they can call
		// getExisting
		// after this failure
		newModel = getModelManager().getExistingModelForEdit(newId);
		if (newModel != null) {
			// be sure to release the reference we got "by accident" (and no
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

		if (fUndoManager != null)
			fUndoManager.disableUndoManagement();
	}

	/**
	 * Enable undo management.
	 */
	public void enableUndoManagement() {

		if (fUndoManager != null)
			fUndoManager.enableUndoManagement();
	}

	public void endRecording(Object requester) {

		if (fUndoManager != null)
			fUndoManager.endRecording(requester);
	}

	public void endRecording(Object requester, int cursorPosition, int selectionLength) {

		if (fUndoManager != null)
			fUndoManager.endRecording(requester, cursorPosition, selectionLength);
	}

	protected void fireModelAboutToBeReinitialized() {

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				// NOTE: trick for transition. We actual use the same listeners
				// as modelState, but only send this to those that have
				// implemented ModelStateExtended.
				IModelStateListener listener = (IModelStateListener) holdListeners[i];
				if (listener instanceof IModelStateListenerExtended) {
					IModelStateListenerExtended extendedListner = (IModelStateListenerExtended) listener;
					extendedListner.modelAboutToBeReinitialized(this);
				}
			}
		}
	}

	protected void fireModelReinitialized() {

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				// NOTE: trick for transition. We actual use the same listeners
				// as modelState, but only send this to those that have
				// implemented ModelStateExtended.
				IModelStateListener listener = (IModelStateListener) holdListeners[i];
				if (listener instanceof IModelStateListenerExtended) {
					IModelStateListenerExtended extendedListner = (IModelStateListenerExtended) listener;
					extendedListner.modelReinitialized(this);
				}
			}
		}
	}

	/**
	 * Informs all registered model state listeners that the the model is about
	 * to under go a "large" change. This change might be interms of contents,
	 * in might be in terms of the model id or base location.
	 */
	protected void fireModelAboutToBeChanged() {

		// notice we only fire this event if we are not already in a model
		// state changing sequence
		if (modelStateChanging == 0) {
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
		// we always increment counter, for every request (so must receive
		// corresponding number of 'changedModel' requests)
		modelStateChanging++;
	}

	/**
	 * Informs all registered model state listeners that an impending change is
	 * now complete. This method must only be called by 'modelChanged' since it
	 * keeps track of counts.
	 */
	protected void fireModelChanged() {

		// always decrement
		modelStateChanging--;
		// to be less than zero is a programming error, but we'll reset to zero
		// with no error messages.
		if (modelStateChanging < 0)
			modelStateChanging = 0;
		// We only fire this event if all pending requests are done.
		// That is, if we've received the same number of fireModelChanged as we
		// have fireModelAboutToBeChanged.
		if (modelStateChanging == 0) {
			// we must assign listeners to local variable, since the add and
			// remove listner
			// methods can change the actual instance of the listener array
			// from another thread
			if (fModelStateListeners != null) {
				Object[] holdListeners = fModelStateListeners;
				for (int i = 0; i < holdListeners.length; i++) {
					((IModelStateListener) holdListeners[i]).modelChanged(this);
				}
			}
		}
	}

	/**
	 * Informs all registered model state listeners about a change in the dirty
	 * state of the model. The dirty state is entirely about changes in the
	 * content of the model (not, for example, about changes to id, or base
	 * location -- see modelMoved).
	 */
	protected void fireModelDirtyStateChanged(IStructuredModel element, boolean isDirty) {

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelDirtyStateChanged(element, isDirty);
			}
		}
	}

	/**
	 * Informs all registered model state listeners about the deletion of a
	 * model's underlying resource.
	 */
	protected void fireModelResourceDeleted(IStructuredModel element) {

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
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

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fModelStateListeners != null) {
			Object[] holdListeners = fModelStateListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				((IModelStateListener) holdListeners[i]).modelResourceMoved(originalElement, movedElement);
			}
		}
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getBaseLocation() {

		return fBaseLocation;
	}

	/**
	 *  
	 */
	public IFactoryRegistry getFactoryRegistry() {

		if (factoryRegistry == null) {
			factoryRegistry = new FactoryRegistry();
		}
		return factoryRegistry;
	}

	public IStructuredDocument getStructuredDocument() {

		return fStructuredDocument;
	}

	/**
	 * The id is the id that the model manager uses to identify this model
	 */
	public String getId() {

		return fId;
	}

	/**
	 * This method returns a mememto that can later be used to restore the
	 * state at this point. A model's state, in this sense, does not relate to
	 * its content, or Ids, etc., just its dirty state, and its synchronization
	 * state with its underlying resource. The 'resource' argument must be the
	 * resource that underlies the instance of the model this method is sent
	 * to. Note: this parameter will not be required in future versions of
	 * 'strucutured model'.
	 */
	public IStateMemento getMemento(IResource resource) {

		ModelStateMemento memento = new ModelStateMemento();
		memento.setUnderlyingResource(resource);
		memento.setDirtyState(isDirty());
		long modDate = computeModificationStamp(resource);
		memento.setDatesInSync(fSynchronizationStamp == modDate);
		return memento;
	}

	/**
	 * This method is just for getting an instance of the model manager of the
	 * right Impl type, to be used "internally" for making protected calls
	 * directly to the impl class.
	 */
	// TODO: those places we now use non-public methods can now be fixed.
	private ModelManagerImpl _getModelManager() {

		if (fModelManager == null) {
			// get the model manager from the plugin
			// note: we can use the static "ID" variable, since we pre-req that
			// plugin
			IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
			fModelManager = plugin.getModelManager();
		}
		if (!(fModelManager instanceof ModelManagerImpl)) {
			throw new IllegalStateException(ResourceHandler1.getString("unexpected_ModelManager_Impl_1")); //$NON-NLS-1$
		}
		return (ModelManagerImpl) fModelManager;
	}

	/**
	 */
	public IModelManager getModelManager() {

		return _getModelManager();
	}

	public abstract IndexedRegion getIndexedRegion(int offset);

	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public synchronized int getReferenceCount() {

		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCount(getId());
	}

	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public synchronized int getReferenceCountForEdit() {

		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCountForEdit(getId());
	}

	/**
	 * This function returns the reference count of underlying model.
	 */
	// TODO: try to refine the design not to use this function
	public synchronized int getReferenceCountForRead() {

		if (getModelManager() == null)
			return 0;
		return getModelManager().getReferenceCountForRead(getId());
	}

	/**
	 */
	public URIResolver getResolver() {

		return fResolver;
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

	public StructuredTextUndoManager getUndoManager() {

		return fUndoManager;
	}

	public boolean isDirty() {

		return fDirtyState;
	}

	/**
	 * This method can be called to determine if the model is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public synchronized boolean isModelStateChanging() {

		return modelStateChanging > 0;
	}

	public boolean isNew() {

		return fNewState;
	}

	public synchronized boolean isSaveNeeded() {

		if (!isSharedForEdit())
			return isDirty();
		else
			return false;
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public synchronized boolean isShared() {

		if (getModelManager() == null)
			return false;
		return getModelManager().isShared(getId());
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public synchronized boolean isSharedForEdit() {

		if (getModelManager() == null)
			return false;
		return getModelManager().isSharedForEdit(getId());
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public synchronized boolean isSharedForRead() {

		if (getModelManager() == null)
			return false;
		return getModelManager().isSharedForRead(getId());
	}

	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 */
	public synchronized void releaseFromEdit() {

		if (getModelManager() == null) {
			throw new SourceEditingRuntimeException("Warning: AbstractStructuredModel::close:  model manager was null during a close of a model (which should be impossible)"); //$NON-NLS-1$
		}
		else {
			// be sure to check the shared state before releasing. (Since
			// isShared assumes a count
			// of 1 means not shared ... and we want our '1' to be that one.)
			boolean isShared = isShared();
			_getModelManager().releaseFromEdit(getId());
			// if no one else is using us, free up
			// an resources
			if (!isShared) {
				_commonRelease();
			}
		}
	}

	private void _commonRelease() {

		if (factoryRegistry != null) {
			factoryRegistry.release();
		}
		// if document as not been changed, we'll still be listening for
		// first change. This is not a critical clean up, since presumanly
		// whole model and document are "going away", but can make
		// other memory leaks harder to find if we stay attached.
		// (Note: my first thought was to set fStructuredDocument to null also,
		// but there's others in shutdown process that still need to
		// get it, in order to disconnect from it.)
		if (fStructuredDocument != null) {
			fStructuredDocument.removeModelChangedListener(fDirtyStateWatcher);
		}
	}

	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 */
	public synchronized void releaseFromRead() {

		if (getModelManager() == null) {
			throw new SourceEditingRuntimeException("Warning: AbstractStructuredModel::close:  model manager was null during a close of a model (which should be impossible)"); //$NON-NLS-1$
		}
		else {
			// be sure to check the shared state before releasing. (Since
			// isShared assumes a count
			// of 1 means not shared ... and we want our '1' to be that one.)
			boolean isShared = isShared();
			_getModelManager().releaseFromRead(getId());
			// if no one else is using us, free up
			// an resources
			if (!isShared) {
				// factoryRegistry.release();
				_commonRelease();
			}
		}
	}

	/**
	 * This function replenishes the model with the resource without saving any
	 * possible changes. It is used when one editor may be closing, and
	 * specifially says not to save the model, but another "display" of the
	 * model still needs to hang on to some model, so needs a fresh copy.
	 */
	public synchronized IStructuredModel reload(InputStream inputStream) throws IOException {

		IStructuredModel result = null;
		try {
			aboutToChangeModel();
			result = _getModelManager().reloadModel(getId(), inputStream);
		}
		catch (UnsupportedEncodingException e) {
			// its a very serious error to get an unsupported encoding
			// exception,
			// since we've presumable loaded it once already, so won't bother
			// with a checked exception.
			throw new SourceEditingRuntimeException(e);
		}
		finally {
			changedModel();
		}
		return result;
	}

	public synchronized IStructuredModel reinit() {

		IStructuredModel result = null;
		if (modelStateChanging == 0) {
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
			System.out.println("indeed!!!"); //$NON-NLS-1$
		}
		return result;
	}

	public synchronized void save(OutputStream outputStream) throws UnsupportedEncodingException, CoreException, IOException {

		String stringId = getId();
		_getModelManager().saveModel(stringId, outputStream, EncodingRule.CONTENT_BASED);
	}

	public synchronized void save() throws UnsupportedEncodingException, IOException, CoreException {

		String stringId = getId();
		_getModelManager().saveModel(stringId, EncodingRule.CONTENT_BASED);
	}

	public synchronized void save(EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		String stringId = getId();
		_getModelManager().saveModel(stringId, encodingRule);
	}

	public synchronized void save(IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {

		String stringId = getId();
		_getModelManager().saveModel(iFile, stringId, EncodingRule.CONTENT_BASED);
	}

	public synchronized void save(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		String stringId = getId();
		_getModelManager().saveModel(iFile, stringId, encodingRule);
	}

	public synchronized void removeModelStateListener(IModelStateListener listener) {

		if ((fModelStateListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fModelStateListeners, listener)) {
				int oldSize = fModelStateListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelStateListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fModelStateListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fModelStateListeners = newListeners;
			}
		}
	}

	/**
	 * A method that modififies the model's synchonization stamp to match the
	 * resource. Turns out there's several ways of doing it, so this ensures a
	 * common algorithm.
	 */
	public synchronized void resetSynchronizationStamp(IResource resource) {

		setSynchronizationStamp(computeModificationStamp(resource));
	}

	/**
	 * This API allows a client to initiate notification to all interested
	 * parties that a model's underlying resource has been deleted.
	 */
	public synchronized void resourceDeleted() {

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
	 * left the dual argument for future possiblities.
	 */
	public synchronized void resourceMoved(IStructuredModel newModel) {

		// notice this is just a public avenue to our protected method
		fireModelResourceMoved(this, newModel);
	}

	public synchronized void restoreState(IStateMemento memento) {

		ModelStateMemento mMemento = (ModelStateMemento) memento;
		// be sure to use setter, so side effects take place.
		setDirtyState(mMemento.isDirtyState());
		if (mMemento.isDatesInSync()) {
			IResource resource = mMemento.getUnderlyingResource();
			setSynchronizationStamp(computeModificationStamp(resource));
		}
	}

	/**
	 * This attribute is typically used to denote the model's underlying
	 * resource.
	 */
	public void setBaseLocation(java.lang.String newBaseLocation) {

		fBaseLocation = newBaseLocation;
	}

	/**
	 *  
	 */
	public void setDirtyState(boolean dirtyState) {

		// no need to process (set or fire event), if same value
		if (fDirtyState != dirtyState) {
			// prechange notificaiton
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
				getStructuredDocument().addModelChangedListener(fDirtyStateWatcher);
			}
			fireModelDirtyStateChanged(this, dirtyState);


			// post change notification
			type = ModelLifecycleEvent.MODEL_DIRTY_STATE | ModelLifecycleEvent.POST_EVENT;
			modelLifecycleEvent = new ModelLifecycleEvent(this, type);
			signalLifecycleEvent(modelLifecycleEvent);
		}
	}

	/**
	 * @param newStructuredDocument
	 */
	public void setStructuredDocument(IStructuredDocument newStructuredDocument) {

		if (fStructuredDocument != null)
			fStructuredDocument.removeModelChangedListener(fDirtyStateWatcher);

		// prechange notificaiton
		ModelLifecycleEvent modelLifecycleEvent = new DocumentChanged(ModelLifecycleEvent.PRE_EVENT, this, fStructuredDocument, newStructuredDocument);
		signalLifecycleEvent(modelLifecycleEvent);

		// the actual change
		fStructuredDocument = newStructuredDocument;


		// at the super class level, we'll listen for structuredDocument
		// changes
		// so we can set our dirty state flag
		fStructuredDocument.addModelChangedListener(fDirtyStateWatcher);

		// post change notification
		modelLifecycleEvent = new DocumentChanged(ModelLifecycleEvent.POST_EVENT, this, fStructuredDocument, newStructuredDocument);
		signalLifecycleEvent(modelLifecycleEvent);
	}

	/**
	 * The id is the id that the model manager uses to identify this model. If
	 * it is being set here, it means the model manger is already managing the
	 * model with another id, so we have to keep it in sync. This method calls
	 * notifies listners, if they haven't been notified already, that a "model
	 * state change" is about to occur.
	 */
	public synchronized void setId(String newId) throws ResourceInUse {

		// It makes no sense, I don't think, to have an id of null, so we'll
		// throw
		// an illegal argument exception if someone trys. Note: the
		// IModelManager could
		// not manage a model with an id of null, since it uses hashtables, and
		// you can't
		// have a null id for a hashtable.
		if (newId == null)
			throw new IllegalArgumentException(ResourceHandler1.getString("A_model's_id_can_not_be_nu_EXC_")); //$NON-NLS-1$ = "A model's id can not be null"
		// To gaurd againt throwing a spurious ResourceInUse exception, which
		// can occur
		// when two pieces of code both want to change the id, so the second
		// request is
		// spurious, we'll ignore any requests that attempt to change the id to
		// what it
		// already is ... note, we use 'equals', not identity ('==') so that
		// things like
		// strings can be used. This is the same criteria that ids are found in
		// model manager
		// -- well, actually, I just checked, and for the hashtable impl, the
		// criteria uses .equals AND the condition that the hash values be
		// identical
		// (I'm assuming this is always true, if equals is true, for now, I'm
		// not sure
		// we can assume that hashtable will always be used, but in general,
		// should match.)
		//
		if (newId.equals(fId))
			return;
		// we must gaurd against reassigning an id to one that we already are
		// managing.
		if (getModelManager() != null) {
			IStructuredModel newModel = getModelManager().getExistingModelForEdit(newId);
			if (newModel != null) {
				// be sure to release the reference we got "by accident" (and
				// no longer need)
				newModel.releaseFromEdit();
				throw new ResourceInUse();
			}
		}
		try {
			// normal path
			aboutToChangeModel();
			String oldId = fId;
			fId = newId;
			if (getModelManager() != null) {
				// if managed and the id has changed, notify to IModelManager
				// TODO: try to refine the design not to do that
				if (oldId != null && newId != null && !newId.equals(oldId)) {
					getModelManager().moveModel(oldId, newId);
				}
			}
		}
		finally {
			// make sure this finally is only executed if 'about to Change
			// model' has
			// ben executed.
			changedModel();
		}
	}

	/**
	 * @param newModelManager
	 */
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
	 * @param newResolver
	 */
	public void setResolver(URIResolver newResolver) {

		fResolver = newResolver;
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

	public void setUndoManager(StructuredTextUndoManager undoManager) {

		fUndoManager = undoManager;
	}

	/**
	 * Gets the contentTypeDescription.
	 * 
	 * @return Returns a ContentTypeDescription
	 */
	public IModelHandler getModelHandler() {

		return fModelHandler;
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
		boolean fireEvents = false;
		if (fModelHandler != null)
			fireEvents = true;
		if (fireEvents) {
			fireModelAboutToBeChanged();
		}
		fModelHandler = modelHandler;
		if (fireEvents) {
			fireModelChanged();
		}
	}

	public IStructuredModel newInstance() throws IOException {

		IStructuredModel newModel = null;
		// we delegate to the model manager, so loader, etc., can be
		// used.
		newModel = getModelManager().createNewInstance(this);
		return newModel;
	}

	/**
	 * Sets a "flag" that reinitialization is needed.
	 */
	public void setReinitializeNeeded(boolean needed) {

		reinitializationNeeded = needed;
	}

	public boolean isReinitializationNeeded() {

		return reinitializationNeeded;
	}

	/**
	 * Holds any data that the reinit procedure might find useful in
	 * reinitializing the model. This is handy, since the reinitialization may
	 * not take place at once, and some "old" data may be needed to properly
	 * undo previous settings. Note: the parameter was intentially made to be
	 * of type 'Object' so different models can use in different ways.
	 */
	public void setReinitializeStateData(Object object) {

		reinitializeStateData = object;
	}

	public Object getReinitializeStateData() {

		return reinitializeStateData;
	}

	/**
	 * @see org.eclipse.wst.sse.core.IStructuredModel#getContentTypeIdentifier()
	 */
	public String getContentTypeIdentifier() {

		return getModelHandler().getAssociatedContentTypeId();
	}

	public Object getAdapter(Class adapter) {

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
