/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.performance;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TestModelPerformanceOLD extends TestCase {

	public TestModelPerformanceOLD(String name) {
		super(name);
	}

	public void testStructuredModel100JSP() throws IOException {
		doStructuredModelTest("Test100K.jsp");
	}

	public void testStructuredModel100HTML() throws IOException {
		doStructuredModelTest("Test100K.html");
	}

	public void testStructuredModel100XML() throws IOException {
		doStructuredModelTest("Test100K.xml");
	}

	public void testStructuredModel300JSP() throws IOException {
		doStructuredModelTest("Test300K.jsp");
	}

	public void testStructuredModel300HTML() throws IOException {
		doStructuredModelTest("Test300K.html");
	}

	public void testStructuredModel300XML() throws IOException {
		doStructuredModelTest("Test300K.xml");
	}

	protected int countNodes(Node node) {
		int count = 1; // one for this node
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			count = count + countNodes(child);
		}
		return count;
	}

	//    /**
	//     * To maintain compatibility with V5 GA version, 
	//     * this method should not used.
	//     */
	//    protected int countAdapters(Node node) {
	//        int count = ((TextRegionListImpl) node).getAdapterCount();
	//        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
	//            count = count + countAdapters(child);
	//        }
	//        return count;
	//    }
	protected void doStructuredModelTest(String filename) throws IOException {
		int nodeCount = 0;
		//        int adapterCount = 0;
		int nFactories = 0;
		int nPropagatingFactories = 0;
		int nEmbeddedFactories = 0;

		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		System.out.println();
		float total = 0;
		int nTrials = 101;
		for (int i = 0; i < nTrials; i++) {
			InputStream inStream = getClass().getResourceAsStream(filename);
			//            // run garbage collection to get a little more consistent times.
			//            // but don't GC to get more realistic numbers
			//            System.gc();
			//            System.gc();
			long startTime = System.currentTimeMillis();
			IStructuredModel model = modelManager.getModelForEdit(filename, inStream, null);
			long endTime = System.currentTimeMillis();
			long thisTime = endTime - startTime;
			// don't include first, since is 5 times longer than usual
			// presumably due to class loading
			if (i > 0) {
				total = total + thisTime;
			}
			else {
				// just need to do this statistics gathering 
				// once, so will put here.
				Document document = ((XMLModel) model).getDocument();
				nodeCount = countNodes(document);
				// adapterCount = countAdapters(document);
				nFactories = countFactories(model);
				nPropagatingFactories = countPropatingFactories(document);
				nEmbeddedFactories = countEmbeddedFactories(document);
			}
			//System.out.println(i + ".  Time to create Model: " + thisTime);
			model.releaseFromEdit();
			inStream.close();
		}
		System.out.println();
		System.out.println("          Average Time to create model for " + filename + ": " + (total / (nTrials - 1)));
		System.out.println("          (used " + (nTrials - 1) + " trials)");
		System.out.println("          (N Nodes == " + nodeCount + ")");
		//        System.out.println("          (N Adapters == " + adapterCount + ")");
		System.out.println("          (N Factories == " + nFactories + ")");
		System.out.println("          (N PropagatingFactories == " + nPropagatingFactories + ")");
		System.out.println("          (N EmbeddedFactories == " + nEmbeddedFactories + ")");

		//assertTrue("model could not be created!", model != null);

	}

	/**
	 * Method countEmbeddedFactories.
	 * @param document
	 */
	private int countEmbeddedFactories(Document document) {
		int result = 0;
		if (document instanceof XMLDocument) {
			XMLDocument xmlDocument = (XMLDocument) document;
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) xmlDocument).getExistingAdapter(PageDirectiveAdapter.class);
			if (pageDirectiveAdapter != null) {
				EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
				if (embeddedHandler != null) {
					result = embeddedHandler.getAdapterFactories().size();
				}
			}
		}
		return result;
	}

	/**
	 * Method countPropatingFactories.
	 * @param document
	 * @return int
	 */
	private int countPropatingFactories(Document document) {
		int result = 0;
		if (document instanceof XMLDocument) {
			PropagatingAdapter pAdapter = (PropagatingAdapter) ((XMLDocument) document).getAdapterFor(PropagatingAdapter.class);
			result = pAdapter.getAdaptOnCreateFactories().size();
		}
		return result;
	}

	/**
	 * Method countFactories.
	 * @param model
	 * @return int
	 */
	private int countFactories(IStructuredModel model) {
		IFactoryRegistry reg = model.getFactoryRegistry();
		int result = reg.getFactories().size();
		return result;
	}
}