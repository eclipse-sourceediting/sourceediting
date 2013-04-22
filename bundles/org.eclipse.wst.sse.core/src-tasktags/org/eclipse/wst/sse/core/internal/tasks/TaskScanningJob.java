/*******************************************************************************
 * Copyright (c) 2001, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (Intalio) - bug 300443 - some constants aren't static final
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.osgi.framework.Bundle;

/**
 * Queueing Job for processing deltas and projects.
 */
class TaskScanningJob extends Job {
	static final int JOB_DELAY_DELTA = 1000;
	private static final int JOB_DELAY_PROJECT = 5000;
	static final String TASK_TAG_PROJECTS_ALREADY_SCANNED = "task-tag-projects-already-scanned"; //$NON-NLS-1$
	/** List of scanned projects.  Since it's easily too long for a persistent property, and can be exported if stored in general preferences, store as a regular file. */
	static final IPath TASK_TAG_PROJECTS_ALREADY_SCANNED_LOCATION = SSECorePlugin.getDefault().getStateLocation().append("task-tags.properties"); //$NON-NLS-1$
	static final QualifiedName QTASK_TAG_PROJECTS_ALREADY_SCANNED = new QualifiedName(SSECorePlugin.ID, TASK_TAG_PROJECTS_ALREADY_SCANNED);
	private List fQueue = null;

	/** symbolic name for OSGI framework */
	private static final String OSGI_FRAMEWORK_ID = "org.eclipse.osgi"; //$NON-NLS-1$

	TaskScanningJob() {
		super(SSECoreMessages.TaskScanner_0);
		fQueue = new ArrayList();
		setPriority(Job.DECORATE);
		setSystem(true);
		setUser(false);
	}

	synchronized void addProjectDelta(IResourceDelta delta) {
		IResource projectResource = delta.getResource();

		if (projectResource.getType() == IResource.PROJECT) {
			if (!projectResource.isAccessible()) {
				String[] projectsPreviouslyScanned = getScannedProjects();
				HashSet updatedProjects = new HashSet(Arrays.asList(projectsPreviouslyScanned));
				updatedProjects.remove(projectResource.getName());
				setScannedProjects((String[]) updatedProjects.toArray(new String[updatedProjects.size()]));
			}
			if (isEnabledOnProject((IProject) projectResource)) {
				if (delta.getFlags() == IResourceDelta.OPEN) {
					addProject((IProject) projectResource);
				}
				else {
					fQueue.add(delta);
				}
				if (Logger.DEBUG_TASKSJOB) {
					String kind = null;
					switch (delta.getKind()) {
						case IResourceDelta.ADDED :
							kind = " [IResourceDelta.ADDED]"; //$NON-NLS-1$
							break;
						case IResourceDelta.CHANGED :
							kind = " [IResourceDelta.CHANGED]"; //$NON-NLS-1$
							break;
						case IResourceDelta.REMOVED :
							kind = " [IResourceDelta.REMOVED]"; //$NON-NLS-1$
							break;
						case IResourceDelta.ADDED_PHANTOM :
							kind = " [IResourceDelta.ADDED_PHANTOM]"; //$NON-NLS-1$
							break;
						case IResourceDelta.REMOVED_PHANTOM :
							kind = " [IResourceDelta.REMOVED_PHANTOM]"; //$NON-NLS-1$
							break;
					}
					System.out.println("Adding delta " + delta.getFullPath() + kind); //$NON-NLS-1$
				}
				schedule(JOB_DELAY_DELTA);
			}
		}
	}

	synchronized void addProject(IProject project) {
		if (projectHasNotBeenFullyScanned(project)) {
			fQueue.add(project);
			if (Logger.DEBUG_TASKSJOB) {
				System.out.println("Adding project " + project.getName()); //$NON-NLS-1$
			}
			schedule(JOB_DELAY_PROJECT);
		}
	}

	/**
	 * A check to see if the OSGI framework is shutting down.
	 * 
	 * @return true if the System Bundle is stopped (ie. the framework is
	 *         shutting down)
	 */
	boolean frameworkIsShuttingDown() {
		// in the Framework class there's a note:
		// set the state of the System Bundle to STOPPING.
		// this must be done first according to section 4.19.2 from the OSGi
		// R3 spec.
		boolean shuttingDown = !Platform.isRunning() || Platform.getBundle(OSGI_FRAMEWORK_ID).getState() == Bundle.STOPPING;
		if (Logger.DEBUG_TASKSJOB && shuttingDown) {
			System.out.println("TaskScanningJob: system is shutting down!"); //$NON-NLS-1$
		}
		return shuttingDown;
	}
	
	private boolean isEnabledOnProject(IProject p) {
		IPreferencesService preferencesService = Platform.getPreferencesService();
		IScopeContext[] lookupOrder = new IScopeContext[]{new ProjectScope(p), new InstanceScope(), new DefaultScope()};
		return preferencesService.getBoolean(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_ENABLE, false, lookupOrder);
	}

