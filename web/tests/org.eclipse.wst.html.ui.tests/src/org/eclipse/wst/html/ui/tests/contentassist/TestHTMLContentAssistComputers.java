/*******************************************************************************
 * Copyright (c) 2010, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.html.ui.tests.contentassist;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.internal.contentassist.resources.AbstractWebResourcesCompletionProposalComputer;
import org.eclipse.wst.html.ui.internal.contentassist.resources.CSSWebResourcesCompletionProposalComputer;
import org.eclipse.wst.html.ui.internal.contentassist.resources.HrefWebResourcesCompletionProposalComputer;
import org.eclipse.wst.html.ui.internal.contentassist.resources.ImageWebResourcesCompletionProposalComputer;
import org.eclipse.wst.html.ui.internal.contentassist.resources.ScriptWebResourcesCompletionProposalComputer;
import org.eclipse.wst.html.ui.tests.ProjectUtil;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestHTMLContentAssistComputers extends TestCase {
	/** The name of the project that all of these tests will use */
	private static final String PROJECT_NAME = "TestHTMLContentAssistComputers";
	
	/** so we don't have to depend on JSDT */
	private static final String JAVA_SCRIPT_NATURE_ID = "org.eclipse.wst.jsdt.core.jsNature";
	
	/** The location of the testing files */
	private static final String PROJECT_FILES = "/testresources/contentassist";
	
	/** The project that all of the tests use */
	static IProject fProject;
	
	/**
	 * Used to keep track of the already open editors so that the tests don't go through
	 * the trouble of opening the same editors over and over again
	 */
	static Map<IFile, StructuredTextEditor> fFileToEditorMap = new HashMap<>();
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public TestHTMLContentAssistComputers() {
		super("Test HTML Content Assist Computers");
	}
	
	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public TestHTMLContentAssistComputers(String name) {
		super(name);
	}
	
	/**
	 * <p>Use this method to add these tests to a larger test suite so set up
	 * and tear down can be performed</p>
	 * 
	 * @return a {@link TestSetup} that will run all of the tests in this class
	 * with set up and tear down.
	 */
	public static Test suite() {
		TestSuite ts = new TestSuite(TestHTMLContentAssistComputers.class, "Test HTML Content Assist Computers");
		return new TestHTMLContentAssistComputersSetup(ts);
	}
	
	public void testEmptyDocument() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {7, 6, 1, 0, 0, 7};
		runProposalTest("test0.html", 0, 0, expectedProposalCounts);
	}
	
	public void testAfterDocTypeBeforeHTMLTagProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {8, 8, 0, 0, 0, 8};
		runProposalTest("test1.html", 1, 0, expectedProposalCounts);
	}
	
	public void testAfterDocTypeBeforeEmptyDocProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {9, 8, 1, 0, 0, 9};
		runProposalTest("test2.html", 1, 0, expectedProposalCounts);
	}
	
	public void testBodyTagChildElementProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {78, 8, 70, 0, 0, 78};
		runProposalTest("test1.html", 8, 0, expectedProposalCounts);
	}
	
	public void testPTagChildElementProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {54, 8, 46, 0, 0, 54};
		runProposalTest("test1.html", 14, 0, expectedProposalCounts);
	}
	
	public void testDIVTagChildElementProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {78, 8, 70, 0, 0, 78};
		runProposalTest("test1.html", 18, 0, expectedProposalCounts);
	}

