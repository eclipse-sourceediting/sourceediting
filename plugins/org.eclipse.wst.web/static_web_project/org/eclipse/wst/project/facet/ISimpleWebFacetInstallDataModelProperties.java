package org.eclipse.wst.project.facet;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;

public interface ISimpleWebFacetInstallDataModelProperties extends IDataModelProperties {
	/**
	 * This field should not be used.  It is not part of the API and may be modified in the future.
	 */
	public static Class _provider_class = SimpleWebFacetProjectCreationDataModelProvider.class;

	public static final String CONTENT_DIR = "IStaticWebFacetInstallDataModelProperties.CONTENT_DIR"; //$NON-NLS-1$
	
	public static final String CONTEXT_ROOT = "IStaticWebFacetInstallDataModelProperties.CONTEXT_ROOT"; //$NON-NLS-1$
}
