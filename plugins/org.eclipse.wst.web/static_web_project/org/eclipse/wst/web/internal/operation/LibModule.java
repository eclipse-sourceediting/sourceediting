/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.eclipse.wst.web.internal.WSTWebPlugin;


public class LibModule implements ILibModule {
	protected static final IPath LIB_PATH = new Path(ISimpleWebNatureConstants.INFO_DIRECTORY).append(ISimpleWebNatureConstants.LIBRARY_DIRECTORY);

	private String jarName;
	private String projectName;

	public LibModule(String jarName, String projectName) {
		this.jarName = jarName;
		this.projectName = projectName;
	}

	/*
	 * @see ILibModule#getJarName()
	 */
	public String getJarName() {
		return jarName;
	}

	/*
	 * @see ILibModule#getProjectName()
	 */
	public String getProjectName() {
		return projectName;
	}

	/*
	 * @see ILibModule#getProject()
	 */
	public IProject getProject() {
		return (IProject) WSTWebPlugin.getWorkspace().getRoot().findMember(projectName);
	}

	/**
	 * @see ILibModule#getURI()
	 */
	public String getURI() {
		return IPath.SEPARATOR + LIB_PATH.append(getJarName()).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ILibModule)) {
			return false;
		}
		ILibModule module = (ILibModule) obj;
		return getJarName().equals(module.getJarName()) && getProjectName().equals(module.getProjectName()) && getURI().equals(module.getURI()) && getProject().equals(module.getProject());
	}

}