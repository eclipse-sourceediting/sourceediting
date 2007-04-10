/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class TestModelRelease extends TestCase {

	
	
	public void testJSPModel() {
		IModelManager modelManager = getModelManager();
		IDOMModel structuredModel = (IDOMModel) modelManager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		boolean test = structuredModel.getId().equals(IModelManager.UNMANAGED_MODEL);
		assertTrue(test);
		// even though model is unmanaged, release still required, since 
		// adapter factories, etc., may be depending on it. 
		structuredModel.releaseFromEdit();
		// if above complete's normally (with no exceptions) 
		// consider passed. 
		assertTrue(true);
	}

	private IModelManager getModelManager() {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		return modelManager;
	}
}
