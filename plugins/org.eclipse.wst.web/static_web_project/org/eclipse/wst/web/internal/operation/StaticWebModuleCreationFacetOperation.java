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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.project.facet.IStaticWebFacetInstallDataModelProperties;
import org.eclipse.wst.project.facet.StaticWebFacetInstallDataModelProvider;

public class StaticWebModuleCreationFacetOperation extends AbstractDataModelOperation {

	public StaticWebModuleCreationFacetOperation(IDataModel dataModel) {
		super(dataModel);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) {

		IDataModel dm = DataModelFactory.createDataModel(new FacetProjectCreationDataModelProvider());
		String projectName = model.getStringProperty(IComponentCreationDataModelProperties.PROJECT_NAME);

		dm.setProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME, projectName);

		FacetDataModelMap map = (FacetDataModelMap) dm.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		map.add(setupWebInstallAction());

		IStatus stat = OK_STATUS;
		try {
			stat = dm.getDefaultOperation().execute(monitor, info);
		} catch (ExecutionException e) {
			Logger.getLogger().logError(e);
		}
		return stat;
	}

	protected IDataModel setupWebInstallAction() {
		String versionStr = "1.0";
		IDataModel webFacetInstallDataModel = DataModelFactory.createDataModel(new StaticWebFacetInstallDataModelProvider());
		webFacetInstallDataModel.setProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, model.getStringProperty(IComponentCreationDataModelProperties.PROJECT_NAME));
		webFacetInstallDataModel.setProperty(IFacetDataModelProperties.FACET_VERSION_STR, versionStr);
		webFacetInstallDataModel.setProperty(IStaticWebFacetInstallDataModelProperties.CONTENT_DIR, model.getStringProperty(ISimpleWebModuleCreationDataModelProperties.WEBCONTENT_FOLDER));

		return webFacetInstallDataModel;
	}
}