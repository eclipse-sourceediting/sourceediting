/**
 *  Copyright (c) 2013, 2020 Angelo ZERR and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Mickael Istria (Red Hat Inc.) - Extracted, refactored and moved to org.eclipse
 *  Nitin Dahyabhai (IBM Corporation) - improve performance finding matching resources
 */
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.core.internal.validate.ModuleCoreSupport;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.html.ui.internal.wizard.FacetModuleCoreSupport;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.parser.regions.AttributeNameRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;

public abstract class AbstractWebResourcesCompletionProposalComputer extends DefaultXMLCompletionProposalComputer {

	private Job completionComputerJob;
	protected Set<Image> images = new HashSet<>();
	

	@Override
	protected void addAttributeValueProposals(
			final ContentAssistRequest contentAssistRequest,
			final CompletionProposalInvocationContext context) {
//		long time0 = System.currentTimeMillis();
		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();
		final IPath referencePath = new Path(element.getModel().getBaseLocation());
		if (referencePath.segmentCount() > 1) {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(referencePath);
			if (file.exists() && matchRequest(contentAssistRequest)) {
				String matchString = contentAssistRequest.getMatchString();
				matchString = StringUtils.stripQuotes(matchString);
				if (matchString.length() > 0 && (matchString.startsWith("\"") || matchString.startsWith("'"))) {
					matchString = matchString.substring(1);
				}
				IPath runtimeReferencePath = ModuleCoreSupport.getRuntimePath(referencePath).removeLastSegments(1).makeAbsolute();
//			this.completionComputerJob = new Job("Compute completion proposals") {
//				@Override
//				protected IStatus run(IProgressMonitor arg0) {
				IPath[] matchingPaths = findMatchingPaths(file);
				for (IPath path : matchingPaths) {
					if (!referencePath.equals(path)) {
						String proposalText = null;
						try {
							IPath runtimeProposalPath = ModuleCoreSupport.getRuntimePath(path);
							if (runtimeProposalPath != null && runtimeReferencePath != null) {
								proposalText = runtimeProposalPath.makeRelativeTo(runtimeReferencePath).toString();
							}
							if (proposalText == null) {
								proposalText = runtimeProposalPath.makeRelativeTo(referencePath.removeLastSegments(1)).toString();
							}
							if (proposalText.startsWith(matchString)) {
								contentAssistRequest.addProposal(createCompletionProposal(contentAssistRequest, referencePath, runtimeReferencePath, path, runtimeProposalPath, proposalText));
							}
						}
						catch (IllegalArgumentException ex) {
							Logger.logException(ex);
						}
					}
				}
//					return Status.OK_STATUS;
//				}
//			};
			}
		}
//		System.out.println("Generated proposals as " + getClass().getName() + " in " + (System.currentTimeMillis() - time0) + "ms.");
	}

	protected ICompletionProposal createCompletionProposal(ContentAssistRequest request, IPath referencePath, IPath runtimeReferencePath, IPath proposalPath, IPath runtimeProposalPath, String relativeProposal) {
		String replacementString = '"' + relativeProposal + '"';
		int cursorPosition = replacementString.length();
		Image image = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(referencePath.lastSegment()).createImage();
		if (image != null) {
			this.images.add(image);
		}
		final int replacementLength = request.getRegion().getTextLength();
		final int replacementOffset = request.getStartOffset();

		return new CustomCompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, relativeProposal, null, proposalPath.toString(), 0);
	}

	protected IPath[] findMatchingPaths(IResource referenceResource) {
		ContentTypeSpecs fileMatcher = createFilenameMatcher();
		final List<IPath> res = new ArrayList<>();
		IWorkspaceRoot workspaceRoot = referenceResource.getWorkspace().getRoot();
		IPath referencePath = referenceResource.getFullPath();
		IPath[] roots = FacetModuleCoreSupport.getAcceptableRootPaths(referenceResource.getProject());
		/*
		 * If editing a file not within one of the roots, offer everything
		 * project-wide. The deployment information is either wrong or the
		 * user is intentionally editing something that won't be deployed, and
		 * they know better than us.
		 */
		boolean referencePathWithinValidRoot = false;
		for (int i = 0; i < roots.length; i++) {
			referencePathWithinValidRoot |= roots[i].isPrefixOf(referencePath);
		}
		if (!referencePathWithinValidRoot) {
			roots = new IPath[]{referenceResource.getProject().getFullPath()};
		}
		for (int i = 0; i < roots.length; i++) {
			try {
				workspaceRoot.findMember(roots[i]).accept(new IResourceProxyVisitor() {
					@Override
					public boolean visit(IResourceProxy proxy) throws CoreException {
						if (proxy.getType() == IResource.FILE && fileMatcher.matches(proxy.getName())) {
							res.add(proxy.requestFullPath());
						}
						return !proxy.isDerived();
					}
				}, IResource.NONE);
			}
			catch (CoreException ex) {
				HTMLUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, HTMLUIPlugin.ID, ex.getMessage(), ex));
			}
		}
		return res.toArray(new IPath[res.size()]);
	}

	abstract boolean matchRequest(ContentAssistRequest contentAssistRequest);

	abstract ContentTypeSpecs createFilenameMatcher();

	@Override
	public void sessionEnded() {
		if (this.completionComputerJob != null) {
			this.completionComputerJob.cancel();
		}
		for (Image image : this.images) {
			image.dispose();
		}
		super.sessionEnded();
	}

	protected String getCurrentAttributeName(ContentAssistRequest contentAssistRequest) {
		String attributeName = null;
		for (ITextRegion childRegion : contentAssistRequest.getDocumentRegion().getRegions().toArray()) {
			if (childRegion instanceof AttributeNameRegion) {
				attributeName = contentAssistRequest.getDocumentRegion().getText(childRegion);
			} else if (childRegion.equals(contentAssistRequest.getRegion())) {
				break;
			}
		}
		return attributeName;
	}
}
