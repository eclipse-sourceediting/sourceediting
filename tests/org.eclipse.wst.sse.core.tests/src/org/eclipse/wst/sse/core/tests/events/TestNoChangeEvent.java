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
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class TestNoChangeEvent extends TestCase {
	final private static String testString = "testing";

	public void testNoChangeEvent() {
		NoChangeEvent event = getBasicEvent();
		assertNotNull(event);
		assertEquals(NoChangeEvent.NO_CONTENT_CHANGE, event.reason);
	}

	private NoChangeEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		document.setText(this, testString);
		NoChangeEvent event = new NoChangeEvent(document, this, "", 0, 0);
		event.reason = NoChangeEvent.NO_CONTENT_CHANGE;
		return event;
	}

}
