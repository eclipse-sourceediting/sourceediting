package org.eclipse.wst.jsdt.web.core.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;

public class JsNatureUninstaller implements IDelegate {
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Uninstalling Facet for JavaScript Development Tools" + ".", 100); //$NON-NLS-1$
		try {
			uninstallNature(project, monitor);
		} finally {
			monitor.done();
		}
	}
	
	public void uninstallNature(IProject project, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Uninstalling JavaScript Development Tools...", 100);
		// by using natures we can leverage the precondition support
		monitor.subTask("Removing JavaScript Development Toolking Nature...");
		SubProgressMonitor sub = new SubProgressMonitor(monitor, 25);
		if (!JsWebNature.hasNature(project)) {
			return;
		}
		try {
			IProjectNature jsNature = new JsWebNature(project, monitor);
			monitor.worked(20);
			monitor.worked(50);
			jsNature.deconfigure();
			monitor.worked(20);
		} catch (CoreException e) {
			throw new CoreException(new Status(IStatus.ERROR, JsCorePlugin.PLUGIN_ID, IStatus.OK, "Error installing runtime! JavaScript Development Tools could not be removed, or is not present in target project..", e));
		}
	}
}
