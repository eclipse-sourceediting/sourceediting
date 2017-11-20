/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.JSONFormatUtil
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.Node;

public class JSONFormatUtil {

	public List collectJSONNodes(IStructuredModel model, int start, int length) {
		List nodes = new ArrayList();

		IndexedRegion startNode = model.getIndexedRegion(start);
		IndexedRegion endNode = model.getIndexedRegion(start + length - 1);

		if (startNode == null || endNode == null) {
			return nodes;
		}

		if (model instanceof IJSONModel && startNode instanceof IJSONNode
				&& endNode instanceof IJSONNode) {
			// JSON model
			IJSONNode ca = getCommonAncestor((IJSONNode) startNode,
					(IJSONNode) endNode);
			if (ca != null) {
				for (IJSONNode node = ca.getFirstChild(); node != null
						&& start + length < ((IndexedRegion) node)
								.getStartOffset(); node = node.getNextSibling()) {
					if (start < ((IndexedRegion) node).getEndOffset()) {
						nodes.add(node);
					}
				}
			}
		}
		return nodes;
	}

	/**
	 * getCommonAncestor method
	 * 
	 * @return org.w3c.dom.Node
	 * @param node
	 *            org.w3c.dom.Node
	 */
	private Node getCommonAncestor(Node node1, Node node2) {
		if (node1 == null || node2 == null)
			return null;

		for (Node na = node2; na != null; na = na.getParentNode()) {
			for (Node ta = node1; ta != null; ta = ta.getParentNode()) {
				if (ta == na)
					return ta;
			}
		}
		return null; // not found
	}

	private IJSONNode getCommonAncestor(IJSONNode nodeA, IJSONNode nodeB) {
		if (nodeA == null || nodeB == null) {
			return null;
		}

		for (IJSONNode na = nodeA; na != null; na = na.getParentNode()) {
			for (IJSONNode ta = nodeB; ta != null; ta = ta.getParentNode()) {
				if (ta == na) {
					return ta;
				}
			}
		}

		return null; // not found
	}

	/**
	 */
	public void replaceSource(IStructuredModel model, int offset, int length,
			String source) {
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		if (offset >= 0 && length >= 0
				&& offset + length <= structuredDocument.getLength()) {
			if (structuredDocument.containsReadOnly(offset, length))
				return;
			if (source == null)
				source = new String();
			// We use 'structuredDocument' as the requester object just so
			// this and the other
			// format-related 'repalceText' (in replaceSource) can use the
			// same requester.
			// Otherwise, if requester is not identical,
			// the undo group gets "broken" into multiple pieces based
			// on the requesters being different. Technically, any unique,
			// common
			// requester object would work.
			structuredDocument.replaceText(structuredDocument, offset, length,
					source);
		}
	}

	public synchronized static JSONFormatUtil getInstance() {
		if (fInstance == null) {
			fInstance = new JSONFormatUtil();
		}
		return fInstance;
	}

	private JSONFormatUtil() {
		super();
	}

	private static JSONFormatUtil fInstance;
}
