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
package org.eclipse.jst.jsp.ui.tests.breakpointproviders;

import org.eclipse.jst.jsp.ui.internal.breakpointproviders.ClassPatternRegistry;

import junit.framework.TestCase;

public class BreakpointProvidersTest extends TestCase {

	/**
	 * Tests the case of a single provider of additional class patterns
	 */
	public void testAdditionalProviders() {
		// Test
		final String pattern = ClassPatternRegistry.getInstance().getClassPattern("org.eclipse.jst.jsp.ui.tests.type");
		assertNotNull("Class pattern for 'type' shouldn't be null", pattern);
		assertEquals("Class patterns are not equal", "*foo,*bar", pattern);
	}

	/**
	 * Tests the case of no provider for a content type
	 */
	public void testNoAdditionalProviders() {
		final String pattern = ClassPatternRegistry.getInstance().getClassPattern("org.eclipse.jst.jsp.ui.tests.notype");
		assertNull("There should be no providers for 'notype'", pattern);
	}

	/**
	 * Tests the case of multiple providers for a content type
	 */
	public void testMultipleProviders() {
		final String pattern = ClassPatternRegistry.getInstance().getClassPattern("org.eclipse.jst.jsp.ui.tests.multitype");
		assertNotNull("Class pattern for 'type' shouldn't be null", pattern);
		assertEquals("Class patterns are not equal", "*foo,*bar,*baz", pattern);
	}
}