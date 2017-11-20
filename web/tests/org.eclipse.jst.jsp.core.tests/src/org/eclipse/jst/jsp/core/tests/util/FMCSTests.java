/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;

public class FMCSTests extends TestCase {
	private IProject createProject(String name) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.exists())
			// Create new project
			project = BundleResourceUtil.createSimpleProject(name, null, null);
		else if (!project.isAccessible())
			project.open(null);
		return project;
	}

	public void testFacetModuleAbstractionWithWebFragment() throws IOException, CoreException {
		IProject bd1 = createProject("BugDemo1");
		IProject bd2 = createProject("BugDemo2");
		BundleResourceUtil.copyBundleZippedEntriesIntoWorkspace("/testfiles/jsp_include_false_error.zip", Path.ROOT);

		bd1.refreshLocal(IResource.DEPTH_INFINITE, null);
		bd2.refreshLocal(IResource.DEPTH_INFINITE, null);

		assertEquals("/BugDemo2/src/META-INF/resources/referenced.jsp", "" + FacetModuleCoreSupport.resolve(new Path("BugDemo1/WebContent/index.jsp"), "referenced.jsp"));
	}
	public void testFacetModuleAbstractionWithoutWebFragment() throws Exception {
		IProject bd1 = createProject("BugDemo1");
		IProject bd2 = createProject("BugDemo2");
		BundleResourceUtil.copyBundleZippedEntriesIntoWorkspace("/testfiles/jsp_include_false_error.zip", Path.ROOT);

		bd1.refreshLocal(IResource.DEPTH_INFINITE, null);
		bd2.refreshLocal(IResource.DEPTH_INFINITE, null);

		assertEquals("/BugDemo2/src/WEB-INF/web.xml", "" + FacetModuleCoreSupport.resolve(new Path("/BugDemo2/src/META-INF/resources/referenced.jsp"), "/WEB-INF/web.xml"));
	}
	public void testFacetModuleAbstractionInDynamicWebProject() throws Exception {
		IProject bd1 = createProject("bug_399017");
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug_399017", bd1.getFullPath().toString());

		bd1.refreshLocal(IResource.DEPTH_INFINITE, null);

		assertEquals("/bug_399017/WebContent/header.jspf", "" + FacetModuleCoreSupport.resolve(new Path("/bug_399017/WebContent/main.jsp"), "header.jspf"));
	}
}
