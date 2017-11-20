/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 225418 - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.hyperlinkdetector;

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
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
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
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.editor.XSLHyperlinkDetector;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;
import org.eclipse.wst.xsl.core.internal.text.rules.StructuredTextPartitionerForXSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestXSLHyperlinkDetector extends AbstractXSLUITest {

	protected String projectName = null;
	protected String fileName = null;
	protected IFile file = null;
	protected IEditorPart textEditorPart = null;
	protected ITextEditor editor = null;

	protected XMLDocumentLoader xmlDocumentLoader = null;
	protected IStructuredDocument document = null;
	protected StructuredTextViewer sourceViewer = null;
	protected StructuredTextViewerConfigurationXSL xslConfiguration = new StructuredTextViewerConfigurationXSL();
	protected String Partitioning = IDocumentExtension3.DEFAULT_PARTITIONING;
	protected StructuredTextPartitionerForXSL defaultPartitioner = new StructuredTextPartitionerForXSL();
	protected Shell shell = null;
	protected Composite parent = null;

	public TestXSLHyperlinkDetector() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Setup the necessary projects, files, and source viewer for the tests.
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		setupProject();

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
		sourceViewer.configure(xslConfiguration);

		sourceViewer.setDocument(document);
	}

	protected void setupProject() {
		projectName = "xsltestfiles";
		IProjectDescription description = ResourcesPlugin.getWorkspace()
				.newProjectDescription(projectName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		} catch (CoreException e) {

		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		parent.dispose();
	}

	private LineStyleProvider[] getLineStyleProviders() {
		LineStyleProvider[] lineStyleProviders = xslConfiguration
				.getLineStyleProviders(sourceViewer, IXMLPartitions.XML_DEFAULT);
		return lineStyleProviders;
	}

	private void setUpTest(String file) throws ResourceAlreadyExists,
			ResourceInUse, IOException, CoreException {
		fileName = file;
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		assertNotNull("Missing Document Partitioner", document
				.getDocumentPartitioner());
	}

	private IStructuredModel model = null;

	protected void loadXSLFile() throws ResourceAlreadyExists, ResourceInUse,
			IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		model = modelManager.getNewModelForEdit(file, true);
		document = model.getStructuredDocument();
		IDocumentPartitioner partitioner = defaultPartitioner.newInstance();
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	@Test
	public void testDetectHyperlinksDocument() throws Exception {
		setUpTest("DraftReleaseCRs.xsl");

		try {
			XSLHyperlinkDetector linkDetector = new XSLHyperlinkDetector();
			IRegion region = new Region(sourceViewer.getDocument().getLineOffset(28) + 54, 0);

			boolean canShowMultipleLinks = true;
			IHyperlink[] links = linkDetector.detectHyperlinks(document,
					region, canShowMultipleLinks);
			assertNotNull("No links returned", links);
			assertEquals("Unexpected number of links", 1, links.length);
		} finally {
			model.releaseFromEdit();
		}
	}

	@Test
	public void testDetectHyperlinksViewer() throws Exception {
		setUpTest("DraftReleaseCRs.xsl");

		try {
			XSLHyperlinkDetector linkDetector = new XSLHyperlinkDetector();
			IRegion region = new Region(sourceViewer.getDocument().getLineOffset(28) + 54, 0);

			boolean canShowMultipleLinks = true;
			IHyperlink[] links = linkDetector.detectHyperlinks(sourceViewer,
					region, canShowMultipleLinks);
			assertNotNull("No links returned", links);
			assertEquals("Unexpected number of links", 1, links.length);
		} finally {
			model.releaseFromEdit();
		}
	}

	@Test
	public void testWithParmVariableLink() throws Exception {
		setUpTest("DraftReleaseCRs.xsl");

		try {
			XSLHyperlinkDetector linkDetector = new XSLHyperlinkDetector();
			IRegion region = new Region(sourceViewer.getDocument().getLineOffset(74) + 44, 0);

			boolean canShowMultipleLinks = true;
			IHyperlink[] links = linkDetector.detectHyperlinks(sourceViewer,
					region, canShowMultipleLinks);
			assertNotNull("No links returned", links);
			assertEquals("Unexpected number of links", 1, links.length);
			IHyperlink link = links[0];
			assertTrue("Wrong file returned.", link.getHyperlinkText()
					.contains("utils.xsl"));
		} finally {
			model.releaseFromEdit();
		}
	}

}
