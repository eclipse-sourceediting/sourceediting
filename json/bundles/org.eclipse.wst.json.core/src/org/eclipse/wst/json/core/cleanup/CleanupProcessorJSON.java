/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.cleanup.CleanupProcessorCSS
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.cleanup;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.internal.format.IJSONSourceFormatter;
import org.eclipse.wst.json.core.internal.format.JSONFormatUtil;
import org.eclipse.wst.json.core.internal.format.JSONSourceFormatterFactory;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.w3c.dom.Node;

public class CleanupProcessorJSON extends AbstractStructuredCleanupProcessor {

	public void cleanupModel(IStructuredModel structuredModel, int start,
			int length) {
		JSONFormatUtil formatUtil = JSONFormatUtil.getInstance();
		if (structuredModel instanceof IJSONModel) {
			IJSONDocument doc = ((IJSONModel) structuredModel).getDocument();
			IJSONSourceFormatter formatter = JSONSourceFormatterFactory
					.getInstance().getSourceFormatter((INodeNotifier) doc);
			StringBuilder buf = formatter.cleanup(doc);
			if (buf != null) {
				int startOffset = ((IndexedRegion) doc).getStartOffset();
				int endOffset = ((IndexedRegion) doc).getEndOffset();
				formatUtil.replaceSource(doc.getModel(), startOffset, endOffset
						- startOffset, buf.toString());
			}
		}
	}

	protected String getContentType() {
		return ContentTypeIdForJSON.ContentTypeID_JSON;
	}

	public void cleanupModel(IStructuredModel structuredModel) {
		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();

		cleanupModel(structuredModel, start, length);
	}

	public void cleanupDocument(IDocument document) throws IOException,
			CoreException {
		// TODO should implement, or delete?

	}

	public void cleanupDocument(IDocument document, int start, int length)
			throws IOException, CoreException {
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
