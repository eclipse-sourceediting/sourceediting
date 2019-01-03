/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.xml.core.internal.provisional.NameValidator;

public class Checks {
	
	public static RefactoringStatus checkName(String name) {
		RefactoringStatus result= new RefactoringStatus();
		if ("".equals(name)) //$NON-NLS-1$
			return RefactoringStatus.createFatalErrorStatus(RefactoringMessages.getString("Checks_Choose_name"));  //$NON-NLS-1$
		return result;
	}
	
	public static boolean isAlreadyNamed(RefactoringComponent element, String name){
		return name.equals(element.getName());
	}
	
	public static RefactoringStatus checkComponentName(String name) {
		RefactoringStatus result= new RefactoringStatus();
		if (!NameValidator.isValid(name))
			return RefactoringStatus.createFatalErrorStatus(RefactoringMessages.getString("Checks_Choose_name"));  //$NON-NLS-1$

		return result;
	}

}
