/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.correction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * This quick assist processor will provide quick fixes for source validation
 * errors (Temporary Annotation)
 */
public class SourceValidationQuickAssistProcessor implements IQuickAssistProcessor {
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return false;
	}

	public boolean canFix(Annotation annotation) {
		if (annotation instanceof TemporaryAnnotation) {
			Object fixInfo = ((TemporaryAnnotation) annotation).getAdditionalFixInfo();
			if (fixInfo instanceof IQuickAssistProcessor) {
				return ((IQuickAssistProcessor) fixInfo).canFix(annotation);
			}
		}
		return false;
	}

	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext quickAssistContext) {
		ISourceViewer viewer = quickAssistContext.getSourceViewer();
		int documentOffset = quickAssistContext.getOffset();

		int length = viewer != null ? viewer.getSelectedRange().y : 0;
		TextInvocationContext context = new TextInvocationContext(viewer, documentOffset, length);


		IAnnotationModel model = viewer.getAnnotationModel();
		if (model == null)
			return null;

		List proposals = computeProposals(context, model);
		if (proposals.isEmpty())
			return null;

		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	private boolean isAtPosition(int offset, Position pos) {
		return (pos != null) && (offset >= pos.getOffset() && offset <= (pos.getOffset() + pos.getLength()));
	}

	private List computeProposals(IQuickAssistInvocationContext context, IAnnotationModel model) {
		int offset = context.getOffset();
		ArrayList proposalsList = new ArrayList();
		Iterator iter = model.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation = (Annotation) iter.next();
			// canFix will verify annotation is instanceof TemporaryAnnotation
			// so if true, we can assume it is
			if (canFix(annotation)) {
				Position pos = model.getPosition(annotation);
				if (isAtPosition(offset, pos)) {
					context = new TextInvocationContext(context.getSourceViewer(), pos.getOffset(), pos.getLength());
					collectProposals(annotation, context, proposalsList);
				}
			}
		}
		return proposalsList;
	}

	private void collectProposals(Annotation annotation, IQuickAssistInvocationContext invocationContext, List proposalsList) {
		TemporaryAnnotation temporaryAnno = (TemporaryAnnotation) annotation;
		Object fixInfo = temporaryAnno.getAdditionalFixInfo();
		if (fixInfo instanceof IQuickAssistProcessor) {
			ICompletionProposal[] proposals = ((IQuickAssistProcessor) fixInfo).computeQuickAssistProposals(invocationContext);
			if (proposals != null && proposals.length > 0) {
				proposalsList.addAll(Arrays.asList(proposals));
			}
		}
	}


	public String getErrorMessage() {
		return null;
	}

}
