/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.emf2xml;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMAdapter;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRenderer;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.ResourceSetWorkbenchEditSynchronizer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;


public class EMF2DOMSSERenderer extends EMF2DOMRenderer implements IModelStateListener, IModelLifecycleListener {

	protected Object aboutToChangeNode = null;

	protected boolean isBatchChanges = false;

	private boolean isSaving = false;

	private IModelManager modelManager;
	
	private Class resourceClass;
	
	private boolean reverting = false;

	/** The XML DOM model */
	protected IDOMModel xmlModel;

	/** Used internally; the unique id for the xml model */
	protected String xmlModelId;

	protected boolean xmlModelReverted = false;

	public EMF2DOMSSERenderer() {
		super();
	}

	public void accessForRead() {
		if (!resource.isNew()) {
			String id = getModelManagerId();
			getModelManager().getExistingModelForRead(id);
		}
	}

	public void accessForWrite() {
		modelAccessForWrite();
	}

	/**
	 * see batchModelEnd
	 * 
	 */

	public void batchModeEnd() {
		getXMLModel().changedModel();
		setRootNodeAdapterNotificationEnabled(true);
		isBatchChanges = false;
	}

	/**
	 * batchModeStart and batchModeEnd is a pair that controls notifications,
	 * and tread access. They should always be called in a try/finally block.
	 * 
	 * setBatchModel begins the processing where notifications are not sent
	 * out on each change, but saved up until the endBatchMode called.
	 * 
	 * This pair of calls can also, indirectly, "lock" the DOM Model to access
	 * from only one thread, so it should not be locked for long periods of
	 * time. That's also why it is important to have the endBatchMode in a
	 * finally block to be sure it is always called, or the DOM will be left
	 * in a locked, unusable, state and only shortly away from severere
	 * program error.
	 * 
	 * <pre><code>
	 * Example
	 * </code>
	 *  
	 *  try { 
	 *  		batchModelStart();
	 *  		...do a some work ...
	 *  		}
	 *  	finally {
	 *  		endBatchMode();
	 *  		}
	 *  
	 *  
	 * </pre>
	 */
	public void batchModeStart() {
		isBatchChanges = true;
		getXMLModel().aboutToChangeModel();
		setRootNodeAdapterNotificationEnabled(false);
	}

	private void cacheSynchronizationStamp() {
		IFile file = WorkbenchResourceHelper.getFile(resource);
		if (file != null) {
			if (xmlModel != null)
				xmlModel.resetSynchronizationStamp(file);
		}
	}

	/**
	 * Create a new Document given
	 * 
	 * @aResource.
	 */
	protected void createDocument() {
		TranslatorResource res = getResource();
		res.setDefaults();
		IFile file = WorkbenchResourceHelper.getFile(resource);
		InputStream is = DOMUtilities.createHeaderInputStream(res.getDoctype(), res.getPublicId(), res.getSystemId());
		if (is == null)
			return;
		try {
			try {
				List folders = new ArrayList();
				IContainer container = file.getParent();
				while (null != container && !container.exists() && container instanceof IFolder) {
					folders.add(container);
					container = container.getParent();
				}
				IFolder folder = null;
				for (int i = 0; i < folders.size(); i++) {
					folder = (IFolder) folders.get(i);
					folder.create(true, true, null);
				}
				file.create(is, true, null);
				file.setLocal(true, 1, null);
			}
			catch (CoreException e1) {
				Logger.logException(e1);
			}
			finally {
				if (null != is) {
					is.close();
				}
			}
			initializeXMLModel(file, resource.getWriteCount() != 0);
		}
		catch (IOException ex) {
			Logger.log(Logger.ERROR, "IWAE0017E Unexpected IO exception occurred creating xml document");//$NON-NLS-1$
		}
	}

	protected void createDOMTreeIfNecessary() {
		if (needsToCreateDOM)
			createDOMTree();
	}

	protected EMF2DOMAdapter createRootDOMAdapter() {
		return new EMF2DOMSSEAdapter(getResource(), document, this, getResource().getRootTranslator());
	}

	public void deRegisterAsModelLifecycleListener() {
		if (xmlModel != null)
			xmlModel.removeModelLifecycleListener(this);
	}

	public void deRegisterAsModelStateListener() {
		if (xmlModel != null)
			xmlModel.removeModelStateListener(this);
	}

