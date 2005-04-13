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
package org.eclipse.wst.sse.core.internal.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.ltk.builder.IBuilderDelegate;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;

public class StructuredDocumentBuilder extends IncrementalProjectBuilder implements IExecutableExtension {

	protected static final boolean _debugBuilder = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder")); //$NON-NLS-1$ //$NON-NLS-2$
	protected static final boolean _debugBuilderContentTypeDetection = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/detection")); //$NON-NLS-1$ //$NON-NLS-2$
	protected static final boolean _debugBuilderPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/time")); //$NON-NLS-1$ //$NON-NLS-2$
	private static final boolean performValidateEdit = false;

	private static boolean isGloballyEnabled = true;
	private static final String OFF = "off"; //$NON-NLS-1$
	static final boolean _debugResourceChangeListener = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/resourcechangehandling")); //$NON-NLS-1$ //$NON-NLS-2$

	protected static class ProjectChangeListener implements IResourceChangeListener, IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta.getResource() != null) {
				int resourceType = delta.getResource().getType();
				if (resourceType == IResource.PROJECT || resourceType == IResource.ROOT) {
					try {
						delta.accept(this);
					}
					catch (CoreException e) {
						Logger.logException("Exception managing buildspec list", e); //$NON-NLS-1$
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource != null) {
				if (resource.getType() == IResource.ROOT)
					return true;
				else if (resource.getType() == IResource.PROJECT) {
					if (delta.getKind() == IResourceDelta.ADDED) {
						if (_debugResourceChangeListener) {
							System.out.println("Project " + delta.getResource().getName() + " added to workspace and registering with SDMB");//$NON-NLS-2$//$NON-NLS-1$
						}
						add(new NullProgressMonitor(), (IProject) resource, null);
					}
					return false;
				}
			}
			return false;
		}
	}

	static {
		String build = System.getProperty(SSECorePlugin.STRUCTURED_BUILDER);
		isGloballyEnabled = (build == null || !build.equalsIgnoreCase(OFF));
	}

	/**
	 * Add the StructuredBuilder to the build spec of a single IProject
	 * 
	 * @param project -
	 *            the IProject to add to, when needed
	 */
	public static void add(IProgressMonitor monitor, IProject project, Object validateEditContext) {
		if (project == null || !project.isAccessible()) {
			return;
		}
		boolean isBuilderPresent = false;
		try {
			IFile descriptionFile = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
			if (descriptionFile.exists() && descriptionFile.isAccessible()) {
				IProjectDescription description = project.getDescription();
				ICommand[] commands = description.getBuildSpec();
				if (commands != null) {
					for (int i = 0; i < commands.length; i++) {
						String builderName = commands[i].getBuilderName();
						// builder name will be null if it has not been set
						if (builderName != null && builderName.equals(getBuilderId())) {
							isBuilderPresent = true;
							break;
						}
					}
				}
				if (!isBuilderPresent && !monitor.isCanceled()) {
					// validate for edit
					IStatus status = null;
					if (performValidateEdit) {
						ISchedulingRule validateEditRule = null;
						try {
							if (_debugBuilder) {
								System.out.println("Attempting validateEdit for " + descriptionFile.getFullPath().toString()); //$NON-NLS-1$
							}
							IFile[] validateFiles = new IFile[]{descriptionFile};
							IWorkspace workspace = descriptionFile.getWorkspace();
							validateEditRule = workspace.getRuleFactory().validateEditRule(validateFiles);
							Platform.getJobManager().beginRule(validateEditRule, monitor);
							status = workspace.validateEdit(validateFiles, null);
							if (_debugBuilder) {
								if (status.isOK()) {
									System.out.println("ValidateEdit completed for " + descriptionFile.getFullPath().toString()); //$NON-NLS-1$
								}
								else {
									System.out.println("ValidateEdit failed for " + descriptionFile.getFullPath().toString() + " " + status.getMessage()); //$NON-NLS-2$//$NON-NLS-1$
								}
							}
						}
						finally {
							if (validateEditRule != null) {
								Platform.getJobManager().endRule(validateEditRule);
							}
						}
					}
					if (status == null || status.isOK()) {
						// add the builder
						ICommand newCommand = description.newCommand();
						newCommand.setBuilderName(getBuilderId());
						ICommand[] newCommands = null;
						if (commands != null) {
							newCommands = new ICommand[commands.length + 1];
							System.arraycopy(commands, 0, newCommands, 0, commands.length);
							newCommands[commands.length] = newCommand;
						}
						else {
							newCommands = new ICommand[1];
							newCommands[0] = newCommand;
						}
						description.setBuildSpec(newCommands);
						/*
						 * This 'refresh' was added since unit tests were
						 * throwing exceptions about being out of sync. That
						 * may indicate a "deeper" problem such as needing to
						 * use scheduling rules, (although there don't appear
						 * to be examples of that) or something similar.
						 */
						// project.refreshLocal(IResource.DEPTH_ZERO,
						// subMonitorFor(monitor, 1,
						// IProgressMonitor.UNKNOWN));
						try {
							project.setDescription(description, subMonitorFor(monitor, 1, IProgressMonitor.UNKNOWN));
						}
						catch (CoreException e) {
							if (_debugBuilder) {
								if (performValidateEdit) {
									System.out.println("Description for project \"" + project.getName() + "\" could not be updated despite successful validateEdit"); //$NON-NLS-2$//$NON-NLS-1$
								}
								else {
									System.out.println("Description for project \"" + project.getName() + "\" could not be updated"); //$NON-NLS-2$//$NON-NLS-1$
								}
							}
							if (performValidateEdit) {
								Logger.log(Logger.WARNING, "Description for project \"" + project.getName() + "\" could not be updated despite successful validateEdit"); //$NON-NLS-2$//$NON-NLS-1$					
							}
							else {
								Logger.log(Logger.WARNING, "Description for project \"" + project.getName() + "\" could not be updated"); //$NON-NLS-2$//$NON-NLS-1$					
							}
						}
					}
				}
			}
			else {
				if (_debugBuilder) {
					System.out.println("Description for project \"" + project.getName() + "\" could not be updated"); //$NON-NLS-2$//$NON-NLS-1$
				}
				Logger.log(Logger.WARNING, "Description for project \"" + project.getName() + "\" could not be updated"); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		catch (Exception e) {
			// if we can't read the information, the project isn't open,
			// so it can't run auto-validate
			Logger.logException("Exception caught when adding Model Builder", e); //$NON-NLS-1$
		}
	}

	/**
	 * Adds the StructuredBuilder to every project in the Workspace
	 * 
	 * @param root
	 */
	public synchronized static void add(IProgressMonitor monitor, IWorkspaceRoot root, Object validateEditContext) {
		if (!isGloballyEnabled) {
			return;
		}
		IProject[] allProjects = root.getProjects();
		IProgressMonitor localMonitor = subMonitorFor(monitor, allProjects.length);
		localMonitor.beginTask(SSECoreMessages.StructuredDocumentBuilder_0, 1); //$NON-NLS-1$
		for (int i = 0; i < allProjects.length && !monitor.isCanceled(); i++) {
			add(localMonitor, allProjects[i], validateEditContext);
			localMonitor.worked(1);
		}
		localMonitor.done();
	}

	private static String getBuilderId() {
		return "org.eclipse.wst.sse.core.structuredbuilder"; //$NON-NLS-1$
	}

	public static IProgressMonitor monitorFor(IProgressMonitor monitor) {
		if (monitor == null)
			return new NullProgressMonitor();
		return monitor;
	}

	public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null)
			return new NullProgressMonitor();
		if (monitor instanceof NullProgressMonitor)
			return monitor;
		return new SubProgressMonitor(monitor, ticks);
	}

	public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks, int style) {
		if (monitor == null)
			return new NullProgressMonitor();
		if (monitor instanceof NullProgressMonitor)
			return monitor;
		return new SubProgressMonitor(monitor, ticks, style);
	}

	protected List fActiveDelegates = null;
	private String fName = "Structured Document Builder"; //$NON-NLS-1$
	protected BuilderParticipantRegistryReader registry = null;

	private long time0;
	private IResourceChangeListener changeListener;

	/**
	 * 
	 */
	public StructuredDocumentBuilder() {
		super();
		if (isGloballyEnabled) {
			registry = new BuilderParticipantRegistryReader();
			fActiveDelegates = new ArrayList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject currentProject = getProject();
		// Currently, just use the Task Tags preference
		boolean locallyEnabled = isGloballyEnabled && SSECorePlugin.getDefault().getPluginPreferences().getBoolean(CommonModelPreferenceNames.TASK_TAG_ENABLE);
		if (!locallyEnabled || currentProject == null || !currentProject.isAccessible()) {
			if (_debugBuilderPerf || _debugBuilder) {
				System.out.println(getClass().getName() + " skipping build of " + currentProject.getName()); //$NON-NLS-1$
			}
			return new IProject[]{currentProject};
		}

		if (_debugBuilderPerf || _debugBuilder) {
			time0 = System.currentTimeMillis();
		}
		IResourceDelta delta = getDelta(currentProject);
		IProgressMonitor localMonitor = subMonitorFor(monitor, 1);
		localMonitor.beginTask(getDisplayName(), 1);

		if (!localMonitor.isCanceled()) {
			// check the kind of delta if one was given
			if (kind == FULL_BUILD || kind == CLEAN_BUILD || delta == null) {
				doFullBuild(kind, args, localMonitor, getProject());
			}
			else {
				doIncrementalBuild(kind, args, localMonitor);
			}
		}
		localMonitor.worked(1);
		shutdownDelegates();
		localMonitor.done();
		if (_debugBuilderPerf || _debugBuilder) {
			if (kind == FULL_BUILD || delta == null) {
				System.out.println(getClass().getName() + " finished FULL build of " + currentProject.getName() //$NON-NLS-1$
							+ " in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				System.out.println(getClass().getName() + " finished INCREMENTAL/CLEAN/AUTO build of " + currentProject.getName() //$NON-NLS-1$
							+ " in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return new IProject[]{getProject()};
	}

	void build(int kind, Map args, IResource resource, IContentType[] types, IProgressMonitor monitor) {
		if (!monitor.isCanceled() && resource.getType() == IResource.FILE) {
			IBuilderDelegate[] delegates = null;
			List allDelegates = new ArrayList();
			for (int i = 0; i < types.length; i++) {
				IBuilderDelegate[] typeDelegates = registry.getBuilderDelegates(types[i].getId());
				if (typeDelegates != null && typeDelegates.length > 0) {
					allDelegates.addAll(Arrays.asList(typeDelegates));
				}
			}
			delegates = (IBuilderDelegate[]) allDelegates.toArray(new IBuilderDelegate[0]);
			for (int j = 0; delegates != null && j < delegates.length; j++) {
				if (kind != IncrementalProjectBuilder.CLEAN_BUILD) {
					monitor.subTask(getDisplayName() + " building " + resource.getFullPath()); //$NON-NLS-1$
				}
				try {
					if (!fActiveDelegates.contains(delegates[j]) && !monitor.isCanceled()) {
						delegates[j].startup(getProject(), kind, args);
						fActiveDelegates.add(delegates[j]);
					}
					delegates[j].build((IFile) resource, kind, args, subMonitorFor(monitor, 100));
				}
				catch (Exception e) {
					Logger.logException(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		if (_debugBuilderPerf || _debugBuilder) {
			time0 = System.currentTimeMillis();
		}
		super.clean(monitor);
		IProject currentProject = getProject();
		if (!isGloballyEnabled || currentProject == null || !currentProject.isAccessible()) {
			return;
		}
		doFullBuild(IncrementalProjectBuilder.CLEAN_BUILD, new HashMap(0), monitor, getProject());
		if (_debugBuilderPerf || _debugBuilder) {
			System.out.println(getClass().getName() + " finished CLEAN build of " + currentProject.getName() //$NON-NLS-1$
						+ " in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	IContentType[] detectContentTypes(IResource resource) {
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
				// should not be possible given the accessible and file type
				// check above
			}
			if (types == null) {
				types = Platform.getContentTypeManager().findContentTypesFor(resource.getName());
			}
			if (_debugBuilderContentTypeDetection) {
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
	 * Iterate through the list of resources and build each one
	 * 
	 * @param monitor
	 * @param resources
	 */
	protected void doFullBuild(int kind, Map args, IProgressMonitor monitor, IProject project) {
		if (_debugBuilder) {
			System.out.println(getClass().getName() + " building project " + project.getName()); //$NON-NLS-1$
		}

		final IProgressMonitor subMonitor = subMonitorFor(monitor, IProgressMonitor.UNKNOWN);
		final int localKind = kind;
		final Map localArgs = args;

		final IProgressMonitor visitorMonitor = monitor;
		IResourceVisitor internalBuilder = new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				if (resource.getType() == IResource.FILE) {
					// for any supported file type, record the resource
					IContentType[] contentTypes = detectContentTypes(resource);
					if (contentTypes != null) {
						build(localKind, localArgs, resource, contentTypes, subMonitor);
						visitorMonitor.worked(1);
					}
					return false;
				}
				else {
					return true;
				}
			}

		};
		try {
			project.accept(internalBuilder);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
	}

	/**
	 * 
	 */
	protected void doIncrementalBuild(int kind, Map args, IProgressMonitor monitor) {
		IResourceDelta projectDelta = getDelta(getProject());
		if (projectDelta == null) {
			throw new IllegalArgumentException("delta is null, should do a full build"); //$NON-NLS-1$
		}
		if (_debugBuilder) {
			if (projectDelta != null && projectDelta.getResource() != null) {
				System.out.println(getClass().getName() + " building " + projectDelta.getResource().getFullPath()); //$NON-NLS-1$
			}
			else {
				System.out.println(getClass().getName() + " building project " + getProject().getName()); //$NON-NLS-1$
			}
		}

		final Map localArgs = args;
		final int localKind = kind;
		final IProgressMonitor localMonitor = subMonitorFor(monitor, IProgressMonitor.UNKNOWN, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		IResourceDeltaVisitor participantVisitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) throws CoreException {
				if (!localMonitor.isCanceled() && delta.getResource().getType() == IResource.FILE) {
					IContentType[] contentTypes = detectContentTypes(delta.getResource());
					if (contentTypes != null)
						build(localKind, localArgs, delta.getResource(), contentTypes, localMonitor);
				}
				return delta.getAffectedChildren().length > 0;
			}
		};
		try {
			projectDelta.accept(participantVisitor);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		monitor.worked(1);
	}

	private String getDisplayName() {
		return fName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		if (config != null) {
			fName = config.getDeclaringExtension().getLabel();
		}
	}

	/**
	 * 
	 */
	private void shutdownDelegates() {
		for (int j = 0; j < fActiveDelegates.size(); j++) {
			try {
				((IBuilderDelegate) fActiveDelegates.get(j)).shutdown(getProject());
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
		fActiveDelegates = new ArrayList(1);
	}

	// make private if ever used again
	void doTaskTagProcessing() {
		// Must make sure the builder is registered for projects which may
		// have been created before this plugin was activated.
		Job adder = new WorkspaceJob(SSECoreMessages.ModelPlugin_0) { //$NON-NLS-1$
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				add(monitor, ResourcesPlugin.getWorkspace().getRoot(), null);
				return Status.OK_STATUS;
			}
		};
		adder.setSystem(true);
		// use SHORT, since once executing, this job should be quick
		adder.setPriority(Job.SHORT);
		// since we have potential to change several .project files,
		// we should wait until we can get exclusive access to
		// whole workspace.
		// TODO: future re-design should not require this.
		adder.setRule(ResourcesPlugin.getWorkspace().getRoot());
		adder.schedule();

		// Register the ProjectChangeListener so that it can add the
		// builder
		// to projects as needed
		changeListener = new ProjectChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(changeListener, IResourceChangeEvent.PRE_BUILD);
	}

	/**
	 * Only for use by ModelPlugin class
	 */
	public static void shutdown() {
	}

	/**
	 * Only for use by ModelPlugin class
	 */
	public static void startup() {
		// TODO:disabled for now
	}
}
