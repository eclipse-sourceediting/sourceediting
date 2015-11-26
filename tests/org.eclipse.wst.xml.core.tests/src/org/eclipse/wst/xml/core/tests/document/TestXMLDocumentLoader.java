package org.eclipse.wst.xml.core.tests.document;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

public class TestXMLDocumentLoader extends TestCase {
	
	private boolean isSetup = false;
	private final String fProjectName ="DOCUMENT-LOADER";
	private final String fZipFileName = "xml-document-loader-tests.zip";
		
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
	
	
	public void testCreateNewEmptyStructuredDocument() {

		XMLDocumentLoader xmlDocumentLoader = new XMLDocumentLoader();
		IDocument document = null;
		
		document = xmlDocumentLoader.createNewStructuredDocument();
		assertNotNull("failed to create Empty document", document);
	}
	
	public void testCreateNewStructuredDocumentFromFile() {
		// from a file	
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fProjectName + "/files/simple.xml"));
			
		XMLDocumentLoader xmlDocumentLoader = new XMLDocumentLoader();
		IDocument document = null;
		try {
			document = xmlDocumentLoader.createNewStructuredDocument(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {	
			e.printStackTrace();
		}
		assertNotNull("failed to create document from IFile", document);
	}
	
	public void testCreateNewStructuredDocumentFromInputStream() {
		String fileLocation = "/files/simple.xml";
		String absoluteFileLocation = Platform.getInstanceLocation() + fProjectName + fileLocation;
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fProjectName + "/files/simple.xml"));
		
		XMLDocumentLoader xmlDocumentLoader = new XMLDocumentLoader();
		IDocument document = null;
		
		try {
			document = xmlDocumentLoader.createNewStructuredDocument(absoluteFileLocation, file.getContents());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {	
			e.printStackTrace();
		}
		assertNotNull("failed to create document from InputStream", document);
	}
	
	public void testCreateNewDocumentFromReaderWithEncodingRule() {
		
		String fileLocation = "/files/simple.xml";
		String absoluteFileLocation = Platform.getInstanceLocation().getURL().getPath().toString() + fProjectName + fileLocation;
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fProjectName + "/files/simple.xml"));
		
		XMLDocumentLoader xmlDocumentLoader = new XMLDocumentLoader();
		IDocument document = null;
		
		try {
			document = xmlDocumentLoader.createNewStructuredDocument(absoluteFileLocation, file.getContents());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {	
			e.printStackTrace();
		}
		assertNotNull("failed to create document from InputStream with EncodingRule", document);
	}
}
