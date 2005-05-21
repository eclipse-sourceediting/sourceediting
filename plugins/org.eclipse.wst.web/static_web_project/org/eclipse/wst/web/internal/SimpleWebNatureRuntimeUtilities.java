/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.web.internal.operation.IStaticWebNature;
import org.eclipse.wst.web.internal.operation.StaticWebNatureRuntime;


public class SimpleWebNatureRuntimeUtilities {
	/**
	 * WebNatureRuntimeHelper constructor comment.
	 */
	public SimpleWebNatureRuntimeUtilities() {
		super();
	}

	/**
	 * Adds a nauture to a project
	 */
	protected static void addNatureToProject(IProject proj, String natureId) throws CoreException {
		ProjectUtilities.addNatureToProject(proj, natureId);
	}

	/**
	 * Creation date: (10/22/2001 2:17:25 PM)
	 * 
	 * @return org.eclipse.jst.j2ee.internal.internal.internal.web.operations.IBaseWebNature
	 */
	public static IStaticWebNature getRuntime(IProject project) {
		if (project == null)
			return null;
		try {
			IStaticWebNature nature = null;
			if (project.hasNature(ISimpleWebNatureConstants.STATIC_NATURE_ID))
				nature = (IStaticWebNature) project.getNature(ISimpleWebNatureConstants.STATIC_NATURE_ID);
			return nature;
		} catch (CoreException e) {
			return null;
		}
	}

	/**
	 * Return the J2EE Web Nature for the given project. If the the project does not have a J2EE Web
	 * Nature, then return null.
	 * 
	 * @param project
	 *            The project to get the nature from
	 * @return IJ2EEWebNature The J2EE Web Nature
	 */
	public static StaticWebNatureRuntime getStaticRuntime(IProject project) {
		IStaticWebNature nature = getRuntime(project);
		if (nature != null) {
			if (nature.isStatic())
				return (StaticWebNatureRuntime) nature;
		}
		return null;
	}

	/**
	 * Return whether or not the project has a runtime created on it.
	 * 
	 * @return boolean
	 * @param project
	 *            com.ibm.itp.core.api.resources.IProject
	 */
	public static boolean hasStaticRuntime(IProject project) {
		if (project == null || !project.exists()) {
			return false;
		}
		try {
			return project.hasNature(ISimpleWebNatureConstants.STATIC_NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	public static String getDefaultStaticWebContentName() {
		return WSTWebPlugin.getDefault().getWSTWebPreferences().getStaticWebContentFolderName();
	}

	public static String getContextRootFromWebProject(IProject project) {

		// get uri from web app display name, and get context root from web nature
		IStaticWebNature nature = SimpleWebNatureRuntimeUtilities.getRuntime(project);
		String contextRoot = ""; //$NON-NLS-1$
		contextRoot = nature.getContextRoot();

		//	WebEditModel webEditModel = null;
		//	try {
		//		webEditModel = (nature.isStatic())
		//						? null
		//						:
		// ((J2EEWebNatureRuntime)nature).getWebAppEditModelForRead(WebNatureRuntimeUtilities.this);
		//		contextRoot = nature.getContextRoot();
		//	} catch (Exception e) {
		//		e.printStackTrace();
		//	}
		//	finally {
		//		if (webEditModel != null)
		//			webEditModel.releaseAccess(WebNatureRuntimeUtilities.this);
		//	}
		return contextRoot;
	}

}