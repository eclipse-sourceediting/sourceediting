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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;

/**
 * Used to constrain JSP/java search to certain paths and elements.
 * @author pavery
 */
public class JSPSearchScope implements IJavaSearchScope {

	private boolean fEnclosesAll = false;
	private List fResourcePaths = null;
	private List fJavaElements = null;

	public JSPSearchScope() {
		// empty constructor just returns true for everything
		// everything is in scope
		this.fEnclosesAll = true;
		init();
	}

	public JSPSearchScope(String[] resourceStringPath) {
		init();
		fResourcePaths.addAll(Arrays.asList(resourceStringPath));
	}

	public JSPSearchScope(IJavaElement[] javaElement) {
		init();
		fJavaElements.addAll(Arrays.asList(javaElement));
	}

	private void init() {
		this.fResourcePaths = new ArrayList();
		this.fJavaElements = new ArrayList();
	}

	public boolean encloses(String resourcePathString) {

		if (this.fEnclosesAll)
			return true;
		else if (enclosesPath(resourcePathString))
			return true;

		return false;
	}

	public boolean encloses(IJavaElement element) {

		// pa_TOD implement
		if (this.fEnclosesAll)
			return true;

		return true;
	}

	public boolean encloses(IResourceProxy element) {

		if (this.fEnclosesAll)
			return true;
		else if (enclosesPath(element.requestFullPath().toOSString()))
			return true;

		return true;
	}

	public void addPath(String path) {
		this.fResourcePaths.add(path);
	}

	public void addElement(IJavaElement element) {
		this.fJavaElements.add(element);
	}

	private boolean enclosesPath(String possible) {

		String[] paths = (String[]) fResourcePaths.toArray(new String[fResourcePaths.size()]);
		for (int i = 0; i < paths.length; i++) {
			if (possible.equals(paths[i]))
				return true;
		}
		return false;
	}

	public String getDescription() {

		return "JSPSearchScope"; //$NON-NLS-1$
	}

	public IPath[] enclosingProjectsAndJars() {
		return (IPath[]) fResourcePaths.toArray(new IPath[fResourcePaths.size()]);
	}

	public boolean includesBinaries() {
		return false;
	}

	public boolean includesClasspaths() {
		return false;
	}

	public void setIncludesBinaries(boolean includesBinaries) {
		// do nothing
	}

	public void setIncludesClasspaths(boolean includesClasspaths) {
		// do nothing
	}
}
