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
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.encoding.EncodingRule;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.URIResolver;


/**
 * Responsible for providing a set of APIs for creating a new model manager, for managing (add or remove)
 * model loaders and model dumpers, and for managing (get, release, save, and save as) models.
 */
public interface IModelManager {

	public final static String DUPLICATED_MODEL = "org.eclipse.wst.sse.core.IModelManager.DUPLICATED_MODEL"; //$NON-NLS-1$
	public final static String UNMANAGED_MODEL = "org.eclipse.wst.sse.core.IModelManager.UNMANAGED_MODEL"; //$NON-NLS-1$

	/**
	 * This API allows clients to declare that they are about to make
	 * a "large" change to the model. This change
	 * might be in terms of content or it might be in terms of
	 * the model id or base location.
	 *
	 * Note that in the case of embedded calls, notification to
	 * listners is sent only once.
	 *
	 * The method isModelStateChanging can be used by a client
	 * to determin if the model is already in a change sequence.
	 */
	void aboutToChangeModels();

	/**
	 * Conveience method, since a proper IStructuredDocument must have
	 * a proper parser assigned. 
	 */
	IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException;

	/**
	 * Conveience method, since a proper IStructuredDocument must have
	 * a proper parser assigned. It should only be used when an 
	 * empty structuredDocument is needed. Otherwise, use 
	 * IFile form.
	 */
	IStructuredDocument createStructuredDocumentFor(String contentTypeId);

