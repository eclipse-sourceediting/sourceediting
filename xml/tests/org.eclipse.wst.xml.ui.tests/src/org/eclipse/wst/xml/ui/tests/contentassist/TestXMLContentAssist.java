/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 259447 - intial API and implementation
 *     IBM Corporation - make use of (copy of JSP UI Tests') ProjectUtil to skip
 *         file-system calls
 *******************************************************************************/

package org.eclipse.wst.xml.ui.tests.contentassist;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.tests.ProjectUtil;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestXMLContentAssist extends TestCase {

	protected String projectName = null;
	protected String fileName = null;
	protected IFile file = null;
	protected IEditorPart textEditorPart = null;
	protected ITextEditor editor = null;

	protected XMLDocumentLoader xmlDocumentLoader = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;
	private IStructuredModel model;
	protected XMLContentAssistProcessor xmlContentAssistProcessor = null;

	public TestXMLContentAssist() {
	}

	/**
	 * Setup the necessary projects, files, and source viewer for the tests.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		projectName = "TestXMLContentAssist";
		fileName = "xmlContentAssist-test1.xml";

		// Setup the Project and File to be used during the test.
		String xmlFilePath = setupProject();
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(xmlFilePath));
		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate " + xmlFilePath + ".");
		}

		loadXMLFile();

		initializeSourceViewer();
		xmlContentAssistProcessor = new XMLContentAssistProcessor();
	}

	protected void initializeSourceViewer() {
		// some test environments might not have a "real" display
		if (Display.getCurrent() != null) {

			Shell shell = null;
			Composite parent = null;

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
		sourceViewer.configure(new StructuredTextViewerConfigurationXML());

		sourceViewer.setDocument(document);
	}

	protected void loadXMLFile() throws ResourceAlreadyExists, ResourceInUse,
			IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		model = modelManager.getModelForEdit(file);
		document = model.getStructuredDocument();
	}

	protected String setupProject() {
		IProjectDescription description = ResourcesPlugin.getWorkspace()
				.newProjectDescription(projectName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		} catch (CoreException e) {

		}
		String xmlFilePath = project.getFullPath().addTrailingSeparator().append(fileName).toString();
		// needs both the test file and schemas
		ProjectUtil.copyBundleEntriesIntoWorkspace("/testresources", project.getFullPath().toString());
		return xmlFilePath;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		xmlContentAssistProcessor.release();
	}

	/**
	 * Get the content completion proposals at <code>lineNumber</code>,
	 * <code>columnNumber</code>.
	 * 
	 * @param lineNumber
	 * @param columnNumber
	 * @return
	 * @throws Exception
	 */
	private ICompletionProposal[] getProposals(int offset) throws Exception {
		
		return xmlContentAssistProcessor.computeCompletionProposals(
				sourceViewer, offset);
	}

	public void testAttributeProposal() throws Exception {
		try {
			int offset = sourceViewer.getDocument().getLineOffset(10) + 10;
			IDOMNode node = (IDOMNode) ContentAssistUtils.getNodeAt(
					sourceViewer, offset);
			assertEquals("Wrong node name returned:", "Member", node
					.getNodeName());

			ICompletionProposal[] proposals = getProposals(offset);
			assertTrue("Length less than 1", proposals.length > 1);
			ICompletionProposal proposal = proposals[0];
			assertEquals("Wrong attribute proposal returned at ["+sourceViewer.getDocument().get(offset-9, 9)+"|"+sourceViewer.getDocument().get(offset, 9)+"]", "handicap",
					proposal.getDisplayString());
		} finally {
			model.releaseFromEdit();
		}
	}

	public void testChildElementProposal() throws Exception {
		try {
			int offset = sourceViewer.getDocument().getLineOffset(11) + 8;

			ICompletionProposal[] proposals = getProposals(offset);
			assertEquals("Unexpected number of proposals", 5, proposals.length);
			ICompletionProposal proposal = proposals[0];
			assertEquals("Wrong element proposal returned.", "ExclusiveMember",
					proposal.getDisplayString());
		} finally {
			model.releaseFromEdit();
		}
	}

	public void testShaftElementProposal() throws Exception {
		try {
			int offset = sourceViewer.getDocument().getLineOffset(24) + 6;

			ICompletionProposal[] proposals = getProposals(offset);
			assertEquals("Unexpected number of proposals", 7, proposals.length);
			ICompletionProposal proposal = proposals[0];
			assertEquals("Wrong element proposal returned.", "Shaft", proposal
					.getDisplayString());
		} finally {
			model.releaseFromEdit();
		}
	}

	public void testXMLTemplatePositionalProposal() throws Exception {
		try {
			int offset = sourceViewer.getDocument().getLineOffset(24) + 6;

			ICompletionProposal[] proposals = getProposals(offset);
			assertEquals("Unexpected number of proposals", 7, proposals.length);
			ICompletionProposal proposal = proposals[5];
			assertEquals("Wrong template proposal returned.",
					"comment - xml comment", proposal.getDisplayString());
		} finally {
			model.releaseFromEdit();
		}
	}

	public void testXMLCommentTemplateProposalExists() throws Exception {
		try {
			int offset = sourceViewer.getDocument().getLineOffset(24) + 6;

			ICompletionProposal[] proposals = getProposals(offset);
			assertNotNull("No proposals returned.", proposals);
			boolean foundsw = false;
			for (int cnt = 0; cnt < proposals.length; cnt++) {
				ICompletionProposal proposal = proposals[cnt];
				if (proposal.getDisplayString().equals("comment - xml comment")) {
					foundsw = true;
				}
			}
			if (foundsw == false) {
				fail("XML Template 'xml comment' was not found in the proposal list");
			}
		} finally {
			model.releaseFromEdit();
		}
	}
}