/*****************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies  this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: IBM Corporation - initial API and implementation
 *
 ****************************************************************************/

package org.eclipse.wst.css.ui.tests;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.editor.CSSSelectionConverterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.SelectionConverter;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import junit.framework.TestCase;

/**
 * 
 * @since 1.0
 */
public class ExistenceTest extends TestCase {

	/**
	 * tests if CSSUIPlugin can be loaded
	 */
	public void testExists() {
		Plugin p = CSSUIPlugin.getDefault();
		assertNotNull("couldn't load CSS UI plugin", p);
	}

	public void testSelectionConverter() throws Exception {
		String projectName = getClass().getName() + "_CSS";
		IProject project = createProject(projectName);
		String contents = "@MEDIA {\n\tACRONYM {\n\t}\n}";
		IFile testFile = project.getFile("testfile.css");
		testFile.create(new ByteArrayInputStream(contents.getBytes("utf8")), true, null);

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IEditorPart editor = IDE.openEditor(page, testFile);
		assertEquals("Not the expected editor class, are Capabilities plug-ins in the way?", StructuredTextEditor.class.getName(), editor.getClass().getName());


		IDocument doc = editor.getAdapter(IDocument.class);
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		try {
			model = modelManager.getExistingModelForEdit(doc);
			assertTrue(model != null);
			String expected = CSSSelectionConverterFactory.CSSSelectionConverter.class.getName();
			String actual = model.getAdapter(SelectionConverter.class).getClass().getName();
			assertEquals("The selection convertor for basic CSS was not the expected type!", expected, actual);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
			page.closeEditor(editor, false);
		}
	}

	private IProject createProject(String projName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);

		project.create(description, new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		return project;
	}
}
