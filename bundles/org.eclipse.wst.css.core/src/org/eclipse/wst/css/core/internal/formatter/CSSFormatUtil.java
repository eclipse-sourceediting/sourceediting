/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.css.core.adapters.ICSSModelAdapter;
import org.eclipse.wst.css.core.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


public class CSSFormatUtil {
	public List collectCSSNodes(IStructuredModel model, int start, int length) {
		List nodes = new ArrayList();

		IndexedRegion startNode = model.getIndexedRegion(start);
		IndexedRegion endNode = model.getIndexedRegion(start + length - 1);

		if (startNode == null || endNode == null) {
			return nodes;
		}

		if (model instanceof ICSSModel && startNode instanceof ICSSNode && endNode instanceof ICSSNode) {
			// CSS model
			ICSSNode ca = getCommonAncestor((ICSSNode) startNode, (ICSSNode) endNode);
			if (ca != null) {
				for (ICSSNode node = ca.getFirstChild(); node != null && start + length < ((IndexedRegion) node).getStartOffset(); node = node.getNextSibling()) {
					if (start < ((IndexedRegion) node).getEndOffset()) {
						nodes.add(node);
					}
				}
			}
		}
		else if (model instanceof DOMModel && startNode instanceof DOMNode && endNode instanceof DOMNode) {
			if (startNode instanceof Text) {
				startNode = (IndexedRegion) ((Text) startNode).getParentNode();
			}
			if (endNode instanceof Text) {
				endNode = (IndexedRegion) ((Text) endNode).getParentNode();
			}
			// HTML model, maybe
			DOMNode ca = (DOMNode) getCommonAncestor((Node) startNode, (Node) endNode);
			findCSS(nodes, ca);
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

	private void findCSS(List cssNodes, DOMNode node) {
		ICSSModelAdapter adapter;
		adapter = (ICSSModelAdapter) node.getAdapterFor(IStyleSheetAdapter.class);
		if (adapter != null) {
			ICSSModel model = adapter.getModel();
			if (model != null && model.getStyleSheetType() == ICSSModel.EMBEDDED) {
				cssNodes.add(model.getDocument());
			}
		}
		else {
			adapter = (ICSSModelAdapter) node.getAdapterFor(IStyleDeclarationAdapter.class);
			if (adapter != null) {
				ICSSModel model = adapter.getModel();
				if (model != null && model.getStyleSheetType() == ICSSModel.INLINE) {
					cssNodes.add(model.getDocument());
				}
			}
		}

		for (DOMNode child = (DOMNode) node.getFirstChild(); child != null; child = (DOMNode) child.getNextSibling()) {
			findCSS(cssNodes, child);
		}
	}

	private ICSSNode getCommonAncestor(ICSSNode nodeA, ICSSNode nodeB) {
		if (nodeA == null || nodeB == null) {
			return null;
		}

		for (ICSSNode na = nodeA; na != null; na = na.getParentNode()) {
			for (ICSSNode ta = nodeB; ta != null; ta = ta.getParentNode()) {
				if (ta == na) {
					return ta;
				}
			}
		}

		return null; // not found
	}

	/**
	 */
	public void replaceSource(IStructuredModel model, int offset, int length, String source) {
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		if (offset >= 0 && length >= 0 && offset + length <= structuredDocument.getLength()) {
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
			structuredDocument.replaceText(structuredDocument, offset, length, source);
		}
	}

	public synchronized static CSSFormatUtil getInstance() {
		if (fInstance == null) {
			fInstance = new CSSFormatUtil();
		}
		return fInstance;
	}

	private CSSFormatUtil() {
		super();
	}

	private static CSSFormatUtil fInstance;
}