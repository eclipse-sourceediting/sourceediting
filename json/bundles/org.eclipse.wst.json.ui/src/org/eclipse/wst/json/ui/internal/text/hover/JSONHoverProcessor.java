/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal.text.hover;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.core.util.JSONUtil;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.json.ui.internal.text.JSONBrowserInformationControlInput;
import org.eclipse.wst.json.ui.internal.text.JSONHoverControlCreator;
import org.eclipse.wst.json.ui.internal.text.JSONPresenterControlCreator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;

/**
 * Text hover processor for JSON.
 *
 */
public class JSONHoverProcessor extends AbstractHoverProcessor implements
		ITextHoverExtension2, IInformationProviderExtension2 {

	private IInformationControlCreator hoverControlCreator;
	private IInformationControlCreator presenterControlCreator;

	@Override
	public String getHoverInfo(ITextViewer viewer, IRegion hoverRegion) {
		JSONBrowserInformationControlInput info = (JSONBrowserInformationControlInput) getHoverInfo2(
				viewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	@Override
	public Object getHoverInfo2(ITextViewer viewer, IRegion hoverRegion) {
		if ((hoverRegion == null) || (viewer == null)
				|| (viewer.getDocument() == null)) {
			return null;
		}
		int documentOffset = hoverRegion.getOffset();
		String html = computeHoverHelp(viewer, documentOffset);
		if (html != null) {
			return new JSONBrowserInformationControlInput(null, html, 20);
		}
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null)) {
			return null;
		}

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer
				.getDocument()).getRegionAtCharacterOffset(offset);
		ITextRegion region = null;

		if (flatNode != null) {
			region = flatNode.getRegionAtCharacterOffset(offset);
		}
		if (region != null) {
			// only supply hoverhelp for object key, or simple JSON value
			String regionType = region.getType();
			if ((regionType == JSONRegionContexts.JSON_OBJECT_KEY)
					|| JSONUtil.isJSONSimpleValue(regionType)) {
				try {
					// check if we are at whitespace before or after line
					IRegion line = textViewer.getDocument()
							.getLineInformationOfOffset(offset);
					if ((offset > (line.getOffset()))
							&& (offset < (line.getOffset() + line.getLength()))) {
						// check if we are in region's trailing whitespace
						// (whitespace after relevant info)
						if (offset < flatNode.getTextEndOffset(region)) {
							return new Region(flatNode.getStartOffset(region),
									region.getTextLength());
						}
					}
				} catch (BadLocationException e) {
					Logger.logException(e);
				}
			}
		}
		return null;
	}

	/**
	 * Retrieves documentation to display in the hover help popup.
	 * 
	 * @return String any documentation information to display <code>null</code>
	 *         if there is nothing to display.
	 * 
	 */
	protected String computeHoverHelp(ITextViewer textViewer,
			int documentPosition) {
		String result = null;

		IndexedRegion treeNode = ContentAssistUtils.getNodeAt(textViewer,
				documentPosition);
		if (treeNode == null) {
			return null;
		}
		IJSONNode node = (IJSONNode) treeNode;
		IJSONNode parentNode = node.getParentNode();

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer
				.getDocument()).getRegionAtCharacterOffset(documentPosition);
		if (flatNode != null) {
			ITextRegion region = flatNode
					.getRegionAtCharacterOffset(documentPosition);
			if (region != null) {
				result = computeRegionHelp(treeNode, parentNode, flatNode,
						region);
			}
		}

		return result;
	}

	/**
	 * Computes the hoverhelp based on region
	 * 
	 * @return String hoverhelp
	 */
	protected String computeRegionHelp(IndexedRegion treeNode,
			IJSONNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		if (region == null) {
			return null;
		}
		String regionType = region.getType();
		if (treeNode instanceof IJSONPair && regionType == JSONRegionContexts.JSON_OBJECT_KEY) {
			return computeObjectKeyHelp((IJSONPair) treeNode, parentNode,
					flatNode, region);
		}
		if (treeNode instanceof IJSONValue && JSONUtil.isJSONSimpleValue(regionType)) {
			return computeValueHelp((IJSONValue) treeNode, parentNode,
					flatNode, region);
		}
		return null;
	}

	protected String computeObjectKeyHelp(IJSONPair treeNode,
			IJSONNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		return HTMLJSONPrinter.getAdditionalProposalInfo(treeNode);
	}

	protected String computeValueHelp(IJSONValue treeNode,
			IJSONNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		return HTMLJSONPrinter.getAdditionalProposalInfo(treeNode);
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (hoverControlCreator == null)
			hoverControlCreator = new JSONHoverControlCreator(
					getInformationPresenterControlCreator());
		return hoverControlCreator;
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (presenterControlCreator == null)
			presenterControlCreator = new JSONPresenterControlCreator();
		return presenterControlCreator;
	}

}
