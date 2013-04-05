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
package org.eclipse.wst.xml.ui.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.w3c.dom.NodeList;

/**
 * Test misc editor functions with an open xml editor.
 */
public class TestOpenEditorXML extends TestCase {
	private final String PROJECT_NAME = "TestOpenEditorXML";
	private final String FILE_NAME = "testStructuredTextEditorXML.xml";

	private static IEditorPart fEditor;
	private static IFile fFile;
	private static boolean fIsSetup = false;

	public TestOpenEditorXML() {
		super("TestStructuredTextEditorXML");
	}

	protected void setUp() throws Exception {
		// only create project and file once
		if (!fIsSetup) {
			// create project
			createProject(PROJECT_NAME);
			fFile = getOrCreateFile(PROJECT_NAME + "/" + FILE_NAME);
			fIsSetup = true;
		}

		assertTrue("Input file not accessible: " + fFile.getFullPath(), fFile.isAccessible());

		// editor is opened each time
		if (fIsSetup && fEditor == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			fEditor = IDE.openEditor(page, fFile, true, true);
			assertTrue("Unable to open structured text editor " + fEditor, (fEditor instanceof XMLMultiPageEditorPart) || (fEditor instanceof StructuredTextEditor));
		}
	}

	protected void tearDown() throws Exception {
		// editor is closed each time
		if (fEditor != null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			page.closeEditor(fEditor, false);
			assertTrue("Unable to close editor", true);
			fEditor = null;
		}
	}

	private void createProject(String projName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);

		project.create(description, new NullProgressMonitor());
		project.open(new NullProgressMonitor());
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

	/**
	 * Test setting text in document of open editor.
	 */
	public void testSetDocument() {
		IDocument document = (IDocument) fEditor.getAdapter(IDocument.class);
		try {
			document.set("<hello></hello>");
		}
		catch (Exception e) {
			assertTrue("Unable to set text in editor: " + e, false);
		}
	}

	/**
	 * Test structured document is reloaded on resource change
	 * 
	 */
	public void testBug151069() {
		IDocument doc = (IDocument) fEditor.getAdapter(IDocument.class);
		doc.set("<html><body><h1>Title</h1></body></html>");
		// set h1 to readonly
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IDOMModel model = null;
		try {
			model = (IDOMModel) modelManager.getExistingModelForEdit(doc);
			if (model != null) {
				NodeList nl = model.getDocument().getElementsByTagName("h1");
				IDOMElement h1 = (IDOMElement) nl.item(0);
				h1.setEditable(false, true);
			}
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}

		String newContent = "new content";
		((IDocumentExtension4) doc).set(newContent, fFile.getModificationStamp());
		assertEquals("Set contents in document with read only regions failed", newContent, doc.get());
	}
	
	public void testOpenOnLocalFileStore() throws Exception {
		// filebuffers for local files have a location
		IPath stateLocation = XMLUITestsPlugin.getDefault().getStateLocation();
		File file = stateLocation.append(FILE_NAME).toFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream contents = fFile.getContents();
		if (contents != null) {
			int c;
			byte bytes[] = new byte[2048];
			try {
				while ((c = contents.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				contents.close();
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
		}

		fileOutputStream.write(buffer.toByteArray());
		URI uri = URIUtil.toURI(new Path(file.getAbsolutePath()));
		IFileStore store = EFS.getStore(uri);
		FileStoreEditorInput input = new FileStoreEditorInput(store);
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IEditorPart editor = IDE.openEditor(page, input, "org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart");
		page.closeEditor(editor, false);
		assertTrue("Unable to open structured text editor " + fEditor, (fEditor instanceof XMLMultiPageEditorPart) || (fEditor instanceof StructuredTextEditor));
	}
}
