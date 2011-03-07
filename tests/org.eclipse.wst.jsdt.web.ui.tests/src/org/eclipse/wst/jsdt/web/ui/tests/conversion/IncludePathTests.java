/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.tests.conversion;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.ui.util.ConvertAction;

public class IncludePathTests extends TestCase {
	private static IProject createSimpleProject(String name, IPath location, String[] natureIds) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		if (location != null) {
			description.setLocation(location);
		}
		if (natureIds != null) {
			description.setNatureIds(natureIds);
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}

	private static final ISelection selectionFor(IProject p) {
		return new StructuredSelection(new IProject[]{p, ResourcesPlugin.getWorkspace().getRoot().getProject("IncludePathTests")});
	}

	public IncludePathTests() {
		this("WEb project Include Path manipulation tests");
	}

	public IncludePathTests(String name) {
		super(name);
	}

	private void assertConvertedIncludePath(IProject p, String expected) throws JavaScriptModelException {
		ConvertAction convertor = new ConvertAction();
		convertor.selectionChanged(null, selectionFor(p));
		convertor.run(null);

		assertIncludePath(p, expected);
	}

	private void assertIncludePath(IProject p, String expected) throws JavaScriptModelException {
		IIncludePathEntry[] rawIncludepath = JavaScriptCore.create(p).getRawIncludepath();
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < rawIncludepath.length; i++) {
			b.append(rawIncludepath[i].toString());
			b.append('\n');
		}
		assertEquals("Unexpected Include Path entries", expected, b.toString());
	}

//	public void testAddingModuleCoreToProject() throws CoreException {
//	}
//
//	public void testAddingWebFacetToJSProject() throws CoreException {
//	}
//
//	public void testAddingWebFacetToNonJSProject() throws CoreException {
//	}
//
//	public void testBrowserSuperTypeJSProject() throws CoreException {
//	}
//
//	public void testConvertModuleCoreProject() throws CoreException {
//	}

	public void testConvertSimpleProject() throws CoreException {
		IProject p = createSimpleProject(getName(), null, null);
		assertConvertedIncludePath(p, "/testConvertSimpleProject[CPE_SOURCE][K_SOURCE][isExported:false][attributes:provider=org.eclipse.wst.jsdt.web.core.internal.project.ModuleSourcePathProvider]\norg.eclipse.wst.jsdt.launching.JRE_CONTAINER[CPE_CONTAINER][K_SOURCE][isExported:false]\norg.eclipse.wst.jsdt.launching.baseBrowserLibrary[CPE_CONTAINER][K_SOURCE][isExported:false]\n");
		p.delete(true, true, null);
	}

	public void testConvertDefaultJSProject() throws CoreException {
		IProject p = createSimpleProject(getName(), null, new String[]{JavaScriptCore.NATURE_ID});
		assertConvertedIncludePath(p, "org.eclipse.wst.jsdt.launching.JRE_CONTAINER[CPE_CONTAINER][K_SOURCE][isExported:false]\norg.eclipse.wst.jsdt.launching.baseBrowserLibrary[CPE_CONTAINER][K_SOURCE][isExported:false]\n/testConvertDefaultJSProject[CPE_SOURCE][K_SOURCE][isExported:false]\n");
		p.delete(true, true, null);
	}

	public void testVerifyDefaultIncludePath() throws CoreException {
		IProject p = createSimpleProject(getName(), null, new String[]{JavaScriptCore.NATURE_ID});
		assertIncludePath(p, "org.eclipse.wst.jsdt.launching.JRE_CONTAINER[CPE_CONTAINER][K_SOURCE][isExported:false]\norg.eclipse.wst.jsdt.launching.baseBrowserLibrary[CPE_CONTAINER][K_SOURCE][isExported:false]\n/testVerifyDefaultIncludePath[CPE_SOURCE][K_SOURCE][isExported:false]\n");
		p.delete(true, true, null);
	}
}
