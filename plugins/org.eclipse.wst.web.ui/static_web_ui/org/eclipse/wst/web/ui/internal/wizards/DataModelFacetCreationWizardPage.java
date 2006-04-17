/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.ui.internal.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.frameworks.internal.ui.NewProjectGroup;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.AddRemoveFacetsWizard;
import org.eclipse.wst.common.project.facet.ui.PresetSelectionPanel;
import org.eclipse.wst.common.project.facet.ui.internal.AddRemoveFacetsDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.internal.ResourceHandler;

public class DataModelFacetCreationWizardPage extends DataModelWizardPage implements IFacetProjectCreationDataModelProperties {

	private static final String LAST_RUNTIME_STORE = "LAST_RUNTIME"; //$NON-NLS-1$

	protected static GridData gdhfill() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}
    
    protected static GridData hspan( final GridData gd,
                                     final int span ) 
    {
        gd.horizontalSpan = span;
        return gd;
    }

	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(top, getInfopopID());
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));
		createProjectGroup(top);
		createServerTargetComposite(top);
        
        final AddRemoveFacetsDataModel model
            = ( (AddRemoveFacetsWizard) getWizard() ).getModel();

        final PresetSelectionPanel ppanel 
            = new PresetSelectionPanel( top, SWT.NONE, model );
        
        ppanel.setLayoutData( gdhfill() );
        
        ( (AddRemoveFacetsWizard) getWizard() ).syncWithPresetsModel( ppanel.getPresetsCombo() );
        
        return top;
	}

	public static boolean launchNewRuntimeWizard(Shell shell, IDataModel model) {
		DataModelPropertyDescriptor[] preAdditionDescriptors = model.getValidPropertyDescriptors(FACET_RUNTIME);
		boolean isOK = ServerUIUtil.showNewRuntimeWizard(shell, "", ""); //$NON-NLS-1$  //$NON-NLS-2$
		if (isOK && model != null) {

			DataModelPropertyDescriptor[] postAdditionDescriptors = model.getValidPropertyDescriptors(FACET_RUNTIME);
			Object[] preAddition = new Object[preAdditionDescriptors.length];
			for (int i = 0; i < preAddition.length; i++) {
				preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
			}
			Object[] postAddition = new Object[postAdditionDescriptors.length];
			for (int i = 0; i < postAddition.length; i++) {
				postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
			}
			Object newAddition = getNewObject(preAddition, postAddition);

			model.notifyPropertyChange(FACET_RUNTIME, IDataModel.VALID_VALUES_CHG);
			if (newAddition != null)
				model.setProperty(FACET_RUNTIME, newAddition);
			else
				return false;
		}
		return isOK;
	}

	protected Combo serverTargetCombo;
	protected NewProjectGroup projectNameGroup;

	public DataModelFacetCreationWizardPage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
	}

	protected void createServerTargetComposite(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(ResourceHandler.TargetRuntime);
        group.setLayoutData(gdhfill());
        group.setLayout(new GridLayout(2, false));
		serverTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		serverTargetCombo.setLayoutData(gdhfill());
		Button newServerTargetButton = new Button(group, SWT.NONE);
		newServerTargetButton.setText(ResourceHandler.NewDotDotDot);
		newServerTargetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!launchNewRuntimeWizard(getShell(), model)) {
					//Bugzilla 135288
					//setErrorMessage(ResourceHandler.InvalidServerTarget);
				}
			}
		});
		Control[] deps = new Control[]{newServerTargetButton};
		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);
		if (serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0)
			serverTargetCombo.select(0);
	}

	protected void createProjectGroup(Composite parent) {
		IDataModel nestedProjectDM = model.getNestedModel(NESTED_PROJECT_DM);
		nestedProjectDM.addListener(this);
		projectNameGroup = new NewProjectGroup(parent, nestedProjectDM);
	}

	protected String[] getValidationPropertyNames() {
		return new String[]{IProjectCreationPropertiesNew.PROJECT_NAME, IProjectCreationPropertiesNew.PROJECT_LOCATION, FACET_RUNTIME};
	}

	public void dispose() {
		super.dispose();
		if (projectNameGroup != null)
			projectNameGroup.dispose();
	}

	public void storeDefaultSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
			String runtimeName = runtime == null ? "" : runtime.getName(); //$NON-NLS-1$
			settings.put(LAST_RUNTIME_STORE, runtimeName);
		}
	}

	public void restoreDefaultSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			if (!model.isPropertySet(IFacetProjectCreationDataModelProperties.FACET_RUNTIME)) {
				boolean runtimeSet = false;
				String lastRuntimeName = settings.get(LAST_RUNTIME_STORE);
				DataModelPropertyDescriptor[] descriptors = model.getValidPropertyDescriptors(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
				if (lastRuntimeName != null) {
					for (int i = 0; i < descriptors.length && !runtimeSet; i++) {
						if (lastRuntimeName.equals(descriptors[i].getPropertyDescription())) {
							model.setProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME, descriptors[i].getPropertyValue());
							runtimeSet = true;
						}
					}
				}
				if (!runtimeSet && descriptors.length > 0) {
					model.setProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME, descriptors[0].getPropertyValue());
				}
			}
		}
	}
	
	/**
	 * Find first newObject that is not in the oldObjects array (using "==").
	 * 
	 * @param oldObjects
	 * @param newObjects
	 * @return first newObject not found in oldObjects, or <code>null</code> if all found.
	 * 
	 * @since 1.0.0
	 */
	private static Object getNewObject(Object[] oldObjects, Object[] newObjects) {
		if (oldObjects != null && newObjects != null && oldObjects.length < newObjects.length) {
			for (int i = 0; i < newObjects.length; i++) {
				boolean found = false;
				Object object = newObjects[i];
				for (int j = 0; j < oldObjects.length; j++) {
					if (oldObjects[j] == object) {
						found = true;
						break;
					}
				}
				if (!found)
					return object;
			}
		}
		if (oldObjects == null && newObjects != null && newObjects.length == 1)
			return newObjects[0];
		return null;
	}
}
