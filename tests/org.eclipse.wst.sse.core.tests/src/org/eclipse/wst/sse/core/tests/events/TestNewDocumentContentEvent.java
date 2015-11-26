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
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentContentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class TestNewDocumentContentEvent extends TestCase {
	final private static String testString = "testing";

	public void testNewDocumentContentEvent() {
		NewDocumentContentEvent event = getBasicEvent();
		assertNotNull(event);
	}

	public void testGetOffset() {
		NewDocumentContentEvent event = getBasicEvent();
		int o = event.getOffset();
		assertEquals(0, o);
	}

	public void testGetLength() {
		NewDocumentContentEvent event = getBasicEvent();
		int i = event.getLength();
		assertEquals(testString.length(), i);
	}

	/*
	 * Class under test for String getText()
	 */
	public void testGetText() {
		NewDocumentContentEvent event = getBasicEvent();
		String text = event.getText();
		assertEquals(testString, text);
	}

	private NewDocumentContentEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		document.setText(this, testString);
		NewDocumentContentEvent event = new NewDocumentContentEvent(document, this);
		return event;

	}
}
