/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XSITypeTest extends TestCase {

	private static final String ZIP_FILE_NAME = "xsiTestProject.zip"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$

	protected void setUp() throws Exception {
		super.setUp();
		Location platformLocation = Platform.getInstanceLocation();
		ProjectUnzipUtility unzipUtil = new ProjectUnzipUtility();
		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, ZIP_FILE_NAME, ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		unzipUtil.unzipAndImport(zipFile, platformLocation.getURL().getFile());
		unzipUtil.initJavaProject(PROJECT_NAME);
	}

	public void testXSIType() {

		IStructuredModel structuredModel = null;
		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(PROJECT_NAME + "/test.xml")); //$NON-NLS-1$
			IModelManager modelManager = StructuredModelManager.getModelManager();
			structuredModel = modelManager.getModelForRead(file);
			Document document = ((IDOMModel) structuredModel).getDocument();
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
			NodeList nodeList = null;
			CMElementDeclaration cmElementDeclaration = null;

			// Check for local XSI type
			nodeList = document.getElementsByTagName("a"); //$NON-NLS-1$
			cmElementDeclaration = modelQuery.getCMElementDeclaration((Element)nodeList.item(0));
			assertNotNull("Local XSI type failed to be resolved", cmElementDeclaration); //$NON-NLS-1$

			// Check for external XSI type
			nodeList = document.getElementsByTagName("b"); //$NON-NLS-1$
			cmElementDeclaration = modelQuery.getCMElementDeclaration((Element)nodeList.item(0));
			assertNotNull("External XSI type failed to be resolved", cmElementDeclaration); //$NON-NLS-1$

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			if (structuredModel != null) {
				structuredModel.releaseFromRead();
			}
		}
	}	

}