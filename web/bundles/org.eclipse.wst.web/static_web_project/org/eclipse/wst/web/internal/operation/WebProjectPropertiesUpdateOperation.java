package org.eclipse.wst.web.internal.operation;

/*******************************************************************************
 * Copyright (c) 2003, 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.internal.operation.ServerContextRootUpdateOperation;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;




/**
 * @deprecated 
 *   Replaced by {@link ServerContextRootUpdateOperation} 
 * @version 	1.0
 * @author
 */
public class WebProjectPropertiesUpdateOperation 
 extends  AbstractDataModelOperation 
 implements IWebProjectPropertiesUpdateDataModelProperties{
	

	public WebProjectPropertiesUpdateOperation(IDataModel model) {
		super(model);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IProject project = (IProject)model.getProperty( IWebProjectPropertiesUpdateDataModelProperties.PROJECT );
		String contextRoot = model.getStringProperty( IWebProjectPropertiesUpdateDataModelProperties.CONTEXT_ROOT );
		if (contextRoot != null) {
			ComponentUtilities.setServerContextRoot(project, contextRoot);			
		}
		return OK_STATUS;
	}

}
