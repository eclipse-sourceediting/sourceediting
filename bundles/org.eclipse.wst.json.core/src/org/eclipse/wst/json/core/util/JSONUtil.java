/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */

package org.eclipse.wst.json.core.util;

import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

public class JSONUtil {

	public static final String QUOTE = "\""; //$NON-NLS-1$

	public static void debugOut(String str) {
		Logger.log(Logger.WARNING, "json warning: " + str); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	public static String extractStringContents(String text) {
		return stripQuotes(text);
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNextSignificantNode(
			IStructuredDocumentRegion startNode) {
		if (startNode == null) {
			return null;
		}
		IStructuredDocumentRegion node = startNode.getNext();
		while (node != null) {
			String type = getStructuredDocumentRegionType(node);
			/*
			 * if (type != JSONRegionContexts.JSON_S && type !=
			 * JSONRegionContexts.JSON_COMMENT && type !=
			 * JSONRegionContexts.JSON_CDO && type !=
			 * JSONRegionContexts.JSON_CDC) { return node; }
			 */
			node = node.getNext();
		}
		return null;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNodeBackward(
			IStructuredDocumentRegion startNode,
			IStructuredDocumentRegion endNode, String type) {
		IStructuredDocumentRegion node;
		for (node = startNode; node != null; node = node.getPrevious()) {
			if (node.getStartOffset() < endNode.getStartOffset()) {
				node = null;
				break;
			} else if (getStructuredDocumentRegionType(node) == type) {
				break;
			}
		}
		return node;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findNodeForward(
			IStructuredDocumentRegion startNode,
			IStructuredDocumentRegion endNode, String type) {
		IStructuredDocumentRegion node;
		for (node = startNode; node != null; node = node.getNext()) {
			if (endNode.getStartOffset() < node.getStartOffset()) {
				node = null;
				break;
			} else if (getStructuredDocumentRegionType(node) == type) {
				break;
			}
		}
		return node;
	}

	/**
	 * 
	 */
	public static IStructuredDocumentRegion findPreviousSignificantNode(
			IStructuredDocumentRegion startNode) {
		if (startNode == null) {
			return null;
		}
		IStructuredDocumentRegion node = startNode.getPrevious();
		while (node != null) {
			String type = getStructuredDocumentRegionType(node);
			/*
			 * if (type != JSONRegionContexts.JSON_S && type !=
			 * JSONRegionContexts.JSON_COMMENT && type !=
			 * JSONRegionContexts.JSON_CDO && type !=
			 * JSONRegionContexts.JSON_CDC) { return node; }
			 */
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
		} else {
			String name = object.getClass().toString();
			int lastPeriod = name.lastIndexOf('.');
			return name.substring(lastPeriod + 1);
		}
	}

	/**
	 * 
	 */
	public static String getStructuredDocumentRegionType(
			IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return JSONRegionContexts.JSON_UNKNOWN;
		}
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null || regions.size() == 0) {
			return JSONRegionContexts.JSON_UNKNOWN;
		}
		ITextRegion region = regions.get(0);
		return region.getType();
	}

	/**
	 * 
	 */
	public static int getLengthDifference(
			IStructuredDocumentRegionList newNodes,
			IStructuredDocumentRegionList oldNodes) {
		int newLen = getTextLength(newNodes);
		int oldLen = getTextLength(oldNodes);
		return newLen - oldLen;
	}

	/**
	 * 
	 */
	public static String getRegionText(IStructuredDocumentRegion flatNode,
			ITextRegionList regions) {
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
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e
						.nextElement();
				if (flatNode != null) {
					length += flatNode.getText().length();
				}
			}
		}

		return length;
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
			if ((first == '\"' && last == '\"')
					|| (first == '\'' && last == '\'')) {
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
		} else if (dIndex < 0) {
			return D_QUOTE;
		} else if (sIndex < 0) {
			return S_QUOTE;
		} else if (dIndex < sIndex) {
			return S_QUOTE;
		} else {
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
			// if (type == JSONRegionContexts.JSON_S
			// || type == JSONRegionContexts.JSON_COMMENT) {
			// regions.remove(0);
			// } else {
			break;
			// }
		}
		while (!regions.isEmpty()) {
			ITextRegion region = regions.get(regions.size() - 1);
			String type = region.getType();
			// if (type == JSONRegionContexts.JSON_S
			// || type == JSONRegionContexts.JSON_COMMENT) {
			// regions.remove(region);
			// } else {
			break;
			// }
		}
	}

	public static boolean isJSONSimpleValue(String regionType) {
		return (regionType == JSONRegionContexts.JSON_VALUE_BOOLEAN
				|| regionType == JSONRegionContexts.JSON_VALUE_NULL
				|| regionType == JSONRegionContexts.JSON_VALUE_NUMBER || regionType == JSONRegionContexts.JSON_VALUE_STRING);
	}

	public static boolean isStartJSONStructure(String regionType) {
		return (regionType == JSONRegionContexts.JSON_ARRAY_OPEN || regionType == JSONRegionContexts.JSON_OBJECT_OPEN);
	}

	public static boolean isEndJSONStructure(String regionType) {
		return (regionType == JSONRegionContexts.JSON_ARRAY_CLOSE || regionType == JSONRegionContexts.JSON_OBJECT_CLOSE);
	}

	public static String getString(IJSONNode node) {
		String value;
		try {
			value = node.getModel().getStructuredDocument().get(node.getStartOffset(), node.getEndOffset()-node.getStartOffset());
		} catch (BadLocationException e) {
			// ignore
			return null;
		}
		value = removeQuote(value);
		return value;
	}

	public static String removeQuote(String value) {
		if (value != null) {
			value = value.trim();
			if (value.startsWith(QUOTE)) {
				value = value.substring(1);
			}
			if (value.endsWith(QUOTE)) {
				value = value.substring(0, value.length() - 1);
			}
		}
		return value;
	}
}
