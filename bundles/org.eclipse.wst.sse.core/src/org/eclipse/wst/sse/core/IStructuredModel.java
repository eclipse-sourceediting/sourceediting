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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.util.URIResolver;


/**
 */
public interface IStructuredModel extends IAdaptable {


	/**
	 * This API allows clients to declare that they are about to make a
	 * "large" change to the model. This change might be in terms of content
	 * or it might be in terms of the model id or base location.
	 * 
	 * Note that in the case of embedded calls, notification to listeners is
	 * sent only once.
	 * 
	 * Note that the client who is making these changes has the responsibility
	 * to restore the model's state once finished with the changes. See
	 * getMemento and restoreState.
	 * 
	 * The method isModelStateChanging can be used by a client to determine if
	 * the model is already in a change sequence.
	 * 
	 * This method is a matched pair to changedModel, and must be called
	 * before changedModel. A client should never call changedModel without
	 * calling aboutToChangeModel first nor call aboutToChangeModel without
	 * calling changedModel later from the same Thread.
	 */
	void aboutToChangeModel();

	void addModelLifecycleListener(IModelLifecycleListener listener);

	void addModelStateListener(IModelStateListener listener);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, int cursorPosition, int selectionLength);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, int cursorPosition, int selectionLength);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, String description);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, String description, int cursorPosition, int selectionLength);

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and must be called after aboutToChangeModel ...
	 * or some listeners could be left waiting indefinitely for the changed
	 * event. So, its suggested that changedModel always be in a finally
	 * clause. Likewise, a client should never call changedModel without
	 * calling aboutToChangeModel first.
	 * 
	 * In the case of embedded calls, the notification is just sent once.
	 *  
	 */
	void changedModel();

	long computeModificationStamp(IResource resource);

	/**
	 * @deprecated
	 * @return com.ibm.sed.model.IStructuredModel
	 */
	IStructuredModel copy(String id) throws ResourceInUse, ResourceAlreadyExists;

	/**
	 * Disable undo management.
	 */
	void disableUndoManagement();

	/**
	 * Enable undo management.
	 */
	void enableUndoManagement();

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester);

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester, int cursorPosition, int selectionLength);

	/**
	 * This is a client-defined value for what that client (and/or loader)
	 * considers the "base" of the structured model. Frequently the location
	 * is either a workspace root-relative path of a workspace resource or an
	 * absolute path in the local file system.
	 */
	String getBaseLocation();

	/**
	 * @return The associated content type identifier (String) for this model.
	 */
	String getContentTypeIdentifier();

	/**
	 * 
	 * @return The model's FactoryRegistry. A model is not valid without one.
	 */
	FactoryRegistry getFactoryRegistry();

	/**
	 * The id is the id that the model manager uses to identify this model
	 */
	String getId();

	/**
	 *  
	 */
	IndexedRegion getIndexedRegion(int offset);

	/**
	 * ContentTypeDescription provides an object that describes what the
	 * content of the file is, e.g. HTML, XML, etc. Compare with
	 * getExternalFileTypeDescription. Though they both return objects of type
	 * ContentTypeDescription, the external file type is intended to denote
	 * JSP, regardless of what the content of that JSP file is. Even for a JSP
	 * file, the ContentTypeDescription will be set according to that file's
	 * "internal" contents.
	 * 
	 * @return ContentTypeDescription
	 */
	IModelHandler getModelHandler();

	IModelManager getModelManager();

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	int getReferenceCount();

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	int getReferenceCountForEdit();

	/**
	 * This function returns the reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 */
	int getReferenceCountForRead();

	Object getReinitializeStateData();

	/**
	 * Get URI resolution helper
	 */
	URIResolver getResolver();

	IStructuredDocument getStructuredDocument();

	/**
	 * modification date of underlying resource, when this model was open, or
	 * last saved. (Note: for this version, the client must manage the
	 * accuracy of this data)
	 */
	long getSynchronizationStamp();

	/**
	 * Get undo manager.
	 */
	IStructuredTextUndoManager getUndoManager();

	/**
	 *  
	 */
	boolean isDirty();

	/**
	 * This method can be called to determine if the model is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public boolean isModelStateChanging();

	/**
	 *  
	 */
	boolean isNew();

	boolean isReinitializationNeeded();

	/**
	 * This is a combination of if the model is dirty and if the model is
	 * shared for write access. The last writer as the responsibility to be
	 * sure the user is prompted to save.
	 */
	public boolean isSaveNeeded();

	/**
	 * This function returns true if either isSharedForRead or isSharedForEdit
	 * is true.
	 */
	boolean isShared();

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	boolean isSharedForEdit();

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	boolean isSharedForRead();

	/**
	 * newInstance is similar to clone, except that the newInstance contains
	 * no content. Its purpose is so clients can get a temporary, unmanaged,
	 * model of the same "type" as the original. Note: the client may still
	 * need to do some intialization of the model returned by newInstance,
	 * depending on desired use. For example, the only factories in the
	 * newInstance are those that would be normally be created for a model of
	 * the given contentType. Others are not copied automatically, and if
	 * desired, should be added by client.
	 */
	IStructuredModel newInstance() throws IOException;

	/**
	 * Performs a reinit procedure. For this model. Note for future: there may
	 * be a day where the model returned from this method is a different
	 * instance than the instance it was called on. This will occur when there
	 * is full support for "save as" type functions, where the model could
	 * theoretically change completely.
	 */
	IStructuredModel reinit() throws IOException;

	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 *  
	 */
	void releaseFromEdit();

	/**
	 * This function allows the model to free up any resources it might be
	 * using. In particular, itself, as stored in the IModelManager.
	 *  
	 */
	void releaseFromRead();

	/**
	 * This function replenishes the model with the resource without saving
	 * any possible changes. It is used when one editor may be closing, and
	 * specifially says not to save the model, but another "display" of the
	 * model still needs to hang on to some model, so needs a fresh copy.
	 * 
	 * Only valid for use with managed models.
	 */
	IStructuredModel reload(InputStream inputStream) throws IOException;

	void removeModelLifecycleListener(IModelLifecycleListener listener);

	void removeModelStateListener(IModelStateListener listener);

	/**
	 * A method that modififies the model's synchonization stamp to match the
	 * resource. Turns out there's several ways of doing it, so this ensures a
	 * common algorithm.
	 */
	void resetSynchronizationStamp(IResource resource);

	void resourceDeleted();

	void resourceMoved(IStructuredModel newModel);

	void save() throws UnsupportedEncodingException, IOException, CoreException;

	void save(EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	void save(IFile iFile) throws UnsupportedEncodingException, IOException, CoreException;

	void save(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	void save(OutputStream outputStream) throws UnsupportedEncodingException, IOException, CoreException;

	void setBaseLocation(String newBaseLocation);

	public void setDirtyState(boolean dirtyState);

	void setFactoryRegistry(FactoryRegistry registry);

	/**
	 * The id is the id that the model manager uses to identify this model
	 */
	void setId(String id) throws ResourceInUse;

	void setModelHandler(IModelHandler modelHandler);

	void setModelManager(IModelManager modelManager);

	public void setNewState(boolean newState);

	/**
	 * Sets a "flag" that reinitialization is needed.
	 */
	void setReinitializeNeeded(boolean b);

	/**
	 * Holds any data that the reinit procedure might find useful in
	 * reinitializing the model. This is handy, since the reinitialization may
	 * not take place at once, and some "old" data may be needed to properly
	 * undo previous settings. Note: the parameter was intentially made to be
	 * of type 'Object' so different models can use in different ways.
	 */
	void setReinitializeStateData(Object object);

	/**
	 * Set the URI resolution helper
	 */
	void setResolver(URIResolver uriResolver);

	void setStructuredDocument(IStructuredDocument structuredDocument);

	/**
	 * Set undo manager.
	 */
	void setUndoManager(IStructuredTextUndoManager undoManager);
}
