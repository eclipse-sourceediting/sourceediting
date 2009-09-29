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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.project.facet.ISimpleWebFacetInstallDataModelProperties;
import org.eclipse.wst.web.internal.ResourceHandler;

public class SimpleWebFacetInstallPage extends DataModelFacetInstallPage implements ISimpleWebFacetInstallDataModelProperties {

	private Label configFolderLabel;
	private Text configFolder;
	private Label contextRootLabel;
	private Text contextRoot;
	
	public SimpleWebFacetInstallPage() {
		super("simpleweb.facet.install.page"); //$NON-NLS-1$
		setTitle(ResourceHandler.StaticWebProjectWizardBasePage_Page_Title);
		setDescription(ResourceHandler.ConfigureSettings);
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[]{CONTEXT_ROOT, CONTENT_DIR};
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		setInfopopID(IWstWebUIContextIds.NEW_STATIC_WEB_PROJECT_PAGE3);
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		this.contextRootLabel = new Label(composite, SWT.NONE);
		this.contextRootLabel.setText(ResourceHandler.StaticContextRootComposite_Context_Root_Label);
		this.contextRootLabel.setLayoutData(gdhfill());

		this.contextRoot = new Text(composite, SWT.BORDER);
		this.contextRoot.setLayoutData(gdhfill());
		this.contextRoot.setData("label", this.contextRootLabel); //$NON-NLS-1$
		synchHelper.synchText(contextRoot, CONTEXT_ROOT, new Control[]{contextRootLabel});
		
		configFolderLabel = new Label(composite, SWT.NONE);
		configFolderLabel.setText(ResourceHandler.StaticWebSettingsPropertiesPage_Web_Content_Label);
		configFolderLabel.setLayoutData(gdhfill());

		configFolder = new Text(composite, SWT.BORDER);
		configFolder.setLayoutData(gdhfill());
		configFolder.setData("label", configFolderLabel); //$NON-NLS-1$
		synchHelper.synchText(configFolder, CONTENT_DIR, null);
	    Dialog.applyDialogFont(parent);
		
		return composite;
	}

}
