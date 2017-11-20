/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 213775 - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.style;

import java.io.File;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
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
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;
import org.eclipse.wst.xsl.core.internal.text.rules.StructuredTextPartitionerForXSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestXSLLineStyleProvider extends AbstractXSLUITest {

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

	public TestXSLLineStyleProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Setup the necessary projects, files, and source viewer for the tests.
	 */
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
			fail("Unable to locate " + fileName + " stylesheet.");
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
			fail("Unable to run the test as a display must be available.");
		}

		configureSourceViewer();
	}

	protected void configureSourceViewer() {
		sourceViewer.configure(xslConfiguration);

		sourceViewer.setDocument(document);
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

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		parent.dispose();
		if (model != null) {
			model.releaseFromEdit();
		}
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

	private LineStyleProvider initializeProvider() {
		LineStyleProvider[] lineStyleProviders = getLineStyleProviders();
		LineStyleProvider lineStyleProvider = lineStyleProviders[0];

		Highlighter highlighter = new Highlighter();
		lineStyleProvider.init(document, highlighter);
		return lineStyleProvider;
	}

	private IRegion getDocumentRangeFromWidgetRange(int offset, int length) {
		IRegion styleRegion = null;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = sourceViewer;
			styleRegion = extension.widgetRange2ModelRange(new Region(offset,
					length));
		} else {
			IRegion vr = null;
			if (sourceViewer != null)
				vr = sourceViewer.getVisibleRegion();
			else
				vr = new Region(0, document.getLength());

			if (offset <= vr.getLength()) {
				styleRegion = new Region(offset + vr.getOffset(), length);
			}
		}
		return styleRegion;
	}

	private void applyStyles(LineStyleProvider provider,
			ITypedRegion[] partitions, ArrayList holdStyleResults) {
		for (int i = 0; i < partitions.length; i++) {
			ITypedRegion currentPartition = partitions[i];
			boolean handled = provider.prepareRegions(currentPartition,
					currentPartition.getOffset(), currentPartition.getLength(),
					holdStyleResults);
			if (Debug.syntaxHighlighting) {
				if (!handled) {
					System.out
							.println("Did not handle highlighting in Highlighter inner while"); //$NON-NLS-1$
				}
			}
		}
	}

	@Test
	public void testHasLineStyleProvider() throws Exception {
		setUpTest("utils.xsl");

		LineStyleProvider[] lineStyleProviders = getLineStyleProviders();
		assertNotNull("No line style providers found.", lineStyleProviders);
		assertEquals("Wrong number of providers", 1, lineStyleProviders.length);
	}

	@Test
	public void testInitializeLineStyleProvider() throws Exception {
		setUpTest("utils.xsl");
		initializeProvider();
	}

	@Test
	public void testPrepareRegion() throws Exception {
		setUpTest("utils.xsl");
		LineStyleProvider provider = initializeProvider();
		ITypedRegion[] partitions = setupPartitions();

		assertTrue("No Partitions found.", partitions.length > 0);
		ArrayList holdStyleResults = new ArrayList();
		applyStyles(provider, partitions, holdStyleResults);
		assertFalse("No styles applied.", holdStyleResults.isEmpty());
		assertEquals("Unexpected StyleRange size", 241, holdStyleResults.size());
	}

	private ITypedRegion[] setupPartitions() throws BadLocationException {
		int startOffset = document.getFirstStructuredDocumentRegion()
				.getStartOffset();
		int endLineLength = document.getLength();

		IRegion styleRegion = getDocumentRangeFromWidgetRange(startOffset,
				endLineLength);
		ITypedRegion[] partitions = TextUtilities.computePartitioning(document,
				Partitioning, styleRegion.getOffset(), styleRegion.getLength(),
				false);
		return partitions;
	}

}
