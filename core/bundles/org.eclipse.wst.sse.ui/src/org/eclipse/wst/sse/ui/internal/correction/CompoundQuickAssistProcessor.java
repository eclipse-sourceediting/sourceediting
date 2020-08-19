/*******************************************************************************
 * Copyright (c) 2007, 2020 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.correction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.Logger;

/**
 * A quick assist processor that will allow for more than one quick assist
 * processor. It includes quick assist processors contributed via the
 * quickAssistProcessor extended configuration extension point. It also
 * includes quick assist processors contributed via validators.
 */
public class CompoundQuickAssistProcessor implements IQuickAssistProcessor {
	private final String QUICK_ASSIST_PROCESSOR_EXTENDED_ID = IQuickAssistProcessor.class.getName();
	private Map<String, Set<IQuickAssistProcessor>> fProcessors;
	private IQuickAssistProcessor fQuickFixProcessor;

	/**
	 * list of partition types where extended processors have been installed
	 */
	private List<String> fInstalledExtendedContentTypes;

	private Set<IQuickAssistProcessor> getQuickAssistProcessors(IQuickAssistInvocationContext invocationContext, String partitionType) {
		if (fInstalledExtendedContentTypes == null || !fInstalledExtendedContentTypes.contains(partitionType)) {
			// get extended quick assist processors that have not already
			// been set
			List<IQuickAssistProcessor> processors = ExtendedConfigurationBuilder.getInstance().getConfigurations(QUICK_ASSIST_PROCESSOR_EXTENDED_ID, partitionType);
			if (processors != null && !processors.isEmpty()) {
				Iterator<IQuickAssistProcessor> iter = processors.iterator();
				while (iter.hasNext()) {
					IQuickAssistProcessor processor = iter.next();
					setQuickAssistProcessor(partitionType, processor);
				}
			}
			// add partition type to list of extended partition types
			// installed (regardless of whether or not any extended content
			// assist processors were installed because don't want to look it
			// up every time)
			if (fInstalledExtendedContentTypes == null) {
				fInstalledExtendedContentTypes = new ArrayList<>();
			}
			fInstalledExtendedContentTypes.add(partitionType);
		}

		Set<IQuickAssistProcessor> processors = null;
		if (fProcessors != null) {
			processors = fProcessors.get(partitionType);
		}

		return processors;
	}

	/**
	 * Gets all the quick assist processors relevant to the partition which is
	 * calculated from the given document and offset.
	 * 
	 * @param invocationContext
	 * @return Set of quick assist processors or null if none exist
	 */
	private Set<IQuickAssistProcessor> getQuickAssistProcessors(IQuickAssistInvocationContext invocationContext) {
		Set<IQuickAssistProcessor> processors = null;

		ISourceViewer sourceViewer = invocationContext.getSourceViewer();
		if (sourceViewer != null) {
			IDocument document = sourceViewer.getDocument();
			try {
				String partitionType;
				if (document != null) {
					partitionType = TextUtilities.getContentType(document, IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, invocationContext.getOffset(), true);
				}
				else {
					partitionType = IDocument.DEFAULT_CONTENT_TYPE;
				}

				processors = getQuickAssistProcessors(invocationContext, partitionType);
			}
			catch (BadLocationException x) {
				Logger.log(Logger.WARNING_DEBUG, x.getMessage(), x);
			}
		}

		return processors;
	}

	/**
	 * Gets the quick assist processor for validator contributed quick fixes
	 * 
	 * @return IQuickAssistProcessor
	 */
	private IQuickAssistProcessor getQuickFixProcessor() {
		if (fQuickFixProcessor == null)
			fQuickFixProcessor = new SourceValidationQuickAssistProcessor();

		return fQuickFixProcessor;
	}

	/**
	 * Associates a quick assist processor to a partition type and adds it to
	 * the list of processors in this compound processor.
	 * 
	 * @param partitionType
	 * @param processor
	 */
	private void setQuickAssistProcessor(String partitionType, IQuickAssistProcessor processor) {
		if (fProcessors == null)
			fProcessors = new HashMap<>();

		Set<IQuickAssistProcessor> processors = fProcessors.get(partitionType);

		if (processor == null && processors != null) {
			// removing quick assist processor for this partition type
			processors.clear();
			// check if it's the only
			fProcessors.remove(partitionType);
		}
		else {
			if (processors == null) {
				processors = new LinkedHashSet<>();
			}
			processors.add(processor);
			fProcessors.put(partitionType, processors);
		}
	}

	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		Set<IQuickAssistProcessor> processors = getQuickAssistProcessors(invocationContext);
		if (processors != null) {
			// iterate through list of processors until one processor says
			// canAssist
			for (Iterator<IQuickAssistProcessor> it = processors.iterator(); it.hasNext();) {
				IQuickAssistProcessor p = it.next();
				if (p.canAssist(invocationContext))
					return true;
			}
		}
		return false;
	}

	public boolean canFix(Annotation annotation) {
		// only quick fix processor contributes fixes so just check it
		IQuickAssistProcessor processor = getQuickFixProcessor();
		return processor.canFix(annotation);
	}

	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext invocationContext) {
		List<ICompletionProposal> proposalsList = new ArrayList<>();

		// first get list of fixes
		IQuickAssistProcessor processor = getQuickFixProcessor();
		ICompletionProposal[] proposals = processor.computeQuickAssistProposals(invocationContext);
		if (proposals != null && proposals.length > 0) {
			proposalsList.addAll(Arrays.asList(proposals));
		}

		// no fixes, so try adding assists
		if (proposalsList.isEmpty()) {
			Set<IQuickAssistProcessor> processors = getQuickAssistProcessors(invocationContext);
			if (processors != null) {
				// iterate through list of processors until one processor says
				// canAssist
				for (Iterator<IQuickAssistProcessor> it = processors.iterator(); it.hasNext();) {
					IQuickAssistProcessor assistProcessor = it.next();
					ICompletionProposal[] assistProposals = assistProcessor.computeQuickAssistProposals(invocationContext);
					if (assistProposals != null && assistProposals.length > 0) {
						proposalsList.addAll(Arrays.asList(assistProposals));
					}
				}
			}
		}

		/*
		 * Java editor currently returns a no modification completion proposal
		 * but it seems better to just return null so user does not get
		 * annoying proposal popup
		 */
		if (proposalsList.isEmpty()) {
			// proposalsList.add(new NoModificationCompletionProposal());
			return null;
		}

		return proposalsList.toArray(new ICompletionProposal[proposalsList.size()]);
	}

	public String getErrorMessage() {
		// never have error messages
		return null;
	}

}
