package org.eclipse.wst.project.facet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class SimpleWebFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {

		IDataModel model = (IDataModel) config;
		addNatures(project);
		final IVirtualComponent c = ComponentCore.createComponent(project);
		c.create(0, null);
		final IVirtualFolder webroot = c.getRootFolder();
		webroot.createLink(new Path("/" + model.getStringProperty(ISimpleWebFacetInstallDataModelProperties.CONTENT_DIR)), 0, null); //$NON-NLS-1$
		try {
			((IDataModelOperation) model.getProperty(FacetDataModelProvider.NOTIFICATION_OPERATION)).execute(monitor, null);
		} catch (ExecutionException e) {
			Logger.getLogger().logError(e);
		}
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

}
