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

import java.text.MessageFormat;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.search.JavaSearchDocumentDelegate;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;

/**
 * Creates document change(s) for a type rename.
 * Changes are created for every type "match" in the workspace
 * @author pavery
 */
public class JSPTypeRenameRequestor extends BasicRefactorSearchRequestor {

	public JSPTypeRenameRequestor(IType type, String newName) {
		super(type, newName);
	}
	
	private IType getType() {
		return (IType)getElement();
	}
	
	protected String getRenameText(JavaSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		
		String renameText = getNewName();
		
		String pkg = getType().getPackageFragment().getElementName();
		IJavaElement parent = getType().getParent();
		String parentName = (parent != null) ? parent.getElementName() : null;
		JSPTranslation trans = searchDoc.getJspTranslation();
		String matchText = trans.getJavaText().substring(javaMatch.getOffset(), javaMatch.getOffset() + javaMatch.getLength());
		
		// if it's an import or jsp:useBean or fully qualified type, we need to add the package name as well
		// else if starts with parent name such as "MyClass.Enum" then need to add the parent name as well
		if(trans.isImport(javaMatch.getOffset()) || isFullyQualified(matchText)) {
			if(!pkg.equals("")) //$NON-NLS-1$
				renameText = pkg + "." + renameText; //$NON-NLS-1$
		} else if(parentName != null && matchText.startsWith(parentName)) {
			renameText = parentName + "." + renameText; //$NON-NLS-1$
		}
		return renameText;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	protected String getDescription() {
		
		String typeName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(JSPUIMessages.BasicRefactorSearchRequestor_4, new String[]{typeName, newName}); //$NON-NLS-1$
		return description;
	}
}
