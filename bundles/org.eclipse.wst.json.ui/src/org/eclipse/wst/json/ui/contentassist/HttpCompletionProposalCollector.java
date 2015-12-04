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

import java.io.IOException;

import org.eclipse.json.http.HttpHelper;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public abstract class HttpCompletionProposalCollector implements
		ICompletionProposalCollector {

	@Override
	public void addProposals(ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, TargetType target) {
		String url = getUrl(contentAssistRequest, target);
		try {
			JsonValue json = HttpHelper.makeRequest(url);
			addProposals(json, contentAssistRequest, context, target);
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

	protected abstract String getUrl(ContentAssistRequest contentAssistRequest,
			TargetType target);

	protected abstract void addProposals(JsonValue json,
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, TargetType target);
}
