/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.validation.internal.core.IHelper;



public class IncrementalHelper implements IHelper {
	private IProject fProject;

	public IncrementalHelper(IDocument sourceDocument, IProject project) {
		super();
		fProject = project;
	}

	public IFile getFileFromFilename(String filename) {
		IResource res = getProject().findMember(filename, true); // true means
		// include
		// phantom
		// resources
		if (res instanceof IFile) {
			return (IFile) res;
		}
		return null;
	}

	public String getPortableName(IResource resource) {
		return resource.getProjectRelativePath().toString();
	}

	public IProject getProject() {
		return fProject;
	}

	public String getTargetObjectName(Object object) {
		if (object == null)
			return null;
		if (object instanceof IResource)
			return getPortableName((IResource) object);
		return object.toString();
	}

	public Object loadModel(String symbolicName) {
		return null;
	}

	public Object loadModel(String symbolicName, Object[] parms) {
		return null;
	}
}
