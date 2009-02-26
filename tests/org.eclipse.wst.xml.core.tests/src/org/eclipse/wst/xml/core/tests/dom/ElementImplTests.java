/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;

public class ElementImplTests extends TestCase {

	private static final String contents = "<elementPrefix:localName attrPrefix:local='lorem' />"; //$NON-NLS-1$

	public ElementImplTests() {
	}

	public ElementImplTests(String name) {
		super(name);
	}

	public void testElementImplPrefix() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);
		
		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		assertEquals("attribute prefix was not as expected", "elementPrefix", documentElement.getPrefix());
	}

	public void testElementImplLocalName() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		assertEquals("attribute local name was not as expected", "localName", documentElement.getLocalName());
	}
}
