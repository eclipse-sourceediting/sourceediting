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
package org.eclipse.wst.sse.ui.internal.provisional.views.properties;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.properties.AdapterPropertySourceProvider;


/**
 * A PropertySheetConfiguration appropriate for StructuredTextEditors and
 * StructuredModels
 * 
 * @plannedfor 1.0
 */
public class StructuredPropertySheetConfiguration extends PropertySheetConfiguration {
	protected IStructuredModel fModel;

	/**
	 * 
	 */
	public StructuredPropertySheetConfiguration() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration#createPropertySourceProvider()
	 */
	protected IPropertySourceProvider createPropertySourceProvider() {
		return new AdapterPropertySourceProvider();
	}

	/**
	 * @return Returns the model.
	 */
	public IStructuredModel getModel() {
		return fModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration#getSelection(org.eclipse.jface.viewers.ISelection,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		ISelection preferredSelection = selection;
		if (selection instanceof ITextSelection && fModel != null) {
			// on text selection, find the appropriate IndexedRegion
			ITextSelection textSel = (ITextSelection) selection;
			Object inode = getModel().getIndexedRegion(textSel.getOffset());
			if (inode != null) {
				preferredSelection = new StructuredSelection(inode);
			}
		}
		else if (selection instanceof IStructuredSelection) {
			// don't support more than one selected node
			if (((IStructuredSelection) selection).size() > 1)
				preferredSelection = StructuredSelection.EMPTY;
		}
		return preferredSelection;
	}

	/**
	 * @return
	 */
	public void setEditor(IEditorPart editor) {
		super.setEditor(editor);
		IStructuredModel model = null;
		if (editor != null) {
			ITextEditor textEditor = null;
			if (editor instanceof ITextEditor)
				textEditor = (ITextEditor) editor;
			else
				textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
			if (textEditor != null) {
				IDocument document = textEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				if (document != null)
					model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
				else
					model = null;
			}
		}
		// as long as the editor remains valid, we won't be the last reference
		// to this model
		if (fModel != null)
			fModel.releaseFromRead();
		fModel = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration#unconfigure()
	 */
	public void unconfigure() {
		super.unconfigure();
		setEditor(null);
	}

}
