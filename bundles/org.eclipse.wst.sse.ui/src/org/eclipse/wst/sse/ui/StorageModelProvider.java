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
package org.eclipse.wst.sse.ui;



import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.StorageDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.extensions.breakpoint.IExtendedStorageEditorInput;
import org.eclipse.wst.sse.ui.internal.debug.BreakpointRulerAction;
import org.eclipse.wst.sse.ui.internal.extension.BreakpointProviderBuilder;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;


/**
 * A StorageDocumentProvider that is IStructuredModel aware
 */
public class StorageModelProvider extends StorageDocumentProvider implements IModelProvider {

	protected class InternalElementStateListener implements IElementStateListener {
		public void elementContentAboutToBeReplaced(Object element) {
			// we just forward the event
			StorageModelProvider.this.fireElementContentAboutToBeReplaced(element);
		}

		public void elementContentReplaced(Object element) {
			// Force a reload of the markers into annotations since their previous Positions
			// have been deleted.  Disconnecting and reconnecting forces a call to the
			// private catchupWithMarkers method.
			StorageInfo info = (StorageInfo) getElementInfo(element);

			if (info != null && info.fModel != null) {
				info.fModel.disconnect(info.fDocument);
			}

			// we just forward the event
			StorageModelProvider.this.fireElementContentReplaced(element);

			if (info != null && info.fModel != null) {
				info.fModel.connect(info.fDocument);
			}
		}

		public void elementDeleted(Object element) {
			// we just forward the event
			StorageModelProvider.this.fireElementDeleted(element);
		}

		public void elementDirtyStateChanged(Object element, boolean isDirty) {
			// we just forward the event
			StorageModelProvider.this.fireElementDirtyStateChanged(element, isDirty);
		}

		public void elementMoved(Object originalElement, Object movedElement) {
			// we just forward the event
			StorageModelProvider.this.fireElementMoved(originalElement, movedElement);
		}
	}

	/**
	 * Collection of info that goes with a model. 
	 */
	protected class ModelInfo {
		public IAnnotationModel fAnnotationModel;
		public IEditorInput fElement;
		public boolean fShouldReleaseOnInfoDispose;
		public IStructuredModel fStructuredModel;

		public ModelInfo(IStructuredModel structuredModel, IEditorInput element, IAnnotationModel model, boolean selfCreated) {
			fElement = element;
			fStructuredModel = structuredModel;
			fAnnotationModel = model;
			fShouldReleaseOnInfoDispose = selfCreated;
		}
	}

	private static StorageModelProvider fInstance = null;
	private static IModelManager fModelManager;

	public synchronized static StorageModelProvider getInstance() {
		if (fInstance == null)
			fInstance = new StorageModelProvider();
		return fInstance;
	}

	/**
	 * Utility method also used in subclasses
	 */
	protected static IModelManager getModelManager() {
		if (fModelManager == null) {
			// get the model manager from the plugin
			// note: we can use the static "ID" variable, since we pre-req that plugin
			IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
			fModelManager = plugin.getModelManager();
		}
		return fModelManager;
	}

	protected IElementStateListener fInternalListener;
	/** IStructuredModel information of all connected elements */
	private Map fModelInfoMap = new HashMap();

	protected StorageModelProvider() {
		super();
		fInternalListener = new InternalElementStateListener();
	}

