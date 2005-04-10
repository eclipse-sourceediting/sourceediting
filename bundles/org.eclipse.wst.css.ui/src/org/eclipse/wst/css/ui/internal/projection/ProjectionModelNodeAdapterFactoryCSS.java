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
package org.eclipse.wst.css.ui.internal.projection;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;

public class ProjectionModelNodeAdapterFactoryCSS extends AbstractAdapterFactory {
	private ProjectionViewer fProjectionViewer;

	public ProjectionModelNodeAdapterFactoryCSS() {
		adapterKey = ProjectionModelNodeAdapterCSS.class;
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		ProjectionModelNodeAdapterCSS adapter = null;

		if ((fProjectionViewer != null) && (target instanceof ICSSNode)) {
			ICSSNode node = (ICSSNode) target;
			short type = node.getNodeType();
			// only add for top stylesheet node
			if (type == ICSSNode.STYLESHEET_NODE) {
				adapter = new ProjectionModelNodeAdapterCSS(this);
				adapter.updateAdapter(node);
			}
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
