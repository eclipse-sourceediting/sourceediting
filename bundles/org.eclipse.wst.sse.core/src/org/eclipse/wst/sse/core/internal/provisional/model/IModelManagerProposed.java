/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.provisional.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * Responsible for managing IStructuredModels.
 * 
 * This allows clients to share models, so they do not have to be re-created
 * or passed around from one client to another. Clients should internally
 * enforce that models are gotten and released from locations close to each
 * other to prevent model leaks.
 * 
 * There are three ways to get a model based on usage and responsibility: for
 * 'MODIFY', just for 'SHARED', and 'UNSHARED'. Contrary to their names, a
 * model can technically be modified, and all modifications directly affect
 * the commonly shared version of the model. It is part of the API contract,
 * however, that clients who get a model for SHARED do not modify it. The
 * significance of the 'MODIFY' model is that the client is registering their
 * interest in saving changes to the model.
 * 
 * Clients can reference this interface, but should not implement.
 * 
 * @see org.eclipse.wst.sse.core.StructuredModelManager
 * @plannedfor 1.0
 */
public interface IModelManagerProposed {

	/**
	 * AccessType is used internally as a JRE 1.4 compatible enumerated type.
	 * Not intended to be referenced by clients.
	 */
	static class AccessType {
		private String fType;

		/**
		 * default access contructor we use to create our specific constants.
		 * 
		 * @param type
		 */
		AccessType(String type) {
			super();
			fType = type;
		}

		/**
		 * For debug purposes only.
		 */
		public String toString() {
			return "Model Access Type: " + fType; //$NON-NLS-1$
		}
	}

	/**
	 * Constant to provide compile time safe parameter. <code>NOTSHARED</code>signifies
	 * the client intentially wants a model that is not shared with other
	 * clients. NOTSHARED models can not be saved.
	 */
	final AccessType NOTSHARED = new AccessType("NOTSHARED"); //$NON-NLS-1$

	/**
	 * Constant to provide compile-time safe parameter. <code>SHARED</code>signifies
	 * the client is not intending to make changes and does not care whether
	 * the model accessed is saved.
	 */
	final AccessType SHARED = new AccessType("SHARED"); //$NON-NLS-1$

	/**
	 * Constant to provide compile-time safe parameter. <code>MODIFY</code>signifies
	 * the client is intending to make changes and is responsible for saving
	 * changes (or not) if they are the last one holding MODIFY access to the
	 * model before it's released.
	 */
	final AccessType MODIFY = new AccessType("MODIFY"); //$NON-NLS-1$

	/**
	 * copyModel is similar to a deep clone. The resulting model is shared,
	 * according to the value of ReadEditType. If a model already is being
	 * managed for 'newLocation' then a ResourceInUse exception is thrown,
	 * unless the ReadEditType is NOTSHARED, in which case the resulting model
	 * can not be saved.
	 * 
	 * @param oldLocation
	 * @param newLocation
	 * @param type
	 * @return
	 * @throws ResourceInUse
	 * 
	 * ISSUE: is this important enough to be API, or can clients solve
	 * themselves
	 */
	IStructuredModel copyModel(IPath oldLocation, IPath newLocation, AccessType type) throws ResourceInUse;

	/**
	 * createNewInstance is similar to clone, except the new instance has no
	 * text content. Note: this produces an UNSHARED model, for temporary use,
	 * that has the same characteristics as original model. If a true shared
	 * model is desired, use "copy".
	 * 
	 * ISSUE: still needed?
	 * 
	 * @param requester
	 * @param model
	 * @return
	 * @throws IOException
	 */
	public IStructuredModel createNewInstance(Object requester, IStructuredModel model) throws IOException;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: its assume that IPath does not actually exist as
	 * a resource yet. If it does, ResourceAlreadyExists exception is thrown.
	 * If the resource does already exist, then createStructuredDocumentFor is
	 * the right API to use.
	 * 
	 * ISSUE: do we want to support this via model manager, or else where?
	 * ISSUE: do we need two of these? What's legacy use case?
	 * 
	 * @param location
	 * @param progressMonitor
	 * @return
	 * @throws ResourceAlreadyExists
	 * @throws IOException
	 * @throws CoreException
	 */
	IStructuredDocument createNewStructuredDocumentFor(IPath location, IProgressMonitor progressMonitor) throws ResourceAlreadyExists, IOException, CoreException;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: clients should verify that the resource
	 * identified by the IPath exists before using this method. If this IFile
	 * does not exist, then createNewStructuredDocument is the correct API to
	 * use.
	 * 
	 * ISSUE: do we want to support this via model manager, or else where?
	 * ISSUE: do we need two of these? What's legacy use case?
	 * 
	 * @param location
	 * @param progressMonitor
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	IStructuredDocument createStructuredDocumentFor(IPath location, IProgressMonitor progressMonitor) throws IOException, CoreException;

