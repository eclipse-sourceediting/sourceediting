/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.operation.SimpleWebModuleCreationDataModelProvider;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

/**
 * This has been slated for removal post WTP 1.5. Do not use this class/interface
 * 
 * @deprecated
 */
public class SimpleWebModuleCreationWizard extends DataModelWizard implements IExecutableExtension, INewWizard, DoNotUseMeThisWillBeDeletedPost15 {

	public SimpleWebModuleCreationWizard(IDataModel model) {
		super(model);
	}
    
    public SimpleWebModuleCreationWizard() {
        super();
    }
    
    protected IDataModelProvider getDefaultProvider() {
        return new SimpleWebModuleCreationDataModelProvider();
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void doAddPages() {
		addPage(new SimpleWebModuleWizardBasePage(getDataModel(), "page1")); //$NON-NLS-1$
	}

    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {   
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(ResourceHandler.StaticWebProjectCreationWizard_Wizard_Title); 
        setDefaultPageImageDescriptor(WSTWebUIPlugin.getDefault().getImageDescriptor("newwprj_wiz")); //$NON-NLS-1$
    }

//	protected void postPerformFinish() throws InvocationTargetException {
//		IWizardRegistry newWizardRegistry = WorkbenchPlugin.getDefault().getNewWizardRegistry();		
//		
//		IWizardDescriptor descriptor = newWizardRegistry.findWizard(getWizardID());
//
//		if(descriptor instanceof WorkbenchWizardElement)
//			BasicNewProjectResourceWizard.updatePerspective(((WorkbenchWizardElement)descriptor).getConfigurationElement());
//		IWorkbenchWindow window = WSTWebPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
//		IProject project = ((SimpleWebModuleCreationDataModel) model).getTargetProject();
//		BasicNewResourceWizard.selectAndReveal(project, window);
//	}
}