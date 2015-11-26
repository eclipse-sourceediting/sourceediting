/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentRegionIterator;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * test class for basic parsing and scanning
 */

public class ScanningTests {
	private static IStructuredDocument textStore = null;
	private static long startTime = 0;
	private static long stopTime = 0;

	/**
	 * ScanningTests constructor comment.
	 */
	public ScanningTests() {
		super();
	}

	public static void checkNodeSeams(String fileName) {
		System.out.println("CHECKING NODE BOUNDARIES AND CONTENTS FOR " + fileName);
		Enumeration e = parseFile(fileName).elements();
		int lastEnd = 0;
		while (e.hasMoreElements()) {
			IStructuredDocumentRegion r = (IStructuredDocumentRegion) e.nextElement();
			if (r.getStartOffset() != lastEnd)
				System.out.println("ERROR: dropped in " + r);
			if (r.getNumberOfRegions() < 1)
				System.out.println("ERROR: empty IStructuredDocumentRegion " + r);
			lastEnd = r.getEndOffset();
		}
		System.out.println("DONE");
	}

	public static void checkParse(String fileName) {
		checkTokenSeams(fileName);
		checkNodeSeams(fileName);
	}

	public static void checkTokenSeams(String fileName) {
		System.out.println("CHECKING TOKENIZER REGION BOUNDARIES FOR " + fileName);
		Iterator e = tokenizeFile(fileName).iterator();
		int lastEnd = 0;
		while (e.hasNext()) {
			IStructuredDocumentRegion r = (IStructuredDocumentRegion) e.next();
			if (r.getStartOffset() != lastEnd)
				System.out.println("ERROR: dropped in " + r);
			lastEnd = r.getEndOffset();
		}
		System.out.println("DONE");
	}

	public static char[] loadChars(String fileName) {
		char[] chars = null;
		int c = 0;
		int length = 0;
		int count = 0;
		File file = null;
		FileInputStream fis = null;
		try {
			file = new File(fileName);
			length = (int) file.length();
			chars = new char[length];
			fis = new FileInputStream(file);
			while (((c = fis.read()) >= 0) && (count < length)) {
				chars[count++] = (char) c;
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found : \"" + fileName + "\"");
			System.exit(1);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Usage : java JSPLexer3 <inputfile>");
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println("An I/O error occured while scanning :");
			System.out.println(e);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return chars;
	}

	public static RegionParser newParser() {
		JSPSourceParser parser = new JSPSourceParser();
		parser.addBlockMarker(new BlockMarker("foo", null, DOMRegionContext.BLOCK_TEXT, true));
		parser.addBlockMarker(new BlockMarker("globalFoo", null, DOMRegionContext.BLOCK_TEXT, true));
		//		parser.addBlockMarker(new BlockMarker("jsp:expression", null, XMLJSPRegionContexts.JSP_CONTENT, true));
		//		parser.addBlockMarker(new BlockMarker("jsp:scriptlet", null, XMLJSPRegionContexts.JSP_CONTENT, true));
		//		parser.addBlockMarker(new BlockMarker("jsp:declaration", null, XMLJSPRegionContexts.JSP_CONTENT, true));
		//		parser.addBlockMarker(new BlockMarker("jsp:text", null, XMLRegionContext.XML_CDATA_TEXT, true));
		return parser;
	}

	public static RegionParser newXMLParser() {
		XMLSourceParser parser = new XMLSourceParser();
		parser.addBlockMarker(new BlockMarker("Script", DOMRegionContext.BLOCK_TEXT, false));
		parser.addBlockMarker(new BlockMarker("stylE", DOMRegionContext.BLOCK_TEXT, false));
		return parser;
	}

	public static Vector parse(String stringdata) {
		return parse(stringdata, false);
	}

	public static Vector parse(String stringdata, boolean useXML) {
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		RegionParser parser = null;
		if (useXML)
			parser = newXMLParser();
		else
			parser = newParser();
		if (Debug.perfTest) {
			stopTime = System.currentTimeMillis();
			System.out.println("ScanningTests spent " + (stopTime - startTime) + " (msecs) creating a " + parser.getClass().getName());
		}
		// Caution: cast
		parser.reset(new StringReader(stringdata));
		IStructuredDocumentRegion aNode = setNodeDocument(parser.getDocumentRegions());
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		textStore = StructuredDocumentFactory.getNewStructuredDocumentInstance(parser);
		textStore.setText(null, stringdata);
		StructuredDocumentRegionIterator.setParentDocument(aNode, textStore);
		Vector v = new Vector();
		while (aNode != null) {
			v.addElement(aNode);
			aNode = aNode.getNext();
		}
		if (Debug.perfTest) {
			stopTime = System.currentTimeMillis();
			System.out.println("ScanningTests spent " + (stopTime - startTime) + " (msecs) setting text and storing nodes");
		}
		return v;
	}

	public static Vector parseFile(String fileName) {
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		char[] input = loadChars(fileName);
		if (Debug.perfTest) {
			stopTime = System.currentTimeMillis();
			System.out.println("ScanningTests spent " + (stopTime - startTime) + " (msecs) loading " + fileName);
		}
		return parse(new String(input));
	}

	private static IStructuredDocumentRegion setNodeDocument(IStructuredDocumentRegion startNode) {
		// Caution: cast
		IStructuredDocumentRegion aNode = startNode;
		while (aNode != null) {
			aNode.setParentDocument(textStore);
			aNode = aNode.getNext();
		}
		return startNode;
	}

	public static List tokenizeFile(String fileName) {
		List v = null;
		StringBuffer buff = new StringBuffer();
		try {
			//char[] input = loadChars(fileName);
			Reader input = new FileReader(fileName);
			RegionParser parser = newParser();
			// parser must be given input, before tokenizer is valid
			parser.reset(input);
			int c = 0;
			parser.getDocumentRegions();
			v = parser.getRegions();
			input.reset();
			while ((c = input.read()) >= 0) {
				buff.append((char) c);
			}
			textStore = StructuredDocumentFactory.getNewStructuredDocumentInstance(parser);
			textStore.setText(null, buff.toString());
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found : \"" + fileName + "\"");
		}
		catch (IOException e) {
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Usage : java JSPLexer3 <inputfile>");
		}
		return v;
	}

