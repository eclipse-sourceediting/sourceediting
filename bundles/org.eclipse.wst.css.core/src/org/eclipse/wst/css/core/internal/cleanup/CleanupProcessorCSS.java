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
package org.eclipse.wst.css.core.internal.cleanup;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.css.core.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.core.document.ICSSDocument;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.internal.formatter.CSSFormatUtil;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.document.XMLModel;
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
		else if (structuredModel instanceof XMLModel) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.cleanup.IStructuredCleanupProcessor#cleanupDocument(org.eclipse.jface.text.IDocument)
	 */
	public void cleanupDocument(IDocument document) throws IOException, CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.cleanup.IStructuredCleanupProcessor#cleanupDocument(org.eclipse.jface.text.IDocument,
	 *      int, int)
	 */
	public void cleanupDocument(IDocument document, int start, int length) throws IOException, CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.cleanup.AbstractStructuredCleanupProcessor#getCleanupHandler(org.w3c.dom.Node)
	 */
	protected IStructuredCleanupHandler getCleanupHandler(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.cleanup.AbstractStructuredCleanupProcessor#getFormatProcessor()
	 */
	protected IStructuredFormatProcessor getFormatProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.cleanup.AbstractStructuredCleanupProcessor#refreshCleanupPreferences()
	 */
	protected void refreshCleanupPreferences() {
		// TODO Auto-generated method stub

	}
}