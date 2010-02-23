/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.core.internal.project;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;

public class FacetedProjectListener implements IFacetedProjectListener {
	final static Collection INTERESTING_FACETS = Arrays.asList(new Object[]{"wst.web", "jst.web"});

	/**
	 * 
	 */
	public FacetedProjectListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener
	 * #handleEvent(org.eclipse.wst.common.project.facet.core.events.
	 * IFacetedProjectEvent)
	 */
	public void handleEvent(IFacetedProjectEvent event) {
		if (event.getType() == IFacetedProjectEvent.Type.POST_INSTALL) {
			IProjectFacetActionEvent actionEvent = (IProjectFacetActionEvent) event;
			if (INTERESTING_FACETS.contains(actionEvent.getProjectFacet().getId())) {
				new ConvertJob(event.getProject().getProject(), true, true).schedule(1000);
			}
		}
	}

}
