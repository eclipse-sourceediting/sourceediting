/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.internal.operation;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.wst.common.componentcore.datamodel.FlexibleProjectCreationDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.ComponentCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties;

/**
 * This has been slated for removal post WTP 1.5. Do not use this class/interface
 * 
 * @deprecated
 */
public class SimpleWebModuleCreationDataModelProvider extends ComponentCreationDataModelProvider implements ISimpleWebModuleCreationDataModelProperties, DoNotUseMeThisWillBeDeletedPost15{

    public SimpleWebModuleCreationDataModelProvider() {
        super();
    }

    public void init() {
        super.init();
    }

    public IDataModelOperation getDefaultOperation() {
    	return new StaticWebModuleCreationFacetOperation(model);
    }
    
    protected EClass getComponentType() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    protected Integer getDefaultComponentVersion() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    protected String getComponentExtension() {
    	return ".war"; //$NON-NLS-1$
    }
    
    protected void initProjectCreationModel() {
    	IDataModel dm = DataModelFactory.createDataModel(new FlexibleProjectCreationDataModelProvider());
		model.addNestedModel(NESTED_PROJECT_CREATION_DM, dm);
		model.setProperty(LOCATION, dm.getProperty(IProjectCreationProperties.PROJECT_LOCATION));
    }
    
    protected List getProperties() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(WEBCONTENT_FOLDER);
		return propertyNames;
	}
    
    public Object getDefaultProperty(String propertyName) {
    	if (propertyName.equals(WEBCONTENT_FOLDER))
			return "WebContent"; //$NON-NLS-1$
		return super.getDefaultProperty(propertyName);
    }
    
    public boolean propertySet(String propertyName, Object propertyValue) {
    	boolean result = super.propertySet(propertyName, propertyValue);
    	if (propertyName.equals(PROJECT_NAME))
    		setProperty(COMPONENT_NAME,propertyValue);
    	return result;
    }

}
