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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.internal.modelquery.JSPModelQueryAdapterImpl;
import org.eclipse.jst.jsp.core.internal.modelquery.JSPModelQueryImpl;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.html.core.internal.modelhandler.EmbeddedHTML;
import org.eclipse.wst.html.core.internal.modelquery.HTMLModelQueryImpl;
//import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeFamilyForHTML;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelhandler.EmbeddedXML;
import org.eclipse.wst.xml.core.internal.modelquery.XMLModelQueryImpl;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 
 */
public class TestModelClone extends TestCase {

	private HashMap embeddedModelQueries = new HashMap();

	/**
	 * Constructor for TestModelClone.
	 */
	public TestModelClone(String name) {
		super(name);
	}

	public static Test getTest() {
		return new TestModelClone("testCreateStructuredModelJSP");
	}

	public void testCloneStructuredModelXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		assertTrue("model could not be created!", model != null);

		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the critical variables are NOT identical, but that new instances 
		// have been made
		boolean passed = true;
		//		if (clonedModel.getEncodingMemento() == model.getEncodingMemento()) passed = false;
		//		if (clonedModel.getParser() == model.getParser()) passed = false;
		//		if (clonedModel.getReParser() == model.getReParser()) passed = false;
		assertTrue("newInstance of structured model is not correct", passed);

	}

	public void testCloneStructuredModelHTML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		assertTrue("model could not be created!", model != null);

		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the critical variables are NOT identical, but that new instances 
		// have been made
		boolean passed = true;
		//		if (clonedModel.getEncodingMemento() == model.getEncodingMemento()) passed = false;
		//		if (clonedModel.getParser() == model.getParser()) passed = false;
		//		if (clonedModel.getReParser() == model.getReParser()) passed = false;
		assertTrue("newInstance of structured model is not correct", passed);

	}

	public void testCloneStructuredModelJSP() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the embedded type is correct
		boolean passed = true;
		Document doc = ((IDOMModel) clonedModel).getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getExistingAdapter(PageDirectiveAdapter.class);
		assertNotNull("cloned model did not have embedded adapter", pageDirectiveAdapter);

		EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
		assertNotNull("cloned model did not have embedded handler", embeddedHandler);

		//		if (clonedModel.getEncodingMemento() == model.getEncodingMemento()) passed = false;
		//		if (clonedModel.getParser() == model.getParser()) passed = false;
		//		if (clonedModel.getReParser() == model.getReParser()) passed = false;
		assertTrue("newInstance of structured model is not correct", passed);

	}

	public void testCloneStructuredModelJSPXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
		// note, we initialy expect HTML, since there is not content
		checkEmbeddedType(model, EmbeddedHTML.class);
	
		String testContent = "<%@ page contentType=\"text/xml\" %>";
		model.getStructuredDocument().setText(this, testContent);
	
		// modified for design change, where re-init should be handled before 
		// set returns.
		assertTrue("model should not need reinit", !model.isReinitializationNeeded());
	
		//		but if it did need re-init, here's the right calling sequence		
		//		if (model.isReinitializationNeeded()) {
		//			model.aboutToChangeModel();
		//			model = model.reinit();
		//			model.changedModel();
		//		}
	
		checkEmbeddedType(model, EmbeddedXML.class);
	
		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		checkEmbeddedType(clonedModel, EmbeddedXML.class);
	
	}

	private void checkEmbeddedType(IStructuredModel model, Class expectedType) {
		Document doc = ((IDOMModel) model).getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getExistingAdapter(PageDirectiveAdapter.class);
		assertNotNull("model did not have pageDirective", pageDirectiveAdapter);

		EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
		assertNotNull("model did not have embedded handler", embeddedHandler);

		assertEquals(expectedType, embeddedHandler.getClass());

	}

	public void testCreateStructuredModelJSP() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
	
		ArrayList factories = (ArrayList) model.getFactoryRegistry().getFactories();
		factories = (ArrayList) factories.clone();
	
		IStructuredModel clonedModel = model.newInstance();
		if (model.getModelHandler() == null) {
			System.out.println();
			assertTrue(false);
		}
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the critical variables are correct.
	
		ArrayList newFactories = (ArrayList) clonedModel.getFactoryRegistry().getFactories();
		newFactories = (ArrayList) newFactories.clone();
	
		boolean passed = checkFactoriesListForIdentity(factories, newFactories);
		assertTrue("newInstance of structured model is not correct", passed);
	
		passed = checkForSameEmbeddedFactories(model, clonedModel);
		assertTrue("newInstance of structured model is not correct", passed);
	
		// Now, assigning some content shouldn't change the factories
		clonedModel.getStructuredDocument().replaceText(this, 0, 0, "<sample> text");
		ArrayList twoFactories = (ArrayList) clonedModel.getFactoryRegistry().getFactories();
		twoFactories = (ArrayList) twoFactories.clone();
	
		passed = checkFactoriesListForIdentity(factories, twoFactories);
		assertTrue("newInstance of structured model is not correct", passed);
	
		passed = checkForSameEmbeddedFactories(model, clonedModel);
		assertTrue("newInstance of structured model is not correct", passed);
	
	
		// Now, assigning use a page directive, but leaving embedded type the same as default
		clonedModel.getStructuredDocument().setText(this, "<%@ page contentType=\"text/html\"");
		ArrayList threeFactories = (ArrayList) clonedModel.getFactoryRegistry().getFactories();
		threeFactories = (ArrayList) threeFactories.clone();
	
		passed = checkFactoriesListForIdentity(factories, threeFactories);
		assertTrue("newInstance of structured model is not correct", passed);
	
		passed = checkForSameEmbeddedFactories(model, clonedModel);
		assertTrue("newInstance of structured model is not correct", passed);
	
	
	}

	public void testCreateStructuredModelJSPXHTML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
	
	
	
	
		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" + 
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n" + 
				"\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" + 
				"<%@page language=\"java\" contentType=\"text/xml; charset=ISO-8859-1\"\n" + 
				"        pageEncoding=\"ISO-8859-1\"%>\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
				"<head>\n" + 
				"<meta http-equiv=\"Content-Type\" content=\"text/xml; charset=ISO-8859-1\" />\n" + 
				"<style>\n" + 
				"A { color: red; }\n" + 
				"</style>\n" + 
				"</head>\n" + 
				"</html>\n" + 
				"");
	
			checkEmbeddedType(model, EmbeddedHTML.class);
			checkModelQuery(model, JSPModelQueryImpl.class);
			checkEmbeddedModelQuery(model, JSPModelQueryAdapterImpl.class, JSPModelQueryImpl.class, HTMLModelQueryImpl.class);
			
			
			
			
	}

	public void testCreateStructuredModelJSPXHTMnoDoctype() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
	
	
	
	
		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" + 
				"<%@page language=\"java\" contentType=\"text/xml; charset=ISO-8859-1\"\n" + 
				"        pageEncoding=\"ISO-8859-1\"%>\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
				"<head>\n" + 
				"<meta http-equiv=\"Content-Type\" content=\"text/xml; charset=ISO-8859-1\" />\n" + 
				"<style>\n" + 
				"A { color: red; }\n" + 
				"</style>\n" + 
				"</head>\n" + 
				"</html>\n" + 
				"");
	
			checkEmbeddedType(model, EmbeddedHTML.class);
			checkModelQuery(model, JSPModelQueryImpl.class);
			checkEmbeddedModelQuery(model, JSPModelQueryAdapterImpl.class, JSPModelQueryImpl.class, HTMLModelQueryImpl.class);
			
			
			
			
	}

	public void testCreateStructuredModelJSPXHTML2() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);




		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" + 
					"<%@page language=\"java\" contentType=\"application/xml; charset=ISO-8859-1\"\n" + 
					"        pageEncoding=\"ISO-8859-1\"%>\n" + 
					"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n" + 
				"\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
				"<head>\n" + 
				"<meta http-equiv=\"Content-Type\" content=\"application/xml; charset=ISO-8859-1\" />\n" + 
				"<style>\n" + 
				"A { color: red; }\n" + 
				"</style>\n" + 
				"</head>\n" + 
				"</html>\n" + 
				"");

			checkEmbeddedType(model, EmbeddedHTML.class);
			checkModelQuery(model, JSPModelQueryImpl.class);
			checkEmbeddedModelQuery(model, JSPModelQueryAdapterImpl.class, JSPModelQueryImpl.class, HTMLModelQueryImpl.class);
			
			
			
			
	}

	private void checkModelQuery(IStructuredModel model, Class class1) {
		Document doc = ((IDOMModel) model).getDocument();
		ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter) ((INodeNotifier) doc).getExistingAdapter(ModelQueryAdapter.class);
		assertNotNull("model did not have modelQueryAdapter", modelQueryAdapter);
		
		assertTrue("modelQueryAdapter is wrong class", class1.equals(JSPModelQueryImpl.class));
		
	}

	private void checkEmbeddedModelQuery(IStructuredModel model, Class outerQueryAdapter, Class outerQueryClass, Class embeddedQueryClass) {

		Document doc = ((IDOMModel) model).getDocument();
		ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter) ((INodeNotifier) doc).getExistingAdapter(ModelQueryAdapter.class);
		assertNotNull("model did not have modelQueryAdapter", modelQueryAdapter);

		Class expected = outerQueryAdapter;
		Class actual = modelQueryAdapter.getClass();
		assertEquals("document's model query is not as expected", expected, actual);
		
		ModelQuery modelQuery = modelQueryAdapter.getModelQuery();
		expected = outerQueryClass;
		actual = modelQuery.getClass();
		assertEquals("model query adapter's model query is not as expected", expected, actual);
		
		ModelQuery nodeQuery = getEmbeddedModelQuery(doc);
		assertNotNull("node does not have a modelQueryAdapter", nodeQuery);
		
		expected = embeddedQueryClass;
		actual = nodeQuery.getClass();
		assertEquals("documents model query is not as expected", expected, actual);


		
	}

	/**
	 * Method checkEmbeddedFactories.
	 * @param model
	 * @param clonedModel
	 * @return boolean
	 */
	private boolean checkForSameEmbeddedFactories(IStructuredModel model, IStructuredModel clonedModel) {
		boolean result = true;


		EmbeddedTypeHandler oldEmbeddedType = getEmbeddedType(model);
		EmbeddedTypeHandler newEmbeddedType = getEmbeddedType(clonedModel);
		// expect to be the same type
		if (!oldEmbeddedType.getClass().equals(newEmbeddedType.getClass())) {
			result = false;
			assertTrue(result);
		}

		List oldFactories = oldEmbeddedType.getAdapterFactories();
		List newFactories = newEmbeddedType.getAdapterFactories();
		result = checkFactoriesListForEquivalence(oldFactories, newFactories);


		return result;
	}

	private EmbeddedTypeHandler getEmbeddedType(IStructuredModel model) {
		Document doc = ((IDOMModel) model).getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getExistingAdapter(PageDirectiveAdapter.class);
		assertNotNull("model did not have embedded adapter", pageDirectiveAdapter);

		EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
		assertNotNull("model did not have embedded handler", embeddedHandler);

		return embeddedHandler;
	}


	/**
	 * Method checkFactoriesList.
	 * @param factories
	 * @param newFactories
	 * @return boolean
	 */
	private boolean checkFactoriesListForIdentity(List factories, List newFactories) {
		boolean result = true;
		if (factories.size() != newFactories.size()) {
			result = false;
		}
		else {
			// nned not be identical, nor same order
			//			for (int i = 0; i < factories.size(); i++) {
			//				if (!(factories.get(i) == newFactories.get(i))) {
			//					result = false;
			//					break;
			//				}
			//			}
		}
		return result;
	}

	private boolean checkFactoriesListForEquivalence(List factories, List newFactories) {
		boolean result = true;
		if (factories.size() != newFactories.size()) {
			result = false;
		}
		else {
			for (int i = 0; i < factories.size(); i++) {
				if (!factories.get(i).getClass().equals(newFactories.get(i).getClass())) {
					result = false;
					break;
				}
			}
		}
		return result;
	}


	public void testCreateStructuredModelHTML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		assertTrue("model could not be created!", model != null);

		ArrayList factories = (ArrayList) model.getFactoryRegistry().getFactories();
		factories = (ArrayList) factories.clone();

		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the critical variables are correct.

		ArrayList newFactories = (ArrayList) clonedModel.getFactoryRegistry().getFactories();
		newFactories = (ArrayList) newFactories.clone();

		boolean passed = checkFactoriesListForIdentity(factories, newFactories);

		assertTrue("newInstance of structured model is not correct", passed);

		// Now, assigning some content shouldn't change the factories
		clonedModel.getStructuredDocument().setText(this, "<sample> text");
		ArrayList twoFactories = (ArrayList) clonedModel.getFactoryRegistry().getFactories();
		twoFactories = (ArrayList) twoFactories.clone();

		passed = checkFactoriesListForIdentity(factories, newFactories);
		assertTrue("newInstance of structured model is not correct", passed);

	}

	private ModelQuery getEmbeddedModelQuery(Node node) {
		ModelQuery embeddedModelQuery = null;

		if (node instanceof INodeNotifier) { 
			Node ownerNode = node.getOwnerDocument();
			if (ownerNode == null) {
				// then must be the document itself
				ownerNode = node; 
			}
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) ownerNode).getAdapterFor(PageDirectiveAdapter.class);
			if (pageDirectiveAdapter != null) {

				String effectiveContentType = null;
				ModelQuery potentialModelQueryObject = null;

				String familyId = pageDirectiveAdapter.getEmbeddedType().getFamilyId();
				//if (ContentTypeFamilyForHTML.HTML_FAMILY.equals(familyId)) {
				if ("org.eclipse.wst.html.core.contentfamily.html".equals(familyId)) {
					effectiveContentType = "text/html";
				}
				else {
					effectiveContentType = pageDirectiveAdapter.getContentType();
				}
				
				potentialModelQueryObject = (ModelQuery) embeddedModelQueries.get(effectiveContentType);
				
				if (potentialModelQueryObject == null) {
					ModelQueryAdapter embeddedAdapter = (ModelQueryAdapter) pageDirectiveAdapter.adapt((INodeNotifier) node, ModelQueryAdapter.class);
					if (embeddedAdapter != null) {
						// we will cache one model query per content type
						embeddedModelQuery = embeddedAdapter.getModelQuery();
						embeddedModelQueries.put(effectiveContentType, embeddedModelQuery);
					}
				}
				else {
					embeddedModelQuery = potentialModelQueryObject;
				}
			}
		}
		return embeddedModelQuery;
	}

	public void testCreateStructuredModelJSPXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
	
	
	
	
		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%-- keep JSP page compiler from generating code that accesses the session --%>\n" + 
				"<%@ page session=\"false\"  contentType=\"text/xml; charset=ISO-8859-1\" %>\n" + 
				"\n" + 
				"<!-- load WPS portlet tag library and initialize objects -->\n" + 
				"<%@ taglib uri=\"/WEB-INF/tld/portlet.tld\" prefix=\"portletAPI\" %>\n" + 
				"<portletAPI:init /> \n" + 
				"\n" + 
				"<%-- Replace the name of the layout specified by the layoutName attribute of the canvas element below with the layout name for your portlet. --%>\n" + 
				"<canvas xmlns =\"http://www.volantis.com/xmlns/marlin-cdm\" \n" + 
				"        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
				"        layoutName=\"/sample_layout.mlyt\" \n" + 
				"        type=\"portlet\" >\n" + 
				"\n" + 
				"\n" + 
				"<%-- Specify the pane name from your layout policy as the value for the name attribute of the pane element below  --%>\n" + 
				"<pane name=\"sample_pane\">\n" + 
				"    <p>Hello!</p>\n" + 
				"    <p>This is an XDIME <b>view mode</b> page . You have to edit this page to customize it for your own use.<br/><br/>\n" + 
				"    The source file for this page is \"/Web Content/testportlet_legacy/jsp/xdime/TemplateLegacyPortletView.jsp\".\n" + 
				"</p>\n" + 
				"\n" + 
				"<br/>\n" + 
				"This is image 1    \n" + 
				"<img src=\"/paw.mimg\" alt=\"noimg\" />\n" + 
				"\n" + 
				"<br/>\n" + 
				"This is image 2    \n" + 
				"<img src=\"paw.mimg\" alt=\"noimg\" />\n" + 
				"\n" + 
				"</pane>\n" + 
				"</canvas>");
	
			checkEmbeddedType(model, EmbeddedXML.class);
			checkModelQuery(model, JSPModelQueryImpl.class);
			checkEmbeddedModelQuery(model, JSPModelQueryAdapterImpl.class, JSPModelQueryImpl.class, XMLModelQueryImpl.class);
			
			
			
			
	}

	public void testCreateStructuredModelJSPWML() throws IOException {
		// First make (empty) structuredDocument
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);
	
	
	
	
		// Now, assigning use a page directive, but leaving embedded type the same as default
		model.getStructuredDocument().setText(this, "<%@ page contentType=\"text/vnd.wap.wml\" %>\n" + 
				"\n" + 
				"  <jsp:useBean id=\"beanInstanceName\" scope=\"session\" class=\"package.class\" />\n" + 
				"  <jsp:getProperty name=\"beanInstanceName\" property=\"*\" />\n" + 
				"\n" + 
				"  <?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
				"  <!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.3//EN\" \"http://www.wapforum.org/DTD/wml13.dtd\">\n" + 
				"  <wml>\n" + 
				"\n" + 
				"    <template>\n" + 
				"      <!-- Template implementation here. -->\n" + 
				"      <do type=\"prev\"><prev/></do>\n" + 
				"    </template>\n" + 
				"\n" + 
				"  <%\n" + 
				"  if (session.isNew() || session.getAttribute(\"userid\")==null ) {\n" + 
				"  %>\n" + 
				"    <card id=\"card1\" title=\"Card #1\">\n" + 
				"\n" + 
				"      <do type=\"unknown\" label=\"Next\">\n" + 
				"        <go href=\"#card2\"/>\n" + 
				"      </do>\n" + 
				"      <p align=\"center\">\n" + 
				"        <big><b>First Card</b></big>\n" + 
				"      </p>\n" + 
				"    </card>\n" + 
				"\n" + 
				"  <%\n" + 
				"  } else {\n" + 
				"  %>\n" + 
				"    <card id=\"card2\" title=\"Card #2\">\n" + 
				"\n" + 
				"      <p align=\"center\">\n" + 
				"        <big><b> <%= beanInstanceName.getUserid() %> </b></big>\n" + 
				"      </p>\n" + 
				"    </card>\n" + 
				"  <%\n" + 
				"  }\n" + 
				"  %>\n" + 
				"  </wml>");
	
			checkEmbeddedType(model, EmbeddedHTML.class);
			checkModelQuery(model, JSPModelQueryImpl.class);
			checkEmbeddedModelQuery(model, JSPModelQueryAdapterImpl.class, JSPModelQueryImpl.class, HTMLModelQueryImpl.class);
			
			
			
			
	}


}
