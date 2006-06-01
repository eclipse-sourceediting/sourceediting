package org.eclipse.jst.jsp.core.tests.source;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.parser.internal.JSPTokenizer;

public class JSPTokenizerTest extends TestCase {
	private JSPTokenizer tokenizer = new JSPTokenizer();
	
	public void testInsertComment(){
		reset("jspcomment01.jsp");
		try {
			while(tokenizer.getNextToken() != null);
		} catch (IOException e) {
		} catch (StackOverflowError e) {
			fail(e.getMessage());
			return;
		}
		// success if StackOverFlowError does not occur with tokenizer.
		assertTrue(true);
	}
	
	private void reset(String filename){
		Reader fileReader = null;
		try {
			fileReader = new InputStreamReader(getClass().getResourceAsStream(filename), "utf8");
		} catch (IOException e) {
		}
		if (fileReader == null){
			fail();
		}
		BufferedReader reader = new BufferedReader(fileReader);
		reset(reader);
	}
		
	private void reset(Reader in){
		tokenizer.reset(in);
	}
}
