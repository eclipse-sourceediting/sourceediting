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
package org.eclipse.wst.jsdt.web.core.javascript.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.search.IJavaSearchScope;

/**
 * Used to constrain JSP/java search to certain paths and elements.
 * 
 * @author pavery
 */
public class JsSearchScope implements IJavaSearchScope {
	private boolean fEnclosesAll = false;
	private List fJavaElements = null;
	private List fResourcePaths = null;
	
	public JsSearchScope() {
		// empty constructor just returns true for everything
		// everything is in scope
		this.fEnclosesAll = true;
		init();
	}
	
	public JsSearchScope(IJavaElement[] javaElement) {
		init();
		fJavaElements.addAll(Arrays.asList(javaElement));
	}
	
	public JsSearchScope(String[] resourceStringPath) {
		init();
		fResourcePaths.addAll(Arrays.asList(resourceStringPath));
	}
	
	public void addElement(IJavaElement element) {
		this.fJavaElements.add(element);
	}
	
	public void addPath(String path) {
		this.fResourcePaths.add(path);
	}
	
	public boolean encloses(IJavaElement element) {
		// pa_TOD implement
		if (this.fEnclosesAll) {
			return true;
		}
		return true;
	}
	
	public boolean encloses(IResourceProxy element) {
		if (this.fEnclosesAll) {
			return true;
		} else if (enclosesPath(element.requestFullPath().toOSString())) {
			return true;
		}
		return true;
	}
	
	public boolean encloses(String resourcePathString) {
		if (this.fEnclosesAll) {
			return true;
		} else if (enclosesPath(resourcePathString)) {
			return true;
		}
		return false;
	}
	
	private boolean enclosesPath(String possible) {
		String[] paths = (String[]) fResourcePaths.toArray(new String[fResourcePaths.size()]);
		for (int i = 0; i < paths.length; i++) {
			if (possible.equals(paths[i])) {
				return true;
			}
		}
		return false;
	}
	
	public IPath[] enclosingProjectsAndJars() {
		// pa_TODO
		return null;
	}
	
	public String getDescription() {
		return "JSPSearchScope"; //$NON-NLS-1$
	}
	
	public boolean includesBinaries() {
		/* TEMP CHANGE BC may 30 */
		return false;
	}
	
	public boolean includesClasspaths() {
		/* TEMP CHANGE BC may 30 */
		return false;
	}
	
	private void init() {
		this.fResourcePaths = new ArrayList();
		this.fJavaElements = new ArrayList();
	}
	
	public void setIncludesBinaries(boolean includesBinaries) {
	// do nothing
	}
	
	public void setIncludesClasspaths(boolean includesClasspaths) {
	// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.search.IJavaSearchScope#shouldExclude(java.lang.String, java.lang.String)
	 */
	public boolean shouldExclude(String container, String resourceName) {
		// TODO Auto-generated method stub
		//System.out.println("Unimplemented method:JsSearchScope.shouldExclude"); //$NON-NLS-1$
		return false;
	}

}