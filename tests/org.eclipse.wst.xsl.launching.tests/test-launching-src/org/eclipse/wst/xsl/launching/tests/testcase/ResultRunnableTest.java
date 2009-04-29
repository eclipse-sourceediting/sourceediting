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
package org.eclipse.wst.xsl.launching.tests.testcase;

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
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.views.*;

/**
 * This class is an abstract class for Content Completion Tests. It provides all
 * of the common methods that are used by the completion tests so that they
 * aren't duplicated across the various classes. Overrides can be done where
 * appropriate.
 * 
 * @author David Carver
 * 
 */
public class ResultRunnableTest extends TestCase {

	protected StructuredTextViewer sourceViewer = null;
	protected Shell shell = null;
	protected Composite parent = null;


	public ResultRunnableTest() {
	}

	public void testNoProcessingInstruction() throws Exception {
		String results = "This is some sample text.";
		MockResultRunnable runnable = new MockResultRunnable(sourceViewer, results, null);
		IDocument document = runnable.testCreateDocument();
		assertNotNull("Structured Document wasn not created successfully.", document);
	}
}
