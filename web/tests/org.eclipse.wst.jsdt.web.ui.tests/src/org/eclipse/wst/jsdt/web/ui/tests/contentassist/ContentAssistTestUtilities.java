/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.contentassist;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.wst.jsdt.internal.ui.text.html.HTML2TextReader;
import org.eclipse.wst.jsdt.ui.text.IJavaScriptPartitions;
import org.eclipse.wst.jsdt.ui.text.JavaScriptSourceViewerConfiguration;
import org.eclipse.wst.jsdt.web.ui.tests.internal.TestProjectSetup;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

/**
 * <p>
 * Helpful utilities for running content assist tests.
 * </p>
 * 
 * @see org.ecliplse.wst.jsdt.ui.tests.contentassist.ContentAssistUtilities
 * @see org.eclipse.wst.jsdt.web.ui.tests.contentassist.ContentAssistUtilities
 */
public class ContentAssistTestUtilities {
	/**
	 * <p>
	 * Run a proposal test by opening the given file and invoking content assist for each expected
	 * proposal count at the given line number and line character offset and then compare the number
	 * of proposals for each invocation (pages) to the expected number of proposals.
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalCounts
	 * @throws Exception
	 */
	public static void runProposalTest(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			String[][] expectedProposals) throws Exception {

		runProposalTest(testProject, filePath, lineNum, lineRelativeCharOffset, expectedProposals, false, true);
	}

	/**
	 * <p>
	 * Run a proposal test by opening the given file and invoking content assist for each expected
	 * proposal count at the given line number and line character offset and then compare the number
	 * of proposals for each invocation (pages) to the expected number of proposals.
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalCounts
	 * @param negativeTest
	 *            <code>true</code> if expectedProposals should not be found, <code>false</code> if
	 *            they should be found
	 * @param exactMatch
	 *            <code>true</code> if expectedProposals should be compared to actual proposals with
	 *            {@link String#equals(Object)}, <code>false</code> if {@link String#indexOf(int)}
	 *            should be used
	 * @throws Exception
	 */
	public static void runProposalTest(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			String[][] expectedProposals, boolean negativeTest, boolean exactMatch) throws Exception {

		ICompletionProposal[][] pages = getProposals(testProject, filePath, lineNum, lineRelativeCharOffset,
				expectedProposals.length);
		verifyExpectedProposal(pages, expectedProposals, negativeTest, exactMatch, false);
	}

	/**
	 * <p>
	 * Run a proposal test by opening the given file and invoking content assist for each expected
	 * proposal count at the given line number and line character offset and then compare the number
	 * of proposals for each invocation (pages) to the expected number of proposals.
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalCounts
	 * @param negativeTest
	 *            <code>true</code> if expectedProposals should not be found, <code>false</code> if
	 *            they should be found
	 * @param exactMatch
	 *            <code>true</code> if expectedProposals should be compared to actual proposals with
	 *            {@link String#equals(Object)}, <code>false</code> if {@link String#indexOf(int)}
	 *            should be used
	 * @param inOrder
	 *            <code>true</code> if <code>expectedProposals</code> should be found in the order
	 *            they are given, <code>false</code> if order does not matter
	 * 
	 * @throws Exception
	 */
	public static void runProposalTest(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			String[][] expectedProposals, boolean negativeTest, boolean exactMatch, boolean inOrder) throws Exception {

		ICompletionProposal[][] pages = getProposals(testProject, filePath, lineNum, lineRelativeCharOffset,
				expectedProposals.length);
		verifyExpectedProposal(pages, expectedProposals, negativeTest, exactMatch, inOrder);
	}
	
	/**
	 * <p>
	 * Run a proposal info test by opening the given file and invoking content assist for each
	 * expected proposal count at the given line number and line character offset and then compare
	 * the proposal Info for each invocation (pages) with its expected Proposal Info .
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalInfo
	 * @throws Exception
	 */
	public static void runProposalInfoTest(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			String[][] expectedProposals, String[][] expectedProposalInfo) throws Exception {

		ICompletionProposal[][] pages = getProposals(testProject, filePath, lineNum, lineRelativeCharOffset,
				expectedProposals.length);
		verifyExpectedProposalInfo(pages, expectedProposals, expectedProposalInfo);
	}

	/**
	 * <p>
	 * Runs a test for finding whether duplicate proposals are generated when content assist is
	 * invoked.
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeOffset
	 */
	public static void verifyNoDuplicates(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset) throws Exception {

		int numOfPages = 1;
		ICompletionProposal[][] pages = getProposals(testProject, filePath, lineNum, lineRelativeCharOffset, numOfPages);
		checkForDuplicates(pages, numOfPages);
	}
	