	/**
	 * Note: callers of this method must still release the model when
	 * finished. Returns the model for this document if it already exists and
	 * is being shared. Returns null if this is not the case. The ReadEditType
	 * must be either MODIFY or SHARED.
	 * 
	 * ISSUE: should we accept IDocument on parameter for future evolution,
	 * and constrain to StructuredDocuments at runtime?
	 * 
	 * @param requester
	 * @param type
	 * @param document
	 * @return
	 */
	IStructuredModel getExistingModel(Object requester, AccessType type, IDocument document);

	/**
	 * Callers of this method must still release the model when finished.
	 * Returns the model for this location if it already exists and is being
	 * shared. Returns null if this is not the case. The ReadEditType must be
	 * either MODIFY or SHARED.
	 * 
	 * @param requester
	 * @param type
	 * @param location
	 * @return
	 */
	public IStructuredModel getExistingModel(Object requester, AccessType type, IPath location);

	/**
	 * Returns the model that has the specified document as its structured
	 * document.
	 * 
	 * @param requester
	 * @param type
	 * @param progressMonitor
	 * @param document
	 * @return
	 */
	public IStructuredModel getModel(Object requester, AccessType type, IProgressMonitor progressMonitor, IDocument document);

	/**
	 * Returns the model based on the content at the specified location.
	 * 
	 * Note: if the ModelManager does not know how to create a model for 
	 * such a file for content, null is return.
	 * ISSUE: should we throw some special, meaningful, checked 
	 * exception instead?
	 * 
	 * @param requester
	 * @param type
	 * @param progressMonitor
	 * @param location
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public IStructuredModel getModel(Object requester, AccessType type, IProgressMonitor progressMonitor, IPath location) throws IOException, CoreException;

	/**
	 * This method will not create a new model if it already exists ... if
	 * force is false. The idea is that a client should call this method once
	 * with force set to false. If the exception is thrown, then prompt client
	 * if they want to overwrite.
	 * 
	 * @param requester
	 * @param location
	 * @param force
	 * @param type
	 * @param progressMonitor
	 * @return
	 * @throws ResourceAlreadyExists
	 * @throws ResourceInUse
	 * @throws IOException
	 * @throws CoreException
	 */
	IStructuredModel getNewModel(Object requester, IPath location, boolean force, AccessType type, IProgressMonitor progressMonitor) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * 
	 * @param location
	 * @return
	 */
	boolean isShared(IPath location);

	/**
	 * This function returns true if there are other references to the
	 * underlying model, shared in the specified way. The ReadEditType must be
	 * either MODIFY or SHARED.
	 * 
	 * @param location
	 * @param type
	 * @return
	 */
	boolean isShared(IPath location, AccessType type);

	/**
	 * This method can be called when the content type of a model changes. Its
	 * assumed the contentType has already been changed, and this method uses
	 * the text of the old one, to repopulate the text of the new one.
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	IStructuredModel reinitialize(IStructuredModel model) throws IOException;


	/**
	 * This is used by clients to signify that they are finished with a model
	 * and will no longer access it or any of its underlying data (such as its
	 * structured document). The ReadEditType must match what ever the client
	 * used in the corresponding 'get' method.
	 * 
	 * This method must be called for every 'get'. Clients should use the
	 * try/finally pattern to ensure the release is called even if there is an
	 * unexpected exception.
	 * 
	 * @param requester
	 * @param structuredModel
	 * @param type
	 */
	void releaseModel(Object requester, IStructuredModel structuredModel, AccessType type);

	/**
	 * Writes the underlying document to the IPath.
	 * 
	 * ISSUE: we want to just "dump" contents to location, but need to spec.
	 * all the different cases of shared, etc.
	 * 
	 * ?If to same location as 'get', then same instance of model, If to
	 * different location (esp. if shared) then need to leave old instance of
	 * model, create new model, and save to new location. ?
	 * 
	 * Cases: IPath is null, write to IPath created with IPath specificed and
	 * equals IPath created with, write to IPath IPath specified and not
	 * equals IPath created with, dumps to new IPath, no change in current
	 * model state.
	 * 
	 * ISSUE: think through 'normalization' cases
	 * 
	 * 
	 * @param structuredModel
	 * @param location - already normalized?
	 * @param progressMonitor
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 * 
	 * ISSUE: resource aleady exists? veto override
	 */
	void saveModel(IStructuredModel structuredModel, IPath location, IProgressMonitor progressMonitor) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * Writes the underlying document to the IPath if the model is only shared
	 * for EDIT by one client. This is the recommended way for 'background
	 * jobs' to save models, in case the model is being shared by an editor,
	 * or other client that might desire user intervention to save a resource.
	 * 
	 * @param structuredModel
	 * @param location - already normalized?
	 * @param progressMonitor
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws CoreException
	 * 
	 * ISSUE: is locaiton needed in this case, or just use the one it was
	 * created with
	 */
	void saveModelIfNotShared(IStructuredModel structuredModel, IPath location, IProgressMonitor progressMonitor) throws UnsupportedEncodingException, IOException, CoreException;
}
