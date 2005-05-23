/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.tasks;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;

public class TaskScanningScheduler {
	private class ListenerVisitor implements IResourceChangeListener, IResourceDeltaVisitor {
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta.getResource() != null) {
				int resourceType = delta.getResource().getType();
				if (resourceType == IResource.PROJECT || resourceType == IResource.ROOT) {
					try {
						delta.accept(this);
					}
					catch (CoreException e) {
						Logger.logException("Exception handling resource change", e); //$NON-NLS-1$
					}
				}
			}
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			if ((delta.getKind() & IResourceDelta.MARKERS) > 0 || (delta.getKind() & IResourceDelta.ENCODING) > 0 || (delta.getKind() & IResourceDelta.NO_CHANGE) > 0)
				return false;

			IResource resource = delta.getResource();
			if (resource != null) {
				if (resource.getType() == IResource.ROOT)
					return true;
				else if (resource.getType() == IResource.PROJECT) {
					fJob.addDelta(delta);
					return false;
				}
			}
			return false;
		}

	}

	private static TaskScanningScheduler scheduler;


	public static void refreshAll() {
		SSECorePlugin.getDefault().getPluginPreferences().setValue(ScanningJob.TASK_TAG_PROJECTS_ALREADY_SCANNED, ""); //$NON-NLS-1$
		scheduler.enqueue(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Only for use by SSECorePlugin class
	 */
	public static void shutdown() {
		if (scheduler != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(scheduler.visitor);
		}
	}

	/**
	 * Only for use by SSECorePlugin class
	 */
	public static void startup() {
		scheduler = new TaskScanningScheduler();

		/*
		 * According to
		 * http://www.eclipse.org/eclipse/development/performance/bloopers.html,
		 * POST_CHANGE listeners add a trivial performance cost
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(scheduler.visitor, IResourceChangeEvent.POST_CHANGE);

		scheduler.enqueue(ResourcesPlugin.getWorkspace().getRoot());
	}

	ScanningJob fJob = null;

	ListenerVisitor visitor = null;

	private TaskScanningScheduler() {
		super();
		fJob = new ScanningJob();
		visitor = new ListenerVisitor();
	}

	void enqueue(IWorkspaceRoot root) {
		IProject[] allProjects = root.getProjects();
		for (int i = 0; i < allProjects.length; i++) {
			fJob.addProject(allProjects[i]);
		}
	}
}
