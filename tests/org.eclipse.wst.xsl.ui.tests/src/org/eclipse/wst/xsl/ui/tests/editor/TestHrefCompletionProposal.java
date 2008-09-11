/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.editor;

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestHrefCompletionProposal extends AbstractXSLUITest
{

	protected String fileName = null;
	protected IFile file = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;

	protected void loadFileForTesting(String filePath) throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException
	{
		file = fTestProject.getFile(new Path(filePath));
		if (file == null || !file.exists())
			Assert.fail("Unable to locate " + fileName + " stylesheet.");
		loadXSLFile();
		initializeSourceViewer();
	}

	protected void initializeSourceViewer()
	{
		// some test environments might not have a "real" display
		if (Display.getCurrent() != null)
		{

			Shell shell = null;
			Composite parent = null;

			if (PlatformUI.isWorkbenchRunning())
			{
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			else
			{
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);

			// dummy viewer
			sourceViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		}
		else
		{
			Assert.fail("Unable to run the test as a display must be available.");
		}

		configureSourceViewer();
	}

	protected void configureSourceViewer()
	{
		sourceViewer.configure(new StructuredTextViewerConfigurationXSL());
		sourceViewer.setDocument(document);
	}

	protected void loadXSLFile() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException
	{
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.getNewModelForEdit(file, true);
		document = model.getStructuredDocument();
	}

	/**
	 * Get the content completion proposals at <code>lineNumber</code>, <code>columnNumber</code>.
	 * 
	 * @param lineNumber
	 * @param columnNumber
	 * @return
	 * @throws Exception
	 */
	private ICompletionProposal[] getProposals(int offset) throws Exception
	{
		return new XSLContentAssistProcessor().computeCompletionProposals(sourceViewer, offset);
	}

	public void testHrefProposalsAtStart() throws Exception
	{
		fileName = "hrefs/mainFile.xsl";
		loadFileForTesting(fileName);
		IStructuredDocument document = (IStructuredDocument) sourceViewer.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 25;
		int line = 16;

		int offset = document.getLineOffset(line) + column;

		ICompletionProposal[] proposals = getProposals(offset);
		assertTrue("Incorrect number of proposals", proposals.length > 3);
		doCommonTests(proposals);

		sourceViewer = null;
	}

	private void doCommonTests(ICompletionProposal[] proposals)
	{
		int currDepth = 1;
		for (ICompletionProposal completionProposal : proposals)
		{
			System.out.println(completionProposal.getDisplayString());
			IPath p = new Path(completionProposal.getDisplayString());
			assertNotSame("Stylesheet must not include itself", new Path("mainFile.xsl"), p);
			assertTrue("Proposals wrongly ordered - number of segments should increase down the list", p.segmentCount() >= currDepth);
			currDepth = p.segmentCount();
		}
	}

}