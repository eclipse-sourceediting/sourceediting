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
package org.eclipse.wst.sse.core.internal.provisional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.URIResolver;

/**
 * <p>
 * Provides APIs for managing (get, release, save, and save as) SSE Structured
 * Models.
 * </p>
 * <p>
 * Structured Models created from an implementor of this interface can be
 * either managed or unmanaged. Managed models are shared using reference
 * counts, so until that count has been decremented to zero, the model will
 * continue to exist in memory. When managed, models can be looked up using
 * their IDs or their IStructuredDocuments, which can be advantageous when
 * building on APIs that aren't specifically designed for SSE (such as those
 * revolving around IDocuments). Unmanaged models offer no such features, and
 * are largely used for tasks where their contents are ephemeral, such as for
 * populating a source viewer with syntax-colored content.
 * </p>
 * <p>
 * There are two types of access used when retrieving a model from the model
 * manager: READ and EDIT. The contents of a model can be modified regardless
 * of which access type is used, but any client who gets a model for EDIT is
 * explicitly declaring that they are interested in saving those changed
 * contents. The EDIT and READ reference counts are visible to everyone, as
 * are convenience methods for determining whether a managed model is shared
 * among multiple clients accessing it for READ or EDIT.
 * </p>
 * <p>
 * Managed models whose contents are "dirty" with READ and EDIT counts above
 * zero will be reverted to the on-disk content if the EDIT count drops to
 * zero while the READ count remains above zero.
 * </p>
 * <p>
 * Shared models for which the read and edit counts have both dropped to zero
 * are no longer valid for use, regardless of whether they have been garbage
 * collected or not. It is possible, but not guaranteed, that the underlying
 * structured document is still valid and may even be used in constructing a
 * new shared model.
 * </p>
 * <p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              </p>
 *              <p>
 * @see StructuredModelManger</p>
 */
public interface IModelManager {

	/**
	 * A fixed ID used for models which were created as duplicates of existing
	 * models
	 */
	public final static String DUPLICATED_MODEL = "org.eclipse.wst.sse.core.IModelManager.DUPLICATED_MODEL"; //$NON-NLS-1$

	/**
	 * A fixed ID used for unmanaged models
	 */
	public final static String UNMANAGED_MODEL = "org.eclipse.wst.sse.core.IModelManager.UNMANAGED_MODEL"; //$NON-NLS-1$

	/**
	 * Calculate id provides a common way to determine the id from the input
	 * ... needed to get and save the model. It is a simple class utility, but
	 * is an instance method so can be accessed via interface.
	 */
	public String calculateId(IFile file);

	/**
	 * Copies a model with the old id 
	 * @param oldId - the old model's ID
	 * @param newId - the new model's ID
	 * @return the new model
	 * @throws ResourceInUse if the given new ID is already in use by a managed model
	 */
	IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse;

	/**
	 * Creates a new, but empty, unmanaged model of the same kind as the one
	 * given. For a managed model with the same contents, use "copy".
	 * 
	 * @param model
	 * @return the model, or null of one could not be created
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	public IStructuredModel createNewInstance(IStructuredModel model) throws IOException;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. If the resource does already exist, then
	 * createStructuredDocumentFor is the right API to use.
	 * 
	 * @param iFile
	 * @return the document, or null if one could not be created
	 * @throws ResourceAlreadyExists
	 *             if the IFile already exists
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 * @throws ResourceAlreadyExists if the give file already exists
	 */
	IStructuredDocument createNewStructuredDocumentFor(IFile iFile) throws ResourceAlreadyExists, IOException, CoreException;

