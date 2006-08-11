/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

public abstract class ComponentDeployable extends ProjectModule {

	protected IVirtualComponent component = null;
	protected List members = new ArrayList();
	
	public ComponentDeployable(IProject project) {
		this(project,ComponentCore.createComponent(project));
	}
	
	public ComponentDeployable(IProject project, IVirtualComponent aComponent) {
		super(project);
		this.component = aComponent;
	}
	
	/**
	 * @see org.eclipse.jst.server.core.IJ2EEModule#isBinary()
	 */
	public boolean isBinary() {
		if (component==null)
			return false;
		return component.isBinary();
	}
	
	private void addMembersToModuleFolder(ModuleFolder mf, IModuleResource[] mr) {
		if (mf == null) return;
		Set membersJoin = new HashSet();
		if (mf.members() != null)
			membersJoin.addAll(Arrays.asList(mf.members()));
		if (mr != null && mr.length > 0)
			membersJoin.addAll(Arrays.asList(mr));
		mf.setMembers((IModuleResource[]) membersJoin.toArray(new IModuleResource[membersJoin.size()]));
	}

	 /**
     * Returns the child modules of this module.
     * 
     * @return org.eclipse.wst.server.core.model.IModule[]
     */
    public IModule[] getChildModules() {
        return getModules();
    }
    
    public IModule[] getModules() {
		List modules = new ArrayList();
		if (component != null) {
	    	IVirtualReference[] components = component.getReferences();
	    	for (int i = 0; i < components.length; i++) {
				IVirtualReference reference = components[i];
				if (reference != null && reference.getDependencyType()==IVirtualReference.DEPENDENCY_TYPE_USES) {
					IVirtualComponent virtualComp = reference.getReferencedComponent();
					IModule module = gatherModuleReference(component, virtualComp);
					if (module != null && !modules.contains(module))
						modules.add(module);
				}
			}
		}
        return (IModule[]) modules.toArray(new IModule[modules.size()]);
	}
    
    protected IModule gatherModuleReference(IVirtualComponent component, IVirtualComponent targetComponent ) {
    	// Handle workspace project module components
		if (targetComponent != null && targetComponent.getProject()!=component.getProject()) {
			if (!targetComponent.isBinary())
				return ServerUtil.getModule(targetComponent.getProject());
		}
		return null;
    }
    
    /**
	 * Find the module resources for a given container and path. Inserts in the java containers
	 * at a given path if not null.
	 * 
	 * @param cont a container
	 * @param path the current module relative path
	 * @param javaPath the path where Java resources fit in the root
	 * @param javaCont
	 * @return a possibly-empty array of module resources
	 * @throws CoreException
	 */
	protected IModuleResource[] getMembers(IContainer cont, IPath path, IPath javaPath, IContainer[] javaCont) throws CoreException {
		IResource[] res = cont.members();
		int size2 = res.length;
		List list = new ArrayList(size2);
		for (int j = 0; j < size2; j++) {
			if (res[j] instanceof IContainer) {
				IContainer cc = (IContainer) res[j];
				
				IPath newPath = path.append(cc.getName()).makeRelative();
				// Retrieve already existing module folder if applicable
				ModuleFolder mf = (ModuleFolder) getExistingModuleResource(members,newPath);
				if (mf == null) {
					mf = new ModuleFolder(cc, cc.getName(), path);
					ModuleFolder parent = (ModuleFolder) getExistingModuleResource(members, path);
					if (path.isEmpty())
						members.add(mf);
					else {
						if (parent == null)
							parent = ensureParentExists(path, cc);
						addMembersToModuleFolder(parent, new IModuleResource[] {mf});
					}
				}
				IModuleResource[] mr = getMembers(cc, newPath, javaPath, javaCont);
				
				if (javaPath != null && newPath.isPrefixOf(javaPath))
					mr = handleJavaPath(path, javaPath, newPath, javaCont, mr, cc);

				addMembersToModuleFolder(mf, mr);
				
			} else {
				IFile f = (IFile) res[j];
				// Handle the default package case
				if (path.equals(javaPath)) {
					ModuleFolder mFolder = (ModuleFolder) getExistingModuleResource(members,javaPath);
					ModuleFile mFile = new ModuleFile(f, f.getName(), javaPath, f.getModificationStamp() + f.getLocalTimeStamp());
					if (mFolder != null)
						addMembersToModuleFolder(mFolder,new IModuleResource[]{mFile});
					else
						list.add(mFile);
				} else {
					ModuleFile mf = new ModuleFile(f, f.getName(), path, f.getModificationStamp() + f.getLocalTimeStamp());
					list.add(mf);
				}
			}
		}
		IModuleResource[] mr = new IModuleResource[list.size()];
		list.toArray(mr);
		return mr;
	}
	
