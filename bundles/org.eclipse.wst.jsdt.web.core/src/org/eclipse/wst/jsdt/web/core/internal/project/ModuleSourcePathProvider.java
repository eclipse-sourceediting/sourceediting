/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.jsdt.core.IIncludePathAttribute;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.core.ClasspathEntry;
import org.eclipse.wst.jsdt.internal.core.util.DefaultSourcePathProvider;

public class ModuleSourcePathProvider extends DefaultSourcePathProvider {

	public static final String PROVIDER_ATTRIBUTE_KEY_NAME = "provider"; //$NON-NLS-1$
	public static final String PROVIDER_ATTRIBUTE_KEY_VALUE = ModuleSourcePathProvider.class.getName(); //$NON-NLS-1$
	
	public ModuleSourcePathProvider() {
	}

	public IIncludePathEntry[] getDefaultSourcePaths(IProject p) {
		if (ModuleCoreNature.isFlexibleProject(p)) {
			IVirtualFile root = ComponentCore.createFile(p, Path.ROOT);
			IResource[] underlyingResources = root.getUnderlyingResources();
			if (underlyingResources == null || underlyingResources.length == 0) {
				underlyingResources = new IResource[]{root.getUnderlyingResource()};
			}
			if (underlyingResources.length > 0 && underlyingResources[0] != null) {
				IPath[] paths = new IPath[underlyingResources.length];
				for (int i = 0; i < underlyingResources.length; i++) {
					paths[i] = underlyingResources[i].getFullPath();
				}
				if (paths.length > 0) {
					IIncludePathEntry[] entries = new IIncludePathEntry[paths.length];
					for (int i = 0; i < paths.length; i++) {
						entries[i] = JavaScriptCore.newSourceEntry(paths[i]);
					}
					return entries;
				}
			}
		}
		
		return new IIncludePathEntry[]{JavaScriptCore.newSourceEntry(p.getFullPath(),
				ClasspathEntry.INCLUDE_ALL,ClasspathEntry.EXCLUDE_NONE,null,
				new IIncludePathAttribute[]{JavaScriptCore.newIncludepathAttribute(PROVIDER_ATTRIBUTE_KEY_NAME, PROVIDER_ATTRIBUTE_KEY_VALUE)})};
	}
}
