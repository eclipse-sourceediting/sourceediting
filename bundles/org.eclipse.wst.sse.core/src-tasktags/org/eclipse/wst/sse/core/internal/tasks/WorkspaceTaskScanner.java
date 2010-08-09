/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300443 - some constants aren't static final
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.eclipse.wst.sse.core.utils.StringUtils;

/**
 * Dispatcher for scanning based on deltas and requested projects
 */
class WorkspaceTaskScanner {
	private static WorkspaceTaskScanner _instance = null;
	static final String SYNTHETIC_TASK = "org.eclipse.wst.sse.task-synthetic";
	static final String MODIFICATION_STAMP = "org.eclipse.wst.sse.modification-stamp";

	static synchronized WorkspaceTaskScanner getInstance() {
		if (_instance == null) {
			_instance = new WorkspaceTaskScanner();
		}
		return _instance;
	}

	static final String DEFAULT_MARKER_TYPE = IFileTaskScanner.TASK_MARKER_ID;
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
			if (Logger.DEBUG_TASKSCONTENTTYPE) {
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

		if (Logger.DEBUG_TASKSPREFS) {
			System.out.println(getClass().getName() + " scan of " + resource.getFullPath() + ":" + proceed); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (proceed) {
			String[] tags = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_TAGS, null, lookupOrder));
			String[] priorities = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_PRIORITIES, null, lookupOrder));
			String[] currentIgnoreContentTypeIDs = StringUtils.unpack(preferencesService.getString(TaskTagPreferenceKeys.TASK_TAG_NODE, TaskTagPreferenceKeys.TASK_TAG_CONTENTTYPES_IGNORED, null, lookupOrder));
			if (Logger.DEBUG_TASKSPREFS) {
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
			if (resource.isAccessible() && !resource.isDerived() && !resource.isPhantom() && !resource.isTeamPrivateMember() && name.length() != 0 && name.charAt(0) != '.') {
				if ((resource.getType() & IResource.FOLDER) > 0 || (resource.getType() & IResource.PROJECT) > 0) {
					IResource[] children = ((IContainer) resource).members();
					scanMonitor.beginTask("", children.length); //$NON-NLS-1$
					for (int i = 0; i < children.length; i++) {
						internalScan(project, children[i], new SubProgressMonitor(scanMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
					}
					scanMonitor.done();
				}
				else if ((resource.getType() & IResource.FILE) > 0) {
					scanFile(project, fCurrentTaskTags, (IFile) resource, scanMonitor);
				}
			}
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
					monitor.beginTask("", children.length);
					if (name.length() != 0 && name.charAt(0) != '.' && children.length > 0) {
						for (int i = children.length - 1; i >= 0; i--) {
							internalScan(children[i], new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
						}
					}
					monitor.done();
				}
				else if ((resource.getType() & IResource.FILE) > 0) {
					if ((delta.getKind() & IResourceDelta.ADDED) > 0 || ((delta.getKind() & IResourceDelta.CHANGED) > 0 && (delta.getFlags() & IResourceDelta.CONTENT) > 0)) {
						IFile file = (IFile) resource;
						scanFile(file.getProject(), fCurrentTaskTags, file, monitor);
					}
				}
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
	}

	private void replaceTaskMarkers(final IFile file, final String[] markerTypes, final Map markerAttributeMaps[], IProgressMonitor monitor) {
		final IFile finalFile = file;
		if (file.isAccessible()) {
			try {
				IWorkspaceRunnable r = new IWorkspaceRunnable() {
					public void run(IProgressMonitor progressMonitor) throws CoreException {
						progressMonitor.beginTask("", 2);//$NON-NLS-1$
						try {
							/*
							 * Delete old Task markers (don't delete regular
							 * Tasks since that includes user-defined ones)
							 */
							for (int i = 0; i < markerTypes.length; i++) {
								if (IMarker.TASK.equals(markerTypes[i])) {
									// only remove if synthetic
									IMarker[] foundMarkers = file.findMarkers(markerTypes[i], true, IResource.DEPTH_ZERO);
									for (int j = 0; j < foundMarkers.length; j++) {
										if (foundMarkers[j].getAttribute(SYNTHETIC_TASK) != null) {
											foundMarkers[j].delete();
										}
									}
								}
								else {
									file.deleteMarkers(markerTypes[i], true, IResource.DEPTH_ZERO);
								}
							}
						}
						catch (CoreException e) {
							Logger.logException("exception deleting old tasks", e); //$NON-NLS-1$ 
						}
						finally {
							progressMonitor.worked(1);
						}
						if (markerAttributeMaps != null && markerAttributeMaps.length > 0) {
							if (Logger.DEBUG_TASKS) {
								System.out.println("" + markerAttributeMaps.length + " tasks for " + file.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							for (int i = 0; i < markerAttributeMaps.length; i++) {
								String specifiedMarkerType = (String) markerAttributeMaps[i].get(IMarker.TASK);
								IMarker marker = finalFile.createMarker(specifiedMarkerType);
								marker.setAttributes(markerAttributeMaps[i]);
								marker.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
								marker.setAttribute(MODIFICATION_STAMP, Long.toString(file.getModificationStamp()));
								if (IMarker.TASK.equals(specifiedMarkerType)) {
									// set to synthetic and make user editable
									marker.setAttribute(SYNTHETIC_TASK, true);
								}
							}
						}
						progressMonitor.worked(1);
						progressMonitor.done();
					}
				};
				if (file.isAccessible()) {
					finalFile.getWorkspace().run(r, ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(file), IWorkspace.AVOID_UPDATE, monitor);
				}
			}
			catch (CoreException e1) {
				Logger.logException(e1);
			}
			catch(OperationCanceledException e) {
				// not an error condition
			}
		}
	}

	void scan(final IProject project, final IProgressMonitor scanMonitor) {
		if (scanMonitor.isCanceled())
			return;
		if (Logger.DEBUG_TASKS) {
			System.out.println(getClass().getName() + " scanning project " + project.getName()); //$NON-NLS-1$
		}
		if (!project.isAccessible()) {
			if (Logger.DEBUG_TASKS) {
				System.out.println(getClass().getName() + " skipping inaccessible project " + project.getName()); //$NON-NLS-1$
			}
			return;
		}

		if (Logger.DEBUG_TASKSOVERALLPERF) {
			time0 = System.currentTimeMillis();
		}
		if (init(project)) {
			internalScan(project, project, scanMonitor);
			shutdownDelegates(project);
		}
		if (Logger.DEBUG_TASKSOVERALLPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + project.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	void scan(IResourceDelta delta, final IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		if (Logger.DEBUG_TASKSOVERALLPERF) {
			time0 = System.currentTimeMillis();
		}
		if (init(delta.getResource())) {
			internalScan(delta, monitor);
			shutdownDelegates(delta.getResource().getProject());
		}
		if (Logger.DEBUG_TASKSOVERALLPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + delta.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void scanFile(IProject project, TaskTag[] taskTags, IFile file, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		// 3 "stages"
		monitor.beginTask("", 8);//$NON-NLS-1$
		monitor.subTask(file.getFullPath().toString().substring(1));

		final List markerAttributes = new ArrayList();
		IContentType[] types = detectContentTypes(file);
		Set markerTypes = new HashSet(3);
		// Always included for safety and migration
		markerTypes.add(DEFAULT_MARKER_TYPE);
		monitor.worked(1);

		IFileTaskScanner[] fileScanners = null;
		IFileTaskScanner[] ignoredFileScanners = null;
		if (types != null) {
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
					else {
						ignoredFileScanners = registry.getFileTaskScanners(new IContentType[] {types[i]});
					}
				}
				fileScanners = registry.getFileTaskScanners((IContentType[]) validTypes.toArray(new IContentType[validTypes.size()]));
			}
			monitor.worked(1);
			if (ignoredFileScanners != null && ignoredFileScanners.length > 0) {
				for (int i = 0; i < ignoredFileScanners.length; i++)
					markerTypes.add(ignoredFileScanners[i].getMarkerType());
			}

			if (fileScanners.length > 0) {
				IProgressMonitor scannerMonitor = new SubProgressMonitor(monitor, 3, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				scannerMonitor.beginTask("", fileScanners.length); //$NON-NLS-1$
				for (int j = 0; fileScanners != null && j < fileScanners.length; j++) {
					if (monitor.isCanceled())
						continue;
					try {
						if (!fActiveScanners.contains(fileScanners[j]) && !monitor.isCanceled()) {
							fileScanners[j].startup(file.getProject());
							fActiveScanners.add(fileScanners[j]);
						}
						markerTypes.add(fileScanners[j].getMarkerType());
						Map[] taskMarkerAttributes = fileScanners[j].scan(file, taskTags, new SubProgressMonitor(scannerMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
						/*
						 * TODO: pool the marker results so there's only one
						 * operation creating them
						 */
						for (int i = 0; i < taskMarkerAttributes.length; i++) {
							if (!taskMarkerAttributes[i].containsKey(IMarker.TASK)) {
								taskMarkerAttributes[i].put(IMarker.TASK, fileScanners[j].getMarkerType());
							}
							taskMarkerAttributes[i].put(IMarker.SOURCE_ID, fileScanners[j].getClass().getName());
							markerAttributes.add(taskMarkerAttributes[i]);
						}
					}
					catch (Exception e) {
						Logger.logException(file.getFullPath().toString(), e);
					}
				}
				scannerMonitor.done();
			}
		}
		else {
			monitor.worked(4);
		}

		if (monitor.isCanceled())
			return;
		// only update markers if we ran a scanner on this file
		if (fileScanners != null && fileScanners.length > 0 ||
				ignoredFileScanners != null && ignoredFileScanners.length > 0) {
			IProgressMonitor markerUpdateMonitor = new SubProgressMonitor(monitor, 3, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
			if (markerAttributes != null)
			replaceTaskMarkers(file, (String[]) markerTypes.toArray(new String[markerTypes.size()]), (Map[]) markerAttributes.toArray(new Map[markerAttributes.size()]), markerUpdateMonitor);
		}
		else {
			monitor.worked(3);
		}
		monitor.done();
	}

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
