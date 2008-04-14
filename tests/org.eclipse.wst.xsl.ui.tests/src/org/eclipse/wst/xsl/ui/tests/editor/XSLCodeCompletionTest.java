/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.editor;

import java.io.File;
import junit.framework.Assert;

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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.tests.UnzippedProjectTester;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class XSLCodeCompletionTest extends UnzippedProjectTester {

	protected String projectName = null;
	protected String fileName = null;
	protected IFile file = null;
	protected IEditorPart textEditorPart = null;
	protected ITextEditor editor = null;

	protected XMLDocumentLoader xmlDocumentLoader = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;
	
	public XSLCodeCompletionTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Setup the necessary projects, files, and source viewer for the
	 * tests.
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		projectName = "net.sourceforge.docbook.stylesheets";
		fileName = "docbook.xsl";
		
        // Setup the Project and File to be used during the test.
		String xslFilePath = projectName + File.separator + "docbook-xsl-1.73.2" + File.separator + "html" + File.separator + fileName;
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			
		}
		
		// Get the IFile instance 
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(xslFilePath));
		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate Docbook HTML stylesheet.");
		}
		
		// Load the File into a Structured Document using the Model Manager
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		document = model.getStructuredDocument();
		
		
		// some test environments might not have a "real" display
		if(Display.getCurrent() != null) {
			
			Shell shell = null;
			Composite parent = null;
			
			if(PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			else {	
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);
			
			// dummy viewer
			sourceViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		}
		else {
			Assert.fail("Unable to run the test as a display must be available.");
		}
		
		// Setup a StructuredTextViewer for Content Assistance and proposals.
        sourceViewer.setDocument(document);
		sourceViewer.configure(new StructuredTextViewerConfigurationXSL());
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
    
    /**
     * Test that there are proposals available at the current position.
     */
    public void testSelectAttributeProposalsAvailable() throws Exception {
    	
       	int lineNumber= 96;   // Starting Line Number
    	int columnNumber= 29; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertTrue(proposals.length >= 1);
    }
    
    /**
     * Test that there are proposals available at the current position.
     */
    public void testTestAttributeProposalsAvailable() throws Exception {
    	
       	int lineNumber= 94;   // Starting Line Number
    	int columnNumber= 19; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertTrue(proposals.length >= 1);
    }
    
    /**
     * Test that there are proposals available at the current position.
     */
    public void testMatchAttributeProposalsAvailable() throws Exception {
    	
       	int lineNumber= 112;   // Starting Line Number
    	int columnNumber= 22; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertTrue(proposals.length >= 1);
    }
    
    /**
     * Test that there are proposals available at the current position.
     * This test is for non-xsl elements with a { to allow for content assistance
     */
    public void testNonXSLAttributeProposalsAvailable() throws Exception {
    	
       	int lineNumber= 142;   // Starting Line Number
    	int columnNumber= 18; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertTrue(proposals.length >= 2);
    }
    
    /**
     * Test that there are proposals available at the current position.
     * This test is for non-xsl elements with a { to allow for content assistance
     */
    public void testXSLElementProposalsAvailable() throws Exception {
    	
       	int lineNumber= 139;   // Starting Line Number
    	int columnNumber= 1; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertTrue(proposals.length >= 2);
    	
    	ICompletionProposal proposal = proposals[0];
    	assertTrue("Can't find XSL element proposals.", proposal.getDisplayString().equals("xsl:apply-imports"));
    }
    
    /**
     * Test that there are proposals available at the current position.
     * This test is for non-xsl elements with a { to allow for content assistance
     */
    public void testNoAttributeProposalsAvailable() throws Exception {
    	
       	int lineNumber= 78;   // Starting Line Number
    	int columnNumber= 18; // Starting Column Number
    	int lineOffset= document.getLineOffset(lineNumber);
    	int cursorPosition = lineOffset + columnNumber;
 
    	XSLContentAssistProcessor processor = new XSLContentAssistProcessor();
    	
    	ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, cursorPosition);
    	assertNull("Found Proposals where there shouldn't be any.", proposals);
    }
    
    
}