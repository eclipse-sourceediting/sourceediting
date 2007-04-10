/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;

/**
 * @plannedfor 1.0
 */
public class JavaTypeFinder {

	public static ICompletionProposal[] getBeanProposals(IResource resource, int replacementStart, int replacementLength) {
		ICompletionProposal[] typeProposals = getTypeProposals(resource, replacementStart, replacementLength);
		ICompletionProposal[] serialProposals = getSerializedProposals(resource, replacementStart, replacementLength);
		ICompletionProposal[] beanProposals = new ICompletionProposal[typeProposals.length + serialProposals.length];

		int i;
		for (i = 0; i < serialProposals.length; i++) {
			beanProposals[i] = serialProposals[i];
		}
		for (i = serialProposals.length; i < serialProposals.length + typeProposals.length; i++) {
			beanProposals[i] = typeProposals[i - serialProposals.length];
		}
		return beanProposals;
	}

	private static void getMembers(IContainer container, List membersList) {
		try {
			IResource[] members = container.members(true);
			if (members != null) {
				for (int i = 0; i < members.length; i++) {
					if (members[i].getType() == IResource.FILE)
						membersList.add(members[i]);
					else if (members[i].getType() == IResource.FOLDER)
						getMembers((IContainer) members[i], membersList);
				}
			}
		}
		catch (CoreException e) {
			// do nothing
		}
	}

	private static ICompletionProposal[] getSerializedProposals(IResource resource, int replacementStart, int replacementLength) {
		List names = new ArrayList();
		List resources = new ArrayList();
		getMembers(resource.getProject(), resources);
		IResource memberResource = null;
		for (int i = 0; i < resources.size(); i++) {
			memberResource = (IResource) resources.get(i);
			if (memberResource.getType() == IResource.FILE && memberResource.getName().endsWith(".ser")) { //$NON-NLS-1$
				String path = URIHelper.normalize(memberResource.getFullPath().toString(), resource.getFullPath().toString(), resource.getProject().getFullPath().toString());
				if (path != null) {
					names.add(new CustomCompletionProposal("\"" + path + "\"", //$NON-NLS-1$ //$NON-NLS-2$
								replacementStart, replacementLength, path.length() + 2, null, path, null, null, IRelevanceConstants.R_NONE));
				}
			}
		}
		return (ICompletionProposal[]) names.toArray(new ICompletionProposal[names.size()]);
	}

	/**
	 *
	 * @param resource
	 * @param replacementStart
	 * @param replacementLength
	 * @param searchFor IJavaSearchConstants.TYPE, IJavaSearchConstants.CLASS
	 * @return
	 */
	private static ICompletionProposal[] findProposals(IResource resource, int replacementStart, int replacementLength, int searchFor, boolean ignoreAbstractClasses) {

		JavaTypeNameRequestor requestor = new JavaTypeNameRequestor();
		requestor.setJSPOffset(replacementStart);
		requestor.setReplacementLength(replacementLength);
		requestor.setIgnoreAbstractClasses(ignoreAbstractClasses);

		try {
			IJavaElement[] elements = new IJavaElement[]{getJavaProject(resource)};
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
			new SearchEngine().searchAllTypeNames(null, null, SearchPattern.R_PATTERN_MATCH | SearchPattern.R_PREFIX_MATCH, searchFor, scope, requestor, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);

		}
		catch (CoreException exc) {
			Logger.logException(exc);
		}
		catch (Exception exc) { 
			// JavaModel
			Logger.logException(exc);
		}
		return requestor.getProposals();
	}

	public static ICompletionProposal[] getTypeProposals(IResource resource, int replacementStart, int replacementLength) {
		return findProposals(resource, replacementStart, replacementLength, IJavaSearchConstants.TYPE, false);
	}
	
	public static ICompletionProposal[] getClassProposals(IResource resource, int replacementStart, int replacementLength) {
		return findProposals(resource, replacementStart, replacementLength, IJavaSearchConstants.CLASS, true);
	}

	private static IJavaProject getJavaProject(IResource resource) {
		IProject proj = resource.getProject();
		IJavaProject javaProject = JavaCore.create(proj);
		return javaProject;
	}
}
