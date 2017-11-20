/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.project.facet;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class SimpleWebFacetProjectCreationDataModelProvider extends FacetProjectCreationDataModelProvider {

	public SimpleWebFacetProjectCreationDataModelProvider() {
		super();
	}
	
	@Override
	public void init() {
		super.init();

        Collection<IProjectFacet> requiredFacets = new ArrayList<IProjectFacet>();
        requiredFacets.add(ProjectFacetsManager.getProjectFacet(IModuleConstants.WST_WEB_MODULE));
        setProperty(REQUIRED_FACETS_COLLECTION, requiredFacets);
	}

}
