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
package org.eclipse.wst.sse.ui.views.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.sse.ui.internal.properties.AdapterPropertySourceProvider;


/**
 * A PropertySheetConfiguration appropriate for StructuredTextEditors and
 * StructuredModels
 * 
 * @plannedfor 1.0
 */
public class StructuredPropertySheetConfiguration extends PropertySheetConfiguration {
	public StructuredPropertySheetConfiguration() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration#getSelection(org.eclipse.jface.viewers.ISelection,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		ISelection preferredSelection = selection;
		if (selection instanceof IStructuredSelection) {
			// don't support more than one selected node
			if (((IStructuredSelection) selection).size() > 1)
				preferredSelection = StructuredSelection.EMPTY;
		}
		return preferredSelection;
	}

	protected IPropertySourceProvider createPropertySourceProvider(IPropertySheetPage page) {
		return new AdapterPropertySourceProvider();
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