	/**
	 * @deprecated -- I marked as deprecated to discouage use 
	 * of this method. It does not really work for JSP fragments, 
	 * since JSP Fragments need an IFile to correctly look up 
	 * the content settings. Use IFile form instead. Note: 
	 * some confustion with it and the form for HTPP encoding, 
	 * so once a null arg is allowed in that API ... we can remove this one.
	 * (after verifying again with Tom/Linksbuild)
	 */
	IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver) throws IOException;

	IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException;

	IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String ianaEncodingName) throws IOException;

	/**
	 * Conveience method. It depends on the loaders
	 * newModel method to return an appropriate StrucuturedModel 
	 * appropriately initialized.
	 */
	IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException;

	/**
	 * Conveience method. It depends on the loaders
	 * newModel method to return an appropriate StrucuturedModel 
	 * appropriately initialized.
	 */
	IStructuredModel createUnManagedStructuredModelFor(String contentTypeId);

	IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver);

	void addModelManagerListener(IModelManagerListener listener);

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and must be called
	 * after aboutToChangeModel ... or some listeners could be left waiting indefinitely
	 * for the changed event. So, its suggested that changedModel always be in
	 * a finally clause. Likewise, a client should never call changedModel without
	 * calling aboutToChangeModel first.
	 *
	 * In the case of embedded calls, the notification is just sent once.
	 *
	 */
	void changedModels();

	IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse;

	/**
	 * This is similar to the getModel method, except this method does not create a model.
	 * This method does increment the reference count (if it exists).
	 * If the model does not already exist in the cache of models, null is returned.
	 */
	public IStructuredModel getExistingModelForEdit(Object id);

	public IStructuredModel getExistingModelForEdit(IFile iFile);

	/**
	 * This is similar to the getModel method, except this method does not create a model.
	 * This method does increment the reference count (if it exists).
	 * If the model does not already exist in the cache of models, null is returned.
	 */
	public IStructuredModel getExistingModelForRead(Object id);

	public IStructuredModel getExistingModelForRead(IFile iFile);

	public Enumeration getExistingModelIds();

	public IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException;

	public IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException;

	public IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	public IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	public IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException;

	public IStructuredModel getModelForRead(IFile iFile, String encoding, String lineDelimiter) throws java.io.UnsupportedEncodingException, IOException, CoreException;

	public IStructuredModel getModelForEdit(String filename, InputStream inStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException;

	public IStructuredModel getModelForRead(String filename, InputStream inStream, URIResolver resolver) throws java.io.UnsupportedEncodingException, IOException;

	/**
	 * This method will not create a new model if it already exists ... if force is false.
	 * The idea is that a client should call this method once with force set to false.
	 * If the exception is thrown, then prompt client if they want to overwrite.
	 */
	IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This method will not create a new model if it already exists ... if force is false.
	 * The idea is that a client should call this method once with force set to false.
	 * If the exception is thrown, then prompt client if they want to overwrite.
	 */
	IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This function returns the reference count of underlying model.
	 * @param id Object The id of the model
	 * TODO: try to refine the design not to use this function
	 */
	int getReferenceCount(Object id);

	/**
	 * This function returns the reference count of underlying model.
	 * @param id Object The id of the model
	 * TODO: try to refine the design not to use this function
	 */
	int getReferenceCountForEdit(Object id);

	/**
	 * This function returns the reference count of underlying model.
	 * @param id Object The id of the model
	 * TODO: try to refine the design not to use this function
	 */
	int getReferenceCountForRead(Object id);

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * @param id Object The id of the model
	 */
	boolean isShared(Object id);

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * @param id Object The id of the model
	 */
	boolean isSharedForEdit(Object id);

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * @param id Object The id of the model
	 */
	boolean isSharedForRead(Object id);

	/**
	 * This method can be called to determine if the model manager is within a "aboutToChange" and "changed" sequence.
	 */
	public boolean isStateChanging();

	/**
	 * This method changes the id of the model.
	 * TODO: try to refine the design not to use this function
	 */
	void moveModel(Object oldId, Object newId);

	/**
	 * This is similar to the getModel method, except this method does not use the cached version,
	 * but forces the cached version to be replaced with a fresh, unchanged version. Note: this method
	 * does not change any reference counts. Also, if there is not already a cached version of the 
	 * model, then this call is essentially ignored (that is, it does not put a model in the cache) and
	 * returns null.
	 */
	IStructuredModel reloadModel(Object id, InputStream inStream) throws UnsupportedEncodingException;

	/**
	 * This method can be called when the content type of a model
	 * changes. Its assumed the contentType has already been changed,
	 * and this method uses the text of the old one, to repopulate the
	 * text of the new one. In theory, the actual instance could change,
	 * (e.g. using 'saveAs' to go from xml to dtd), but in practice, 
	 * the intent of this API is to return the same instance, just using 
	 * different handlers, adapter factories, etc.
	 */
	IStructuredModel reinitialize(IStructuredModel model) throws IOException;

	void removeModelManagerListener(IModelManagerListener listener);

	void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * Calculate id provides a common way to determine the id from the input ... needed to
	 * get and save the model. It is a simple class utility, but is an 
	 * instance method so can be accessed via interface.
	 */
	public String calculateId(IFile file);

	/**
	 * CalculateBaseLocation provides a common way to determine the baseLocation from the input.
	 * The 'setBaseLocation' must still be called for a IStructuredModel, by the client using/creating it,
	 * but this providides a convenient way to get a common value, if desired.
	 * @deprecated is this still needed?
	 */
	public String calculateBaseLocation(IFile file);

	/**
	 * createNewInstance is similar to clone, except the new instance has no content.
	 * Note: this produces an unmanaged model, for temporary use.
	 * If a true shared model is desired, use "copy". 
	 */
	public IStructuredModel createNewInstance(IStructuredModel model) throws IOException;

	/**
	 *	Note: users of this 'model' must still release it 
	 *  when finished. 
	 *  Returns null if there's not a model corresponding to document.
	 */
	IStructuredModel getExistingModelForRead(IDocument document);

	/**
	 *	Note: users of this 'model' must still release it 
	 *  when finished. 
	 *  Returns null if there's not a model corresponding to document.
	 */
	IStructuredModel getExistingModelForEdit(IDocument document);

}
