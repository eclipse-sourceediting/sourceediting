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

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;


/**
 * An IModelProvider that builds StructuredModels on top of IDocuments. A
 * prototype for "any IEditorInput" support
 * 
 * *** VERY UNTESTED *** TODO: Currently BROKEN
 * 
 * @deprecated
 */
public class NullModelProvider extends AbstractDocumentProvider implements IModelProvider {

	private static NullModelProvider _instance = null;

	public static synchronized NullModelProvider getInstance() {
		if (_instance == null) {
			_instance = new NullModelProvider();
		}
		return _instance;
	}

	/**
	 * Utility method also used in subclasses
	 */
	protected static IModelManager getModelManager() {
		// get the model manager from the plugin
		// note: we can use the static "ID" variable, since we pre-req that
		// plugin
		return StructuredModelManager.getInstance().getModelManager();
	}

	private HashMap fModelMap = new HashMap(1);

	public NullModelProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	protected IAnnotationModel createAnnotationModel(Object element) {
		return new AnnotationModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
	 */
	protected IDocument createDocument(Object element) {
		//		return getModel(element).getStructuredDocument();
		return new Document();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createElementInfo(java.lang.Object)
	 */
	//	protected ElementInfo createElementInfo(Object element) throws
	// CoreException {
	//		if (fModelMap.get(element) != null)
	//			fModelMap.put(element,
	// getModelManager().createUnManagedStructuredModelFor(ContentTypeIdentifierForXML.ContentTypeID_XML));
	//		return super.createElementInfo(element);
	//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#disposeElementInfo(java.lang.Object,
	 *      org.eclipse.ui.texteditor.AbstractDocumentProvider.ElementInfo)
	 */
	protected void disposeElementInfo(Object element, ElementInfo info) {
		fModelMap.remove(element);
		super.disposeElementInfo(element, info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.IModelProvider#getModel(java.lang.Object)
	 */
	public IStructuredModel getModel(Object element) {
		return (IStructuredModel) fModelMap.get(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#getOperationRunner(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}
}
