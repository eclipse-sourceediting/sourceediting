/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.tests.viewer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.css.ui.StructuredTextViewerConfigurationCSS;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

public class TestCSSContentAssist extends TestCase {

	protected String projectName = null;
	protected String fileName = null;
	protected String resourcesFolder = null;
	private StructuredTextViewer sourceViewer = null;
	protected IStructuredDocument document = null;
	private IStructuredModel model;
	protected IFile file = null;

	public TestCSSContentAssist() {
	}

	protected void setUp() throws Exception {

		super.setUp();
		projectName = "CSSContentAssistForMedia";
		fileName = "mediaexample.css";
		resourcesFolder = "testresources";

		String filePath = setupProject();
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(filePath));

		if (file != null && !file.exists()) {
			Assert.fail("Unable to locate " + file + ".");
		}

		loadFile();

		initializeSourceViewer();
	}

	private void initializeSourceViewer() {
		if (Display.getCurrent() != null) {

			Shell shell = null;
			Composite parent = null;

			if (PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			} else {
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);

			// dummy viewer
			sourceViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		} else {
			Assert.fail("Unable to run the test as a display must be available.");
		}

		configureSourceViewer();
	}

	protected void configureSourceViewer() {
		sourceViewer.configure(new StructuredTextViewerConfigurationCSS());

		sourceViewer.setDocument(document);
	}

	protected void loadFile() throws ResourceAlreadyExists, ResourceInUse, IOException, CoreException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		model = modelManager.getModelForEdit(file);
		document = model.getStructuredDocument();
	}

	protected static void copyBundleEntryIntoWorkspace(String resourcesPath, String fullNewProjectPath) throws CoreException, FileNotFoundException,
	IOException, CoreException {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fullNewProjectPath));
		InputStream input = new BufferedInputStream(new FileInputStream(resourcesPath));
		if (input != null) {
			byte[] b = new byte[2048];
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int i = -1;
			while ((i = input.read(b)) > -1) {
				output.write(b, 0, i);
			}

			if (file != null) {
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
				}
				else {
					file.setContents(new ByteArrayInputStream(output.toByteArray()), true, false, new NullProgressMonitor());
				}
			}
		}
	}

	protected String setupProject() throws Exception{
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		} catch (CoreException e) {

		}
		String filePath = project.getFullPath().addTrailingSeparator().append(fileName).toString();

		String f = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		copyBundleEntryIntoWorkspace(f + resourcesFolder + "/" + fileName, filePath);

		return filePath;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testContentAssistInsideMedia() throws Exception {

		try {
			CSSContentAssistProcessor processor = new CSSContentAssistProcessor();
			ICompletionProposal[] proposals = processor.computeCompletionProposals(sourceViewer, 24);
			assertTrue("No proposals offset.", proposals.length > 0);
			ICompletionProposal proposal = proposals[0];
			assertEquals("Wrong proposal returned for ACRONYM.", "azimuth", proposal.getDisplayString()); 
		} finally {
			model.releaseFromEdit();
		}
	}
}
