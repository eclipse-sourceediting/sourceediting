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



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.w3c.dom.Node;

public class ContentAssistRequest {


	protected PreferenceManager fPreferenceManager = null;
	protected Node parent = null;
	protected Node node = null;
	protected IStructuredDocumentRegion documentRegion = null;
	protected ITextRegion region = null;
	protected int replacementBeginPosition;
	protected int replacementLength;
	protected String matchString;
	protected List proposals = new ArrayList();
	protected List macros = new ArrayList();

	//	private Boolean separate = null; // (pa) not used
	//	private Boolean sort = null; // (pa) not used
	/**
	 * XMLContentAssistRequest constructor comment.
	 */
	public ContentAssistRequest(Node node, Node parent, IStructuredDocumentRegion documentRegion, ITextRegion completionRegion, int begin, int length, String filter, PreferenceManager preferencesManager) {
		super();
		setNode(node);
		setParent(parent);
		setDocumentRegion(documentRegion);
		setRegion(completionRegion);
		setMatchString(filter);
		setReplacementBeginPosition(begin);
		setReplacementLength(length);
		fPreferenceManager = preferencesManager;
	}

	public void addMacro(ICompletionProposal newProposal) {
		macros.add(newProposal);
	}

	public void addProposal(ICompletionProposal newProposal) {
		proposals.add(newProposal);
	}

	public ICompletionProposal[] getCompletionProposals() {
		ICompletionProposal results[] = null;
		if (getProposals().size() > 0 || getMacros().size() > 0) {
			List allProposals = new ArrayList();
			if (!shouldSeparate()) {
				allProposals.addAll(getProposals());
				// should be empty, as all macros should have gone into the proposal list
				allProposals.addAll(getMacros());
				allProposals = sortProposals(allProposals);
			}
			else {
				allProposals.addAll(sortProposals(getProposals()));
				allProposals.addAll(sortProposals(getMacros()));
			}

			results = new ICompletionProposal[allProposals.size()];
			for (int i = 0; i < allProposals.size(); i++) {
				results[i] = (ICompletionProposal) allProposals.get(i);
			}
		}
		return results;
	}

	/**
	 * 
	 */
	public IStructuredDocumentRegion getDocumentRegion() {
		return documentRegion;
	}

	/**
	 * 
	 * @return java.util.List
	 */
	public java.util.List getMacros() {
		return macros;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getMatchString() {
		return matchString;
	}

	/**
	 * 
	 * @return org.w3c.dom.Node
	 */
	public org.w3c.dom.Node getNode() {
		return node;
	}

	/**
	 * 
	 * @return org.w3c.dom.Node
	 */
	public org.w3c.dom.Node getParent() {
		return parent;
	}

	public PreferenceManager getPreferenceManager() {
		return fPreferenceManager;
	}

	/**
	 * 
	 * @return java.util.List
	 */
	public java.util.List getProposals() {
		return proposals;
	}

	/**
	 * 
	 */
	public ITextRegion getRegion() {
		return region;
	}

	/**
	 * 
	 * @return int
	 */
	public int getReplacementBeginPosition() {
		return replacementBeginPosition;
	}

	/**
	 * @return int
	 */
	public int getReplacementLength() {
		return replacementLength;
	}

	/**
	 * 
	 * @param newMatchString java.lang.String
	 */
	public void setMatchString(java.lang.String newMatchString) {
		matchString = newMatchString;
	}

	/**
	 * 
	 * @param newNode org.w3c.dom.Node
	 */
	public void setNode(org.w3c.dom.Node newNode) {
		node = newNode;
	}

	/**
	 * 
	 * @param newParent org.w3c.dom.Node
	 */
	public void setParent(org.w3c.dom.Node newParent) {
		parent = newParent;
	}

	/**
	 * 
	 */
	public void setRegion(ITextRegion newRegion) {
		region = newRegion;
	}

	/**
	 * 
	 * @param newReplacementBeginPosition int
	 */
	public void setReplacementBeginPosition(int newReplacementBeginPosition) {
		replacementBeginPosition = newReplacementBeginPosition;
	}

	/**
	 * 
	 * @param newReplacementEndPosition int
	 */
	public void setReplacementLength(int newReplacementLength) {
		replacementLength = newReplacementLength;
	}

	public boolean shouldSeparate() {
		/*
		 if (separate == null) {
		 PreferenceManager manager = getPreferenceManager();
		 if(manager == null) {
		 separate = Boolean.FALSE;
		 }
		 else {
		 Element caSettings = manager.getElement(PreferenceNames.CONTENT_ASSIST);
		 separate = new Boolean(caSettings.getAttribute(PreferenceNames.SEPARATE).equals(PreferenceNames.TRUE));
		 }
		 }
		 return separate.booleanValue();
		 */
		return false;
	}

	protected List sortProposals(List proposalsIn) {
		Collections.sort(proposalsIn, new ProposalComparator());
		return proposalsIn;

	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String toString() {
		return "Node: " + getNode() //$NON-NLS-1$
					+ "\nParent: " + getParent() //$NON-NLS-1$
					+ "\nStructuredDocumentRegion: " + StringUtils.escape(getDocumentRegion().toString()) //$NON-NLS-1$
					+ "\nRegion: " + getRegion() //$NON-NLS-1$
					+ "\nMatch string: '" + StringUtils.escape(getMatchString()) + "'" //$NON-NLS-2$//$NON-NLS-1$
					+ "\nOffsets: [" + getReplacementBeginPosition() + "-" + (getReplacementBeginPosition() + getReplacementLength()) + "]\n"; //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

	public int getStartOffset() {
		if (getDocumentRegion() != null && getRegion() != null)
			return ((ITextRegionCollection) getDocumentRegion()).getStartOffset(getRegion());
		return -1;
	}

	public int getTextEndOffset() {
		if (getDocumentRegion() != null && getRegion() != null)
			return ((ITextRegionCollection) getDocumentRegion()).getTextEndOffset(getRegion());
		return -1;
	}

	public String getText() {
		if (getDocumentRegion() != null && getRegion() != null)
			return ((ITextRegionCollection) getDocumentRegion()).getText(getRegion());
		return ""; //$NON-NLS-1$
	}

	/**
	 * @param region
	 */
	public void setDocumentRegion(IStructuredDocumentRegion region) {
		documentRegion = region;
	}

}
