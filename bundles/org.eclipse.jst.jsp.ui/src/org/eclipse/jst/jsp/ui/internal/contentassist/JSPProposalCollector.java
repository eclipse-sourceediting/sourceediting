/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.ui.text.java.ProposalContextInformation;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.swt.graphics.Image;

/**
 * Passed into ICodeComplete#codeComplete(int offset, CompletionRequestor requestor).
 * Adapts IJavaCompletionProposals to JSPCompletion proposals.
 * This includes:
 *  - translating offsets
 *  - "fixing" up display strings
 *  - filtering some unwanted proposals
 *
 * @plannedfor 1.0
 */
public class JSPProposalCollector extends CompletionProposalCollector {

	private JSPTranslation fTranslation;
	private Comparator fComparator;
	private IImportContainer fImportContainer;

	public JSPProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		super(cu);
	
		if(translation == null)
			throw new IllegalArgumentException("JSPTranslation cannot be null"); //$NON-NLS-1$
		
		fTranslation = translation;
		fImportContainer = cu.getImportContainer();
	}

	/**
	 * Ensures that we only return JSPCompletionProposals.
	 * @return an array of JSPCompletionProposals
	 */
	public JSPCompletionProposal[] getJSPCompletionProposals() {
		List results = new ArrayList();
		IJavaCompletionProposal[] javaProposals = getJavaCompletionProposals();
		// need to filter out non JSPCompletionProposals
		// because their offsets haven't been translated
		for (int i = 0; i < javaProposals.length; i++) {
			if(javaProposals[i] instanceof JSPCompletionProposal)
				results.add(javaProposals[i]);
		}
		Collections.sort(results, getComparator());
		return (JSPCompletionProposal[])results.toArray(new JSPCompletionProposal[results.size()]);
	}
	
	private Comparator getComparator() {
		if(fComparator == null)
			fComparator = new CompletionProposalComparator();
		return fComparator;
	}
	
	/**
	 * Overridden to:
	 *  - translate Java -> JSP offsets
	 *  - fix cursor-position-after
	 *  - fix mangled servlet name in display string
	 *  - remove unwanted proposals (servlet constructor)
	 */
	protected IJavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal) {
		
		JSPCompletionProposal jspProposal = null;
		
		// ignore constructor proposals (they're not relevant for our JSP proposal list)
		if(!proposal.isConstructor()) {
			
			if(proposal.getKind() == CompletionProposal.TYPE_REF) {
				String signature = String.valueOf(proposal.getDeclarationSignature());
				String completion = String.valueOf(proposal.getCompletion());
				if(completion.indexOf(signature + ".") != -1) { //$NON-NLS-1$
					jspProposal = createAutoImportProposal(proposal);			
				}
			}
			
			// default behavior
			if(jspProposal == null)
				jspProposal = createJspProposal(proposal);		
		}
		return jspProposal;
	}

	/**
	 * Retrieves the type name from the string <code>fullName</code>
	 * @param fullName the fully qualified Java name
	 * @return the type name
	 */
	private String getTypeName(String fullName) {
		int index = fullName.lastIndexOf('.');
		return (index != -1) ? fullName.substring(index + 1) : fullName;
	}

	private JSPCompletionProposal createAutoImportProposal(CompletionProposal proposal) {
		
		JSPCompletionProposal jspProposal = null;

		String completion = new String(proposal.getCompletion());
		
		// it's fully qualified so we should
		// add an import statement
		// create an autoimport proposal
		String newCompletion = getTypeName(completion);
		
		// java offset
		int offset = proposal.getReplaceStart();
		// replacement length
		int length = proposal.getReplaceEnd() - offset;
		// translate offset from Java > JSP
		offset = fTranslation.getJspOffset(offset);
		// cursor position after must be calculated
		int positionAfter = calculatePositionAfter(proposal, newCompletion, offset);
		
		// from java proposal
		IJavaCompletionProposal javaProposal = super.createJavaCompletionProposal(proposal);
		proposal.getDeclarationSignature();
		Image image = javaProposal.getImage();
		String displayString = javaProposal.getDisplayString();
		displayString = getTranslation().fixupMangledName(displayString);
		IContextInformation contextInformation = javaProposal.getContextInformation();
		// don't do this, it's slow
		// String additionalInfo = javaProposal.getAdditionalProposalInfo();
		int relevance = javaProposal.getRelevance();
		
		boolean updateLengthOnValidate = true;
		
		jspProposal = new AutoImportProposal(completion, fImportContainer, newCompletion, offset, length, positionAfter, image, displayString, contextInformation, null, relevance, updateLengthOnValidate);
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=124483
		// set wrapped java proposal so additional info can be calculated on demand
		jspProposal.setJavaCompletionProposal(javaProposal);
		
		return jspProposal;
	}

	private JSPCompletionProposal createJspProposal(CompletionProposal proposal) {
		
		JSPCompletionProposal jspProposal;
		String completion = String.valueOf(proposal.getCompletion());
		// java offset
		int offset = proposal.getReplaceStart();
		// replacement length
		int length = proposal.getReplaceEnd() - offset;
		// translate offset from Java > JSP
		offset = fTranslation.getJspOffset(offset);
		// cursor position after must be calculated
		int positionAfter = calculatePositionAfter(proposal, completion, offset);
		
		// from java proposal
		IJavaCompletionProposal javaProposal = super.createJavaCompletionProposal(proposal);
		proposal.getDeclarationSignature();
		Image image = javaProposal.getImage();
		String displayString = javaProposal.getDisplayString();
		displayString = getTranslation().fixupMangledName(displayString);
		IContextInformation contextInformation = javaProposal.getContextInformation();
		// String additionalInfo = javaProposal.getAdditionalProposalInfo();
		
		/* the context information is calculated with respect to the java document
		 * thus it needs to be updated in respect of the JSP document.
		 */
		if(contextInformation instanceof ProposalContextInformation) {
			ProposalContextInformation proposalInfo = (ProposalContextInformation)contextInformation;
			int contextInfoJSPOffset = fTranslation.getJspOffset(proposalInfo.getContextInformationPosition());
			proposalInfo.setContextInformationPosition(contextInfoJSPOffset);
		}
		
		int relevance = javaProposal.getRelevance();
		
		boolean updateLengthOnValidate = true;
		
		jspProposal = new JSPCompletionProposal(completion, offset, length, positionAfter, image, displayString, contextInformation, null, relevance, updateLengthOnValidate);
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=124483
		// set wrapped java proposal so additional info can be calculated on demand
		jspProposal.setJavaCompletionProposal(javaProposal);
		
		return jspProposal;
	}

	/**
	 * Cacluates the where the cursor should be after applying this proposal.
	 * eg. method(|) if the method proposal chosen had params.
	 * 
	 * @param proposal
	 * @param completion
	 * @param currentCursorOffset
	 * @return
	 */
	private int calculatePositionAfter(CompletionProposal proposal, String completion, int currentCursorOffset) {
		// calculate cursor position after
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=118398
		//int positionAfter = currentCursorOffset+completion.length();
		int positionAfter = completion.length();
		
		int kind = proposal.getKind();
		
		// may need better logic here...
		// put cursor inside parenthesis if there's params
		// only checking for any kind of declaration
		if(kind == CompletionProposal.ANONYMOUS_CLASS_DECLARATION || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.POTENTIAL_METHOD_DECLARATION || kind == CompletionProposal.METHOD_REF) {
			String[] params = Signature.getParameterTypes(String.valueOf(proposal.getSignature()));
			if(completion.length() > 0 && params.length > 0)
				positionAfter--;
		}
		return positionAfter;
	}
	
	static char[] getTypeTriggers() {
		return TYPE_TRIGGERS;
	}

	public JSPTranslation getTranslation() {
		return fTranslation;
	}
	
}