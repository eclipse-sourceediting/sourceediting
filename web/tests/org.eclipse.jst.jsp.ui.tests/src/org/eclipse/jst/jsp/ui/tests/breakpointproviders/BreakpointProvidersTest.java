/*******************************************************************************
 * Copyright (c) 2010, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.breakpointproviders;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.ui.internal.breakpointproviders.ClassPatternRegistry;

import com.ibm.icu.util.StringTokenizer;

public class BreakpointProvidersTest extends TestCase {

	/**
	 * Tests the case of a single provider of additional class patterns
	 */
	public void testAdditionalProviders() {
		// Test
		final String pattern = ClassPatternRegistry.getInstance().getClassPattern("org.eclipse.jst.jsp.ui.tests.type");
		assertNotNull("Class pattern for 'type' shouldn't be null", pattern);
		final String[] expected = new String[] { "*foo", "*bar" };
		final StringTokenizer tokenizer = new StringTokenizer(pattern, ",");
		final Set tokens = new HashSet(expected.length);
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextElement());
		}
		for (int i = 0; i < expected.length; i++) {
			tokens.remove(expected[i]);
		}
		assertTrue("Class patterns are not equal", tokens.isEmpty());
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
		final String[] expected = new String[] { "*foo", "*bar", "*baz" };
		final StringTokenizer tokenizer = new StringTokenizer(pattern, ",");
		final Set tokens = new HashSet(expected.length);
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextElement());
		}
		for (int i = 0; i < expected.length; i++) {
			tokens.remove(expected[i]);
		}
		assertTrue("Class patterns are not equal", tokens.isEmpty());
	}
}