package org.eclipse.wst.web.internal.operation;

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
