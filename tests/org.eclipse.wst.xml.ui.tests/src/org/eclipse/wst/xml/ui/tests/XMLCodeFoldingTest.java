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
package org.eclipse.wst.xml.ui.tests;

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
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

/**
 * <p>Tests that code folding annotations are correctly added/removed from XML Documents</p>
 * <p>All of these tests use the same project and when possible the same open documents</p>
 * 
 * @see org.eclipse.wst.xml.ui.tests.XMLCodeFoldingTest Similar Test - XML Code Folding Test
 * @see org.eclipse.wst.css.ui.tests.viewer.CSSCodeFoldingTest Similar Test - CSS Code Folding Test
 * @see org.eclipse.wst.dtd.ui.tests.viewer.DTDCodeFoldingTest Similar Test - DTD Code Folding Test
 */
public class XMLCodeFoldingTest extends TestCase {
	private static final String JOB_NAME_PROCESSING_DIRTY_REGIONS = "Processing Dirty Regions";
	
	private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
	private String previousWTPAutoTestNonInteractivePropValue = null;
	
	/**
	 * The name of the project that all of these tests will use
	 */
	private static final String PROJECT_NAME = "HTMLCodeFoldingTest";
	
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
	public XMLCodeFoldingTest() {
		super("XMLCodeFoldingTest");
	}
	
	/**
	 * Constructor that takes a test name.
	 * 
	 * @param name The name this test run should have.
	 */
	public XMLCodeFoldingTest(String name) {
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
		IFile file = getFile("XMLFoldingTest1.xml");
		
		StructuredTextEditor editor  = getEditor(file);
		
		List expectedPositions = new ArrayList();
		expectedPositions.add(new Position(40, 137));
		expectedPositions.add(new Position(63, 104));
		expectedPositions.add(new Position(146, 9));
		expectedPositions.add(new Position(72, 5));
		expectedPositions.add(new Position(49, 5));
		expectedPositions.add(new Position(87, 45));
		
		waitForReconcileThenVerify(editor.getTextViewer(), expectedPositions);
	}
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is removed</p>
	 */
	public void testRemoveNode() {
		IFile file = getFile("XMLFoldingTest1.xml");
		
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			doc.replace(87, 51, "");
			editor.doSave(null);
			
			final List expectedPositions = new ArrayList();
			expectedPositions.add(new Position(63, 53));
			expectedPositions.add(new Position(49, 5));
			expectedPositions.add(new Position(95, 9));
			expectedPositions.add(new Position(40, 86));
			expectedPositions.add(new Position(72, 5));
			
			waitForReconcileThenVerify(viewer, expectedPositions);
		} catch(BadLocationException e) {
			fail("Test is broken, replace location has become invalid.\n" + e.getMessage());
		}
	}
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is added</p>
	 */
	public void testAddNode() {
		IFile file = getFile("XMLFoldingTest2.xml");
		
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			doc.replace(151, 0, "\r\n<addMe>\r\n\r\n\r\n</addMe>");
			editor.doSave(null);
			
			List expectedPositions = new ArrayList();
			expectedPositions.add(new Position(72, 5));
			expectedPositions.add(new Position(40, 160));
			expectedPositions.add(new Position(87, 45));
			expectedPositions.add(new Position(63, 127));
			expectedPositions.add(new Position(153, 13));
			expectedPositions.add(new Position(49, 5));
			expectedPositions.add(new Position(146, 32));
			
			waitForReconcileThenVerify(viewer, expectedPositions);
		} catch(BadLocationException e) {
			fail("Test is broken, add location has become invalid.\n" + e.getMessage());
		}
	}
	
	/**
	 * <p><b>TEST:</b> that even though .xsl documents do not have a specifically specified
	 * folding strategy that by the power of higheracical content types the XML folding
	 * strategy is used for the XSLT document.
	 */
	public void testConfigTypeHeigheracryExploration() {
		IFile file = getFile("XSLFoldingTest1.xsl");
		
		StructuredTextEditor editor = getEditor(file);
		
		List expectedPositions = new ArrayList();
		expectedPositions.add(new Position(122, 69));
		expectedPositions.add(new Position(40, 168));
		
		waitForReconcileThenVerify(editor.getTextViewer(), expectedPositions);
		
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
				if(editorPart instanceof XMLMultiPageEditorPart) {
					XMLMultiPageEditorPart xmlEditorPart = (XMLMultiPageEditorPart)editorPart;
					editor = (StructuredTextEditor)xmlEditorPart.getAdapter(StructuredTextEditor.class);
				} else if(editorPart instanceof StructuredTextEditor) {
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
	 * @param expectedPositions check for annotations at these positions in the given <code>viewer</code>
	 * after the dirty region reconciler job has finished
	 */
	private void waitForReconcileThenVerify(final StructuredTextViewer viewer, final List expectedPositions) {
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
		
			verifyAnnotationPositions(viewer, expectedPositions);
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
			fail("There were " + expectedPositions.size() + " less folding annotations then expected");
		}
	}
}
