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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner;
import org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag;
import org.eclipse.wst.sse.core.internal.util.StringUtils;

/**
 * Dispatcher for scanning based on deltas and requested projects
 */
class WorkspaceTaskScanner {
	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean _debugContentTypeDetection = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/detection")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean _debugOverallPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/overalltime")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean _debugPreferences = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/preferences")); //$NON-NLS-1$ //$NON-NLS-2$

	private static WorkspaceTaskScanner _instance = null;

	static synchronized WorkspaceTaskScanner getInstance() {
		if (_instance == null) {
			_instance = new WorkspaceTaskScanner();
		}
		return _instance;
	}

	static String getTaskMarkerType() {
		return IFileTaskScanner.TASK_MARKER_ID;
	}

	private List fActiveScanners = null;
	private IContentType[] fCurrentIgnoreContentTypes = null;
	private TaskTag[] fCurrentTaskTags = null;
	private FileTaskScannerRegistryReader registry = null;

	private long time0;


	/**
	 * 
	 */
	private WorkspaceTaskScanner() {
		super();
		registry = FileTaskScannerRegistryReader.getInstance();
		fActiveScanners = new ArrayList();
		fCurrentTaskTags = new TaskTag[0];
		fCurrentIgnoreContentTypes = new IContentType[0];
	}

	private IContentType[] detectContentTypes(IResource resource) {
		IContentType[] types = null;
		if (resource.getType() == IResource.FILE && resource.isAccessible()) {
			types = Platform.getContentTypeManager().findContentTypesFor(resource.getName());
			if (types.length == 0) {
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
					 * should not be possible given the accessible and file
					 * type check above
					 */
				}
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

	/**
	 * @param resource
	 * @return
	 */
	private IProject getProject(IResource resource) {
		IProject project = null;
		if (resource.getType() == IResource.PROJECT) {
			project = (IProject) resource;
		}
		else {
			project = resource.getProject();
		}
		return project;
	}

	private boolean init(IResource resource) {
		IProject project = getProject(resource);

		IPreferencesService preferencesService = Platform.getPreferencesService();
		IScopeContext[] lookupOrder = new IScopeContext[]{new ProjectScope(project), new InstanceScope(), new DefaultScope()};

		boolean proceed = preferencesService.getBoolean(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_ENABLE, false, lookupOrder);

		if (_debugPreferences) {
			System.out.println(getClass().getName() + " scan of " + resource.getFullPath() + ":" + proceed); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (proceed) {
			String[] tags = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_TAGS, null, lookupOrder));
			String[] priorities = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_PRIORITIES, null, lookupOrder));
			String[] currentIgnoreContentTypeIDs = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_CONTENTTYPES_IGNORED, null, lookupOrder));
			if (_debugPreferences) {
				System.out.print(getClass().getName() + " tags: "); //$NON-NLS-1$
				for (int i = 0; i < tags.length; i++) {
					if (i > 0) {
						System.out.print(","); //$NON-NLS-1$
					}
					System.out.print(tags[i]);
				}
				System.out.println();
				System.out.print(getClass().getName() + " priorities: "); //$NON-NLS-1$
				for (int i = 0; i < priorities.length; i++) {
					if (i > 0) {
						System.out.print(","); //$NON-NLS-1$
					}
					System.out.print(priorities[i]);
				}
				System.out.println();
				System.out.print(getClass().getName() + " ignored content types: "); //$NON-NLS-1$
				for (int i = 0; i < currentIgnoreContentTypeIDs.length; i++) {
					if (i > 0) {
						System.out.print(","); //$NON-NLS-1$
					}
					System.out.print(currentIgnoreContentTypeIDs[i]);
				}
				System.out.println();
			}
			fCurrentIgnoreContentTypes = new IContentType[currentIgnoreContentTypeIDs.length];
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			for (int i = 0; i < currentIgnoreContentTypeIDs.length; i++) {
				fCurrentIgnoreContentTypes[i] = contentTypeManager.getContentType(currentIgnoreContentTypeIDs[i]);
			}
			int max = Math.min(tags.length, priorities.length);
			fCurrentTaskTags = new TaskTag[max];
			for (int i = 0; i < max; i++) {
				int priority = TaskTag.PRIORITY_NORMAL;
				try {
					priority = Integer.parseInt(priorities[i]);
				}
				catch (NumberFormatException e) {
					// default to normal priority
				}
				fCurrentTaskTags[i] = new TaskTag(tags[i], priority);
			}
		}
		return proceed;
	}

	void internalScan(final IProject project, final IResource resource, final IProgressMonitor scanMonitor) {
		if (scanMonitor.isCanceled())
			return;
		try {
			String name = resource.getName();
			if (!resource.isDerived() && !resource.isPhantom() && !resource.isTeamPrivateMember() && name.length() != 0 && name.charAt(0) != '.') {
				if ((resource.getType() & IResource.FOLDER) > 0 || (resource.getType() & IResource.PROJECT) > 0) {
					SubProgressMonitor childMonitor = new SubProgressMonitor(scanMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
					IResource[] children = ((IContainer) resource).members();
					childMonitor.beginTask("", children.length); //$NON-NLS-1$
					for (int i = 0; i < children.length; i++) {
						internalScan(project, children[i], childMonitor);
					}
					childMonitor.done();
				}
				else if ((resource.getType() & IResource.FILE) > 0) {
					scanFile(project, fCurrentTaskTags, (IFile) resource, scanMonitor);
					scanMonitor.worked(1);
				}
			}
			else {
				scanMonitor.worked(1);

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
			if (!resource.isDerived() && !resource.isPhantom() && !resource.isTeamPrivateMember() && name.length() != 0 && name.charAt(0) != '.') {
				if ((resource.getType() & IResource.FOLDER) > 0 || (resource.getType() & IResource.PROJECT) > 0) {
					IResourceDelta[] children = delta.getAffectedChildren();
					if (name.length() != 0 && name.charAt(0) != '.' && children.length > 0) {
						SubProgressMonitor childMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
						childMonitor.beginTask("", children.length); //$NON-NLS-1$
						for (int i = children.length - 1; i >= 0; i--) {
							internalScan(children[i], new SubProgressMonitor(childMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
						}
						childMonitor.done();
					}
					else {
						monitor.worked(1);
					}
				}
				else if ((resource.getType() & IResource.FILE) > 0) {
					if ((delta.getKind() & IResourceDelta.ADDED) > 0 || ((delta.getKind() & IResourceDelta.CHANGED) > 0 && (delta.getFlags() & IResourceDelta.CONTENT) > 0)) {
						IFile file = (IFile) resource;
						scanFile(file.getProject(), fCurrentTaskTags, file, monitor);
					}
					monitor.worked(1);
				}
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

	private void replaceMarkers(final IFile file, final Map markerAttributes[], IProgressMonitor monitor) {
		final IFile finalFile = file;
		if (file.isAccessible()) {
			try {
				IWorkspaceRunnable r = new IWorkspaceRunnable() {
					public void run(IProgressMonitor progressMonitor) throws CoreException {
						try {
							// Delete old Task markers
							file.deleteMarkers(getTaskMarkerType(), true, IResource.DEPTH_ZERO);
						}
						catch (CoreException e) {
							Logger.logException("exception deleting old tasks", e); //$NON-NLS-1$ 
						}
						if (markerAttributes != null && markerAttributes.length > 0) {
							if (_debug) {
								System.out.println("" + markerAttributes.length + " tasks for " + file.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							for (int i = 0; i < markerAttributes.length; i++) {
								IMarker marker = finalFile.createMarker(getTaskMarkerType());
								marker.setAttributes(markerAttributes[i]);
							}
						}
					}
				};
				finalFile.getWorkspace().run(r, file, IWorkspace.AVOID_UPDATE, monitor);
			}
			catch (CoreException e1) {
				Logger.logException(e1);
			}
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
			scanMonitor.beginTask("", project.members().length); //$NON-NLS-1$
			if (init(project)) {
				internalScan(project, project, scanMonitor);
				shutdownDelegates(project);
			}
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
		monitor.beginTask("", 1); //$NON-NLS-1$
		if (init(delta.getResource())) {
			internalScan(delta, monitor);
			shutdownDelegates(delta.getResource().getProject());
		}
		monitor.done();
		if (_debugOverallPerf) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + delta.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void scanFile(IProject project, TaskTag[] taskTags, IFile file, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		SubProgressMonitor scannerMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		scannerMonitor.setTaskName(file.getFullPath().toString());
		List markerAttributes = null;
		IContentType[] types = detectContentTypes(file);
		if (types != null) {
			IFileTaskScanner[] fileScanners = null;
			if (fCurrentIgnoreContentTypes.length == 0) {
				fileScanners = registry.getFileTaskScanners(types);
			}
			else {
				List validTypes = new ArrayList();
				// obtain a filtered list of delegates
				for (int i = 0; i < types.length; i++) {
					boolean ignoreContentType = false;
					for (int j = 0; j < fCurrentIgnoreContentTypes.length; j++) {
						ignoreContentType = ignoreContentType || types[i].isKindOf(fCurrentIgnoreContentTypes[j]);
					}
					if (!ignoreContentType) {
						validTypes.add(types[i]);
					}
				}
				fileScanners = registry.getFileTaskScanners((IContentType[]) validTypes.toArray(new IContentType[validTypes.size()]));
			}
			if (fileScanners.length > 0) {
				for (int j = 0; fileScanners != null && j < fileScanners.length; j++) {
					if (scannerMonitor.isCanceled())
						continue;
					scannerMonitor.beginTask("", fileScanners.length);
					try {
						if (!fActiveScanners.contains(fileScanners[j]) && !monitor.isCanceled()) {
							fileScanners[j].startup(file.getProject());
							fActiveScanners.add(fileScanners[j]);
						}
						Map[] taskMarkerAttributes = fileScanners[j].scan(file, taskTags, scannerMonitor);
						/*
						 * TODO: pool the marker results so there's only one
						 * operation creating them
						 */
						for (int i = 0; i < taskMarkerAttributes.length; i++) {
							if (markerAttributes == null) {
								markerAttributes = new ArrayList();
							}
							markerAttributes.add(taskMarkerAttributes[i]);
						}
						scannerMonitor.worked(1);
					}
					catch (Exception e) {
						Logger.logException(file.getFullPath().toString(), e);
					}
				}
				scannerMonitor.done();
			}
			else {
				monitor.worked(1);
			}
		}
		if (markerAttributes != null) {
			replaceMarkers(file, (Map[]) markerAttributes.toArray(new Map[markerAttributes.size()]), monitor);
		}
		else {
			replaceMarkers(file, null, monitor);
		}
	}

	/**
	 * 
	 */
	private void shutdownDelegates(IProject project) {
		for (int j = 0; j < fActiveScanners.size(); j++) {
			try {
				((IFileTaskScanner) fActiveScanners.get(j)).shutdown(project);
			}
			catch (Exception e) {
				Logger.logException(project.getFullPath().toString(), e);
			}
		}
		fActiveScanners = new ArrayList(1);
	}
}
