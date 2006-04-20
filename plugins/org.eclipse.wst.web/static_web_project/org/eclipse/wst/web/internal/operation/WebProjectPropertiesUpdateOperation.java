package org.eclipse.wst.web.internal.operation;

/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

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
