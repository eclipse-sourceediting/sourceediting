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
import org.eclipse.wst.jsdt.internal.core.JavaProject;

import org.eclipse.wst.jsdt.web.core.internal.JSPCorePlugin;

public class JsNatureInstaller implements IDelegate{
	
    	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
    			
    		if (monitor == null) {
    			monitor = new NullProgressMonitor();
    		}
    		
    		monitor.beginTask("Installing Facet for JavaScript Development Tools" + ".", 100); //$NON-NLS-1$
    
    		try {
    			installNature(project,monitor);
    		} finally {
    			monitor.done();
    		}
    
    	}
    	 public void installNature( IProject project, IProgressMonitor monitor ) throws CoreException{
     		
     		monitor.beginTask( "Installing JavaScript Development Tools...", 100 );
     		
     		//by using natures we can leverage the precondition support
     		monitor.subTask( "Adding nature..." );
     		SubProgressMonitor sub = new SubProgressMonitor( monitor, 25 );
     		
     		
     		try{
     			JavaProject proj = new JavaProject();
    			proj.setProject(project);
    			proj.configure();
     			
     			monitor.worked(20);
     			//jsNature.setProject(project);
     			monitor.worked(20);
     			JsWebNature jsNature = new JsWebNature(project);
         		if(jsNature.isValidJSDTProject()) return;
         		jsNature.configure();
     			monitor.worked(40);
     			
     		}
     		catch( CoreException e ){
     			throw new CoreException( new Status(
     					IStatus.ERROR,
     					JSPCorePlugin.PLUGIN_ID,
     					IStatus.OK,
     					"Error installing runtime! JavaScript Development Tools could not be added..", e) );
     		}
     		
     	}
}
