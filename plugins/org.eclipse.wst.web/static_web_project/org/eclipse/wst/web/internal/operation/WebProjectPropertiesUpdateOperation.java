package org.eclipse.wst.web.internal.operation;
/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;




/**
 * @version 	1.0
 * @author
 */
public class WebProjectPropertiesUpdateOperation 
 extends  AbstractDataModelOperation 
 implements IWebProjectPropertiesUpdateDataModelProperties{
	

	public WebProjectPropertiesUpdateOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IProject project = (IProject)model.getProperty( IWebProjectPropertiesUpdateDataModelProperties.PROJECT );
		String contextRoot = model.getStringProperty( IWebProjectPropertiesUpdateDataModelProperties.CONTEXT_ROOT );
		if (contextRoot != null) {
			ComponentUtilities.setServerContextRoot(project, contextRoot);			
		}
		return OK_STATUS;
	}

}
