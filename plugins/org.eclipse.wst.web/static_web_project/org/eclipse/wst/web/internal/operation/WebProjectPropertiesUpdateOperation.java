package org.eclipse.wst.web.internal.operation;
/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.frameworks.internal.operations.IHeadlessRunnableWithProgress;




/**
 * @version 	1.0
 * @author
 */
public class WebProjectPropertiesUpdateOperation implements IHeadlessRunnableWithProgress {

	protected IProject project;
	protected String contextRoot; 

	

	public WebProjectPropertiesUpdateOperation(IProject project, String contextRoot) {
		this.project = project;
		this.contextRoot = contextRoot;
	}
	/*
	 * @see IHeadlessRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		// Update the context root 
		if (contextRoot != null) {
			ComponentUtilities.setServerContextRoot(project, contextRoot);
		}	
	
	}

}
