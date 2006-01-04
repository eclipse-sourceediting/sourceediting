package org.eclipse.wst.project.facet;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class SimpleWebFacetInstallDataModelProvider extends FacetInstallDataModelProvider implements ISimpleWebFacetInstallDataModelProperties {

	public SimpleWebFacetInstallDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(CONTENT_DIR);
		names.add(CONTEXT_ROOT);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(CONTENT_DIR)) {
			return "WebContent"; //$NON-NLS-1$
		} else if (propertyName.equals(CONTEXT_ROOT)) {
			return getProperty(FACET_PROJECT_NAME);
		} else if (propertyName.equals(FACET_ID)) {
			return IModuleConstants.WST_WEB_MODULE;
		}
		return super.getDefaultProperty(propertyName);
	}
	
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			model.notifyPropertyChange(CONTEXT_ROOT, IDataModel.VALID_VALUES_CHG);
		}
		return super.propertySet(propertyName, propertyValue);
	}
}
