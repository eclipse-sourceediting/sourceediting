/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.internal.operation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationOperation;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class SimpleWebModuleCreationOperation extends ProjectCreationOperation
{
	/**
	 * @param dataModel
	 */
	public SimpleWebModuleCreationOperation(ProjectCreationDataModel dataModel)
	{
		super(dataModel);
	}

	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException
	{
		super.execute(monitor);

		SimpleWebModuleCreationDataModel dataModel = (SimpleWebModuleCreationDataModel) operationDataModel;
		IProject project = dataModel.getProject();

		IFolder webContentFolder = project
				.getFolder(getWebContentFolderPreference());

		if( !webContentFolder.exists() )
				webContentFolder.create(true, true, null);

		IFolder cssFolder = project.getFolder(webContentFolder
				.getProjectRelativePath().append(
						ISimpleWebNatureConstants.CSS_DIRECTORY));

		if( !cssFolder.exists() ) cssFolder.create(true, true, null);

		createWebSettings(project);

		if( monitor.isCanceled() ) throw new OperationCanceledException();
	}

	private void createWebSettings(IProject project) throws CoreException
	{
		WebSettings webSettings = new WebSettings(project);
		webSettings.setContextRoot(project.getName());
		webSettings.setWebContentName(getWebContentFolderPreference());
		//		webSettings
		//				.setProjectType(IStaticWebProjectConstants.STATIC_PROJECT_TYPE);

		webSettings.write();
	}

	private String getWebContentFolderPreference()
	{
		//TODO implement
		String webContentFolder = WSTWebPlugin.getDefault().getWSTWebPreferences().getStaticWebContentFolderName();
		return webContentFolder;
	}
}