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
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import java.text.MessageFormat;

import org.eclipse.wst.jsdt.core.IType;
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
 * Creates document change(s) for a type rename. Changes are created for every
 * type "match" in the workspace
 * 
 * @author pavery
 */
public class JSPTypeRenameRequestor extends BasicRefactorSearchRequestor {
	public JSPTypeRenameRequestor(IType type, String newName) {
		super(type, newName);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	
	protected String getDescription() {
		String typeName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(JsUIMessages.BasicRefactorSearchRequestor_4, new String[] { typeName, newName });
		return description;
	}
	
	
	protected String getRenameText(JSDTSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		String renameText = getNewName();
	//	String pkg = getType().getPackageFragment().getElementName();
	//	JsTranslation trans = searchDoc.getJspTranslation();
	//	String matchText = trans.getJsText().substring(javaMatch.getOffset(), javaMatch.getOffset() + javaMatch.getLength());
		// if it's an import or jsp:useBean or fully qualified type, we need to
		// add the package name as well
// if (trans.isImport(javaMatch.getOffset())
// || /* trans.isUseBean(javaMatch.getOffset()) ||
// */isFullyQualified(matchText)) {
// if (!pkg.equals("")) {
// renameText = pkg + "." + renameText; //$NON-NLS-1$
// }
// }
		return renameText;
	}
	
//	private IType getType() {
//		return (IType) getElement();
//	}
}
