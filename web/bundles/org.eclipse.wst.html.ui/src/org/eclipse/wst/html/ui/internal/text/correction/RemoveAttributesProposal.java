/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.text.correction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class RemoveAttributesProposal implements ICompletionProposal {

	private IStructuredDocumentRegion fTag;
	private String fDisplayString;

	public RemoveAttributesProposal(IStructuredDocumentRegion tag, String displayString) {
		fTag = tag;
		fDisplayString = displayString;
	}

	boolean isEndType(String regionType) {
		return regionType.equals(DOMRegionContext.XML_TAG_CLOSE) || regionType.equals(DOMRegionContext.XML_EMPTY_TAG_CLOSE);
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		ITextRegionList regions = fTag.getRegions();
		if (fTag.getNumberOfRegions() > 2) {
			int removalStart = fTag.getTextEndOffset(regions.get(1));
			int removalEnd = fTag.getStartOffset(fTag.getLastRegion());
//			if (isEndType(fTag.getLastRegion().getType())) {
				try {
					document.replace(removalStart, removalEnd - removalStart, "");
				}
				catch (BadLocationException e) {
					Logger.logException(e);
				}
//			}
		}
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return fDisplayString;
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return HTMLEditorPluginImageHelper.getInstance().getImage(HTMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fTag.getEndOffset(fTag.getRegions().get(1)), 0);
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof RemoveAttributesProposal) {
			RemoveAttributesProposal p = (RemoveAttributesProposal) obj;
			return this.fTag.equals(p.fTag);
		}
		return false;
	}
}