	private boolean projectHasNotBeenFullyScanned(IResource project) {
		String[] projectsScanned = getScannedProjects();

		boolean shouldScan = true;
		String name = project.getName();
		for (int j = 0; shouldScan && j < projectsScanned.length; j++) {
			if (projectsScanned[j].equals(name)) {
				if (Logger.DEBUG_TASKSJOB)
					System.out.println("Scanning Job skipping " + project.getName()); //$NON-NLS-1$
				shouldScan = false;
			}
		}
		return shouldScan;
	}

	synchronized List retrieveQueue() {
		List queue = fQueue;
		fQueue = new ArrayList();
		return queue;
	}

	protected IStatus run(IProgressMonitor monitor) {
		if (frameworkIsShuttingDown())
			return Status.CANCEL_STATUS;

		cleanupRememberedProjectList();

		IStatus status = null;
		List currentQueue = retrieveQueue();
		
		try {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		}
		catch (SecurityException e) {
			// not a critical problem
		}
		
		List errors = null;
		int ticks = currentQueue.size();
		String taskName = null;
		if (Logger.DEBUG_TASKSJOB) {
			taskName = SSECoreMessages.TaskScanningJob_0 + " (" + ticks + " work items)"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			taskName = SSECoreMessages.TaskScanningJob_0;
		}
		monitor.beginTask(taskName, ticks);

		IProgressMonitor scanMonitor = null;
		while (!currentQueue.isEmpty()) {
			Object o = currentQueue.remove(0);
			if (frameworkIsShuttingDown() || monitor.isCanceled())
				return Status.CANCEL_STATUS;
			try {
				scanMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				if (o instanceof IResourceDelta) {
					WorkspaceTaskScanner.getInstance().scan((IResourceDelta) o, scanMonitor);
				}
				else if (o instanceof IProject) {
					WorkspaceTaskScanner.getInstance().scan((IProject) o, scanMonitor);
					if (!scanMonitor.isCanceled()) {
						String[] projectsPreviouslyScanned = getScannedProjects();
						HashSet updatedProjects = new HashSet(Arrays.asList(projectsPreviouslyScanned));
						updatedProjects.add(((IProject) o).getName());
						setScannedProjects((String[]) updatedProjects.toArray(new String[updatedProjects.size()]));
					}
					if (!((IProject) o).isAccessible()) {
						String[] projectsPreviouslyScanned = getScannedProjects();
						HashSet updatedProjects = new HashSet(Arrays.asList(projectsPreviouslyScanned));
						updatedProjects.remove(((IProject) o).getName());
						setScannedProjects((String[]) updatedProjects.toArray(new String[updatedProjects.size()]));
					}
				}
			}
			catch (Exception e) {
				if (errors == null) {
					errors = new ArrayList();
				}
				errors.add(new Status(IStatus.ERROR, SSECorePlugin.ID, IStatus.ERROR, "", e)); //$NON-NLS-1$
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
				status = new MultiStatus(SSECorePlugin.ID, IStatus.ERROR, statii, SSECoreMessages.TaskScanningJob_1, null);
			}
		}

		SSECorePlugin.getDefault().savePluginPreferences();
		return status;
	}

	private void cleanupRememberedProjectList() {
		String[] rememberedProjectNames = getScannedProjects();
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
			else if (Logger.DEBUG_TASKSJOB) {
				System.out.println("Removing " + rememberedProjectNames[i] + " removed from " + TASK_TAG_PROJECTS_ALREADY_SCANNED); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (projectNamesToRemember.size() != rememberedProjectNames.length) {
			setScannedProjects((String[]) projectNamesToRemember.toArray(new String[projectNamesToRemember.size()]));
		}
	}
	
	static String[] getScannedProjects() {
		String rawValue = null;
		File file = TASK_TAG_PROJECTS_ALREADY_SCANNED_LOCATION.toFile();
		if (file.canRead()) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(file));
				rawValue = props.getProperty(TASK_TAG_PROJECTS_ALREADY_SCANNED);
				props = null;
			}
			catch (IOException e) {
				Logger.logException(e);
			}
		}
		if (rawValue != null)
			return StringUtils.unpack(rawValue);
		return new String[0];
	}

	static void setScannedProjects(String[] projectNames) {
		if (Logger.DEBUG_TASKSJOB)
			System.out.println("Task scanned projects set to " + StringUtils.pack(projectNames)); //$NON-NLS-1$

		File file = TASK_TAG_PROJECTS_ALREADY_SCANNED_LOCATION.toFile();
		if (!file.exists() || file.canWrite()) {
			Properties props = new Properties();
			props.setProperty(TASK_TAG_PROJECTS_ALREADY_SCANNED, StringUtils.pack(projectNames));
			try {
				props.store(new FileOutputStream(file), ""); //$NON-NLS-1$
			}
			catch (IOException e) {
				Logger.logException(e);
			}
			props = null;
		}
		else {
			Logger.log(Logger.WARNING, "Could not write task tags scanned list to " + TASK_TAG_PROJECTS_ALREADY_SCANNED_LOCATION.toOSString()); //$NON-NLS-1$
		}
	}
}
