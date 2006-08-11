package org.eclipse.wst.project.facet;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class SimpleWebFacetProjectCreationDataModelProvider extends FacetProjectCreationDataModelProvider {

	public SimpleWebFacetProjectCreationDataModelProvider() {
		super();
	}
	
	public void init() {
		super.init();
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel simpleWebFacet = DataModelFactory.createDataModel(new SimpleWebFacetInstallDataModelProvider());
		map.add(simpleWebFacet);
		
		Collection requiredFacets = new ArrayList();
		requiredFacets.add(ProjectFacetsManager.getProjectFacet(simpleWebFacet.getStringProperty(IFacetDataModelProperties.FACET_ID)));
		setProperty(REQUIRED_FACETS_COLLECTION, requiredFacets);
	}

}
