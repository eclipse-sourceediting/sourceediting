package org.eclipse.wst.sse.core.internal.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class ModelResourceFactory implements IAdapterFactory {

	private static final Class[] TYPES = new Class[] { IResource.class };

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IStructuredModel && IResource.class.equals(adapterType)) {
			String baseLocation = ((IStructuredModel) adaptableObject).getBaseLocation();
			if (baseLocation != null && !IModelManager.DUPLICATED_MODEL.equals(baseLocation) && !IModelManager.UNMANAGED_MODEL.equals(baseLocation)) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(baseLocation));
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return TYPES;
	}

}
