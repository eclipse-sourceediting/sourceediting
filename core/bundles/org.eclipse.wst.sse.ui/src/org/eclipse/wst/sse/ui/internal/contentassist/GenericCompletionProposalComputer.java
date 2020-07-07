/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.sse.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.internal.genericeditor.ContentAssistProcessorRegistry;
import org.eclipse.ui.internal.genericeditor.GenericEditorPlugin;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.sse.ui.internal.Logger;

public class GenericCompletionProposalComputer implements ICompletionProposalComputer {
	List<IContentAssistProcessor> fContentAssistProcessors = null;
	private String fLastErrorMessage;
	private static final String fExampleFileName = "filename.html"; //$NON-NLS-1$

	public void sessionEnded() {
		fContentAssistProcessors = null;
	}

	@Override
	public void sessionStarted() {
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(CompletionProposalInvocationContext context, IProgressMonitor monitor) {
		ISourceViewer viewer = null;
		fLastErrorMessage = null;
		if (context.getViewer() instanceof ISourceViewer) {
			viewer = (ISourceViewer) context.getViewer();
		}
		if (fContentAssistProcessors == null && viewer != null) {
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			ContentAssistProcessorRegistry contentAssistProcessorRegistry = GenericEditorPlugin.getDefault().getContentAssistProcessorRegistry();
			Set<IContentType> types = new HashSet<>();
			types.addAll(Arrays.asList(contentTypeManager.findContentTypesFor(fExampleFileName)));
			IContentType contentType = contentTypeManager.getContentType(IContentTypeManager.CT_TEXT);
			types.add(contentType);
			try {
				fContentAssistProcessors = contentAssistProcessorRegistry.getContentAssistProcessors(viewer, null, types);
			}
			catch (NoClassDefFoundError e) {
				Logger.logException(e);
			}
		}
		List<ICompletionProposal> proposals = new ArrayList<>();
		for (IContentAssistProcessor processor : fContentAssistProcessors) {
			ICompletionProposal[] computed = processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset());
			proposals.addAll(Arrays.asList(computed));
			String errorMessage = processor.getErrorMessage();
			if (errorMessage != null) {
				if (fLastErrorMessage == null) {
					fLastErrorMessage = processor.getErrorMessage();
				}
				else {
					fLastErrorMessage = fLastErrorMessage.concat("\n").concat(errorMessage);
				}
			}
		}
		return proposals;
	}

	@Override
	public List<IContextInformation> computeContextInformation(CompletionProposalInvocationContext context, IProgressMonitor monitor) {
		ISourceViewer viewer = null;
		fLastErrorMessage = null;
		if (context.getViewer() instanceof ISourceViewer) {
			viewer = (ISourceViewer) context.getViewer();
		}
		if (fContentAssistProcessors == null && viewer != null) {
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			ContentAssistProcessorRegistry contentAssistProcessorRegistry = GenericEditorPlugin.getDefault().getContentAssistProcessorRegistry();
			Set<IContentType> types = new HashSet<>();
			types.addAll(Arrays.asList(contentTypeManager.findContentTypesFor(fExampleFileName)));
			IContentType contentType = contentTypeManager.getContentType(IContentTypeManager.CT_TEXT);
			types.add(contentType);
			fContentAssistProcessors = contentAssistProcessorRegistry.getContentAssistProcessors(viewer, null, types);
		}
		List<IContextInformation> infos = new ArrayList<>();
		for (IContentAssistProcessor processor : fContentAssistProcessors) {
			IContextInformation[] computed = processor.computeContextInformation(context.getViewer(), context.getInvocationOffset());
			infos.addAll(Arrays.asList(computed));
			String errorMessage = processor.getErrorMessage();
			if (errorMessage != null) {
				if (fLastErrorMessage == null) {
					fLastErrorMessage = processor.getErrorMessage();
				}
				else {
					fLastErrorMessage = fLastErrorMessage.concat("\n").concat(errorMessage);
				}
			}
		}
		return infos;
	}

	@Override
	public String getErrorMessage() {
		return fLastErrorMessage;
	}
}