	protected IModuleResource[] getMembers(IVirtualContainer cont, IPath path) throws CoreException {
		IVirtualResource[] res = cont.members();
		int size2 = res.length;
		List list = new ArrayList(size2);
		for (int j = 0; j < size2; j++) {
			if (res[j] instanceof IVirtualContainer) {
				IVirtualContainer cc = (IVirtualContainer) res[j];
				// Retrieve already existing module folder if applicable
				ModuleFolder mf = (ModuleFolder) getExistingModuleResource(members,path.append(new Path(cc.getName()).makeRelative()));
				if (mf == null) {
					mf = new ModuleFolder((IContainer)cc.getUnderlyingResource(), cc.getName(), path);
					ModuleFolder parent = (ModuleFolder) getExistingModuleResource(members, path);
					if (path.isEmpty())
						members.add(mf);
					else {
						if (parent == null)
							parent = ensureParentExists(path, (IContainer)cc.getUnderlyingResource());
						addMembersToModuleFolder(parent, new IModuleResource[] {mf});
					}
				}
				IModuleResource[] mr = getMembers(cc, path.append(cc.getName()));
				addMembersToModuleFolder(mf, mr);
			} else {
				IFile f = (IFile) res[j].getUnderlyingResource();
				if (!isFileInSourceContainer(f)) {
					ModuleFile mf = new ModuleFile(f, f.getName(), path, f.getModificationStamp() + f.getLocalTimeStamp());
					list.add(mf);
				}
			}
		}
		IModuleResource[] mr = new IModuleResource[list.size()];
		list.toArray(mr);
		return mr;
	}
	
	protected ModuleFolder ensureParentExists(IPath path, IContainer cc) {
		ModuleFolder parent = (ModuleFolder) getExistingModuleResource(members, path);
		if (parent == null) {
			String folderName = path.lastSegment();
			IPath folderPath = Path.EMPTY;
			if (path.segmentCount()>1)
				folderPath = path.removeLastSegments(1);
			parent = new ModuleFolder(cc, folderName, folderPath);
			if (path.segmentCount()>1)
				addMembersToModuleFolder(ensureParentExists(path.removeLastSegments(1),cc), new IModuleResource[] {parent});
			else
				members.add(parent);
		}
		return parent;
	}
	protected boolean isFileInSourceContainer(IFile file) {
		return false;
	}
	
	/**
	 * Check the current cache to see if we already have an existing module resource for
	 * the given path.
	 * @param aList
	 * @param path
	 * @return an existing moduleResource from the cached result
	 */
	 
	protected IModuleResource getExistingModuleResource(List aList, IPath path) { 
    	// If the list is empty, return null
    	if (aList==null || aList.isEmpty() || path == null)
    		return null;
    	// Otherwise recursively check to see if given resource matches current resource or if it is a child
    	String[] pathSegments = path.segments(); 
    	IModuleResource moduleResource = null;
    	
    	if(pathSegments.length == 0)
    		return null;
    	for (Iterator iter = aList.iterator(); iter.hasNext();) {
    		moduleResource = (IModuleResource) iter.next();     	
    		String[] moduleSegments = moduleResource.getModuleRelativePath().segments();
    		// If the last segment in passed in path equals the module resource name 
    		// and segment count is the same and the path segments start with the module path segments
    		// then we have a match and we return the existing moduleResource
    		if (pathSegments[pathSegments.length - 1].equals(moduleResource.getName()) && 
		    		(moduleSegments.length + 1) == pathSegments.length && 
		    		startsWith(moduleSegments, pathSegments))
		    	return moduleResource; 
    		
    		// Otherwise, if it is a folder, check its children for the existing resource path
    		// but only check if the beginning segments are a match
	    	if(moduleResource instanceof IModuleFolder && 
	    			startsWith(moduleSegments, pathSegments) && pathSegments.length > moduleSegments.length &&
	    			moduleResource.getName().equals(pathSegments[moduleSegments.length > 0 ? moduleSegments.length : 0]))	    	  
    			if (((IModuleFolder)moduleResource).members()!=null)
    				return getExistingModuleResource(Arrays.asList(((IModuleFolder)moduleResource).members()),path);		
    	}
    	return null;
    }
	
