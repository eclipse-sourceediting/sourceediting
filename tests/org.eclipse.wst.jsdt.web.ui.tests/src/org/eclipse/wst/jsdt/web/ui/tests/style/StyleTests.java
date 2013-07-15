/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.tests.style;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.LibrarySuperType;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.launching.JavaRuntime;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;

/**
 * @author nitin
 * 
 */
public class StyleTests extends TestCase {

	/**
	 * 
	 */
	public StyleTests() {
		super();
	}

	/**
	 * @param name
	 */
	public StyleTests(String name) {
		super(name);
	}

	public void testKeywordStyle() throws CoreException {
		String projectName = getName();
		String fileName = projectName + ".html";
		String contents = "<html><head><script> var params = \"someBadString\".substring(1,2,3,4);\nparahnas.shift();</script></head><body> </body></html>";

		// Create the JavaScript project
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		description.setNatureIds(new String[]{JavaScriptCore.NATURE_ID});
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		iProject.create(description, new NullProgressMonitor());
		iProject.open(null);

		// Setup the JavaScript project with Web support
		IJavaScriptProject project = JavaScriptCore.create(iProject);
		project.setRawIncludepath(null, new NullProgressMonitor());
		LibrarySuperType superType = new LibrarySuperType(new Path(JavaRuntime.DEFAULT_SUPER_TYPE_LIBRARY), project, JavaRuntime.DEFAULT_SUPER_TYPE);
		((JavaProject) project).setCommonSuperType(superType);
		new JsWebNature(iProject, null).configure();

		// create test file
		IFile file = iProject.getFile(fileName);
		file.create(new ByteArrayInputStream(contents.getBytes()), true, null);
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = IDE.openEditor(activePage, file, "org.eclipse.wst.html.core.htmlsource.source");
		try {
			assertNotNull("editor failed to open", editor);
			Control control = (Control) editor.getAdapter(Control.class);
			assertNotNull("editor did not return a Control adapter", control);
			assertTrue("editor Control adapter is not a StyledText widget", control instanceof StyledText);
			int varIndex = contents.indexOf("var");
			StyleRange[] styleRanges = ((StyledText) control).getStyleRanges(varIndex, 3);
			assertTrue("no style range for 'var' keyword (this test may fail due to unpredictable timing issues with the test workbench)", styleRanges.length > 0);
			assertNotNull("no foreground color for 'var' keyword", styleRanges[0].foreground);
			assertNotSame("foreground color has same RGB as black", PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK).getRGB(), styleRanges[0].foreground.getRGB());
		}
		finally {
			activePage.closeEditor(editor, false);
		}
	}
}
