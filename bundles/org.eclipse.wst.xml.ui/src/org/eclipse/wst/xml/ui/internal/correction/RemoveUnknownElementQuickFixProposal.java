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
package org.eclipse.wst.xml.ui.internal.correction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


public class RemoveUnknownElementQuickFixProposal implements ICompletionProposal, ICompletionProposalExtension2 {
	private Object fAdditionalFixInfo = null;
	private String fDisplayString;
	private Image fImage;
	private Point fSelection; // initialized by apply()

	/**
	 * @param additionalFixInfo
	 * @param image
	 * @param string
	 */
	public RemoveUnknownElementQuickFixProposal(Object additionalFixInfo, Image image, String displayString) {
		fAdditionalFixInfo = additionalFixInfo;
		fImage = image;
		fDisplayString = displayString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(org.eclipse.jface.text.ITextViewer,
	 *      char, int, int)
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		int startTagOffset = ((Integer) ((Object[]) fAdditionalFixInfo)[0]).intValue();
		int startTagLength = ((Integer) ((Object[]) fAdditionalFixInfo)[1]).intValue();
		int endTagOffset = ((Integer) ((Object[]) fAdditionalFixInfo)[2]).intValue();
		int endTagLength = ((Integer) ((Object[]) fAdditionalFixInfo)[3]).intValue();

		MultiTextEdit multiTextEdit = new MultiTextEdit();
		if (endTagOffset != -1) {
			multiTextEdit.addChild(new DeleteEdit(endTagOffset, endTagLength));
			fSelection = new Point(endTagOffset, 0);
		}
		if (startTagOffset != -1) {
			multiTextEdit.addChild(new DeleteEdit(startTagOffset, startTagLength));
			fSelection = new Point(startTagOffset, 0);
		}

		try {
			multiTextEdit.apply(viewer.getDocument());
		} catch (MalformedTreeException e) {
			throw new SourceEditingRuntimeException(e);
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (fDisplayString == null)
			fDisplayString = ResourceHandler.getString("QuickFixProcessorXML.11"); //$NON-NLS-1$

		return fDisplayString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return fSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer,
	 *      boolean)
	 */
	public void selected(ITextViewer viewer, boolean smartToggle) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
	 */
	public void unselected(ITextViewer viewer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument,
	 *      int, org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		return false;
	}

}
