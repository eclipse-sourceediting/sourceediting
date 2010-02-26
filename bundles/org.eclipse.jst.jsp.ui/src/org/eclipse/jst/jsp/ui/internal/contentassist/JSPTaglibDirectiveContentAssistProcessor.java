/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;

/**
 * @deprecated This class is no longer used locally and will be removed in the future
 * @see JSPTaglibCompletionProposalComputer
 */
public class JSPTaglibDirectiveContentAssistProcessor extends JSPDummyContentAssistProcessor {

	private JSPTaglibCompletionProposalComputer fComputer;
	
	public JSPTaglibDirectiveContentAssistProcessor() {
		super();
		fComputer = new JSPTaglibCompletionProposalComputer();
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		fComputer.addAttributeValueProposals(contentAssistRequest, null);
	}
}
