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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;


/**
 * Configuration class for Property Sheet Pages. Not finalized.
 * 
 * @plannedfor 1.0
 */
public abstract class PropertySheetConfiguration {
	public PropertySheetConfiguration() {
		super();
	}

	public void addContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		// do nothing
	}

	/**
	 * Allows for filteration of selection before being sent to the viewer
	 * 
	 * @param selectingPart -
	 *            may be null
	 * @param selection
	 * @return
	 */
	public ISelection getInputSelection(IWorkbenchPart selectingPart, ISelection selection) {
		ISelection preferredSelection = selection;
		if (selection instanceof IStructuredSelection) {
			// don't support more than one selected node
			if (((IStructuredSelection) selection).size() > 1)
				preferredSelection = StructuredSelection.EMPTY;
		}
		return preferredSelection;
	}

	/**
	 * Returns the correct IPropertySourceProvider
	 */
	public abstract IPropertySourceProvider getPropertySourceProvider(IPropertySheetPage page);

	public void removeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		// do nothing
	}

	public void unconfigure() {
	}
}
