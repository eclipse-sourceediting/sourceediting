/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.correction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.w3c.dom.Node;

public class SurroundWithNewElementQuickAssistProposal extends RenameInFileQuickAssistProposal {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(org.eclipse.jface.text.ITextViewer,
	 *      char, int, int)
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		try {
			int startTagOffset = offset;
			int endTagOffset = offset + viewer.getSelectedRange().y;

			// surround the node if no selection
			if (startTagOffset == endTagOffset) {
				IDOMNode cursorNode = (IDOMNode) ContentAssistUtils.getNodeAt(viewer, offset);
				// use parent node if text node is empty
				if ((cursorNode.getNodeType() == Node.TEXT_NODE) && (cursorNode.getNodeValue().trim().length() == 0)) {
					cursorNode = (IDOMNode) cursorNode.getParentNode();
				}

				startTagOffset = cursorNode.getStartOffset();
				endTagOffset = cursorNode.getEndOffset();
			}

			// insert new element
			MultiTextEdit multiTextEdit = new MultiTextEdit();
			// element tag name cannot be DBCS, do not translate "<element>"
			// and "</element>"
			multiTextEdit.addChild(new InsertEdit(startTagOffset, "<element>")); //$NON-NLS-1$
			multiTextEdit.addChild(new InsertEdit(endTagOffset, "</element>")); //$NON-NLS-1$
			multiTextEdit.apply(viewer.getDocument());

			// get new element node
			IDOMNode newElementNode = (IDOMNode) ContentAssistUtils.getNodeAt(viewer, startTagOffset);

			// format new element
			IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
			formatProcessor.formatNode(newElementNode);

			// rename new element
			super.apply(viewer, trigger, stateMask, newElementNode.getStartOffset() + 1);
		}
		catch (MalformedTreeException e) {
			// log for now, unless find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		catch (BadLocationException e) {
			// log for now, unless find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return XMLUIMessages.SurroundWithNewElementQuickAssistProposal_0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return XMLUIMessages.SurroundWithNewElementQuickAssistProposal_1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		// return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);
		return XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ADD_CORRECTION);
	}
}
