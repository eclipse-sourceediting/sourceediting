/*
 * Created on Feb 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.web.internal.deployables;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.internal.project.IWebNatureConstants;
import org.eclipse.jst.j2ee.internal.web.operations.WebNatureRuntimeUtilities;
import org.eclipse.jst.server.core.WebResource;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.web.internal.operation.IBaseWebNature;
import org.eclipse.wst.web.internal.operation.StaticWebNatureRuntime;

public class StaticWebDeployableObjectAdapterUtil {

	private final static String[] extensionsToExclude = new String[]{"sql", "xmi"}; //$NON-NLS-1$ //$NON-NLS-2$


	public static IModuleArtifact getModuleObject(Object obj) {
		IResource resource = null;
		if (obj instanceof IResource)
			resource = (IResource) obj;
		else if (obj instanceof IAdaptable)
			resource = (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
		if (resource == null)
			return null;

		// find deployable
		IBaseWebNature webNature = WebNatureRuntimeUtilities.getRuntime(resource.getProject());
		if (webNature == null || !(webNature instanceof StaticWebNatureRuntime))
			return null;

		if (resource instanceof IProject)
			return new WebResource(getModule(webNature), new Path("")); //$NON-NLS-1$

		// determine path
		IPath rootPath = webNature.getRootPublishableFolder().getProjectRelativePath();
		IPath resourcePath = resource.getProjectRelativePath();

		// Check to make sure the resource is under the webApplication directory
		if (resourcePath.matchingFirstSegments(rootPath) != rootPath.segmentCount())
			return null;

		// Do not allow resource under the web-inf directory
		resourcePath = resourcePath.removeFirstSegments(rootPath.segmentCount());
		if (resourcePath.segmentCount() > 1 && resourcePath.segment(0).equals(IWebNatureConstants.INFO_DIRECTORY))
			return null;

		if (shouldExclude(resource))
			return null;

		// return Web resource type
		return new WebResource(getModule(webNature), resourcePath);
	}

	/**
	 * Method shouldExclude.
	 * 
	 * @param resource
	 * @return boolean
	 */
	private static boolean shouldExclude(IResource resource) {
		String fileExt = resource.getFileExtension();

		// Exclude files of certain extensions
		for (int i = 0; i < extensionsToExclude.length; i++) {
			String extension = extensionsToExclude[i];
			if (extension.equalsIgnoreCase(fileExt))
				return true;
		}
		return false;
	}

	protected static IModule getModule(IBaseWebNature nature) {
		IModule deployable = nature.getModule();
		if (deployable != null)
			return deployable;

		IProject project = nature.getProject();
		Iterator iterator =  Arrays.asList(ServerUtil.getModules("web.static")).iterator(); //$NON-NLS-1$ //$NON-NLS-2$
		while (iterator.hasNext()) {
			deployable = (IModule) iterator.next();
			if (deployable instanceof IModule) {
				if (((IModule) deployable).getProject().equals(project))
					return deployable;
			}
		}
		return null;
	}


}
