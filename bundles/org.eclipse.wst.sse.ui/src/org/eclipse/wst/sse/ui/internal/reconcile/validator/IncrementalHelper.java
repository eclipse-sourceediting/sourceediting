/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.validation.IHelper;


public class IncrementalHelper implements IHelper {
	private IProject fProject;

	public IncrementalHelper(IDocument sourceDocument, IProject project) {
		super();
		fProject = project;
	}

	public String getTargetObjectName(Object object) {
		if (object == null)
			return null;
		if (object instanceof IResource)
			return getPortableName((IResource) object);
		return object.toString();
	}

	public IFile getFileFromFilename(String filename) {
		IResource res = getProject().findMember(filename, true); // true means include phantom resources
		if (res instanceof IFile) {
			return (IFile) res;
		}
		return null;
	}

	public IProject getProject() {
		return fProject;
	}

	public String getPortableName(IResource resource) {
		return resource.getProjectRelativePath().toString();
	}

	public Object loadModel(String symbolicName) {
		return null;
	}

	public Object loadModel(String symbolicName, Object[] parms) {
		return null;
	}
}
