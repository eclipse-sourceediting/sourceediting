/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.internal.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;

public class SimpleWebModuleCreationDataModel extends ProjectCreationDataModel
{
	/*
	 * required (automatically set) for the Features page to determine which
	 * features to provide. Type "int" set to
	 * IWebNatureConstants.STATIC_WEB_PROJECT
	 */
	public SimpleWebModuleCreationDataModel createStaticWebProjectCreationDataModel(
			String projectName)
	{
		SimpleWebModuleCreationDataModel model = (SimpleWebModuleCreationDataModel) super
				.createProjectCreationDataModel(projectName);
		model.addStaticWebNature();
		//setIntProperty(WebProjectFeaturesDataModel.WEB_PROJECT_TYPE, IWebNatureConstants.STATIC_WEB_PROJECT);

		return model;
	}

	public SimpleWebModuleCreationDataModel()
	{
		super();
		addStaticWebNature();
		//setIntProperty(WebProjectFeaturesDataModel.WEB_PROJECT_TYPE,IWebNatureConstants.STATIC_WEB_PROJECT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.operation.ProjectCreationDataModel#initValidBaseProperties()
	 */
	protected void initValidBaseProperties()
	{
		super.initValidBaseProperties();
		//addValidBaseProperty(WebProjectFeaturesDataModel.WEB_PROJECT_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.operation.ProjectCreationDataModel#getDefaultOperation()
	 */
	public WTPOperation getDefaultOperation()
	{
		return new SimpleWebModuleCreationOperation(this);
	}

	protected final void addStaticWebNature()
	{
		String[] natures = (String[]) getProperty(PROJECT_NATURES);
		String[] newNatures;

		if( natures == null )
		{
			newNatures = new String[1];
			newNatures[0] = ISimpleWebNatureConstants.STATIC_NATURE_ID;
		}
		else
		{
			newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = ISimpleWebNatureConstants.STATIC_NATURE_ID;
		}

		setProperty(PROJECT_NATURES, newNatures);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.operation.ProjectCreationDataModel#doValidateProperty(java.lang.String)
	 */
	protected IStatus doValidateProperty(String propertyName)
	{
		/*if( propertyName.equals(WebProjectFeaturesDataModel.WEB_PROJECT_TYPE) )
		{
			return OK_STATUS;
		}*/
		return super.doValidateProperty(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.operation.WTPOperationDataModel#getTargetProject()
	 */
	public IProject getTargetProject()
	{
		return getProjectHandle(PROJECT_NAME);
	}
}