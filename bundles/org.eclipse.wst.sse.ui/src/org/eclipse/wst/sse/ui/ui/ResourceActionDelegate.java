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
package org.eclipse.wst.sse.ui.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;

public class ResourceActionDelegate implements IActionDelegate {
	protected IStructuredSelection fSelection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (fSelection != null && !fSelection.isEmpty()) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			ResourceActionProgressDialog progressDialog = new ResourceActionProgressDialog(shell);
			progressDialog.setCancelable(true);
			initProgressDialog(progressDialog);
			try {
				progressDialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						Object[] elements = fSelection.toArray();
						for (int i = 0; i < elements.length; i++) {
							if (elements[i] instanceof IResource) {
								process(monitor, (IResource) elements[i]);
							}
						}
					}
				});
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			fSelection = (IStructuredSelection) selection;
			boolean available = false;

			Object[] elements = fSelection.toArray();
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IResource) {
					available = processorAvailable((IResource) elements[i]);

					if (available)
						break;
				}
			}

			action.setEnabled(available);
		}
	}

	protected boolean processorAvailable(IResource resource) {
		return false;
	}

	protected void process(IProgressMonitor monitor, IResource resource) {

	}

	protected void initProgressDialog(ResourceActionProgressDialog progressDialog) {

	}
}
