package org.eclipse.wst.web.internal.deployables;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.server.core.IModuleArtifact;

public class StaticWebDeployableObjectAdapter implements IAdapterFactory

{

	public StaticWebDeployableObjectAdapter() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {

		IModuleArtifact moduleArtifact = null;
		if (adapterType == IStaticWebModuleArtifact.class) {
			moduleArtifact = StaticWebDeployableObjectAdapterUtil.getModuleObject(adaptableObject);
			/*
			 * else if (adapterType == ILaunchable.class) { if (adaptableObject instanceof EObject) {
			 * return adaptableObject; }
			 */
		}

		return moduleArtifact;
	}

	public Class[] getAdapterList() {

		return new Class[]{IStaticWebModuleArtifact.class};
	}
}