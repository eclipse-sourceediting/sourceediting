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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

/**
 * {@link JSPMoveParticipant} used to update JSP documents when a Java type is moved.
 */
public class JSPTypeMoveParticipant extends JSPMoveParticipant {
	
	/**
	 * Initializes the name of this participant to the name of the {@link IType}
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		boolean success = false;
		if(element instanceof IType) {
			super.fName =((IType)element).getElementName();
			success = true;
		}
		return success;
	}

	/**
	 * @return a {@link JSPTypeMoveRequestor}
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPMoveParticipant#getSearchRequestor(org.eclipse.jdt.core.IJavaElement, java.lang.String)
	 */
	protected BasicRefactorSearchRequestor getSearchRequestor(
			IJavaElement element, String newName) {
		
		BasicRefactorSearchRequestor searchRequestor = null;
		
		if(isLegalElementType(element)) {
			searchRequestor = new JSPTypeMoveRequestor(element, newName);
		}
		
		return searchRequestor;
	}

	/**
	 * <p>Legal types are: 
	 * <ul><li>{@link IType}</li></ul></p>
	 * 
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPRenameParticipant#isLegalElementType(org.eclipse.jdt.core.IJavaElement)
	 */
	protected boolean isLegalElementType(IJavaElement element) {
		return (element instanceof IType);
	}
	
}