	public static String viewableTokenizeWithSeparator(String data) {
		StringBuffer s = new StringBuffer();
		s.append("\"");
		s.append(data);
		s.append("\"\n");
		s.append(viewableTokenize(data));
		s.append("--------------------------------------------------------------------------------\n");
		return s.toString();
	}

	public static String viewableTokenize(String stringdata) {
		return viewableTokenize(stringdata, false);
	}

	public static String viewableTokenize(String stringdata, boolean useXML) {
		java.util.List l = parse(stringdata, useXML);
		String s = "";
		for (int i = 0; i < l.size(); i++) {
			IStructuredDocumentRegion node = (IStructuredDocumentRegion) l.get(i);
			try {
				s += StringUtils.escape(node.toString()) + "\n";
			}
			catch (Exception e) {
				s += "[" + node.getStart() + ", " + node.getEnd() + "] (UNPRINTABLE " + e + ")";
			}
			ITextRegionList m = node.getRegions();
			for (int j = 0; j < m.size(); j++)
				if (m.get(j) instanceof ITextRegionContainer) {
					s = s + "\t" + StringUtils.escape(m.get(j).toString()) + "\n";
					ITextRegionList n = ((ITextRegionContainer) m.get(j)).getRegions();
					for (int k = 0; k < n.size(); k++)
						s = s + "\t\t" + StringUtils.escape(n.get(k).toString()) + "\n";
				}
				else
					s = s + "\t" + StringUtils.escape(m.get(j).toString()) + "\n";
		}
		return s;
	}

	public static String viewableTokenizeFile(String fileName) {
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		if (Debug.perfTest) {
			startTime = System.currentTimeMillis();
		}
		char[] input = loadChars(fileName);
		if (Debug.perfTest) {
			stopTime = System.currentTimeMillis();
			System.out.println("ScanningTests spent " + (stopTime - startTime) + " (msecs) loading " + fileName);
		}
		return viewableTokenize(new String(input));
	}
}
