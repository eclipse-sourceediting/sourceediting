/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.ui.internal.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

public class SimpleWebProjectFirstPage extends DataModelFacetCreationWizardPage {

	public SimpleWebProjectFirstPage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
		setDescription(ResourceHandler.StaticWebProjectWizardBasePage_Page_Description); 
		setTitle(ResourceHandler.StaticWebProjectWizardBasePage_Page_Title); 
		setImageDescriptor(WSTWebUIPlugin.getDefault().getImageDescriptor("newwprj_wiz")); //$NON-NLS-1$
		setInfopopID(IWstWebUIContextIds.NEW_STATIC_WEB_PROJECT_PAGE1);
	}
	
	@Override
	protected String getModuleTypeID() {
		return IModuleConstants.WST_WEB_MODULE;
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
        final Composite top = super.createTopLevelComposite(parent);
        createWorkingSetGroupPanel(top, new String[] { RESOURCE_WORKING_SET });
		return top;
	}

}
