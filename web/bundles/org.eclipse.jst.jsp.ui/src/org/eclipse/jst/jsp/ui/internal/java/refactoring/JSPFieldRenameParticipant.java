/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>Participant for renaming Java fields in JSP documents</p>
 *
 */
public class JSPFieldRenameParticipant extends JSPRenameParticipant {

	/**
	 * <p>Initializes the name of this participant to the name of the {@link IField}</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		boolean success = false;
		if(element instanceof IField) {
			super.fName = ((IField)element).getElementName();
			success = true;
		}
		return success;
	}
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPRenameParticipant#isLegalElementType(org.eclipse.jdt.core.IJavaElement)
	 */
	protected boolean isLegalElementType(IJavaElement element) {
		return (element instanceof IField);
	}
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPRenameParticipant#getSearchRequestor(org.eclipse.jdt.core.IJavaElement, java.lang.String)
	 */
	protected BasicRefactorSearchRequestor getSearchRequestor(IJavaElement element, String newName) {
		BasicRefactorSearchRequestor searchRequestor = null;
		
		if(isLegalElementType(element)) {
			searchRequestor = new JSPFieldRenameRequestor(element, newName);
		}
		
		return searchRequestor;
	}
}