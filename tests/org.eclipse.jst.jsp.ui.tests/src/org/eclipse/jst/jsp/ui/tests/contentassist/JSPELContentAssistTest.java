/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPELContentAssistProcessor;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

public class JSPELContentAssistTest extends TestCase {
	private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
	private String previousWTPAutoTestNonINteractivePropValue = null;
	
	private static final String TEST_PROJECT_NAME = "testJSPELContentAssist";
	private static final String CONTENT_DIR = "WebContent";
	
	private static final String STRUCTURED_EDITOR_TYPE = "org.eclipse.wst.sse.ui.StructuredTextEditor.test";
	
	private static final String FILE_0_PATH = CONTENT_DIR + IPath.SEPARATOR + "Test0.jsp";
	private static final String FILE_1_PATH = CONTENT_DIR + IPath.SEPARATOR + "Test1.jsp";
	private static final String FILE_2_PATH = CONTENT_DIR + IPath.SEPARATOR + "Test2.jsp";
	
	private static final String[] TEST_0_EXPECTED_PROPS = {
		"errorData", "exception", "expressionEvaluator", "out", "page", "request", "response",
		"servletConfig", "servletContext", "session", "variableResolver"};
	private static final String[] TEST_1_EXPECTED_PROPS = {"request", "response"};
	private static final String[] TEST_2_EXPECTED_PROPS = {"request"};
	
	private static final int TEST_0_DOC_LOC = 371;
	private static final int TEST_1_DOC_LOC = 372;
	private static final int TEST_2_DOC_LOC = 374;
	
	private IProject project;
	private IWorkbenchPage page;
	
	public JSPELContentAssistTest() {
		super("JSP EL Content Assist Tests");
	}
	
	public JSPELContentAssistTest(String name) {
		super(name);
	}
	
	/**
	 * Do set up, ignore all pop ups during test
	 */
	protected void setUp() throws Exception {
		super.setUp();
		initializeResource();
		String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
		
		if (noninteractive != null) {
			previousWTPAutoTestNonINteractivePropValue = noninteractive;
		} else {
			previousWTPAutoTestNonINteractivePropValue = "false";
		}
		System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		if (previousWTPAutoTestNonINteractivePropValue != null) {
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonINteractivePropValue);
		}
		
