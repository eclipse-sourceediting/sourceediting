/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class StaticWebDeployableFactory extends ProjectModuleFactoryDelegate {
	private static final String ID = "org.eclipse.wst.web.internal.deployables.static"; //$NON-NLS-1$
	protected ArrayList moduleDelegates = new ArrayList();

	/*
	 * @see DeployableProjectFactoryDelegate#getFactoryID()
	 */
	public static String getFactoryId() {
		return ID;
	}
	
	/**
	 * Use {@link #createModules(IProject)} instead.
	 * @deprecated
	 * @param nature
	 * @return
	 */
	protected IModule[] createModules(ModuleCoreNature nature) {
		if(nature != null){
			return createModules(nature.getProject());
		}
		return null;
	}
	/**
	 * Returns true if the project represents a deployable project of this type.
	 * 
	 * @param project
	 *            org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	protected boolean isValidModule(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null)
				return false;
			IProjectFacet webFacet = ProjectFacetsManager.getProjectFacet(IModuleConstants.WST_WEB_MODULE);
			return facetedProject.hasProjectFacet(webFacet);
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModuleDelegate(org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		for (Iterator iter = moduleDelegates.iterator(); iter.hasNext();) {
			ModuleDelegate element = (ModuleDelegate) iter.next();
			if (module == element.getModule())
				return element;
		}
		return null;

	}

	@Override
	protected IModule[] createModules(IProject project) {
		IVirtualComponent component = ComponentCore.createComponent(project);
		if(component != null){
			try {
				return createModuleDelegates(component);
			} catch (CoreException e) {
				WSTWebPlugin.logError(e);
			}
		}
		return null;
	}

	protected IModule[] createModuleDelegates(IVirtualComponent component) throws CoreException {
		if(component == null){
			return null;
		}
		StaticWebDeployable moduleDelegate = null;
		IModule module = null;
		try {
			if(isValidModule(component.getProject())) {
				moduleDelegate = new StaticWebDeployable(component.getProject(),component);
				module = createModule(component.getName(), component.getName(), IModuleConstants.WST_WEB_MODULE, moduleDelegate.getVersion(), moduleDelegate.getProject());
				moduleDelegate.initialize(module);
			}
		} catch (Exception e) {
			WSTWebPlugin.logError(e);
		} finally {
			if (module != null) {
				if (getModuleDelegate(module) == null)
					moduleDelegates.add(moduleDelegate);
			}
		}
		if (module == null)
			return null;
		return new IModule[] {module};
	}
	
	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return a possibly empty array of paths
	 */
	@Override
	protected IPath[] getListenerPaths() {
		return new IPath[] {
			new Path(".project"), // nature //$NON-NLS-1$
			new Path(StructureEdit.MODULE_META_FILE_NAME), // component
			new Path(".settings/org.eclipse.wst.common.project.facet.core.xml") // facets //$NON-NLS-1$
		};
	}
}
