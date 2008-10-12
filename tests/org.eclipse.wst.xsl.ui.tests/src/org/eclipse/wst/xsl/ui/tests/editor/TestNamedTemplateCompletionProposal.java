/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 243577 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.editor;

import java.io.File;
import java.io.IOException;

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
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;

public class TestNamedTemplateCompletionProposal extends AbstractXSLUITest {
	
	protected String projectName = null;
	protected String fileName = null;
	protected IFile file = null;
	protected IEditorPart textEditorPart = null;
	protected ITextEditor editor = null;

	protected XMLDocumentLoader xmlDocumentLoader = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;
	
	/**
	 * Setup the necessary projects, files, and source viewer for the
	 * tests.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setupProject();
		
	}

	protected void loadFileForTesting(String xslFilePath)
			throws ResourceAlreadyExists, ResourceInUse, IOException,
			CoreException {
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(xslFilePath));
		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate " + fileName + " stylesheet.");
		}
		
		loadXSLFile();
		
		initializeSourceViewer();
	}

	protected void initializeSourceViewer() {
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
		
		configureSourceViewer();
	}

	protected void configureSourceViewer() {
		sourceViewer.configure(new StructuredTextViewerConfigurationXSL());
		
        sourceViewer.setDocument(document);
	}

	protected void loadXSLFile() throws ResourceAlreadyExists, ResourceInUse,
			IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		document = model.getStructuredDocument();
	}

	protected void setupProject() {
		projectName = "xsltestfiles";
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Get the content completion proposals at <code>lineNumber</code>, <code>columnNumber</code>.
	 * @param lineNumber
	 * @param columnNumber
	 * @return
	 * @throws Exception
	 */
	private ICompletionProposal[] getProposals(int offset) throws Exception {
    	return new XSLContentAssistProcessor().computeCompletionProposals(sourceViewer, offset); 
	}
	
	private void setupTestFile(String fileName) throws ResourceAlreadyExists,
			ResourceInUse, IOException, CoreException {
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
	}

    public void testXSLPropsoalAvailable() throws Exception {
		setupTestFile("TestNamedTemplatesAssist.xsl");
		int offset = 1810;
		
    	ICompletionProposal[] proposals = getProposals(offset);
    	assertEquals("Missing Proposals", 1, proposals.length);
    	sourceViewer = null;
    }
    
}
