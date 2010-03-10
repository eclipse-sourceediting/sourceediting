/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

/**
 * Manages creation and caching (ordered MRU) of TaglibHelpers.
 * Removes helpers when their classpath changes (so they are rebuilt).
 * There is one helper per project (with a specific classpath entry).
 * 
 * @author pavery
 */
public class TaglibHelperManager implements IElementChangedListener {
   

    private static TaglibHelperManager instance = null;
    // using a cache of just 3 loaders
    private TaglibHelperCache fCache = new TaglibHelperCache(3);

    private TaglibHelperManager() {
        // use instance
    }
    public static synchronized TaglibHelperManager getInstance() {
        if(instance == null)
            instance = new TaglibHelperManager();
        return instance;
    }
    
    public TaglibHelper getTaglibHelper(IFile f) {
        IProject p = f.getProject();
        return getHelperFromCache(p);
    }
    
    /**
     * @param projectPath
     */
    private TaglibHelper getHelperFromCache(IProject project) {
        return fCache.getHelper(project);
    }
    
    /**
     * Update classpath for appropriate loader.
     * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent)
     */
	public void elementChanged(ElementChangedEvent event) {
		// handle classpath changes
		IJavaElementDelta delta = event.getDelta();
		if (delta.getElement().getElementType() == IJavaElement.JAVA_MODEL) {
			IJavaElementDelta[] changed = delta.getChangedChildren();
			for (int i = 0; i < changed.length; i++) {
				if ((changed[i].getFlags() & IJavaElementDelta.F_CLASSPATH_CHANGED) != 0 || (changed[i].getFlags() & IJavaElementDelta.F_REORDER) != 0 || (changed[i].getFlags() & IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED) != 0 || (changed[i].getFlags() & IJavaElementDelta.F_PRIMARY_RESOURCE) != 0) {
					IJavaElement proj = changed[i].getElement();
					handleClasspathChange(changed, i, proj);
				}
			}
		}
		else if (delta.getElement().getElementType() == IJavaElement.COMPILATION_UNIT) {
			IJavaElementDelta[] changed = delta.getChangedChildren();
			for (int i = 0; i < changed.length; i++) {
				if ((changed[i].getFlags() & IJavaElementDelta.F_SUPER_TYPES) != 0) {
					IJavaElement element = changed[i].getElement();
					handleSuperTypeChange(element);
				}
			}
		}
	}

	private void handleSuperTypeChange(IJavaElement element) {
		IJavaProject project = element.getJavaProject();
		if (element instanceof IType) {
			fCache.invalidate(project.getProject().getName(), ((IType) element).getFullyQualifiedName());
		}
	}
    
    /**
     * @param changed
     * @param i
     * @param proj
     */
    private void handleClasspathChange(IJavaElementDelta[] changed, int i, IJavaElement proj) {
        if (proj.getElementType() == IJavaElement.JAVA_PROJECT) {
			String projectName = ((IJavaProject) proj).getProject().getName();
			fCache.removeHelper(projectName);
		}
    }
}
