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
package org.eclipse.wst.sse.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.wst.common.encoding.CodedReaderCreator;
import org.eclipse.wst.common.encoding.EncodingMemento;
import org.eclipse.wst.common.encoding.EncodingRule;
import org.eclipse.wst.common.encoding.exceptions.CharConversionErrorWithDetail;
import org.eclipse.wst.common.encoding.exceptions.MalformedInputExceptionWithDetail;
import org.eclipse.wst.common.encoding.exceptions.MalformedOutputExceptionWithDetail;
import org.eclipse.wst.common.encoding.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelLifecycleListener;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IModelStateListenerExtended;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * This version of an IDocumentProvider is the editor side counter part to
 * IModelManager. It is responsible for providing the structuredModel, which
 * is actually deferred to the IModelManager. But other, non-deffered
 * responsibilities include providing editing related models, such as
 * annotation models, undo command stack, etc.
 * 
 * @deprecated - no longer used
 */
public class FileModelProvider extends FileDocumentProvider implements IModelProvider {

	// Ensure that the ElementInfo metadata is correct for the model
	protected class InternalModelStateListener implements IModelStateListenerExtended {
		public void modelAboutToBeChanged(IStructuredModel model) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelAboutToBeChanged: " + model.getId());
			}
		}

		public void modelAboutToBeReinitialized(IStructuredModel model) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelAboutToBeReinitialized: " + model.getId());
			}
		}

		public void modelChanged(IStructuredModel model) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelChanged: " + model.getId());
			}
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelDirtyStateChanged:(" + isDirty + ") " + model.getId());
			}
			// synchronize the saveability with the dirty flag
			IFileEditorInput editorInput = (IFileEditorInput) getInputFor(model);
			if (editorInput != null) {
				FileInfo info = (FileInfo) getElementInfo(editorInput);
				if (info != null) {
					info.fCanBeSaved = isDirty;
					if (!isDirty) {
						addUnchangedElementListeners(editorInput, info);
						// This timestamp udpate is required until
						// ModelLifecycleEvent.MODEL_SAVED are received in the
						// ModelInfo
						info.fModificationStamp = computeModificationStamp(editorInput.getFile());
					}
					fireElementDirtyStateChanged(editorInput, isDirty);
				} else {
					throw new SourceEditingRuntimeException(new IllegalArgumentException("no corresponding element info found for " + model.getId()), "leftover model state listener in FileModelProvider"); //$NON-NLS-2$ //$NON-NLS-1$
				}
			}
			fireModelDirtyStateChanged(model, isDirty);
		}

		public void modelReinitialized(IStructuredModel model) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelReinitialized " + model.getId());
			}
			reinitializeFactories(model);
		}

		public void modelResourceDeleted(IStructuredModel model) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelResourceDeleted " + model.getId());
			}
			fireModelDeleted(model);
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
			if (debugModelStatelistener) {
				System.out.println("FileModelProvider: modelResourceMoved " + originalmodel.getBaseLocation() + " --> " + movedmodel.getBaseLocation());
			}
			fireModelMoved(originalmodel, movedmodel);
		}
	}

	/**
	 * Collection of info that goes with a model. Implements
	 * IModelLifecycleListener so that there's a separate instance per
	 * IEditorInput
	 */
	protected class ModelInfo implements IModelLifecycleListener {
		public IEditorInput fElement;
		public FileSynchronizer fFileSynchronizer;
		public boolean fShouldReleaseOnInfoDispose;
		public IStructuredModel fStructuredModel;

		public ModelInfo(IStructuredModel structuredModel, IEditorInput element, boolean selfCreated) {
			fElement = element;
			fStructuredModel = structuredModel;
			fShouldReleaseOnInfoDispose = selfCreated;
			fFileSynchronizer = createModelSynchronizer(element, structuredModel);
		}

		public void processPostModelEvent(ModelLifecycleEvent event) {
			if (debugLifecyclelistener) {
				System.out.println("FileModelProvider: " + event.getModel().getId() + " " + event.toString());
			}
			if (event.getType() == ModelLifecycleEvent.MODEL_SAVED) {
				// update the recorded timestamps in the ElementInfo
				FileInfo info = (FileInfo) getElementInfo(fElement);
				if (info != null) {
					info.fModificationStamp = computeModificationStamp(((IFileEditorInput) fElement).getFile());
				} else {
					throw new SourceEditingRuntimeException(new IllegalArgumentException("no corresponding element info found for " + event.getModel().getId()), "leftover model lifecycle listener"); //$NON-NLS-2$ //$NON-NLS-1$
				}
			}
		}

		public void processPreModelEvent(ModelLifecycleEvent event) {
			if (debugLifecyclelistener) {
				System.out.println("FileModelProvider: " + event.getModel().getId() + " " + event.toString());
			}
		}
	}

	static final boolean debugLifecyclelistener = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/filemodelprovider/lifecyclelistener"));
	static final boolean debugModelStatelistener = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/filemodelprovider/modelstatelistener"));

	private static final boolean debugOperations = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/filemodelprovider/operations"));

	private static FileModelProvider fInstance = null;
	private static IModelManager fModelManager;

	/** NLS strings */
	//    private static final String UNSUPPORTED_ENCODING_WARNING =
	// ResourceHandler.getString("1This_encoding({0})_is_not__WARN_");
	// //$NON-NLS-1$
	//$NON-NLS-1$ = "This encoding({0}) is not supported. The default encoding will be used instead."
	// CSSFileModelProvider will use this.
	protected static final String UNSUPPORTED_ENCODING_WARNING_TITLE = ResourceHandler.getString("Encoding_warning_UI_"); //$NON-NLS-1$ = "Encoding warning"

	public synchronized static FileModelProvider getInstance() {
		if (fInstance == null)
			fInstance = new FileModelProvider();
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

	private Shell fActiveShell;

	// workaround for save-as in C4
	private Vector fChangingElements = new Vector(1);

	private InternalModelStateListener fInternalModelStateListener;

	/** IStructuredModel information of all connected elements */
	private Map fModelInfoMap = new HashMap();

	/**
	 * @deprecated - temporary flag to change the behavior of createDocument
	 *             during revert/reset
	 */
	private boolean fReuseModelDocument = true;

	protected FileModelProvider() {
		fInternalModelStateListener = new InternalModelStateListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#aboutToChange(java.lang.Object)
	 */
	public void aboutToChange(Object element) {

		super.aboutToChange(element);

		// start recording for revert
		IStructuredModel model = getModel(element);
		if (model != null) {
			fChangingElements.add(element);
			IStructuredTextUndoManager undoMgr = model.getUndoManager();
			undoMgr.beginRecording(this, 0, 0);
		}
	}

	/**
	 * Register additional (viewer related) factories Note: this can be called
	 * twice, one for when model first created and loaded to editor. And
	 * possible later, if a model "reinit" takes place.
	 */
	protected void addProviderFactories(IStructuredModel structuredModel) {
		// its an error to call with null argument
		if (structuredModel == null)
			return;

		EditorPlugin plugin = ((EditorPlugin) Platform.getPlugin(EditorPlugin.ID));
		AdapterFactoryRegistry adapterRegistry = plugin.getAdapterFactoryRegistry();
		//Iterator adapterFactoryList =
		// adapterRegistry.getAdapterFactories();
		String contentTypeId = structuredModel.getModelHandler().getAssociatedContentTypeId();

		Iterator adapterFactoryList = ((AdapterFactoryRegistryImpl) adapterRegistry).getAdapterFactories(contentTypeId);
		IFactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "model in invalid state");
		// And all those appropriate for this particular type of content
		while (adapterFactoryList.hasNext()) {
			try {
				AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterFactoryList.next();
				// (pa) ContentType was already checked above
				// this check is here for backwards compatability
				// for those that still don't specify content type
				if (provider.isFor(structuredModel.getModelHandler())) {
					provider.addAdapterFactories(structuredModel);
				}
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#changed(java.lang.Object)
	 */
	public void changed(Object element) {

		super.changed(element);

		// end recording for revert
		if (fChangingElements.contains(element)) {
			fChangingElements.remove(element);
			IStructuredTextUndoManager undoMgr = getModel(element).getUndoManager();
			undoMgr.endRecording(this, 0, 0);
		}
	}

	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (debugOperations) {
			if (element instanceof IStorageEditorInput)
				System.out.println("FileModelProvider: createAnnotationModel for " + ((IStorageEditorInput) element).getStorage().getFullPath());
			else
				System.out.println("FileModelProvider: createAnnotationModel for " + element);
		}
		IAnnotationModel model = null;
		if (element instanceof IEditorInput) {
			IFile file = (IFile) ((IEditorInput) element).getAdapter(IFile.class);
			model = new StructuredResourceMarkerAnnotationModel(file);
		}
		if (model == null)
			model = super.createAnnotationModel(element);
		return model;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
	 */
	protected IDocument createDocument(Object element) {
		IDocument document = null;
		if (debugOperations) {
			if (element instanceof IFileEditorInput)
				System.out.println("FileModelProvider: createDocument for " + ((IFileEditorInput) element).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: createDocument for " + element);
		}
		if (element instanceof IEditorInput) {
			// create a new IDocument for the element; should always reflect
			// the contents of the resource
			ModelInfo info = getModelInfoFor((IEditorInput) element);
			if (info == null) {
				throw new SourceEditingRuntimeException(new IllegalArgumentException("no corresponding model info found")); //$NON-NLS-1$
			}
			IStructuredModel model = info.fStructuredModel;
			if (model != null) {
				if (!fReuseModelDocument && element instanceof IFileEditorInput) {
					Reader reader = null;
					IStructuredDocument innerdocument = null;
					try {
						// update document from file contents

						IFile iFile = ((IFileEditorInput) element).getFile();
						CodedReaderCreator codedReaderCreator = new CodedReaderCreator(iFile);
						reader = codedReaderCreator.getCodedReader();

						innerdocument = model.getStructuredDocument();

						int originalLengthToReplace = innerdocument.getLength();

						//TODO_future: we could implement with sequential
						// rewrite, if we don't
						// pickup automatically from FileBuffer support, so
						// not so
						// much has to be pulled into memory (as an extra big
						// string), but
						// we need to carry that API through so that
						// StructuredModel is not
						// notified until done.

						//innerdocument.startSequentialRewrite(true);
						//innerdocument.replaceText(this, 0,
						// innerdocument.getLength(), "");


						StringBuffer stringBuffer = new StringBuffer();
						int bufferSize = 2048;
						char[] buffer = new char[bufferSize];
						int nRead = 0;
						boolean eof = false;
						while (!eof) {
							nRead = reader.read(buffer, 0, bufferSize);
							if (nRead == -1) {
								eof = true;
							} else {
								stringBuffer.append(buffer, 0, nRead);
								//innerdocument.replaceText(this,
								// innerdocument.getLength(), 0, new
								// String(buffer, 0, nRead));
							}
						}
						// ignore read-only settings if reverting whole
						// document
						innerdocument.replaceText(this, 0, originalLengthToReplace, stringBuffer.toString(), true);
						model.setDirtyState(false);

					} catch (CoreException e) {
						Logger.logException(e);
					} catch (IOException e) {
						Logger.logException(e);
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e1) {
								// would be highly unusual
								Logger.logException(e1);
							}
						}
						//						if (innerdocument != null) {
						//							innerdocument.stopSequentialRewrite();
						//						}
					}

				}
				if (document == null) {
					document = model.getStructuredDocument();
				}
			}
		}
		return document;
	}

	/**
	 * Also create ModelInfo - extra resource synchronization classes should
	 * be stored within the ModelInfo
	 */
	protected ElementInfo createElementInfo(Object element) throws CoreException {
		if (debugOperations) {
			if (element instanceof IFileEditorInput)
				System.out.println("FileModelProvider: createElementInfo for " + ((IFileEditorInput) element).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: createElementInfo for " + element);
		}
		// create the corresponding ModelInfo if necessary
		if (getModelInfoFor((IEditorInput) element) == null) {
			createModelInfo((IEditorInput) element);
		}
		ElementInfo info = super.createElementInfo(element);
		return info;
	}

	/**
	 * NOT API
	 * 
	 * @param input
	 */
	public void createModelInfo(IEditorInput input) {
		if (debugOperations) {
			if (input instanceof IFileEditorInput)
				System.out.println("FileModelProvider: createModelInfo for " + ((IFileEditorInput) input).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: createModelInfo for " + input);
		}
		if (!(input instanceof IFileEditorInput)) {
			throw new SourceEditingRuntimeException(new IllegalArgumentException("This model provider only supports IFileEditorInputs")); //$NON-NLS-1$
		}
		if (getModelInfoFor(input) == null) {
			IStructuredModel structuredModel = selfCreateModel((IFileEditorInput) input);
			createModelInfo(input, structuredModel, true);
		}
	}

	/**
	 * NOT API - setModel(IStructuredModel, IEditorInput) should be used
	 * instead to force IEditorInput to IStructuredModel associations
	 * 
	 * To be used when model is provided to us, ensures that when setInput is
	 * used on this input, the given model will be used.
	 */
	public void createModelInfo(IEditorInput input, IStructuredModel structuredModel, boolean releaseModelOnDisconnect) {
		if (!(input instanceof IFileEditorInput)) {
			throw new SourceEditingRuntimeException(new IllegalArgumentException("This model provider only supports IFileEditorInputs")); //$NON-NLS-1$
		} else if (structuredModel == null) {
			throw new SourceEditingRuntimeException(new IllegalArgumentException("No model loaded for input: " + input.getName())); //$NON-NLS-1$
		}
		// we have to make sure factories are added, whether we created or
		// not.
		if (getModelInfoFor(input) != null)
			return;
		// When a model is moved, it may be in the map for two IEditorInputs
		// for
		// a brief time. Ensure that model "setup" is only performed once.
		ModelInfo existingModelInfo = getModelInfoFor(structuredModel);
		if (existingModelInfo == null) {
			addProviderFactories(structuredModel);
		} else {
			reinitializeFactories(structuredModel);
		}
		ModelInfo modelInfo = new ModelInfo(structuredModel, input, releaseModelOnDisconnect);
		fModelInfoMap.put(input, modelInfo);
		if (modelInfo.fFileSynchronizer != null) {
			modelInfo.fFileSynchronizer.install();
		}
		modelInfo.fStructuredModel.addModelStateListener(fInternalModelStateListener);
		modelInfo.fStructuredModel.addModelLifecycleListener(modelInfo);
		// fix once approved
		// we only resetSynchronizationStamp on model if its not set already
		if (input.getAdapter(IFile.class) != null && modelInfo.fStructuredModel.getSynchronizationStamp() == IResource.NULL_STAMP) {
			modelInfo.fStructuredModel.resetSynchronizationStamp((IResource) input.getAdapter(IFile.class));
		}
	}

	protected FileSynchronizer createModelSynchronizer(IEditorInput input, IStructuredModel model) {
		return null;
	}

	protected void disposeElementInfo(Object element, ElementInfo info) {
		if (debugOperations) {
			if (element instanceof IFileEditorInput)
				System.out.println("FileModelProvider: disposeElementInfo for " + ((IFileEditorInput) element).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: disposeElementInfo for " + element);
		}
		if (element instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) element;
			ModelInfo modelInfo = getModelInfoFor(input);
			if (modelInfo.fFileSynchronizer != null) {
				modelInfo.fFileSynchronizer.uninstall();
			}
			disposeModelInfo(modelInfo);
		}
		super.disposeElementInfo(element, info);
	}

	/**
	 * disconnect from this model info
	 * 
	 * @param info
	 */
	public void disposeModelInfo(ModelInfo info) {
		info.fStructuredModel.removeModelLifecycleListener(info);
		info.fStructuredModel.removeModelStateListener(fInternalModelStateListener);
		if (info.fShouldReleaseOnInfoDispose) {
			if (debugOperations) {
				if (info.fElement instanceof IFileEditorInput)
					System.out.println("FileModelProvider: disposeModelInfo and releaseFromEdit for " + ((IFileEditorInput) info.fElement).getFile().getFullPath());
				else
					System.out.println("FileModelProvider: disposeModelInfo and releaseFromEdit for " + info.fElement);
			}
			info.fStructuredModel.releaseFromEdit();
		} else if (debugOperations) {
			if (info.fElement instanceof IFileEditorInput)
				System.out.println("FileModelProvider: disposeModelInfo without releaseFromEdit for " + ((IFileEditorInput) info.fElement).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: disposeModelInfo without releaseFromEdit for " + info.fElement);
		}
		if (info.fFileSynchronizer != null) {
			info.fFileSynchronizer.uninstall();
		}
		fModelInfoMap.remove(info.fElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doResetDocument(java.lang.Object,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void doResetDocument(Object element, IProgressMonitor monitor) throws CoreException {
		fReuseModelDocument = false;
		super.doResetDocument(element, monitor);
		fReuseModelDocument = true;
	}

	public void doSaveDocument(IProgressMonitor progressMonitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		//TODO: still need to "transfer" much of this logic to model level
		boolean success = false;
		Assert.isTrue(element instanceof IFileEditorInput);
		IFileEditorInput input = (IFileEditorInput) element;
		IStructuredModel model = getModel(getInputFor(document));
		Assert.isNotNull(model);
		boolean localDirtyState = model.isDirty();
		IFile resource = input.getFile();
		try {
			if (!overwrite) {
				checkSynchronizationState(((FileInfo) getElementInfo(element)).fModificationStamp, resource);
			}
			// inform about the upcoming content change
			fireElementStateChanging(element);
			try {
				model.save(resource); //, encodingname, null);
			} catch (UnsupportedCharsetException exception) {
				Shell shell = getActiveShell();
				// FYI: made this indirect chain of calls to help get rid of
				// our specific Exception, need to test
				// that all is updated in right order, even when error.
				EncodingMemento encodingMemento = model.getStructuredDocument().getEncodingMemento();
				String foundEncoding = encodingMemento.getInvalidEncoding();
				String defaultToUse = encodingMemento.getAppropriateDefault();
				boolean tryDefault = openUnsupportEncodingForSave(foundEncoding, defaultToUse, resource.getName(), shell);
				if (tryDefault) {
					model.save(resource, EncodingRule.FORCE_DEFAULT);
				} else {
					// User has canceled Save. Keep view opened
					progressMonitor.setCanceled(true);
					return;
				}
			} catch (MalformedOutputExceptionWithDetail exception) {
				Shell shell = getActiveShell();
				boolean userOK = openUnconvertableCharactersWarningForSave(exception, shell); //resource.getName()
				if (userOK) {
					model.save(resource, EncodingRule.IGNORE_CONVERSION_ERROR);
				} else {
					// User has canceled Save. Keep view opened
					progressMonitor.setCanceled(true);
					return;
				}
			} catch (CharConversionErrorWithDetail exception) {
				Shell shell = getActiveShell();
				boolean userOK = openUnconvertableCharactersWarningForSave(exception, shell); //resource.getName()
				if (userOK) {
					model.save(resource, EncodingRule.IGNORE_CONVERSION_ERROR);
				} else {
					// User has canceled Save. Keep view opened
					progressMonitor.setCanceled(true);
					//outStream.close();
					//outStream = null;
					return;
				}
			}
			if (!resource.exists()) {
				progressMonitor.beginTask(ResourceHandler.getString("FileDocumentProvider.task.saving"), 2000); //$NON-NLS-1$ 
				ContainerGenerator generator = new ContainerGenerator(resource.getParent().getFullPath());
				generator.generateContainer(new SubProgressMonitor(progressMonitor, 1000));
			}
			// if we get to here without an exception... success!
			success = true;
			model.setDirtyState(false);
			model.resetSynchronizationStamp(resource);

			// update markers
			if (getAnnotationModel(element) instanceof AbstractMarkerAnnotationModel) {
				((AbstractMarkerAnnotationModel) getAnnotationModel(element)).updateMarkers(document);
			}
			// reset the modification stamp record so we know we don't try to
			// reload on following resource change notifications
			FileInfo info = (FileInfo) getElementInfo(element);
			if (info != null) {
				info.fModificationStamp = computeModificationStamp(resource);
			}

			//For error handling test only!!!==========
			//
			//Uncomment the following line of code to simulate a
			// FileNotFoundException.
			//throw new FileNotFoundException();
			//
			//Uncomment the following line of code to simulate a
			// UnsupportedEncodingException.
			//throw new UnsupportedEncodingException();
			//
			//Uncomment the following line of code to simulate a IOException.
			//throw new IOException();
			//For error handling test only!!!==========
		} catch (java.io.FileNotFoundException exception) {
			/*
			 * The FileNotFoundException's message may not be very meaningful
			 * to user. Since the SourceEditingRuntimeException(exception,
			 * message) form will use just the detail message in the passed-in
			 * exception (instead of concatenating the detail message with the
			 * additional message), we are converting this exception into a
			 * more informative message. Same idea applies to the following
			 * catch blocks.
			 */
			progressMonitor.setCanceled(true);
			// inform about failure
			fireElementStateChangeFailed(element);
			throw new SourceEditingRuntimeException(ResourceHandler.getString("Unable_to_save_the_documen_ERROR_")); //$NON-NLS-1$ = "Unable to save the document. Output file not found."
		} catch (UnsupportedEncodingException exception) {
			progressMonitor.setCanceled(true);
			// inform about failure
			fireElementStateChangeFailed(element);
			throw new SourceEditingRuntimeException(ResourceHandler.getString("Unable_to_save_the_documen1_ERROR_")); //$NON-NLS-1$ = "Unable to save the document. Unsupported encoding."
		} catch (java.io.IOException exception) {
			progressMonitor.setCanceled(true);
			// inform about failure
			fireElementStateChangeFailed(element);
			throw new SourceEditingRuntimeException(ResourceHandler.getString("Unable_to_save_the_documen2_ERROR_")); //$NON-NLS-1$
			//$NON-NLS-1$ = "Unable to save the document. An I/O error occurred while saving the document."
		} catch (OperationCanceledException exception) {
			Logger.log(Logger.INFO, "Save Operation Canceled at user's request"); //$NON-NLS-1$
		} finally {
			if (!success) {
				model.setDirtyState(localDirtyState);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#synchronize(java.lang.Object)
	 */
	public void doSynchronize(Object element, IProgressMonitor monitor) throws CoreException {
		super.doSynchronize(element, monitor);
		if (element instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) element;
			getModel(input).resetSynchronizationStamp(input.getFile());
		}
	}

	protected void fireModelContentAboutToBeReplaced(IStructuredModel model) {
	}

	protected void fireModelContentReplaced(IStructuredModel model) {
	}

	protected void fireModelDeleted(IStructuredModel model) {
	}

	protected void fireModelDirtyStateChanged(IStructuredModel model, final boolean isDirty) {
	}

	protected void fireModelMoved(IStructuredModel originalModel, IStructuredModel movedModel) {
	}

	protected Shell getActiveShell() {
		Shell shell = null;
		// Looks like Display tells me what is the current active Shell
		// so that it seems not to ask EditorPart to give me a Shell.
		// Same technique in used by
		// Infopop(org.eclipse.help.internal.ui.ContextHelpDialog(Object [],
		// int, int))
		// looks like there's not always an active shell, at least
		// during debugging the save operation with invalid encoding
		if (fActiveShell != null) {
			shell = fActiveShell;
		} else {
			Display dsp = getDisplay();
			shell = dsp.getActiveShell();
		}
		return shell;
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.IStorageDocumentProvider#getEncoding(java.lang.Object)
	 */
	public String getEncoding(Object element) {
		EncodingMemento em = null;
		IStructuredModel model = getModel((IEditorInput) element);
		// model can be null if opened on non-expected file
		if (model != null) {
			em = model.getStructuredDocument().getEncodingMemento();
		}
		// DefaultEncodingSupport uses IANA names
		if (em != null)
			return em.getDetectedCharsetName();
		return super.getEncoding(element);
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
	 * @see org.eclipse.wst.sse.ui.IModelProvider#getModel(java.lang.Object)
	 */
	public IStructuredModel getModel(Object element) {
		Assert.isTrue(element instanceof IFileEditorInput);
		return getModel((IFileEditorInput) element);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#getPersistedEncoding(java.lang.Object)
	 */
	protected String getPersistedEncoding(Object element) {
		EncodingMemento em = null;
		IStructuredModel model = getModel((IEditorInput) element);
		if (model != null) {
			em = model.getStructuredDocument().getEncodingMemento();
		}
		// DefaultEncodingSupport uses IANA names
		if (em != null) {
			return em.getDetectedCharsetName();
		} else {
			// error condition
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Defines the standard procedure to handle CoreExceptions.
	 * 
	 * @param exception
	 *            the exception to be logged
	 * @param message
	 *            the message to be logged
	 */
	protected void handleCoreException(CoreException exception, String message) {
		Logger.logException(message, exception);
	}

	/**
	 * Updates the element info and Document contents to a change of the file
	 * content and sends out appropriate notifications.
	 * 
	 * @param fileEditorInput
	 *            the input of a text editor
	 */
	protected void handleElementContentChanged(IFileEditorInput fileEditorInput) {
		FileInfo info = (FileInfo) getElementInfo(fileEditorInput);
		if (info == null)
			return;

		String oldContents = getModel(fileEditorInput).getStructuredDocument().get();

		try {
			refreshFile(fileEditorInput.getFile());
		} catch (CoreException x) {
			handleCoreException(x, "FileDocumentProvider.handleElementContentChanged"); //$NON-NLS-1$
		}

		// set the new content and fire content related events
		fireElementContentAboutToBeReplaced(fileEditorInput);
		removeUnchangedElementListeners(fileEditorInput, info);

		// direct superclass also removes but never adds?
		info.fDocument.removeDocumentListener(info);

		boolean reloaded = false;
		InputStream inStream = null;
		try {
			inStream = fileEditorInput.getFile().getContents(true);
			getModel(fileEditorInput).reload(inStream);
			reloaded = true;
		} catch (IOException e) {
			String message = MessageFormat.format(ResourceHandler.getString("FileModelProvider.0"), new String[]{fileEditorInput.getName()}); //$NON-NLS-1$
			info.fStatus = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.ERROR, message, e);
		} catch (CoreException e) {
			info.fStatus = e.getStatus();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e1) {
					Logger.logException(e1);
				}
			}
		}
		info.fDocument = getModel(fileEditorInput).getStructuredDocument();

		info.fCanBeSaved = false;
		info.fModificationStamp = computeModificationStamp(fileEditorInput.getFile());
		if (reloaded) {
			info.fStatus = null;
		}

		addUnchangedElementListeners(fileEditorInput, info);
		fireElementContentReplaced(fileEditorInput);
		if (!reloaded) {
			info.fDocument.set(oldContents);
		}
	}

	/**
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#isModifiable(java.lang.Object)
	 */
	public boolean isModifiable(Object element) {
		return super.isModifiable(element); //!isReadOnly(element);
	}

	/**
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#mustSaveDocument(java.lang.Object)
	 *      <br>
	 * 
	 * The rule is that if it is sharedForEdit, then it never "must be saved",
	 * but if its not sharedForEdit, then it depends on its dirty state.
	 */
	public boolean mustSaveDocument(Object element) {
		boolean result = false;
		if (getModel((IEditorInput) element) != null) {
			if (!getModel((IEditorInput) element).isSharedForEdit()) {
				result = getModel((IEditorInput) element).isDirty();
			}
		}
		return result;
	}

	private boolean openUnconvertableCharactersWarningForSave(CharConversionErrorWithDetail outputException, Shell topshell) {
		// open message dialog
		final String title = UNSUPPORTED_ENCODING_WARNING_TITLE;
		String userMessage = ResourceHandler.getString("cannot_convert_some_characters2"); //$NON-NLS-1$
		MessageFormat form = new MessageFormat(userMessage);
		Object[] args = {outputException.getCharsetName()};
		final String msg = form.format(args);
		Shell shell = getActiveShell();
		MessageDialog warning = new MessageDialog(shell, title, null, msg, MessageDialog.QUESTION, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
		if (warning.open() == 0)
			return true;
		return false;
	}

	protected boolean openUnconvertableCharactersWarningForSave(MalformedOutputExceptionWithDetail outputException, Shell topshell) {
		// open message dialog
		final String title = UNSUPPORTED_ENCODING_WARNING_TITLE;
		String userMessage = ResourceHandler.getString("cannot_convert_some_characters"); //$NON-NLS-1$
		MessageFormat form = new MessageFormat(userMessage);
		Object[] args = {outputException.getAttemptedIANAEncoding(), Integer.toString(outputException.getCharPosition())};
		final String msg = form.format(args);
		Shell shell = getActiveShell();
		MessageDialog warning = new MessageDialog(shell, title, null, msg, MessageDialog.QUESTION, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
		if (warning.open() == 0)
			return true;
		return false;
	}

	protected void openUndecodableCharacterError(MalformedInputExceptionWithDetail e) {
		// checked and coordinated among all editors.
		String title = ResourceHandler.getString("Error_opening_file_UI_"); //$NON-NLS-1$ = "Error opening file"
		String msg = e.toString();
		// if the exception char position < 0, perhaps we exceeded the max
		// buffer when detecting pos of error
		// if that is the case, display a different error message
		IStatus status;
		if ((e.getCharPosition() < 0) && (e.isExceededMax()))
			status = new Status(IStatus.ERROR, EditorPlugin.ID, 0, ResourceHandler.getString("8concat_ERROR_", (new Object[]{Integer.toString(e.getMaxBuffer()), e.getAttemptedIANAEncoding()})), e); //$NON-NLS-1$
		else
			status = new Status(IStatus.ERROR, EditorPlugin.ID, 0, ResourceHandler.getString("7concat_ERROR_", (new Object[]{(Integer.toString(e.getCharPosition())), e.getAttemptedIANAEncoding()})), e); //$NON-NLS-1$
		//$NON-NLS-1$ = "Could not be decoded character (at position {0}) according to the encoding parameter {1}"
		ErrorDialog.openError(getActiveShell(), title, msg, status);
	}

	protected void openUnsupportedEncodingWarningForLoad(String encoding, String defaultToUse) {
		// open message dialog
		final String title = UNSUPPORTED_ENCODING_WARNING_TITLE;
		MessageFormat form = new MessageFormat(ResourceHandler.getString("This_encoding_({0})_is_not_supported._The_default_encoding_({1})_will_be_used_instead._1")); //$NON-NLS-1$
		Object[] args = {encoding, defaultToUse};
		final String msg = form.format(args);
		Shell shell = getActiveShell();
		MessageDialog warning = new MessageDialog(shell, title, null, msg, MessageDialog.WARNING, new String[]{IDialogConstants.OK_LABEL}, 0);
		warning.open();
	}

	protected boolean openUnsupportEncodingForSave(String foundEncoding, String defaultEncodingToUse, String dialogTitle, Shell topshell) {
		if (topshell == null)
			return true; // if no topshell, return true;
		// open message dialog
		//  MessageFormat form = new
		// MessageFormat(ResourceHandler.getString("This_encoding({0})_is_not__WARN_"));
		// //$NON-NLS-1$ = "This encoding({0}) is not supported. Continue ?"
		MessageFormat form = new MessageFormat(ResourceHandler.getString("This_encoding_({0})_is_not_supported._Continue_the_save_using_the_default_({1})__2")); //$NON-NLS-1$
		Object[] args = {foundEncoding, defaultEncodingToUse};
		final String msg = form.format(args);
		MessageDialog warning = new MessageDialog(topshell, dialogTitle, null, msg, MessageDialog.QUESTION, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
		if (warning.open() == 0)
			return true;
		return false;
	}

	/**
	 * Register additional (viewer related) factories Note: this can be called
	 * twice, one for when model first created and loaded to editor. And
	 * possible later, if a model "reinit" takes place.
	 */
	protected void reinitializeFactories(IStructuredModel structuredModel) {
		// its an error to call with null argument
		if (structuredModel == null)
			return;
		EditorPlugin plugin = ((EditorPlugin) Platform.getPlugin(EditorPlugin.ID));
		AdapterFactoryRegistry adapterRegistry = plugin.getAdapterFactoryRegistry();
		Iterator adapterList = adapterRegistry.getAdapterFactories();
		// And all those appropriate for this particular type of content
		while (adapterList.hasNext()) {
			try {
				AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterList.next();
				if (provider.isFor(structuredModel.getModelHandler())) {
					provider.reinitializeFactories(structuredModel);
				}
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

	protected IStructuredModel selfCreateModel(IFileEditorInput input) {
		// this method should be called only after is established the
		// desired model is not in the active items list.
		IStructuredModel structuredModel = getModelManager().getExistingModelForEdit(input.getFile());
		if (structuredModel == null) {
			try {
				try {
					structuredModel = getModelManager().getModelForEdit(input.getFile()); //
				} catch (UnsupportedCharsetException exception) {
					// TODO-future: user should be given a choice here to try
					// other encodings other than 'default'
					EncodingMemento encodingMemento = null;
					if (exception instanceof UnsupportedCharsetExceptionWithDetail) {
						UnsupportedCharsetExceptionWithDetail detailedException = (UnsupportedCharsetExceptionWithDetail) exception;
						encodingMemento = detailedException.getEncodingMemento();
					}

					String foundEncoding = encodingMemento.getInvalidEncoding();
					String defaultToUse = encodingMemento.getAppropriateDefault();
					openUnsupportedEncodingWarningForLoad(foundEncoding, defaultToUse);
					try {
						structuredModel = getModelManager().getModelForEdit(input.getFile(), EncodingRule.FORCE_DEFAULT); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (MalformedInputExceptionWithDetail ex) { // this
						openUndecodableCharacterError(ex);
						structuredModel = null;
					}
				} catch (MalformedInputExceptionWithDetail e) {
					openUndecodableCharacterError(e);

					structuredModel = getModelManager().getModelForEdit(input.getFile(), EncodingRule.IGNORE_CONVERSION_ERROR);
				}
			} catch (CoreException e) {
				throw new SourceEditingRuntimeException(e);
			} catch (IOException e) {
				throw new SourceEditingRuntimeException(e);
			}
		}
		return structuredModel;
	}

	/**
	 * we should figure out how not to need this in future. I've found that
	 * during 'save', the active shell, obtainable from Display, is null, so
	 * this shell must be set by editor before requesting the save. We should
	 * check "legality" directly from editor.
	 */
	public void setActiveShell(Shell activeShell) {
		fActiveShell = activeShell;

	}

	/**
	 * This method is intended for those uses where the model has already been
	 * obtained and provided by the client (with its own id, not necessarily
	 * the one we would create by default). The IEditorInput must still be
	 * connect()ed before regular IDocumentProvider APIs will function.
	 */
	public void setModel(IStructuredModel model, IEditorInput input) {
		if (debugOperations) {
			if (input instanceof IFileEditorInput)
				System.out.println("FileModelProvider: setModel override used for " + ((IFileEditorInput) input).getFile().getFullPath());
			else
				System.out.println("FileModelProvider: setModel override used for " + input);
		}
		createModelInfo(input, model, false);
	}

}
