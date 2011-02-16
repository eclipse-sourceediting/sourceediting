/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.jsdt.core.IIncludePathAttribute;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.core.ClasspathEntry;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.internal.core.util.Messages;
import org.eclipse.wst.jsdt.web.core.internal.project.ModuleSourcePathProvider;
import org.eclipse.wst.sse.core.indexing.AbstractIndexManager;

/**
 * <p>This is an implementation of the {@link AbstractIndexManager} for the JavaScript Web core plugin.</p>
 * 
 * <p>Current Uses:
 * <ul>
 * <li>listen for .project changes so that JavaScript class paths can be updated
 * if the module core nature is added to a project</li>
 * </ul></p>
 * 
 * <p><b>NOTE:</b> If any other file resource change listening needs to take place in the future
 * in this plugin it should be done here.</p>
 */
public class JSWebResourceEventManager extends AbstractIndexManager {
	/** the singleton instance of the {@link JSWebResourceEventManager} */
	private static JSWebResourceEventManager INSTANCE;
	
	/** the name of the ".project" file where natures are stored */
	private static final String DOT_PROJECT_FILE_NAME = ".project"; //$NON-NLS-1$
	
	/** the location to store state */
	private IPath fWorkingLocation;
	
	/**
	 * <p>Private constructor for the resource event manager</p>
	 */
	private JSWebResourceEventManager() {
		super(Messages.build_analyzingDeltas, Messages.build_analyzingDeltas,
				Messages.javamodel_initialization, Messages.manager_filesToIndex);
	}
	
	/**
	 * @return the singleton instance of the {@link JSWebResourceEventManager}
	 */
	public static JSWebResourceEventManager getDefault() {
		return INSTANCE != null ? INSTANCE : (INSTANCE = new JSWebResourceEventManager());
	}

	/**
	 * @see org.eclipse.wst.sse.core.indexing.AbstractIndexManager#isResourceToIndex(int, org.eclipse.core.runtime.IPath)
	 */
	protected boolean isResourceToIndex(int type, IPath path) {
		return 
			type == IResource.ROOT ||
			type == IResource.PROJECT || 
			(type == IResource.FILE && DOT_PROJECT_FILE_NAME.equals(path.lastSegment()));
	}

	/**
	 * @see org.eclipse.wst.sse.core.indexing.AbstractIndexManager#performAction(byte, byte, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IPath)
	 */
	protected void performAction(byte source, byte action, IResource resource,
			IPath movePath) {
		
		switch(action) {
			case(AbstractIndexManager.ACTION_ADD): {
				if(resource.getName().equals(DOT_PROJECT_FILE_NAME)) {
					updateClassPathEntries(resource.getProject());
				}
				break;
			}
		}

	}

	/**
	 * @see org.eclipse.wst.sse.core.indexing.AbstractIndexManager#getWorkingLocation()
	 */
	protected IPath getWorkingLocation() {
		if(this.fWorkingLocation == null) {
			//create path to working area
    		IPath workingLocation =
    			JsCorePlugin.getDefault().getStateLocation().append("JSWebResourceEventManager"); //$NON-NLS-1$

            // ensure that it exists on disk
            File folder = new File(workingLocation.toOSString());
    		if (!folder.isDirectory()) {
    			try {
    				folder.mkdir();
    			}
    			catch (SecurityException e) {
    				Logger.logException(this.getName() +
    						": Error while creating state location: " + folder + //$NON-NLS-1$
    						" This renders the index manager irrevocably broken for this workspace session", //$NON-NLS-1$
    						e);
    			}
    		}
    		
    		this.fWorkingLocation = workingLocation;
    	}
    	
        return this.fWorkingLocation;
	}
	
