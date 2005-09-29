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

import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;

public interface ISimpleWebModuleCreationDataModelProperties extends IComponentCreationDataModelProperties {
	
	/**
     * Optional, type String, the user defined name of web contents folder
     */
    public static final String WEBCONTENT_FOLDER = "IWebComponentCreationDataModelProperties.WEBCONTENT_FOLDER"; //$NON-NLS-1$  


}
