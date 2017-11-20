/*******************************************************************************
 * Copyright (c) 2010 Intalio Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - bug 325473
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.templates;

import java.util.Iterator;

import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeXSL;

import org.junit.*;
import static org.junit.Assert.*;

public class TestTemplateContextTypeXSL {

	@Test
	public void testXSLTemplateContextType() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
	}

	@Test
	public void testCursorVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("cursor")) {
				return;
			}
		}

		fail("Cursor variable resolver was not found.");
	}

	@Test
	public void testDateVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("date")) {
				return;
			}
		}

		fail("Date variable resolver was not found.");
	}

	@Test
	public void testYearVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("year")) {
				return;
			}
		}

		fail("Year variable resolver was not found.");
	}

	@Test
	public void testTimeVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("time")) {
				return;
			}
		}

		fail("Time variable resolver was not found.");
	}

	@Test
	public void testUserVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("user")) {
				return;
			}
		}

		fail("User variable resolver was not found.");
	}

	@Test
	public void testDollarVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("dollar")) {
				return;
			}
		}

		fail("User variable resolver was not found.");
	}

	@Test
	public void testWordSelectionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("word_selection")) {
				return;
			}
		}

		fail("Word Selection variable resolver was not found.");
	}

	@Test
	public void testLineSelectionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("line_selection")) {
				return;
			}
		}

		fail("Line Selection variable resolver was not found.");
	}

	@Test
	public void testXMLEncodingVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("encoding")) {
				return;
			}
		}

		fail("Encoding Selection variable resolver was not found.");
	}

	@Test
	public void testXSLVersionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();

		while (variables.hasNext()) {
			TemplateVariableResolver resolver = variables.next();
			if (resolver.getType().equals("xsl_version")) {
				return;
			}
		}

		fail("XSL Version variable resolver was not found.");
	}

}
