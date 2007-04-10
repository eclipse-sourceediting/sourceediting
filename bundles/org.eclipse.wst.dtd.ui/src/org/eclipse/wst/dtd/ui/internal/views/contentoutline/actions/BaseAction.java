/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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


package org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions;

import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;


abstract public class BaseAction extends SelectionListenerAction {

	protected IEditorActionBarContributor contextContributor;
	private IStructuredModel fModel;

	public BaseAction(IStructuredModel model, String text) {
		this(model, text, null);
	}

	public BaseAction(IStructuredModel model, String text, ImageDescriptor imageDesc) {
		super(text);
		fModel = model;
		setImageDescriptor(imageDesc);
	}

	protected DTDNode getFirstNodeSelected() {
		return getFirstNodeSelected(getStructuredSelection());
	}

	protected DTDNode getFirstNodeSelected(IStructuredSelection selection) {
		Iterator iter = selection.iterator();
		// DTDNode referencePoint = null;
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof DTDNode) {
				return (DTDNode) element;
			}
		}
		return null;
	}

	protected DTDModelImpl getModel() {
		return (DTDModelImpl) fModel;
	}

	public void setContextContributor(IEditorActionBarContributor contributor) {
		contextContributor = contributor;
	}

}
