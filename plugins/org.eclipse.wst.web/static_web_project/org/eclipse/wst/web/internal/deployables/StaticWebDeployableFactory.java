/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.logger.proxy.Logger;
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

public class StaticWebDeployableFactory extends ProjectModuleFactoryDelegate {
	private static final String ID = "org.eclipse.wst.web.internal.deployables.static"; //$NON-NLS-1$
	protected ArrayList moduleDelegates = new ArrayList();

	/*
	 * @see DeployableProjectFactoryDelegate#getFactoryID()
	 */
	public String getFactoryId() {
		return ID;
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
	public ModuleDelegate getModuleDelegate(IModule module) {
		for (Iterator iter = moduleDelegates.iterator(); iter.hasNext();) {
			ModuleDelegate element = (ModuleDelegate) iter.next();
			if (module == element.getModule())
				return element;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModules()
	 */
	public IModule[] getModules() {
		if (projects == null || projects.isEmpty())
			cacheModules();
		int i = 0;
		Iterator modules = projects.keySet().iterator();
		IModule[] modulesArray = new IModule[projects.size()];
		while (modules.hasNext()) {
			IModule[] module = null;
			IProject project = (IProject) modules.next();
			module = (IModule[])projects.get(project);
			modulesArray[i++] = module[0];
		}
		return modulesArray;

	}

	protected List createModules(ModuleCoreNature nature) {
		IProject project = nature.getProject();
		List modules = new ArrayList(1); 
		StructureEdit moduleCore = null;
		try {
			IVirtualComponent component = ComponentCore.createComponent(project);
			modules = createModuleDelegates(component);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(moduleCore != null) 
				moduleCore.dispose();
		}
		return modules;
	}
	
	protected List createModuleDelegates(IVirtualComponent component) throws CoreException {
		StaticWebDeployable moduleDelegate = null;
		IModule module = null;
		List moduleList = new ArrayList();
		try {
			if(isValidModule(component.getProject())) {
				moduleDelegate = new StaticWebDeployable(component.getProject(),component);
				module = createModule(component.getName(), component.getName(), moduleDelegate.getType(), moduleDelegate.getVersion(), moduleDelegate.getProject());
				moduleList.add(module);
				moduleDelegate.initialize(module);
			}
			// adapt(moduleDelegate, (WorkbenchComponent) workBenchModules.get(i));
		} catch (Exception e) {
			Logger.getLogger().write(e);
		} finally {
			if (module != null) {
				if (getModuleDelegate(module) == null)
					moduleDelegates.add(moduleDelegate);
			}
		}
		return moduleList;

	}
	
	protected IModule[] createModules(IProject project) {

		// List modules = createModules(nature);
		ModuleCoreNature nature = null;
		try {
			nature = (ModuleCoreNature) project.getNature(IModuleConstants.MODULE_NATURE_ID);
		} catch (CoreException e) {
			Logger.getLogger().write(e);
		}
		if(nature == null) return new IModule[0];
		List modules = createModules(nature);
		if (modules == null)
			return new IModule[0];
		IModule[] moduleArray = new IModule[modules.size()];
		modules.toArray(moduleArray);
		return moduleArray;
	}
}