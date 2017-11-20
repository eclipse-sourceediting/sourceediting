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
package org.eclipse.jst.jsp.ui.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerUtility;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;

/**
 * @author davidw
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public class TestModelEmbeddedContentType extends TestCase {

	/**
	 * Constructor for TestModelClone.
	 */
	public TestModelEmbeddedContentType(String name) {
		super(name);
	}

	public static void main(String[] args) {
	}

	public static Test getTest() {
		return new TestModelEmbeddedContentType("testStructuredModelEmbeddedJSPChange");
	}

	protected void checkEmbeddedType(IStructuredModel clonedModel, Object expectedType) {
		Document doc = ((IDOMModel) clonedModel).getDocument();
		PageDirectiveAdapter embeddedTypeAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getAdapterFor(PageDirectiveAdapter.class);
		assertNotNull("cloned model did not have embedded adapter", embeddedTypeAdapter);

		EmbeddedTypeHandler embeddedHandler = embeddedTypeAdapter.getEmbeddedType();
		assertNotNull("cloned model did not have embedded handler", embeddedHandler);

		assertTrue("cloned model embeddedHandler is wrong type", embeddedHandler.equals(expectedType));
	}

	public void testStructuredModelEmbeddedXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		assertTrue("model could not be created!", model != null);

		// XML should NOT have an embedded type
		Document doc = ((IDOMModel) model).getDocument();
		EmbeddedTypeHandler embeddedHandler = (EmbeddedTypeHandler) ((INodeNotifier) doc).getAdapterFor(EmbeddedTypeHandler.class);
		assertTrue("embededHanlder should be null for XML", embeddedHandler == null);

	}

	public void testStructuredModelEmbeddedHTML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		assertTrue("model could not be created!", model != null);

		// should NOT have an embedded type
		Document doc = ((IDOMModel) model).getDocument();
		EmbeddedTypeHandler embeddedHandler = (EmbeddedTypeHandler) ((INodeNotifier) doc).getAdapterFor(EmbeddedTypeHandler.class);
		assertTrue("embededHanlder should be null for HTML", embeddedHandler == null);

	}

	protected IStructuredModel doStructuredModelEmbeddedJSP() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		// should have an embedded type
		Document doc = ((IDOMModel) model).getDocument();
		PageDirectiveAdapter embeddedHandler = (PageDirectiveAdapter) ((INodeNotifier) doc).getAdapterFor(PageDirectiveAdapter.class);
		assertTrue("embededHanlder should NOT be null for JSP", embeddedHandler != null);

		// embedded type should be HTML since no content
		checkEmbeddedType(model, ModelHandlerUtility.getDefaultEmbeddedType());

		String testContent = "<%@ page contentType=\"text/html\" %>";
		model.getStructuredDocument().replaceText(this, 0, 0, testContent);
		assertTrue("reinit should NOT be needed in this case", !model.isReinitializationNeeded());

		// embedded type should STILL be HTML since no contentType was
		// text/html
		checkEmbeddedType(model, ModelHandlerUtility.getDefaultEmbeddedType());

		return model;
		//
	}

	public void testStructuredModelEmbeddedJSP() throws IOException {
		doStructuredModelEmbeddedJSP();
	}

	public void testStructuredModelEmbeddedJSPChange() throws IOException {

		// start with the full test case
		IStructuredModel model = doStructuredModelEmbeddedJSP();
		// change "html" to "xml"
		model.getStructuredDocument().replaceText(this, 27, 4, "xml");
		// with reinit in XML model ... its already been re-initialized
		// assertTrue("reinit SHOULD be needed in this case",
		// model.isReiniitializationNeeded());

		if (model.isReinitializationNeeded()) {
			model.reinit();
		}

		// embedded type should now be XML
		checkEmbeddedType(model, ModelHandlerUtility.getEmbeddedContentTypeFor("text/xml"));

	}
}
