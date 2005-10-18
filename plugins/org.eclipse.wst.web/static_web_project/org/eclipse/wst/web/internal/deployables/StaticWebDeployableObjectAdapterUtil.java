/*
 * Created on Feb 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.web.internal.deployables;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.Module;
import org.eclipse.wst.server.core.util.WebResource;

public class StaticWebDeployableObjectAdapterUtil {

	private final static String[] extensionsToExclude = new String[]{"sql", "xmi"}; //$NON-NLS-1$ //$NON-NLS-2$
	static String J2EE_NATURE_ID = "org.eclipse.jst.j2ee.web.WebNature"; //$NON-NLS-1$
	static String INFO_DIRECTORY = "WEB-INF"; //$NON-NLS-1$

	public static IModuleArtifact getModuleObject(Object obj) {
		IResource resource = null;
		if (obj instanceof IResource)
			resource = (IResource) obj;
		else if (obj instanceof IAdaptable)
			resource = (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
		if (resource == null)
			return null;
		
		if (resource instanceof IProject)
			return new WebResource(getModule((IProject)resource), new Path("")); //$NON-NLS-1$

		IProject project = ProjectUtilities.getProject(resource);
		IVirtualComponent comp = ComponentCore.createComponent(project);
		// determine path
		IPath rootPath = comp.getRootFolder().getProjectRelativePath();
		IPath resourcePath = resource.getProjectRelativePath();

		// Check to make sure the resource is under the webApplication directory
		if (resourcePath.matchingFirstSegments(rootPath) != rootPath.segmentCount())
			return null;

		// Do not allow resource under the web-inf directory
		resourcePath = resourcePath.removeFirstSegments(rootPath.segmentCount());
		if (resourcePath.segmentCount() > 1 && resourcePath.segment(0).equals(INFO_DIRECTORY))
			return null;

		if (shouldExclude(resource))
			return null;

		// return Web resource type
		return new WebResource(getModule(project), resourcePath);

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

	protected static IModule getModule(IProject project) {
		return getModule(ComponentCore.createComponent(project));
	}
	
	public static IModule getModule(IVirtualComponent component) {
		IModule[] modules  = getModules(component);
		for (int i = 0; i < modules.length; i++) {
			Module module = (Module) modules[i];
			if (module.getName().equals(component.getName()))
				return module;
		}

		return null;
	}
	
	protected static IModule[] getModules(IVirtualComponent component) {
		IProject project = component.getProject();
		if (project == null)
			return null;
		IModule[] modules = ServerUtil.getModules(project);
		return modules;
	}


}
