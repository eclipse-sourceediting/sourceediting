/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.core.tests.events;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.provisional.events.AboutToBeChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class TestAboutToBeChangedEvent extends TestCase {

	public void testAboutToBeChangedEvent() {
		AboutToBeChangedEvent event = getBasicEvent();
		assertNotNull(event);
	}

	public void testGetDeletedText() {
		AboutToBeChangedEvent event = getBasicEvent();
		String d = event.getDeletedText();
		assertEquals(d, null);
	}

	public void testGetOriginalRequester() {
		AboutToBeChangedEvent event = getBasicEvent();
		Object r = event.getOriginalRequester();
		assertEquals(this, r);
	}

	public void testGetStructuredDocument() {
		AboutToBeChangedEvent event = getBasicEvent();
		IStructuredDocument d = event.getStructuredDocument();
		assertNotNull(d);
	}

	public void testSetDeletedText() {
		AboutToBeChangedEvent event = getBasicEvent();
		event.setDeletedText("junkyNothing");
		// assume ok if no exception
		assertTrue(true);
	}

	private AboutToBeChangedEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		AboutToBeChangedEvent event = new AboutToBeChangedEvent(document, this, "test", 0, 0);
		return event;

	}

}
