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
package org.eclipse.wst.sse.ui.internal.correction;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.StructuredTextViewer;


public class StructuredCorrectionProcessor implements IContentAssistProcessor {
	protected IAnnotationModel fAnnotationModel;
	protected IQuickAssistProcessor fQuickAssistProcessor;
	protected IQuickFixProcessor fQuickFixProcessor;

	public StructuredCorrectionProcessor(ITextEditor editor) {
		IEditorInput input = ((IEditorPart) editor).getEditorInput();
		IAnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(input);

		fAnnotationModel = annotationModel;
	}

	protected void addQuickAssistProposals(StructuredTextViewer viewer, ArrayList proposals, int documentOffset) {
		try {
			IQuickAssistProcessor processor = getQuickAssistProcessor();
			if (processor != null && processor.canAssist(viewer, documentOffset)) {
				ICompletionProposal[] res = processor.getProposals(viewer, documentOffset);
				if (res != null) {
					for (int k = 0; k < res.length; k++) {
						proposals.add(res[k]);
					}
				}
			}
		} catch (Exception e) {
			throw new SourceEditingRuntimeException();
		}
	}

	protected void addQuickFixProposals(StructuredTextViewer viewer, ArrayList proposals, int documentOffset) {
		Iterator iter = fAnnotationModel.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation = (Annotation) iter.next();
			Position pos = fAnnotationModel.getPosition(annotation);
			if (pos != null && documentOffset >= pos.offset && documentOffset <= pos.offset + pos.length) {
				IQuickFixProcessor processor = getQuickFixProcessor();
				if (processor != null && processor.canFix(annotation)) {
					try {
						ICompletionProposal[] res = processor.getProposals(annotation);
						if (res != null) {
							for (int k = 0; k < res.length; k++) {
								proposals.add(res[k]);
							}
						}
					} catch (CoreException e) {
						throw new SourceEditingRuntimeException();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		ArrayList proposals = new ArrayList();

		if (viewer instanceof StructuredTextViewer) {
			addQuickFixProposals((StructuredTextViewer) viewer, proposals, documentOffset);

			if (proposals.isEmpty()) {
				addQuickAssistProposals((StructuredTextViewer) viewer, proposals, documentOffset);
			}
		}

		if (proposals.isEmpty())
			proposals.add(new NoModificationCompletionProposal());

		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	protected IQuickAssistProcessor getQuickAssistProcessor() {
		return null;
	}

	protected IQuickFixProcessor getQuickFixProcessor() {
		return null;
	}
}
