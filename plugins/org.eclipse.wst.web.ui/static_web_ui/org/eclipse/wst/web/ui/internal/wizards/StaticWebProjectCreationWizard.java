/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

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
import org.eclipse.ui.internal.registry.NewWizardsRegistryReader;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.ExtendableWizard;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.WSTWebPlugin;
import org.eclipse.wst.web.ui.internal.operations.StaticWebProjectCreationDataModel;
import org.eclipse.wst.web.ui.internal.operations.StaticWebProjectCreationOperation;


public class StaticWebProjectCreationWizard extends ExtendableWizard implements
		IExecutableExtension, INewWizard
{
	private IConfigurationElement configData;

	public StaticWebProjectCreationWizard(
			StaticWebProjectCreationDataModel model)
	{
		super(model);
	}

	public StaticWebProjectCreationWizard()
	{
		super();
	}

	public String getWizardID()
	{
		return "com.ibm.etools.webtools.StaticWebProjectCreation"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void doAddPages()
	{
		addPage(new StaticWebProjectWizardBasePage(
				(StaticWebProjectCreationDataModel) model, "page1")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#createDefaultModel()
	 */
	protected WTPOperationDataModel createDefaultModel()
	{
		return new StaticWebProjectCreationDataModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#createOperation()
	 */
	protected WTPOperation createBaseOperation()
	{
		return new StaticWebProjectCreationOperation(
				(StaticWebProjectCreationDataModel) model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException
	{
		configData = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setWindowTitle(ResourceHandler
				.getString("StaticWebProjectCreationWizard.Wizard_Title")); //$NON-NLS-1$

		setDefaultPageImageDescriptor(WSTWebPlugin.getDefault().getImageDescriptor("newwprj_wiz.obj")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizard#postPerformFinish()
	 */
	protected void postPerformFinish() throws InvocationTargetException
	{
		NewWizardsRegistryReader reader = new NewWizardsRegistryReader(true);
		IConfigurationElement element = reader.findWizard(getWizardID())
				.getConfigurationElement();

		BasicNewProjectResourceWizard.updatePerspective(element);
		IWorkbenchWindow window = WSTWebPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IProject project = ((StaticWebProjectCreationDataModel) model)
				.getTargetProject();
		BasicNewProjectResourceWizard.selectAndReveal(project, window);
	}
}