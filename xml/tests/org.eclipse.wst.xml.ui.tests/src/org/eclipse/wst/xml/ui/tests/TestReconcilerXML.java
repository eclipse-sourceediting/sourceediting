/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

/**
 * Tests reconciler in an open editor.
 */
public class TestReconcilerXML extends TestCase {
	private final String PROJECT_NAME = "TestReconcilerXML";
	private final String FILE_NAME = "TestReconcilerXMLfile.xml";
	final private String ANNOTATION_ERROR = "org.eclipse.wst.sse.ui.temp.error"; //$NON-NLS-1$
	final private String ANNOTATION_WARNING = "org.eclipse.wst.sse.ui.temp.warning"; //$NON-NLS-1$

	private static IEditorPart fEditor;
	private static IFile fFile;
	private static boolean fIsSetup = false;

	private boolean fReconcilerPref;

	public TestReconcilerXML() {
		super("TestReconcilerXML");
	}

	protected void setUp() throws Exception {
		// only create project and file once
		if (!fIsSetup) {
			// create project
			createProject(PROJECT_NAME);
			fFile = getOrCreateFile(PROJECT_NAME + "/" + FILE_NAME);
			fIsSetup = true;
		}

		// editor is opened each time
		if (fIsSetup && fEditor == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			fEditor = IDE.openEditor(page, fFile, true, true);
			if (!((fEditor instanceof XMLMultiPageEditorPart) || (fEditor instanceof StructuredTextEditor)))
				assertTrue("Unable to open structured text editor", false);
		}

		// turn on reconciling
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		fReconcilerPref = store.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		if (!fReconcilerPref)
			store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
	}

	protected void tearDown() throws Exception {
		// restore reconciling preference
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, fReconcilerPref);

		// editor is closed each time
		if (fEditor != null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			page.closeEditor(fEditor, false);
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
	 * Tests reconciler by verifying no errors/warnings found with well-formed
	 * xml.
	 */
	public void testWellFormed() {
		IDocument doc = (IDocument) fEditor.getAdapter(IDocument.class);
		doc.set("<html><body><h1>Title</h1></body></html>");
		ITextEditor textEditor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
		IAnnotationModel annoModel = textEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

		// verify well-formed xml
		try {
			Thread.sleep(5000);
			Iterator iter = annoModel.getAnnotationIterator();
			// make sure the only problem we find is the lack of a specified grammar
			while (iter.hasNext()) {
				Annotation anno = (Annotation) iter.next();
				String annoType = anno.getType();
				if ((annotationAccess.isSubtype(annoType, ANNOTATION_ERROR)) || (annotationAccess.isSubtype(annoType, ANNOTATION_WARNING))) {
					assertTrue("testReconciler: Unexpected initial annotations" + anno.getText(), anno.getText().indexOf("No grammar constraints") > -1);
				}
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests reconciler by verifying error/warning found with ill-formed xml.
	 * (missing close bracket)
	 */
	public void testIllFormedNoCloseBracket() {
		IDocument doc = (IDocument) fEditor.getAdapter(IDocument.class);
		doc.set("<html><body><h1>Title</h1></body></html>");
		ITextEditor textEditor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
		IAnnotationModel annoModel = textEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

		// verify ill-formed xml
		try {
			doc.replace(6, 6, "<body ");
			Thread.sleep(5000);
			boolean errorFound = false;
			Iterator iter = annoModel.getAnnotationIterator();
			StringBuffer buffer = new StringBuffer();
			while (iter.hasNext()) {
				Annotation anno = (Annotation) iter.next();
				String annoType = anno.getType();
				buffer.append("\n");
				buffer.append(anno.getText());
				if ((annotationAccess.isSubtype(annoType, ANNOTATION_ERROR)) || (annotationAccess.isSubtype(annoType, ANNOTATION_WARNING))) {
					errorFound = true;
				}
			}
			assertTrue("testReconciler: Did not find expected errors in: " + doc.get() + buffer.toString(), errorFound);
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests reconciler by verifying error/warning found with ill-formed xml.
	 * (missing attribute value)
	 */
	public void testIllFormedNoAttrValue() {
		IDocument doc = (IDocument) fEditor.getAdapter(IDocument.class);
		doc.set("<html><body><h1>Title</h1></body></html>");
		ITextEditor textEditor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
		IAnnotationModel annoModel = textEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

		// verify ill-formed xml
		try {
			doc.replace(6, 6, "<body hello>");
			Thread.sleep(5000);
			boolean errorFound = false;
			Iterator iter = annoModel.getAnnotationIterator();
			StringBuffer buffer = new StringBuffer();
			while (iter.hasNext()) {
				Annotation anno = (Annotation) iter.next();
				String annoType = anno.getType();
				buffer.append("\n");
				buffer.append(anno.getText());
				if ((annotationAccess.isSubtype(annoType, ANNOTATION_ERROR)) || (annotationAccess.isSubtype(annoType, ANNOTATION_WARNING))) {
					errorFound = true;
				}
			}
			assertTrue("testReconciler: Did not find expected errors in: " + doc.get() + buffer, errorFound);
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
