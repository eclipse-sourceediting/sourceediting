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
package org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;

public class TestModelHandlers extends TestCase {
	private static ModelHandlerRegistry getModelHandlerRegistry() {
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		ModelHandlerRegistry registry = plugin.getModelHandlerRegistry();
		return registry;
	}

	public TestModelHandlers() {
		super();
	}

	public void testCreation() {
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		assertTrue("model handler registry must exist", registry != null);
	}

	public void testCSSExists() {
		String id = IContentTypeIdentifier.ContentTypeID_CSS; //"com.ibm.sse.model.handler.css"; 
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have CSS type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testCSSExistsFromFilename() throws IOException {
		String filename = "test.css";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have CSS type ", handler != null && handler.getAssociatedContentTypeId().equals(IContentTypeIdentifier.ContentTypeID_CSS));
	}

	public void testDTDExists() {
		String id = IContentTypeIdentifier.ContentTypeID_DTD; // "com.ibm.sse.model.handler.dtd"; 
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have DTD type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testDTDExistsFromFilename() throws IOException {
		String filename = "test.dtd";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have DTD type ", handler != null && handler.getAssociatedContentTypeId().equals(IContentTypeIdentifier.ContentTypeID_DTD));
	}

	public void testHTMLExists() {
		String id = IContentTypeIdentifier.ContentTypeID_HTML;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have HTML type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testHTMLExistsFromFilename() throws IOException {
		String filename = "test.html";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have HTML type ", handler != null && handler.getAssociatedContentTypeId().equals(IContentTypeIdentifier.ContentTypeID_HTML));
	}

	public void testJSPExists() {
		String id = IContentTypeIdentifier.ContentTypeID_JSP;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have JSP type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testJSPExistsFromFilename() throws IOException {
		String filename = "test.jsp";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have JSP type ", handler != null && handler.getAssociatedContentTypeId().equals(IContentTypeIdentifier.ContentTypeID_JSP));
	}

	public void testXMLExists() {
		String id = IContentTypeIdentifier.ContentTypeID_SSEXML;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have XML type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testXMLExistsFromFilename() throws IOException {
		String filename = "test.xml";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have XML type ", handler != null && handler.getAssociatedContentTypeId().equals(IContentTypeIdentifier.ContentTypeID_SSEXML));
	}


}