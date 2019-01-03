package org.eclipse.wst.web.internal.operation;

/*******************************************************************************
 * Copyright (c) 2003, 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.Set;

import org.eclipse.wst.common.componentcore.internal.operation.ServerContextRootDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * @deprecated Replaced by {@link ServerContextRootDataModelProvider}
 *
 */
public class WebProjectPropertiesUpdateDataModelProvider 
 extends AbstractDataModelProvider
 implements IWebProjectPropertiesUpdateDataModelProperties{

	public WebProjectPropertiesUpdateDataModelProvider(){
		super();
	}

	@Override
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(PROJECT);
		names.add(CONTEXT_ROOT);
		return names;
	}
	
	@Override
	public IDataModelOperation getDefaultOperation() {
		return new WebProjectPropertiesUpdateOperation(model);
	}
	
}
