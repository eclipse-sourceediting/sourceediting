/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.internal.ui.NewProjectGroup;
import org.eclipse.wst.common.frameworks.internal.ui.WTPWizardPage;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.WSTWebPlugin;
import org.eclipse.wst.web.internal.operation.SimpleWebModuleCreationDataModel;


class SimpleWebModuleWizardBasePage extends WTPWizardPage
{
	protected NewProjectGroup projectNameGroup;
	private Text projectNameField;
	private Button fAdvancedButton;

	/**
	 * WebProjectWizardBasePage constructor comment.
	 * 
	 * @param pageName
	 *            java.lang.String
	 */
	public SimpleWebModuleWizardBasePage(
			SimpleWebModuleCreationDataModel dataModel, String pageName)
	{
		super(dataModel, pageName);
		setDescription(ResourceHandler
				.getString("StaticWebProjectWizardBasePage.Page_Description")); //$NON-NLS-1$
		setTitle(ResourceHandler
				.getString("StaticWebProjectWizardBasePage.Page_Title")); //$NON-NLS-1$
		ImageDescriptor desc = WSTWebPlugin
				.imageDescriptorFromPlugin(WSTWebPlugin.PLUGIN_ID,
						"icons/full/wizban/newwprj_wiz.gif"); //$NON-NLS-1$
		setImageDescriptor(desc);
		setPageComplete(false);
	}

	protected void setSize(Composite composite)
	{
		if( composite != null )
		{
			Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			composite.setSize(minSize);
			// set scrollbar composite's min size so page is expandable but has
			// scrollbars when needed
			if( composite.getParent() instanceof ScrolledComposite )
			{
				ScrolledComposite sc1 = (ScrolledComposite) composite
						.getParent();
				sc1.setMinSize(minSize);
				sc1.setExpandHorizontal(true);
				sc1.setExpandVertical(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizardPage#getValidationPropertyNames()
	 */
	protected String[] getValidationPropertyNames()
	{
		return new String[] { SimpleWebModuleCreationDataModel.PROJECT_NAME,
				SimpleWebModuleCreationDataModel.PROJECT_LOCATION};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.common.ui.wizard.WTPWizardPage#createTopLevelComposite(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createTopLevelComposite(Composite parent)
	{
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setData(new GridData(GridData.FILL_BOTH));
		Composite composite = new Composite(top, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		createProjectNameGroup(composite);
		Composite detail = new Composite(top, SWT.NONE);
		detail.setLayout(new GridLayout());
		detail.setData(new GridData(GridData.FILL_BOTH));

		WorkbenchHelp.setHelp(top, "com.ibm.etools.webtools.wizards.basic.webw1450"); //$NON-NLS-1$
		return top;
	}

	private final void createProjectNameGroup(Composite parent)
	{
		projectNameGroup = new NewProjectGroup(parent, SWT.NULL,
				(ProjectCreationDataModel) model);
	}
}