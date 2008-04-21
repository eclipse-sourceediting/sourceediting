/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.source;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.parser.internal.JSPTokenizer;

public class JSPTokenizerTest extends TestCase {
	private JSPTokenizer tokenizer = null;

	private void reset(Reader in) {
		tokenizer.reset(in);
	}

	private void reset(String filename) {
		Reader fileReader = null;
		try {
			fileReader = new InputStreamReader(getClass().getResourceAsStream(filename), "utf8");
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		BufferedReader reader = new BufferedReader(fileReader);
		reset(reader);
	}

	protected void setUp() throws Exception {
		super.setUp();
		tokenizer = new JSPTokenizer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		tokenizer = null;
	}

	public void test144807_AttrName() {
		String input = "";
		for (int i = 0; i < 400; i++) {
			input = input += "<a ";
		}
		try {
			reset(new StringReader(input));
			assertTrue("empty input", tokenizer.getNextToken() != null);
			while (tokenizer.getNextToken() != null) {
				// really, we just want to loop
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}

	public void test144807_AttrValue() {
		String input = "<a b=";
		for (int i = 0; i < 400; i++) {
			input = input += "<a ";
		}
		try {
			reset(new StringReader(input));
			assertTrue("empty input", tokenizer.getNextToken() != null);
			while (tokenizer.getNextToken() != null) {
				// really, we just want to loop
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}

	public void test144807_Equals() {
		String input = "<a b";
		for (int i = 0; i < 400; i++) {
			input = input += "<a ";
		}
		try {
			reset(new StringReader(input));
			assertTrue("empty input", tokenizer.getNextToken() != null);
			while (tokenizer.getNextToken() != null) {
				// really, we just want to loop
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}

	public void testInsertComment() {
		reset("jspcomment01.jsp");
		try {
			assertTrue("empty input", tokenizer.getNextToken() != null);
			while (tokenizer.getNextToken() != null) {
				// really, we just want to loop
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		catch (StackOverflowError e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		// success if StackOverFlowError does not occur with tokenizer.
		assertTrue(true);
	}
}
