/**
 *  Copyright (c) 2013, 2019 Angelo ZERR and others
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
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.wizard.FacetModuleCoreSupport;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.w3c.dom.Node;

public class ScriptWebResourcesCompletionProposalComputer extends
		AbstractWebResourcesCompletionProposalComputer {

	ContentTypeSpecs fileMatcher = ContentTypeSpecs.createFor("org.eclipse.wst.jsdt.core.jsSource");

	@Override
	protected IPath[] findMatchingPaths(IResource referenceResource) {
		final List<IPath> res = new ArrayList<>();
		IWorkspaceRoot root = referenceResource.getWorkspace().getRoot();
		IPath[] roots = FacetModuleCoreSupport.getAcceptableRootPaths(referenceResource.getProject());
		for (int i = 0; i < roots.length; i++) {
			try {
				root.findMember(roots[i]).accept(new IResourceProxyVisitor() {
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

	@Override
	boolean matchRequest(ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		return
			"script".equals(node.getLocalName()) &&
			"src".equals(getCurrentAttributeName(contentAssistRequest));
	}

}