	/**
	 * <p>
	 * Run a proposal test by opening the given file and invoking content assist for each expected
	 * proposal count at the given line number and line character offset and then compare the number
	 * of proposals for each invocation (pages) to the expected number of proposals.
	 * </p>
	 * 
	 * @param testProject
	 * @param filePath
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param expectedProposalCounts
	 * @param negativeTest
	 *            <code>true</code> if expectedProposals should not be found, <code>false</code> if
	 *            they should be found
	 * @param exactMatch
	 *            <code>true</code> if expectedProposals should be compared to actual proposals with
	 *            {@link String#equals(Object)}, <code>false</code> if {@link String#indexOf(int)}
	 *            should be used
	 * @throws Exception
	 */
	public static void runHTMLProposalTest(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			String[][] expectedProposals, boolean negativeTest, boolean exactMatch) throws Exception {

		ICompletionProposal[][] pages = getHTMLProposals(testProject, filePath, lineNum, lineRelativeCharOffset,
				expectedProposals.length);
		verifyExpectedProposal(pages, expectedProposals, negativeTest, exactMatch, false);
	}

	/**
	 * <p>
	 * Returns the content assist proposals when invoked at the offset provided.
	 * </p>
	 * 
	 * @param fileNum
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param numOfPages
	 */
	private static ICompletionProposal[][] getProposals(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			int numOfPages) throws Exception {

		IFile file = testProject.getFile(filePath);
		AbstractTextEditor editor = testProject.getEditor(file);
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		int offset = doc.getLineOffset(lineNum) + lineRelativeCharOffset;

		ICompletionProposal[][] pages = getProposals(editor, offset, numOfPages);
		return pages;
	}

	/**
	 * <p>
	 * Invoke content assist on the given editor at the given offset, for the given number of pages
	 * and return the results of each page
	 * </p>
	 */
	private static ICompletionProposal[][] getProposals(AbstractTextEditor editor, int offset, int pageCount)
			throws Exception {

		ICompletionProposal[][] pages = null;

		/* if JS editor
		 * else if HTML editor */
		if(editor instanceof JavaEditor) {
			// setup the viewer
			ISourceViewer viewer = ((JavaEditor) editor).getViewer();
			JavaScriptSourceViewerConfiguration configuration = new JavaScriptSourceViewerConfiguration(
					JavaScriptPlugin.getDefault().getJavaTextTools().getColorManager(),
					JavaScriptPlugin.getDefault().getCombinedPreferenceStore(), editor,
					IJavaScriptPartitions.JAVA_PARTITIONING);

			ContentAssistant contentAssistant = (ContentAssistant) configuration.getContentAssistant(viewer);

			// get the processor
			String partitionTypeID = viewer.getDocument().getPartition(offset).getType();
			IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor(partitionTypeID);

			// fire content assist session about to start
			Method privateFireSessionBeginEventMethod = ContentAssistant.class.getDeclaredMethod(
					"fireSessionBeginEvent", new Class[] { boolean.class });
			privateFireSessionBeginEventMethod.setAccessible(true);
			privateFireSessionBeginEventMethod.invoke(contentAssistant, new Object[] { Boolean.TRUE });

			// get content assist suggestions
			pages = new ICompletionProposal[pageCount][];
			for(int p = 0; p < pageCount; ++p) {
				pages[p] = processor.computeCompletionProposals(viewer, offset);
			}

			// fire content assist session ending
			Method privateFireSessionEndEventMethod = ContentAssistant.class.getDeclaredMethod("fireSessionEndEvent",
					null);
			privateFireSessionEndEventMethod.setAccessible(true);
			privateFireSessionEndEventMethod.invoke(contentAssistant, null);

		} else if(editor instanceof StructuredTextEditor) {
			//setup the viewer
			StructuredTextViewer viewer = ((StructuredTextEditor) editor).getTextViewer();
			StructuredTextViewerConfigurationHTML configuration = new StructuredTextViewerConfigurationHTML();
			ContentAssistant contentAssistant = (ContentAssistant) configuration.getContentAssistant(viewer);
			viewer.configure(configuration);
			viewer.setSelectedRange(offset, 0);

			//get the processor
			String partitionTypeID = viewer.getDocument().getPartition(offset).getType();
			IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor(partitionTypeID);

			//fire content assist session about to start
			Method privateFireSessionBeginEventMethod = ContentAssistant.class.getDeclaredMethod(
					"fireSessionBeginEvent", new Class[] { boolean.class });
			privateFireSessionBeginEventMethod.setAccessible(true);
			privateFireSessionBeginEventMethod.invoke(contentAssistant, new Object[] { Boolean.TRUE });

			//get content assist suggestions
			pages = new ICompletionProposal[pageCount][];
			for(int p = 0; p < pageCount; ++p) {
				pages[p] = processor.computeCompletionProposals(viewer, offset);
			}

			//fire content assist session ending
			Method privateFireSessionEndEventMethod = ContentAssistant.class.getDeclaredMethod("fireSessionEndEvent",
					null);
			privateFireSessionEndEventMethod.setAccessible(true);
			privateFireSessionEndEventMethod.invoke(contentAssistant, null);
		}

		return pages;
	}

