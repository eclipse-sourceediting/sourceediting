/*******************************************************************************
 * Copyright (c) 2012, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class ModelResourceFactory implements IAdapterFactory {

	private static final Class[] TYPES = new Class[] { IResource.class };

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IStructuredModel && IResource.class.equals(adapterType)) {
			String baseLocation = ((IStructuredModel) adaptableObject).getBaseLocation();
			if (baseLocation != null && !IModelManager.DUPLICATED_MODEL.equals(baseLocation) && !IModelManager.UNMANAGED_MODEL.equals(baseLocation)) {
				IPath path = new Path(baseLocation);
				if (path.segmentCount() > 1 && ResourcesPlugin.getWorkspace().getRoot().exists(path)) {
					return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				}
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return TYPES;
	}

}
