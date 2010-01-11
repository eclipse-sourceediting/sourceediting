/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.cleanup;



import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

// nakamori_TODO: check and remove

public class CSSTextNodeCleanupHandler extends AbstractNodeCleanupHandler {

	public Node cleanup(Node node) {
		if (node == null)
			return node;
		IDOMModel model = ((IDOMNode) node).getModel();
		if (model == null)
			return node;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return node;
		if ( !getCleanupPreferences().getFormatSource())
			return node;
		
		String content = getCSSContent(node);
		if (content == null)
			return node;

		int offset = ((IDOMNode) node).getStartOffset();
		int length = ((IDOMNode) node).getEndOffset() - offset;
		replaceSource(model, this, offset, length, content);
		return (IDOMNode) model.getIndexedRegion(offset);
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
		CSSSourceFormatter formatter = (CSSSourceFormatter) notifier.getAdapterFor(CSSSourceFormatter.class);
		// try another way to get formatter
		if (formatter == null)
			formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(notifier);
		if (formatter == null)
			return null;
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