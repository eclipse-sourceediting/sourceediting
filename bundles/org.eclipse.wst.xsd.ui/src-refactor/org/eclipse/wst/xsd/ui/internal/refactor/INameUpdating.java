/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author ebelisar
 */
public interface INameUpdating {

	public abstract void setNewElementName(String newName);
	public abstract String getNewElementName();
	public abstract String getCurrentElementName();
    public abstract RefactoringStatus checkNewElementName(String newName);
}