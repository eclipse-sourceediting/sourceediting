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
package org.eclipse.wst.css.core.internal.cleanup;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.css.core.internal.formatter.CSSFormatUtil;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;


public class CleanupProcessorCSS extends AbstractStructuredCleanupProcessor {

	public void cleanupModel(IStructuredModel structuredModel, int start, int length) {
		CSSFormatUtil formatUtil = CSSFormatUtil.getInstance();
		if (structuredModel instanceof ICSSModel) {
			ICSSDocument doc = ((ICSSModel) structuredModel).getDocument();
			CSSSourceFormatter formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) doc);
			StringBuffer buf = formatter.cleanup(doc);
			if (buf != null) {
				int startOffset = ((IndexedRegion) doc).getStartOffset();
				int endOffset = ((IndexedRegion) doc).getEndOffset();
				formatUtil.replaceSource(doc.getModel(), startOffset, endOffset - startOffset, buf.toString());
			}
		}
		else if (structuredModel instanceof IDOMModel) {
			List cssnodes = formatUtil.collectCSSNodes(structuredModel, start, length);
			if (cssnodes != null && !cssnodes.isEmpty()) {
				ICSSModel model = null;
				for (int i = 0; i < cssnodes.size(); i++) {
					ICSSNode node = (ICSSNode) cssnodes.get(i);
					CSSSourceFormatter formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) node);
					StringBuffer buf = formatter.cleanup(node);
					if (buf != null) {
						int startOffset = ((IndexedRegion) node).getStartOffset();
						int endOffset = ((IndexedRegion) node).getEndOffset();
						if (model == null) {
							model = node.getOwnerDocument().getModel();
						}
						formatUtil.replaceSource(model, startOffset, endOffset - startOffset, buf.toString());
					}
				}
			}
		}
	}

	protected String getContentType() {
		return ContentTypeIdForCSS.ContentTypeID_CSS;
	}

	public void cleanupModel(IStructuredModel structuredModel) {
		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();

		cleanupModel(structuredModel, start, length);
	}

	public void cleanupDocument(IDocument document) throws IOException, CoreException {
		// TODO should implement, or delete?

	}

	public void cleanupDocument(IDocument document, int start, int length) throws IOException, CoreException {
		// TODO should implement, or delete?

	}

	protected IStructuredCleanupHandler getCleanupHandler(Node node) {
		return null;
	}

	protected IStructuredFormatProcessor getFormatProcessor() {
		return null;
	}

	protected void refreshCleanupPreferences() {
	}
}
