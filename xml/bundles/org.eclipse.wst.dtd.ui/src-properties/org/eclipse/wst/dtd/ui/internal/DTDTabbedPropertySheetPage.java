/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

class DTDTabbedPropertySheetPage extends TabbedPropertySheetPage {

	private ISelection oldSelection = null;

	public DTDTabbedPropertySheetPage(ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor) {
		super(tabbedPropertySheetPageContributor);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage activePage = workbenchWindow.getActivePage();
			if (activePage != null) {
				IEditorPart activeEditor = activePage.getActiveEditor();
				if (activeEditor != null) {
					// make sure the correct editor is active
					if (activeEditor.getAdapter(IPropertySheetPage.class) == DTDTabbedPropertySheetPage.this) {
						if (oldSelection == null || !oldSelection.equals(selection)) {
							oldSelection = selection;
							super.selectionChanged(part, selection);
						}
					}
				}
			}
		}
	}
}
