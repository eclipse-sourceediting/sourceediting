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
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.builder.IBuilderDelegate;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.nls.ResourceHandler;


public class StructuredDocumentBuilder extends IncrementalProjectBuilder implements IExecutableExtension {

	protected static final boolean _debugBuilder = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder")); //$NON-NLS-1$ //$NON-NLS-2$
	protected static final boolean _debugBuilderContentTypeDetection = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/detection")); //$NON-NLS-1$ //$NON-NLS-2$
	protected static final boolean _debugBuilderPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/time")); //$NON-NLS-1$ //$NON-NLS-2$

	protected static IModelManagerPlugin fPlugin = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		super.clean(monitor);
		doFullBuild(IncrementalProjectBuilder.CLEAN_BUILD, new HashMap(0), monitor, getProject());
	}

	IContentType[] detectContentTypes(IResource resource) {
		IContentType[] types = null;
		if (resource.getType() == IResource.FILE && resource.isAccessible()) {
			IContentDescription d = null;
			try {
				// optimized description lookup, might not succeed
				d = ((IFile) resource).getContentDescription();
				if (d != null)
					types = new IContentType[]{d.getContentType()};
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
	protected BuilderParticipantRegistryReader registry = null;
	private String fName = "Structured Document Builder"; //$NON-NLS-1$

	private long time0;

	/**
	 *  
	 */
	public StructuredDocumentBuilder() {
		super();
		registry = new BuilderParticipantRegistryReader();
		fActiveDelegates = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible())
			return new IProject[]{currentProject};

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
				// for any supported file type, record the resource
				IContentType[] contentTypes = detectContentTypes(resource);
				if (contentTypes != null) {
					build(localKind, localArgs, resource, contentTypes, subMonitor);
					visitorMonitor.worked(1);
				}
				return true;
			}

		};
		try {
			project.accept(internalBuilder);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
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

	private static String getBuilderId() {
		return "org.eclipse.wst.sse.core.structuredbuilder"; //$NON-NLS-1$
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
			catch (Throwable e) {
				Logger.logException(e);
			}
		}
		fActiveDelegates = new ArrayList(1);
	}

	/**
	 * Adds the StructuredBuilder to every project in the Workspace
	 * 
	 * @param root
	 */
	public synchronized static void add(IProgressMonitor monitor, IWorkspaceRoot root) {
		IProject[] allProjects = root.getProjects();
		IProgressMonitor localMonitor = subMonitorFor(monitor, allProjects.length);
		localMonitor.beginTask(ResourceHandler.getString("StructuredDocumentBuilder.0"), 1); //$NON-NLS-1$
		for (int i = 0; i < allProjects.length && !monitor.isCanceled(); i++) {
			add(localMonitor, allProjects[i]);
			localMonitor.worked(1);
		}
		localMonitor.done();
	}

	/**
	 * Add the StructuredBuilder to the build spec of a single IProject
	 * 
	 * @param project -
	 *            the IProject to add to, if needed
	 */
	public static void add(IProgressMonitor monitor, IProject project) {
		if (project == null || !project.isAccessible()) {
			return;
		}
		boolean isBuilderPresent = false;
		try {
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
				project.setDescription(description, new NullProgressMonitor());
			}
		}
		catch (CoreException e) {
			// if we can't read the information, the project isn't open,
			// so it can't run auto-validate
			Logger.logException("Exception adding Model Builder", e); //$NON-NLS-1$
		}
	}
}
