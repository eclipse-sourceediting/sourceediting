/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;

/**
 * This class computes attribute value completion proposals for &lt;jsp:[gs]etProperty&gt; tags.
 * 
 * @deprecated This class is no longer used locally and will be removed in the future
 * @see JSPPropertyCompletionProposalComputer
 */
public class JSPPropertyContentAssistProcessor extends JSPDummyContentAssistProcessor {
	private JSPPropertyCompletionProposalComputer fComputer;
	
	public JSPPropertyContentAssistProcessor() {
		super();
		fComputer = new JSPPropertyCompletionProposalComputer();
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		fComputer.addAttributeValueProposals(contentAssistRequest, null);
	}
}