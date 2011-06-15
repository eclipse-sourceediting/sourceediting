/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

/**
 * Tests to ensure that the cyclic group references does'nt cause StackOverflow
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=242639
 */
public class TestCyclicGroupReferences extends TestCase {
	private boolean isSetup = false;

	/**
	 * The target project name.
	 */
	private final String fProjectName = "CyclicGroupReference"; //$NON-NLS-N$

	/**
	 * The name of the zip file containing the project to import.
	 */
	private final String fZipFileName = "cyclicgrouptestfiles.zip"; //$NON-NLS-1$

	public TestCyclicGroupReferences() {
		super("TestCyclicGroupReferences");
	}

	/**
	 * Test CMVisitor for cyclic group references.
	 */
	public void testCyclicGroupReferences() {

		IFile file = getFile("Test.xml"); //$NON-NLS-1$
		CMVisitor cmVisitor = new CMVisitor();
		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager
					.getModelManager();
			model = modelManager.getModelForRead(file);
			assertNotNull("failure loading model", model); //$NON-NLS-1$
			IDOMModel domModel = (IDOMModel) model;
			IDOMDocument document = domModel.getDocument();
			assertNotNull("failure getting document", document); //$NON-NLS-1$
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(model);
			assertNotNull("ModelQuery is missing", modelQuery); //$NON-NLS-1$
			IDOMElement documentElement = (IDOMElement) document
					.getDocumentElement();
			assertNotNull("missing document element", documentElement); //$NON-NLS-1$
			CMElementDeclaration cmElementDeclaration = modelQuery
					.getCMElementDeclaration(documentElement);
			assertNotNull(
					"No element declaration for" + documentElement.getNodeName() + " (" + documentElement.getNamespaceURI() + ")", cmElementDeclaration); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			cmVisitor.visitCMElementDeclaration(cmElementDeclaration);
		} 
		catch (Throwable th) {
			fail("Test failed :" + th.getClass().getCanonicalName());
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}

	}

	protected void setUp() throws Exception {
		super.setUp();

		if (!this.isSetup) {
			doSetup();
			this.isSetup = true;
		}
	}

	/**
	 * Sets up the required project in the workspace.
	 * 
	 * @throws Exception
	 */
	private void doSetup() throws Exception {
		Location platformLocation = Platform.getInstanceLocation();

		ProjectUnzipUtility unzipUtil = new ProjectUnzipUtility();
		File zipFile = FileUtil.makeFileFor(
				ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, fZipFileName,
				ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		URL platformLocationURL = platformLocation.getURL();
		String file = platformLocationURL.getFile();
		unzipUtil.unzipAndImport(zipFile, file);
		unzipUtil.initJavaProject(fProjectName);
	}


	/**
	 * Utility to retrieve the IFile for the given file name. The file is
	 * expected to be in the workspace in the project named by
	 * {@link #fProjectName}.
	 * 
	 * @param fileName
	 *            the name of the file to retrieve.
	 * @return an IFile.
	 */
	private IFile getFile(String fileName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		Path path = new Path(fProjectName + "/" + fileName); //$NON-NLS-1$
		return root.getFile(path);
	}
}