	/**
	 * @param pages
	 * @param expectedProposals
	 * @param negativeTest
	 *            <code>true</code> if <code>expectedProposals</code> should not be found,
	 *            <code>false</code> if
	 *            they should be found
	 * @param exactMatch
	 *            <code>true</code> if expectedProposals should be compared to actual proposals with
	 *            {@link String#equals(Object)}, <code>false</code> if {@link String#indexOf(int)}
	 *            should be used
	 * @param inOrder
	 *            <code>true</code> if <code>expectedProposals</code> should be found in the order
	 *            they are given, <code>false</code> if order does not matter
	 */
	private static void verifyExpectedProposal(ICompletionProposal[][] pages, String[][] expectedProposals,
			boolean negativeTest, boolean exactMatch, boolean inOrder) {

		StringBuffer error = new StringBuffer();
		int lastFoundIndex = -1;
		String lastFoundProposal = "";
		for(int page = 0; page < expectedProposals.length; ++page) {
			for(int expected = 0; expected < expectedProposals[page].length; ++expected) {
				String expectedProposal = expectedProposals[page][expected];
				boolean found = false;
				int suggestion = 0;
				for( ; suggestion < pages[page].length && !found; ++suggestion) {
					String displayString = pages[page][suggestion].getDisplayString();
					found = exactMatch ?
							displayString.equals(expectedProposal) : displayString.indexOf(expectedProposal) != -1;
				}
				
				/* error if checking for in order and this expected proposal was
				 * found at an index lower then the last found proposal */
				if(inOrder && found && suggestion < lastFoundIndex) {
					error.append("\nProposal was found out of order: " + expectedProposal + " found before " + lastFoundProposal);
				}

				if(found && negativeTest) {
					error.append("\nUnexpected proposal was found on page " + page + ": '" + expectedProposal + "'");
				} else if(!found && !negativeTest) {
					error.append("\n Expected proposal was not found on page " + page + ": '" + expectedProposal + "'");
				}
				
				if(found) {
					lastFoundProposal = expectedProposal;
					lastFoundIndex = suggestion;
				}
			}
		}

		// if errors report them
		if(error.length() > 0) {
			StringBuffer expected = new StringBuffer();
			for(int i = 0; i < expectedProposals.length; i++) {
				for(int j = 0; j < expectedProposals[i].length; j++) {
					expected.append(expectedProposals[i][j]);
					expected.append('\n');
				}
			}
			StringBuffer actual = new StringBuffer();
			for(int i = 0; i < pages.length; i++) {
				for(int j = 0; j < pages[i].length; j++) {
					actual.append(pages[i][j].getDisplayString());
					actual.append('\n');
				}
			}
			Assert.assertEquals(error.toString(), expected.toString(), actual.toString());
		}
	}

	/**
	 * 
	 * <p>
	 * Compares the expected proposal Info with the one generated from the JavaDoc
	 * </p>
	 * 
	 * @param pages
	 * @param expectedProposalInfo
	 */

