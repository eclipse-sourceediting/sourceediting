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
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.URIResolver;


/**
 * Simple "null" implementation, entirely to more easily handle case where
 * model manager can't be retrieved due to workspace shutting down. Its intent
 * is to help minimize null checks (especially new null checks :)
 */
public class NullModelManager extends ModelManagerImpl implements IModelManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#aboutToChangeModels()
	 */
	public void aboutToChangeModels() {


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#addModelManagerListener(org.eclipse.wst.sse.core.IModelManagerListener)
	 */
	public void addModelManagerListener(IModelManagerListener listener) {


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#calculateBaseLocation(org.eclipse.core.resources.IFile)
	 */
	public String calculateBaseLocation(IFile file) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#calculateId(org.eclipse.core.resources.IFile)
	 */
	public String calculateId(IFile file) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#changedModels()
	 */
	public void changedModels() {


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#copyModelForEdit(java.lang.String,
	 *      java.lang.String)
	 */
	public IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createNewInstance(org.eclipse.wst.sse.core.IStructuredModel)
	 */
	public IStructuredModel createNewInstance(IStructuredModel model) throws IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createNewStructuredDocumentFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredDocument createNewStructuredDocumentFor(IFile iFile) throws ResourceAlreadyExists, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createStructuredDocumentFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createStructuredDocumentFor(java.lang.String)
	 */
	public IStructuredDocument createStructuredDocumentFor(String contentTypeId) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.io.InputStream, org.eclipse.wst.sse.core.util.URIResolver)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver) throws IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.io.InputStream, org.eclipse.wst.sse.core.util.URIResolver,
	 *      java.lang.String)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String ianaEncodingName) throws IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.lang.String, org.eclipse.wst.sse.core.util.URIResolver)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createUnManagedStructuredModelFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createUnManagedStructuredModelFor(java.lang.String)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(String contentTypeId) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#createUnManagedStructuredModelFor(java.lang.String,
	 *      org.eclipse.wst.sse.core.util.URIResolver)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForEdit(org.eclipse.jface.text.IDocument)
	 */
	public IStructuredModel getExistingModelForEdit(IDocument document) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForEdit(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getExistingModelForEdit(IFile iFile) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForEdit(java.lang.Object)
	 */
	public IStructuredModel getExistingModelForEdit(Object id) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForRead(org.eclipse.jface.text.IDocument)
	 */
	public IStructuredModel getExistingModelForRead(IDocument document) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForRead(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getExistingModelForRead(IFile iFile) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelForRead(java.lang.Object)
	 */
	public IStructuredModel getExistingModelForRead(Object id) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getExistingModelIds()
	 */
	public Enumeration getExistingModelIds() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile,
	 *      org.eclipse.wst.sse.core.internal.encoding.EncodingRule)
	 */
	public IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile,
	 *      java.lang.String, java.lang.String)
	 */
	public IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForEdit(java.lang.String,
	 *      java.io.InputStream, org.eclipse.wst.sse.core.util.URIResolver)
	 */
	public IStructuredModel getModelForEdit(String filename, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForRead(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForRead(org.eclipse.core.resources.IFile,
	 *      org.eclipse.wst.sse.core.internal.encoding.EncodingRule)
	 */
	public IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForRead(org.eclipse.core.resources.IFile,
	 *      java.lang.String, java.lang.String)
	 */
	public IStructuredModel getModelForRead(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getModelForRead(java.lang.String,
	 *      java.io.InputStream, org.eclipse.wst.sse.core.util.URIResolver)
	 */
	public IStructuredModel getModelForRead(String filename, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getNewModelForEdit(org.eclipse.core.resources.IFile,
	 *      boolean)
	 */
	public IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getNewModelForRead(org.eclipse.core.resources.IFile,
	 *      boolean)
	 */
	public IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getReferenceCount(java.lang.Object)
	 */
	public int getReferenceCount(Object id) {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getReferenceCountForEdit(java.lang.Object)
	 */
	public int getReferenceCountForEdit(Object id) {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#getReferenceCountForRead(java.lang.Object)
	 */
	public int getReferenceCountForRead(Object id) {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#isShared(java.lang.Object)
	 */
	public boolean isShared(Object id) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#isSharedForEdit(java.lang.Object)
	 */
	public boolean isSharedForEdit(Object id) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#isSharedForRead(java.lang.Object)
	 */
	public boolean isSharedForRead(Object id) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#isStateChanging()
	 */
	public boolean isStateChanging() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#moveModel(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void moveModel(Object oldId, Object newId) {


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#reinitialize(org.eclipse.wst.sse.core.IStructuredModel)
	 */
	public IStructuredModel reinitialize(IStructuredModel model) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#reloadModel(java.lang.Object,
	 *      java.io.InputStream)
	 */
	public IStructuredModel reloadModel(Object id, InputStream inStream) throws UnsupportedEncodingException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#removeModelManagerListener(org.eclipse.wst.sse.core.IModelManagerListener)
	 */
	public void removeModelManagerListener(IModelManagerListener listener) {


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IModelManager#saveStructuredDocument(org.eclipse.wst.sse.core.text.IStructuredDocument,
	 *      org.eclipse.core.resources.IFile)
	 */
	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {


	}

}
