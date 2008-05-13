/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.search.IJavaScriptSearchScope;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*(copied from JSP)
 * Used to constrain JSP/java search to certain paths and elements.
 * @author pavery
 */
public class JsSearchScope implements IJavaScriptSearchScope {

	private boolean fEnclosesAll = false;
	private List fResourcePaths = null;
	private List fJavaElements = null;

	public JsSearchScope() {
		// empty constructor just returns true for everything
		// everything is in scope
		this.fEnclosesAll = true;
		init();
	}

	public JsSearchScope(String[] resourceStringPath) {
		init();
		fResourcePaths.addAll(Arrays.asList(resourceStringPath));
	}

	public JsSearchScope(IJavaScriptElement[] javaElement) {
		init();
		fJavaElements.addAll(Arrays.asList(javaElement));
	}

	private void init() {
		this.fResourcePaths = new ArrayList();
		this.fJavaElements = new ArrayList();
	}

	public boolean encloses(String resourcePathString) {

		if (this.fEnclosesAll) {
			return true;
		} else if (enclosesPath(resourcePathString)) {
			return true;
		}

		return false;
	}

	public boolean encloses(IJavaScriptElement element) {

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

	public void addPath(String path) {
		this.fResourcePaths.add(path);
	}

	public void addElement(IJavaScriptElement element) {
		this.fJavaElements.add(element);
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

	public String getDescription() {

		return "JavaScript Search Scope"; //$NON-NLS-1$
	}

	public IPath[] enclosingProjectsAndJars() {
		return (IPath[]) fResourcePaths.toArray(new IPath[fResourcePaths.size()]);
	}

	public boolean shouldExclude(String container, String resourceName) {
		/* auto gen */
		return false;
	}
}
