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
package org.eclipse.jst.jsp.ui.tests.pagedirective;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.document.XMLModel;

/**
 * @author davidw
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestPageDirective extends TestCase {

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
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((XMLModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

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
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((XMLModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

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
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((XMLModel) model).getDocument().getAdapterFor(PageDirectiveAdapter.class);

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
		assertTrue("language should be java", "java".equals(language));
	}
}