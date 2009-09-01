/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.css.ui.tests.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.css.core.internal.document.CSSStructuredDocumentRegionContainer;
import org.eclipse.wst.css.ui.internal.projection.CSSFoldingStrategy;
import org.eclipse.wst.css.ui.tests.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.w3c.dom.css.CSSStyleRule;

/**
 * <p>Tests that code folding annotations are correctly added/removed from CSS Documents</p>
 * <p>All of these tests use the same project and when possible the same open documents</p>
 * 
 * @see org.eclipse.wst.xml.ui.tests.XMLCodeFoldingTest Similar Test - XML Code Folding Test
 * @see org.eclipse.wst.css.ui.tests.viewer.CSSCodeFoldingTest Similar Test - CSS Code Folding Test
 * @see org.eclipse.wst.dtd.ui.tests.viewer.DTDCodeFoldingTest Similar Test - DTD Code Folding Test
 */
public class CSSCodeFoldingTest extends TestCase {
	/**
	 * The name of the reconciler job that adds the folding annotations to a <code>StructuredTextEditor</code>
	 */
	private static final String JOB_NAME_PROCESSING_DIRTY_REGIONS = "Processing Dirty Regions";
	
	private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
	private String previousWTPAutoTestNonInteractivePropValue = null;
	
	/**
	 * The name of the project that all of these tests will use
	 */
	private static final String PROJECT_NAME = "CSSCodeFoldingTest";
	
	/**
	 * The location of the testing files
	 */
	private static final String PROJECT_FILES = "/testresources/folding";
	
	/**
	 * the initial set up for these tests should only happen once
	 */
	private static boolean fIsSetup = false;
	
	/**
	 * After the last test the project that all of the tests were using should be deleted
	 */
	private static boolean fIsLastTest = false;
	
	/**
	 * The project that all of the tests use
	 */
	private static IProject fProject;
	
	/**
	 * Used to keep track of the already open editors so that the tests don't go through
	 * the trouble of opening the same editors over and over again
	 */
	private static Map fFileToEditorMap = new HashMap();
	
	/**
	 * Default constructor
	 */
	public CSSCodeFoldingTest() {
		super("CSSCodeFoldingTest");
	}
	
	/**
	 * Constructor that takes a test name.
	 * 
	 * @param name The name this test run should have.
	 */
	public CSSCodeFoldingTest(String name) {
		super(name);
	}
	
