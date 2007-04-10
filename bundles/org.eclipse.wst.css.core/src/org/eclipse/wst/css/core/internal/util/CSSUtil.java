/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.util;



import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.parser.CSSRegionUtil;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;


public class CSSUtil {

	/**
	 * 
	 */
	public static void debugOut(String str) {
		Logger.log(Logger.WARNING, "css warning: " + str); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	// public static Vector extractMediaContents(Vector regions) {
	// Vector media = new Vector();
	// if (regions == null) { return media; }
	//
	// boolean bReady = true;
	// for (Iterator i = regions.iterator(); i.hasNext(); ) {
	// ITextRegion region = (ITextRegion)i.next();
	// if (region == null) { continue; }
	// String type = region.getType();
	// if (bReady) {
	// if (type == IDENT) {
	// media.addElement(region.getText());
	// bReady = false;
	// }
	// } else {
	// if (type == COMMA) {
	// bReady = true;
	// }
	// }
	// }
	//
	// return media;
	// }
	/**
	 * 
	 */
	public static String extractStringContents(String text) {
		return stripQuotes(text);
	}

	/**
	 * 
	 */
	public static String extractUriContents(String text) {
		String contents = text.trim();
		if (contents.toLowerCase().startsWith("url(") && //$NON-NLS-1$
					contents.toLowerCase().endsWith(")")) {//$NON-NLS-1$
			// strip "url(", ")"
			contents = contents.substring(4, contents.length() - 1);
		}
		contents = stripQuotes(contents);

		return contents;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNextSignificantNode(IStructuredDocumentRegion startNode) {
		if (startNode == null) {
			return null;
		}
		IStructuredDocumentRegion node = startNode.getNext();
		while (node != null) {
			String type = getStructuredDocumentRegionType(node);
			if (type != CSSRegionContexts.CSS_S && type != CSSRegionContexts.CSS_COMMENT && type != CSSRegionContexts.CSS_CDO && type != CSSRegionContexts.CSS_CDC) {
				return node;
			}
			node = node.getNext();
		}
		return null;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNodeBackward(IStructuredDocumentRegion startNode, IStructuredDocumentRegion endNode, String type) {
		IStructuredDocumentRegion node;
		for (node = startNode; node != null; node = node.getPrevious()) {
			if (node.getStartOffset() < endNode.getStartOffset()) {
				node = null;
				break;
			}
			else if (getStructuredDocumentRegionType(node) == type) {
				break;
			}
		}
		return node;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNodeForward(IStructuredDocumentRegion startNode, IStructuredDocumentRegion endNode, String type) {
		IStructuredDocumentRegion node;
		for (node = startNode; node != null; node = node.getNext()) {
			if (endNode.getStartOffset() < node.getStartOffset()) {
				node = null;
				break;
			}
			else if (getStructuredDocumentRegionType(node) == type) {
				break;
			}
		}
		return node;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findPreviousSignificantNode(IStructuredDocumentRegion startNode) {
		if (startNode == null) {
			return null;
		}
		IStructuredDocumentRegion node = startNode.getPrevious();
		while (node != null) {
			String type = getStructuredDocumentRegionType(node);
			if (type != CSSRegionContexts.CSS_S && type != CSSRegionContexts.CSS_COMMENT && type != CSSRegionContexts.CSS_CDO && type != CSSRegionContexts.CSS_CDC) {
				return node;
			}
			node = node.getPrevious();
		}
		return null;
	}

	/**
	 * 
	 */
	public static String getClassString(Object object) {
		if (object == null) {
			return "null"; //$NON-NLS-1$
		}
		else {
			String name = object.getClass().toString();
			int lastPeriod = name.lastIndexOf('.');
			return name.substring(lastPeriod + 1);
		}
	}

	/**
	 * 
	 */
	public static String getStructuredDocumentRegionType(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return CSSRegionContexts.CSS_UNDEFINED;
		}
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null || regions.size() == 0) {
			return CSSRegionContexts.CSS_UNDEFINED;
		}
		ITextRegion region = regions.get(0);
		return region.getType();
	}

	/**
	 * 
	 */
	public static int getLengthDifference(IStructuredDocumentRegionList newNodes, IStructuredDocumentRegionList oldNodes) {
		int newLen = getTextLength(newNodes);
		int oldLen = getTextLength(oldNodes);
		return newLen - oldLen;
	}

	/**
	 * 
	 */
	public static String getRegionText(IStructuredDocumentRegion flatNode, ITextRegionList regions) {
		StringBuffer buf = new StringBuffer();
		if (regions != null) {
			for (Iterator i = regions.iterator(); i.hasNext();) {
				ITextRegion region = (ITextRegion) i.next();
				if (region == null) {
					continue;
				}
				buf.append(flatNode.getText(region));
			}
		}

		return buf.toString();
	}

	/**
	 * 
	 */
	public static int getTextLength(IStructuredDocumentRegionList nodes) {
		int length = 0;

		if (nodes != null) {
			for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
				if (flatNode != null) {
					length += flatNode.getText().length();
				}
			}
		}

		return length;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isLength(CSSTextToken token) {
		if (token == null)
			return false;
		if (token.kind == CSSRegionContexts.CSS_DECLARATION_VALUE_DIMENSION)
			return true;
		if (token.kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER) {
			double number = Double.parseDouble(token.image);
			if (number == 0.0)
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public static boolean isSelectorText(IStructuredDocumentRegion region) {
		String type = getStructuredDocumentRegionType(region);
		if (CSSRegionUtil.isSelectorBegginingType(type)) {
			return true;
		}
		else if (type == CSSRegionContexts.CSS_UNKNOWN) {
			// String text = flatNode.getText();
			// if (text != null && text.indexOf('.') == 0) {
			return true;
			// }
		}

		return false;
	}

	/**
	 * 
	 */
	public static String stripQuotes(String text) {
		if (text == null)
			return null;
		String contents = text.trim();
		if (2 <= contents.length()) {
			char first = contents.charAt(0);
			char last = contents.charAt(contents.length() - 1);
			if ((first == '\"' && last == '\"') || (first == '\'' && last == '\'')) {
				contents = contents.substring(1, contents.length() - 1);
			}
		}
		return contents;
	}

	public static String detectQuote(String source, String defaultQuote) {
		if (source == null)
			return defaultQuote;
		final String D_QUOTE = "\""; //$NON-NLS-1$
		final String S_QUOTE = "\'"; //$NON-NLS-1$

		int dIndex = source.indexOf(D_QUOTE);
		int sIndex = source.indexOf(S_QUOTE);
		if (dIndex < 0 && sIndex < 0) {
			return defaultQuote;
		}
		else if (dIndex < 0) {
			return D_QUOTE;
		}
		else if (sIndex < 0) {
			return S_QUOTE;
		}
		else if (dIndex < sIndex) {
			return S_QUOTE;
		}
		else {
			return D_QUOTE;
		}
	}

	/**
	 * 
	 */
	public static void stripSurroundingSpace(ITextRegionList regions) {
		if (regions == null) {
			return;
		}
		while (!regions.isEmpty()) {
			ITextRegion region = regions.get(0);
			String type = region.getType();
			if (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT) {
				regions.remove(0);
			}
			else {
				break;
			}
		}
		while (!regions.isEmpty()) {
			ITextRegion region = regions.get(regions.size() - 1);
			String type = region.getType();
			if (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT) {
				regions.remove(region);
			}
			else {
				break;
			}
		}
	}
}