		try {
			this.project.delete(true, null);
		} catch (Exception e ) {
			//ignore, this is not part of the test, just useful cleanup
		}
	}
	
	/**
	 * Set up the project and workbench
	 * 
	 * @throws Exception
	 */
	private void initializeResource() throws Exception {
		ProjectUnzipUtility fProjUtil = new ProjectUnzipUtility();
		// root of workspace directory
		Location platformLocation = Platform.getInstanceLocation();
		// platform location may be null -- depends on "mode" of platform
		if (platformLocation != null) {
			File zipFile = FileUtil.makeFileFor(
				ProjectUnzipUtility.PROJECT_ZIPS_FOLDER,
				TEST_PROJECT_NAME + ProjectUnzipUtility.ZIP_EXTENSION,
				ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
			fProjUtil.unzipAndImport(zipFile, platformLocation.getURL().getPath());
			fProjUtil.initJavaProject(TEST_PROJECT_NAME);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			this.project = root.getProject(TEST_PROJECT_NAME);
			
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			this.page = workbenchWindow.getActivePage();
		}
	}
	
	public void testELProposals_0() throws Exception {		
		ICompletionProposal[] props = getCompletionProposals(FILE_0_PATH, TEST_0_DOC_LOC);
		verifyProposals(props, TEST_0_EXPECTED_PROPS);
	}
	
	public void testELProposals_1() throws Exception {		
		ICompletionProposal[] props = getCompletionProposals(FILE_1_PATH, TEST_1_DOC_LOC);
		verifyProposals(props, TEST_1_EXPECTED_PROPS);
	}
	
	public void testELProposals_2() throws Exception {		
		ICompletionProposal[] props = getCompletionProposals(FILE_2_PATH, TEST_2_DOC_LOC);
		verifyProposals(props, TEST_2_EXPECTED_PROPS);
	}
	
	public void testELChosenProposalCompletion() throws Exception {
		IFile file = getFile(FILE_1_PATH);
		ICompletionProposal[] props = getCompletionProposals(file, TEST_1_DOC_LOC);
		verifyProposals(props, TEST_1_EXPECTED_PROPS);
		
		StructuredTextEditor editor = getEditor(file);
		StructuredTextViewer viewer = editor.getTextViewer();
		IDocument document = viewer.getDocument();
		props[0].apply(document);
		
		String inserted = document.get(TEST_1_DOC_LOC-1, TEST_1_EXPECTED_PROPS[0].length());
		assertEquals(
			"The completed proposal " + inserted + " does not match the expected completion " + TEST_1_EXPECTED_PROPS[0],
			TEST_1_EXPECTED_PROPS[0], inserted);
		
		editor.doSave(null);
	}
	
	/**
	 * Get a file contained in the project
	 * 
	 * @param filePath
	 * @return
	 */
	private IFile getFile(String filePath) {
		IFile retFile = this.project.getFile(filePath);
		
		assertTrue(retFile + " testing file does not exist.", retFile.exists());
		
		return retFile;
	}
	
	/**
	 * use a viewer and document location to generate completion proposals for that location in the viewer
	 * 
	 * @param filePath
	 * @param documentLocation
	 * @return
	 * @throws PartInitException
	 */
	private ICompletionProposal[] getCompletionProposals(String filePath, int documentLocation) throws PartInitException {
		IFile file = getFile(filePath);
		StructuredTextEditor editor = getEditor(file);
		StructuredTextViewer viewer = editor.getTextViewer();
		return getCompletionProposals(viewer, documentLocation);
	}
	
	
	/**
	 * use a viewer and document location to generate completion proposals for that location in the viewer
	 * 
	 * @param file
	 * @param documentLocation
	 * @return
	 * @throws PartInitException
	 */
	private ICompletionProposal[] getCompletionProposals(IFile file, int documentLocation) throws PartInitException {
		StructuredTextEditor editor = getEditor(file);
		StructuredTextViewer viewer = editor.getTextViewer();
		return getCompletionProposals(viewer, documentLocation);
	}
	
	/**
	 * use a viewer and document location to generate completion proposals for that location in the viewer
	 * 
	 * @param viewer
	 * @param documentLocation
	 * @return
	 * @throws PartInitException
	 */
	private ICompletionProposal[] getCompletionProposals(StructuredTextViewer viewer, int documentLocation) throws PartInitException {
		JSPELContentAssistProcessor processor = new JSPELContentAssistProcessor();
		ICompletionProposal[] props = processor.computeCompletionProposals(viewer, documentLocation);
		
		return props;
	}
	
	/**
	 * Given a file returns a viewer for that file
	 * 
	 * @param file
	 * @return
	 * @throws PartInitException
	 */
	private StructuredTextEditor getEditor(IFile file) throws PartInitException {
		IEditorInput input = new FileEditorInput(file);
		IEditorPart part = this.page.openEditor(input, STRUCTURED_EDITOR_TYPE, true);
		
		assertTrue("Unable to open structured text editor", part instanceof StructuredTextEditor);
		
		return (StructuredTextEditor) part;
	}
	
	/**
	 * Given generated proposals be sure that all of the epxted proposals are contained in the generated ones
	 * @param props
	 * @param expectedProps
	 */
	private void verifyProposals(ICompletionProposal[] props, String[] expectedProps) {
		for(int i = 0; i < expectedProps.length; ++i) {
			assertTrue("The expected proposal \"" + expectedProps[i] + "\" was not given",
				findPropsoal(props, expectedProps[i]));
		}
	}
	
	/**
	 * Given an expected proposal string check to be sure it exists in the given proposals
	 * 
	 * @param props
	 * @param expectedProp
	 * @return
	 */
	private boolean findPropsoal(ICompletionProposal[] props, String expectedProp) {
		boolean found = false;
		
		for(int i = 0; i < props.length && !found; ++i) {
			found = props[i].getDisplayString().startsWith(expectedProp);
		}
		
		return found;
	}
}
