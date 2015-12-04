/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.contentassist;

import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public class DefaultJSONCompletionProposalComputer extends
		AbstractJSONCompletionProposalComputer {

	@Override
	public void sessionStarted() {
		// default is to do nothing
	}

	@Override
	protected void addObjectKeyProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {
		// default is to do nothing
	}
}
