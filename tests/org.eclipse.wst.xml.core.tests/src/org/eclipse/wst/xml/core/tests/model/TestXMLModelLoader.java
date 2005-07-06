package org.eclipse.wst.xml.core.tests.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

/**
 *
 * 
 */
public class TestXMLModelLoader extends TestCase {
	
	private boolean isSetup = false;
	private XMLModelLoader fLoader = null;

	private final String fProjectName ="DOCUMENT-LOADER";
	private final String fZipFileName = "xml-document-loader-tests.zip";
	
	
    public TestXMLModelLoader() {
        super("TestModelLoader");
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
	
	public void testCreateEmptyModel() {
		IStructuredModel model = fLoader.createModel();
		assertNotNull("model is null", model);
	}
	
	public void testCreateModelWithDocument() {
		IStructuredModel emptyModel = fLoader.createModel();
		IStructuredDocument sDoc = emptyModel.getStructuredDocument();
		sDoc.set("<?xml version=\"1.0\"?>\n<test>\n<item attr=\"val\" /></test>\n");
		
		// null handlder is normally not valid, including only for this test.
		IStructuredModel modelFromDoc = fLoader.createModel(sDoc, "/test", null);
		assertNotNull("model from doc is null", modelFromDoc);
	}
	
	public void testCreateModelWithModel() {
		IStructuredModel emptyModel = fLoader.createModel();
		IStructuredDocument sDoc = emptyModel.getStructuredDocument();
		sDoc.set("<?xml version=\"1.0\"?>\n<test>\n<item attr=\"val\" /></test>\n");
		// TODO: need an existing full model
		//IStructuredModel modelFromModel = fLoader.createModel(emptyModel);
		//assertNotNull("model from model is null", modelFromModel);
	}
	
	public void testGetAdapterFactories() {
		List factories = fLoader.getAdapterFactories();
		assertTrue("there were no adapter factories for XML", factories.size() > 0);
	}
	
	public void testLoad() {
		// from a file	
		IFile f = getFile();
		
		try {
			IStructuredModel model = fLoader.createModel();
			fLoader.load(f, model);
			String text = model.getStructuredDocument().get();
			assertTrue("failed to load", text.length() > 0);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void testLoadFromStream() {
		// need to fix this test
		// unless the API is going away...
//		IFile f = getFile();
//		
//		try {
//			IStructuredModel model = fLoader.createModel();
//			fLoader.load(f.getContents(), model, EncodingRule.FORCE_DEFAULT);
//			String text = model.getStructuredDocument().get();
//			assertTrue("failed to load", text.length() > 0);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		catch (CoreException e) {
//			e.printStackTrace();
//		}
	}
	
	
	private IFile getFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fProjectName + "/files/simple.xml"));	
	}
}
