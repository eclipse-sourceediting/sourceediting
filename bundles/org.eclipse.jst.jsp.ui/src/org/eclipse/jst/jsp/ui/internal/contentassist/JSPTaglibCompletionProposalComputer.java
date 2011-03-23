/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITagDirRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibDescriptor;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.html.core.internal.contentmodel.JSP20Namespace;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.w3c.dom.Node;

/**
 * <p>Compute JSP taglib completion proposals</p>
 */
public class JSPTaglibCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		IPath basePath = getBasePath(contentAssistRequest);
		if (basePath != null) {
			IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
	
			//only add attribute value proposals for specific elements
			if(node.getNodeName().equals(JSP11Namespace.ElementName.DIRECTIVE_TAGLIB)) {
				// Find the attribute name for which this position should have a value
				IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
				ITextRegionList openRegions = open.getRegions();
				int i = openRegions.indexOf(contentAssistRequest.getRegion());
				if (i < 0)
					return;
				ITextRegion nameRegion = null;
				while (i >= 0) {
					nameRegion = openRegions.get(i--);
					if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
						break;
				}
		
				String attributeName = null;
				if (nameRegion != null)
					attributeName = open.getText(nameRegion);
		
				String currentValue = null;
				if (contentAssistRequest.getRegion().getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
					currentValue = contentAssistRequest.getText();
				else
					currentValue = ""; //$NON-NLS-1$
				String matchString = null;
				// fixups
				int start = contentAssistRequest.getReplacementBeginPosition();
				int length = contentAssistRequest.getReplacementLength();
				if (currentValue.length() > StringUtils.strip(currentValue).length() &&
						(currentValue.startsWith("\"") || currentValue.startsWith("'")) && //$NON-NLS-1$ //$NON-NLS-2$
						contentAssistRequest.getMatchString().length() > 0) {
					
					matchString = currentValue.substring(1, contentAssistRequest.getMatchString().length());
				}
				else {
					matchString = currentValue.substring(0, contentAssistRequest.getMatchString().length());
				}
				boolean existingComplicatedValue = contentAssistRequest.getRegion() != null &&
						contentAssistRequest.getRegion() instanceof ITextRegionContainer;
				if (existingComplicatedValue) {
					contentAssistRequest.getProposals().clear();
					contentAssistRequest.getMacros().clear();
				}
				else {
					String lowerCaseMatch = matchString.toLowerCase(Locale.US);
					if (attributeName.equals(JSP11Namespace.ATTR_NAME_URI)) {
						ITaglibRecord[] availableTaglibRecords = TaglibIndex.getAvailableTaglibRecords(basePath);
						/*
						 * a simple enough way to remove duplicates (resolution at
						 * runtime would be nondeterministic anyway)
						 */
						Map uriToRecords = new HashMap();
						for (int taglibRecordNumber = 0; taglibRecordNumber < availableTaglibRecords.length; taglibRecordNumber++) {
							ITaglibRecord taglibRecord = availableTaglibRecords[taglibRecordNumber];
							ITaglibDescriptor descriptor = taglibRecord.getDescriptor();
							String uri = null;
							switch (taglibRecord.getRecordType()) {
								case ITaglibRecord.URL :
									uri = descriptor.getURI();
									uriToRecords.put(uri, taglibRecord);
									break;
								case ITaglibRecord.JAR : {
									IPath location = ((IJarRecord) taglibRecord).getLocation();
									IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(location);
									IPath localContextRoot = FacetModuleCoreSupport.computeWebContentRootPath(basePath);
									for (int fileNumber = 0; fileNumber < files.length; fileNumber++) {
										if (localContextRoot.isPrefixOf(files[fileNumber].getFullPath())) {
											uri = IPath.SEPARATOR +
													files[fileNumber].getFullPath().removeFirstSegments(localContextRoot.segmentCount()).toString();
											uriToRecords.put(uri, taglibRecord);
										}
										else {
											uri = FacetModuleCoreSupport.getRuntimePath(files[fileNumber].getFullPath()).toString();
											uriToRecords.put(uri, taglibRecord);
										}
									}
									break;
								}
								case ITaglibRecord.TLD : {
									uri = descriptor.getURI();
									if (uri == null || uri.trim().length() == 0) {
										IPath path = ((ITLDRecord) taglibRecord).getPath();
										IPath localContextRoot = FacetModuleCoreSupport.computeWebContentRootPath(basePath);
										if (localContextRoot.isPrefixOf(path)) {
											uri = IPath.SEPARATOR + path.removeFirstSegments(localContextRoot.segmentCount()).toString();
										}
										else {
											uri = FacetModuleCoreSupport.getRuntimePath(path).toString();
										}
									}
									uriToRecords.put(uri, taglibRecord);
									break;
								}
							}
						}
						/*
						 * use the records and their descriptors to construct
						 * proposals
						 */
						Object[] uris = uriToRecords.keySet().toArray();
						for (int uriNumber = 0; uriNumber < uris.length; uriNumber++) {
							String uri = uris[uriNumber].toString();
							ITaglibRecord taglibRecord = (ITaglibRecord) uriToRecords.get(uri);
							ITaglibDescriptor descriptor = (taglibRecord).getDescriptor();
							if (uri != null && uri.length() > 0 && (matchString.length() == 0 ||
									uri.toLowerCase(Locale.US).startsWith(lowerCaseMatch))) {
								
								String url = getSmallImageURL(taglibRecord);
								ImageDescriptor imageDescriptor = JSPUIPlugin.getInstance().getImageRegistry().getDescriptor(url);
								if (imageDescriptor == null && url != null) {
									URL imageURL;
									try {
										imageURL = new URL(url);
										imageDescriptor = ImageDescriptor.createFromURL(imageURL);
										JSPUIPlugin.getInstance().getImageRegistry().put(url, imageDescriptor);
									}
									catch (MalformedURLException e) {
										Logger.logException(e);
									}
								}
								String additionalInfo = descriptor.getDisplayName() + "<br/>" + //$NON-NLS-1$
										descriptor.getDescription() + "<br/>" + descriptor.getTlibVersion(); //$NON-NLS-1$
								Image image = null;
								try {
									image = JSPUIPlugin.getInstance().getImageRegistry().get(url);
								}
								catch (Exception e) {
									Logger.logException(e);
								}
								if (image == null) {
									image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
								}
								CustomCompletionProposal proposal = new CustomCompletionProposal(
										"\"" + uri + "\"", start, length, uri.length() + 2, //$NON-NLS-1$ //$NON-NLS-2$
										image, uri, null, additionalInfo, IRelevanceConstants.R_NONE);
								contentAssistRequest.addProposal(proposal);
							}
						}
					}
					else if (attributeName.equals(JSP20Namespace.ATTR_NAME_TAGDIR)) {
						ITaglibRecord[] availableTaglibRecords = TaglibIndex.getAvailableTaglibRecords(basePath);
						/*
						 * a simple enough way to remove duplicates (resolution at
						 * runtime would be nondeterministic anyway)
						 */
						Map uriToRecords = new HashMap();
						IPath localContextRoot = FacetModuleCoreSupport.computeWebContentRootPath(basePath);
						for (int taglibRecordNumber = 0; taglibRecordNumber < availableTaglibRecords.length; taglibRecordNumber++) {
							ITaglibRecord taglibRecord = availableTaglibRecords[taglibRecordNumber];
							String uri = null;
							if (taglibRecord.getRecordType() == ITaglibRecord.TAGDIR) {
								IPath path = ((ITagDirRecord) taglibRecord).getPath();
								if (localContextRoot.isPrefixOf(path)) {
									uri = IPath.SEPARATOR + path.removeFirstSegments(localContextRoot.segmentCount()).toString();
									uriToRecords.put(uri, taglibRecord);
								}
							}
						}
						/*
						 * use the records and their descriptors to construct
						 * proposals
						 */
						Object[] uris = uriToRecords.keySet().toArray();
						for (int uriNumber = 0; uriNumber < uris.length; uriNumber++) {
							String uri = uris[uriNumber].toString();
							ITaglibRecord taglibRecord = (ITaglibRecord) uriToRecords.get(uri);
							ITaglibDescriptor descriptor = (taglibRecord).getDescriptor();
							if (uri != null && uri.length() > 0 && (matchString.length() == 0 ||
									uri.toLowerCase(Locale.US).startsWith(lowerCaseMatch))) {
								
								String url = getSmallImageURL(taglibRecord);
								ImageDescriptor imageDescriptor = null;
								if (url != null) {
									imageDescriptor = JSPUIPlugin.getInstance().getImageRegistry().getDescriptor(url);
								}
								if (imageDescriptor == null && url != null) {
									URL imageURL;
									try {
										imageURL = new URL(url);
										imageDescriptor = ImageDescriptor.createFromURL(imageURL);
										JSPUIPlugin.getInstance().getImageRegistry().put(url, imageDescriptor);
									}
									catch (MalformedURLException e) {
										Logger.logException(e);
									}
								}
								String additionalInfo = descriptor.getDescription() + "<br/>" + descriptor.getTlibVersion(); //$NON-NLS-1$
								Image image = null;
								try {
									image = JSPUIPlugin.getInstance().getImageRegistry().get(url);
								}
								catch (Exception e) {
									Logger.logException(e);
								}
								if (image == null) {
									image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
								}
								CustomCompletionProposal proposal = new CustomCompletionProposal(
										"\"" + uri + "\"", start, length, uri.length() + 2, image, uri, //$NON-NLS-1$ //$NON-NLS-2$
										null, additionalInfo, IRelevanceConstants.R_NONE);
								contentAssistRequest.addProposal(proposal);
							}
						}
					}
					else if (attributeName.equals(JSP11Namespace.ATTR_NAME_PREFIX)) {
						Node uriAttr = node.getAttributes().getNamedItem(JSP11Namespace.ATTR_NAME_URI);
						String uri = null;
						if (uriAttr != null) {
							uri = uriAttr.getNodeValue();
							ITaglibRecord[] availableTaglibRecords = TaglibIndex.getAvailableTaglibRecords(basePath);
							Map prefixMap = new HashMap();
							for (int taglibrecordNumber = 0; taglibrecordNumber < availableTaglibRecords.length; taglibrecordNumber++) {
								ITaglibDescriptor descriptor = availableTaglibRecords[taglibrecordNumber].getDescriptor();
								if (descriptor != null && descriptor.getURI() != null &&
										descriptor.getURI().toLowerCase(Locale.US).equals(uri.toLowerCase(Locale.US))) {
									String shortName = descriptor.getShortName().trim();
									if (shortName.length() > 0) {
										boolean valid = true;
										for (int character = 0; character < shortName.length(); character++) {
											valid = valid && !Character.isWhitespace(shortName.charAt(character));
										}
										if (valid) {
											prefixMap.put(shortName, descriptor);
										}
									}
								}
							}
							Object prefixes[] = prefixMap.keySet().toArray();
							for (int j = 0; j < prefixes.length; j++) {
								String prefix = (String) prefixes[j];
								ITaglibDescriptor descriptor = (ITaglibDescriptor) prefixMap.get(prefix);
								Image image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
								CustomCompletionProposal proposal = new CustomCompletionProposal(
										"\"" + prefix + "\"", start, length, prefix.length() + 2, image, //$NON-NLS-1$ //$NON-NLS-2$
										prefix, null, descriptor.getDescription(), IRelevanceConstants.R_NONE);
								contentAssistRequest.addProposal(proposal);
							}
						}
						else {
							Node dirAttr = node.getAttributes().getNamedItem(JSP20Namespace.ATTR_NAME_TAGDIR);
							if (dirAttr != null) {
								String dir = dirAttr.getNodeValue();
								if (dir != null) {
									ITaglibRecord record = TaglibIndex.resolve(basePath.toString(), dir, false);
									if (record != null) {
										ITaglibDescriptor descriptor = record.getDescriptor();
										if (descriptor != null) {
											String shortName = descriptor.getShortName();
		
											Image image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
											CustomCompletionProposal proposal = new CustomCompletionProposal(
													"\"" + shortName + "\"", start, length, shortName.length() + 2, //$NON-NLS-1$ //$NON-NLS-2$
													image, shortName, null, descriptor.getDescription(),
													IRelevanceConstants.R_NONE);
											contentAssistRequest.addProposal(proposal);
										}
										else {
											if (dir.startsWith("/WEB-INF/")) { //$NON-NLS-1$
												dir = dir.substring(9);
											}
											String prefix = StringUtils.replace(dir, "/", "-"); //$NON-NLS-1$ //$NON-NLS-2$
		
											Image image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
											CustomCompletionProposal proposal = new CustomCompletionProposal(
													"\"" + prefix + "\"", start, length, prefix.length() + 2, //$NON-NLS-1$ //$NON-NLS-2$
													image, prefix, null, null, IRelevanceConstants.R_NONE);
											contentAssistRequest.addProposal(proposal);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private String getSmallImageURL(ITaglibRecord taglibRecord) {
		String url = null;
		switch (taglibRecord.getRecordType()) {
			case (ITaglibRecord.TLD) : {
				ITLDRecord record = (ITLDRecord) taglibRecord;
				IResource file = ResourcesPlugin.getWorkspace().getRoot().getFile(record.getPath());
				if (file.getLocation() != null && record.getDescriptor().getSmallIcon().length() > 0) {
					url = "platform:/resource/" + //$NON-NLS-1$
					FacetModuleCoreSupport.resolve(file.getFullPath(),
							record.getDescriptor().getSmallIcon());
				}
			}
				break;
			case (ITaglibRecord.JAR) : {
				IJarRecord record = (IJarRecord) taglibRecord;
				if (record.getDescriptor().getSmallIcon().length() > 0) {
					// url = "file:" +
					// URIHelper.normalize(record.getDescriptor().getSmallIcon(),
					// record.getLocation().toString(), "/"); //$NON-NLS-1$
				}
			}
				break;
			case (ITaglibRecord.TAGDIR) : {
			}
				break;
			case (ITaglibRecord.URL) : {
				IURLRecord record = (IURLRecord) taglibRecord;
				if (record.getDescriptor().getSmallIcon().length() > 0) {
					url = URIHelper.normalize(record.getDescriptor().getSmallIcon(), record.getURL().toString(), "/"); //$NON-NLS-1$
				}
			}
				break;
		}
		return url;
	}
	
	/**
	 * Returns project request is in
	 * 
	 * @param request
	 * @return
	 */
	private IPath getBasePath(ContentAssistRequest request) {
		IPath baselocation = null;

		if (request != null) {
			IStructuredDocumentRegion region = request.getDocumentRegion();
			if (region != null) {
				IDocument document = region.getParentDocument();
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
					if (model != null) {
						String location = model.getBaseLocation();
						if (location != null) {
							baselocation = new Path(location);
						}
					}
				}
				finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
		}
		return baselocation;
	}
}
