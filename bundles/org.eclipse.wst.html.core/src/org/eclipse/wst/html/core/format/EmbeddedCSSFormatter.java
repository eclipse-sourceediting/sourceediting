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
package org.eclipse.wst.html.core.format;



import org.eclipse.wst.css.core.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.format.CSSSourceFormatter;
import org.eclipse.wst.html.core.HTMLFormatContraints;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.document.XMLText;

// nakamori_TODO: check and remove

public class EmbeddedCSSFormatter extends HTMLFormatter {

	//private IAdapterFactory factory = new CSSSourceFormatterFactory(CSSSourceFormatter.class, true);
	/**
	 */
	protected EmbeddedCSSFormatter() {
		super();
	}

	/**
	 */
	protected void formatNode(XMLNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		XMLText text = (XMLText) node;

		String source = getCSSContent(node);
		if (source == null) { // fallback
			source = text.getSource();
		}

		int offset = text.getStartOffset();
		int length = text.getEndOffset() - offset;
		replaceSource(text.getModel(), offset, length, source);
		setWidth(contraints, source);
	}

	/**
	 */
	private String getCSSContent(XMLNode text) {
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
		StringBuffer buffer = formatter.format(document);
		if (buffer == null)
			return null;
		return buffer.toString();
	}

	/**
	 */
	private ICSSModel getCSSModel(XMLNode text) {
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