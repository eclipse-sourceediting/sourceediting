/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.ui.ExtendableWizard;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.WSTWebPlugin;
import org.eclipse.wst.web.internal.operation.SimpleWebModuleCreationDataModel;
import org.eclipse.wst.web.internal.operation.SimpleWebModuleCreationOperation;

public class SimpleWebModuleCreationWizard extends ExtendableWizard implements IExecutableExtension, INewWizard {

	public SimpleWebModuleCreationWizard(SimpleWebModuleCreationDataModel model) {
		super(model);
	}

	public SimpleWebModuleCreationWizard() {
		super();
	}

	public String getWizardID() {
		return "org.eclipse.wst.web.ui.internal.wizards.SimpleWebModuleCreation"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void doAddPages() {
		addPage(new SimpleWebModuleWizardBasePage((SimpleWebModuleCreationDataModel) model, "page1")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#createDefaultModel()
	 */
	protected WTPOperationDataModel createDefaultModel() {
		return new SimpleWebModuleCreationDataModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#createOperation()
	 */
	protected WTPOperation createBaseOperation() {
		return new SimpleWebModuleCreationOperation((SimpleWebModuleCreationDataModel) model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(ResourceHandler.getString("StaticWebProjectCreationWizard.Wizard_Title")); //$NON-NLS-1$

		setDefaultPageImageDescriptor(WSTWebPlugin.getDefault().getImageDescriptor("newwprj_wiz")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#postPerformFinish()
	 */
	protected void postPerformFinish() throws InvocationTargetException {
		IWizardRegistry newWizardRegistry = WorkbenchPlugin.getDefault().getNewWizardRegistry();		
		
		IWizardDescriptor descriptor = newWizardRegistry.findWizard(getWizardID());

		if(descriptor instanceof WorkbenchWizardElement)
			BasicNewProjectResourceWizard.updatePerspective(((WorkbenchWizardElement)descriptor).getConfigurationElement());
		IWorkbenchWindow window = WSTWebPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IProject project = ((SimpleWebModuleCreationDataModel) model).getTargetProject();
		BasicNewResourceWizard.selectAndReveal(project, window);
	}
}