/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.ITypeNameRequestor;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @version 	5.0
 */

public class JavaTypeFinder {
	// COPIED TO REMOVE INTERNAL DEPENDENCY FOR NOW...
	// org.eclipse.jdt.internal.compiler.env.AccPublic
	static int AccPublic = 0x0001;
	// org.eclipse.jdt.internal.codeassist.R_DEFAULT
	static int R_DEFAULT = 0;

	protected static class JavaTypeNameRequestor implements ITypeNameRequestor {

		private JavaTypeResultCollector collector = null;
		private StringBuffer s = null;
		private boolean allowInterfaces = true;

		public JavaTypeNameRequestor(boolean allowInterfaces) {
			super();
			this.allowInterfaces = allowInterfaces;
			collector = new JavaTypeResultCollector(allowInterfaces);
			s = new StringBuffer();
		}

		private char[] getCompletionName(char[] packageName, char[][] enclosingTypeNames, char[] simpleTypeName) {
			s.delete(0, s.length());
			if (packageName != null && packageName.length > 0) {
				s.append(packageName);
				s.append('.');
			}
			if (enclosingTypeNames != null) {
				for (int i = 0; i < enclosingTypeNames.length; i++) {
					if (enclosingTypeNames[i].length > 0) {
						s.append(enclosingTypeNames[i]);
						s.append('.');
					}
				}
			}
			s.append(simpleTypeName);
			return s.toString().toCharArray();
		}

		public void acceptClass(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {
			// forbid inner classes as they don't work [yet]
			if (enclosingTypeNames == null || enclosingTypeNames.length == 0)
				collector.acceptClass(packageName, simpleTypeName, getCompletionName(packageName, enclosingTypeNames, simpleTypeName), AccPublic, 0, 0, R_DEFAULT);
		}

		public void acceptInterface(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {
			// forbid inner classes as they don't work [yet]
			if (this.allowInterfaces && (enclosingTypeNames == null || enclosingTypeNames.length == 0))
				collector.acceptInterface(packageName, simpleTypeName, getCompletionName(packageName, enclosingTypeNames, simpleTypeName), AccPublic, 0, 0, R_DEFAULT);
		}

		public JavaTypeResultCollector getCollector() {
			return collector;
		}
	}

	public static void initJDT() {
		// The following code will initialize the Java UI plugin if it
		// is not already initialized.
		try {
			Bundle jdtUI = Platform.getBundle("org.eclipse.jdt.ui"); //$NON-NLS-1$
			Bundle jdtCore = Platform.getBundle("org.eclipse.jdt.core"); //$NON-NLS-1$
			jdtUI.start();
			jdtCore.start();
		} catch (BundleException e1) {
			// problems initializing plugins
		}
	}

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

	protected static ICompletionProposal[] getSerializedProposals(IResource resource, int replacementStart, int replacementLength) {
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

	protected static ICompletionProposal[] findTypeProposals(IResource resource, int replacementStart, int replacementLength, boolean allowInterfaces) {
		initJDT();
		JavaTypeNameRequestor requestor = new JavaTypeNameRequestor(allowInterfaces);
		requestor.getCollector().setReplacementStart(replacementStart);
		requestor.getCollector().setReplacementLength(replacementLength);

		try {
			IJavaElement[] elements = new IJavaElement[]{getJavaProject(resource)};
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
			new SearchEngine().searchAllTypeNames(ResourcesPlugin.getWorkspace(), null, null, IJavaSearchConstants.PATTERN_MATCH, IJavaSearchConstants.CASE_INSENSITIVE, IJavaSearchConstants.TYPE, scope, requestor, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
		}
		catch (CoreException exc) {
			Logger.logException(exc);
		}
		catch (Exception exc) { // JavaModel
			Logger.logException(exc);
		}
		return requestor.getCollector().getResults();
	}

	public static ICompletionProposal[] getClassProposals(IResource resource, int replacementStart, int replacementLength) {
		return findTypeProposals(resource, replacementStart, replacementLength, false);
	}

	public static ICompletionProposal[] getTypeProposals(IResource resource, int replacementStart, int replacementLength) {
		return findTypeProposals(resource, replacementStart, replacementLength, true);
	}

	public static IJavaProject getJavaProject(IResource resource) {
		IProject proj = resource.getProject();
		IJavaProject javaProject = JavaCore.create(proj);
		//		IJavaModel javaModel = JavaModelManager.getJavaModelManager().getJavaModel();
		//		IJavaProject javaProject = javaModel.getJavaProject(proj.getName());
		return javaProject;
	}
}