/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.common.encoding.EncodingRule;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.URIResolver;

/**
 * Simple "null" implementation, entirely to more easily handle case where
 * model manager can't be retrieved due to workspace shutting down. Its intent
 * is to help minimize null checks (especially new null checks :)
 */
public class NullModelManager implements IModelManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#aboutToChangeModels()
	 */
	public void aboutToChangeModels() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createStructuredDocumentFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredDocument createStructuredDocumentFor(IFile iFile) throws IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createNewStructuredDocumentFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredDocument createNewStructuredDocumentFor(IFile iFile) throws ResourceAlreadyExists, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createStructuredDocumentFor(java.lang.String)
	 */
	public IStructuredDocument createStructuredDocumentFor(String contentTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.io.InputStream, com.ibm.sse.model.util.URIResolver)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.lang.String, com.ibm.sse.model.util.URIResolver)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, String content, URIResolver resolver) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createStructuredDocumentFor(java.lang.String,
	 *      java.io.InputStream, com.ibm.sse.model.util.URIResolver,
	 *      java.lang.String)
	 */
	public IStructuredDocument createStructuredDocumentFor(String filename, InputStream inputStream, URIResolver resolver, String ianaEncodingName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createUnManagedStructuredModelFor(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(IFile iFile) throws IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createUnManagedStructuredModelFor(java.lang.String)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(String contentTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createUnManagedStructuredModelFor(java.lang.String,
	 *      com.ibm.sse.model.util.URIResolver)
	 */
	public IStructuredModel createUnManagedStructuredModelFor(String contentTypeId, URIResolver resolver) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#addModelManagerListener(com.ibm.sse.model.IModelManagerListener)
	 */
	public void addModelManagerListener(IModelManagerListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#changedModels()
	 */
	public void changedModels() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#copyModelForEdit(java.lang.String,
	 *      java.lang.String)
	 */
	public IStructuredModel copyModelForEdit(String oldId, String newId) throws ResourceInUse {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForEdit(java.lang.Object)
	 */
	public IStructuredModel getExistingModelForEdit(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForEdit(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getExistingModelForEdit(IFile iFile) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForRead(java.lang.Object)
	 */
	public IStructuredModel getExistingModelForRead(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForRead(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getExistingModelForRead(IFile iFile) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelIds()
	 */
	public Enumeration getExistingModelIds() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getModelForEdit(IFile iFile) throws IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile,
	 *      java.lang.String, java.lang.String)
	 */
	public IStructuredModel getModelForEdit(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForEdit(org.eclipse.core.resources.IFile,
	 *      com.ibm.encoding.resource.EncodingRule)
	 */
	public IStructuredModel getModelForEdit(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForRead(org.eclipse.core.resources.IFile,
	 *      com.ibm.encoding.resource.EncodingRule)
	 */
	public IStructuredModel getModelForRead(IFile iFile, EncodingRule encodingRule) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForRead(org.eclipse.core.resources.IFile)
	 */
	public IStructuredModel getModelForRead(IFile iFile) throws IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForRead(org.eclipse.core.resources.IFile,
	 *      java.lang.String, java.lang.String)
	 */
	public IStructuredModel getModelForRead(IFile iFile, String encoding, String lineDelimiter) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForEdit(java.lang.String,
	 *      java.io.InputStream, com.ibm.sse.model.util.URIResolver)
	 */
	public IStructuredModel getModelForEdit(String filename, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getModelForRead(java.lang.String,
	 *      java.io.InputStream, com.ibm.sse.model.util.URIResolver)
	 */
	public IStructuredModel getModelForRead(String filename, InputStream inStream, URIResolver resolver) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getNewModelForEdit(org.eclipse.core.resources.IFile,
	 *      boolean)
	 */
	public IStructuredModel getNewModelForEdit(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getNewModelForRead(org.eclipse.core.resources.IFile,
	 *      boolean)
	 */
	public IStructuredModel getNewModelForRead(IFile iFile, boolean force) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getReferenceCount(java.lang.Object)
	 */
	public int getReferenceCount(Object id) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getReferenceCountForEdit(java.lang.Object)
	 */
	public int getReferenceCountForEdit(Object id) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getReferenceCountForRead(java.lang.Object)
	 */
	public int getReferenceCountForRead(Object id) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#isShared(java.lang.Object)
	 */
	public boolean isShared(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#isSharedForEdit(java.lang.Object)
	 */
	public boolean isSharedForEdit(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#isSharedForRead(java.lang.Object)
	 */
	public boolean isSharedForRead(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#isStateChanging()
	 */
	public boolean isStateChanging() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#moveModel(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void moveModel(Object oldId, Object newId) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#reloadModel(java.lang.Object,
	 *      java.io.InputStream)
	 */
	public IStructuredModel reloadModel(Object id, InputStream inStream) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#reinitialize(com.ibm.sse.model.IStructuredModel)
	 */
	public IStructuredModel reinitialize(IStructuredModel model) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#removeModelManagerListener(com.ibm.sse.model.IModelManagerListener)
	 */
	public void removeModelManagerListener(IModelManagerListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#saveStructuredDocument(com.ibm.sse.model.text.IStructuredDocument,
	 *      org.eclipse.core.resources.IFile)
	 */
	public void saveStructuredDocument(IStructuredDocument structuredDocument, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#calculateId(org.eclipse.core.resources.IFile)
	 */
	public String calculateId(IFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#calculateBaseLocation(org.eclipse.core.resources.IFile)
	 */
	public String calculateBaseLocation(IFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#createNewInstance(com.ibm.sse.model.IStructuredModel)
	 */
	public IStructuredModel createNewInstance(IStructuredModel model) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForRead(org.eclipse.jface.text.IDocument)
	 */
	public IStructuredModel getExistingModelForRead(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.IModelManager#getExistingModelForEdit(org.eclipse.jface.text.IDocument)
	 */
	public IStructuredModel getExistingModelForEdit(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.model.IModelManager#getModelForEdit(com.ibm.sse.model.text.IStructuredDocument)
	 */
	public IStructuredModel getModelForEdit(IStructuredDocument textFileBufferDocument) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.model.IModelManager#getModelForRead(com.ibm.sse.model.text.IStructuredDocument)
	 */
	public IStructuredModel getModelForRead(IStructuredDocument textFileBufferDocument) {
		// TODO Auto-generated method stub
		return null;
	}
}