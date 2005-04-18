package org.eclipse.wst.xml.core.tests.document;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.Position;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.xml.core.internal.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;

public class TestStructuredDocument extends TestCase {
	
	class TestListener implements IStructuredDocumentListener {
		
		boolean newModelCalled = false;
		boolean noChangeCalled = false;
		boolean nodesReplacedCalled = false;
		boolean regionChangedCalled = false;
		boolean regionsReplacedCalled = false;
		
		public void newModel(NewDocumentEvent structuredDocumentEvent) {
			newModelCalled = true;
		}

		public void noChange(NoChangeEvent structuredDocumentEvent) {
			noChangeCalled = true;
		}

		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			nodesReplacedCalled = true;
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			regionChangedCalled = true;
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			regionsReplacedCalled = true;
		}
	}
	
	private boolean isSetup = false;
	private XMLModelLoader fLoader = null;

	private final String fProjectName ="DOCUMENT-LOADER";
	private final String fZipFileName = "xml-document-loader-tests.zip";
	
    public TestStructuredDocument() {
        super("TestStructuredDocument");
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
	
	
	
	public void testDocumentChangingListener() throws BadLocationException {
		// TODO: may need to fake events to really tests this
		
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		
		TestListener listener = new TestListener();
		
		sDoc.addDocumentChangingListener(listener);
		String text = sDoc.get();
		sDoc.replace(0, text.length(), text);
		
		sDoc.removeDocumentChangingListener(listener);
	}

	public void testReadOnly() {
		
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		String text = sDoc.get();
		
		sDoc.clearReadOnly(0, text.length());
		assertFalse(sDoc.containsReadOnly(0, text.length()));
		
		sDoc.makeReadOnly(0, 10);
		assertTrue(sDoc.containsReadOnly(9, 1));
		assertTrue(sDoc.containsReadOnly(0, text.length()));
		
		sDoc.clearReadOnly(0, text.length());
		assertFalse(sDoc.containsReadOnly(0, text.length()));
	}
	
	public void testGetRegionAtCharacterOffset() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		IStructuredDocumentRegion r = sDoc.getRegionAtCharacterOffset(0);
		assertNotNull("couldn't get region at offset: 0", r);
		
		r = sDoc.getRegionAtCharacterOffset(sDoc.getLength());
		assertNotNull("couldn't get region at offset: " + sDoc.getLength(), r);
	}

	public void testGetRegionList() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		IStructuredDocumentRegionList regions = sDoc.getRegionList();
		assertNotNull("couldn't get region list", regions);
		assertEquals(17, regions.getLength());
		
	}
	
	public void testGetText() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		String text = sDoc.getText();
		String expectedText = "<?xml version=\"1.0\"?>\r\n<list>\r\n\t<item1 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\">\r\n\t\tone\r\n\t</item1>\r\n\t<item2 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\" >\r\n\t\ttwo\r\n\t</item2>\r\n\t<item3 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\">\r\n\t\tthree\r\n\t</item3>\r\n</list>";
		assertEquals(expectedText, text);
	}

	public void testNewInstance() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		IStructuredDocument newDoc = sDoc.newInstance();
		assertNotSame("document instances should be different", sDoc, newDoc);
	}

	public void testReplaceText() throws BadLocationException {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		sDoc.replace(0, sDoc.getLength(), "replaced");
		
		String text = sDoc.getText();
		assertEquals("replaced", text);
	}

	public void testOverrideReadOnlyReplaceText() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		sDoc.makeReadOnly(0, 1);
		sDoc.replaceText(this, 0, sDoc.getLength(), "replaced");
		assertNotSame("text should not have been replacable (read only)", "replaced", sDoc.getText());

		sDoc.replaceText(this, 0, sDoc.getLength(), "replaced", true);
		assertEquals("text should have been forced replaced", "replaced", sDoc.getText());
	}

	public void testSetText() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		sDoc.setText(this, "set text");
		assertEquals("set text failed", "set text", sDoc.getText());
	}

	public void testGetEncodingMemento() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		EncodingMemento memento = sDoc.getEncodingMemento();
		assertNotNull("couldn't get encoding memento", memento);
	}

	public void testGetDetectedLineDelimiter() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		String delim = sDoc.getLineDelimiter();
		assertEquals("wrong preferred line delmiter", "\r\n", delim);
	}

	public void testSetEncodingMemento() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		EncodingMemento memento = new EncodingMemento();
		sDoc.setEncodingMemento(memento);
		EncodingMemento gottenMemento = sDoc.getEncodingMemento();
		assertEquals("mementos don't match", memento, gottenMemento);
	}

	public void setDetectedLineDelimiter() {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		sDoc.setPreferredLineDelimiter("\n");
		String delim = sDoc.getPreferredLineDelimiter();
		assertEquals("set line delimiter failed", "\n", delim);
		
		sDoc.setPreferredLineDelimiter("\r\n");
		delim = sDoc.getPreferredLineDelimiter();
		assertEquals("set line delimiter failed", "\r\n", delim);
	}

	public void testStringMatches() {
		// TODO: will need a test when this API is created
//		IStructuredModel model = getUnmanagedModel();
//		IStructuredDocument sDoc = model.getStructuredDocument();
	}
	
	public void testCreatePosition() throws BadPositionCategoryException, BadLocationException {
		IStructuredModel model = getUnmanagedModel();
		IStructuredDocument sDoc = model.getStructuredDocument();
		Position p = new Position(0, 10);
		String[] categories = sDoc.getPositionCategories();
		for (int i = 0; i < categories.length; i++) {
			sDoc.addPosition(categories[i], p);
		}
		int found = 0;
		for (int i = 0; i < categories.length; i++) {
			Position[] positions = sDoc.getPositions(categories[i]);
			for (int j = 0; j < positions.length; j++) {
				if(positions[j] == p) {
					found++;
				}
			}
		}
		assertEquals("wrong number of positions", categories.length, found);
	}
}
