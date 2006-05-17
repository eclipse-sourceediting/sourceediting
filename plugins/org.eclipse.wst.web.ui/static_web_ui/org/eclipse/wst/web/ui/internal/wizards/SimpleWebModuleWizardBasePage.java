/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.operation.ISimpleWebModuleCreationDataModelProperties;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

/**
 * This has been slated for removal post WTP 1.5. Do not use this class/interface
 * 
 * @deprecated
 */
class SimpleWebModuleWizardBasePage extends DataModelWizardPage implements ISimpleWebModuleCreationDataModelProperties, DoNotUseMeThisWillBeDeletedPost15{
	public Text projectNameField = null;
	protected Text locationPathField = null;
	protected Button browseButton = null;
	//	constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 305;
	//	default values
	private String defProjectNameLabel = WTPCommonUIResourceHandler.Name_;
	private String defBrowseButtonLabel = WTPCommonUIResourceHandler.Browse_;
	private static final String defDirDialogLabel = "Directory"; //$NON-NLS-1$

	public SimpleWebModuleWizardBasePage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
		setDescription(ResourceHandler.StaticWebProjectWizardBasePage_Page_Description); 
		setTitle(ResourceHandler.StaticWebProjectWizardBasePage_Page_Title); 
		ImageDescriptor desc = WSTWebUIPlugin.getDefault().getImageDescriptor("newwprj_wiz"); //$NON-NLS-1$
		setImageDescriptor(desc);
		setPageComplete(false);
	}

	protected void setSize(Composite composite) {
		if (composite != null) {
			Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			composite.setSize(minSize);
			// set scrollbar composite's min size so page is expandable but has
			// scrollbars when needed
			if (composite.getParent() instanceof ScrolledComposite) {
				ScrolledComposite sc1 = (ScrolledComposite) composite.getParent();
				sc1.setMinSize(minSize);
				sc1.setExpandHorizontal(true);
				sc1.setExpandVertical(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.ui.wizard.WTPWizardPage#getValidationPropertyNames()
	 */
	protected String[] getValidationPropertyNames() {
		return new String[]{PROJECT_NAME};
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.ui.wizard.WTPWizardPage#createTopLevelComposite(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setData(new GridData(GridData.FILL_BOTH));
		Composite composite = new Composite(top, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		buildComposites(composite);
		Composite detail = new Composite(top, SWT.NONE);
		detail.setLayout(new GridLayout());
		detail.setData(new GridData(GridData.FILL_BOTH));

		PlatformUI.getWorkbench().getHelpSystem().setHelp(top, "com.ibm.etools.webtools.wizards.basic.webw1450"); //$NON-NLS-1$
		return top;
	}

	/**
	 * Create the controls within this composite
	 */
	public void buildComposites(Composite parent) {
		createProjectNameGroup(parent);
		createProjectLocationGroup(parent);
		projectNameField.setFocus();
	}
	
	private void createProjectLocationGroup(Composite parent) {
		//		set up location path label
		Label locationPathLabel = new Label(parent, SWT.NONE);
		locationPathLabel.setText(WTPCommonUIResourceHandler.Project_location_);
		GridData data = new GridData();
		locationPathLabel.setLayoutData(data);
		// set up location path entry field
		locationPathField = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		locationPathField.setLayoutData(data);
		// set up browse button
		browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText(defBrowseButtonLabel);
		browseButton.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleLocationBrowseButtonPressed();
			}
		});
		browseButton.setEnabled(true);
		synchHelper.synchText(locationPathField, LOCATION, null);
	}

	/**
	 * Open an appropriate directory browser
	 */
	protected void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(locationPathField.getShell());
		dialog.setMessage(defDirDialogLabel);
		String dirName = model.getStringProperty(LOCATION);
		if ((dirName != null) && (dirName.length() != 0)) {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(dirName);
			}
		}
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			model.setProperty(LOCATION, selectedDirectory);
		}
	}
	
	private void createProjectNameGroup(Composite parent) {
		// set up project name label
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText(defProjectNameLabel);
		GridData data = new GridData();
		projectNameLabel.setLayoutData(data);
		// set up project name entry field
		projectNameField = new Text(parent, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		new Label(parent, SWT.NONE); // pad
		synchHelper.synchText(projectNameField, PROJECT_NAME, new Control[]{projectNameLabel});
	}
}