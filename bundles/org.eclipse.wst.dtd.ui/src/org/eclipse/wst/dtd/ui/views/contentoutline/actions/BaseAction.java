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
package org.eclipse.wst.dtd.ui.views.contentoutline.actions;

import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.document.DTDModelImpl;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

abstract public class BaseAction extends SelectionListenerAction {
	private StructuredTextEditor fTextEditor;

	public BaseAction(StructuredTextEditor editor, String text, ImageDescriptor imageDesc) {
		super(text);
		fTextEditor = editor;
		setImageDescriptor(imageDesc);
	}

	public BaseAction(StructuredTextEditor editor, String text) {
		this(editor, text, null);
	}

	public DTDNode getFirstNodeSelected() {
		return getFirstNodeSelected(getStructuredSelection());
	}

	public DTDNode getFirstNodeSelected(IStructuredSelection selection) {
		Iterator iter = selection.iterator();
		//DTDNode referencePoint = null;
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof DTDNode) {
				return (DTDNode) element;
			}
		}
		return null;
	}

	protected IEditorActionBarContributor contextContributor;

	public DTDModelImpl getModel() {
		return (DTDModelImpl) getTextEditor().getModel();
	}

	public void setContextContributor(IEditorActionBarContributor contributor) {
		contextContributor = contributor;
	}

	public IEditorActionBarContributor getContextContributor() {
		return contextContributor;
	}

	/**
	 * @return Returns the textEditor.
	 */
	public StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

}
