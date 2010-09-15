/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.format;

import java.util.List;

import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.css.core.internal.formatter.CSSFormatUtil;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;


public class FormatProcessorCSS extends AbstractStructuredFormatProcessor {
	/*
	 * Max length of text to be formatted to be considered a "small change"
	 * Used for document rewrite session type.
	 */
	private final int MAX_SMALL_FORMAT_SIZE = 1000;

	protected String getFileExtension() {
		return "css"; //$NON-NLS-1$
	}

	public void formatModel(IStructuredModel structuredModel, int start, int length) {
		CSSFormatUtil formatUtil = CSSFormatUtil.getInstance();
		if (structuredModel instanceof ICSSModel) {
			// BUG102822 take advantage of IDocumentExtension4
			IDocumentExtension4 docExt4 = null;
			if (structuredModel.getStructuredDocument() instanceof IDocumentExtension4) {
				docExt4 = (IDocumentExtension4) structuredModel.getStructuredDocument();
			}
			DocumentRewriteSession rewriteSession = null;

			try {
				DocumentRewriteSessionType rewriteType = (length > MAX_SMALL_FORMAT_SIZE) ? DocumentRewriteSessionType.UNRESTRICTED : DocumentRewriteSessionType.UNRESTRICTED_SMALL;
				rewriteSession = (docExt4 == null || docExt4.getActiveRewriteSession() != null) ? null : docExt4.startRewriteSession(rewriteType);

				ICSSDocument doc = ((ICSSModel) structuredModel).getDocument();
				IStructuredDocumentRegion startRegion = 
					structuredModel.getStructuredDocument().getRegionAtCharacterOffset(start);
				IStructuredDocumentRegion endRegion = 
					structuredModel.getStructuredDocument().getRegionAtCharacterOffset(start + length);
				start = startRegion.getStart();
				CSSSourceFormatter formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) doc);
				StringBuffer buf = formatter.format(doc, new Region(start, (endRegion.getEnd() - start)));
				if (buf != null) {
					formatUtil.replaceSource(doc.getModel(), start, endRegion.getEnd() - start, buf.toString());
				}
			}
			finally {
				// BUG102822 take advantage of IDocumentExtension4
				if (docExt4 != null && rewriteSession != null)
					docExt4.stopRewriteSession(rewriteSession);
			}
		}
		else if (structuredModel instanceof IDOMModel) {
			List cssnodes = formatUtil.collectCSSNodes(structuredModel, start, length);
			if (cssnodes != null && !cssnodes.isEmpty()) {
				ICSSModel model = null;

				// BUG102822 take advantage of IDocumentExtension4
				IDocumentExtension4 docExt4 = null;
				if (structuredModel.getStructuredDocument() instanceof IDocumentExtension4) {
					docExt4 = (IDocumentExtension4) structuredModel.getStructuredDocument();
				}
				DocumentRewriteSession rewriteSession = null;

				try {
					DocumentRewriteSessionType rewriteType = (length > MAX_SMALL_FORMAT_SIZE) ? DocumentRewriteSessionType.UNRESTRICTED : DocumentRewriteSessionType.UNRESTRICTED_SMALL;
					rewriteSession = (docExt4 == null || docExt4.getActiveRewriteSession() != null) ? null : docExt4.startRewriteSession(rewriteType);

					for (int i = 0; i < cssnodes.size(); i++) {
						ICSSNode node = (ICSSNode) cssnodes.get(i);
						CSSSourceFormatter formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) node);
						StringBuffer buf = formatter.format(node);
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
				finally {
					// BUG102822 take advantage of IDocumentExtension4
					if (docExt4 != null && rewriteSession != null)
						docExt4.stopRewriteSession(rewriteSession);
				}
			}
		}
	}

	public IStructuredFormatPreferences getFormatPreferences() {
		return null;
	}

	protected IStructuredFormatter getFormatter(Node node) {
		return null;
	}

	protected void refreshFormatPreferences() {
	}
}
