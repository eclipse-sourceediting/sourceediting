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
package org.eclipse.wst.html.core.internal.cleanup;



import org.eclipse.wst.css.core.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.format.CSSSourceFormatter;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.w3c.dom.Node;

// nakamori_TODO: check and remove

public class CSSTextNodeCleanupHandler extends AbstractNodeCleanupHandler {

	public Node cleanup(Node node) {
		if (node == null)
			return node;
		DOMModel model = ((DOMNode) node).getModel();
		if (model == null)
			return node;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return node;

		String content = getCSSContent(node);
		if (content == null)
			return node;

		int offset = ((DOMNode) node).getStartOffset();
		int length = ((DOMNode) node).getEndOffset() - offset;
		replaceSource(model, this, offset, length, content);
		return (DOMNode) model.getIndexedRegion(offset);
	}

	/**
	 */
	private String getCSSContent(Node text) {
		ICSSModel model = getCSSModel(text);
		if (model == null)
			return null;
		ICSSNode document = model.getDocument();
		if (document == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) document;
		INodeAdapter adapter = notifier.getAdapterFor(CSSSourceFormatter.class);
		if (adapter == null)
			return null;
		CSSSourceFormatter formatter = (CSSSourceFormatter) adapter;
		StringBuffer buffer = formatter.cleanup(document);
		if (buffer == null)
			return null;
		return buffer.toString();
	}

	/**
	 */
	private ICSSModel getCSSModel(Node text) {
		if (text == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) text.getParentNode();
		if (notifier == null)
			return null;
		INodeAdapter adapter = notifier.getAdapterFor(IStyleSheetAdapter.class);
		if (adapter == null)
			return null;
		if (!(adapter instanceof IStyleSheetAdapter))
			return null;
		IStyleSheetAdapter styleAdapter = (IStyleSheetAdapter) adapter;
		return styleAdapter.getModel();
	}
}