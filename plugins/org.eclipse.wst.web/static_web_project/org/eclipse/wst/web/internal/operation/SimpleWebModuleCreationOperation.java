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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationOp;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class SimpleWebModuleCreationOperation extends ProjectCreationOp implements ISimpleWebModuleCreationDataModelProperties {

    public SimpleWebModuleCreationOperation(IDataModel dataModel) {
        super(dataModel);
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        IStatus status = super.execute(monitor, info);

        IProject project = (IProject) model.getProperty(PROJECT);
        IFolder webContentFolder = project.getFolder(getWebContentFolderPreference());
        try {
            if (!webContentFolder.exists())
                webContentFolder.create(true, true, null);

            IFolder cssFolder = project.getFolder(webContentFolder.getProjectRelativePath().append(ISimpleWebNatureConstants.CSS_DIRECTORY));

            if (!cssFolder.exists())
                cssFolder.create(true, true, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        if (monitor.isCanceled())
            throw new OperationCanceledException();
        return status;
    }

    private String getWebContentFolderPreference() {
        // TODO implement
        String webContentFolder = WSTWebPlugin.getDefault().getWSTWebPreferences().getStaticWebContentFolderName();
        return webContentFolder;
    }
}
