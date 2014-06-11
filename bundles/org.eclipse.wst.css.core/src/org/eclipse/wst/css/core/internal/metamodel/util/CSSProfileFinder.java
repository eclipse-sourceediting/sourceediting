/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodel.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.css.core.internal.contentproperties.CSSContentProperties;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfile;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfileRegistry;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;


public class CSSProfileFinder {
	/**
	 * Constructor for CSSProfileFinder.
	 */
	private CSSProfileFinder() {
		super();
	}

	static synchronized public CSSProfileFinder getInstance() {
		if (fInstance == null) {
			fInstance = new CSSProfileFinder();
		}
		return fInstance;
	}

	public CSSProfile findProfileFor(ICSSNode node) {
		ICSSModel model = null;
		if (node != null) {
			ICSSDocument doc = node.getOwnerDocument();
			if (doc != null) {
				model = doc.getModel();
			}
		}
		return findProfileFor(model);
	}

	public CSSProfile findProfileFor(IStructuredModel model) {
		String baseLocation = null;
		if (model instanceof ICSSModel) {
			Object modelType = ((ICSSModel) model).getStyleSheetType();
			if (modelType == ICSSModel.EXTERNAL) {
				baseLocation = model.getBaseLocation();
			}
			else if (modelType == ICSSModel.EMBEDDED || modelType == ICSSModel.INLINE) {
				baseLocation = model.getBaseLocation(); // may be null
			}
		}
		else if (model != null) {
			baseLocation = model.getBaseLocation();
		}
		return findProfileFor(baseLocation);
	}

	public CSSProfile findProfileFor(Node node) {
		String baseLocation = null;
		if (node instanceof IDOMNode) {
			IDOMModel model = ((IDOMNode) node).getModel();
			if (model != null) {
				baseLocation = model.getBaseLocation();
			}
		}
		return findProfileFor(baseLocation);
	}

	public CSSProfile findProfileFor(String baseLocation) {
		CSSProfileRegistry reg = CSSProfileRegistry.getInstance();
		CSSProfile profile = null;

		if (baseLocation != null) {
			IPath path = new Path(baseLocation);
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			if (resource == null && path.segmentCount() > 1)
				resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (resource != null) {
				String profileID = CSSContentProperties.getProperty(CSSContentProperties.CSS_PROFILE, resource, true);
				if (profileID != null && 0 < profileID.length()) {
					profile = reg.getProfile(profileID);
				}
			}
		}

		return (profile != null) ? profile : reg.getDefaultProfile();
	}

	static private CSSProfileFinder fInstance = null;
}
