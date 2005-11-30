/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author ebelisar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface INameUpdating {
	//---- INameUpdating ---------------------------------------------------
	public abstract void setNewElementName(String newName);

	public abstract String getNewElementName();

	public abstract String getCurrentElementName();

	/* non java-doc
	 * @see IRenameRefactoring#checkNewName()
	 */public abstract RefactoringStatus checkNewElementName(String newName);
}