/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.internal.correction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


public class RenameInFileQuickAssistProposal implements ICompletionProposal, ICompletionProposalExtension2 {
	protected IRegion fSelectedRegion; // initialized by apply()

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return ResourceHandler.getString("RenameInFileQuickAssistProposal.0"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return ResourceHandler.getString("RenameInFileQuickAssistProposal.1"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		// return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);
		return XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_LOCAL_VARIABLE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(org.eclipse.jface.text.ITextViewer, char, int, int)
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		IDocument document = viewer.getDocument();
		LinkedPositionGroup group = new LinkedPositionGroup();
		try {
			if (viewer instanceof StructuredTextViewer) {
				XMLNode node = (XMLNode) ContentAssistUtils.getNodeAt((StructuredTextViewer) viewer, offset);
				IStructuredDocumentRegion startStructuredDocumentRegion = node.getStartStructuredDocumentRegion();
				ITextRegion region = (startStructuredDocumentRegion == null) ? null : startStructuredDocumentRegion.getRegionAtCharacterOffset(offset);
				if (region != null) {
					group.addPosition(new LinkedPosition(document, startStructuredDocumentRegion.getStartOffset() + region.getStart(), region.getTextLength(), 0));

					if (region.getType() == XMLRegionContext.XML_TAG_NAME && node.getEndStructuredDocumentRegion() != null) {
						region = node.getEndStructuredDocumentRegion().getRegions().get(1);
						if (region != null)
							group.addPosition(new LinkedPosition(document, node.getEndStructuredDocumentRegion().getStartOffset() + region.getStart(), region.getTextLength(), 1));
					}
				}
				else {
					IStructuredDocumentRegion endStructuredDocumentRegion = node.getEndStructuredDocumentRegion();
					region = (endStructuredDocumentRegion == null) ? null : endStructuredDocumentRegion.getRegionAtCharacterOffset(offset);
					if (region != null) {
						if (region.getType() == XMLRegionContext.XML_TAG_NAME && node.getStartStructuredDocumentRegion() != null) {
							ITextRegion startTagNameRegion = node.getStartStructuredDocumentRegion().getRegions().get(1);
							if (region != null) {
								group.addPosition(new LinkedPosition(document, node.getStartStructuredDocumentRegion().getStartOffset() + startTagNameRegion.getStart(), startTagNameRegion.getTextLength(), 0));
								group.addPosition(new LinkedPosition(document, endStructuredDocumentRegion.getStartOffset() + region.getStart(), region.getTextLength(), 1));
							}
						}
						else
							group.addPosition(new LinkedPosition(document, endStructuredDocumentRegion.getStartOffset() + region.getStart(), region.getTextLength(), 0));
					}
				}

				// TODO CompletionProposalPopup#insertProposal() calls IRewriteTarget.beginCompoundChange()
				//      which disables redraw in ITextViewer. Workaround for now.
				((StructuredTextViewer) viewer).setRedraw(true);
			}

			LinkedModeModel linkedModeModel = new LinkedModeModel();
			linkedModeModel.addGroup(group);
			linkedModeModel.forceInstall();

			LinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
			ui.setExitPosition(viewer, offset, 0, LinkedPositionGroup.NO_STOP);
			ui.enter();

			fSelectedRegion = ui.getSelectedRegion();
		}
		catch (BadLocationException e) {
			throw new SourceEditingRuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer, boolean)
	 */
	public void selected(ITextViewer viewer, boolean smartToggle) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
	 */
	public void unselected(ITextViewer viewer) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument, int, org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		return false;
	}

}
