package org.eclipse.wst.project.facet;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;

public class StaticWebFacetInstallDataModelProvider
	extends FacetInstallDataModelProvider
	implements IStaticWebFacetInstallDataModelProperties{

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(CONTENT_DIR);
		return names;
	}
	
	public Object getDefaultProperty(String propertyName) {
		if(propertyName.equals(CONTENT_DIR)){
			return "WebContent"; //$NON-NLS-1$
		} else if(propertyName.equals(FACET_ID)){
			return IModuleConstants.WST_WEB_MODULE;
		}
		return super.getDefaultProperty(propertyName);
	}	
}
