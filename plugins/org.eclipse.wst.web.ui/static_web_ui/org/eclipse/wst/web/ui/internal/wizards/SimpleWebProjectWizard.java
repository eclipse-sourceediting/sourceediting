package org.eclipse.wst.web.ui.internal.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.project.facet.SimpleWebFacetProjectCreationDataModelProvider;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

public class SimpleWebProjectWizard extends NewProjectDataModelFacetWizard {

	public SimpleWebProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle(ResourceHandler.StaticWebProjectCreationWizard_Wizard_Title);
	}

	public SimpleWebProjectWizard() {
		super();
		setWindowTitle(ResourceHandler.StaticWebProjectCreationWizard_Wizard_Title);
	}

	protected IDataModel createDataModel() {
		return DataModelFactory.createDataModel(new SimpleWebFacetProjectCreationDataModelProvider());
	}

	protected ImageDescriptor getDefaultPageImageDescriptor() {
		return WSTWebUIPlugin.getDefault().getImageDescriptor("newwprj_wiz"); //$NON-NLS-1$
	}

	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate("template.wst.web"); //$NON-NLS-1$
	}

	protected IWizardPage createFirstPage() {
		return new SimpleWebProjectFirstPage(model, "first.page"); //$NON-NLS-1$
	}

}
