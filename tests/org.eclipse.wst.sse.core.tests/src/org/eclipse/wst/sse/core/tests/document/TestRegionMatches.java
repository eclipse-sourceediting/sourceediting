/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.document;

import org.eclipse.wst.sse.core.internal.text.StructuredDocumentTextStore;

import junit.framework.TestCase;



public class TestRegionMatches extends TestCase {

	/**
	 *  
	 */
	public TestRegionMatches() {
		super();

	}

	/**
	 * @param name
	 */
	public TestRegionMatches(String name) {
		super(name);

	}

	public void testRegionMatches1() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		structuredDocumentTextStore.set("testing123");
		boolean result = structuredDocumentTextStore.regionMatches(0, 1, "t");
		assertEquals(true, result);
	}

	public void testRegionMatches2() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		structuredDocumentTextStore.set("testing123");
		boolean result = structuredDocumentTextStore.regionMatches(0, 2, "t");
		assertEquals(false, result);
	}
	public void testRegionMatches3() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		structuredDocumentTextStore.set("testing123");
		boolean result = structuredDocumentTextStore.regionMatches(7, 3, "123");
		assertEquals(true, result);
	}
	public void testRegionMatches4() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		structuredDocumentTextStore.set("testing123");
		boolean result = structuredDocumentTextStore.regionMatches(8, 3, "234");
		assertEquals(false, result);
	}
	
	public void testRegionMatchesIgnoreCase() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		String test = "testing123";
		String compare = "TeSting123";
		structuredDocumentTextStore.set(test);
		boolean result = structuredDocumentTextStore.regionMatches(0, test.length(), compare);
		assertEquals(false, result);
		result = structuredDocumentTextStore.regionMatchesIgnoreCase(0, test.length(), compare);
		assertEquals(true, result);
	}
	public void testRegionMatchesIgnoreCase2() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		String test = "test\u0130ng123";
		String compare = "TeStIng123";
		structuredDocumentTextStore.set(test);
		boolean result = structuredDocumentTextStore.regionMatches(0, test.length(), compare);
		assertEquals(false, result);
		result = structuredDocumentTextStore.regionMatchesIgnoreCase(0, test.length(), compare);
		assertEquals(true, result);
	}
	public void testRegionMatchesIgnoreCase3() {
		StructuredDocumentTextStore structuredDocumentTextStore = new StructuredDocumentTextStore();
		String test = "testing123";
		String compare = "TeSt\u0131ng123";
		structuredDocumentTextStore.set(test);
		boolean result = structuredDocumentTextStore.regionMatches(0, test.length(), compare);
		assertEquals(false, result);
		result = structuredDocumentTextStore.regionMatchesIgnoreCase(0, test.length(), compare);
		assertEquals(true, result);
	}
}