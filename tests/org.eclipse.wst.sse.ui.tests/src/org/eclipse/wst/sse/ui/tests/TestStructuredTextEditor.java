/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
import java.lang.reflect.Method;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentRegionProcessor;
import org.eclipse.wst.sse.ui.reconcile.ISourceReconcilingListener;

public class TestStructuredTextEditor extends TestCase {
	private final String PROJECT_NAME = "TestStructuredTextEditor";
	private final String FILE_NAME = "testStructuredTextEditor.xml";
	private static final int MAX_WAIT = 10000;
	private static final int TIME_DELTA = 50;

	private static StructuredTextEditor fEditor;
	private static IFile fFile;
	private static boolean fIsSetup = false;

	public TestStructuredTextEditor() {
		super("TestStructuredTextEditor");
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
	
	public void testSingleNonUIThreadUpdatesToEditorDocument() throws Exception {
		IFile file = getOrCreateFile(PROJECT_NAME + "/" + "testBackgroundChanges.xml");
		ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
		textFileBufferManager.connect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());

		ITextFileBuffer textFileBuffer = textFileBufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
		final IDocument document = textFileBuffer.getDocument();
		document.replace(0, 0, "<?xml encoding=\"UTF-8\" version=\"1.0\"?>\n");
		textFileBuffer.commit(new NullProgressMonitor(), true);

		String testText = document.get() + "<c/><b/><a/>";
		final int end = document.getLength();
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart openedEditor = IDE.openEditor(activePage, file);

		final boolean state[] = new boolean[]{false};
		Job changer = new Job("text changer") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					document.replace(end, 0, "<a/>");
					document.replace(end, 0, "<b/>");
					document.replace(end, 0, "<c/>");
				}
				catch (Exception e) {
					return new Status(IStatus.ERROR, SSEUIPlugin.ID, e.getMessage());
				}
				finally {
					state[0] = true;
				}
				return Status.OK_STATUS;
			}
		};
		changer.setUser(true);
		changer.setSystem(false);
		changer.schedule();

		while (!state[0]) {
			openedEditor.getSite().getShell().getDisplay().readAndDispatch();
		}

		String finalText = document.get();
		textFileBuffer.commit(new NullProgressMonitor(), true);
		textFileBufferManager.disconnect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
		activePage.closeEditor(openedEditor, false);
		assertEquals("Non-UI changes did not apply", testText, finalText);
	}

	
	/**
	 * Note: This test takes a while to run, but it's testing scalability after all.
	 * @throws Exception
	 */
	public void testManyNonUIThreadsUpdatingEditorDocument() throws Exception {
		final int numberOfJobs = 50;
		/**
		 * 15 minute timeout before we stop waiting for the change jobs to
		 * complete
		 */
		long timeout = 15*60*1000;

		long startTime = System.currentTimeMillis();
		IFile file = getOrCreateFile(PROJECT_NAME + "/" + "testManyBackgroundChanges.xml");
		ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
		textFileBufferManager.connect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());

		ITextFileBuffer textFileBuffer = textFileBufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
		final IDocument document = textFileBuffer.getDocument();
		document.replace(0, 0, "<?xml encoding=\"UTF-8\" version=\"1.0\"?>\n");
		textFileBuffer.commit(new NullProgressMonitor(), true);

		final int insertionPoint = document.getLength();
		// numberOfJobs Jobs, inserting 26 tags, each 4 characters long
		int testLength = insertionPoint + numberOfJobs * 4 * 26;
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		activePage.showView(IPageLayout.ID_PROGRESS_VIEW);
		IEditorPart openedEditor = IDE.openEditor(activePage, file);

		final int finished[] = new int[]{numberOfJobs};
		Job changers[] = new Job[numberOfJobs];
		for (int i = 0; i < changers.length; i++) {
			changers[i] = new Job("Text Changer " + Integer.toString(i)) {
				protected IStatus run(IProgressMonitor monitor) {
					try {
						char names[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
						for (int j = 0; j < names.length; j++) {
							document.replace(insertionPoint + 4 * j, 0, "<" + names[j] + "/>");
						}
					}
					catch (Exception e) {
						return new Status(IStatus.ERROR, SSEUIPlugin.ID, e.getMessage());
					}
					finally {
						finished[0]--;
					}
					return Status.OK_STATUS;
				}
			};
			changers[i].setUser(true);
			changers[i].setSystem(false);
		}
		for (int i = 0; i < changers.length; i++) {
			changers[i].schedule();
		}

		long runtime = 0;
		while (finished[0] > 0 && (runtime = System.currentTimeMillis()) - startTime < timeout) {
			openedEditor.getSite().getShell().getDisplay().readAndDispatch();
		}
		assertTrue("Test timed out", runtime - startTime < timeout);

		int finalLength = document.getLength();
		textFileBuffer.commit(new NullProgressMonitor(), true);
		textFileBufferManager.disconnect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
		activePage.closeEditor(openedEditor, false);
		assertEquals("Some non-UI changes did not apply", testLength, finalLength);
	}

	/**
	 * Test receiving the initial reconcile notification when the editor opens
	 */
	public void testInitialReconciling() throws Exception {
		IFile file = getOrCreateFile(PROJECT_NAME + "/" + "reconcilingtest.xml");
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final int[] state = new int[2];
		Arrays.fill(state, -1);
		ISourceReconcilingListener listener = new ISourceReconcilingListener() {
			int mod = 0;
			public void reconciled(IDocument document, IAnnotationModel model, boolean forced, IProgressMonitor progressMonitor) {
				state[1] = mod++;
			}
			
			public void aboutToBeReconciled() {
				state[0] = mod++;
			}
		};
		IEditorPart editor = IDE.openEditor(activePage, file, "org.eclipse.wst.sse.ui.StructuredTextEditor");
		try {
			assertTrue("Not a StructuredTextEditor", editor instanceof StructuredTextEditor);
			addReconcilingListener((StructuredTextEditor) editor, listener);
			waitForReconcile(state);
			assertTrue("Initial: Reconciling did not complete in a timely fashion", state[0] != -1 && state[1] != -1);
			assertTrue("Initial: aboutToBeReconciled not invoked first (" + state[0] +")", state[0] == 0);
			assertTrue("Initial: reconciled not invoked after aboutToBeReconciled (" + state[1] +")", state[1] == 1);
		} finally {
			if (editor != null && activePage != null) {
				activePage.closeEditor(editor, false);
			}
		}
	}

	/**
	 * Test that modifications to the editor's content will notify reconciling listeners
	 */
	public void testModificationReconciling() throws Exception {
		IFile file = getOrCreateFile(PROJECT_NAME + "/" + "reconcilingmodificationtest.xml");
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final int[] state = new int[2];
		Arrays.fill(state, -1);
		ISourceReconcilingListener listener = new ISourceReconcilingListener() {
			int mod = 0;
			public void reconciled(IDocument document, IAnnotationModel model, boolean forced, IProgressMonitor progressMonitor) {
				state[1] = mod++;
			}
			
			public void aboutToBeReconciled() {
				state[0] = mod++;
			}
		};
		IEditorPart editor = IDE.openEditor(activePage, file, "org.eclipse.wst.sse.ui.StructuredTextEditor");
		try {
			assertTrue("Not a StructuredTextEditor", editor instanceof StructuredTextEditor);
			addReconcilingListener((StructuredTextEditor) editor, listener);
			waitForReconcile(state);
			assertTrue("Initial: Reconciling did not complete in a timely fashion", state[0] != -1 && state[1] != -1);
			assertTrue("Initial: aboutToBeReconciled not invoked first (" + state[0] +")", state[0] == 0);
			assertTrue("Initial: reconciled not invoked after aboutToBeReconciled (" + state[1] +")", state[1] == 1);
			IDocument document = ((StructuredTextEditor) editor).getDocumentProvider().getDocument(editor.getEditorInput());
			assertTrue("Editor doesn't have a document", document != null);
			document.set("<?xml version=\"1.0\" ?>");
			Arrays.fill(state, -1);
			waitForReconcile(state);
			assertTrue("Modified: Reconciling did not complete in a timely fashion", state[0] != -1 && state[1] != -1);
			assertTrue("Modified: aboutToBeReconciled not invoked first (" + state[0] +")", state[0] == 2);
			assertTrue("Modified: reconciled not invoked after aboutToBeReconciled (" + state[1] +")", state[1] == 3);
		} finally {
			if (editor != null && activePage != null) {
				activePage.closeEditor(editor, false);
			}
		}
	}

	/**
	 * Test that an editor notifies reconciling listeners when the editor gets focus.
	 */
	public void testFocusedReconciling() throws Exception {
		IFile file = getOrCreateFile(PROJECT_NAME + "/" + "focustest.xml");
		IFile fileAlt = getOrCreateFile(PROJECT_NAME + "/" + "focustestAlt.xml");
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final int[] state = new int[2];
		Arrays.fill(state, -1);
		ISourceReconcilingListener listener = new ISourceReconcilingListener() {
			int mod = 0;
			public void reconciled(IDocument document, IAnnotationModel model, boolean forced, IProgressMonitor progressMonitor) {
				state[1] = mod++;
			}
			
			public void aboutToBeReconciled() {
				state[0] = mod++;
			}
		};
		IEditorPart editor = IDE.openEditor(activePage, file, "org.eclipse.wst.sse.ui.StructuredTextEditor");
		try {
			assertTrue("Not a StructuredTextEditor", editor instanceof StructuredTextEditor);
			addReconcilingListener((StructuredTextEditor) editor, listener);
			waitForReconcile(state);
			assertTrue("Initial: Reconciling did not complete in a timely fashion", state[0] != -1 && state[1] != -1);
			assertTrue("Initial: aboutToBeReconciled not invoked first (" + state[0] +")", state[0] == 0);
			assertTrue("Initial: reconciled not invoked after aboutToBeReconciled (" + state[1] +")", state[1] == 1);
			IDE.openEditor(activePage, fileAlt, "org.eclipse.wst.sse.ui.StructuredTextEditor");
			Arrays.fill(state, -1);
			IEditorPart editorPart = IDE.openEditor(activePage, file, "org.eclipse.wst.sse.ui.StructuredTextEditor");
			assertEquals("Didn't get the original editor back.", editor, editorPart);
			waitForReconcile(state);
			assertTrue("Modified: Reconciling did not complete in a timely fashion", state[0] != -1 && state[1] != -1);
			assertTrue("Modified: aboutToBeReconciled not invoked first (" + state[0] +")", state[0] == 2);
			assertTrue("Modified: reconciled not invoked after aboutToBeReconciled (" + state[1] +")", state[1] == 3);
		} finally {
			if (editor != null && activePage != null) {
				activePage.closeEditor(editor, false);
			}
		}
	}

	private void addReconcilingListener(StructuredTextEditor editor, ISourceReconcilingListener listener) throws Exception {
		Method mConfig = AbstractTextEditor.class.getDeclaredMethod("getSourceViewerConfiguration", null);
		mConfig.setAccessible(true);
		Object config = mConfig.invoke(editor, null);
		assertTrue("Did not get a source viewer configuration", config instanceof SourceViewerConfiguration);
		IReconciler reconciler = ((SourceViewerConfiguration) config).getReconciler(fEditor.getTextViewer());
		assertTrue("Reconciler is not a DirtyRegionProcessor", reconciler instanceof DocumentRegionProcessor);
		((DocumentRegionProcessor) reconciler).addReconcilingListener(listener);
	}

	public void waitForReconcile(int[] state) throws Exception {
		int time = 0;
		while ((state[0] == -1 || state[1] == -1) && time < MAX_WAIT) {
			Thread.sleep(TIME_DELTA);
			time += TIME_DELTA;
		}
	}
	
}
