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
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class TestRegionChangedEvent extends TestCase {
	final private static String testString = "testing";

	public void testRegionChangedEvent() {
		RegionChangedEvent event = getBasicEvent();
		assertNotNull(event);
	}

	public void testGetRegion() {
		RegionChangedEvent event = getBasicEvent();
		IStructuredDocumentRegion structuredDocumentRegion = event.getStructuredDocumentRegion();
		assertEquals(null, structuredDocumentRegion);
	}

	public void testGetStructuredDocumentRegion() {
		RegionChangedEvent event = getBasicEvent();
		ITextRegion region = event.getRegion();
		assertEquals(null, region);
	}

	private RegionChangedEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		document.setText(this, testString);
		RegionChangedEvent event = new RegionChangedEvent(document, this, null, null, "", 0, 0);
		return event;
	}
}
