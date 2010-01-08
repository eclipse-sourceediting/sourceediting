/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

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
	// [260004]
	public void test26004() {
		String input = "<c:set var=\"foo\" value=\"${foo} bar #\" /> <div id=\"container\" >Test</div>";
		try {
			reset(new StringReader(input));
			ITextRegion region = tokenizer.getNextToken();
			assertTrue("empty input", region != null);
			while (region != null) {
				if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					region = tokenizer.getNextToken();
					assertNotNull("document consumed by trailing $ or #", region);
				}
				else
					region = tokenizer.getNextToken();
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}
	// [150794]
	public void test150794() {
		String input = "<a href=\"<jsp:getProperty/>\">";
		try {
			reset(new StringReader(input));
			ITextRegion region = tokenizer.getNextToken();
			assertTrue("empty input", region != null);
			while (region != null) {
				if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					region = tokenizer.getNextToken();
					assertNotNull("document consumed by embedded JSP tag", region);
				}
				else
					region = tokenizer.getNextToken();
			}
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}
	
	/**
	 * <p>This test is to protect against regression of 299146 where &#160 was being
	 * broken up into & #1 60 and then when a ; was typed could not be recognized
	 * as a Unicode character reference.  It should be detected as & #160 which is
	 * what is tested for here.</p>
	 */
	public void test299146() {
		String input = "<root>&#160</root>";
		try {
			reset(new StringReader(input));
			ITextRegion region = null;
			for(int i = 0; i < 5; ++i) {
				region = tokenizer.getNextToken();
			}
			assertNotNull("This region should exist", region);
			assertEquals("The region did not have the expected start location", 7, region.getStart());
			assertEquals("The region did not have the expected length", 4, region.getLength());
		}
		catch (IOException e) {
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
	}
	
	// Need to simulate typing characters into the document to cause the stack overflow.
	// Test is irrelevant due to changes in [280496]
	/*public void test265380() throws Exception {
		String projectName = "bug_265380";
		int oldDepth = BooleanStack.maxDepth;
		// Make the maxDepth equivalent to that we'd see in a normal editor
		BooleanStack.maxDepth = 100;
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		IFile file = project.getFile("test265380.jsp");
		assertTrue(file.exists());
		
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(file);
		
		try {
			IStructuredDocument jspDocument = model.getStructuredDocument();
		
			// offset in the document to begin inserting text
			int offset = 414;
			// String to insert character-by-character
			String cif = "<c:out value=\"lorem ipsum\"></c:out>\n";
			// It takes several tags to be inserted before the stack was overflowed
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < cif.length(); j++)
					jspDocument.replace(offset++, 0, String.valueOf(cif.charAt(j)));
			}
		}
		catch (StackOverflowError e) {
			fail("Stack overflow encountered while editing document.");
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
			BooleanStack.maxDepth = oldDepth;
		}
	}*/
}
