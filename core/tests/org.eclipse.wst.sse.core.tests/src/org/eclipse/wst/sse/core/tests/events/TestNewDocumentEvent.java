/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.core.tests.events;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class TestNewDocumentEvent extends TestCase {
	final private static String testString = "testing";

	public void testGetOffset() {
		NewDocumentEvent event = getBasicEvent();
		int o = event.getOffset();
		assertEquals(0, o);
	}

	public void testGetLength() {
		NewDocumentEvent event = getBasicEvent();
		int i = event.getLength();
		assertEquals(testString.length(), i);
	}

	public void testNewDocumentEvent() {
		NewDocumentEvent event = getBasicEvent();
		assertNotNull(event);
	}


	public void testGetText() {
		NewDocumentEvent event = getBasicEvent();
		String text = event.getText();
		assertEquals(testString, text);
	}

	private NewDocumentEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		document.setText(this, testString);
		NewDocumentEvent event = new NewDocumentEvent(document, this);
		return event;

	}
}
