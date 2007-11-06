package org.eclipse.wst.jsdt.web.core.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;

public class JsNatureInstaller implements IDelegate {
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Installing Facet for JavaScript Development Tools" + ".", 100); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			installNature(project, monitor);
		} finally {
			monitor.done();
		}
	}
	
	public void installNature(IProject project, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.getString("JsNatureInstaller.1"), 100); //$NON-NLS-1$
		monitor.subTask(Messages.getString("JsNatureInstaller.2")); //$NON-NLS-1$
		SubProgressMonitor sub = new SubProgressMonitor(monitor, 25);
		try {
			monitor.worked(20);
			JsWebNature jsNature = new JsWebNature(project, monitor);
			monitor.worked(20);
			jsNature.configure();
			monitor.worked(40);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, JsCorePlugin.PLUGIN_ID, IStatus.OK, Messages.getString("JsNatureInstaller.3"), e)); //$NON-NLS-1$
		}
	}
}
