/**
 *  Copyright (c) 2015, 2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.junit.Assert;
import org.junit.Test;

public class JSONTokenizerTest {

	@Test
	public void array() {
		assertRegions("[]", "[ContextRegion--> JSON_ARRAY_OPEN: 0-1, "
				+ "ContextRegion--> JSON_ARRAY_CLOSE: 1-2]");
	}

	@Test
	public void arrayString() {
		assertRegions("[\"a\"]", "[ContextRegion--> JSON_ARRAY_OPEN: 0-1, "
				+ "ContextRegion--> JSON_VALUE_STRING: 1-4, "
				+ "ContextRegion--> JSON_ARRAY_CLOSE: 4-5]");
	}

	@Test
	public void array2String() {
		assertRegions("[\"a\", \"b\"]",
				"[ContextRegion--> JSON_ARRAY_OPEN: 0-1, "
						+ "ContextRegion--> JSON_VALUE_STRING: 1-4, "
						+ "ContextRegion--> JSON_COMMA: 4-5, "
						+ "ContextRegion--> WHITE_SPACE: 5-6, "
						+ "ContextRegion--> JSON_VALUE_STRING: 6-9, "
						+ "ContextRegion--> JSON_ARRAY_CLOSE: 9-10]");
	}

	@Test
	public void arrayStringAndSpace() {
		assertRegions("[\"a\" ]",
				"[ContextRegion--> JSON_ARRAY_OPEN: 0-1, "
				+ "ContextRegion--> JSON_VALUE_STRING: 1-4, "
				+ "ContextRegion--> WHITE_SPACE: 4-5, "
				+ "ContextRegion--> JSON_ARRAY_CLOSE: 5-6]");
	}
	
	@Test
	public void objectWithArray() {
		assertRegions("{\"array\":[]}", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
				+ "ContextRegion--> JSON_OBJECT_KEY: 1-8, "
				+ "ContextRegion--> JSON_COLON: 8-9, "
				+ "ContextRegion--> JSON_ARRAY_OPEN: 9-10, "
				+ "ContextRegion--> JSON_ARRAY_CLOSE: 10-11, "
				+ "ContextRegion--> JSON_OBJECT_CLOSE: 11-12]");
	}
	
	@Test
	public void notValidStartObject() {
		assertRegions("{", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1]");
	}

	@Test
	public void notValidEndObject() {
		assertRegions("}", "[ContextRegion--> JSON_OBJECT_CLOSE: 0-1]");
	}

	@Test
	public void emptyJSON() {
		assertRegions("{}", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1,"
				+ " ContextRegion--> JSON_OBJECT_CLOSE: 1-2]");
	}

	@Test
	public void emptyJSONWithSpace() {
		assertRegions("{}  \n\r", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
				+ "ContextRegion--> JSON_OBJECT_CLOSE: 1-2, "
				+ "ContextRegion--> WHITE_SPACE: 2-6]");
	}

	@Test
	public void notValidKeyStartQuote() {
		assertRegions("{\"}", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
				+ "ContextRegion--> JSON_UNKNOWN: 1-2, "
				+ "ContextRegion--> JSON_OBJECT_CLOSE: 2-3]");
	}

	@Test
	public void notValidKeyDontEndWithQuote() {
		assertRegions("{\"config}", "[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
				+ "ContextRegion--> JSON_UNKNOWN: 1-8, "
				+ "ContextRegion--> JSON_OBJECT_CLOSE: 8-9]");
	}

	@Test
	public void notValidMissColon() {
		assertRegions("{\"config\"}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 9-10]");
	}

	@Test
	public void notValidMissValue() {
		assertRegions("{\"config\": }",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 11-12]");
	}

	@Test
	public void trueValue() {
		assertRegions("{\"config\": true}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_VALUE_BOOLEAN: 11-15, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 15-16]");
	}

	@Test
	public void stringValue() {
		assertRegions("{\"config\": \"true\"}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_VALUE_STRING: 11-17, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 17-18]");
	}

	@Test
	public void nullValue() {
		assertRegions("{\"config\": null}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_VALUE_NULL: 11-15, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 15-16]");
	}

	@Test
	public void numberValue() {
		assertRegions("{\"config\": 5   }",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_VALUE_NUMBER: 11-12, "
						+ "ContextRegion--> WHITE_SPACE: 12-15, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 15-16]");
	}

	@Test
	public void embed() {
		assertRegions("{\"config\": {}}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-9, "
						+ "ContextRegion--> JSON_COLON: 9-10, "
						+ "ContextRegion--> WHITE_SPACE: 10-11, "
						+ "ContextRegion--> JSON_OBJECT_OPEN: 11-12, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 12-13, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 13-14]");
	}

	@Test
	public void twoFields() {
		assertRegions("{\"a\": 1, \"b\": 2}",
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 1-4, "
						+ "ContextRegion--> JSON_COLON: 4-5, "
						+ "ContextRegion--> WHITE_SPACE: 5-6, "
						+ "ContextRegion--> JSON_VALUE_NUMBER: 6-7, "
						+ "ContextRegion--> JSON_COMMA: 7-8, "
						+ "ContextRegion--> WHITE_SPACE: 8-9, "
						+ "ContextRegion--> JSON_OBJECT_KEY: 9-12, "
						+ "ContextRegion--> JSON_COLON: 12-13, "
						+ "ContextRegion--> WHITE_SPACE: 13-14, "
						+ "ContextRegion--> JSON_VALUE_NUMBER: 14-15, "
						+ "ContextRegion--> JSON_OBJECT_CLOSE: 15-16]");
	}
	
	@Test
	public void sample() {
		InputStream json = JSONTokenizerTest.class
				.getResourceAsStream("sample.json");
		assertRegions(
				new EOLSkippingReader(new InputStreamReader(json)),
				"[ContextRegion--> JSON_OBJECT_OPEN: 0-1, ContextRegion--> WHITE_SPACE: 1-5, ContextRegion--> JSON_OBJECT_KEY: 5-15, ContextRegion--> JSON_COLON: 15-16, ContextRegion--> WHITE_SPACE: 16-17, ContextRegion--> JSON_OBJECT_OPEN: 17-18, ContextRegion--> WHITE_SPACE: 18-26, ContextRegion--> JSON_OBJECT_KEY: 26-33, ContextRegion--> JSON_COLON: 33-34, ContextRegion--> WHITE_SPACE: 34-35, ContextRegion--> JSON_VALUE_STRING: 35-53, ContextRegion--> JSON_COMMA: 53-54, ContextRegion--> WHITE_SPACE: 54-56, ContextRegion--> JSON_OBJECT_KEY: 56-66, ContextRegion--> JSON_COLON: 66-67, ContextRegion--> WHITE_SPACE: 67-68, ContextRegion--> JSON_OBJECT_OPEN: 68-69, ContextRegion--> WHITE_SPACE: 69-81, ContextRegion--> JSON_OBJECT_KEY: 81-88, ContextRegion--> JSON_COLON: 88-89, ContextRegion--> WHITE_SPACE: 89-90, ContextRegion--> JSON_VALUE_STRING: 90-93, ContextRegion--> JSON_COMMA: 93-94, ContextRegion--> WHITE_SPACE: 94-97, ContextRegion--> JSON_OBJECT_KEY: 97-108, ContextRegion--> JSON_COLON: 108-109, ContextRegion--> WHITE_SPACE: 109-110, ContextRegion--> JSON_OBJECT_OPEN: 110-111, ContextRegion--> WHITE_SPACE: 111-127, ContextRegion--> JSON_OBJECT_KEY: 127-139, ContextRegion--> JSON_COLON: 139-140, ContextRegion--> WHITE_SPACE: 140-141, ContextRegion--> JSON_OBJECT_OPEN: 141-142, ContextRegion--> WHITE_SPACE: 142-162, ContextRegion--> JSON_OBJECT_KEY: 162-166, ContextRegion--> JSON_COLON: 166-167, ContextRegion--> WHITE_SPACE: 167-168, ContextRegion--> JSON_VALUE_STRING: 168-174, ContextRegion--> JSON_COMMA: 174-175, ContextRegion--> WHITE_SPACE: 175-180, ContextRegion--> JSON_OBJECT_KEY: 180-188, ContextRegion--> JSON_COLON: 188-189, ContextRegion--> WHITE_SPACE: 189-190, ContextRegion--> JSON_VALUE_STRING: 190-196, ContextRegion--> JSON_COMMA: 196-197, ContextRegion--> WHITE_SPACE: 197-202, ContextRegion--> JSON_OBJECT_KEY: 202-213, ContextRegion--> JSON_COLON: 213-214, ContextRegion--> WHITE_SPACE: 214-215, ContextRegion--> JSON_VALUE_STRING: 215-253, ContextRegion--> JSON_COMMA: 253-254, ContextRegion--> WHITE_SPACE: 254-259, ContextRegion--> JSON_OBJECT_KEY: 259-268, ContextRegion--> JSON_COLON: 268-269, ContextRegion--> WHITE_SPACE: 269-270, ContextRegion--> JSON_VALUE_STRING: 270-276, ContextRegion--> JSON_COMMA: 276-277, ContextRegion--> WHITE_SPACE: 277-282, ContextRegion--> JSON_OBJECT_KEY: 282-290, ContextRegion--> JSON_COLON: 290-291, ContextRegion--> WHITE_SPACE: 291-292, ContextRegion--> JSON_VALUE_STRING: 292-307, ContextRegion--> JSON_COMMA: 307-308, ContextRegion--> WHITE_SPACE: 308-313, ContextRegion--> JSON_OBJECT_KEY: 313-323, ContextRegion--> JSON_COLON: 323-324, ContextRegion--> WHITE_SPACE: 324-325, ContextRegion--> JSON_OBJECT_OPEN: 325-326, ContextRegion--> WHITE_SPACE: 326-350, ContextRegion--> JSON_OBJECT_KEY: 350-356, ContextRegion--> JSON_COLON: 356-357, ContextRegion--> WHITE_SPACE: 357-358, ContextRegion--> JSON_VALUE_STRING: 358-432, ContextRegion--> JSON_COMMA: 432-433, ContextRegion--> WHITE_SPACE: 433-439, ContextRegion--> JSON_OBJECT_KEY: 439-453, ContextRegion--> JSON_COLON: 453-454, ContextRegion--> WHITE_SPACE: 454-455, ContextRegion--> JSON_ARRAY_OPEN: 455-456, ContextRegion--> JSON_VALUE_STRING: 456-461, ContextRegion--> JSON_COMMA: 461-462, ContextRegion--> WHITE_SPACE: 462-463, ContextRegion--> JSON_VALUE_STRING: 463-468, ContextRegion--> JSON_ARRAY_CLOSE: 468-469, ContextRegion--> WHITE_SPACE: 469-489, ContextRegion--> JSON_OBJECT_CLOSE: 489-490, ContextRegion--> JSON_COMMA: 490-491, ContextRegion--> WHITE_SPACE: 491-496, ContextRegion--> JSON_OBJECT_KEY: 496-506, ContextRegion--> JSON_COLON: 506-507, ContextRegion--> WHITE_SPACE: 507-508, ContextRegion--> JSON_VALUE_STRING: 508-516, ContextRegion--> WHITE_SPACE: 516-532, ContextRegion--> JSON_OBJECT_CLOSE: 532-533, ContextRegion--> WHITE_SPACE: 533-545, ContextRegion--> JSON_OBJECT_CLOSE: 545-546, ContextRegion--> WHITE_SPACE: 546-554, ContextRegion--> JSON_OBJECT_CLOSE: 554-555, ContextRegion--> WHITE_SPACE: 555-559, ContextRegion--> JSON_OBJECT_CLOSE: 559-560, ContextRegion--> JSON_OBJECT_CLOSE: 560-561]");
	}

	@Test
	public void comment() {
		assertRegions("/*{}*/", "[ContextRegion--> JSON_COMMENT: 0-6]");
	}
	
	@Test
	public void simpleComment() {
		assertRegions("// bla bla bla\n{}", "[ContextRegion--> JSON_COMMENT: 0-14, "
				+ "ContextRegion--> WHITE_SPACE: 14-15, "
				+ "ContextRegion--> JSON_OBJECT_OPEN: 15-16, "
				+ "ContextRegion--> JSON_OBJECT_CLOSE: 16-17]");
	}
	
	private void assertRegions(String json, String regions) {
		assertRegions(new java.io.StringReader(json), regions);
	}

	private void assertRegions(InputStream json, String regions) {
		assertRegions(new InputStreamReader(json), regions);
	}

	private void assertRegions(Reader reader, String regions) {
		JSONTokenizer tokenizer = new JSONTokenizer();
		tokenizer.reset(reader, 0);
		List<ITextRegion> r = getRegions(tokenizer);
		System.err.println(reader.toString() + "==>" + r);
		Assert.assertEquals(regions, r.toString());
	}

	private static final List<ITextRegion> getRegions(JSONTokenizer tokenizer) {
		List<ITextRegion> tokens = new ArrayList<ITextRegion>();
		ITextRegion region = null;
		try {
			region = tokenizer.getNextToken();
			while (region != null) {
				if (region != null) {
					tokens.add(region);
				}
				region = tokenizer.getNextToken();
			}
		} catch (StackOverflowError e) {
			//Logger.logException(getClass().getName()+": input could not be tokenized correctly at position " + getOffset(), e);//$NON-NLS-1$
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			// Since this is convenience method and NOT the recommended
			// way of getting tokens, many errors are simply hidden
			//Logger.logException("Exception not handled retrieving regions: " + e.getLocalizedMessage(), e);//$NON-NLS-1$
		}
		return tokens;
	}

	/*
	 * The reader skipping the EOL characters so the test should work OK for Win-created files on *nix systems and backwards
	 */
	private static class EOLSkippingReader extends Reader {
		Reader source;
		
		public EOLSkippingReader(Reader source) {
			this.source = source;
		}
		
		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			int start = off;
			int count = 0;
			
			for (; count < len; count++) {
				int ch = read();
				if (ch == -1)
					return count;
				cbuf[start + count] = (char)ch;
			}
			return count; 
		}
		
		@Override
		public long skip(long n) throws IOException {
			throw new IOException("skip(long n) not supported");
		}

		@Override
		public boolean markSupported() {
			return false;
		}
		
		@Override
		public int read() throws IOException {
			int ch = -1;
			do { 
				ch = source.read();
			} while (ch == '\n' || ch == '\r');	
			return ch;
		}
		
		@Override
		public void close() throws IOException {
			source.close();
		}
	
	}
}
