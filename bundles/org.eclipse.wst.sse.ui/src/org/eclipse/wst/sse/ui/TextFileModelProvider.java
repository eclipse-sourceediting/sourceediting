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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.extensions.breakpoint.IExtendedStorageEditorInput;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;


/**
 * A TextFileDocumentProvider that is IStructuredModel aware. This
 * implementation is marked as final since it will change significantly in C4,
 * possibly even be removed.
 */
public final class TextFileModelProvider extends TextFileDocumentProvider implements IModelProvider {

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

	private static TextFileModelProvider fInstance = null;
	private static IModelManager fModelManager;

	public synchronized static TextFileModelProvider getInstance() {
		if (fInstance == null)
			fInstance = new TextFileModelProvider();
		return fInstance;
	}

	/**
	 * Utility method also used in subclasses
	 */
	protected static IModelManager getModelManager() {
		if (fModelManager == null) {
			// get the model manager from the plugin
			// note: we can use the static "ID" variable, since we pre-req
			// that plugin
			IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
			fModelManager = plugin.getModelManager();
		}
		return fModelManager;
	}

	protected IElementStateListener fInternalListener;
	/** IStructuredModel information of all connected elements */
	private Map fModelInfoMap = new HashMap();

	protected TextFileModelProvider() {
		super();
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

	protected String computePath(IEditorInput input) {
		/**
		 * Typically CVS will return a path of "filename.ext" and the input's
		 * name will be "filename.ext version". The path must be used to load
		 * the model so that the suffix will be available to compute the
		 * contentType properly. The editor input name can then be set as the
		 * base location for display on the editor title bar.
		 */
		String path = null;
		boolean addHash = false;
		try {
			if (input instanceof IStorageEditorInput) {
				IStorage storage = ((IStorageEditorInput) input).getStorage();
				if (storage != null) {
					IPath storagePath = storage.getFullPath();
					String name = storage.getName();
					// if either the name or storage path are null or they are
					// identical, add a hash to it to guarantee uniqueness
					addHash = storagePath == null || storagePath.toString().equals(name);
					if (storagePath != null)
						path = storagePath.makeAbsolute().toString();
					if (path == null)
						path = name;
				}
			}
			else if (input.getAdapter(ILocationProvider.class) != null) {
				IPath locationPath = ((ILocationProvider) input.getAdapter(ILocationProvider.class)).getPath(input);
				if (locationPath != null) {
					path = locationPath.toString();
				}
			}

		}
		catch (CoreException e) {
			Logger.logException(e);
			return null;
		}
		finally {
			if (path == null)
				path = ""; //$NON-NLS-1$
		}
		if (addHash)
			path = input.hashCode() + path;
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#commitFileBuffer(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.ui.editors.text.TextFileDocumentProvider.FileInfo,
	 *      boolean)
	 */
	protected void commitFileBuffer(IProgressMonitor monitor, FileInfo info, boolean overwrite) throws CoreException {
		IStructuredModel model = getModel(info.fElement);
		if (model != null) {
			String contents = model.getStructuredDocument().get();
			info.fTextFileBuffer.getDocument().set(contents);
		}
		super.commitFileBuffer(monitor, info, overwrite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createAnnotationModel(org.eclipse.core.resources.IFile)
	 */
	protected IAnnotationModel createAnnotationModel(IFile file) {
		String id = file.getFullPath().toString();
		IAnnotationModel model = new StructuredResourceMarkerAnnotationModel(file, id);
		IEditorInput input = null;
		Iterator i = getConnectedElementsIterator();
		while (input == null && i.hasNext()) {
			FileInfo info = (FileInfo) i.next();
			if (getSystemFile(info).equals(file)) {
				input = (IEditorInput) info.fElement;
			}
		}
		ModelInfo modelInfo = getModelInfoFor(input);
		modelInfo.fAnnotationModel = model;
		return model;
	}

	public IDocument getDocument(Object element) {
		return getModel(element).getStructuredDocument();
	}

	/**
	 * Also create ModelInfo - extra resource synchronization classes should
	 * be stored within the ModelInfo
	 */
	protected FileInfo createFileInfo(Object element) throws CoreException {
		FileInfo info = super.createFileInfo(element);
		// create the corresponding ModelInfo if necessary
		if (getModelInfoFor((IEditorInput) element) == null) {
			createModelInfo((IEditorInput) element);
		}

		return info;
	}

	public void createModelInfo(IEditorInput input) {
		if (getModelInfoFor(input) == null) {
			IStructuredModel structuredModel = selfCreateModel(input);
			createModelInfo(input, structuredModel, true);
		}
	}

	/**
	 * To be used when model is provided to us, ensures that when setInput is
	 * used on this input, the given model will be used.
	 */
	public void createModelInfo(IEditorInput input, IStructuredModel structuredModel, boolean releaseModelOnDisconnect) {
		// we have to make sure factories are added, whether we created or
		// not.
		if (getModelInfoFor(input) != null || getModelInfoFor(structuredModel) != null)
			return;

		if (input instanceof IExtendedStorageEditorInput) {
			((IExtendedStorageEditorInput) input).addElementStateListener(fInternalListener);
		}

		addProviderFactories(structuredModel);

		ModelInfo modelInfo = new ModelInfo(structuredModel, input, null, releaseModelOnDisconnect);
		// to help with downstream usage, create a dummy/"null" annotation model
		if (!(input instanceof IFileEditorInput)) {
			modelInfo.fAnnotationModel = new AnnotationModel();
		}
		fModelInfoMap.put(input, modelInfo);
	}

	protected void disposeFileInfo(Object element, FileInfo info) {
		if (element instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) element;
			ModelInfo modelInfo = getModelInfoFor(input);
			disposeModelInfo(modelInfo);
		}
		super.disposeFileInfo(element, info);
	}

	/**
	 * disconnect from this model info
	 * 
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
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		new FileDocumentProvider().saveDocument(monitor, element, document, overwrite);
	}

	/**
	 * Overridden to use ModelInfo's annotation model
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getAnnotationModel(java.lang.Object)
	 */
	public IAnnotationModel getAnnotationModel(Object element) {
		// override behavior an retrieve the annotation model from the model
		// info
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

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @param input
	 * @return IStructuredModel
	 */
	public IStructuredModel loadModel(IEditorInput input) {
		return loadModel(input, false);
	}

	/**
	 * Method loadModel.
	 * 
	 * @param input
	 * @param logExceptions
	 * @return IStructuredModel
	 */
	public IStructuredModel loadModel(IEditorInput input, boolean logExceptions) {
		InputStream contents = null;
		IStructuredModel model = null;
		File file = getSystemFile(input);
		if (file != null) {
			String path = file.getAbsolutePath();
			try {
				contents = new FileInputStream(file);
				// first parameter must be unique
				model = getModelManager().getModelForEdit(path, contents, null);
				model.setBaseLocation(path);
			}
			catch (IOException e) {
				if (logExceptions)
					Logger.logException(ResourceHandler.getString("32concat_EXC_", new Object[]{input}), e); //$NON-NLS-1$
			}
			try {
				contents.close();
			}
			catch (Throwable e1) {
				// do nothing, this is just to ensure the resource isn't held
				// open
			}
		}
		return model;
	}

	/**
	 * @param input
	 * @return
	 */
	private File getSystemFile(IEditorInput input) {
		File file = null;
		ILocationProvider provider = (ILocationProvider) input.getAdapter(ILocationProvider.class);
		if (provider != null) {
			IPath fullPath = provider.getPath(input);
			if (fullPath != null) {
				file = fullPath.toFile();
			}
		}
		return file;
	}

	/**
	 * @param input
	 * @return
	 */
	protected IStructuredModel selfCreateModel(IEditorInput input) {
		return loadModel(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#resetDocument(java.lang.Object)
	 */
	public void resetDocument(Object element) throws CoreException {
		super.resetDocument(element);
		FileInfo info = getFileInfo(element);
		File file = getSystemFile(info);
		if (file != null && file.exists()) {
			IStructuredModel model = getModel(element);
			InputStream fis = null;
			try {
				fis = new FileInputStream(file);
			}
			catch (FileNotFoundException e) {
				// possibly nothing if the file was deleted, shouldn't happen
				// otherwise
			}
			if (fis != null) {
				String oldContents = model.getStructuredDocument().get();
				try {
					model.reload(fis);
					info.fTextFileBuffer.getDocument().set(model.getStructuredDocument().get());
				}
				catch (IOException e1) {
					Logger.logException("Exception caught reloading model from " + file.getName(), e1); //$NON-NLS-1$
					model.getStructuredDocument().set(oldContents);
				}
			}
		}
	}
}
