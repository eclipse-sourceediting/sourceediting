/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.properties.ConfigurablePropertySheetPage;
import org.eclipse.wst.sse.ui.views.properties.IPropertySourceExtension;
import org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration;

public class TestPropertySheetConfiguration extends TestCase {
	private final String PROJECT_NAME = "TestPropertySheetConfiguration";
	private final String FILE_NAME = "testPropertySheetConfiguration.xml";

	private static StructuredTextEditor fEditor;
	private static IEditorPart fMainEditor;
	private static IFile fFile;
	private static boolean fIsSetup = false;

	public TestPropertySheetConfiguration() {
		super("TestPropertySheetConfiguration");
	}

	protected void setUp() throws Exception {
		if (!fIsSetup) {
			// create project
			createProject(PROJECT_NAME);
			fFile = getOrCreateFile(PROJECT_NAME + "/" + FILE_NAME);
			fIsSetup = true;
		}
		// editor is opened each time
		if (fIsSetup && fMainEditor == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			fMainEditor = IDE.openEditor(page, fFile, true, true);
			if (fMainEditor instanceof StructuredTextEditor)
				fEditor = (StructuredTextEditor) fMainEditor;
			else if (fMainEditor != null) {
				Object adapter = fMainEditor.getAdapter(ITextEditor.class);
				if (adapter instanceof StructuredTextEditor)
					fEditor = (StructuredTextEditor) adapter;
			}
			if (fEditor == null)
				assertTrue("Unable to open structured text editor " + ((fMainEditor != null) ? fMainEditor.getClass().getName() : ""), false);
		}
	}

	protected void tearDown() throws Exception {
		// editor is closed each time
		if (fMainEditor != null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			page.closeEditor(fMainEditor, false);
			assertTrue("Unable to close editor", true);
			fMainEditor = null;
			fEditor = null;
		}
	}

	private void createProject(String projName) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private IFile getOrCreateFile(String filePath) {
		IFile blankFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		if (blankFile != null && !blankFile.exists()) {
			try {
				blankFile.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return blankFile;
	}

	public void testPropertySourceRemoval() throws BadLocationException {
		IDocument document = (IDocument) fEditor.getAdapter(IDocument.class);
		// set up the editor document
		document.replace(0, 0, "<test><myproperty props=\"yes\" /></test>");

		// set current selection in editor
		ISelection setSelection = new TextSelection(9, 0);
		fEditor.getSelectionProvider().setSelection(setSelection);

		// get current selection in editor
		Object item = null;
		ISelection selection = fEditor.getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			item = ((IStructuredSelection) selection).getFirstElement();

			IPropertySheetPage propertySheet = (IPropertySheetPage) fEditor.getAdapter(IPropertySheetPage.class);
			assertTrue("No ConfigurablePropertySheetPage found", propertySheet instanceof ConfigurablePropertySheetPage);
			if (propertySheet instanceof ConfigurablePropertySheetPage) {
				ConfigurablePropertySheetPage cps = (ConfigurablePropertySheetPage) propertySheet;
				PropertySheetConfiguration config = cps.getConfiguration();
				assertNotNull("No property sheet configuration found", config);

				IPropertySourceProvider provider = config.getPropertySourceProvider(cps);
				assertNotNull("No property sheet provider found", provider);

				IPropertySource source = provider.getPropertySource(item);
				if (source instanceof IPropertySourceExtension) {
					boolean canRemove = ((IPropertySourceExtension) source).isPropertyRemovable("props");
					assertTrue("Current property cannot be removed", canRemove);
					if (canRemove) {
						((IPropertySourceExtension) source).removeProperty("props");
						assertTrue("Current property cannot be removed", true);
						// force return here, to avoid last fall through
						// failing assert
						return;
					}
				}
			}
		}
		// if we get to here, always fail, since something went wrong.
		assertTrue("testPropertySourceRemoval test did not take expected path", false);
	}
}
