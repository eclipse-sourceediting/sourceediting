/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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