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

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
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
public class JSPPackageRenameRequestor extends BasicRefactorSearchRequestor {
	/**
	 * Element is the old package. newName is the new package name.
	 * 
	 * @param element
	 * @param newName
	 */
	public JSPPackageRenameRequestor(IJavaScriptElement element, String newName) {
		super(element, newName);
	}
	
	/*
	 * @see org.eclipse.wst.jsdt.web.ui.internal.java.refactoring.BasicRefactorSearchRequestor#getDescription()
	 */
	
	protected String getDescription() {
		String packageName = getElement().getElementName();
		String newName = getNewName();
		String description = NLS.bind(JsUIMessages.BasicRefactorSearchRequestor_5, (new String[] { packageName, newName }));
		return description;
	}
}
