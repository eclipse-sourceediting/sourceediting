/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.format.FormatProcessorCSS
 *                                           modified in order to process JSON Objects.               
 *******************************************************************************/
package org.eclipse.wst.json.core.format;

import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.internal.format.JSONFormatUtil;
import org.eclipse.wst.json.core.internal.format.IJSONSourceFormatter;
import org.eclipse.wst.json.core.internal.format.JSONSourceFormatterFactory;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.w3c.dom.Node;

/**
 * Format processor for JSON.
 *
 */
public class FormatProcessorJSON extends AbstractStructuredFormatProcessor {
	/*
	 * Max length of text to be formatted to be considered a "small change" Used
	 * for document rewrite session type.
	 */
	private final int MAX_SMALL_FORMAT_SIZE = 1000;

	protected String getFileExtension() {
		return "json"; //$NON-NLS-1$
	}

	public void formatModel(IStructuredModel structuredModel, int start,
			int length) {
		JSONFormatUtil formatUtil = JSONFormatUtil.getInstance();
		if (structuredModel instanceof IJSONModel) {
			// BUG102822 take advantage of IDocumentExtension4
			IDocumentExtension4 docExt4 = null;
			if (structuredModel.getStructuredDocument() instanceof IDocumentExtension4) {
				docExt4 = (IDocumentExtension4) structuredModel
						.getStructuredDocument();
			}
			DocumentRewriteSession rewriteSession = null;

			try {
				DocumentRewriteSessionType rewriteType = (length > MAX_SMALL_FORMAT_SIZE) ? DocumentRewriteSessionType.UNRESTRICTED
						: DocumentRewriteSessionType.UNRESTRICTED_SMALL;
				rewriteSession = (docExt4 == null || docExt4
						.getActiveRewriteSession() != null) ? null : docExt4
						.startRewriteSession(rewriteType);

				IJSONDocument doc = ((IJSONModel) structuredModel)
						.getDocument();
				IStructuredDocumentRegion startRegion = structuredModel
						.getStructuredDocument().getRegionAtCharacterOffset(
								start);
				IStructuredDocumentRegion endRegion = structuredModel
						.getStructuredDocument().getRegionAtCharacterOffset(
								start + length);
				if (startRegion != null && endRegion != null) {
					start = startRegion.getStart();
					IJSONSourceFormatter formatter = JSONSourceFormatterFactory
							.getInstance().getSourceFormatter(
									(INodeNotifier) doc);
					StringBuilder buf = formatter.format(doc, new Region(start,
							(endRegion.getEnd() - start)));
					if (buf != null) {
						formatUtil.replaceSource(doc.getModel(), start,
								endRegion.getEnd() - start, buf.toString());
					}
				}
			} finally {
				// BUG102822 take advantage of IDocumentExtension4
				if (docExt4 != null && rewriteSession != null)
					docExt4.stopRewriteSession(rewriteSession);
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
