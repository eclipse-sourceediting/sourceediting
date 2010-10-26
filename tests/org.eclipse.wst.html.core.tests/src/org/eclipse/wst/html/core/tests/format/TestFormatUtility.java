/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.format;

import junit.framework.TestCase;

import org.eclipse.wst.html.core.internal.format.HTMLFormattingUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class TestFormatUtility extends TestCase {

	public void testInlineDefaults() {
		Object[] defaultElements = HTMLFormattingUtil.getDefaultInlineElements();
		assertNotNull("Default elements cannot be null", defaultElements);
		assertTrue("Default element list has no elements", defaultElements.length > 0);

		Object[] elements = HTMLFormattingUtil.getInlineElements();
		assertNotNull("Inline elements cannot be null", elements);
		assertTrue("Inline elements size does not match the default", elements.length == defaultElements.length);

		for (int i = 0; i < elements.length; i++) {
			assertEquals("Element does not match up with default list", defaultElements[i], elements[i]);
		}
	}

	public void testInlineNode() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		IDOMDocument document = model.getDocument();

		HTMLFormattingUtil util = new HTMLFormattingUtil();
		assertTrue("Anchor tag must be inline", util.isInline(document.createElement("a")));

		assertFalse("Div tag must not be inline", util.isInline(document.createElement("div")));
		
	}

	public void testExportElements() {
		String[] newElements = { "foo" };
		HTMLFormattingUtil.exportToPreferences(newElements);

		Object[] elements = HTMLFormattingUtil.getInlineElements();
		assertNotNull("Inline elements cannot be null", elements);
		assertEquals("Inline count is wrong", newElements.length, elements.length);
		for (int i = 0; i < newElements.length; i++) {
			assertEquals("Element names do not match up", newElements[i], elements[i]);
		}

		HTMLFormattingUtil.exportToPreferences(HTMLFormattingUtil.getDefaultInlineElements());
	}

}
