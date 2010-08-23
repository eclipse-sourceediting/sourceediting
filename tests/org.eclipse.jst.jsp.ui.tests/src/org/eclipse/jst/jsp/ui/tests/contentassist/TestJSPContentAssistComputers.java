/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.jsp
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jst.jsp.ui.StructuredTextViewerConfigurationJSP;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

public class TestJSPContentAssistComputers extends TestCase {
	/** The name of the project that all of these tests will use */
	private static final String PROJECT_NAME = "TestJSPContentAssistComputers";
	
	private static final String CONTENT_DIR = "WebContent";
	
	/** The project that all of the tests use */
	private static IProject fProject;
	
	/**
	 * Used to keep track of the already open editors so that the tests don't go through
	 * the trouble of opening the same editors over and over again
	 */
	private static Map fFileToEditorMap = new HashMap();
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public TestJSPContentAssistComputers() {
		super("Test JSP Content Assist Computers");
	}
	
	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public TestJSPContentAssistComputers(String name) {
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
		TestSuite ts = new TestSuite(TestJSPContentAssistComputers.class, "Test JSP Content Assist Computers");
		return new TestJSPContentAssistComputersSetup(ts);
	}
	
	public void testEmptyDocument() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {14, 7, 6, 1, 0, 14};
		runProposalTest("test0.jsp", 0, 0, expectedProposalCounts);
	}
	
	public void testAfterXMLDeclarationBeforeHTMLTagProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {18, 10, 8, 0, 0, 18};
		runProposalTest("test1.jsp", 1, 0, expectedProposalCounts);
	}
	
	public void testAfterXMLDeclarationBeforeEmptyDocProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {19, 10, 8, 1, 0, 19};
		runProposalTest("test2.jsp", 1, 0, expectedProposalCounts);
	}
	
	public void testAfterJSPTaglibsBeforeDocType() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {18, 10, 8, 0, 0, 18};
		runProposalTest("test1.jsp", 6, 0, expectedProposalCounts);
	}
	
	public void testBodyTagChildElementProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {95, 10, 8, 64, 13, 95};
		runProposalTest("test1.jsp", 14, 0, expectedProposalCounts);
	}
	
	public void testPTagChildElementProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {72, 10, 8, 41, 13, 72};
		runProposalTest("test1.jsp", 16, 0, expectedProposalCounts);
	}
	
	public void testDIVTagChildElementProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {95, 10, 8, 64, 13, 95};
		runProposalTest("test1.jsp", 20, 0, expectedProposalCounts);
	}
	
	public void testCommentTagChildElementProposals() throws Exception {
		//default, HTML templates, HTML tags, JSP proposals, default
		int[] expectedProposalCounts = new int[] {1, 0, 0, 1, 1};
		runProposalTest("test1.jsp", 25, 0, expectedProposalCounts);
	}
	
	public void testJSPRegionProposals() throws Exception {
		//default, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {60, 0, 60, 60};
		runProposalTest("test1.jsp", 28, 0, expectedProposalCounts);
	}
	
	public void testDIVTagAttributeNameProposals() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {19, 1, 0, 18, 0, 19};
		runProposalTest("test1.jsp", 19, 5, expectedProposalCounts);
	}
	
	public void testELProposals() throws Exception {
		//default, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {12, 0, 12, 12};
		runProposalTest("test1.jsp", 33, 15, expectedProposalCounts);
	}
	
	public void testELInAttributeProposals() throws Exception {
		//default, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {12, 0, 12, 12};
		runProposalTest("test1.jsp", 31, 24, expectedProposalCounts);
	}
	
	public void testJSPTagAttributeNameProposals() throws Exception {
		//default, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {6, 4, 2, 0, 0, 6};
		runProposalTest("test1.jsp", 41, 15, expectedProposalCounts);
	}
	
	public void testHTMLTagAttributeValueProposals() throws Exception {
		//default, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {15, 2, 0, 0, 13, 15};
		runProposalTest("test3.jsp", 8, 10, expectedProposalCounts);
	}
	
	public void testJSPTagProposalsAtDocumentRootLevel() throws Exception {
		//default, jsp templates, html templates, html tags, jsp, default
		int[] expectedProposalCounts = new int[] {36, 10, 8, 1, 17, 36};
		runProposalTest("test6.jsp", 2, 0, expectedProposalCounts);
	}
	
	public void testFinishClosingHTMLTagNamePropsoals() throws Exception {
		//default, jsp template, html template, html, jsp, default
		int[] expectedProposalCounts = new int[] {2, 1, 0, 1, 0, 2};
		runProposalTest("test4.jsp", 11, 9, expectedProposalCounts);
	}
	
	public void testFinishClosingHTMLTagPropsoals() throws Exception {
		//default, jsp template, html template, html, jsp, default
		int[] expectedProposalCounts = new int[] {3, 1, 0, 2, 0, 3};
		runProposalTest("test4.jsp", 12, 0, expectedProposalCounts);
	}
	
	public void testFinishClosingJSPTagNamePropsoals() throws Exception {
		//default, xml template, html tag, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {1, 0, 0, 1, 0, 1};
		runProposalTest("test5.jsp", 11, 9, expectedProposalCounts);
	}
	
	public void testFinishClosingJSPTagPropsoals() throws Exception {
		//default, xml template, html tag, jsp, jsp java, default
		int[] expectedProposalCounts = new int[] {5, 4, 2, 0, 0, 5};
		runProposalTest("test5.jsp", 12, 0, expectedProposalCounts);
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
	private static void runProposalTest(String fileName,
			int lineNum, int lineRelativeCharOffset,
			int[] expectedProposalCounts) throws Exception{
		
		IFile file = getFile(fileName);
		StructuredTextEditor editor  = getEditor(file);
		StructuredTextViewer viewer = editor.getTextViewer();
		int offset = viewer.getDocument().getLineOffset(lineNum) + lineRelativeCharOffset;

		ICompletionProposal[][] pages = getProposals(viewer, offset, expectedProposalCounts.length);
		
		verifyProposalCounts(pages, expectedProposalCounts);
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
		StructuredTextViewerConfigurationJSP configuration = new StructuredTextViewerConfigurationJSP();
		ContentAssistant contentAssistant = (ContentAssistant)configuration.getContentAssistant(viewer);
		viewer.configure(configuration);
		viewer.setSelectedRange(offset, 0);
		
		//get the processor
		String partitionTypeID = viewer.getDocument().getPartition(offset).getType();
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
		Method privateFireSessionEndEventMethod = ContentAssistant.class.
        getDeclaredMethod("fireSessionEndEvent", null);
		privateFireSessionEndEventMethod.setAccessible(true);
		privateFireSessionEndEventMethod.invoke(contentAssistant, null);
		
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
				error.append("\nProposal page " + page + " did not have the expected number of proposals: was " +
						pages[page].length + " expected " + expectedProposalCounts[page]);
			}
		}
		
		//if errors report them
		if(error.length() > 0) {
			Assert.fail(error.toString());
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
		IFile file = fProject.getFile(CONTENT_DIR + IPath.SEPARATOR + name);
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
	private static class TestJSPContentAssistComputersSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public TestJSPContentAssistComputersSetup(Test test) {
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
			ProjectUnzipUtility fProjUtil = new ProjectUnzipUtility();
			Location platformLocation = Platform.getInstanceLocation();
			// platform location may be null -- depends on "mode" of platform
			if (platformLocation != null) {
				File zipFile = FileUtil.makeFileFor(
					ProjectUnzipUtility.PROJECT_ZIPS_FOLDER,
					PROJECT_NAME + ProjectUnzipUtility.ZIP_EXTENSION,
					ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
				fProjUtil.unzipAndImport(zipFile, platformLocation.getURL().getPath());
				fProjUtil.initJavaProject(PROJECT_NAME);
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				fProject = root.getProject(PROJECT_NAME);
			}
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			//close out the editors
			Iterator iter = fFileToEditorMap.values().iterator();
			while(iter.hasNext()) {
				StructuredTextEditor editor = (StructuredTextEditor)iter.next();
				editor.doSave(null);
				editor.close(false);
			}
			
			//remove project
			fProject.delete(true, new NullProgressMonitor());
			
			//restore properties
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
		}
	}
}
