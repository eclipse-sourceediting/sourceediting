/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.util;



import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;

public class Debug {

	public static boolean debugNotifyDeferred = false;

	public static int DEBUG = 0;

	public static boolean collectStats = false;
	public static boolean jsDebugContextAssist = false;
	//
	public static boolean useStandardEolInWidget = false;
	//
	public static boolean syntaxHighlighting = false;
	public static boolean headParsing = false;

	public static boolean debugBreakpoints = false;

	/** effects output of Logger */
	public static boolean displayToConsole = true;
	public static boolean displayWarnings = false;
	public static boolean displayInfo = false;
	//
	public static boolean debugMediator = false;
	public static boolean debugCaretMediator = false;
	//
	public static boolean debugStructuredDocument = false;
	//
	public static boolean debugTreeModel = false;
	public static boolean debugDisplayTreePositions = false;
	public static boolean debugUpdateTreePositions = false;
	//
	public static boolean jsDebugSyntaxColoring = false;
	//
	public static boolean debugTokenizer = false;
	public static boolean debugTaglibs = false;
	//
	public static boolean debugRtfFormatProvider = false;
	public static boolean debugReconciling = false;
	// 
	public static boolean perfTest = false;
	public static boolean perfTestStructuredDocumentOnly = false;
	public static boolean perfTestRawStructuredDocumentOnly = false;
	public static boolean perfTestStructuredDocumentEventOnly = false;
	public static boolean perfTestAdapterClassLoading = false;
	public static boolean perfTestFormat = false;
	public static boolean checkForMemoryLeaks = false;
	//
	public static boolean failedTests = false;

	/**
	 * Debug constructor comment.
	 */
	public Debug() {
		super();
	}

	/**
	 * For tests and debug only
	 */

	public static void dump(IStructuredDocument structuredDocument) {
		dump(structuredDocument, false);
	}

	public static void dump(IStructuredDocument structuredDocument, boolean verbose) {
		ITextRegionCollection flatNode = null;
		System.out.println("Dump of structuredDocument:"); //$NON-NLS-1$
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		java.util.Enumeration enum = flatNodes.elements();
		while (enum.hasMoreElements()) {
			flatNode = (ITextRegionCollection) enum.nextElement();
			if (!verbose) {
				String outString = flatNode.toString();
				outString = org.eclipse.wst.sse.core.util.StringUtils.escape(outString);
				System.out.println(outString);
			}
			else {
				dump(flatNode, verbose);
			}
		}
		System.out.println();
		System.out.println("= = = = = ="); //$NON-NLS-1$
		System.out.println();
	}

	public static String toStringUtil(IStructuredDocument object) {
		String className = object.getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		// ==> // String resultText = null;
		String result = shortClassName;
		// NOTE: if the document held by any region has been updated and the region offsets have not
		// yet been updated, the output from this method invalid.
		return result;

	}

	public static String toStringUtil(ITextRegionCollection object) {
		String className = object.getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		// ==> // String resultText = null;
		String result = shortClassName;
		// NOTE: if the document held by any region has been updated and the region offsets have not
		// yet been updated, the output from this method invalid.
		return result;

	}

	/**
	 * @param flatNode
	 * @param verbose
	 */
	public static void dump(ITextRegionCollection region, boolean verbose) {
		if (region == null)
			return;
		if (verbose) {
			printParent(region);
		}
		printChildRegions(region, 0);
	}

	private static void printChildRegions(ITextRegionCollection region, int depth) {
		if (region != null) {
			// ==> // ITextRegionCollection regionCollection = region;
			System.out.println(region);
			ITextRegionList regionList = region.getRegions();
			for (int i = 0; i < regionList.size(); i++) {
				ITextRegion r = regionList.get(i);
				if (r instanceof ITextRegionCollection) {
					ITextRegionCollection rc = (ITextRegionCollection) r;
					printChildRegions(rc, depth++);
				}
				else {
					System.out.println(space(depth) + r);
					depth--;
				}
			}
		}
	}

	/**
	 * @param depth
	 * @return
	 */
	private static String space(int depth) {
		String result = "  "; //$NON-NLS-1$
		for (int i = 0; i < depth; i++) {
			result += "  "; //$NON-NLS-1$
		}
		return result;
	}

	private static void printParent(ITextRegionContainer region) {
		System.out.println("    [parent document: " + toStringUtil(region.getParent()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void printParent(IStructuredDocumentRegion region) {
		System.out.println("    [parent document: " + toStringUtil(region.getParentDocument()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void printParent(ITextRegionCollection region) {
		if (region instanceof IStructuredDocumentRegion) {
			printParent((IStructuredDocumentRegion) region);
		}
		else if (region instanceof ITextRegionContainer) {
			printParent((ITextRegionContainer) region);
		}
		else
			System.out.println("    [parent document: " + "(na)" + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
