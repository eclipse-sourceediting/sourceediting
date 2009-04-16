/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.internal.ui.util.ExceptionHandler;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author childsb
 * 
 */
public class ShowHistoryAction extends JsElementActionProxy {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	private IResource getHostResource(IJavaScriptElement virtualElement) {
		IProject project = virtualElement.getJavaScriptProject().getProject();
		IPath path = new Path(virtualElement.getHostPath().getPath());
		IResource host = project.getWorkspace().getRoot().findMember(path);
		return host;
	}
	
	
	public void run(IAction action) {
		IJavaScriptElement elements[] = JsElementActionProxy.getJsElementsFromSelection(getCurrentSelection());
		if (elements == null || elements.length == 0) {
			return;
		}
		IResource resource = null;
		if (elements[0].isVirtual()) {
			resource = getHostResource(elements[0]);
		} else {
			resource = elements[0].getResource();
		}
		if (resource == null) {
			return;
		}
		try {
			IWorkbenchPage page = targetWorkbenchPart.getSite().getPage();
			IViewPart view = page.showView(IPageLayout.ID_RES_NAV);
			if (view instanceof ISetSelectionTarget) {
				ISelection selection = new StructuredSelection(resource);
				((ISetSelectionTarget) view).selectReveal(selection);
			}
		} catch (PartInitException e) {
			ExceptionHandler.handle(e, targetWorkbenchPart.getSite().getShell(), Messages.getString("ShowHistoryAction.0"), Messages.getString("ShowHistoryAction.1") + e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	
	public void selectionChanged(IAction action, ISelection selection) {
		setSelection(selection);
		IJavaScriptElement elements[] = JsElementActionProxy.getJsElementsFromSelection(getCurrentSelection());
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].isVirtual()) {
				IResource resource = getHostResource(elements[i]);
				if (resource == null || !resource.exists()) {
					action.setEnabled(false);
				}
			}
		}
	}
}
