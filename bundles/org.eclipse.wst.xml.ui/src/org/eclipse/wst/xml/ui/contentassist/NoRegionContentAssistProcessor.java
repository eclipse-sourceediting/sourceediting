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
package org.eclipse.wst.xml.ui.contentassist;



import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;

/**
 * ContentAssistProcessor to handle special cases in content assist where the partitioner
 * cannot determine a partition type at the current cursor position (usually at EOF).
 * 
 * @author pavery
 */
public class NoRegionContentAssistProcessor implements IContentAssistProcessor, IResourceDependentProcessor {

	protected char completionProposalAutoActivationCharacters[] = null;
	protected char contextInformationAutoActivationCharacters[] = null;
	protected String fErrorMessage = null;
	protected HashMap fNameToProcessorMap = null;
	protected HashMap fPartitionToProcessorMap = null;
	protected IResource fResource = null;

	private final ICompletionProposal[] EMPTY_PROPOSAL_SET = new ICompletionProposal[0];

	public NoRegionContentAssistProcessor() {
		super();
		fPartitionToProcessorMap = new HashMap();
		fNameToProcessorMap = new HashMap();
		initNameToProcessorMap();
		initPartitionToProcessorMap();

	}

	/**
	 * Adds all relevent ContentAssistProcessors to the partition to processor map (just XML here)
	 */
	protected void initPartitionToProcessorMap() {
		XMLContentAssistProcessor xmlProcessor = new XMLContentAssistProcessor();
		fPartitionToProcessorMap.put(StructuredTextPartitionerForXML.ST_DEFAULT_XML, xmlProcessor);
	}

	/**
	 * Inits map for extra ContentAssistProcessors (useBean, get/setProperty)
	 */
	protected void initNameToProcessorMap() {
	}

	/**
	 * Figures out what the correct ICompletionProposalProcessor is and computesCompletionProposals on that.
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		IContentAssistProcessor p = null;
		ICompletionProposal[] results = EMPTY_PROPOSAL_SET;

		p = guessContentAssistProcessor(viewer, documentOffset);
		if (p != null)
			results = p.computeCompletionProposals(viewer, documentOffset);

		return (results != null) ? results : EMPTY_PROPOSAL_SET;
	}

	/**
	 * Guesses a ContentAssistProcessor based on the TextViewer and documentOffset.
	 * 
	 * @param viewer
	 * @param documentOffset
	 * @return
	 */
	protected IContentAssistProcessor guessContentAssistProcessor(ITextViewer viewer, int documentOffset) {
		//  mapping logic here...
		// look @ previous region
		// look @ previous doc partition type
		// look @ page language
		IContentAssistProcessor p = null;
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion((StructuredTextViewer) viewer, documentOffset);
		if (sdRegion != null) {
			String currentRegionType = sdRegion.getType();
			//System.out.println("current region type is >> " + currentRegionType);
			if (currentRegionType == XMLRegionContext.UNDEFINED) {
				IStructuredDocumentRegion sdPrev = sdRegion.getPrevious();
				if (sdPrev != null) {
					String prevRegionType = sdPrev.getType();
					//System.out.println("previous region type is >> " + prevRegionType);
				}
			}
		}
		// working w/ viewer & document partition
		if (p == null && viewer.getDocument().getLength() > 0) {
			String prevPartitionType = getPartitionType((StructuredTextViewer) viewer, documentOffset - 1);
			//System.out.println("previous partition type is > " + prevPartitionType);
			p = (IContentAssistProcessor) fPartitionToProcessorMap.get(prevPartitionType);
		}
		return p;
	}

	/**
	 * Gives you the document partition type (String) for the given StructuredTextViewer and documentPosition.
	 * @param viewer
	 * @param documentPosition
	 * @return String
	 */
	protected String getPartitionType(StructuredTextViewer viewer, int documentPosition) {
		IDocumentPartitioner partitioner = viewer.getDocument().getDocumentPartitioner();
		return partitioner.getPartition(documentPosition).getType();
	}

	/**
	 * Necessary for certain content assist processors (such as JSPJavaContentAssistProcessor).
	 * This gets set in StructuredTextViewerConfiguration.
	 * 
	 */
	public void initialize(IResource resource) {
		fResource = resource;
		setResourceOnProcessors(resource);
	}

	/**
	 * @param resource
	 */
	private void setResourceOnProcessors(IResource resource) {
		setResourceOnMap(resource, fPartitionToProcessorMap);
		setResourceOnMap(resource, fNameToProcessorMap);
	}

	private void setResourceOnMap(IResource resource, HashMap map) {
		if (!map.isEmpty()) {
			Iterator keys = map.keySet().iterator();
			Object o = null;
			while (keys.hasNext()) {
				o = fPartitionToProcessorMap.get(keys.next());
				if (o instanceof IResourceDependentProcessor)
					((IResourceDependentProcessor) o).initialize(resource);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		// get context info from processor that we end up using...
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return completionProposalAutoActivationCharacters;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return contextInformationAutoActivationCharacters;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return fErrorMessage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		// return the validator for the content assist processor that we used...
		return null;
	}

	public void release() {
		releasePartitionToProcessorMap();
		releaseNameToProcessorMap();
	}

	protected void releasePartitionToProcessorMap() {
		releaseMap(fPartitionToProcessorMap);
	}

	protected void releaseNameToProcessorMap() {
		releaseMap(fNameToProcessorMap);
	}

	protected void releaseMap(HashMap map) {
		if (map != null && !map.isEmpty()) {
			Iterator it = map.keySet().iterator();
			Object key = null;
			while (it.hasNext()) {
				key = it.next();
				if (map.get(key) instanceof IReleasable)
					((IReleasable) map.get(key)).release();
			}
			map.clear();
			map = null;
		}
	}

}
