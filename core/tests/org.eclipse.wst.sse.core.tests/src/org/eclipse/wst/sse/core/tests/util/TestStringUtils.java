/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
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

package org.eclipse.wst.sse.core.tests.util;

import org.eclipse.wst.sse.core.utils.StringUtils;

import junit.framework.TestCase;

public class TestStringUtils extends TestCase {
	public void testFirstLine() {
		assertEquals("a", StringUtils.firstLine("a", 5, true));
		assertEquals("a", StringUtils.firstLine("a", 5, false));
		assertEquals("abcde...", StringUtils.firstLine("abcdefgh", 5, true));
		assertEquals("abcde", StringUtils.firstLine("abcdefgh", 5, false));
		assertEquals("abcde...", StringUtils.firstLine("abcdefgh\n", 5, true));
		assertEquals("abcde", StringUtils.firstLine("abcdefgh\n", 5, false));
		assertEquals("abcde...", StringUtils.firstLine("abcde\nfghij", 15, true));
		assertEquals("abcde", StringUtils.firstLine("abcde\nfghij", 15, false));
		assertEquals("abcde...", StringUtils.firstLine("\nabcde\nfghij", 15, true));
		assertEquals("abcde", StringUtils.firstLine("\nabcde\nfghij", 15, false));
		assertEquals("abcde...", StringUtils.firstLine("\nabcdefghij\n", 5, true));
		assertEquals("abcde", StringUtils.firstLine("\nabcdefghij\n", 5, false));
	}
}
