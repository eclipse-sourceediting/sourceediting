package org.eclipse.wst.web.internal.deployables;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;

public class StaticWebDeployableObjectAdapter extends ModuleArtifactAdapterDelegate implements IAdapterFactory

{

	public StaticWebDeployableObjectAdapter() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IStaticWebModuleArtifact.class};
	}

	public IModuleArtifact getModuleArtifact(Object obj) {
		return StaticWebDeployableObjectAdapterUtil.getModuleObject(obj);
	}
}