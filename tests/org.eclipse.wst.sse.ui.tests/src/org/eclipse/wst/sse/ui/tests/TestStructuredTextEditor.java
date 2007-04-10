/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

public class TestStructuredTextEditor extends TestCase {
	private final String PROJECT_NAME = "TestStructuredTextEditor";
	private final String FILE_NAME = "testStructuredTextEditor.xml";

	private static StructuredTextEditor fEditor;
	private static IFile fFile;
	private static boolean fIsSetup = false;

	public TestStructuredTextEditor() {
		super("TestStructredTextEditor");
	}

	protected void setUp() throws Exception {
		if (!fIsSetup) {
			// create project
			createProject(PROJECT_NAME);
			fFile = getOrCreateFile(PROJECT_NAME + "/" + FILE_NAME);
			fIsSetup = true;
		}

		if (fIsSetup && fEditor == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			IEditorInput input = new FileEditorInput(fFile);
			/*
			 * This should take care of testing init, createPartControl,
			 * beginBackgroundOperation, endBackgroundOperation methods
			 */
			IEditorPart part = page.openEditor(input, "org.eclipse.wst.sse.ui.StructuredTextEditor", true);
			if (part instanceof StructuredTextEditor)
				fEditor = (StructuredTextEditor) part;
			else
				assertTrue("Unable to open structured text editor", false);
		}
	}

	protected void tearDown() throws Exception {
		if (fEditor != null) {
			/*
			 * This should take care of testing close and dispose methods
			 */
			fEditor.close(false);
			assertTrue("Unable to close editor", true);
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

	protected IFile getOrCreateFile(String filePath) {
		IFile blankJspFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		if (blankJspFile != null && !blankJspFile.exists()) {
			try {
				blankJspFile.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return blankJspFile;
	}

	public void testDoSaving() {
		fEditor.doRevertToSaved();
		assertTrue("Unable to revert to saved", true);
		fEditor.doSave(new NullProgressMonitor());
		assertTrue("Unable to save", true);
	}

	public void testEditorContextMenuAboutToShow() {
		IMenuManager menu = new MenuManager();
		fEditor.editorContextMenuAboutToShow(menu);
		assertTrue("Unable to prepare for context menu about to show", true);
		menu.dispose();
		menu = null;
	}

	public void testGetAdapter() {
		Object adapter = fEditor.getAdapter(IShowInTargetList.class);
		assertTrue("Get adapter for show in target failed", adapter instanceof IShowInTargetList);
	}

	public void testGetSetEditorPart() {
		fEditor.setEditorPart(null);
		assertTrue("Unable to set editor part", true);
		IEditorPart part = fEditor.getEditorPart();
		assertTrue("Did not get expected editor part", part instanceof StructuredTextEditor);
	}

	public void testInitializeDocumentProvider() {
		fEditor.initializeDocumentProvider(null);
		assertTrue("Unable to initialize document provider", true);
	}

	public void testGetOrientation() {
		int or = fEditor.getOrientation();
		assertEquals(SWT.LEFT_TO_RIGHT, or);
	}

	public void testGetSelectionProvider() {
		ISelectionProvider provider = fEditor.getSelectionProvider();
		assertNotNull("Editor's selection provider was null", provider);
	}

	public void testGetTextViewer() {
		StructuredTextViewer viewer = fEditor.getTextViewer();
		assertNotNull("Editor's text viewer was null", viewer);
	}

	public void testRememberRestoreSelection() {
		fEditor.rememberSelection();
		assertTrue("Unable to remember editor selection", true);
		fEditor.restoreSelection();
		assertTrue("Unable to restore editor selection", true);
	}

	public void testSafelySanityCheck() {
		fEditor.safelySanityCheckState(fEditor.getEditorInput());
		assertTrue("Unable to safely sanity check editor state", true);
	}

	public void testShowBusy() {
		fEditor.showBusy(false);
		assertTrue("Unable to show editor is busy", true);
	}

	public void testUpdate() {
		fEditor.update();
		assertTrue("Unable to update editor", true);
	}
}
