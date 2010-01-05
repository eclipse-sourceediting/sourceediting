/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIPlugin;

/**
 * Not API
 */
public class SetupProjectsWizzard implements IObjectActionDelegate, IActionDelegate {
	IWorkbenchPart fPart;
	Object[] fTarget;

	private void doInstall(IProject project, final boolean openProperties, IProgressMonitor monitor) {
		boolean configured = false;
		JsWebNature nature = new JsWebNature(project, monitor);
		try {
			boolean hadBasicNature = JsWebNature.hasNature(project);

			nature.configure();

			if (!hadBasicNature) {
				/*
				 * No nature before, so no existing build path. Define the
				 * project itself as an include folder.
				 */
				IJavaScriptProject jp = JavaScriptCore.create(project);
				IIncludePathEntry[] oldEntries = null;
				try {
					oldEntries = jp.getRawIncludepath();
				}
				catch (JavaScriptModelException ex1) {
					Logger.log(Logger.ERROR_DEBUG, null, ex1);
				}
				
				IPath projectPath = project.getFullPath();
				IIncludePathEntry projectPathEntry = JavaScriptCore.newSourceEntry(projectPath);
				
				if (! Arrays.asList(oldEntries).contains(projectPathEntry)) {
					IIncludePathEntry[] newEntries = new IIncludePathEntry[oldEntries.length + 1];
					System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
					
					newEntries[oldEntries.length] = projectPathEntry;
					jp.setRawIncludepath(newEntries, monitor);
				}
			}
			configured = true;
		}
		catch (CoreException ex) {
			Logger.logException(ex);
		}

		if (configured && openProperties) {
			showPropertiesOn(project, monitor);
		}
	}
	
	private void doUninstall(IProject project, IProgressMonitor monitor) {
		JsWebNature nature = new JsWebNature(project, monitor);
		try {
			nature.deconfigure();
		} catch (CoreException ex) {
			Logger.logException(ex);
		}
	}

	private void install(final IProject project, final boolean openProperties) {
		IProgressService service = null;
		if (fPart != null) {
			service = (IProgressService) fPart.getSite().getService(IProgressService.class);
		}
		if (service == null) {
			doInstall(project, openProperties, null);
		}
		else {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					doInstall(project, openProperties, monitor);
				}
			};
			try {
				service.run(false, false, runnable);
			}
			catch (InvocationTargetException e) {
				Logger.logException(e);
			}
			catch (InterruptedException e) {
				Logger.logException(e);
			}
		}
	}

	public void run(IAction action) {
		if (fTarget == null)
			return;

		for (int i = 0; i < fTarget.length; i++) {
			if (fTarget[i] instanceof IResource) {
				final IProject project = ((IResource) fTarget[i]).getProject();

				if (!JsWebNature.hasNature(project)) {
					/* Doesn't have nature, do a full install. */
					install(project, i == fTarget.length - 1);
				}
				else {
					/*
					 * Has nature, check for browser library on include path
					 * and setup if not found.
					 */
					IJavaScriptProject jp = JavaScriptCore.create(project);
					IIncludePathEntry[] rawClasspath = null;
					try {
						rawClasspath = jp.getRawIncludepath();
					}
					catch (JavaScriptModelException ex1) {
						Logger.log(Logger.ERROR_DEBUG, null, ex1);
					}

					boolean browserFound = false;
					for (int k = 0; rawClasspath != null && !browserFound && k < rawClasspath.length; k++) {
						if (rawClasspath[k].getPath().equals(JsWebNature.VIRTUAL_BROWSER_CLASSPATH)) {
							browserFound = true;
						}
					}
					if (!browserFound) {
						install(project, false);
					}
				}
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			fTarget = ((IStructuredSelection) selection).toArray();
		}
		else {
			fTarget = null;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fPart = targetPart;
	}

	private void showPropertiesOn(final IProject project, final IProgressMonitor monitor) {
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.ui.propertyPages").getExtensions(); //$NON-NLS-1$
		final List pageIds = new ArrayList(8);
		for (int i = 0; i < extensions.length; i++) {
			if (extensions[i].getNamespaceIdentifier().startsWith("org.eclipse.wst.jsdt.")) { //$NON-NLS-1$
				IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configurationElements.length; j++) {
					if ("page".equals(configurationElements[j].getName())) {
						pageIds.add(configurationElements[j].getAttribute("id")); //$NON-NLS-1$
					}
				}
			}
		}
		Shell shell = (Shell) fPart.getAdapter(Shell.class);
		if (shell == null) {
			IWorkbenchWindow activeWorkbenchWindow = JsUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null)
				shell = activeWorkbenchWindow.getShell();
		}
		final Shell finalShell = shell;
		if (finalShell != null) {
			finalShell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(finalShell, project, "org.eclipse.wst.jsdt.ui.propertyPages.BuildPathsPropertyPage", (String[]) pageIds.toArray(new String[pageIds.size()]), null); //$NON-NLS-1$
					if (dialog.open() == Window.CANCEL) {
						doUninstall(project, monitor);
					}
				}
			});
		}
	}
}