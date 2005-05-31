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
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;

public class SimpleWebModuleCreationDataModelProvider extends ProjectCreationDataModelProvider {

    public SimpleWebModuleCreationDataModelProvider() {
        super();
    }

    public void init() {
        super.init();
        addStaticWebNature();
    }

    public String[] getPropertyNames() {
        return super.getPropertyNames();
    }

    public IDataModelOperation getDefaultOperation() {
        return new SimpleWebModuleCreationOp(getDataModel());
    }

    protected final void addStaticWebNature() {
        String[] natures = (String[]) getProperty(PROJECT_NATURES);
        String[] newNatures;

        if (natures == null) {
            newNatures = new String[1];
            newNatures[0] = ISimpleWebNatureConstants.STATIC_NATURE_ID;
        } else {
            newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = ISimpleWebNatureConstants.STATIC_NATURE_ID;
        }

        model.setProperty(PROJECT_NATURES, newNatures);
    }
}
