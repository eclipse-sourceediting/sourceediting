/*******************************************************************************
 * Copyright (c) 2009 Chase Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Doug Satchwell - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.quickassist;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;

public class ValidationQuickAssist implements IQuickAssistProcessor {

	public ValidationQuickAssist() {
		super();
	}

	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canFix(Annotation annotation) {
		// TODO Auto-generated method stub
		return true;
	}

	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext) {
		CompletionProposal proposal = new CompletionProposal("doug", 5, 10, 11); //$NON-NLS-1$
		return new ICompletionProposal[] { proposal };
	}

	public String getErrorMessage() {
		return null;
	}

}
