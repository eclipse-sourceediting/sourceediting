/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;

/**
 * @author ebelisar
 */
public class ResourceRenameParticipant extends RenameParticipant {
	
	private IFile fFile = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		if(element instanceof IFile) {
			this.fFile = (IFile) element;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	public String getName() {
		String name = ""; //$NON-NLS-1$
		if(this.fFile != null) {
			name = fFile.getName();
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		// TODO add check for file content type
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		//computeQualifiedNameMatches(pm);	
		//Change[] changes = fQualifiedNameSearchResult.getAllChanges();
		return new CompositeChange(RefactoringMessages.getString("XSDResourceRenameParticipant.compositeChangeName"));
		
	}
	
//	private void computeQualifiedNameMatches(IProgressMonitor pm) throws CoreException {
//		IPath fragment= fFile.getFullPath();
//		if (fQualifiedNameSearchResult == null)
//			fQualifiedNameSearchResult= new QualifiedNameSearchResult();
//		QualifiedNameFinder.process(fQualifiedNameSearchResult, fFile.getName(),  
//			getArguments().getNewName(), 
//			"*.xsd", fFile.getProject(), pm);
//	}
	

}
