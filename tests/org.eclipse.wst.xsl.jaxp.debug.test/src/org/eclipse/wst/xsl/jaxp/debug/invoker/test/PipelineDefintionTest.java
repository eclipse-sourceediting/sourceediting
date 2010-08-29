/*******************************************************************************
 * Copyright (c) 2010 Intalio Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker.test;

import java.util.List;


import org.eclipse.wst.xsl.jaxp.debug.invoker.PipelineDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TypedValue;
import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.JAXPSAXProcessorInvoker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class PipelineDefintionTest {
	PipelineDefinition pldef = null;

	@Before
	public void setUp() throws Exception {
		pldef = new PipelineDefinition();
	}
	
	@After
	public void tearDown() throws Exception {
		pldef = null;
	}
	
	@Test
	public void testAddTransformDefinition() throws Exception {
		setupTransformDefinition();
		List<TransformDefinition> tdefs = pldef.getTransformDefs();
		assertNotNull(tdefs);
		assertEquals("Did not find expected transformation defs", 1, tdefs.size());
	}

	private TransformDefinition setupTransformDefinition() {
		TransformDefinition tdef = new TransformDefinition();
		pldef.addTransformDef(tdef);
		return tdef;
	}
	
	@Test
	public void testRemoveTransformDefintion() throws Exception {
		TransformDefinition tdef = setupTransformDefinition();
		assertEquals(1, pldef.getTransformDefs().size());
		pldef.removeTransformDef(tdef);
		assertEquals(0, pldef.getTransformDefs().size());
	}
	
	@Test
	public void testAddAttribute() throws Exception {
		setupAttribute();
		assertEquals(1, pldef.getAttributes().size());
	}

	private TypedValue setupAttribute() {
		TypedValue attr = new TypedValue("attr", TypedValue.TYPE_STRING, "1");
		pldef.addAttribute(attr);
		return attr;
	}
	
	@Test
	public void testRemoveAttribute() throws Exception {
		TypedValue attr = setupAttribute();
		pldef.addAttribute(attr);
		assertEquals(1, pldef.getAttributes().size());
		pldef.removeAttribute(attr);
		assertEquals(0, pldef.getAttributes().size());
	}
	
	@Test
	public void testConfigureInvoker() throws Exception {
		JAXPSAXProcessorInvoker invoker = new JAXPSAXProcessorInvoker();
		pldef.configure(invoker);
	}

}
