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
package org.eclipse.wst.sse.core.internal.provisional.model;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;


/**
 * IStructuredModel's are mainly interesting by their extensions and
 * implementers. The main purposed of this abstraction it to provide a common
 * way to manage models that have an associated structured documnet.
 * 
 * @plannedfor 1.0
 * 
 */
public interface IStructuredModelProposed extends IAdaptable {


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

	void addModelStateListener(IModelStateListener listener);

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

	/**
	 * This is a client-defined value for what that client (and/or loader)
	 * considers the "base" of the structured model. Frequently the location
	 * is either a workspace root-relative path of a workspace resource or an
	 * absolute path in the local file system.
	 */
	IPath getLocation();

	/**
	 * @return The associated content type identifier (String) for this model.
	 */
	IContentType getContentType() throws CoreException;

	/**
	 * 
	 * @return The model's FactoryRegistry. A model is not valid without one.
	 */
	FactoryRegistry getFactoryRegistry();

	/**
	 * Return the index region at offset. Returns null if there is no
	 * IndexedRegion that contains offset.
	 */
	IndexedRegion getIndexedRegion(int offset);

	/**
	 * ISSUE: do we want to provide this? How to ensure job/thread safety
	 * 
	 * @return
	 */
	IndexedRegion[] getIndexedRegions();

	/**
	 * Rreturns the IStructuredDocument that underlies this model
	 * 
	 * @return
	 */
	IStructuredDocument getStructuredDocument();

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
	 * newInstance is similar to clone, except that the newInstance contains
	 * no content. Its purpose is so clients can get a temporary, unmanaged,
	 * model of the same "type" as the original. Note: the client may still
	 * need to do some intialization of the model returned by newInstance,
	 * depending on desired use. For example, the only factories in the
	 * newInstance are those that would be normally be created for a model of
	 * the given contentType. Others are not copied automatically, and if
	 * desired, should be added by client.
	 */
	IStructuredModelProposed newInstance() throws IOException;

	void removeModelStateListener(IModelStateListener listener);
}
