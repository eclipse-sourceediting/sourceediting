/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.tests.model;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;

/**
 * @deprecated - we don't have INodeAdapters directly on our models and this
 *             is not part of the usual test suite (test.xml)
 */
public class TestModelAdapters extends TestCase {



	public void testJSPModel() throws IOException {
		IModelManager modelManager = getModelManager();
		IDOMModel structuredModel = (IDOMModel) modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		try {
			boolean test = structuredModel.getId().equals(IModelManager.UNMANAGED_MODEL);
			assertTrue(test);
			ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter) structuredModel.getAdapter(ModelQueryAdapter.class);
			assertNotNull("initial modelQueryAdapter should not be null", modelQueryAdapter);
			IStructuredModel newModel = structuredModel.newInstance();
			// IDOMDocument newDocument = ((IDOMModel)
			// newModel).getDocument();
			// INodeNotifier notifier = newDocument;
			ModelQueryAdapter result = (ModelQueryAdapter) newModel.getAdapter(ModelQueryAdapter.class);
			assertNotNull("newInstance modelQueryAdapter should not be null", result);

		}
		finally {
			// even though model is unmanaged, release still required, since
			// adapter factories, etc., may be depending on it.
			structuredModel.releaseFromEdit();
			// if above complete's normally (with no exceptions)
			// consider passed.
			assertTrue(true);
		}
	}

	private IModelManager getModelManager() {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		return modelManager;
	}
}