	/**
	 * 
	 * @param beginningSegments
	 * @param testSegments
	 * @return True if beginningSegments[i] == testSegments[i] for all 0<=i<beginningSegments[i] 
	 */
	private boolean startsWith(String[] beginningSegments, String[] testSegments) { 
		for(int i=0; i < beginningSegments.length; i++) {
			if(!beginningSegments[i].equals(testSegments[i]))
				return false;
		}
		return true;
	}

	protected IModuleResource[] handleJavaPath(IPath path, IPath javaPath, IPath curPath, IContainer[] javaCont, IModuleResource[] mr, IContainer cc) throws CoreException {
		//subclasses may override
		return new IModuleResource[]{};
	}
	
	public IModuleResource[] members() throws CoreException {
		members.clear();
		IVirtualComponent vc = ComponentCore.createComponent(getProject());
		if (vc != null) {
			IVirtualFolder vFolder = vc.getRootFolder();
			IModuleResource[] mr = getMembers(vFolder, Path.EMPTY);
			int size = mr.length;
			for (int j = 0; j < size; j++) {
				if (!members.contains(mr[j]))
					members.add(mr[j]);
			}
			addUtilMembers(vc);
		}
		
		IModuleResource[] mr = new IModuleResource[members.size()];
		members.toArray(mr);
		return mr;
	}
	
	protected boolean shouldIncludeUtilityComponent(IVirtualComponent virtualComp, IVirtualReference[] references, ArtifactEdit edit) {
		return virtualComp != null && virtualComp.isBinary() && virtualComp.getProject()==component.getProject();
	}
	
	protected void addUtilMembers(IVirtualComponent vc) {
		ArtifactEdit edit = null;
		try {
			edit = getComponentArtifactEditForRead();
			IVirtualReference[] components = vc.getReferences();
	    	for (int i = 0; i < components.length; i++) {
				IVirtualReference reference = components[i];
				IVirtualComponent virtualComp = reference.getReferencedComponent();
				if (shouldIncludeUtilityComponent(virtualComp,components,edit)) {
					IPath archivePath = ((VirtualArchiveComponent)virtualComp).getWorkspaceRelativePath();
					ModuleFile mf = null;
					if (archivePath != null) { //In Workspace
						IFile utilFile = ResourcesPlugin.getWorkspace().getRoot().getFile(archivePath);
						mf = new ModuleFile(utilFile, utilFile.getName(), reference.getRuntimePath().makeRelative());
					}
					else {
						File extFile = ((VirtualArchiveComponent)virtualComp).getUnderlyingDiskFile();
						mf = new ModuleFile(extFile, extFile.getName(), reference.getRuntimePath().makeRelative());
					}
					if (mf == null)
						continue;
					IModuleResource moduleParent = getExistingModuleResource(members, mf.getModuleRelativePath());
					
					if (moduleParent != null && moduleParent instanceof ModuleFolder)
						addMembersToModuleFolder((ModuleFolder)moduleParent, new IModuleResource[]{mf});
					else {
						if (mf.getModuleRelativePath().isEmpty())
							members.add(mf);
						else {
							if (moduleParent == null)
								moduleParent = ensureParentExists(mf.getModuleRelativePath(), (IContainer)vc.getRootFolder().getUnderlyingResource());
							addMembersToModuleFolder((ModuleFolder)moduleParent, new IModuleResource[] {mf});
						}
					}
				}
	    	}
		} finally {
			if (edit!=null)
				edit.dispose();
		}
	}
	
	protected ArtifactEdit getComponentArtifactEditForRead() {
		return null;
	}

	protected static boolean isProjectOfType(IProject project, String typeID) {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
		} catch (CoreException e) {
			return false;
		}
		
		if (facetedProject !=null && ProjectFacetsManager.isProjectFacetDefined(typeID)) {
			IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
			return projectFacet!=null && facetedProject.hasProjectFacet(projectFacet);
		}
		return false;
	}

	/**
	 * Returns the root folders for the resources in this module.
	 * 
	 * @return a possibly-empty array of resource folders
	 */
	public IContainer[] getResourceFolders() {
		IVirtualComponent vc = ComponentCore.createComponent(getProject());
		if (vc != null) {
			IVirtualFolder vFolder = vc.getRootFolder();
			if (vFolder != null)
				return vFolder.getUnderlyingFolders();
		}
		return new IContainer[]{};
	}

}
