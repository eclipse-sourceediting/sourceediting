/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.pagedirective;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.modelhandler.EmbeddedXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Node;

/**
 * @author davidw
 */
public class TestPageDirective extends TestCase {

	class MyEmbeddedFactory implements INodeAdapterFactory {

		public INodeAdapter adapt(INodeNotifier object) {
			return null;
		}

		public INodeAdapterFactory copy() {
			return null;
		}

		public boolean isFactoryForType(Object type) {
			return type instanceof MyAdaptedClass;
		}

		public void release() {
			// noop
		}
	}
	
	class MyAdaptedClass {
		// dummy class is key
	}
	
	/**
	 * Constructor for TestPageDirective.
	 * @param name
	 */
	public TestPageDirective(String name) {
		super(name);
	}

	public void testBasicPD() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%@ page contentType=\"text/html\" language=\"java\" %>");
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((IDOMModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

		String contentType = pageDirectiveAdapter.getContentType();
		String language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertTrue("language should be java", "java".equals(language));

	}

	public void testBasicChangedPD() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%@ page contentType=\"text/html\" language=\"java\" %>");
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((IDOMModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

		String contentType = pageDirectiveAdapter.getContentType();
		String language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertTrue("language should be java", "java".equals(language));

		// change to javascript
		model.getStructuredDocument().replaceText(this, 43, 4, "javascript");

		contentType = pageDirectiveAdapter.getContentType();
		language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertTrue("language should be javascript", "javascript".equals(language));

	}

	public void testBasicChangedPDBack() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%@ page contentType=\"text/html\" language=\"java\" %>");
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((IDOMModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

		String contentType = pageDirectiveAdapter.getContentType();
		String language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertTrue("language should be java", "java".equals(language));

		// change to javascript
		model.getStructuredDocument().replaceText(this, 43, 4, "javascript");

		contentType = pageDirectiveAdapter.getContentType();
		language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertTrue("language should be javascript", "javascript".equals(language));

		// change back to java
		model.getStructuredDocument().replaceText(this, 43, 10, "java");

		contentType = pageDirectiveAdapter.getContentType();
		language = pageDirectiveAdapter.getLanguage();

		assertTrue("contentType should be html", "text/html".equals(contentType));
		assertEquals("java", language);
	}
	
	public void testSetEmbeddedType(){
		IStructuredModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((IDOMModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);
		EmbeddedTypeHandler embeddedXMLHandler = new EmbeddedXML();
		pageDirectiveAdapter.setEmbeddedType(embeddedXMLHandler);
		
		EmbeddedTypeHandler handler = pageDirectiveAdapter.getEmbeddedType();
		assertTrue("incorrect embedded handler", handler == embeddedXMLHandler);
	}
	
	public void testAdapt() {
		IDOMModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) model.getDocument().getAdapterFor(PageDirectiveAdapter.class);
		Node ownerNode = model.getDocument();
		
		ModelQueryAdapter embeddedAdapter = (ModelQueryAdapter) pageDirectiveAdapter.adapt((INodeNotifier) ownerNode, ModelQueryAdapter.class);
		assertNotNull("could not adapt embedded adapter", embeddedAdapter);
	}
	
	public void testAddEmbeddedFactory() {
		IDOMModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) model.getDocument().getAdapterFor(PageDirectiveAdapter.class);
		pageDirectiveAdapter.addEmbeddedFactory(new MyEmbeddedFactory());
	}
	
	public void testSetLanguage() {
		IDOMModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) model.getDocument().getAdapterFor(PageDirectiveAdapter.class);
		pageDirectiveAdapter.setLanguage("text/xml");
		assertTrue("set language failed", pageDirectiveAdapter.getLanguage().equals("text/xml"));
	}
	
	public void testGetTarget() {
		IDOMModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) model.getDocument().getAdapterFor(PageDirectiveAdapter.class);
		INodeNotifier notifier = pageDirectiveAdapter.getTarget();
		assertNotNull("target is null", notifier);
	}
	
	public void testRelease() {
		IDOMModel model = createUnmanagedHTMLModel();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) model.getDocument().getAdapterFor(PageDirectiveAdapter.class);
		pageDirectiveAdapter.release();
		// considered "success" if doesn't through exception
		assertTrue(true);
	}
	
	private IDOMModel createUnmanagedHTMLModel() {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%@ page contentType=\"text/html\" language=\"java\" %>");
		return (IDOMModel)model;
	}
}
