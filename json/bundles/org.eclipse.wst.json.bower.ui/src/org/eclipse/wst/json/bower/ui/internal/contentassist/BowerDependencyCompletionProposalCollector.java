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
package org.eclipse.wst.json.bower.ui.internal.contentassist;

import java.text.MessageFormat;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.json.bower.ui.internal.editor.BowerEditorPluginImageHelper;
import org.eclipse.wst.json.bower.ui.internal.editor.BowerEditorPluginImages;
import org.eclipse.wst.json.ui.contentassist.ContentAssistHelper;
import org.eclipse.wst.json.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.json.ui.contentassist.HttpCompletionProposalCollector;
import org.eclipse.wst.json.ui.contentassist.JSONKeyCompletionProposal;
import org.eclipse.wst.json.ui.contentassist.JSONRelevanceConstants;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public class BowerDependencyCompletionProposalCollector extends
		HttpCompletionProposalCollector {

	public final String searchUrl = "https://bower.herokuapp.com/packages/search/{0}";
	public final String packageUrl = "https://bower.herokuapp.com/packages/{0}";

	@Override
	protected String getUrl(ContentAssistRequest contentAssistRequest,
			TargetType target) {
		String keyword = contentAssistRequest.getMatchString();
		return MessageFormat.format(searchUrl, keyword);
	}

	protected void addProposals(JsonValue json,
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, TargetType target) {
		if (json.isArray()) {
			// Ex :
			// [{"name":"angular-mocks","url":"git://github.com/angular/bower-angular-mocks.git"},
			//  {"name":"angular-moment","url":"git://github.com/urish/angular-moment.git"}]
			String dependency = null;
			String replacementString = null;
			JsonArray values = (JsonArray) json;
			for (JsonValue value : values) {
				if (value.isObject()) {
					dependency = ((JsonObject) value).get("name").asString();
					replacementString = ContentAssistHelper.getRequiredName(
							dependency, JSONSchemaType.String);
					Image icon = BowerEditorPluginImageHelper.getInstance()
							.getImage(BowerEditorPluginImages.IMG_OBJ_BOWER);
					JSONKeyCompletionProposal proposal = new JSONKeyCompletionProposal(
							replacementString,
							contentAssistRequest.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							replacementString.length() - 2, icon, dependency,
							null, null, JSONRelevanceConstants.R_OBJECT_KEY);
					contentAssistRequest.addProposal(proposal);
				}
			}
		}
		//System.err.println(json);
	}
}
