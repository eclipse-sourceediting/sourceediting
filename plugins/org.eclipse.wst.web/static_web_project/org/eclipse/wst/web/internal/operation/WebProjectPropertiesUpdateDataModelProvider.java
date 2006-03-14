package org.eclipse.wst.web.internal.operation;

import java.util.Set;

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class WebProjectPropertiesUpdateDataModelProvider 
 extends AbstractDataModelProvider
 implements IWebProjectPropertiesUpdateDataModelProperties{

	public WebProjectPropertiesUpdateDataModelProvider(){
		super();
	}

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(PROJECT);
		names.add(CONTEXT_ROOT);
		return names;
	}
	
	public IDataModelOperation getDefaultOperation() {
		return new WebProjectPropertiesUpdateOperation(model);
	}
	
}
