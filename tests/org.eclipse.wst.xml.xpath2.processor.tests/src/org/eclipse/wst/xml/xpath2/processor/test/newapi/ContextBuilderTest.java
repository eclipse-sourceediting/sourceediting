/*******************************************************************************
 * Copyright (c) 2011 Jesper Steen Moller and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jesper Steen Moller  - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.test.newapi;

import junit.framework.TestCase;

import org.eclipse.wst.xml.xpath2.processor.util.StaticContextBuilder;

public class ContextBuilderTest extends TestCase {

	
	public void testReasonableDefaults() {
		StaticContextBuilder scb = new StaticContextBuilder();
		
		assertFalse(scb.isXPath1Compatible());
		assertEquals("", scb.getDefaultNamespace());
		assertEquals(null, scb.getBaseUri());
	}
	
}