	private void deregisterFromXMLModel() {
		
		deRegisterAsModelStateListener();
		deRegisterAsModelLifecycleListener();
		// This try/catch block is a hack to fix defect 204114. This occurs
		// because
		// the model manager plugin is shut down and unloaded before the j2ee
		// plugin.
		// Calling getModelManager() can result in a class cast exception that
		// should
		// be ignored.
		// ModelManager mgr = null;
		try {
			getModelManager();
		}
		catch (ClassCastException exc) {
			return;
		}
		if (xmlModel != null) {
			int writeCount = resource.getWriteCount();
			int readCount = resource.getReadCount();
			for (int i = 0; i < writeCount; i++)
				xmlModel.releaseFromEdit();
			for (int ii = 0; ii < readCount; ii++)
				xmlModel.releaseFromRead();
		}
		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getAdapter(resource.eAdapters(), EMF2DOMAdapter.ADAPTER_CLASS);
		if (adapter != null) {
			adapter.removeAdapters(adapter.getNode());
		}
		xmlModel = null;
		xmlModelId = null;
	}

	public void doSave(OutputStream outputStream, Map options) throws IOException {

		try {
			isSaving = true;
			if (null != outputStream) {
				throw new RuntimeException(this.getClass().getName() + " should not use an OutputStream for saving");} //$NON-NLS-1$
			createDOMTreeIfNecessary();
			ResourceSetWorkbenchEditSynchronizer synchronizer = (ResourceSetWorkbenchEditSynchronizer) ((ProjectResourceSet) resource.getResourceSet()).getSynchronizer();
			IFile aFile = WorkbenchResourceHelper.getFile(resource);
			try {
				synchronizer.preSave(aFile, resource);
				xmlModel.save(aFile);
			}
			catch (CoreException ex) {
				synchronizer.removeFromRecentlySavedList(aFile);
				Logger.logException(ex);
			}
			cacheSynchronizationStamp();
		}
		finally {
			isSaving = false;
		}
	}

	public EMF2DOMAdapter getExistingDOMAdapter(Node node) {
		//IDOMNode xNode = (IDOMNode) node;
		//return (EMF2DOMSSEAdapter) xNode.getAdapterFor(EMF2DOMAdapter.ADAPTER_CLASS);

		INodeNotifier sseNode = (INodeNotifier) node;
		Collection adapters = sseNode.getAdapters();
		for (Iterator iterator = adapters.iterator(); iterator.hasNext();) {
			INodeAdapter adapter = (INodeAdapter) iterator.next();
			// First Check if it's an EMF2DOMAdapter
			if (adapter != null && adapter.isAdapterForType(EMF2DOMAdapter.ADAPTER_CLASS)) {
				// Cast to EMF2DOMSSEAdapter
				EMF2DOMSSEAdapter e2DAdapter = (EMF2DOMSSEAdapter) adapter;
				//Handle the cases where either adapter's target is null 
				//Use the resourceClass to make sure the resource type is identical
				if (getResource() == null || e2DAdapter.getTarget() == null)
					if(resourceClass.equals(e2DAdapter.getResourceClass()))
						return e2DAdapter;
					else
						continue;
				
				// First check if targets are resources
				if (e2DAdapter.getTarget() instanceof Resource) {
					/*
					 * Now check if it's the right one (Multiple resources
					 * could be attached)
					 */
					if (e2DAdapter.getTarget() == getResource()) {
						return e2DAdapter;
					}
				} else {
					// Check if targets are EObjects with the same resources
					TranslatorResource myTarget = getResource();
					EObject adapterTarget = (EObject) e2DAdapter.getTarget();
					/*
					 * Now check if it's the right one (Multiple resources could
					 * be attached)
					 */
					if (adapterTarget.eResource() == myTarget || adapterTarget.eResource() == null) {
						return e2DAdapter;
					}
				}
			}
		}
		return null;
	
	}

	protected IModelManager getModelManager() {
		if (modelManager == null)
			modelManager = StructuredModelManager.getModelManager();
		return modelManager;
	}

	/**
	 * Return id used to key the XML resource in the XML ModelManager.
	 */
	protected String getModelManagerId() {
		if (xmlModelId == null) {
			IFile file = WorkbenchResourceHelper.getFile(getResource());
			if (file != null) {
				xmlModelId = getModelManager().calculateId(file);
			}
			else {
				xmlModelId = resource.getURI() + Long.toString(System.currentTimeMillis());
			}
		}
		return xmlModelId;
	}

	protected ResourceSet getResourceSet() {
		return resource == null ? null : resource.getResourceSet();
	}

	/**
	 * Return the DOM model for this resource.
	 */
	public IDOMModel getXMLModel() {
		return xmlModel;
	}

