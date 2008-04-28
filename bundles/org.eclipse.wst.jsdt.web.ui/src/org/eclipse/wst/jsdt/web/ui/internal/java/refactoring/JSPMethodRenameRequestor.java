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

import java.text.MessageFormat;

import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.search.SearchMatch;
import org.eclipse.wst.jsdt.web.core.javascript.search.JSDTSearchDocumentDelegate;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;

/**
 * @author pavery
 */
public class JSPMethodRenameRequestor extends BasicRefactorSearchRequestor {
	public JSPMethodRenameRequestor(IJavaScriptElement element, String newName) {
		super(element, newName);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	
	protected String getDescription() {
		String methodName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(JsUIMessages.BasicRefactorSearchRequestor_3, new String[] { methodName, newName });
		return description;
	}
	
	
	protected String getRenameText(JSDTSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		String javaText = searchDoc.getJspTranslation().getJsText();
		String methodText = javaText.substring(javaMatch.getOffset(), javaMatch.getOffset() + javaMatch.getLength());
		String methodSuffix = methodText.substring(methodText.indexOf("(")); //$NON-NLS-1$
		return getNewName() + methodSuffix;
	}
}
