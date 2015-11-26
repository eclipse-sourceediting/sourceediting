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
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class TestRegionsReplacedEvent extends TestCase {

	final private static String testString = "testing";

	public void testRegionsReplacedEvent() {
		RegionsReplacedEvent event = getBasicEvent();
		assertNotNull(event);
	}

	public void testGetNewRegions() {
		RegionsReplacedEvent event = getBasicEvent();
		assertNull(event.getNewRegions());
	}

	public void testGetOldRegions() {
		RegionsReplacedEvent event = getBasicEvent();
		assertNull(event.getOldRegions());
	}

	public void testGetStructuredDocumentRegion() {
		RegionsReplacedEvent event = getBasicEvent();
		assertNull(event.getStructuredDocumentRegion());
	}

	private RegionsReplacedEvent getBasicEvent() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(new NullParser());
		document.setText(this, testString);
		RegionsReplacedEvent event = new RegionsReplacedEvent(document, this, null, null, null, "", 0, 0);
		return event;
	}
}
