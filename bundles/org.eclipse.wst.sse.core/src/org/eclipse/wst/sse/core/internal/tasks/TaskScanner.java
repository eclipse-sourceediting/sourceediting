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
package org.eclipse.wst.sse.core.internal.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.Logger;

/**
 * Dispatcher for scanning based on deltas and requested projects
 */
class TaskScanner {
	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean _debugContentTypeDetection = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/detection")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean _debugOverallPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/overalltime")); //$NON-NLS-1$ //$NON-NLS-2$

	private static TaskScanner _instance = null;

	static synchronized TaskScanner getInstance() {
		if (_instance == null) {
			_instance = new TaskScanner();
		}
		return _instance;
	}

	private List fActiveDelegates = null;
	private TaskScannerDelegateRegistryReader registry = null;
	private long time0;

	/**
	 * 
	 */
	private TaskScanner() {
		super();
		registry = new TaskScannerDelegateRegistryReader();
		fActiveDelegates = new ArrayList();
	}


	private IContentType[] detectContentTypes(IResource resource) {
		IContentType[] types = null;
		if (resource.getType() == IResource.FILE && resource.isAccessible()) {
			IContentDescription d = null;
			try {
				// optimized description lookup, might not succeed
				d = ((IFile) resource).getContentDescription();
				if (d != null) {
					types = new IContentType[]{d.getContentType()};
				}
			}
			catch (CoreException e) {
				/*
				 * should not be possible given the accessible and file type
				 * check above
				 */
			}
			if (types == null) {
				types = Platform.getContentTypeManager().findContentTypesFor(resource.getName());
			}
			if (_debugContentTypeDetection) {
				if (types.length > 0) {
					if (types.length > 1) {
						System.out.println(resource.getFullPath() + ": " + "multiple based on name (probably hierarchical)"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					for (int i = 0; i < types.length; i++) {
						System.out.println(resource.getFullPath() + " matched: " + types[i].getId()); //$NON-NLS-1$
					}
				}
			}
		}
		return types;
	}

	void internalScan(final IProject project, final IResource resource, final IProgressMonitor scanMonitor) {
		if (scanMonitor.isCanceled())
			return;
		try {
			String name = resource.getName();
			if (name.length() != 0 && name.charAt(0) != '.') {
				if ((resource.getType() & IResource.FOLDER) > 0 || (resource.getType() & IResource.PROJECT) > 0) {
					SubProgressMonitor childMonitor = new SubProgressMonitor(scanMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
					IResource[] children = ((IContainer) resource).members();
					childMonitor.beginTask("", children.length);
					for (int i = children.length - 1; i >= 0; i--) {
						internalScan(project, children[i], new SubProgressMonitor(childMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
					}
					childMonitor.done();
				}
				else if ((resource.getType() & IResource.FILE) > 0) {
					scanFile(project, (IFile) resource, scanMonitor);
					scanMonitor.worked(1);
				}
				else {
					scanMonitor.worked(1);
				}
			}
			scanMonitor.done();
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
	}

	void internalScan(IResourceDelta delta, final IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		try {
			String name = delta.getFullPath().lastSegment();
			IResource resource = delta.getResource();
			if ((resource.getType() & IResource.FOLDER) > 0 || (resource.getType() & IResource.PROJECT) > 0) {
				IResourceDelta[] children = delta.getAffectedChildren();
				if (name.length() != 0 && name.charAt(0) != '.' && children.length > 0) {
					SubProgressMonitor childMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
					childMonitor.beginTask("", children.length);
					for (int i = children.length - 1; i >= 0; i--) {
						internalScan(children[i], new SubProgressMonitor(childMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
					}
					childMonitor.done();
				}
				else {
					monitor.worked(1);
				}
			}
			else if (name.length() != 0 && name.charAt(0) != '.' && (resource.getType() & IResource.FILE) > 0) {
				if ((delta.getKind() & IResourceDelta.ADDED) > 0 || ((delta.getKind() & IResourceDelta.CHANGED) > 0 && (delta.getFlags() & IResourceDelta.CONTENT) > 0)) {
					IFile file = (IFile) resource;
					scanFile(file.getProject(), file, monitor);
				}
				monitor.worked(1);
			}
			else {
				monitor.worked(1);
			}
		}
		catch (Exception e) {
			monitor.done();
			Logger.logException(e);
		}
	}

	void scan(final IProject project, final IProgressMonitor scanMonitor) {
		if (scanMonitor.isCanceled())
			return;
		if (_debug) {
			System.out.println(getClass().getName() + " scanning project " + project.getName()); //$NON-NLS-1$
		}
		if (_debugOverallPerf) {
			time0 = System.currentTimeMillis();
		}
		try {
			scanMonitor.beginTask("", project.members().length);
			internalScan(project, project, scanMonitor);
			shutdownDelegates(project);
			scanMonitor.done();
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		if (_debugOverallPerf) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + project.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void scan(IResourceDelta delta, final IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		if (_debugOverallPerf) {
			time0 = System.currentTimeMillis();
		}
		monitor.beginTask("", 1);
		internalScan(delta, monitor);
		shutdownDelegates(delta.getResource().getProject());
		monitor.done();
		if (_debugOverallPerf) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + delta.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void scanFile(IProject project, IFile file, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		ITaskScannerDelegate[] delegates = null;
		IContentType[] types = detectContentTypes(file);
		if (types != null) {
			List allDelegates = new ArrayList();
			for (int i = 0; i < types.length; i++) {
				ITaskScannerDelegate[] typeDelegates = registry.getTaskScannerDelegates(types[i].getId());
				if (typeDelegates != null && typeDelegates.length > 0) {
					allDelegates.addAll(Arrays.asList(typeDelegates));
				}
			}
			delegates = (ITaskScannerDelegate[]) allDelegates.toArray(new ITaskScannerDelegate[0]);
			if (delegates.length > 0) {
				SubProgressMonitor delegateMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				delegateMonitor.beginTask(file.getFullPath().toString(), delegates.length);
				for (int j = 0; delegates != null && j < delegates.length; j++) {
					if (delegateMonitor.isCanceled())
						continue;
					try {
						if (!fActiveDelegates.contains(delegates[j]) && !monitor.isCanceled()) {
							delegates[j].startup(file.getProject());
							fActiveDelegates.add(delegates[j]);
						}
						delegates[j].scan(file, delegateMonitor);
						delegateMonitor.worked(1);
					}
					catch (Exception e) {
						Logger.logException(file.getFullPath().toString(), e);
					}
				}
				delegateMonitor.done();
			}
			else {
				monitor.worked(1);
			}
		}
	}

	/**
	 * 
	 */
	private void shutdownDelegates(IProject project) {
		for (int j = 0; j < fActiveDelegates.size(); j++) {
			try {
				((ITaskScannerDelegate) fActiveDelegates.get(j)).shutdown(project);
			}
			catch (Exception e) {
				Logger.logException(project.getFullPath().toString(), e);
			}
		}
		fActiveDelegates = new ArrayList(1);
	}
}
