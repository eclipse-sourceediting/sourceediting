package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.ArrayList;
import java.util.EventListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.*;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

public class WebRootFinder {
	
	public static String getWebContext(IProject project) {
		return null;
	}
	
	public static IPath getWebContentFolder(IProject project) {
		
		
		IVirtualComponent comp = ComponentCore.createComponent(project);
		if(comp != null) {
			IVirtualFolder rootFolder = comp.getRootFolder();
			return rootFolder.getUnderlyingFolder().getProjectRelativePath();
		}
		return new Path("");
	}
	public static IPath getServerContextRoot(IProject project) {

		String contextRoot = ComponentUtilities.getServerContextRoot(project);
	
		return new Path(contextRoot);
	}
}
