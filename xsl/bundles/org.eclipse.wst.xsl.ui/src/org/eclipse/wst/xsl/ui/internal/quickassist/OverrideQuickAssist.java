/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.quickassist;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;

public class OverrideQuickAssist implements IQuickAssistProcessor
{

	public OverrideQuickAssist()
	{
		// TODO Auto-generated constructor stub
	}

	public boolean canAssist(IQuickAssistInvocationContext invocationContext)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canFix(Annotation annotation)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext invocationContext)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
