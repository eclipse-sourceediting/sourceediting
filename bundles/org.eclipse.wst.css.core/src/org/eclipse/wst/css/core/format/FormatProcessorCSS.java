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
package org.eclipse.wst.css.core.format;

import java.util.List;

import org.eclipse.wst.css.core.document.ICSSDocument;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.internal.formatter.CSSFormatUtil;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.w3c.dom.Node;


public class FormatProcessorCSS extends AbstractStructuredFormatProcessor {


	protected String getFileExtension() {
		return "css"; //$NON-NLS-1$
	}

	public void formatModel(IStructuredModel structuredModel) {
		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();

		formatModel(structuredModel, start, length);
	}

	public void formatModel(IStructuredModel structuredModel, int start, int length) {
		CSSFormatUtil formatUtil = CSSFormatUtil.getInstance();
		if (structuredModel instanceof ICSSModel) {
			ICSSDocument doc = ((ICSSModel) structuredModel).getDocument();
			CSSSourceFormatter formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) doc);
			StringBuffer buf = formatter.format(doc);
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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.format.AbstractStructuredFormatProcessor#getFormatPreferences()
	 */
	public IStructuredFormatPreferences getFormatPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.format.AbstractStructuredFormatProcessor#getFormatter(org.w3c.dom.Node)
	 */
	protected IStructuredFormatter getFormatter(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.format.AbstractStructuredFormatProcessor#refreshFormatPreferences()
	 */
	protected void refreshFormatPreferences() {
		// TODO Auto-generated method stub

	}
}