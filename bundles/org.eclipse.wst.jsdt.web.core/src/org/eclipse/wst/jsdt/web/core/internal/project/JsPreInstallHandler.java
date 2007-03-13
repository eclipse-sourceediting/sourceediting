package org.eclipse.wst.jsdt.web.core.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class JsPreInstallHandler implements IDelegate{
	
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
			
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		monitor.beginTask("Installing Facet for JavaScript Development Tools" + ".", 100); //$NON-NLS-1$

		try {
			new JSDTFacetInstaller().installNature(project,monitor);
		} finally {
			monitor.done();
		}

	}

}