	/**
	 * <p>Updates the JavaScript class path entries for the given project if
	 * both the Module core and JavaScript natures are installed on that project.</p>
	 *
	 * @param project {@link IProject} to update the JavaScript class path entries for
	 */
	private static void updateClassPathEntries(IProject project) {
		try {
			/*
			 * if a JS project with Module Core nature, check if include path
			 * needs to be updated
			 */
			if (project.hasNature(JavaScriptCore.NATURE_ID) && ModuleCoreNature.isFlexibleProject(project)) {
				JavaProject jsProject = (JavaProject) JavaScriptCore.create(project);

				IIncludePathEntry[] oldEntries = jsProject.getRawIncludepath();
				List updatedEntries = new ArrayList();
				boolean updateIncludePath = false;

				for (int oldEntry = 0; oldEntry < oldEntries.length; ++oldEntry) {
					IIncludePathAttribute[] entryAttributes = oldEntries[oldEntry].getExtraAttributes();

					boolean isProvidedEntry = false;
					for (int attribute = 0; attribute < entryAttributes.length; ++attribute) {
						isProvidedEntry = entryAttributes[attribute].getName().equals(ModuleSourcePathProvider.PROVIDER_ATTRIBUTE_KEY_NAME) && entryAttributes[attribute].getValue().equals(ModuleSourcePathProvider.PROVIDER_ATTRIBUTE_KEY_VALUE);
						updateIncludePath |= isProvidedEntry;
						if (isProvidedEntry) {
							/*
							 * create updated exclusion paths that are not
							 * relative to the parent entry
							 */
							IPath[] nonRelativeExclusionPaths = oldEntries[oldEntry].getExclusionPatterns();
							for (int i = 0; i < nonRelativeExclusionPaths.length; ++i) {
								nonRelativeExclusionPaths[i] = oldEntries[oldEntry].getPath().append(nonRelativeExclusionPaths[i]);
							}

							/*
							 * create updated inclusion paths that are not
							 * relative to the parent entry
							 */
							IPath[] nonRelativeInclusionPaths = oldEntries[oldEntry].getInclusionPatterns();
							for (int i = 0; i < nonRelativeInclusionPaths.length; ++i) {
								nonRelativeInclusionPaths[i] = oldEntries[oldEntry].getPath().append(nonRelativeInclusionPaths[i]);
							}

							IResource[] roots = getRoots(project);
							for (int root = 0; root < roots.length; ++root) {
								IPath rootPath = roots[root].getFullPath();

								/*
								 * find matching pre-existing exclusion
								 * patterns
								 */
								List exclusionPatterns = new ArrayList();
								for (int i = 0; i < nonRelativeExclusionPaths.length; ++i) {
									IPath parentRelativeExclusionPattern = PathUtils.makePatternRelativeToParent(nonRelativeExclusionPaths[i], rootPath);
									if (parentRelativeExclusionPattern != null) {
										exclusionPatterns.add(parentRelativeExclusionPattern);
									}
								}

								/*
								 * find matching pre-existing inclusion
								 * patterns
								 */
								List inclusionPatterns = new ArrayList();
								for (int i = 0; i < nonRelativeInclusionPaths.length; ++i) {
									IPath parentRelativeInclusionPattern = PathUtils.makePatternRelativeToParent(nonRelativeInclusionPaths[i], rootPath);
									if (parentRelativeInclusionPattern != null) {
										inclusionPatterns.add(parentRelativeInclusionPattern);
									}
								}

								// create new inclusion/exclusion rules
								IPath[] exclusionPaths = exclusionPatterns.isEmpty() ? ClasspathEntry.EXCLUDE_NONE : (IPath[]) exclusionPatterns.toArray(new IPath[exclusionPatterns.size()]);
								IPath[] inclusionPaths = inclusionPatterns.isEmpty() ? ClasspathEntry.INCLUDE_ALL : (IPath[]) inclusionPatterns.toArray(new IPath[inclusionPatterns.size()]);

								IIncludePathEntry newEntry = JavaScriptCore.newSourceEntry(rootPath, inclusionPaths, exclusionPaths, null);
								updatedEntries.add(newEntry);
							}
						}
					}
					if (!isProvidedEntry) {
						updatedEntries.add(oldEntries[oldEntry]);
					}
				}

				/*
				 * if found that a default source path was added, replace with
				 * module core determined path
				 */
				if (updateIncludePath) {
					// commit the updated include path
					jsProject.setRawIncludepath((IIncludePathEntry[]) updatedEntries.toArray(new IIncludePathEntry[updatedEntries.size()]), jsProject.getOutputLocation(), null);
				}
			}
		}
		catch (CoreException e) {
			Logger.logException("Error while updating JavaScript includepath", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * <p>Uses module core to get the roots of the given project.</p>
	 *
	 * @param project find the module core roots for this {@link IProject}
	 * @return the module core roots for the given {@link IProject
	 */
	private static IResource[] getRoots(IProject project) {
		IVirtualFile root = ComponentCore.createFile(project, Path.ROOT);
		IResource[] underlyingResources = root.getUnderlyingResources();
		if (underlyingResources == null || underlyingResources.length == 0) {
			underlyingResources = new IResource[]{root.getUnderlyingResource()};
		}
		
		return underlyingResources;
	}
}