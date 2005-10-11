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
package org.eclipse.wst.web.internal.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.operation.ComponentCreationOperation;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.web.internal.ISimpleWebModuleConstants;

public class SimpleWebModuleCreationOperation extends ComponentCreationOperation {

    public SimpleWebModuleCreationOperation(IDataModel dataModel) {
        super(dataModel);
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	return super.execute(IModuleConstants.WST_WEB_MODULE,monitor, info);
    }
    
    protected void createAndLinkJ2EEComponentsForMultipleComponents() throws CoreException {
    	createWebStructure();
    }
    
    protected void createAndLinkJ2EEComponentsForSingleComponent() throws CoreException {
    	createWebStructure();
    }
    
    protected void createWebStructure() throws CoreException {
		IVirtualComponent component = ComponentCore.createComponent(getProject());
		component.create(0, null);
		// create and link webContent and css folder
		IVirtualFolder webContent = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$       
		webContent.createLink(new Path("/" + model.getStringProperty(ISimpleWebModuleCreationDataModelProperties.WEBCONTENT_FOLDER)), 0, null); //$NON-NLS-1$
		
		IVirtualFolder webCSSFolder = webContent.getFolder(ISimpleWebModuleConstants.CSS_DIRECTORY);
		webCSSFolder.create(IResource.FORCE, null);
	}
    
    //  Should return null if no additional properties needed
    protected List getProperties() {
        return null;
    }
    
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    protected String getVersion() {
    	// TODO Auto-generated method stub
    	return null;
    }
}
