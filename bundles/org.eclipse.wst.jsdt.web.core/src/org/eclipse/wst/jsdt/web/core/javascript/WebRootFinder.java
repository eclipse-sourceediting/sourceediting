/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/



package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
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
