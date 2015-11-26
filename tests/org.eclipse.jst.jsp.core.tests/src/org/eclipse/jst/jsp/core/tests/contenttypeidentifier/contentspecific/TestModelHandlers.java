/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;

public class TestModelHandlers extends TestCase {
	private static ModelHandlerRegistry getModelHandlerRegistry() {
		ModelHandlerRegistry registry = ModelHandlerRegistry.getInstance();
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
		String id = ContentTypeIdForCSS.ContentTypeID_CSS;  
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have CSS type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testCSSExistsFromFilename() throws IOException {
		String filename = "test.css";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have CSS type ", handler != null && handler.getAssociatedContentTypeId().equals(ContentTypeIdForCSS.ContentTypeID_CSS));
	}

	public void testDTDExists() {
		String id = "org.eclipse.wst.dtd.core.dtdsource"; 
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have DTD type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testDTDExistsFromFilename() throws IOException {
		String filename = "test.dtd";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have DTD type ", handler != null && handler.getAssociatedContentTypeId().equals("org.eclipse.wst.dtd.core.dtdsource"));
	}

	public void testHTMLExists() {
		String id = ContentTypeIdForHTML.ContentTypeID_HTML;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have HTML type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testHTMLExistsFromFilename() throws IOException {
		String filename = "test.html";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have HTML type ", handler != null && handler.getAssociatedContentTypeId().equals(ContentTypeIdForHTML.ContentTypeID_HTML));
	}

	public void testJSPExists() {
		String id = ContentTypeIdForJSP.ContentTypeID_JSP;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertTrue("model handler registry does not have JSP type ", handler != null && handler.getAssociatedContentTypeId().equals(id));
	}

	public void testJSPExistsFromFilename() throws IOException {
		String filename = "test.jsp";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertTrue("model handler registry does not have JSP type ", handler != null && handler.getAssociatedContentTypeId().equals(ContentTypeIdForJSP.ContentTypeID_JSP));
	}

	public void testXMLExists() {
		String id = ContentTypeIdForXML.ContentTypeID_XML;
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerForContentTypeId(id);
		assertEquals("model handler registry does not have XML type ", id, handler.getAssociatedContentTypeId());
	}

	public void testXMLExistsFromFilename() throws IOException {
		String filename = "test.xml";
		ModelHandlerRegistry registry = getModelHandlerRegistry();
		IModelHandler handler = registry.getHandlerFor(filename, null);
		assertEquals("model handler registry does not have XML type ", ContentTypeIdForXML.ContentTypeID_XML, handler.getAssociatedContentTypeId());
	}

	public void testDirtyStateForEmbeddedContentTypeTextHTML() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("html.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}

	public void testDirtyStateForEmbeddedContentTypeTextCSS() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("css.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}
	
	public void testDirtyStateForEmbeddedContentTypeTextXML() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("xml.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}

	public void testDirtyStateForMisspelledEmbeddedCharset() throws Exception {
		String projectName = "TestModelHandlers." + getName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		}
		IFile testFile = project.getFile("charaset.jsp");
		String contents = "<%@ page language=\"java\" contentType=\"text/xml; charaset=UTF-8\" pageEncoding=\"UTF-8\"%>\n" + 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<Contents>\n" + 
				"<Error state=\"fatal\" code=\"\">\n" + 
				"</Error>\n" + 
				"</Contents>";
		if (!testFile.exists()) {
			testFile.create(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		else {
			testFile.setContents(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(testFile);

			Document doc = ((IDOMModel) model).getDocument();
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getAdapterFor(PageDirectiveAdapter.class);
			EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();

			assertFalse("newly opened model was dirty, embedded handler changed? current family:" + embeddedHandler.getFamilyId(), model.isDirty());
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		project.delete(true, null);
	}
	
	public void testDirtyStateForEmbeddedCharsetWithoutContentType() throws Exception {
		String projectName = "TestModelHandlers." + getName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		}
		IFile testFile = project.getFile("charaset.jsp");
		String contents = "<%@ page language=\"java\" contentType=\"charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n" + 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<Contents>\n" + 
				"<Error state=\"fatal\" code=\"\">\n" + 
				"</Error>\n" + 
				"</Contents>";
		if (!testFile.exists()) {
			testFile.create(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		else {
			testFile.setContents(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(testFile);

			Document doc = ((IDOMModel) model).getDocument();
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getAdapterFor(PageDirectiveAdapter.class);
			EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();

			assertFalse("newly opened model was dirty, embedded handler changed? current family:" + embeddedHandler.getFamilyId(), model.isDirty());
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		project.delete(true, null);
	}

	public void testDirtyStateForUnsupportedEmbeddedContentType() throws Exception {
		String projectName = "TestModelHandlers." + getName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		}
		IFile testFile = project.getFile("unsupported.jsp");
		String contents = "<%@ page language=\"java\" contentType=\"image/gif\"%>\n" + 
		"out.write(\"GIF89a\"";
		if (!testFile.exists()) {
			testFile.create(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		else {
			testFile.setContents(new ByteArrayInputStream(contents.getBytes("utf8")), IResource.FORCE, null);
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(testFile);
			assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		project.delete(true, null);
	}

	public void testDirtyStateForEmbeddedContentTypeSubXML() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("rdf.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}

	public void testDirtyStateForDefaultEmbeddedContentType() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("default.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}

	public void testDirtyStateWithNoPageDirective() throws Exception {
		String name = "bug243243";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isAccessible()) {
			project = BundleResourceUtil.createSimpleProject(name, null, null);
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug243243", "/bug243243");
		}
		IFile testFile = project.getFile("nodirective.jsp");
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(testFile);
		assertFalse("newly opened model was dirty " + testFile.getName(), model.isDirty());
		model.releaseFromRead();
		project.delete(true, null);
	}
}
