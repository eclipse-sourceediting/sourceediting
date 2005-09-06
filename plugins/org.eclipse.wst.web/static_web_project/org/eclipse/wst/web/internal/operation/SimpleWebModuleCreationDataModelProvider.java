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

import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModelProvider;

public class SimpleWebModuleCreationDataModelProvider extends ProjectCreationDataModelProvider {

    public SimpleWebModuleCreationDataModelProvider() {
        super();
    }

    public void init() {
        super.init();
    }

    public IDataModelOperation getDefaultOperation() {
        return new SimpleWebModuleCreationOperation(getDataModel());
    }

}
