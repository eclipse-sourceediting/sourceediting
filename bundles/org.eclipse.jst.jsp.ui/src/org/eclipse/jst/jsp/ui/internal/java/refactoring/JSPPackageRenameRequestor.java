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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.osgi.util.NLS;

/**
 * Overrides get description
 * @author pavery
 */
public class JSPPackageRenameRequestor extends BasicRefactorSearchRequestor {
	
	/**
	 * Element is the old package.  newName is the new package name.
	 * @param element
	 * @param newName
	 */
	public JSPPackageRenameRequestor(IJavaElement element, String newName) {
		super(element, newName);
	}
	
	/*
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	protected String getDescription() {
		String packageName = getElement().getElementName();
		String newName = getNewName();
		String description = NLS.bind(JSPUIMessages.BasicRefactorSearchRequestor_5, (new String[]{packageName, newName})); //$NON-NLS-1$
		return description;
	}
}
