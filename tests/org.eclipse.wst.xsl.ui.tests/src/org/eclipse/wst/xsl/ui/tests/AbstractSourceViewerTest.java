/*******************************************************************************
 *Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests;

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor;
import org.junit.After;

/**
 * This class is an abstract class for Content Completion Tests. It provides all
 * of the common methods that are used by the completion tests so that they
 * aren't duplicated across the various classes. Overrides can be done where
 * appropriate.
 * 
 * @author David Carver
 * 
 */
public class AbstractSourceViewerTest extends AbstractXSLUITest {

	protected String projectName = TEST_PROJECT_NAME;
	protected String fileName = null;
	protected IFile file = null;
	protected IEditorPart textEditorPart = null;
	protected ITextEditor editor = null;
	protected XMLDocumentLoader xmlDocumentLoader = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;
	protected IStructuredModel model;
	protected Shell shell = null;
	protected Composite parent = null;


	public AbstractSourceViewerTest() {
	}

	protected void initializeSourceViewer() {
		// some test environments might not have a "real" display
		if (Display.getCurrent() != null) {

			if (PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell();
			} else {
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);

			// dummy viewer
			sourceViewer = new StructuredTextViewer(parent, null, null, false,
					SWT.NONE);
		} else {
			Assert
					.fail("Unable to run the test as a display must be available.");
		}

		configureSourceViewer();
	}

	protected void configureSourceViewer() {
		sourceViewer.configure(new StructuredTextViewerConfigurationXSL());

		sourceViewer.setDocument(document);
	}

	protected void loadFileForTesting(String xslFilePath)
			throws ResourceAlreadyExists, ResourceInUse, IOException,
			CoreException {
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(xslFilePath));
		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate " + fileName + " stylesheet.");
		}

		loadXSLFile();

		initializeSourceViewer();
	}

	protected void loadXSLFile() throws ResourceAlreadyExists, ResourceInUse,
			IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		model = modelManager.getModelForEdit(file);
		document = model.getStructuredDocument();

	}

	/**
	 * Get the content completion proposals at <code>lineNumber</code>,
	 * <code>columnNumber</code>.
	 * 
	 * @param lineNumber
	 * @param columnNumber
	 * @return
	 * @throws Exception
	 * @deprecated different operating systems can have different offsets depending on the line feed. Use getProposals(int, int) instead.
	 */
	protected ICompletionProposal[] getProposals(int offset) throws Exception {
		return new XSLContentAssistProcessor().computeCompletionProposals(
				sourceViewer, offset);
	}
	
	protected ICompletionProposal[] getXMLProposals(int offset) throws Exception {
		return new XMLContentAssistProcessor().computeCompletionProposals(
				sourceViewer, offset);
	}
	
	/**
	 * Get the content completion proposals at <code>lineNumber</code>, <code>numberOfCharacters</code>.
	 * Number of characters refers to how many total characters from the starting offset of the line.  This is
	 * not the same as the column number as tabs can cause the column number to be different from the number of
	 * characters.
	 * 
	 * @param lineNumber
	 * @param numberOfCharacters
	 * @return
	 * @throws BadLocationException 
	 */
	protected ICompletionProposal[] getProposals(int lineNumber, int numberOfCharacters) throws BadLocationException {
		int offset = calculateOffset(lineNumber, numberOfCharacters);
		return new XSLContentAssistProcessor().computeCompletionProposals(sourceViewer, offset);
	}
	
	protected ICompletionProposal[] getXMLProposals(int lineNumber, int numberOfCharacters) throws BadLocationException {
		int offset = calculateOffset(lineNumber, numberOfCharacters);
		return new XMLContentAssistProcessor().computeCompletionProposals(sourceViewer, offset);
	}
	

	protected int calculateOffset(int lineNumber, int columnNumber)
			throws BadLocationException {
		IDocument document = sourceViewer.getDocument();
		int lineOffset = document.getLineOffset(lineNumber);
		int offset = lineOffset + columnNumber;
		return offset;
	}	

	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		parent.dispose();
		if (model != null) {
			model.releaseFromEdit();
		}
	}
	
	

}