	public String getXMLModelId() {
		return xmlModelId;
	}	
	
	private IDOMModel initializeXMLModel(IFile file, boolean forWrite) throws UnsupportedEncodingException, IOException {
		if (file == null || !file.exists())
			throw new FileNotFoundException((file == null) ? "null" : file.getFullPath().toOSString()); //$NON-NLS-1$
		try {
			IModelManager manager = getModelManager();
			String id = manager.calculateId(file);			
			if (forWrite) {
				IDOMModel mod = (IDOMModel)manager.getExistingModelForEdit(id);
				if (mod == null)
					setXMLModel((IDOMModel) manager.getModelForEdit(file));
				else {
					setXMLModel(mod);
				}
			}
			else {
				IDOMModel mod = (IDOMModel)manager.getExistingModelForRead(id);
				if (mod == null)
					setXMLModel((IDOMModel) manager.getModelForRead(file));
				else {
					setXMLModel(mod);
				}
			}
			setXMLModelId(getXMLModel().getId());
			needsToCreateDOM = false;
		}
		catch (CoreException e) {
			Logger.logException(e);
			return null;
		}
		String id = getXMLModel().getId();
		syncReferenceCounts(id, forWrite);
		if (xmlModel != null)
			document = xmlModel.getDocument();
		return xmlModel;
	}

	public boolean isBatchMode() {
		return isBatchChanges;
	}

	public boolean isModified() {
		return (getXMLModel() != null && getXMLModel().isDirty());
	}

	public boolean isShared() {
		
		if( xmlModel == null) { //resource could be in process of being unloaded - check with model manager
			String id = getModelManagerId();
			return getModelManager().isShared(id);
		}
		if (getResourceSet() == null)
			return false;
		return xmlModel.isShared();
	}

	public boolean isSharedForWrite() {
		if (getResourceSet() == null || xmlModel == null)
			return false;
		return xmlModel.isSharedForEdit();
	}

	protected void loadDocument(InputStream in, Map options) throws IOException {
		if (null != in) {
			throw new RuntimeException(this.getClass().getName() + " should not use an InputStream for loading");} //$NON-NLS-1$
		IFile file = WorkbenchResourceHelper.getFile(resource);
		initializeXMLModel(file, (resource.getWriteCount() != 0));
		cacheSynchronizationStamp();
	}

	protected boolean managesDOMAdapters() {
		return false;
	}

	public void modelAboutToBeChanged(IStructuredModel model) {
		if (model.getStructuredDocument() != null)
			aboutToChangeNode = model.getStructuredDocument().getFirstStructuredDocumentRegion();
	}

