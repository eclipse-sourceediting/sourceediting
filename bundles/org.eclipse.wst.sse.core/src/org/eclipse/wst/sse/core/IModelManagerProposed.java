/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


/**
 * Responsible for managing structured models.
 * 
 * This allows clients to share models, so they do not have to be re-created
 * or "passed around" from one client to another. There is a heavy burden on
 * clients, though, in that every client who 'gets' a model must correctly
 * 'release' it.
 * 
 * There are two ways to get a model, for 'EDIT' or for 'READ'. Contrary to
 * their names, either model can, technically, be modified, and when modified,
 * it modifies the commonily shared version of the model. It is part of the
 * API contract however, that clients who get a model for READ do not modify
 * it. The significance of the 'EDIT' model is that the client is registering
 * their interest in saving changes to the model.
 * 
 * Clients can reference this interface, but should not implement.
 * 
 * @see StructuredModelManger
 * @since 1.0
 */
public interface IModelManagerProposed {

	/**
	 * ReadEditType is used internally to create. Not intented to be
	 * referenced by clients.
	 */
	static class ReadEditType {
		private String fType;

		/**
		 * default access contructor we use to create our specific constants.
		 * 
		 * @param type
		 */
		ReadEditType(String type) {
			super();
			fType = type;
		}

		/**
		 * For debug purposes only.
		 */
		public String toString() {
			return "Model ReadEditType: " + fType;
		}
	}

	/**
	 * Constant to provide compile time safe paramenter. <code>EDIT</code>signifies
	 * the client is intending to make changes and is responsible for saving
	 * changes (or not) if they are the last one holding the model before its
	 * released.
	 */
	final ReadEditType EDIT = new ReadEditType("EDIT"); //$NON-NLS-1$
	/**
	 * Constant to provide compile time safe paramenter. <code>READ</code>signifies
	 * the client is not intending to make changes and does not care about the
	 * saved state of the model.
	 */
	final ReadEditType READ = new ReadEditType("READ"); //$NON-NLS-1$
	/**
	 * Constant to provide compile time safe paramenter.
	 * <code>NOTSHARED</code>signifies the client intentially wants a model
	 * that is not shared with other clients. Note: NOTSHARED models can not
	 * be saved.
	 */
	final ReadEditType NOTSHARED = new ReadEditType("NOTSHARED"); //$NON-NLS-1$

	/**
	 * createNewInstance is similar to clone, except the new instance has no
	 * text content. Note: this produces an UNSHARED model, for temporary use,
	 * that has the same characteristics as original model. If a true shared
	 * model is desired, use "copy".
	 */
	public IStructuredModel createNewInstance(IStructuredModel model) throws IOException;

	/**
	 * Returns the model, if it already exists and is being shared. Returns
	 * null if this is not the case.
	 * 
	 * @param location
	 * @param type
	 * @return
	 */
	public IStructuredModel getExistingModel(IPath location, ReadEditType type);

	/**
	 * Returns the model that has the specified document as its core text
	 * buffer.
	 * 
	 * @param document
	 * @param type
	 * @return
	 */
	public IStructuredModel getModel(IDocument document, ReadEditType type);

	/**
	 * Returns the model based on the content at the specified location.
	 * 
	 * @param location
	 * @param type
	 * @return
	 */
	public IStructuredModel getModel(IPath location, ReadEditType type) throws IOException, CoreException;

	/**
	 * This method can be called to determine if the model manager is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public boolean isStateChanging();

	/**
	 * This API allows clients to declare that they are about to make a
	 * "large" change to many models.
	 * 
	 * In the case of embedded calls, the notification is just sent once.
	 */
	void aboutToChangeModels();

	/**
	 * Adds a model manager listener.
	 * 
	 * @param listener
	 */
	void addModelManagerListener(IModelManagerListener listener);

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
	void changedModels();

	/**
	 * copyModel is similar to a deep clone. The resulting model is shared,
	 * according to the value of ReadEditType. If a model already is being
	 * managed for 'newLocation' then a ResourceInUse exception is thrown,
	 * unless the ReadEditType is NOTSHARED, in which case the resulting model
	 * can not be saved.
	 * 
	 * @param oldId
	 * @param newId
	 * @return
	 * @throws ResourceInUse
	 */
	IStructuredModel copyModel(IPath oldLocation, IPath newLocation, ReadEditType type) throws ResourceInUse;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: its assume that IFile does not actually exist as
	 * a resource yet. If it does, ResourceAlreadyExists exception is thrown.
	 * If the resource does already exist, then createStructuredDocumentFor is
	 * the right API to use.
	 * 
	 * ISSUE: do we want to support this via model manager, or else where?
	 */
	IStructuredDocument createNewStructuredDocumentFor(IPath location) throws ResourceAlreadyExists, IOException, CoreException;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: clients should verify IFile exists before using
	 * this method. If this IFile does not exist, then
	 * createNewStructuredDocument is the correct API to use.
	 */
	IStructuredDocument createStructuredDocumentFor(IPath location) throws IOException, CoreException;

	/**
	 * Note: users of this 'model' must still release it when finished.
	 * Returns null if there's not a model corresponding to document.
	 * 
	 * ISSUE: should we accept IDocument on parameter for future evolution,
	 * and constrain to StructuredDocuments at runtime?
	 */
	IStructuredModel getExistingModel(IDocument document, ReadEditType type);

	/**
	 * This method will not create a new model if it already exists ... if
	 * force is false. The idea is that a client should call this method once
	 * with force set to false. If the exception is thrown, then prompt client
	 * if they want to overwrite.
	 */
	IStructuredModel getNewModel(IPath location, boolean force, ReadEditType type) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	boolean isShared(IPath location);

	/**
	 * This function returns true if there are other references to the
	 * underlying model, shared in the specified way.
	 * 
	 */
	boolean isShared(IPath location, ReadEditType type);

	/**
	 * This method can be called when the content type of a model changes. Its
	 * assumed the contentType has already been changed, and this method uses
	 * the text of the old one, to repopulate the text of the new one.
	 * 
	 */
	IStructuredModel reinitialize(IStructuredModel model) throws IOException;


	/**
	 * Removes listener. If the listener is is not currently in the list of
	 * listeners, this call has no effect.
	 * 
	 * @param listener
	 */
	void removeModelManagerListener(IModelManagerListener listener);

	/**
	 * Writes the underlying document to the IPath.
	 * 
	 * @param structuredModel
	 * @param location
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 */
	void saveModel(IStructuredModel structuredModel, IPath location) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * Writes the underlying document to the IPath if the model is only shared
	 * for EDIT by one client. This is the recommended way for 'background
	 * jobs' to save models, in case the model is being shared by an editor,
	 * or other client that might desire user intervention to save a resource.
	 * 
	 * @param structuredModel
	 * @param location
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 */
	void saveModelIfNecessary(IStructuredModel structuredModel, IPath location) throws UnsupportedEncodingException, IOException, CoreException;

}
