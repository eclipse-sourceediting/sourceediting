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

import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.search.SearchMatch;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JavaSearchDocumentDelegate;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;

/**
 * @author pavery
 */
public class JSPMethodRenameRequestor extends BasicRefactorSearchRequestor {

	public JSPMethodRenameRequestor(IJavaElement element, String newName) {
		super(element, newName);
	}

	@Override
	protected String getRenameText(JavaSearchDocumentDelegate searchDoc,
			SearchMatch javaMatch) {

		String javaText = searchDoc.getJspTranslation().getJsText();
		String methodText = javaText.substring(javaMatch.getOffset(), javaMatch
				.getOffset()
				+ javaMatch.getLength());
		String methodSuffix = methodText.substring(methodText.indexOf("(")); //$NON-NLS-1$
		return getNewName() + methodSuffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	@Override
	protected String getDescription() {

		String methodName = getElement().getElementName();
		String newName = getNewName();
		String description = MessageFormat.format(
				JSPUIMessages.BasicRefactorSearchRequestor_3, new String[] {
						methodName, newName });
		return description;
	}
}
