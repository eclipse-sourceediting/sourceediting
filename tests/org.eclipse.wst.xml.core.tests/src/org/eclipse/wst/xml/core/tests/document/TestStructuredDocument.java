/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.document;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.Position;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
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
	

	private final String fProjectName = "DOCUMENT-LOADER";
	private final String fZipFileName = "xml-document-loader-tests.zip";

	public TestStructuredDocument() {
		super("TestStructuredDocument");
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



	public void testDocumentChangingListener() throws BadLocationException, IOException, CoreException {
		// TODO: may need to fake events to really tests this

		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();

			TestListener listener = new TestListener();

			sDoc.addDocumentChangingListener(listener);
			String text = sDoc.get();
			sDoc.replace(0, text.length(), text);

			sDoc.removeDocumentChangingListener(listener);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testReadOnly() throws IOException, CoreException {

		IStructuredModel model = getTestModel();
		try {
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
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetRegionAtCharacterOffset() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			IStructuredDocumentRegion r = sDoc.getRegionAtCharacterOffset(0);
			assertNotNull("couldn't get region at offset: 0", r);

			r = sDoc.getRegionAtCharacterOffset(sDoc.getLength());
			assertNotNull("couldn't get region at offset: " + sDoc.getLength(), r);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}

		}
	}

	public void testGetRegionList() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			IStructuredDocumentRegionList regions = sDoc.getRegionList();
			assertNotNull("couldn't get region list", regions);
			assertEquals(17, regions.getLength());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetText() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			String text = sDoc.getText();
			String expectedText = "<?xml version=\"1.0\"?>\r\n<list>\r\n\t<item1 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\">\r\n\t\tone\r\n\t</item1>\r\n\t<item2 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\" >\r\n\t\ttwo\r\n\t</item2>\r\n\t<item3 attr1=\"attr1\" attr2=\"attr2\" attr3=\"attr3\">\r\n\t\tthree\r\n\t</item3>\r\n</list>";
			assertEquals(expectedText, text);
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
			IStructuredDocument sDoc = model.getStructuredDocument();
			IStructuredDocument newDoc = sDoc.newInstance();
			assertNotSame("document instances should be different", sDoc, newDoc);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	public void testReplace() throws BadLocationException, IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			long modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp();
			}
			sDoc.replace(0, sDoc.getLength(), "replaced");

			String text = sDoc.getText();
			assertEquals("replaced", text);

			if (modificationStamp > -1)
				assertTrue("replace text modification stamp failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	public void testReplaceWithModificationStamp() throws BadLocationException, IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			if (sDoc instanceof IDocumentExtension4) {
				IDocumentExtension4 ext4 = (IDocumentExtension4)sDoc;
				long modificationStamp = ext4.getModificationStamp() + 1;
				
				ext4.replace(0, sDoc.getLength(), "replaced", modificationStamp);
				String text = sDoc.getText();
				assertEquals("replaced", text);
				
				assertTrue("replace text modification stamp failed", modificationStamp ==((IDocumentExtension4)sDoc).getModificationStamp());
			}
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testReplaceText() throws BadLocationException, IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			long modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp();
			}
			sDoc.replaceText(this, 0, sDoc.getLength(), "replaced");

			String text = sDoc.getText();
			assertEquals("replaced", text);

			if (modificationStamp > -1)
				assertTrue("replace text modification stamp failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testOverrideReadOnlyReplaceText() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			sDoc.makeReadOnly(0, 1);
			
			long modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp();
			}
			
			sDoc.replaceText(this, 0, sDoc.getLength(), "replaced");
			assertNotSame("text should not have been replacable (read only)", "replaced", sDoc.getText());
			
			if (modificationStamp > -1)
				assertTrue("replace text modification stamp (read only) failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
			
			modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp() + 1;
			}
			
			sDoc.replaceText(this, 0, sDoc.getLength(), "replaced", true);
			assertEquals("text should have been forced replaced", "replaced", sDoc.getText());
			
			if (modificationStamp > -1)
				assertTrue("replace text modification stamp failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	public void testSet() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			long modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp();
			}
			sDoc.set("set text");

			assertEquals("set text failed", "set text", sDoc.getText());
			if (modificationStamp > -1)
				assertTrue("set text modification stamp failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
	
	public void testSetWithModificationStamp() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			if (sDoc instanceof IDocumentExtension4) {
				IDocumentExtension4 ext4 = (IDocumentExtension4)sDoc;
				long modificationStamp = ext4.getModificationStamp() + 1;
				ext4.set("set text", modificationStamp);

				assertEquals("set text failed", "set text", sDoc.getText());
				assertTrue("set text modification stamp failed", modificationStamp ==((IDocumentExtension4)sDoc).getModificationStamp());
			}
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testSetText() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			long modificationStamp = -1;
			if (sDoc instanceof IDocumentExtension4) {
				modificationStamp = ((IDocumentExtension4)sDoc).getModificationStamp();
			}
			sDoc.setText(this, "set text");

			assertEquals("set text failed", "set text", sDoc.getText());
			if (modificationStamp > -1)
				assertTrue("set text modification stamp failed", modificationStamp !=((IDocumentExtension4)sDoc).getModificationStamp());
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testGetEncodingMemento() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = null;
			sDoc = model.getStructuredDocument();
			EncodingMemento memento = sDoc.getEncodingMemento();
			assertNotNull("couldn't get encoding memento", memento);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	/*
	 * Verify that the first delimiter is \r\n regardless of
	 * preferences or platform
	 */
	public void testGetDetectedLineDelimiter() throws IOException, CoreException, BadLocationException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			String delim = sDoc.getLineDelimiter();
			assertEquals("wrong preferred line delmiter", "\r\n", delim);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testSetEncodingMemento() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			EncodingMemento memento = new EncodingMemento();
			sDoc.setEncodingMemento(memento);
			EncodingMemento gottenMemento = sDoc.getEncodingMemento();
			assertEquals("mementos don't match", memento, gottenMemento);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	void setDetectedLineDelimiter() throws IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
			IStructuredDocument sDoc = model.getStructuredDocument();
			sDoc.setPreferredLineDelimiter("\n");
			String delim = sDoc.getPreferredLineDelimiter();
			assertEquals("set line delimiter failed", "\n", delim);

			sDoc.setPreferredLineDelimiter("\r\n");
			delim = sDoc.getPreferredLineDelimiter();
			assertEquals("set line delimiter failed", "\r\n", delim);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	public void testStringMatches() {
		// TODO: will need a test when this API is created
		// IStructuredModel model = getTestModel();
		// IStructuredDocument sDoc = model.getStructuredDocument();
	}

	public void testCreatePosition() throws BadPositionCategoryException, BadLocationException, IOException, CoreException {
		IStructuredModel model = getTestModel();
		try {
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
					if (positions[j] == p) {
						found++;
					}
				}
			}
			assertEquals("wrong number of positions", categories.length, found);
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	/** Tests that when a tag is read-only, its entirety is read-only. */
	public void testReadOnlyTag() throws CoreException, IOException {
		IStructuredModel model = getTestModel();
		try {
			IDOMNode node = (IDOMNode) model.getIndexedRegion(23);
			assertNotNull("couldn't get node", node);
			// all descendants are read-only
			node.setChildEditable(false);
			IStructuredDocument document = model.getStructuredDocument();
			assertTrue(document.containsReadOnly(80, 0));
			
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
}