	public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub

	}

	private void modelAccessForWrite() {
		String id = getModelManagerId();
		getModelManager().getExistingModelForEdit(id);
	}

	public void modelChanged(IStructuredModel model) {
		if (isBatchChanges)
			return;
		try {
			if (reverting && aboutToChangeNode != null && model.getStructuredDocument() != null
					&& model.getStructuredDocument().getFirstStructuredDocumentRegion() != aboutToChangeNode) {
				String id = getModelManagerId();
				IStructuredModel tempModel = null;
				try {
					tempModel = getModelManager().getExistingModelForEdit(id);
					xmlModelReverted = true;
					resource.unload();
				} finally {
					if (tempModel != null && (tempModel.getReferenceCountForEdit() > 0)) {
						tempModel.releaseFromEdit();
					}
					reverting = false;
				}
			}
		} finally {
			aboutToChangeNode = null;
		}
	} 
	
	public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		if (!isDirty && resource.isModified()) { // The XMLModel was saved
			resource.setModified(false);
			long stamp = WorkbenchResourceHelper.computeModificationStamp(resource);
			WorkbenchResourceHelper.setSynhronizationStamp(resource, stamp);
			IProject proj = WorkbenchResourceHelper.getProject(resource);
			ResourceSetWorkbenchEditSynchronizer synchronizer = (ResourceSetWorkbenchEditSynchronizer) ((ProjectResourceSet) WorkbenchResourceHelperBase.getResourceSet(proj)).getSynchronizer();
			IFile aFile = WorkbenchResourceHelper.getFile(resource);
			synchronizer.preSave(aFile, resource);
		}
		if (isDirty)
			resource.setModified(true);
	}

	public void modelReinitialized(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub

	}

	public void modelResourceDeleted(IStructuredModel model) {
		// Do nothing
	}

	public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
		// Do nothing
	}

	/**
	 * This method is called just prior to being removed from the ResourceSet.
	 * Ensure that all reference counts to the XMLModel are removed.
	 */
	public void preDelete() {
		if (resource.isLoaded())
			deregisterFromXMLModel();
	}

	public void preUnload() {
		deregisterFromXMLModel();
	}

	public void processPostModelEvent(ModelLifecycleEvent event) {
		if (event.getType() == ModelLifecycleEvent.MODEL_REVERT)
			reverting = false;

	}

	public void processPreModelEvent(ModelLifecycleEvent event) {
		if (event.getType() == ModelLifecycleEvent.MODEL_REVERT)
			reverting = true;
			
		if (!isSaving) {
			if (event.getType() == ModelLifecycleEvent.MODEL_SAVED) {
				ResourceSetWorkbenchEditSynchronizer synchronizer = (ResourceSetWorkbenchEditSynchronizer) ((ProjectResourceSet) resource.getResourceSet()).getSynchronizer();
				IFile aFile = WorkbenchResourceHelper.getFile(resource);
				synchronizer.preSave(aFile, resource);
			}
		}
		
	}


	public void registerAsModelLifecycleListener() {
		this.xmlModel.addModelLifecycleListener(this);
	}

	/**
	 * Insert the method's description here. Creation date: (9/7/2001 10:49:53
	 * AM)
	 */
	public void registerAsModelStateListener() {
		this.xmlModel.addModelStateListener(this);
	}

	public void releaseFromRead() {
		if (xmlModel != null)
			xmlModel.releaseFromRead();
	}

	public void releaseFromWrite() {
		if (xmlModel != null)
			xmlModel.releaseFromEdit();
	}

	public void removeDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		((IDOMNode) aNode).removeAdapter((EMF2DOMSSEAdapter) anAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.internal.util.emf.xml.EMF2DOMRenderer#replaceDocumentType(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void replaceDocumentType(String docTypeName, String publicId, String systemId) {
		if (document == null)
			return;
		DocumentTypeImpl docType = (DocumentTypeImpl) document.getDoctype();
		if (docType == null)
			return;
		if (publicId == null && systemId == null)
			document.removeChild(docType);
		else {
			docType.setPublicId(publicId);
			docType.setSystemId(systemId);
		}
	}

	/**
	 * @deprecated use batchModeStart and BatchModeEnd instead even if you do
	 *             not use batchModelStart/End, you still need to use the
	 *             try/finally pattern documented there.
	 */

	public void setBatchMode(boolean isBatch) {

		// This is some extra processing for clients to know they may be using
		// incorrectly
		if (isBatch) {
			if (isBatchChanges) {
				Logger.log(Logger.INFO_DEBUG, "setBatch was set to true when it was already true. This can be an indication of invalid calling order"); //$NON-NLS-1$
			}
		}


		if (isBatch) {
			batchModeStart();
		}
		else {
			batchModeEnd();
		}
	}

	private void setRootNodeAdapterNotificationEnabled(boolean b) {
		EObject root = resource.getRootObject();
		if (root != null) {
			EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(root, EMF2DOMAdapter.ADAPTER_CLASS);
			if (adapter != null) {
				adapter.setNotificationEnabled(b);
				if (b)
					adapter.updateDOM();
			}
		}
	}

	/**
	 * Return the DOM model for this resource.
	 */
	public void setXMLModel(IDOMModel xmlModel) {
		deRegisterAsModelStateListener();
		deRegisterAsModelLifecycleListener();
		this.xmlModel = xmlModel;
		registerAsModelStateListener();
		registerAsModelLifecycleListener();
	}

	public void setXMLModelId(String id) {
		xmlModelId = id;
	}

	private void syncReferenceCounts(String id, boolean forWrite) {
		int editIndex = 0, readIndex = 0;
		if (forWrite)
			editIndex++;
		else
			readIndex++;
		int writeCount = resource.getWriteCount();
		int readCount = resource.getReadCount();
		for (int i = writeCount; i > editIndex; i--)
			modelManager.getExistingModelForEdit(id);
		for (int i = readCount; i > readIndex; i--)
			modelManager.getExistingModelForRead(id);
	}

	public boolean useStreamsForIO() {
		return false;
	}

	public boolean wasReverted() {
		return xmlModelReverted;
	}

	public void setResource(TranslatorResource resource) {
		super.setResource(resource);
		if (resource != null)
			resourceClass = resource.getClass();
	}
	public boolean isReverting() {
		return this.reverting;
	}


}
