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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.osgi.util.NLS;

/**
 * <p>Reactor search requestor to rename Java fields in JSP documents</p>
 *
 */
public class JSPFieldRenameRequestor extends BasicRefactorSearchRequestor {

	/**
	 * <p>Constructor</p>
	 * 
	 * @param element the old field name
	 * @param newName the new field name
	 */
	public JSPFieldRenameRequestor(IJavaElement element, String newName) {
		super(element, newName);
	}
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	protected String getDescription() {
		String fieldName = getElement().getElementName();
		String newName = getNewName();
		String description = NLS.bind(JSPUIMessages.BasicRefactorSearchRequestor_7, (new String[]{fieldName, newName})); //$NON-NLS-1$
		return description;
	}

}
