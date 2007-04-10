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
package org.eclipse.jst.jsp.core.internal.java.search;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;

/**
 * pa_TODO Still need to take into consideration:
 * 	- focus in workspace
 *  - search pattern
 * 
 * @author pavery
 */
public class JSPPathIndexer {

	// for debugging
	static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	// visitor that retrieves jsp project paths for all jsp files in the workspace
	class JSPFileVisitor implements IResourceProxyVisitor {
		// hash map forces only one of each file
		private HashMap fPaths = new HashMap();
		IJavaSearchScope fScope = null;
		SearchPattern fPattern = null;

		public JSPFileVisitor(SearchPattern pattern, IJavaSearchScope scope) {
			this.fPattern = pattern;
			this.fScope = scope;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {
			
			if(JSPSearchSupport.getInstance().isCanceled())
				return false;
			
			if (proxy.getType() == IResource.FILE) {

				IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3553
				// check this before description
				// check name before actually getting the file (less work)
				if(contentTypeJSP.isAssociatedWith(proxy.getName())) {
					
					IFile file = (IFile)proxy.requestResource();
					IContentDescription contentDescription = file.getContentDescription();
					String ctId = null;
					if (contentDescription != null) {
						ctId = contentDescription.getContentType().getId();
					}
					if (ContentTypeIdForJSP.ContentTypeID_JSP.equals(ctId)) {
						if (this.fScope.encloses(proxy.requestFullPath().toString())) {
	
							if (DEBUG)
								System.out.println("adding selected index path:" + file.getParent().getFullPath()); //$NON-NLS-1$

							fPaths.put(file.getParent().getFullPath(), JSPSearchSupport.getInstance().computeIndexLocation(file.getParent().getFullPath()));
						}
					}
				}
				// don't search deeper for files
				return false;
			}
			return true;
		}

		public IPath[] getPaths() {
			return (IPath[]) fPaths.values().toArray(new IPath[fPaths.size()]);
		}
	}

	public IPath[] getVisibleJspPaths(SearchPattern pattern, IJavaSearchScope scope) {

		JSPFileVisitor jspFileVisitor = new JSPFileVisitor(pattern, scope);
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(jspFileVisitor, 0);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return jspFileVisitor.getPaths();
	}
}

