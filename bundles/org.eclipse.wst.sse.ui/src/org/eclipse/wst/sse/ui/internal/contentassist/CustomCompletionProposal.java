/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.contentassist;



import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.ui.contentassist.IRelevanceCompletionProposal;


/**
 * An implementation of ICompletionProposal whose values can be read after
 * creation.
 */
public class CustomCompletionProposal implements ICompletionProposal, ICompletionProposalExtension, ICompletionProposalExtension2, IRelevanceCompletionProposal {
	protected String fAdditionalProposalInfo;
	protected IContextInformation fContextInformation;
	protected int fCursorPosition = 0;
	protected String fDisplayString;
	protected Image fImage;
	protected int fOriginalReplacementLength;

	protected CompletionProposal fProposal = null;
	protected int fRelevance = IRelevanceConstants.R_NONE;
	protected int fReplacementLength = 0;
	protected int fReplacementOffset = 0;
	protected String fReplacementString = null;
	private boolean fUpdateLengthOnValidate;

	public CustomCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {

		fProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition);
		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
	}

	public CustomCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo) {

		fProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo);
		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fImage = image;
		fDisplayString = displayString;
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
	}

	// constructor with relevance
	public CustomCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance) {

		fProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo);
		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fImage = image;
		fDisplayString = displayString;
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
		fRelevance = relevance;
	}

	/**
	 * Constructor with relevance and replacement length update flag.
	 * 
	 * If the <code>updateReplacementLengthOnValidate</code> flag is true,
	 * then when the user types, the replacement length will be incremented by
	 * the number of new characters inserted from the original position.
	 * Otherwise the replacement length will not change on validate.
	 * 
	 * ex.
	 * 
	 * <tag |name="attr"> - the replacement length is 4 <tag i|name="attr"> -
	 * the replacement length is now 5 <tag id|name="attr"> - the replacement
	 * length is now 6 <tag |name="attr"> - the replacementlength is now 4
	 * again <tag |name="attr"> - the replacment length remains 4
	 *  
	 */
	public CustomCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {

		fProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo);
		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fImage = image;
		fDisplayString = displayString;
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
		fRelevance = relevance;
		fUpdateLengthOnValidate = updateReplacementLengthOnValidate;
		fOriginalReplacementLength = fReplacementLength;
	}

	// constructor with relevance
	public CustomCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, int relevance) {

		fProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition);
		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fRelevance = relevance;
	}

	public void apply(IDocument document) {
		fProposal.apply(document);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#apply(org.eclipse.jface.text.IDocument,
	 *      char, int)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		// we currently don't do anything special for which character
		// selected the proposal, and where the cursor offset is
		// but we might in the future...
		fProposal.apply(document);
		// we want to ContextInformationPresenter.updatePresentation() here
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		IDocument document = viewer.getDocument();
		// CMVC 252634 to compensate for "invisible" initial region
		int caretOffset = viewer.getTextWidget().getCaretOffset();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			caretOffset = extension.widgetOffset2ModelOffset(caretOffset);
		} else {
			caretOffset = viewer.getTextWidget().getCaretOffset() + viewer.getVisibleRegion().getOffset();
		}

		if (caretOffset == getReplacementOffset()) {
			apply(document);
		} else {
			// replace the text without affecting the caret Position as this
			// causes the cursor to move on its own
			try {
				int endOffsetOfChanges = getReplacementString().length() + getReplacementOffset();
				// Insert the portion of the new text that comes after the
				// current caret position
				if (endOffsetOfChanges >= caretOffset) {
					int postCaretReplacementLength = getReplacementOffset() + getReplacementLength() - caretOffset;
					int preCaretReplacementLength = getReplacementString().length() - (endOffsetOfChanges - caretOffset);
					if (postCaretReplacementLength < 0) {
						if (Debug.displayWarnings) {
							System.out.println("** postCaretReplacementLength was negative: " + postCaretReplacementLength); //$NON-NLS-1$
						}
						// This is just a quick fix while I figure out what
						// replacement length is supposed to be
						// in each case, otherwise we'll get negative
						// replacment length sometimes
						postCaretReplacementLength = 0;
					}
					document.replace(caretOffset, postCaretReplacementLength, getReplacementString().substring(preCaretReplacementLength));
				}
				// Insert the portion of the new text that comes before the
				// current caret position
				// Done second since offsets would change for the post text
				// otherwise
				// Outright insertions are handled here
				if (caretOffset > getReplacementOffset()) {
					int preCaretTextLength = caretOffset - getReplacementOffset();
					document.replace(getReplacementOffset(), preCaretTextLength, getReplacementString().substring(0, preCaretTextLength));
				}
			} catch (BadLocationException x) {
				apply(document);
			} catch (StringIndexOutOfBoundsException e) {
				apply(document);
			}
		}
	}

	public String getAdditionalProposalInfo() {
		return fProposal.getAdditionalProposalInfo();
	}

	public IContextInformation getContextInformation() {
		return fProposal.getContextInformation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition() {
		return getCursorPosition();
	}

	public int getCursorPosition() {
		return fCursorPosition;
	}

	public String getDisplayString() {
		return fProposal.getDisplayString();
	}

	public Image getImage() {
		return fProposal.getImage();
	}

	public int getRelevance() {
		return fRelevance;
	}

	public int getReplacementLength() {
		return fReplacementLength;
	}

	public int getReplacementOffset() {
		return fReplacementOffset;
	}

	public String getReplacementString() {
		return fReplacementString;
	}

	public Point getSelection(IDocument document) {
		return fProposal.getSelection(document);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getTriggerCharacters()
	 */
	public char[] getTriggerCharacters() {
		// we currently don't pay attention to which charaters select the
		// proposal
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#isValidFor(org.eclipse.jface.text.IDocument,
	 *      int)
	 */
	public boolean isValidFor(IDocument document, int offset) {
		return validate(document, offset, null);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer,
	 *      boolean)
	 */
	public void selected(ITextViewer viewer, boolean smartToggle) {
	}

	// code is borrowed from JavaCompletionProposal
	protected boolean startsWith(IDocument document, int offset, String word) {
		int wordLength = word == null ? 0 : word.length();
		if (offset > fReplacementOffset + wordLength)
			return false;

		try {
			int length = offset - fReplacementOffset;
			String start = document.get(fReplacementOffset, length);
			return word.substring(0, length).equalsIgnoreCase(start);
		} catch (BadLocationException x) {
		}

		return false;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
	 */
	public void unselected(ITextViewer viewer) {
	}

	/**
	 * borrowed from JavaCompletionProposal
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument,
	 *      int, org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		if (offset < fReplacementOffset)
			return false;
		boolean validated = startsWith(document, offset, fDisplayString);
		// CMVC 269884
		if (fUpdateLengthOnValidate) {
			int delta = offset - fReplacementOffset;
			if (delta > 0)
				fReplacementLength = delta + fOriginalReplacementLength;
		}
		return validated;
	}
}
