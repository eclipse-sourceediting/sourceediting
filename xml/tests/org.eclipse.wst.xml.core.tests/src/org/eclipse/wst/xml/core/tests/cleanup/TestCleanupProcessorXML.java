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
package org.eclipse.wst.xml.core.tests.cleanup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.cleanup.CleanupProcessorXML;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class TestCleanupProcessorXML extends TestCase {

	private CleanupProcessorXML cleanup;
	protected void setUp() throws Exception {
		cleanup = new CleanupProcessorXML();
	}

	/**
	 * must release model (from edit) after
	 * 
	 * @param filename
	 *            relative to this class (TestCleanupProcessorXML)
	 */
	private IStructuredModel getModelForEdit(final String filename) {

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			InputStream inStream = getClass().getResourceAsStream(filename);
			if (inStream == null)
				throw new FileNotFoundException("Can't file resource stream " + filename);
			final String baseFile = getClass().getResource(filename).toString();
			model = modelManager.getModelForEdit(baseFile, inStream, new URIResolver() {

				String fBase = baseFile;

				public String getFileBaseLocation() {
					return fBase;
				}

				public String getLocationByURI(String uri) {
					return getLocationByURI(uri, fBase);
				}

				public String getLocationByURI(String uri, boolean resolveCrossProjectLinks) {
					return getLocationByURI(uri);
				}

				public String getLocationByURI(String uri, String baseReference) {
					int lastSlash = baseReference.lastIndexOf("/");
					if (lastSlash > 0)
						return baseReference.substring(0, lastSlash + 1) + uri;
					return baseReference;
				}

				public String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks) {
					return getLocationByURI(uri, baseReference);
				}

				public IProject getProject() {
					return null;
				}

				public IContainer getRootLocation() {
					return null;
				}

				public InputStream getURIStream(String uri) {
					return getClass().getResourceAsStream(getLocationByURI(uri));
				}

				public void setFileBaseLocation(String newLocation) {
					this.fBase = newLocation;
				}

				public void setProject(IProject newProject) {
				}
			});
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return model;
	}

	public void testCleanupNoHeader() {
		String path = "testfiles/noheader.xml";
		IStructuredModel model = null;
		try {
			model = getModelForEdit(path);
			assertNotNull("Could not load model for [" + path + "]", model);

			cleanup.getCleanupPreferences().setFormatSource(false);
			cleanup.cleanupModel(model);

			IndexedRegion region = model.getIndexedRegion(0);
			assertTrue("First region is not a node", region instanceof Node);

			Node node = (Node) region;
			assertTrue("First region is not a processing instruction", node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE);

			ProcessingInstruction pi = (ProcessingInstruction) node;
			assertEquals("Node is not an XML declaration", "xml", pi.getTarget());
			String encoding = XMLCorePlugin.getDefault().getPluginPreferences().getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
			assertEquals("version=\"1.0\" encoding=\"" + encoding + "\"", pi.getData());
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testCleanupFixDeclaration() {
		String path = "testfiles/fixdecl.xml";
		IStructuredModel model = null;
		try {
			model = getModelForEdit(path);
			assertNotNull("Could not load model for [" + path + "]", model);

			cleanup.getCleanupPreferences().setFormatSource(false);
			cleanup.cleanupModel(model);

			IndexedRegion region = model.getIndexedRegion(0);
			assertTrue("First region is not a node", region instanceof Node);

			Node node = (Node) region;
			assertTrue("First region is not a processing instruction", node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE);

			ProcessingInstruction pi = (ProcessingInstruction) node;
			assertEquals("Node is not an XML declaration", "xml", pi.getTarget());
			assertEquals("version=\"1.0\" encoding=\"ISO-8859-1\"", pi.getData());
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}
}
