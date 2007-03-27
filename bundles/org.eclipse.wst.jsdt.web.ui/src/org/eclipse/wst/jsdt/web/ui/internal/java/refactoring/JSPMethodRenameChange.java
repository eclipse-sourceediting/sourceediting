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
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.jsdt.core.IMethod;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JSPSearchScope;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JSPSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;

/**
 * @author pavery
 */
public class JSPMethodRenameChange extends Change {

	public static Change[] createChangesFor(IMethod method, String newName) {
		JSPSearchSupport support = JSPSearchSupport.getInstance();

		// should be handled by JSPIndexManager
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3036
		// support.indexWorkspaceAndWait();

		BasicRefactorSearchRequestor requestor = new JSPMethodRenameRequestor(
				method, newName);
		support.searchRunnable(method, new JSPSearchScope(), requestor);

		return requestor.getChanges();
	}

	@Override
	public String getName() {
		return JSPUIMessages.JSP_changes;
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		// pa_TODO implement
		// must be implemented to decide correct value of isValid
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		// pa_TODO implement
		// This method must ensure that the change object is still valid.
		// This is in particular interesting when performing an undo change
		// since the workspace could have changed since the undo change has
		// been created.
		return new RefactoringStatus();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		// pa_TODO return the "undo" change here
		return null;
	}

	@Override
	public Object getModifiedElement() {
		// pa_TODO Auto-generated method stub
		return null;
	}
}