/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.model;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

public class TestStructuredModel extends TestCase {

	private boolean isSetup = false;
	

	private final String fProjectName = "DOCUMENT-LOADER";
	// is it a problem to unzip the same project in
	// different tests?
	private final String fZipFileName = "xml-document-loader-tests.zip";

	public TestStructuredModel() {
		super("TestStructuredModel");
	}

	protected void setUp() throws Exception {

		super.setUp();
		if (!this.isSetup) {
			
			doSetup();
			this.isSetup = true;
		}
	}

	private void doSetup() throws Exception {

		// root of workspace directory
		Location platformLocation = Platform.getInstanceLocation();

		ProjectUnzipUtility unzipUtil = new ProjectUnzipUtility();
		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, fZipFileName, ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		unzipUtil.unzipAndImport(zipFile, platformLocation.getURL().getFile());
		unzipUtil.initJavaProject(fProjectName);
	}

	public void testAboutToChangeModel() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			model.aboutToChangeModel();
			assertTrue(true);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testAddRemoveModelStateListener() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IModelStateListener listener = new IModelStateListener() {

				public void modelAboutToBeChanged(IStructuredModel model) {
					//
				}

				public void modelChanged(IStructuredModel model) {
					// 	
				}

				public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
					//
				}

				public void modelResourceDeleted(IStructuredModel model) {
					// 
				}

				public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
					// 		
				}

				public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
					// 	
				}

				public void modelReinitialized(IStructuredModel structuredModel) {
					// 
				}
			};

			model.aboutToChangeModel();

			model.removeModelStateListener(listener);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}


	/**
	 * Test expectes an exception since only a changedModel sent, without
	 * beginning 'aboutToChangeModel'
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * 
	 */
	public void testChangedModel() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			try {
				model.changedModel();
			}
			catch (Exception e) {
				assertTrue(e instanceof IllegalStateException);
			}
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetContentType() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			String ct = model.getContentTypeIdentifier();
			assertTrue("model has wrong content type:" + ct + " != " + ContentTypeIdForXML.ContentTypeID_XML, ct.equals(ContentTypeIdForXML.ContentTypeID_XML));
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetBaseLocation() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			String location = model.getBaseLocation();
			assertTrue("wrong base location", location.equals("/" + fProjectName + "/files/simple.xml"));
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}


	public void testGetFactoryRegistry() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			model.getFactoryRegistry();
			assertTrue(true);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetIndexedRegion() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			model.getIndexedRegion(0);
			assertTrue(true);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetIndexedRegions() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			// not implemented yet...
			// model.getIndexedRegions();
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}

	}

	public void testGetStructuredDocument() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument doc = model.getStructuredDocument();
			assertNotNull("document is null", doc);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testIsDirty() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			assertFalse("model should not be dirty", model.isDirty());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testIsModelStateChanging() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
		assertFalse("model should not be changing", model.isModelStateChanging());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testIsNew() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
		// this API seems strange
		// assertFalse("new model check failed", model.isNew());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testIsReinitializationNeeded() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
		assertFalse("reinitialization should not be needed", model.isReinitializationNeeded());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}


	public void testIsSaveNeeded() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			assertFalse("save should not be needed", model.isSaveNeeded());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testNewInstance() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredModel newInstance = null;
			try {
				newInstance = model.newInstance();
			}
			catch (IOException e) {
				assertTrue("IOException during model new instance", false);
			}
			assertNotNull("new instance is null", newInstance);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetModelResource() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		IResource resource = (IResource) model.getAdapter(IResource.class);
		assertNotNull("A resource wasn't obtained from the model", resource);
		assertEquals("The resource doesn't correspond to the model's base location", new Path("/" + fProjectName + "/files/simple.xml"), resource.getFullPath());
	}
	
	public void testUnmanagedModelResource() throws IOException, CoreException {
		IStructuredModel model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		IResource resource = (IResource) model.getAdapter(IResource.class);
		assertNull("A resource was obtained from an unmanaged model", resource);
	}

	public void testGetNodeResource() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		IndexedRegion region = model.getIndexedRegion(0);
		assertTrue("The region is not adaptable", region instanceof IAdaptable);
		IResource resource = (IResource) ((IAdaptable) region).getAdapter(IResource.class);
		assertNotNull("A resource wasn't obtained from the model", resource);
		assertEquals("The resource doesn't correspond to the model's base location", new Path("/" + fProjectName + "/files/simple.xml"), resource.getFullPath());
	}

	/**
	 * Be sure to release any models obtained from this method.
	 * 
	 */
	IStructuredModel getTestModel() throws IOException, CoreException {

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectName);
		IFile iFile = (IFile) project.findMember("/files/simple.xml");
		// fProjectName + "/files/simple.xml"


		IStructuredModel model = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();

		model = modelManager.getModelForEdit(iFile);


		return model;

	}
}
