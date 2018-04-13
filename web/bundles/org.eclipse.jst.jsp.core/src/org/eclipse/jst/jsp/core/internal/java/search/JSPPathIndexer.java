/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;

/** 
 * @author pavery
 */
public class JSPPathIndexer {

	// for debugging
	static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	// visitor that retrieves jsp project paths for all jsp files in the workspace
	class JSPFileVisitor implements IResourceProxyVisitor {
		// hash map forces only one of each file
		private Set<IPath> fPaths = new HashSet<>();
		IJavaSearchScope fScope = null;
		SearchPattern fPattern = null;

		public JSPFileVisitor(SearchPattern pattern, IJavaSearchScope scope) {
			this.fPattern = pattern;
			this.fScope = scope;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (JSPSearchSupport.getInstance().isCanceled() || proxy.isDerived())
				return false;
			
			if (proxy.getType() == IResource.FILE) {
				if (ContentTypeIdForJSP.indexOfJSPExtension(proxy.getName()) >= 0) {
					IPath fullPath = proxy.requestFullPath();
					if (this.fScope.encloses(fullPath.toString())) {

						if (DEBUG)
							System.out.println("adding selected index path:" + fullPath.removeLastSegments(1)); //$NON-NLS-1$

						fPaths.add(JSPSearchSupport.getInstance().computeIndexLocation(fullPath.removeLastSegments(1)));
					}
				}
				// don't search deeper for files
				return false;
			}
			return true;
		}

		public IPath[] getPaths() {
			return fPaths.toArray(new IPath[fPaths.size()]);
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

