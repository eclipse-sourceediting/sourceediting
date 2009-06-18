/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.project.facet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class SimpleWebFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
		if (monitor != null)
			monitor.beginTask("", 1); //$NON-NLS-1$
		try {
			IDataModel model = (IDataModel) config;
			addNatures(project);
			final IVirtualComponent c = ComponentCore.createComponent(project, false);
			c.create(0, null);
			
			final IWorkspace ws = ResourcesPlugin.getWorkspace();
			final IPath pjpath = project.getFullPath();
			final IPath contentdir = setContentPropertyIfNeeded(model, pjpath, project);
			mkdirs(ws.getRoot().getFolder(contentdir));
			
			final IVirtualFolder webroot = c.getRootFolder();
			if (webroot.getProjectRelativePath().equals(new Path("/"))) { //$NON-NLS-1$
				webroot.createLink(new Path("/" + model.getStringProperty(ISimpleWebFacetInstallDataModelProperties.CONTENT_DIR)), 0, null); //$NON-NLS-1$
			}
			
			ComponentUtilities.setServerContextRoot(project,model.getStringProperty(ISimpleWebFacetInstallDataModelProperties.CONTEXT_ROOT));
			
			try {
				((IDataModelOperation) model.getProperty(FacetDataModelProvider.NOTIFICATION_OPERATION)).execute(monitor, null);
			} catch (ExecutionException e) {
				WSTWebPlugin.logError(e);
			}
		} finally {
			if (monitor != null)
				monitor.done();
		}
	}
	
	private IPath setContentPropertyIfNeeded(final IDataModel model, final IPath pjpath, IProject project) {
		IVirtualComponent c = ComponentCore.createComponent(project);
		if (c.exists()) {
			if( !c.getRootFolder().getProjectRelativePath().isRoot() ){
				return c.getRootFolder().getUnderlyingResource().getFullPath();
			}
		}
		return pjpath.append(model.getStringProperty(ISimpleWebFacetInstallDataModelProperties.CONTENT_DIR));
	}

	private void addNatures(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final String[] current = desc.getNatureIds();
		final String[] replacement = new String[current.length + 1];
		System.arraycopy(current, 0, replacement, 0, current.length);
		replacement[current.length] = IModuleConstants.MODULE_NATURE_ID;
		desc.setNatureIds(replacement);
		project.setDescription(desc, null);
	}
	
	private static void mkdirs(final IFolder folder) throws CoreException {
		if (!folder.exists()) {
			if (folder.getParent() instanceof IFolder) {
				mkdirs((IFolder) folder.getParent());
			}
			folder.create(true, true, null);
		}
	}

}
