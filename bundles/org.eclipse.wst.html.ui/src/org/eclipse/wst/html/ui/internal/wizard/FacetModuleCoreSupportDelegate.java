/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.html.ui.internal.Logger;

/**
 * Wrapper class for all Facet-related calls. If the Facet or ModuleCore
 * bundles are not available, this class will not load, or if it does, its
 * methods will cause NoClassDefFoundErrors. This allows us to
 * compartmentalize the dependencies.
 * 
 */
final class FacetModuleCoreSupportDelegate {
	/**
	 * Copied to avoid unneeded extra dependency (plus it's unclear why the
	 * value is in that plug-in).
	 * 
	 * @see org.eclipse.wst.common.componentcore.internal.util.IModuleConstants.JST_WEB_MODULE
	 */
	private final static String JST_WEB_MODULE = "jst.web"; //$NON-NLS-1$

	private final static String WST_WEB_MODULE = "wst.web"; //$NON-NLS-1$

	/**
	 * @param project
	 * @return the IPath to the "root" of the web contents
	 */
	static IPath getWebContentRootPath(IProject project) {
		if (!ModuleCoreNature.isFlexibleProject(project))
			return project.getFullPath();

		IPath path = null;
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null && component.exists()) {
			path = component.getRootFolder().getWorkspaceRelativePath();
		}
		else {
			path = project.getFullPath();
		}
		return path;
	}

	/**
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	static boolean isWebProject(IProject project) {
		try {
			IFacetedProject faceted = ProjectFacetsManager.create(project);
			IProjectFacet jstModuleFacet = ProjectFacetsManager.getProjectFacet(JST_WEB_MODULE);
			IProjectFacet wstModuleFacet = ProjectFacetsManager.getProjectFacet(WST_WEB_MODULE);
			if (faceted != null && (faceted.hasProjectFacet(jstModuleFacet) || faceted.hasProjectFacet(wstModuleFacet))) {
				return true;
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		return false;
	}
}
