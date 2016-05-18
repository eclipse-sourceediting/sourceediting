/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.json.jsonpath.JSONPath;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.json.ui.contentassist.ICompletionProposalCollector;
import org.eclipse.wst.json.ui.contentassist.ICompletionProposalCollector.TargetType;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public class CompletionProposalCollectorsRegistryReader {

	protected static final String EXTENSION_POINT_ID = "completionProposalCollectors"; //$NON-NLS-1$
	protected static final String TAG_CONTRIBUTION = "completionProposalCollector"; //$NON-NLS-1$

	public static CompletionProposalCollectorsRegistryReader INSTANCE = null;

	private final Map<String, Collection<CompletionProposalMatcher>> matchers;

	public CompletionProposalCollectorsRegistryReader() {
		this.matchers = new HashMap<String, Collection<CompletionProposalMatcher>>();
	}

	public static CompletionProposalCollectorsRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CompletionProposalCollectorsRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	public void addProposals(ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, TargetType target) {
		IJSONNode node = contentAssistRequest.getNode();
		String contentTypeId = node.getModel().getContentTypeIdentifier();
		Collection<CompletionProposalMatcher> matchersByContentType = matchers
				.get(contentTypeId);
		if (matchersByContentType != null) {
			for (CompletionProposalMatcher matcher : matchersByContentType) {
				matcher.addProposalsIfMatch(contentAssistRequest, context,
						target);
			}
		}
	}

	/**
	 * read from plugin registry and parse it.
	 */
	protected void readRegistry() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry
				.getExtensionPoint(JSONUIPlugin.getDefault().getBundle()
						.getSymbolicName(), EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				readElement(elements[i]);
			}
		}
	}

	protected void readElement(IConfigurationElement element) {
		if (TAG_CONTRIBUTION.equals(element.getName())) {
			try {
				ICompletionProposalCollector collector = (ICompletionProposalCollector) element
						.createExecutableExtension("class");
				JSONPath[] paths = createPaths(element.getAttribute("paths"));
				Collection<TargetType> targets = createTargets(element
						.getAttribute("targets"));
				String contentTypeId = element.getAttribute("contentTypeId");
				CompletionProposalMatcher matcher = new CompletionProposalMatcher(
						targets, paths, collector);
				Collection<CompletionProposalMatcher> matchersByContentType = matchers
						.get(contentTypeId);
				if (matchersByContentType == null) {
					matchersByContentType = new ArrayList<CompletionProposalMatcher>();
					matchers.put(contentTypeId, matchersByContentType);
				}
				matchersByContentType.add(matcher);
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

	}

	private JSONPath[] createPaths(String value) {
		String[] s = value.split(",");
		JSONPath[] paths = new JSONPath[s.length];
		for (int i = 0; i < s.length; i++) {
			paths[i] = new JSONPath(s[i].trim());
		}
		return paths;
	}

	private Collection<TargetType> createTargets(String value) {
		String[] s = value.split(",");
		Collection<TargetType> targets = new ArrayList<ICompletionProposalCollector.TargetType>();
		for (int i = 0; i < s.length; i++) {
			targets.add(TargetType.valueOf(s[i].trim()));
		}
		return targets;
	}

}
