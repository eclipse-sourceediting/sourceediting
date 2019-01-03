/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
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
package org.eclipse.wst.xsl.ui.provisional.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Provides content assistance ICompletionProposals.
 * 
 * @author David Carver
 * @since 1.1
 */
public interface IContentAssistProposalRequest {

	/**
	 * Completion Proposals for a Content Assist Request.
	 * @return ArrayLlist<ICompletionProposal>
	 */
	public ArrayList<ICompletionProposal> getCompletionProposals();
}