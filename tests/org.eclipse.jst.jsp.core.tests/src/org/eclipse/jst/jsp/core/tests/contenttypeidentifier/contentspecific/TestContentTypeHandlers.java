/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;

/**
 * This class is to test very basics of content type handlers.
 * 
 * It tests that
 * 
 * BVT: content registry can be / is created 5 expected contentTypeIdentifiers
 * can be created/found based on id. there is one and only one default content
 * type handler.
 * 
 * 
 * 
 *  
 */
public class TestContentTypeHandlers extends TestCase {
	private static final boolean DEBUG = false;

	public TestContentTypeHandlers(String name) {
		super(name);
	}

	private static IContentTypeManager getContentTypeRegistry() {
		IContentTypeManager registry = Platform.getContentTypeManager();
		return registry;
	}

	public void testCreation() {
		IContentTypeManager registry = getContentTypeRegistry();
		assertTrue("content type identifer registry must exist", registry != null);
		if (DEBUG) {
			IContentType[] allTypes = registry.getAllContentTypes();
			for (int i = 0; i < allTypes.length; i++) {
				System.out.println(allTypes[i]);

			}
		}
	}

	public void testXMLExists() {
		String id = ContentTypeIdForXML.ContentTypeID_SSEXML;
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getContentType(id);
		assertTrue("content type identifier " + id + " does not have custom XML type ", identifier != null);
	}

	public void testHTMLExists() {
		String id = ContentTypeIdForHTML.ContentTypeID_HTML;
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getContentType(id);
		assertTrue("content type identifier " + id + " does not have HTML type ", identifier != null);
	}

	public void testJSPExists() {
		String id = ContentTypeIdForJSP.ContentTypeID_JSP;
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getContentType(id);
		assertTrue("content type identifier " + id + " does not have JSP type ", identifier != null);
	}

	public void testCSSExists() {
		String id = ContentTypeIdForCSS.ContentTypeID_CSS;
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getContentType(id);
		assertTrue("content type identifier " + id + " does not have CSS type ", identifier != null);
	}

	public void testDTDExists() {
		String id = "org.eclipse.wst.dtd.core.dtdsource";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getContentType(id);
		assertTrue("content type identifier " + id + " does not have DTD type ", identifier != null);
	}

	public void testXMLExistsByFileExtension() throws IOException {
		String filename = "test.xml";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("content type identifier for " + filename + " does not have XML type ", identifier != null);
	}

	public void testHTMLExistsByFileExtension() throws IOException {
		String filename = "test.html";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("content type identifier for " + filename + " does not have HTML type ", identifier != null);
	}

	public void testJSPExistsByFileExtension() throws IOException {
		String filename = "test.jsp";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("content type identifier for " + filename + " does not have JSP type ", identifier != null);
	}

	public void testCSSExistsByFileExtension() throws IOException {
		String filename = "test.css";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("content type identifier for " + filename + " does not have CSS type ", identifier != null);
	}

	public void testDTDExistsByFileExtension() throws IOException {
		String filename = "test.dtd";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("content type identifier for " + filename + " does not have DTD type ", identifier != null);
	}

	public void testMultipleDefinitions() throws IOException {
		String id = ContentTypeIdForCSS.ContentTypeID_CSS;
		String filename = "test.css";
		IContentTypeManager registry = getContentTypeRegistry();
		IContentType identifier1 = registry.getContentType(id);
		IContentType identifier2 = registry.getDescriptionFor(new NullStream(), filename, IContentDescription.ALL).getContentType();
		assertTrue("mulitple content type identifiers need to be equal (but not same instance) ", identifier1.equals(identifier2));
	}

}
