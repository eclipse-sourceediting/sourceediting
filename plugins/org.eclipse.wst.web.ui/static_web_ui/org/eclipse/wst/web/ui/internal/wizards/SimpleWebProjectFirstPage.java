package org.eclipse.wst.web.ui.internal.wizards;

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

}
