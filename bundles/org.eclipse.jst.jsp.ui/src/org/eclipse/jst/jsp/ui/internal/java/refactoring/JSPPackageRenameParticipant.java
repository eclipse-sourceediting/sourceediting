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
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * Remember to change the plugin.xml file if the name of this class changes.
 * 
 * @author pavery
 */
public class JSPPackageRenameParticipant extends RenameParticipant {

	private IPackageFragment fPkg = null;
	
	/**
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		if(element instanceof IPackageFragment) {
			this.fPkg = (IPackageFragment) element;
			return true;
		}
		return false;
	}
	
	/**
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	public String getName() {
		String name = ""; //$NON-NLS-1$
		if(this.fPkg != null) {
			name = this.fPkg.getElementName();
		}
		return name;
	}

	/**
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException {
		 Change[] changes = JSPPackageRenameChange.createChangesFor(this.fPkg, getArguments().getNewName());
		 CompositeChange multiChange = null;
		 if(changes.length > 0)
			multiChange = new CompositeChange(JSPUIMessages.JSP_changes, changes); //$NON-NLS-1$
	     return multiChange;
	}
	
	
}
