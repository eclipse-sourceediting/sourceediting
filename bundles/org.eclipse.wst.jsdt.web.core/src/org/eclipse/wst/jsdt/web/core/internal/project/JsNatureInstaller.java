/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;
/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
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
		try {
			monitor.worked(20);
			JsWebNature jsNature = new JsWebNature(project, monitor);
			monitor.worked(20);
			jsNature.configure();
			monitor.worked(40);
			
			new ConvertJob(project, false, true).schedule(1000);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, JsCorePlugin.PLUGIN_ID, IStatus.OK, Messages.getString("JsNatureInstaller.3"), e)); //$NON-NLS-1$
		}
	}
}