	/**
	 * Factory method, since a proper IStructuredDocument must have a proper
	 * parser assigned. Note: clients should verify IFile exists before using
	 * this method. If this IFile does not exist, then
	 * {@link #createNewStructuredDocumentFor(IFile)} is the correct API to use.
	 * 
	 * @param iFile - the file
	 * @return the document, or null if one could not be created
	 * 
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException;

	/**
	 * Convenience method, since a proper IStructuredDocument must have a
	 * proper parser assigned. It should only be used when an empty
	 * structuredDocument is needed. Otherwise, use IFile form.
	 * 
	 * @param contentTypeId
	 * @return a structured document with the correct parsing setup for the
	 *         given content type ID, or null if one could not be created or
	 *         the given content type ID is unknown or unsupported
	 */
	IStructuredDocument createStructuredDocumentFor(String contentTypeId);

	/**
	 * @deprecated - use IFile form instead as the correct encoding and content rules may not be applied otherwise
	 * 
	 * Creates and returns a properly configured structured document for the given contents with the given name
	 * 
	 * @param filename - the filename, which may be used to guess the content type
	 * @param contents - the contents to load
	 * @param resolver - the URIResolver to use for locating any needed resources
	 * @return the IStructuredDocument or null of one could not be created
	 * @throws IOException if the file's contents can not be read or its content type can not be determined
	 */ 
	IStructuredDocument createStructuredDocumentFor(String filename, InputStream contents, URIResolver resolver) throws IOException;

