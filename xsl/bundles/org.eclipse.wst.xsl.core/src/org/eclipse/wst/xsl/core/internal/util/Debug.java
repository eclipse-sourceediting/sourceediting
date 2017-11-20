/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation based on WTP SSE Debug class
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.util;



import java.util.Enumeration;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;


/**
 * Debug Class is used to determine when Debugging output to system out.
 * @author dcarver
 *
 */
public final class Debug {
	public static final boolean checkForMemoryLeaks = false;

	public static final boolean collectStats = false;

	public static final int DEBUG = 0;

	public static final boolean DEBUG_THREADLOCAL = false;

	public static final boolean debugBreakpoints = false;
	public static final boolean debugCaretMediator = false;
	public static final boolean debugDisplayTreePositions = false;
	//
	public static final boolean debugMediator = false;
	//
	public static final boolean debugNotification = false;
	public static final boolean debugNotificationAndEvents = false;

	public static final boolean debugNotifyDeferred = false;
	public static final boolean debugReconciling = false;
	//
	public static final boolean debugRtfFormatProvider = false;
	//
	public static final boolean debugStructuredDocument = false;
	public static final boolean debugTaglibs = false;
	//
	public static final boolean debugTokenizer = false;
	
	//
	public static final boolean debugLauncher = false;
	
	/**
	 * Output Modeling Messages.
	 */
	public static final boolean debugXSLModel = false;
	//
	public static final boolean debugTreeModel = false;
	public static final boolean debugUpdateTreePositions = false;
	public static final boolean displayInfo = false;

	/** effects output of Logger */
	public static final boolean displayToConsole = true;
	public static final boolean displayWarnings = false;
	//
	public static final boolean headParsing = false;
	public static final boolean jsDebugContextAssist = false;
	//
	public static final boolean jsDebugSyntaxColoring = false;

	public static final boolean LOCKS = false;
	// 
	public static final boolean perfTest = false;
	public static final boolean perfTestAdapterClassLoading = false;
	public static final boolean perfTestFormat = false;
	public static final boolean perfTestRawStructuredDocumentOnly = false;
	public static final boolean perfTestStructuredDocumentEventOnly = false;
	public static final boolean perfTestStructuredDocumentOnly = false;

	//
	public static final boolean syntaxHighlighting = false;
	//
	public static final boolean useStandardEolInWidget = false;

	/**
	 * For tests and debug only
	 * @param structuredDocument 
	 */

	public static final void dump(IStructuredDocument structuredDocument) {
		dump(structuredDocument, false);
	}

	public static final void dump(IStructuredDocument structuredDocument, boolean verbose) {
		ITextRegionCollection flatNode = null;
		System.out.println("Dump of structuredDocument:"); //$NON-NLS-1$
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		Enumeration<?> structuredDocumentRegions = flatNodes.elements();
		while (structuredDocumentRegions.hasMoreElements()) {
			flatNode = (ITextRegionCollection) structuredDocumentRegions.nextElement();
			if (!verbose) {
				String outString = flatNode.toString();
				outString = org.eclipse.wst.sse.core.utils.StringUtils.escape(outString);
				System.out.println(outString);
			} else {
				dump(flatNode, verbose);
			}
		}
		System.out.println();
		System.out.println("= = = = = ="); //$NON-NLS-1$
		System.out.println();
	}

	/**
	 * @param region 
	 * @param flatNode
	 * @param verbose
	 */
	public static final void dump(ITextRegionCollection region, boolean verbose) {
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
				} else {
					System.out.println(space(depth) + r);
					depth--;
				}
			}
		}
	}

	/**
	 * Simple utility to make sure println's are some what in order
	 * @param msg 
	 */
	public static final synchronized void println(String msg) {
		System.out.println(System.currentTimeMillis() + "\t" + msg); //$NON-NLS-1$
	}

	private static void printParent(IStructuredDocumentRegion region) {
		System.out.println("    [parent document: " + toStringUtil(region.getParentDocument()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void printParent(ITextRegionCollection region) {
		if (region instanceof IStructuredDocumentRegion) {
			printParent((IStructuredDocumentRegion) region);
		} else if (region instanceof ITextRegionContainer) {
			printParent((ITextRegionContainer) region);
		} else
			System.out.println("    [parent document: " + "(na)" + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private static void printParent(ITextRegionContainer region) {
		System.out.println("    [parent document: " + toStringUtil(region.getParent()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param depth
	 * @return
	 */
	private static String space(int depth) {
		String result = "  "; //$NON-NLS-1$
		StringBuffer buf = new StringBuffer(result);
		for (int i = 0; i < depth; i++) {
			buf.append("  "); //$NON-NLS-1$
		}
		result = buf.toString();
		return result;
	}

	public static final String toStringUtil(IStructuredDocument object) {
		String className = object.getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		String result = shortClassName;
		// NOTE: if the document held by any region has been updated and the
		// region offsets have not
		// yet been updated, the output from this method invalid.
		return result;

	}

	public static final String toStringUtil(ITextRegionCollection object) {
		String className = object.getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		String result = shortClassName;
		// NOTE: if the document held by any region has been updated and the
		// region offsets have not
		// yet been updated, the output from this method invalid.
		return result;

	}

	/**
	 * Debug constructor comment.
	 */
	public Debug() {
		super();
	}
}
