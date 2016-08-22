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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.json.jsonpath.IJSONPath;
import org.eclipse.json.jsonpath.JSONPath;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.ui.contentassist.AbstractJSONCompletionProposalComputer;
import org.eclipse.wst.json.ui.contentassist.ContentAssistHelper;
import org.eclipse.wst.json.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.json.ui.contentassist.ICompletionProposalCollector.TargetType;
import org.eclipse.wst.json.ui.contentassist.JSONKeyCompletionProposal;
import org.eclipse.wst.json.ui.contentassist.JSONRelevanceConstants;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.json.ui.internal.editor.JSONEditorPluginImageHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public class JSONCompletionProposalComputer extends
		AbstractJSONCompletionProposalComputer {

	@Override
	public void sessionStarted() {
		// default is to do nothing
	}

	@Override
	protected void addObjectKeyProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		collectProposalsFromSchema(contentAssistRequest, context);
		collectProposalsFromExtensionPoint(contentAssistRequest, context);
	}

	/**
	 * Collect completion proposals from
	 * "org.eclipse.wst.json.ui.completionProposalCollectors" extension point.
	 * 
	 * @param contentAssistRequest
	 * @param context
	 */
	private void collectProposalsFromExtensionPoint(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		CompletionProposalCollectorsRegistryReader.getInstance().addProposals(
				contentAssistRequest, context, TargetType.key);
	}

	/**
	 * Collect completion proposals from JSON Schema.
	 * 
	 * @param contentAssistRequest
	 * @param context
	 */
	private void collectProposalsFromSchema(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		try {
			IJSONNode node = contentAssistRequest.getNode();
			IJSONSchemaDocument schemaDocument = JSONCorePlugin.getDefault()
					.getSchemaDocument(node);
			if (schemaDocument != null) {
				String matchString = contentAssistRequest.getMatchString();
				if (matchString == null) {
					matchString = ""; //$NON-NLS-1$
				}
				if ((matchString.length() > 0)
						&& (matchString.startsWith(QUOTE))) {
					matchString = matchString.substring(1);
				}
				// Loop for each properties of the JSON Schema.
				IJSONPath path = node.getPath();
				if (node instanceof IJSONPair) {
					IJSONSchemaProperty thisProperty = schemaDocument.getProperty(path);
					ITextRegion region = contentAssistRequest.getRegion();
					boolean isValue = isPairValue(context, node);
					if (thisProperty != null && isValue) {
						if (thisProperty.getFirstType() == JSONSchemaType.Boolean) {
							if (beginsWith(FALSE, matchString.trim())) {
								addStringProposal(contentAssistRequest, FALSE, false);
							}
							if (beginsWith(TRUE, matchString.trim())) {
								addStringProposal(contentAssistRequest, TRUE, false);
							}
							return;
						}
						if (thisProperty.getFirstType() == JSONSchemaType.String) {
							if (thisProperty.getEnumList() != null && thisProperty.getEnumList().size() > 0) {
								for (String prop : thisProperty.getEnumList()) {
									boolean showProperty = beginsWith(prop, matchString.trim());
									if (showProperty) {
										addStringProposal(contentAssistRequest, prop,
												!(region.getType() == JSONRegionContexts.JSON_VALUE_STRING));
									}
								}
							} else {
								if (thisProperty.getDefaultValue() != null) {
									boolean showProperty = beginsWith(thisProperty.getDefaultValue(), matchString.trim());
									if (showProperty) {
										addStringProposal(contentAssistRequest, thisProperty.getDefaultValue(),
												!(region.getType() == JSONRegionContexts.JSON_VALUE_STRING));
									}
								}
							}
							return;
						}
					}
				}
				if ( !(node instanceof IJSONObject && node.getOwnerPairNode() != null) ) {
					if (path.getSegments().length > 0) {
						String[] segments = new String[path.getSegments().length - 1];
						System.arraycopy(path.getSegments(), 0, segments, 0, path.getSegments().length-1);
						path = new JSONPath(segments);
					}
				}
				IJSONSchemaProperty parentProperty = schemaDocument
						.getProperty(path);
				Set<String> existing = new HashSet<String>();
				boolean addComma = false;
				if (node instanceof IJSONObject) {
					addExisting(existing, node);
					addComma = addComma(context, node);
				} else if (node instanceof IJSONPair && node.getParentNode() instanceof IJSONObject) {
					addExisting(existing, node.getParentNode());
				}
				if (parentProperty != null) {
					for (IJSONSchemaProperty property : parentProperty
							.getPropertyValues()) {
						boolean showProperty = !existing.contains(property.getName()) && beginsWith(property.getName(),
								matchString.trim());
						if (showProperty) {
							String replacementString;
							if (node instanceof IJSONPair) {
								replacementString = property.getName();
							} else {
								replacementString = ContentAssistHelper
									.getRequiredName(node, property);
								if (addComma) {
									replacementString = replacementString + ",";
								}
							}
							String additionalProposalInfo = property
									.getDescription();
							Image icon = JSONEditorPluginImageHelper
									.getInstance().getImage(
											property.getFirstType());
							String displayString = property.getName();
							JSONKeyCompletionProposal proposal = new JSONKeyCompletionProposal(
									replacementString,
									contentAssistRequest
											.getReplacementBeginPosition(),
									contentAssistRequest.getReplacementLength(),
									replacementString.length() - 2, icon,
									displayString, null,
									additionalProposalInfo,
									JSONRelevanceConstants.R_OBJECT_KEY);
							contentAssistRequest.addProposal(proposal);
						}
					}
				}
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

	private boolean addComma(CompletionProposalInvocationContext context, IJSONNode node) {
		IJSONNode child = node.getFirstChild();
		int documentPosition = context.getInvocationOffset();
		while (child != null) {
			if (documentPosition > child.getStartOffset()) {
				child = child.getNextSibling();
			} else {
				break;
			}
		}
		return child != null;
	}

	private void addExisting(Set<String> existing, IJSONNode node) {
		IJSONNode child = node.getFirstChild();
		while (child != null) {
			if (child instanceof IJSONPair) {
				String name = ((IJSONPair) child).getName();
				if (name != null && !name.isEmpty()) {
					existing.add(name);
				}
			}
			child = child.getNextSibling();
		}
	}

	private void addStringProposal(ContentAssistRequest contentAssistRequest, String replacementString, boolean addQuote) {
		String additionalProposalInfo = null;
		Image icon = null;
		String displayString = replacementString;
		if (addQuote) {
			replacementString = QUOTE + replacementString + QUOTE;
		}
		String matchString = contentAssistRequest.getMatchString();
		if (matchString != null) {
			matchString = matchString.replaceAll(QUOTE, ""); //$NON-NLS-1$
		}
		JSONKeyCompletionProposal proposal = new JSONKeyCompletionProposal(
				replacementString,
				contentAssistRequest
						.getReplacementBeginPosition(),
				contentAssistRequest.getReplacementLength(),
				replacementString.length() - 2, icon,
				displayString, null,
				additionalProposalInfo,
				JSONRelevanceConstants.R_OBJECT_KEY);
		contentAssistRequest.addProposal(proposal);
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