/*
	public void testCommentTagChildElementProposals() throws Exception {
		// default page (this never should have worked)
		int[] expectedProposalCounts = new int[] {0};
		runProposalTest("test1.html", 22, 0, expectedProposalCounts);
	}
*/

	public void testDIVTagAttributeNameProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {18, 0, 18, 0, 0, 18};
		runProposalTest("test1.html", 17, 5, expectedProposalCounts);
	}
	
	public void testFinishClosingTagNameProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {1, 0, 1, 0, 0, 1};
		runProposalTest("test4.html", 9, 9, expectedProposalCounts);
	}
	
	public void testFinishClosingTagProposals() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {2, 0, 2, 0, 0, 2};
		runProposalTest("test4.html", 10, 0, expectedProposalCounts);
	}
	
	public void testFinishClosingTagNameProposalsXHTML() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {1, 0, 1, 0, 0, 1};
		runProposalTest("test5.xhtml", 9, 9, expectedProposalCounts);
	}
	
	public void testFinishClosingTagProposalsXHTML() throws Exception {
		// default page, templates page, tags page, default page again
		int[] expectedProposalCounts = new int[] {1, 0, 2, 0, 0, 2};
		runProposalTest("test5.xhtml", 10, 0, expectedProposalCounts);
	}

	public void testResourceProposalsForAHref() throws Exception {
		IFile referencePoint = fProject.getFile("testResources.html");
		HrefWebResourcesCompletionProposalComputer proposalComputer = new HrefWebResourcesCompletionProposalComputer();
		Method findMatchingPaths = AbstractWebResourcesCompletionProposalComputer.class.getDeclaredMethod("findMatchingPaths", IResource.class);
		assertNotNull("findMatchingPaths", findMatchingPaths);
		findMatchingPaths.setAccessible(true);
		IPath[] paths = (IPath[]) findMatchingPaths.invoke(proposalComputer, referencePoint);
		assertNotNull("paths", paths);
		String[] strings = new String[paths.length];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = paths[i].toString();
		}
		assertTrue(String.valueOf(paths.length).concat(StringUtils.pack(strings)), paths.length >= 6);
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.css")));
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.js")));
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.txt")));
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.css")));
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.js")));
		assertTrue(StringUtils.pack(strings), Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.txt")));
	}

	public void testResourceProposalsForImgSrc() throws Exception {
		IFile referenceFile = fProject.getFile("testResources.html");
		ImageWebResourcesCompletionProposalComputer proposalComputer = new ImageWebResourcesCompletionProposalComputer();
		Method findMatchingPaths = AbstractWebResourcesCompletionProposalComputer.class.getDeclaredMethod("findMatchingPaths", IResource.class);
		assertNotNull("findMatchingPaths", findMatchingPaths);
		findMatchingPaths.setAccessible(true);
		IPath[] paths = (IPath[]) findMatchingPaths.invoke(proposalComputer, referenceFile);
		assertNotNull("paths", paths);
		String[] strings = new String[paths.length];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = paths[i].toString();
		}
		assertEquals(String.valueOf(paths.length).concat(StringUtils.pack(strings)), 3, paths.length);
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.png")));
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.gif")));
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.png")));
	}

	public void testResourceProposalsForLinkHref() throws Exception {
		IFile referencePoint = fProject.getFile("testResources.html");
		CSSWebResourcesCompletionProposalComputer proposalComputer = new CSSWebResourcesCompletionProposalComputer();
		Method findMatchingPaths = AbstractWebResourcesCompletionProposalComputer.class.getDeclaredMethod("findMatchingPaths", IResource.class);
		assertNotNull("findMatchingPaths", findMatchingPaths);
		findMatchingPaths.setAccessible(true);
		IPath[] paths = (IPath[]) findMatchingPaths.invoke(proposalComputer, referencePoint);
		assertNotNull("paths", paths);
		String[] strings = new String[paths.length];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = paths[i].toString();
		}
		assertEquals(String.valueOf(paths.length).concat(StringUtils.pack(strings)), 2, paths.length);
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.css")));
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.css")));
	}
	
	public void testResourceProposalsForScriptSrc() throws Exception {
		IFile referencePoint = fProject.getFile("testResources.html");
		ScriptWebResourcesCompletionProposalComputer proposalComputer = new ScriptWebResourcesCompletionProposalComputer();
		Method findMatchingPaths = AbstractWebResourcesCompletionProposalComputer.class.getDeclaredMethod("findMatchingPaths", IResource.class);
		assertNotNull("findMatchingPaths", findMatchingPaths);
		findMatchingPaths.setAccessible(true);
		IPath[] paths = (IPath[]) findMatchingPaths.invoke(proposalComputer, referencePoint);
		assertNotNull("paths", paths);
		String[] strings = new String[paths.length];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = paths[i].toString();
		}
		assertEquals(String.valueOf(paths.length).concat(StringUtils.pack(strings)), 2, paths.length);
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/alsoempty.js")));
		assertTrue(Arrays.asList(paths).stream().map((p)->p.toString()).anyMatch((s)->s.endsWith("/empty.js")));
	}
	
	/**
	 * <p>Run a proposal test by opening the given file and invoking content assist for
	 * each expected proposal count at the given line number and line character
	 * offset and then compare the number of proposals for each invocation (pages) to the
	 * expected number of proposals.</p>
	 * 
	 * @param fileName
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalCounts
	 * @throws Exception
	 */
	private static ICompletionProposal[][] runProposalTest(String fileName,
			int lineNum, int lineRelativeCharOffset,
			int[] expectedProposalCounts) throws Exception{
		
		IFile file = getFile(fileName);
		StructuredTextEditor editor  = getEditor(file);
		StructuredTextViewer viewer = editor.getTextViewer();
		int offset = editor.getDocumentProvider().getDocument(editor.getEditorInput()).getLineOffset(lineNum) + lineRelativeCharOffset;

		ICompletionProposal[][] pages = getProposals(viewer, offset, expectedProposalCounts.length);
		
		verifyProposalCounts(pages, expectedProposalCounts);
		
		return pages;
	}
	
	/**
	 * <p>Invoke content assist on the given viewer at the given offset, for the given number of pages
	 * and return the results of each page</p>
	 * 
	 * @param viewer
	 * @param offset
	 * @param pageCount
	 * @return
	 * @throws Exception
	 */
	private static ICompletionProposal[][] getProposals(StructuredTextViewer viewer, int offset, int pageCount) throws Exception {
		//setup the viewer
		StructuredTextViewerConfigurationHTML configuration = new StructuredTextViewerConfigurationHTML();
		ContentAssistant contentAssistant = (ContentAssistant)configuration.getContentAssistant(viewer);
		viewer.configure(configuration);
		viewer.setSelectedRange(offset, 0);
		
		//get the processor
		String partitionTypeID = viewer.getDocument().getPartition(offset).getType();
		assertEquals("partition type", IHTMLPartitions.HTML_DEFAULT, partitionTypeID);
		IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor(partitionTypeID);

		//fire content assist session about to start
		Method privateFireSessionBeginEventMethod = ContentAssistant.class.
		        getDeclaredMethod("fireSessionBeginEvent", new Class[] {boolean.class});
		privateFireSessionBeginEventMethod.setAccessible(true);
		privateFireSessionBeginEventMethod.invoke(contentAssistant, new Object[] {Boolean.TRUE});

		//get content assist suggestions
		ICompletionProposal[][] pages = new ICompletionProposal[pageCount][];
		for(int p = 0; p < pageCount; ++p) {
			pages[p] = processor.computeCompletionProposals(viewer, offset);
		}
		
		//fire content assist session ending
		Method privateFireSessionEndEventMethod = ContentAssistant.class.getDeclaredMethod("fireSessionEndEvent", new Class[0]);
		privateFireSessionEndEventMethod.setAccessible(true);
		privateFireSessionEndEventMethod.invoke(contentAssistant, new Object[0]);
		
		return pages;
	}
	
	/**
	 * <p>Compare the expected number of proposals per page to the actual number of proposals
	 * per page</p>
	 * 
	 * @param pages
	 * @param expectedProposalCounts
	 */
	private static void verifyProposalCounts(ICompletionProposal[][] pages, int[] expectedProposalCounts) {
		StringBuffer error = new StringBuffer();
		for(int page = 0; page < expectedProposalCounts.length; ++page) {
			if(expectedProposalCounts[page] > pages[page].length) {
				error.append("\nProposal page " + page + " is missing proposals: was " +
						pages[page].length + " expected " + expectedProposalCounts[page]);
			}
		}
		
		//if errors report them
		if(error.length() > 0) {
			fail(error.toString());
		}
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
	 * <p>When opening the editor it will also standardized the line
	 * endings to <code>\n</code></p>
	 * 
	 * @param file open and return an editor for this
	 * @return <code>StructuredTextEditor</code> opened from the given <code>file</code>
	 */
	private static StructuredTextEditor getEditor(IFile file)  {
		StructuredTextEditor editor = fFileToEditorMap.get(file);
		
		if(editor == null) {
			try {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IEditorPart editorPart = IDE.openEditor(page, file, "org.eclipse.wst.html.core.htmlsource.source", true);
				if(editorPart instanceof MultiPageEditorPart) {
					MultiPageEditorPart mpEditorPart = (MultiPageEditorPart)editorPart;
					editor = mpEditorPart.getAdapter(StructuredTextEditor.class);
				} else if(editorPart instanceof StructuredTextEditor) {
					editor = ((StructuredTextEditor)editorPart);
				} else {
					fail("Unable to open structured text editor: " + editorPart.getClass().getName());
				}
				
				if(editor != null) {
					standardizeLineEndings(editor);
					fFileToEditorMap.put(file, editor);
				} else {
					fail("Could not open editor for " + file);
				}
			} catch (Exception e) {
				fail("Could not open editor for " + file + " exception: " + e.getMessage());
			}
		}
		
		return editor;
	}
	
	/**
	 * <p>Line endings can be an issue when running tests on different OSs.
	 * This function standardizes the line endings to use <code>\n</code></p>
	 * 
	 * <p>It will get the text from the given editor, change the line endings,
	 * and then save the editor</p>
	 * 
	 * @param editor standardize the line endings of the text presented in this
	 * editor.
	 */
	private static void standardizeLineEndings(StructuredTextEditor editor) {
		IDocument doc = editor.getTextViewer().getDocument();
		String contents = doc.get();
		contents = StringUtils.replace(contents, "\r\n", "\n");
		contents = StringUtils.replace(contents, "\r", "\n");
		doc.set(contents);
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after (respectively) all tests in the inclosing class have run.</p>
	 */
	private static class TestHTMLContentAssistComputersSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public TestHTMLContentAssistComputersSetup(Test test) {
			super(test);
		}

		/**
		 * <p>This is run once before all of the tests</p>
		 * 
		 * @see junit.extensions.TestSetup#setUp()
		 */
		public void setUp() throws Exception {
			//setup properties
			String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
			if (noninteractive != null) {
				previousWTPAutoTestNonInteractivePropValue = noninteractive;
			} else {
				previousWTPAutoTestNonInteractivePropValue = "false";
			}
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
			
			//setup project
			fProject = ProjectUtil.createProject(PROJECT_NAME, null, new String[] {JAVA_SCRIPT_NATURE_ID});
			ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			//close out the editors
			Iterator<StructuredTextEditor> iter = fFileToEditorMap.values().iterator();
			while(iter.hasNext()) {
				StructuredTextEditor editor = iter.next();
				editor.doSave(null);
				editor.close(false);
			}
			
			//remove project
//			fProject.delete(true, new NullProgressMonitor());
			
			//restore properties
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
		}
	}
}
