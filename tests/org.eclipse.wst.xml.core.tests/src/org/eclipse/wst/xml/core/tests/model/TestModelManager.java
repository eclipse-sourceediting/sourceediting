/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

public class TestModelManager extends TestCase {

	private boolean isSetup = false;

	private final String fProjectName = "DOCUMENT-LOADER";
	private final String fZipFileName = "xml-document-loader-tests.zip";

	public TestModelManager() {
		super("TestModelManager");
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

	/**
	 * must release after use!
	 * 
	 * @return
	 */
	private IStructuredModel getStructuredModelForEdit() {
		// from a file
		IFile f = getFile();
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForEdit(f);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return model;
	}

	private IFile getFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fProjectName + "/files/simple.xml"));
	}
	
	private IModelManager getMM() {
		return StructuredModelManager.getModelManager();
	}

	public void testCopyModel() {
		IStructuredModel model = null;
		try {
			model = getStructuredModelForEdit();
			try {
				IStructuredModel modelCopy = getMM().copyModelForEdit(model.getId(), "newId");
				assertNotNull("copied model was null", modelCopy);
			}
			catch (ResourceInUse e) {
				e.printStackTrace();
			}
			
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testCreateNewInstance() throws IOException {
		IStructuredModel model = null;
		try {
			model = getStructuredModelForEdit();
			IStructuredModel newInstance = getMM().createNewInstance(model);
			assertTrue("failed to create new instance of: " + model, model != newInstance);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testCreateNewStructuredDocumentFor() throws IOException, CoreException {
		IStructuredModel model = null;
		try {
			IFile file = getFile();
			// ensure model already exists
			model = getStructuredModelForEdit();
			boolean resourceExists = false;
			try {
				getMM().createNewStructuredDocumentFor(file);
			}
			catch(ResourceAlreadyExists ex) {
				resourceExists = true;
			}
			assertTrue("should have gotten ResourceAlreadyExits exception", resourceExists);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testCreateStructuredDocumentFor()  throws IOException, ResourceAlreadyExists, CoreException {
		IStructuredModel model = null;
		try {
			IFile file = getFile();
			IStructuredDocument sDoc = getMM().createStructuredDocumentFor(file);
			assertNotNull("failed to create structured document", sDoc);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testGetExistingModelFromFile() throws IOException, CoreException {
		IFile f = getFile();
		IStructuredModel model = null;
		IStructuredModel existingModel = null;
		try {
			model = getStructuredModelForEdit();
			existingModel = StructuredModelManager.getModelManager().getExistingModelForEdit(f);
			assertNotNull("failed to get existing model", existingModel);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
			if (existingModel != null)
				existingModel.releaseFromEdit();
				
		}
	}

	public void testGetExistingModelFromDocument() {
		IStructuredModel model = null;
		IStructuredModel gottenModel = null;
		try {
			model = getStructuredModelForEdit();
			IStructuredDocument doc = model.getStructuredDocument();
			gottenModel = getMM().getExistingModelForEdit(doc);
			assertTrue("models should be the same instance", model == gottenModel);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
			if (gottenModel != null)
				gottenModel.releaseFromEdit();
		}
	}
	
	public void testGetModelFromFile() throws IOException, CoreException {
		IFile f = getFile();
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForEdit(f);
			assertNotNull("failed to get model", model);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testGetModelFromDocument() {
		IStructuredModel model = null;
		IStructuredModel gottenModel = null;
		try {
			model = getStructuredModelForEdit();
			IStructuredDocument doc = model.getStructuredDocument();
		    gottenModel = getMM().getModelForEdit(doc);
			assertTrue("models should be the same instance", model == gottenModel);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
			if (gottenModel != null)
				gottenModel.releaseFromEdit();
		}
	}

	public void testIsShared() {
		IStructuredModel model = null;
		IStructuredModel model2 = null;
		try {
			model = getStructuredModelForEdit();
			model2 = getStructuredModelForEdit();
			// TODO: will change when API added to MM
			boolean isShared = getMM().isShared(model.getId());
			assertTrue("model should be shared", isShared);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
			if (model2 != null)
				model2.releaseFromEdit();
		}
	}

	public void testReinitialize() throws IOException {
		IStructuredModel model = null;
		try {
			model = getStructuredModelForEdit();
			getMM().reinitialize(model);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testReleaseModel() throws IOException {
		// TODO: will need to implement when MM has this function
	}

	public void testSaveModel() throws UnsupportedEncodingException, CoreException, IOException {
		IStructuredModel model = null;
		try {
			model = getStructuredModelForEdit();
			// TODO: will need to change when MM has this function
			model.save();
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testSaveModelIfNotShared() {
		// TODO: will need to implement when MM has this function
	}
}
