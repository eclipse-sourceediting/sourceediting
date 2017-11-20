/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
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
import org.eclipse.jdt.core.IType;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchScope;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * <p><b>Note:</b> This class is not used internally any longer and will
 * be removed at some point.</p>
 * 
 * @deprecated
 */
public class JSPTypeRenameChange extends Change {

	/**
	 * @deprecated
	 */
	public static Change[] createChangesFor(IType type, String newName) {
		JSPSearchSupport support = JSPSearchSupport.getInstance();
		
		// should be handled by JSPIndexManager
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3036
		// support.indexWorkspaceAndWait();
	
		
		JSPTypeRenameRequestor requestor = new JSPTypeRenameRequestor(type, newName);
		support.searchRunnable(type, new JSPSearchScope(), requestor);

		return requestor.getChanges();
	}

	public String getName() {
		return JSPUIMessages.JSP_changes; //$NON-NLS-1$
	}

	public void initializeValidationData(IProgressMonitor pm) {
		// pa_TODO implement
		// must be implemented to decide correct value of isValid
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		// pa_TODO implement
		// This method must ensure that the change object is still valid.
		// This is in particular interesting when performing an undo change
		// since the workspace could have changed since the undo change has
		// been created.
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		// TODO return the "undo" change here
		return null;
	}

	public Object getModifiedElement() {
		// TODO Auto-generated method stub
		return null;
	}
}