	private static void verifyExpectedProposalInfo(ICompletionProposal[][] pages, String[][] expectedProposals,
			String[][] expectedProposalsInfo) {
		StringBuffer error = new StringBuffer();
		String proposalInfo = new String();
		for(int page = 0; page < expectedProposals.length; ++page) {
			for(int expected = 0; expected < expectedProposals[page].length; ++expected) {
				String expectedProposal = expectedProposals[page][expected];
				String expectedProposalInfo = expectedProposalsInfo[page][expected];
				boolean found = false;
				for(int suggestion = 0; suggestion < pages[page].length && !found; ++suggestion) {
					found = pages[page][suggestion].getDisplayString().equals(expectedProposal);
					if(found) {
						proposalInfo = pages[page][suggestion].getAdditionalProposalInfo();

						if(proposalInfo == null || proposalInfo.indexOf(expectedProposalInfo) < 0) {
							error.append("\n" + "Required proposal info for " + expectedProposal + " does not exist.");
						}
						break;
					}

				}
			}

		}

		// if errors report them
		if(error.length() > 0) {
			StringBuffer expected = new StringBuffer();
			for(int i = 0; i < expectedProposalsInfo.length; i++) {
				for(int j = 0; j < expectedProposalsInfo[i].length; j++) {
					expected.append(expectedProposalsInfo[i][j]);
					expected.append('\n');
				}
			}
			StringBuffer actual = new StringBuffer();
			for(int i = 0; i < pages.length; i++) {
				for(int j = 0; j < pages[i].length; j++) {
					try {
						actual.append(new HTML2TextReader(new StringReader(pages[i][j].getAdditionalProposalInfo()),
								null).getString().trim());
					} catch(IOException e) {
						e.printStackTrace();
					}
					actual.append('\n');
				}
			}
			Assert.assertEquals(error.toString(), expected.toString(), actual.toString());
		}
	}

	/**
	 * <p>
	 * Checks for Duplicates and reports an error if found.
	 * </p>
	 * 
	 * @param pages
	 * @param numOfPages
	 */
	private static void checkForDuplicates(ICompletionProposal[][] pages, int numOfPages) {
		Set set = new HashSet();
		StringBuffer error = new StringBuffer();

		for(int suggestion = 0; suggestion < pages[0].length; ++suggestion) {
			if(set.contains(pages[0][suggestion].toString())) {
				error.append("\nDuplicate proposal found: '" + pages[0][suggestion] + "'");
			} else {
				set.add(pages[0][suggestion].toString());
			}
		}

		if(error.length() > 0) {
			Assert.fail(error.toString());
		}

		set.clear();
	}
	
	/**
	 * <p>
	 * Returns the content assist proposals when invoked at the offset provided.
	 * </p>
	 * 
	 * @param fileNum
	 * @param lineNum
	 * @param lineRelativeCharOffset
	 * @param numOfPages
	 */
	private static ICompletionProposal[][] getHTMLProposals(TestProjectSetup testProject, String filePath, int lineNum, int lineRelativeCharOffset,
			int numOfPages) throws Exception {

		IFile file = testProject.getFile(filePath);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IEditorPart part = IDE.openEditor(page, file, "org.eclipse.wst.html.core.htmlsource.source");
		StructuredTextEditor editor = (StructuredTextEditor) part.getAdapter(ITextEditor.class);

		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		int offset = doc.getLineOffset(lineNum) + lineRelativeCharOffset;

		ICompletionProposal[][] pages = getHTMLProposals(editor, offset, numOfPages);
		return pages;
	}
	
	/**
	 * <p>
	 * Invoke content assist on the given editor at the given offset, for the given number of pages
	 * and return the results of each page
	 * </p>
	 */
	private static ICompletionProposal[][] getHTMLProposals(StructuredTextEditor editor, int offset, int pageCount) throws Exception {
		// setup the viewer
		ISourceViewer viewer = editor.getTextViewer();
		Method getSourceViewerConfiguration = AbstractTextEditor.class.getDeclaredMethod("getSourceViewerConfiguration", new Class[0]);
		getSourceViewerConfiguration.setAccessible(true);
		StructuredTextViewerConfiguration configuration = (StructuredTextViewerConfiguration) getSourceViewerConfiguration.invoke(editor, null);
		IContentAssistant contentAssistant = configuration.getContentAssistant(viewer);

		// get the processor
		String partitionTypeID = viewer.getDocument().getPartition(offset).getType();
		IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor(partitionTypeID);

		// fire content assist session about to start
		Method privateFireSessionBeginEventMethod = ContentAssistant.class.getDeclaredMethod("fireSessionBeginEvent",
				new Class[] { boolean.class });
		privateFireSessionBeginEventMethod.setAccessible(true);
		privateFireSessionBeginEventMethod.invoke(contentAssistant, new Object[] { Boolean.TRUE });

		// get content assist suggestions
		ICompletionProposal[][] pages = new ICompletionProposal[pageCount][];
		for(int p = 0; p < pageCount; ++p) {
			pages[p] = processor.computeCompletionProposals(viewer, offset);
		}

		// fire content assist session ending
		Method privateFireSessionEndEventMethod = ContentAssistant.class.getDeclaredMethod("fireSessionEndEvent", null);
		privateFireSessionEndEventMethod.setAccessible(true);
		privateFireSessionEndEventMethod.invoke(contentAssistant, null);

		editor.close(false);
		return pages;
	}
}