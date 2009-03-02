/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;

/**
 * Represents a change that renames a given resource
 */
public class ResourceRenameChange extends Change {

	/*
	 * we cannot use handles because they became invalid when you rename the resource.
	 * paths do not.
	 */
	private IPath fResourcePath;

	private String fNewName;

	/**
	 * @param newName includes the extension
	 */
	public ResourceRenameChange(IResource resource, String newName) {
		this(resource.getFullPath(), newName);
	}

	private ResourceRenameChange(IPath resourcePath, String newName) {
		fResourcePath= resourcePath;
		fNewName= newName;
	}

	private IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(fResourcePath);
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		
		// TODO: implement file validation, see JDTChange
		return new RefactoringStatus();
	}
	
	/*
	 * to avoid the exception senders should check if a resource with the new name
	 * already exists
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(RefactoringMessages.getString("XSDRenameResourceChange.rename_resource"), 1); //$NON-NLS-1$

			getResource().move(renamedResourcePath(fResourcePath, fNewName), getCoreRenameFlags(), pm);

			String oldName= fResourcePath.lastSegment();
			IPath newPath= renamedResourcePath(fResourcePath, fNewName);
			return new ResourceRenameChange(newPath, oldName);
		} finally {
			pm.done();
		}
	}

	private int getCoreRenameFlags() {
		if (getResource().isLinked())
			return IResource.SHALLOW;
		else
			return IResource.NONE;
	}

	/*
	 * changes resource names /s/p/A.java renamed to B.java becomes /s/p/B.java
	 */
	public static IPath renamedResourcePath(IPath path, String newName) {
		return path.removeLastSegments(1).append(newName);
	}

	public String getName() {
		return RefactoringMessages.getFormattedString(
			"XSDRenameResourceChange.name", new String[]{fResourcePath.toString(), //$NON-NLS-1$
			renamedResourcePath(fResourcePath, fNewName).toString()});
	}

	public Object getModifiedElement() {
		return getResource();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initializeValidationData(IProgressMonitor pm) {
		// TODO Auto-generated method stub

	}
}
