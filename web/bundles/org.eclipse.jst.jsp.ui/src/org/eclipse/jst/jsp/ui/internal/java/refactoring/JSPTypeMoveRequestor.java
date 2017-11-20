/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import java.text.MessageFormat;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.search.JavaSearchDocumentDelegate;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;

/**
 * @author pavery
 */
public class JSPTypeMoveRequestor extends BasicRefactorSearchRequestor {

	/**
	 * @param element
	 * @param newName
	 */
	public JSPTypeMoveRequestor(IJavaElement element, String newPackage) {
		super(element, newPackage);
	}
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getRenameText(org.eclipse.jst.jsp.core.internal.java.search.JavaSearchDocumentDelegate, org.eclipse.jdt.core.search.SearchMatch)
	 */
	protected String getRenameText(JavaSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		
		String renameText = getElement().getElementName();

		JSPTranslation trans = searchDoc.getJspTranslation();
		String matchText = trans.getJavaText().substring(javaMatch.getOffset(), javaMatch.getOffset() + javaMatch.getLength());
		
		// if it's an import or jsp:useBean, we need to add the package name as well
		if(trans.isImport(javaMatch.getOffset()) || trans.isUseBean(javaMatch.getOffset()) || isFullyQualified(matchText)) {
			if(!getNewName().equals("")) //$NON-NLS-1$
				// getNewName() is the pkg name
				renameText = getNewName() + "." + renameText; //$NON-NLS-1$
		}

		//if the rename text is the same as the match text then, don't want to bother renaming anything
		if(renameText.equals(matchText)) {
			renameText = null;
		} 

		return renameText;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	protected String getDescription() {
		
		String typeName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(JSPUIMessages.BasicRefactorSearchRequestor_2, new String[]{typeName, newName}); //$NON-NLS-1$
		return description;
	}
}
