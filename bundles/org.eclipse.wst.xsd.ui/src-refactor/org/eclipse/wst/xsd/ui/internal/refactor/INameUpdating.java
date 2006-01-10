/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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