	/**
	 * This is run once before each test, some things should only happen once, others for each test
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		if(!fIsSetup) {
			fIsSetup = true;
			fIsLastTest = false;
			initializeResources();
		}
		
		String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
		
		if (noninteractive != null) {
			previousWTPAutoTestNonInteractivePropValue = noninteractive;
		} else {
			previousWTPAutoTestNonInteractivePropValue = "false";
		}
		System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
	}

	/**
	 * <p>This is run once after each test, some things should happen after each test,
	 * some only after the last test.</p>
	 * 
	 * <p><b>IMPORTANT:</b> Be sure that <code>fIsLastTest</code> is set to true at the end
	 * of the last test</p>
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		if(fIsLastTest) {
			fIsLastTest = false;
			fProject.delete(true, null);
		}
		
		if (previousWTPAutoTestNonInteractivePropValue != null) {
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
		}
	}
	
	/**
	 * <p><b>TEST:</b> the initially placed folding annotations</p>
	 */
	public void testInitFolding() {
		IFile file = getFile("CSSFoldingTest1.css");
		StructuredTextEditor editor  = getEditor(file);
		
		String[] keyWords = {"body", "#header", "#header form", "#header .btn", ".message"};
		waitForReconcileThenVerify(editor, keyWords);
	}
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is removed</p>
	 */
	public void testRemoveNode() {
		IFile file = getFile("CSSFoldingTest1.css");
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			//find the position to remove
			String[] initKeyWords = {"#header"};
			List initExpectedPositions = getExpectedPositions(editor, initKeyWords);
			Position removalPos = (Position)initExpectedPositions.remove(0);
			
			//remove the position
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			doc.replace(removalPos.offset, removalPos.length, "");
			editor.doSave(null);
			
			//verify
			String[] keyWords = {"body", "#header form", "#header .btn", ".message"};
			waitForReconcileThenVerify(editor, keyWords);
		} catch(BadLocationException e) {
			fail("Test is broken, replace location is invalid.\n" + e.getMessage());
		}
	}
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is added</p>
	 */
	public void testAddNode() {
		IFile file = getFile("CSSFoldingTest2.css");
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			//find the position to add the new node after
			String[] initKeyWords = {"#header .btn"};
			List initExpectedPositions = getExpectedPositions(editor, initKeyWords);
			Position insertAfterPos = (Position)initExpectedPositions.get(0);
			
			//add the node
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			String newNodeText = "\n.newClass {\nborder: 1px solid black;\n}\n";
			doc.replace(insertAfterPos.offset+insertAfterPos.length+1, 0, newNodeText);
			editor.doSave(null);
			
			//verify
			String[] keyWords = {"body", "#header", "#header form", "#header .btn", ".message", ".newClass"};
			waitForReconcileThenVerify(editor, keyWords);
		} catch(BadLocationException e) {
			fail("Test is broken, add location is invalid.\n" + e.getMessage());
		}
		
		//TODO: move this to the last test in this file
		fIsLastTest = true;
	}
	
	/**
	 * Set up the project and workbench, this should only be done once
	 */
	private void initializeResources() {
		fProject = ProjectUtil.createProject(PROJECT_NAME, null, null);
		ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
	}
	
	/**
	 * <p>Given a file name in <code>fProject</code> attempts to get an <code>IFile</code>
	 * for it, if the file doesn't exist the test fails.</p>
	 * 
	 * @param name the name of the file to get
	 * @return the <code>IFile</code> associated with the given <code>name</code>
	 */
	private static IFile getFile(String name) {
		IFile file = fProject.getFile(name);
		assertTrue("Test file " + file + " can not be found", file.exists());
		
		return file;
	}
	
	/**
	 * <p>Given a <code>file</code> get an editor for it. If an editor has already
	 * been retrieved for the given <code>file</code> then return the same already
	 * open editor.</p>
	 * 
	 * @param file open and return an editor for this
	 * @return <code>StructuredTextEditor</code> opened from the given <code>file</code>
	 */
	private StructuredTextEditor getEditor(IFile file)  {
		StructuredTextEditor editor = (StructuredTextEditor)fFileToEditorMap.get(file);
		
		if(editor == null) {
			try {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IEditorPart editorPart = IDE.openEditor(page, file, true, true);
				if(editorPart instanceof StructuredTextEditor) {
					editor = ((StructuredTextEditor)editorPart);
				} else {
					fail("Unable to open structured text editor");
				}
				
				if(editor != null) {
					fFileToEditorMap.put(file, editor);
				} else {
					fail("Could not open viewer for " + file);
				}
			} catch (Exception e) {
				fail("Could not open editor for " + file + " exception: " + e.getMessage());
			}
		}
		
		return editor;
	}
	
	/**
	 * Waits for the dirty region reconciler to finish and then verifies that only and all
	 * of the <code>expectedPositions</code> have folding annotations in the given <code>viewer</code>
	 * 
	 * @param viewer check for annotations at the given <code>expectedPositions</code>
	 * in here after the dirty region reconciler job has finished
	 * @param keyWords check for annotations at the positions that these key words are in,
	 * in the given <code>viewer</code> after the dirty region reconciler job has finished
	 */
	private void waitForReconcileThenVerify(StructuredTextEditor editor, String[] keyWords) {
		Job[] jobs = Job.getJobManager().find(null);
		Job job =  null;
		for(int i = 0; i < jobs.length && job == null; ++i) {
			if(jobs[i].getName().equals(JOB_NAME_PROCESSING_DIRTY_REGIONS)) {
				job = jobs[i];
			}
		}
		
		try {
			if(job != null) {
				//wait for dirty region reconciler job to finish before verifying annotations
				job.join();
			}
			
			//wait over, now verify
			List expectedPositions = getExpectedPositions(editor, keyWords);
			verifyAnnotationPositions(editor.getTextViewer(), expectedPositions);
		} catch (InterruptedException e) {
			fail("Could not join job " + job + "\n" + e.getMessage());
		}
	}
	
	/**
	 * Verifies that only and all of the <code>expectedPositions</code> have folding annotations
	 * in the given <code>viewer</code>
	 * 
	 * @param viewer check for annotations at the given <code>expectedPositions</code> in here 
	 * @param expectedPositions check for annotations at these positions in the given <code>viewer</code>
	 */
	private void verifyAnnotationPositions(StructuredTextViewer viewer, List expectedPositions) {
		ProjectionAnnotationModel projectionModel = viewer.getProjectionAnnotationModel();
		Iterator annotationIter = projectionModel.getAnnotationIterator();
		
		while(annotationIter.hasNext()) {
			Object obj = annotationIter.next();
			if(obj instanceof ProjectionAnnotation) {
				ProjectionAnnotation annotation = (ProjectionAnnotation)obj;
				Position pos = projectionModel.getPosition(annotation);
				
				boolean found = expectedPositions.remove(pos);
				
				assertTrue("Position " + pos + " is not one of the expected positions", found);
			}
		}
		
		if(expectedPositions.size() != 0 ) {
			Iterator iter = expectedPositions.iterator();
			String message = "The following expected folding annotatinos could not be found:";
			while(iter.hasNext()) {
				message += "\n\t" + iter.next();
			}
			fail(message);
		}
	}
	
	/**
	 * <p>Searches the document associated with the given {@link StructuredTextEditor} for each
	 * of the given <code>keyWords</code> and then uses the logic from the folding strategy
	 * to determine the expected position of a folding annotation.</p>
	 * 
	 * <p><b>NOTE:</b> see {@link #calcFoldPosition(IndexedRegion)} for an important note</p>
	 * 
	 * @param editor the {@link StructuredTextEditor} to search for the given <code>keyWords</code>
	 * @param keyWords a list of text markers in regions that should have a folding annotations
	 * 
	 * @return the {@link Position}s that there should be folding annotations on based on the
	 * given <code>keyWords</code>
	 */
	private List getExpectedPositions(StructuredTextEditor editor, String[] keyWords) {
		List expectedPositions = new ArrayList(keyWords.length);
		
		IDocument doc = editor.getTextViewer().getDocument();
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
		IStructuredDocument structuredDoc = model.getStructuredDocument();
		String text = structuredDoc.getText();
		
		for(int i = 0; i < keyWords.length; ++i) {
			int offsetOfKeyword = text.indexOf(keyWords[i]);
			IndexedRegion indexedRegion = model.getIndexedRegion(offsetOfKeyword);

			Position pos = calcFoldPosition(indexedRegion);

			if(pos != null) {
				expectedPositions.add(pos);
			}
		}
		
		return expectedPositions;
	}
	
	/**
	 * <p>This is an almost exact copy of {@link CSSFoldingStrategy#calcNewFoldPosition}</p>
	 * 
	 * <p>This has to be done because these tests have to calculate the expected folding
	 * locations on the fly because different OSs end up with different character counts
	 * because of line endings</p>
	 * 
	 * <p>So unfortunately this logic is not really being tested by these tests, but the
	 * more complicated and more likely to break logic of updating/adding/etc folding
	 * locations is still being tested.</p>
	 * 
	 * @see CSSFoldingStrategy#calcNewFoldPosition
	 */
	private Position calcFoldPosition(IndexedRegion indexedRegion) {
		Position newPos = null;

		CSSStructuredDocumentRegionContainer node = (CSSStructuredDocumentRegionContainer)indexedRegion;
		
		int start = node.getStartOffset();
		//so that multi-line CSS selector text does not get folded
		if(node instanceof CSSStyleRule) {
			CSSStyleRule rule = (CSSStyleRule)node;
			start += rule.getSelectorText().length();
		}
		
		//-1 for the end brace
		int length = node.getEndOffset()-start-1;

		if(length >= 0) {
			newPos = new Position(start,length);
		}
	
		return newPos;
	}
}
