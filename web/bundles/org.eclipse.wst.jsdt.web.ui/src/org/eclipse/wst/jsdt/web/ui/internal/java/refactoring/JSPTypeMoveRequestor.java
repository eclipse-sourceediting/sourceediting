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

import java.text.MessageFormat;

import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.search.SearchMatch;
//import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.search.JSDTSearchDocumentDelegate;
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
public class JSPTypeMoveRequestor extends BasicRefactorSearchRequestor {
	/**
	 * @param element
	 * @param newName
	 */
	public JSPTypeMoveRequestor(IJavaScriptElement element, String newPackage) {
		super(element, newPackage);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	
	protected String getDescription() {
		String typeName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(JsUIMessages.BasicRefactorSearchRequestor_2, new String[] { typeName, newName });
		return description;
	}
	
	
	protected String getRenameText(JSDTSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		String renameText = getElement().getElementName();
	//	JsTranslation trans = searchDoc.getJspTranslation();
		//String matchText = trans.getJsText().substring(javaMatch.getOffset(), javaMatch.getOffset() + javaMatch.getLength());
		// if it's an import or jsp:useBean, we need to add the package name as
		// well
// if (trans.isImport(javaMatch.getOffset())
//			
// || isFullyQualified(matchText)) {
// if (!getNewName().equals("")) {
// // getNewName() is the pkg name
// renameText = getNewName() + "." + renameText; //$NON-NLS-1$
// }
// }
		return renameText;
	}
}
