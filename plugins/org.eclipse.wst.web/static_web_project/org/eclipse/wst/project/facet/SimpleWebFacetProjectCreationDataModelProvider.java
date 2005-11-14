package org.eclipse.wst.project.facet;

import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class SimpleWebFacetProjectCreationDataModelProvider extends FacetProjectCreationDataModelProvider {

	public SimpleWebFacetProjectCreationDataModelProvider() {
		super();
	}
	
	public void init() {
		super.init();
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel simpleWebFacet = DataModelFactory.createDataModel(new SimpleWebFacetInstallDataModelProvider());
		map.add(simpleWebFacet);
	}

}
