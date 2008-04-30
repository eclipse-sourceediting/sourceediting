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
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchScope;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class JSPMethodRenameChange extends Change {
	public static Change[] createChangesFor(IFunction method, String newName) {
		JsSearchSupport support = JsSearchSupport.getInstance();
		// should be handled by JSPIndexManager
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3036
		// support.indexWorkspaceAndWait();
		BasicRefactorSearchRequestor requestor = new JSPMethodRenameRequestor(method, newName);
		support.searchRunnable(method, new JsSearchScope(), requestor);
		return requestor.getChanges();
	}
	
	
	public Object getModifiedElement() {
		// pa_TODO Auto-generated method stub
		return null;
	}
	
	
	public String getName() {
		return JsUIMessages.JSP_changes;
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
		// pa_TODO return the "undo" change here
		return null;
	}
}
