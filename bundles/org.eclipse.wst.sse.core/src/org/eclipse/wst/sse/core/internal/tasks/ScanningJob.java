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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.internal.util.StringUtils;

/**
 * Queueing Job for processing deltas and projects.
 */
class ScanningJob extends Job {
	public static final boolean _debugJob = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/job"));
	static final String TASK_TAG_PROJECTS_ALREADY_SCANNED = "task-tag-projects-already-scanned"; //$NON-NLS-1$
	private List fQueue = null;

	ScanningJob() {
		super(SSECoreMessages.TaskScanner_0);
		fQueue = new ArrayList();
		setPriority(Job.DECORATE);
		setSystem(false);

		SSECorePlugin.getDefault().getPluginPreferences().setDefault(CommonModelPreferenceNames.TASK_TAG_PROJECTS_IGNORED, "");
		SSECorePlugin.getDefault().getPluginPreferences().setDefault(TASK_TAG_PROJECTS_ALREADY_SCANNED, "");
	}

	synchronized void addDelta(IResourceDelta delta) {
		if (!isIgnoredProject(delta)) {
			fQueue.add(delta);
			if (_debugJob)
				System.out.println("Adding delta " + delta.getFullPath() + " " + delta.getKind());
			schedule(100);
		}
	}

	synchronized void addProject(IProject project) {
		if (isEnabledProject(project)) {
			fQueue.add(project);
			if (_debugJob)
				System.out.println("Adding project " + project.getName());
			schedule(600);
		}
	}

	private boolean isEnabledProject(IResource project) {
		String[] projectsIgnored = StringUtils.unpack(SSECorePlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PROJECTS_IGNORED));
		String[] projectsScanned = StringUtils.unpack(SSECorePlugin.getDefault().getPluginPreferences().getString(TASK_TAG_PROJECTS_ALREADY_SCANNED));

		boolean shouldScan = true;
		String name = project.getName();
		for (int j = 0; shouldScan && j < projectsIgnored.length; j++) {
			if (projectsIgnored[j].equals(name)) {
				if (_debugJob)
					System.out.println("Scanning Job ignoring " + project.getName());
				shouldScan = false;
			}
		}
		for (int j = 0; shouldScan && j < projectsScanned.length; j++) {
			if (projectsScanned[j].equals(name)) {
				if (_debugJob)
					System.out.println("Scanning Job skipping " + project.getName());
				shouldScan = false;
			}
		}
		return shouldScan;
	}

	private boolean isIgnoredProject(IResourceDelta delta) {
		IResource resource = delta.getResource();
		boolean ignore = false;
		if (resource.getType() == IResource.PROJECT) {
			String[] projectsIgnored = StringUtils.unpack(SSECorePlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PROJECTS_IGNORED));
			String name = resource.getName();
			for (int j = 0; !ignore && j < projectsIgnored.length; j++) {
				if (projectsIgnored[j].equals(name)) {
					if (_debugJob)
						System.out.println("Scanning Job ignoring " + resource.getName());
					ignore = true;
				}
			}
		}
		return ignore;
	}

	synchronized List retrieveQueue() {
		List queue = fQueue;
		fQueue = new ArrayList();
		return queue;
	}

	protected IStatus run(IProgressMonitor monitor) {
		validateRememberedProjectList(CommonModelPreferenceNames.TASK_TAG_PROJECTS_IGNORED);
		validateRememberedProjectList(TASK_TAG_PROJECTS_ALREADY_SCANNED);

		IStatus status = null;
		List currentQueue = retrieveQueue();
		List errors = null;
		int ticks = currentQueue.size();
		String taskName = null;
		if (_debugJob) {
			taskName = "Scanning (" + ticks + " work items)";
		}
		else {
			taskName = "Scanning";
		}
		monitor.beginTask(taskName, ticks);

		IProgressMonitor scanMonitor = null;
		while (!currentQueue.isEmpty()) {
			Object o = currentQueue.remove(0);
			try {
				scanMonitor = new SubProgressMonitor(monitor, 1);
				if (o instanceof IResourceDelta) {
					TaskScanner.getInstance().scan((IResourceDelta) o, scanMonitor);
				}
				else if (o instanceof IProject) {
					TaskScanner.getInstance().scan((IProject) o, scanMonitor);
					String[] projectsPreviouslyScanned = StringUtils.unpack(SSECorePlugin.getDefault().getPluginPreferences().getString(TASK_TAG_PROJECTS_ALREADY_SCANNED));
					String[] updatedProjects = new String[projectsPreviouslyScanned.length + 1];
					updatedProjects[projectsPreviouslyScanned.length] = ((IResource) o).getName();
					System.arraycopy(projectsPreviouslyScanned, 0, updatedProjects, 0, projectsPreviouslyScanned.length);
					SSECorePlugin.getDefault().getPluginPreferences().setValue(TASK_TAG_PROJECTS_ALREADY_SCANNED, StringUtils.pack(updatedProjects));
				}
			}
			catch (Exception e) {
				if (errors == null) {
					errors = new ArrayList();
				}
				errors.add(new Status(IStatus.ERROR, SSECorePlugin.ID, IStatus.ERROR, "", e));
			}
		}
		monitor.done();

		if (errors == null || errors.isEmpty()) {
			status = Status.OK_STATUS;
		}
		else {
			if (errors.size() == 1) {
				status = (IStatus) errors.get(0);
			}
			else {
				IStatus[] statii = (IStatus[]) errors.toArray(new IStatus[errors.size()]);
				status = new MultiStatus(SSECorePlugin.ID, IStatus.ERROR, statii, "Errors while detecting Tasks", null);
			}
		}

		SSECorePlugin.getDefault().savePluginPreferences();
		return status;
	}

	private void validateRememberedProjectList(String preferenceName) {
		String[] rememberedProjectNames = StringUtils.unpack(SSECorePlugin.getDefault().getPluginPreferences().getString(preferenceName));
		IResource[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		String[] projectNames = new String[workspaceProjects.length];
		for (int i = 0; i < projectNames.length; i++) {
			projectNames[i] = workspaceProjects[i].getName();
		}

		List projectNamesToRemember = new ArrayList(rememberedProjectNames.length);
		for (int i = 0; i < rememberedProjectNames.length; i++) {
			boolean rememberedProjectExists = false;
			for (int j = 0; !rememberedProjectExists && j < projectNames.length; j++) {
				if (rememberedProjectNames[i].equals(projectNames[j])) {
					rememberedProjectExists = true;
				}
			}
			if (rememberedProjectExists) {
				projectNamesToRemember.add(rememberedProjectNames[i]);
			}
			else if (_debugJob) {
				System.out.println("Removing " + rememberedProjectNames[i] + " removed from " + preferenceName);
			}
		}

		if (projectNamesToRemember.size() != rememberedProjectNames.length) {
			SSECorePlugin.getDefault().getPluginPreferences().setValue(preferenceName, StringUtils.pack((String[]) projectNamesToRemember.toArray(new String[projectNamesToRemember.size()])));
		}
	}
}