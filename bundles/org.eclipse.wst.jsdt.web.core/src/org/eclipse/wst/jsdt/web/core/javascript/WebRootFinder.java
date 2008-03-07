package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

public class WebRootFinder {
	public static IPath getServerContextRoot(IProject project) {
		String contextRoot = ComponentUtilities.getServerContextRoot(project);
		if(contextRoot==null) {
			contextRoot = project.getName();
		}
		return new Path(contextRoot);
	}
	
	private static String getProjectRoot(IProject project) {
		return project.getLocation().toString();
	}
	
	public static IPath getWebContentFolder(IProject project) {
		IVirtualComponent comp = ComponentCore.createComponent(project);
		if (comp != null) {
			IVirtualFolder rootFolder = comp.getRootFolder();
			return rootFolder.getUnderlyingFolder().getProjectRelativePath();
		}
		return new Path(""); //$NON-NLS-1$
	}
	
	public static String getWebContext(IProject project) {
		return null;
	}
}
