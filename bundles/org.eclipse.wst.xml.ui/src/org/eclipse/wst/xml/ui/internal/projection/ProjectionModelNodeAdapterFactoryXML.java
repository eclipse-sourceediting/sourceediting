/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.projection;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Node;

public class ProjectionModelNodeAdapterFactoryXML extends AbstractAdapterFactory {
	private ProjectionViewer fProjectionViewer;

	public ProjectionModelNodeAdapterFactoryXML() {
		super(ProjectionModelNodeAdapterXML.class, true);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		ProjectionModelNodeAdapterXML adapter = null;

		// create adapter for every element tag
		if ((fProjectionViewer != null) && (target instanceof Node) && ((Node) target).getNodeType() == Node.ELEMENT_NODE) {
			adapter = new ProjectionModelNodeAdapterXML(this);
			adapter.updateAdapter((Node) target);
		}

		return adapter;
	}

	ProjectionViewer getProjectionViewer() {
		return fProjectionViewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.AbstractAdapterFactory#release()
	 */
	public void release() {
		fProjectionViewer = null;

		super.release();
	}

	void setProjectionViewer(ProjectionViewer viewer) {
		fProjectionViewer = viewer;
	}
}
