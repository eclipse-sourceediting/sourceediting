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
package org.eclipse.jst.jsp.core.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.html.core.modelhandler.EmbeddedHTML;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.modelhandler.EmbeddedXML;
import org.w3c.dom.Document;

/**
 * 
 */
public class TestModelClone extends TestCase {

	/**
	 * Constructor for TestModelClone.
	 */
	public TestModelClone(String name) {
		super(name);
	}

	public static void main(String[] args) {
	}

	public static Test getTest() {
		return new TestModelClone("testCreateStructuredModelJSP");
	}

	public void testCloneStructuredModelXML() throws IOException {
		// First make (empty) structuredDocument
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_SSEXML);
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
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_HTML);
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
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_JSP);
		assertTrue("model could not be created!", model != null);

		IStructuredModel clonedModel = model.newInstance();
		assertTrue("cloned model could not be created!", clonedModel != null);
		// make sure the embedded type is correct
		boolean passed = true;
		Document doc = ((XMLModel) clonedModel).getDocument();
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
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_JSP);
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
		Document doc = ((XMLModel) model).getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getExistingAdapter(PageDirectiveAdapter.class);
		assertNotNull("model did not have embedded adapter", pageDirectiveAdapter);

		EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
		assertNotNull("model did not have embedded handler", embeddedHandler);

		assertTrue("embeddedHandler is wrong type", embeddedHandler.getClass().equals(expectedType));

	}

	public void testCreateStructuredModelJSP() throws IOException {
		// First make (empty) structuredDocument
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_JSP);
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
		Document doc = ((XMLModel) model).getDocument();
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
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		IStructuredModel model = modelManager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_HTML);
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


}