/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * @author dcarver
 *
 */
public class XSLVariableCustomCompletionProposal extends CustomCompletionProposal {

	/**
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 * @param relevance
	 * @param updateReplacementLengthOnValidate
	 */
	public XSLVariableCustomCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance,
			boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance,
				updateReplacementLengthOnValidate);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 * @param relevance
	 */
	public XSLVariableCustomCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance);
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * Create a positional based Proposal and replace the text at that position.
	 * @see org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal#apply(org.eclipse.jface.text.ITextViewer, char, int, int)
	 */
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		// TODO Auto-generated method stub
//		super.apply(viewer, trigger, stateMask, offset);
		IStructuredDocument document = (IStructuredDocument)viewer.getDocument();
		Position position = new Position(offset);
	    int currentPosition =  getCursorPosition();
	    int startOffset = document.getRegionAtCharacterOffset(offset).getStart();
	    int existingLength = offset - startOffset;
	    
//		DC: Allows for replacement of text typed to this point.		
//	    try {
//			int caretOffset = viewer.getTextWidget().getCaretOffset();
//			if (viewer instanceof ITextViewerExtension5) {
//				ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
//				caretOffset = extension.widgetOffset2ModelOffset(caretOffset);
//			}
//			else {
//				caretOffset = viewer.getTextWidget().getCaretOffset() + viewer.getVisibleRegion().getOffset();
//			}
//
//
//			int endOffsetOfChanges = getReplacementString().length() + getReplacementOffset();
//			// Insert the portion of the new text that comes after the
//			// current caret position
//			if (endOffsetOfChanges >= caretOffset) {
//				int postCaretReplacementLength = getReplacementOffset() + getReplacementLength() - caretOffset;
//				int preCaretReplacementLength = getReplacementString().length() - (endOffsetOfChanges - caretOffset);
//				if (postCaretReplacementLength < 0) {
//					if (Debug.displayWarnings) {
//						System.out.println("** postCaretReplacementLength was negative: " + postCaretReplacementLength); //$NON-NLS-1$
//					}
//					// This is just a quick fix while I figure out what
//					// replacement length is supposed to be
//					// in each case, otherwise we'll get negative
//					// replacment length sometimes
//					postCaretReplacementLength = 0;
//				}
//				document.replace(caretOffset, postCaretReplacementLength, getReplacementString().substring(preCaretReplacementLength));
//			}
//			// Insert the portion of the new text that comes before the
//			// current caret position
//			// Done second since offsets would change for the post text
//			// otherwise
//			// Outright insertions are handled here
//			if (caretOffset > getReplacementOffset()) {
//				int preCaretTextLength = caretOffset - getReplacementOffset();
//				document.replace(getReplacementOffset(), preCaretTextLength, getReplacementString().substring(0, preCaretTextLength));
//			}
//		}
//		catch (BadLocationException x) {
//			apply(document);
//		}
//		catch (StringIndexOutOfBoundsException e) {
//			apply(document);
//		}
//
	    
		PositionBasedCompletionProposal proposal = 
			new PositionBasedCompletionProposal(getReplacementString(), position, existingLength + getReplacementString().length());
		proposal.apply(document);
//	    apply(document);
	}
}
