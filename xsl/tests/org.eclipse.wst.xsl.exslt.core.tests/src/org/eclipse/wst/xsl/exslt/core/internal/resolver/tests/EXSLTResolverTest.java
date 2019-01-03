/*******************************************************************************
 * Copyright (c) 2009, 2018 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.exslt.core.internal.resolver.tests;

import org.eclipse.wst.xsl.exslt.core.internal.EXSLTCore;
import org.eclipse.wst.xsl.exslt.core.internal.resolver.EXSLTResolverExtension;

import org.junit.*;
import static org.junit.Assert.*;

public class EXSLTResolverTest {
	
	private EXSLTResolverExtension resolver = null;
	
	@Before
	public void setUp() throws Exception {
		resolver = new EXSLTResolverExtension();
	}
	
	@After
	public void tearDown() throws Exception {
		resolver = null;
	}
	

	@Test
	public void testCommonNamespace() {
		String namespace = EXSLTCore.EXSLT_COMMON_NAMESPACE;
		String uri = resolver.resolve(null, null, namespace, null);
		assertTrue("Did not find http://exslt.org/common", uri.contains("/schemas/common.xsd"));
	}
	
	@Test
	public void testURINotResolved() {
		String namespace = "http://www.example.org/";
		String uri = resolver.resolve(null, null, namespace, null);
		assertNull("Found http://www.example.org/", uri);
	}
}
