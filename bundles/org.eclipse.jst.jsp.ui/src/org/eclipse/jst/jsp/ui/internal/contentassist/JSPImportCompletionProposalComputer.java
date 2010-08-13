/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

/**
 * <p>Generates JSP import completion proposals</p>
 */
public class JSPImportCompletionProposalComputer extends
		JSPJavaCompletionProposalComputer {

	/**
	 * @see org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaCompletionProposalComputer#computeCompletionProposals(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		
		List proposals = computeJavaCompletionProposals(context.getViewer(), context.getInvocationOffset(), 0);
		List importProposals = new ArrayList(proposals.size());
		for (int i = 0; i < proposals.size(); i++) {
			if (proposals.get(i) instanceof JSPCompletionProposal) {

				ICompletionProposal importProposal = adjustImportProposal((JSPCompletionProposal) proposals.get(i));
				importProposals.add(importProposal);
			}
		}
		return importProposals;
		
	}

	/**
	 * <p>JSP import proposals need to be adjusted, this does that</p>
	 * 
	 * @param importProposal {@link JSPCompletionProposal} to adjust
	 * @return adjusted {@link ICompletionProposal}
	 */
	private ICompletionProposal adjustImportProposal(JSPCompletionProposal importProposal) {
		// just need to remove the ";"
		// and adjust offsets for the change
		String newReplace;
		if (importProposal instanceof AutoImportProposal){
			newReplace =((AutoImportProposal)importProposal).getImportDeclaration() .replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else{
			 newReplace = importProposal.getReplacementString().replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		importProposal.setReplacementString(newReplace);

		String newDisplay = importProposal.getDisplayString().replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$
		importProposal.setDisplayString(newDisplay);

		int newReplacementLength = importProposal.getReplacementLength() - 1;
		if (newReplacementLength >= 0) {
			importProposal.setReplacementLength(newReplacementLength);
		}

		int newCursorPosition = importProposal.getCursorPosition() - 1;
		importProposal.setCursorPosition(newCursorPosition);

		return importProposal;
	}

}