	public void addProviderFactories(IStructuredModel structuredModel) {
		// (mostly) COPIED FROM FileModelProvider
		EditorPlugin plugin = ((EditorPlugin) Platform.getPlugin(EditorPlugin.ID));
		AdapterFactoryRegistry adapterRegistry = plugin.getAdapterFactoryRegistry();
		Iterator adapterFactoryList = adapterRegistry.getAdapterFactories();

		IFactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		if (factoryRegistry == null) {
			factoryRegistry = new FactoryRegistry();
			structuredModel.setFactoryRegistry(factoryRegistry);
		}

		while (adapterFactoryList.hasNext()) {
			try {
				AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterFactoryList.next();
				if (provider.isFor(structuredModel.getModelHandler())) {
					provider.addAdapterFactories(structuredModel);
				}
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
		// END COPY FileModelProvider
	}

	protected String computePath(IStorageEditorInput input) {
		/**
		 * Typically CVS will return a path of "filename.ext" and the input's
		 * name will be "filename.ext version".  The path must be used to load
		 * the model so that the suffix will be available to compute the
		 * contentType properly.  The editor input name can then be set as the
		 * base location for display on the editor title bar.
		 * 
		 */
		String path = null;
		boolean addHash = false;
		try {
			IStorage storage = input.getStorage();
			if (storage != null) {
				IPath storagePath = storage.getFullPath();
				String name = storage.getName();
				// if either the name or storage path are null or they are identical, add a hash to it to guarantee uniqueness
				addHash = storagePath == null || storagePath.toString().equals(name);
				if (storagePath != null)
					path = storagePath.makeAbsolute().toString();
				if (path == null)
					path = name;
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (path == null)
				path = ""; //$NON-NLS-1$
		}
		if (addHash)
			path = input.hashCode() + path;
		return path;
	}

	//	public boolean canSaveDocument(Object element) {
	//		return false;
	//	}

	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (element instanceof IStorageEditorInput) {
			IStorageEditorInput input = (IStorageEditorInput) element;
			String contentType = (getModel(input) != null ? getModel(input).getContentTypeIdentifier() : null);
			String ext = BreakpointRulerAction.getFileExtension((IEditorInput) element);
			IResource res = BreakpointProviderBuilder.getInstance().getResource(input, contentType, ext);
			String id = input.getName();
			if (input.getStorage() != null)
				id = input.getStorage().getFullPath().toString();
			// we can only create a resource marker annotationmodel off of a valid resource
			if (res != null)
				return new StructuredResourceMarkerAnnotationModel(res, id);
			else
				return new AnnotationModel();
		}
		return super.createAnnotationModel(element);
	}

	protected IDocument createDocument(Object element) {
		return getModel(element).getStructuredDocument();
	}

	/**
	 * Also create ModelInfo - extra resource synchronization classes should
	 * be stored within the ModelInfo 
	 */
	protected ElementInfo createElementInfo(Object element) throws CoreException {
		// create the corresponding ModelInfo if necessary
		if (getModelInfoFor((IEditorInput) element) == null) {
			createModelInfo((IEditorInput) element);
		}

		ElementInfo info = super.createElementInfo(element);
		return info;
	}

	public void createModelInfo(IEditorInput input) {
		if (getModelInfoFor(input) == null) {
			IStructuredModel structuredModel = selfCreateModel(input);
			createModelInfo(input, structuredModel, true);
		}
	}

	/**
	 * To be used when model is provided to us, ensures that when setInput is used on this input, the
	 * given model will be used.
	 */
	public void createModelInfo(IEditorInput input, IStructuredModel structuredModel, boolean releaseModelOnDisconnect) {
		// we have to make sure factories are added, whether we created or not.
		if (getModelInfoFor(input) != null || getModelInfoFor(structuredModel) != null)
			return;

		if (input instanceof IExtendedStorageEditorInput) {
			((IExtendedStorageEditorInput) input).addElementStateListener(fInternalListener);
		}

		addProviderFactories(structuredModel);

		ModelInfo modelInfo = new ModelInfo(structuredModel, input, null, releaseModelOnDisconnect);
		fModelInfoMap.put(input, modelInfo);

		try {
			modelInfo.fAnnotationModel = createAnnotationModel(input);
		}
		catch (CoreException e) {
			// just continue without one
		}
	}

	protected void disposeElementInfo(Object element, ElementInfo info) {
		if (element instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) element;
			ModelInfo modelInfo = getModelInfoFor(input);
			disposeModelInfo(modelInfo);
		}
		super.disposeElementInfo(element, info);
	}

	/**
	 * disconnect from this model info 
	 * @param info
	 */
	public void disposeModelInfo(ModelInfo info) {
		if (info.fElement instanceof IStorageEditorInput) {
			if (info.fElement instanceof IExtendedStorageEditorInput) {
				((IExtendedStorageEditorInput) info.fElement).removeElementStateListener(fInternalListener);
			}
			if (info.fShouldReleaseOnInfoDispose) {
				info.fStructuredModel.releaseFromEdit();
			}
		}
		fModelInfoMap.remove(info.fElement);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		new FileDocumentProvider().saveDocument(monitor, element, document, overwrite);
	}

	/**
	 * Overridden to use ModelInfo's annotation model
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getAnnotationModel(java.lang.Object)
	 */
	public IAnnotationModel getAnnotationModel(Object element) {
		// override behavior an retrieve the annotation model from the model info
		ModelInfo info = getModelInfoFor((IEditorInput) element);
		if (info != null)
			return info.fAnnotationModel;
		return null;
	}

	protected IEditorInput getInputFor(IDocument document) {
		IStructuredModel model = getModelManager().getExistingModelForRead(document);
		IEditorInput input = getInputFor(model);
		model.releaseFromRead();
		return input;
	}

	protected IEditorInput getInputFor(IStructuredModel structuredModel) {
		IEditorInput result = null;
		ModelInfo info = getModelInfoFor(structuredModel);
		if (info != null)
			result = info.fElement;
		return result;
	}

	public IStructuredModel getModel(IEditorInput element) {
		IStructuredModel result = null;
		ModelInfo info = getModelInfoFor(element);
		if (info != null) {
			result = info.fStructuredModel;
		}
		return result;
	}

	/* (non-Javadoc)
	 */
	public IStructuredModel getModel(Object element) {
		if (element instanceof IEditorInput)
			return getModel((IEditorInput) element);
		return null;
	}

	protected ModelInfo getModelInfoFor(IEditorInput element) {
		ModelInfo result = (ModelInfo) fModelInfoMap.get(element);
		return result;
	}

	protected ModelInfo getModelInfoFor(IStructuredModel structuredModel) {
		ModelInfo result = null;
		if (structuredModel != null) {
			ModelInfo[] modelInfos = (ModelInfo[]) fModelInfoMap.values().toArray(new ModelInfo[0]);
			for (int i = 0; i < modelInfos.length; i++) {
				ModelInfo info = modelInfos[i];
				if (structuredModel.equals(info.fStructuredModel)) {
					result = info;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Method loadModel.
	 * @param input
	 * @return IStructuredModel
	 */
	public IStructuredModel loadModel(IStorageEditorInput input) {
		return loadModel(input, false);
	}

	/**
	 * Method loadModel.
	 * @param input
	 * @param logExceptions
	 * @return IStructuredModel
	 */
	public IStructuredModel loadModel(IStorageEditorInput input, boolean logExceptions) {
		InputStream contents = null;
		try {
			contents = input.getStorage().getContents();
		}
		catch (CoreException noStorageExc) {
			if (logExceptions)
				Logger.logException(ResourceHandler.getString("32concat_EXC_", new Object[]{input.getName()}), noStorageExc); //$NON-NLS-1$
		}

		IStructuredModel model = null;
		String path = computePath(input);
		try {
			// first parameter must be unique
			model = getModelManager().getModelForEdit(path, contents, null);
			model.setBaseLocation(input.getName());
		}
		catch (IOException e) {
			if (logExceptions)
				Logger.logException(ResourceHandler.getString("32concat_EXC_", new Object[]{input}), e); //$NON-NLS-1$
		}
		return model;
	}

	/**
	 * @param input
	 * @return
	 */
	protected IStructuredModel selfCreateModel(IEditorInput input) {
		return loadModel((IStorageEditorInput) input);
	}

}