	/**
	 * Creates and returns a properly configured structured document for the given contents with the given name
	 * 
	 * @param filename - the filename, which may be used to guess the content type
	 * @param inputStream - the contents to load
	 * @param resolver - the URIResolver to use for locating any needed resources
	 * @param ianaEncodingName - the IANA specified encoding to use when reading the contents
	 * @return the IStructuredDocument or null if one could not be created
	 * @throws IOException if the file's contents can not be read or its content type can not be determined
	 * @deprecated - clients should convert the InputStream into text themselves
	 *             and then use the version of this method taking a String for its
	 *             content
	 */
	IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String ianaEncodingName) throws IOException;

	/**
	 * Creates and returns a properly configured structured document for the given contents with the given name
	 * 
	 * @param filename - the filename, which may be used to guess the content type
	 * @param content - the contents to load
	 * @param resolver - the URIResolver to use for locating any referenced resources
	 * @return a structured document with the correct parsing setup for the
	 *         given filename, or null if one could not be created
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException;

	/**
	 * Creates and returns an unmanaged model populated with the given IFile's
	 * contents
	 * 
	 * @param iFile
	 * @return a structured model, or null if one could not be created
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException;

	/**
	 * Convenience method. It depends on the loader's newModel method to return
	 * an appropriate StrucuturedModel appropriately initialized.
	 * 
	 * @param contentTypeId
	 * @return a structured model for the given content type, or null if one could not be created or the content type is unsupported
	 */
	IStructuredModel createUnManagedStructuredModelFor(String contentTypeId);

	/**
	 * @deprecated
	 */
	IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver);

	/**
	 * Note: callers of this method must still release the model when finished.
	 * 
	 * @param document
	 * @return the structured model containing the give document, incrementing
	 *         its edit count, or null if there is not a model corresponding
	 *         to this document.
	 */
	IStructuredModel getExistingModelForEdit(IDocument document);

	/**
	 * @param file
	 * @return the structured model for the given file, incrementing its edit
	 *         count, or null if one does not already exist for this file.
	 */
	IStructuredModel getExistingModelForEdit(IFile file);

	/**
	 * @param id
	 * @return the structured model with the given ID, incrementing its edit
	 *         count, or null if one does not already exist for this ID
	 */
	public IStructuredModel getExistingModelForEdit(Object id);

	/**
	 * Note: callers of this method must still release the model when finished.
	 * 
	 * @param document
	 * @return the structured model containing the give document, incrementing
	 *         its read count, or null if there is not a model corresponding
	 *         to this document.
	 */
	IStructuredModel getExistingModelForRead(IDocument document);

	/**
	 * @param file
	 * @return the structured model for the given file, incrementing its read
	 *         count, or null if one does not already exist for this file.
	 */
	public IStructuredModel getExistingModelForRead(IFile iFile);

	/**
	 * @param id
	 * @return the structured model with the given ID, incrementing its edit
	 *         count, or null if one does not already exist for this ID
	 */
	public IStructuredModel getExistingModelForRead(Object id);

	/**
	 * @deprecated - internal information
	 */
	public Enumeration getExistingModelIds();

	/**
	 * Returns a structured model for the given file. If one does not already
	 * exists, one will be created with an edit count of 1. If one already
	 * exists, its edit count will be incremented before it is returned.
	 * 
	 * @param iFile
	 * @return a structured model for the given file, or null if one could not
	 *         be found or created
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	public IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException;

	/**
	 * Returns a structured model for the given file. If one does not already
	 * exists, one will be created with an edit count of 1. If one already
	 * exists, its edit count will be incremented before it is returned.
	 * 
	 * @param iFile
	 * @param encodingRule the rule for handling encoding
	 * @return a structured model for the given file, or null if one could not
	 *         be found or created
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 * @deprecated - encoding is handled automatically based on the file's
	 *             contents or user preferences
	 */
	public IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * @deprecated - Encoding and the line delimiter used are handled
	 *             automatically based on the file's contents or user
	 *             preferences.
	 */
	public IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * Returns a structured model for the given document. If one does not
	 * already exists, one will be created with an edit count of 1. If one
	 * already exists, its edit count will be incremented before it is
	 * returned. This method is intended only to interact with documents
	 * contained within File Buffers.
	 * 
	 * @param textFileBufferDocument
	 * @return a structured model for the given document, or null if there is
	 *         insufficient information known about the document instance to
	 *         do so
	 */
	public IStructuredModel getModelForEdit(IStructuredDocument textFileBufferDocument);

	/**
	 * Returns a structured model for the given contents using the given ID.
	 * If one does not already exist, one will be created with an edit count
	 * of 1. If one already exists, its edit count will be incremented before
	 * it is returned.
	 * 
	 * @param id
	 *            - the id for the model
	 * @param inStream
	 *            - the initial contents of the model
	 * @param resolver
	 *            - the URIResolver to use for locating any needed resources
	 * @return a structured model for the given content, or null if one could
	 *         not be found or created
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 *             if the contents can not be read or its detected encoding
	 *             does not support its contents
	 * @deprecated - a URI resolver should be automatically created when
	 *             needed
	 */
	public IStructuredModel getModelForEdit(String id, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException;

	/**
	 * Returns a structured model for the given file. If one does not already
	 * exists, one will be created with a read count of 1. If one already
	 * exists, its read count will be incremented before it is returned.
	 * 
	 * @param iFile
	 * @return a structured model for the given file, or null if one could not
	 *         be found or created
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	public IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException;

	/**
	 * @deprecated - encoding is handled automatically based on the file's
	 *             contents or user preferences
	 */
	public IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * @deprecated - Encoding and the line delimiter used are handled
	 *             automatically based on the file's contents or user
	 *             preferences.
	 */
	public IStructuredModel getModelForRead(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException;

	/**
	 * Returns a structured model for the given document. If one does not
	 * already exists, one will be created with a read count of 1. If one
	 * already exists, its read count will be incremented before it is
	 * returned. This method is intended only to interact with documents
	 * contained within File Buffers.
	 * 
	 * @param textFileBufferDocument
	 * @return a structured model for the given document, or null if there is
	 *         insufficient information known about the document instance to
	 *         do so
	 */
	public IStructuredModel getModelForRead(IStructuredDocument textFileBufferDocument);

	/**
	 * Returns a structured model for the given contents using the given ID.
	 * If one does not already exist, one will be created with an read count
	 * of 1. If one already exists, its read count will be incremented before
	 * it is returned.
	 * 
	 * @param id
	 *            - the id for the model
	 * @param inStream
	 *            - the initial contents of the model
	 * @param resolver
	 *            - the URIResolver to use for locating any needed resources
	 * @return a structured model for the given content, or null if one could
	 *         not be found or created
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 *             if the contents can not be read or its detected encoding
	 *             does not support its contents
	 * @deprecated - a URI resolver should be automatically created when
	 *             needed
	 */
	public IStructuredModel getModelForRead(String filename, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException;

	/**
	 * This method will not create a new model if it already exists ... if
	 * force is false. The idea is that a client should call this method once
	 * with force set to false. If the exception is thrown, then prompt client
	 * if they want to overwrite.
	 * 
	 * @param iFile
	 * @param force
	 * @return the new structured model, or 
	 * @throws ResourceInUse if the given new ID is already in use by a managed model
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 * @throws ResourceAlreadyExists if the give file already exists
	 */
	IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This method will not create a new model if it already exists ... if
	 * force is false. The idea is that a client should call this method once
	 * with force set to false. If the exception is thrown, then prompt client
	 * if they want to overwrite.
	 * 
	 * @param iFile
	 * @param force
	 * @return the new structured model, or 
	 * @throws ResourceInUse if the given new ID is already in use by a managed model
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 * @throws ResourceAlreadyExists if the give file already exists
	 */
	IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException;

	/**
	 * This function returns the combined "read" and "edit" reference counts
	 * of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 * @deprecated - internal information that can be obtained from the model
	 *             itself
	 */
	int getReferenceCount(Object id);

	/**
	 * This function returns the "edit" reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 * @deprecated - internal information that can be obtained from the model itself
	 */
	int getReferenceCountForEdit(Object id);

	/**
	 * This function returns the "read" reference count of underlying model.
	 * 
	 * @param id
	 *            Object The id of the model TODO: try to refine the design
	 *            not to use this function
	 * @deprecated - internal information that can be obtained from the model itself
	 */
	int getReferenceCountForRead(Object id);

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	boolean isShared(Object id);

	/**
	 * This function returns true if there are other "edit" references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	boolean isSharedForEdit(Object id);

	/**
	 * This function returns true if there are other "read" references to the
	 * underlying model.
	 * 
	 * @param id
	 *            Object The id of the model
	 */
	boolean isSharedForRead(Object id);

	/**
	 * @deprecated - not granular enough
	 * 
	 * This method can be called to determine if the model manager is within a
	 * "aboutToChange" and "changed" sequence.
	 */
	public boolean isStateChanging();

	/**
	 * This method changes the id of the model. 
	 * 
	 * TODO: try to refine the design
	 * not to use this function
	 * 
	 * @deprecated
	 */
	void moveModel(Object oldId, Object newId);

	/**
	 * This method can be called when the content type of a model changes. It's
	 * assumed the contentType has already been changed, and this method uses
	 * the text of the old one, to repopulate the text of the new one. In
	 * theory, the actual instance could change, (e.g. using 'saveAs' to go
	 * from xml to dtd), but in practice, the intent of this API is to return
	 * the same instance, just using different handlers, adapter factories,
	 * etc.
	 */
	IStructuredModel reinitialize(IStructuredModel model) throws IOException;

	/**
	 * This is similar to the getModel method, except this method does not use
	 * the cached version, but forces the cached version to be replaced with a
	 * fresh, unchanged version. Note: this method does not change any
	 * reference counts. Also, if there is not already a cached version of the
	 * model, then this call is essentially ignored (that is, it does not put
	 * a model in the cache) and returns null.
	 * 
	 * @deprecated
	 */
	IStructuredModel reloadModel(Object id, InputStream inStream) throws UnsupportedEncodingException;

	/**
	 * Saves the contents of the given structured document to the given file. If
	 * the document belongs to a managed model, that model will be saved and
	 * marked as non-dirty.
	 * 
	 * @param structuredDocument
	 *            - the structured document
	 * @param iFile
	 *            - the file to save to
	 * @throws UnsupportedEncodingException
	 * @throws CoreException if the file's contents or description can not be read
	 * @throws IOException if the file's contents can not be read or its detected encoding does not support its contents
	 */
	void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException;
}
