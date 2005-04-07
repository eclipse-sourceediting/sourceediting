package org.eclipse.wst.xml.core.tests.model;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xml.core.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

public class TestStructuredModel extends TestCase {
	
	private boolean isSetup = false;
	private XMLModelLoader fLoader = null;

	private final String fProjectName ="DOCUMENT-LOADER";
	// is it a problem to unzip the same project in
	// different tests?
	private final String fZipFileName = "xml-document-loader-tests.zip";
	
    public TestStructuredModel() {
        super("TestStructuredModel");
    }
	
    protected void setUp() throws Exception {
		
    	super.setUp();
		if(!this.isSetup){
			fLoader = new XMLModelLoader();
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
	
	public IStructuredModel getUnmanagedModel() {
		// from a file	
		IFile f = getFile();
		IStructuredModel model = null;
		try {
			model = fLoader.createModel();
			fLoader.load(f, model);
			ModelHandlerForXML xmlModelHandler = new ModelHandlerForXML();
			model.setModelHandler(xmlModelHandler);
			model.setBaseLocation(fProjectName + "/files/simple.xml");
			try {
				model.setId(fProjectName + "/files/simple.xml");
			}
			catch (ResourceInUse e) {
				// ignore
			}
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
	
	public void testAboutToChangeModel() {
		IStructuredModel model = getUnmanagedModel();
		model.aboutToChangeModel();
	}

	public void testAddRemoveModelStateListener() {
		IStructuredModel model = getUnmanagedModel();
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


	public void testChangedModel() {
		IStructuredModel model = getUnmanagedModel();
		model.changedModel();
	}


	public void testGetContentType()  {
		IStructuredModel model = getUnmanagedModel();
		String ct = model.getContentTypeIdentifier();
		assertTrue("model has wrong content type:" + ct + " != " + ContentTypeIdForXML.ContentTypeID_XML, ct.equals(ContentTypeIdForXML.ContentTypeID_XML));
	}
	
	public void testGetBaseLocation() {
		IStructuredModel model = getUnmanagedModel();
		String location = model.getBaseLocation();
		assertTrue("wrong base location", location.equals(fProjectName + "/files/simple.xml"));
	}


	public void testGetFactoryRegistry() {
		IStructuredModel model = getUnmanagedModel();
		model.getFactoryRegistry();
	}

	public void testGetIndexedRegion() {
		IStructuredModel model = getUnmanagedModel();	
		model.getIndexedRegion(0);
	}

	public void testGetIndexedRegions() {
		IStructuredModel model = getUnmanagedModel();
		// not implemented yet...
		//model.getIndexedRegions();
	}

	public void  testGetStructuredDocument() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument doc = model.getStructuredDocument();
		assertNotNull("document is null", doc);
	}

	public void testIsDirty() {
		IStructuredModel model = getUnmanagedModel();
		assertFalse("model should not be dirty", model.isDirty());
	}

	public void testIsModelStateChanging() {
		IStructuredModel model = getUnmanagedModel();
		assertFalse("model should not be changing", model.isModelStateChanging());
	}

	public void testIsNew() {
		IStructuredModel model = getUnmanagedModel();
		// this API seems strange
		//assertFalse("new model check failed", model.isNew());
	}

	public void testIsReinitializationNeeded() {
		IStructuredModel model = getUnmanagedModel();
		assertFalse("reinitialization should not be needed", model.isReinitializationNeeded());
	}


	public void testIsSaveNeeded() {
		IStructuredModel model = getUnmanagedModel();
		assertFalse("save should not be needed", model.isSaveNeeded());
	}

	public void testNewInstance() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredModel newInstance = null;
		try {
			newInstance = model.newInstance();
		}
		catch (IOException e) {
			assertTrue("IOException during model new instance", false);
		}
		assertNotNull("new instance is null", newInstance);
	}
}
