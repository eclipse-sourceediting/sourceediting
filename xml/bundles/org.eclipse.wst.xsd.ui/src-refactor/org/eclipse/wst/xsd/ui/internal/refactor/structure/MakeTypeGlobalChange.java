/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.structure;

import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.Assert;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * @author ebelisar
 * 
 */
public class MakeTypeGlobalChange extends Change {

	private Map fChanges;

	private String fNewName;

	private XSDTypeDefinition fTypeComponent;

	public MakeTypeGlobalChange(XSDTypeDefinition component, 
			String newName) {
		Assert.isNotNull(newName, "new name"); //$NON-NLS-1$

		fTypeComponent = component;
		fNewName = newName;
	}

	// public static Change[] createChangesFor(XSDNamedComponent component,
	// String newName) {
	// // TODO: P1 implement search of XSD files
	// XSDSearchSupport support = XSDSearchSupport.getInstance();
	// RefactorSearchRequestor requestor = new
	// RefactorSearchRequestor(component, newName);
	// support.searchRunnable(component, IXSDSearchConstants.WORKSPACE_SCOPE,
	// requestor);
	//
	// return requestor.getChanges();
	//
	// }

	protected Change createUndoChange() {
		return new MakeTypeGlobalChange(fTypeComponent, getNewName());
	}

	protected void doRename(IProgressMonitor pm) throws CoreException {
		// TODO P1 change temporary rename of XSD model components
		performModify(getNewName());
	}

	public void performModify(final String value) {
//			DelayedRenameRunnable runnable = new DelayedRenameRunnable(
//					fTypeComponent, value);
			// TODO: remove Display
			//Display.getCurrent().asyncExec(runnable);
	}

	protected static class DelayedRenameRunnable implements Runnable {
		protected XSDTypeDefinition component;

		protected String name;

		public DelayedRenameRunnable(XSDTypeDefinition component, String name) {
			this.component = component;
			this.name = name;
		}

		public void run() {
			DocumentImpl doc = (DocumentImpl) component.getElement().getOwnerDocument();
			doc.getModel().beginRecording(
							this,
							RefactoringMessages
									.getString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"));
			MakeAnonymousTypeGlobalCommand command = new MakeAnonymousTypeGlobalCommand(
					component, name);
			command.run();
			doc.getModel().endRecording(this);
		}
	}

	public TextChange getChange(IFile file) {
		TextChange result = (TextChange) fChanges.get(file);
		if (result == null) {
			result = new TextFileChange(file.getName(), file);
			fChanges.put(file, result);
		}
		return result;
	}

	public String getName() {
		return RefactoringMessages
				.getFormattedString(
						"MakeTypeGlobalChange.name", new String[] {getNewName() }); //$NON-NLS-1$
	}

	public final Change perform(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(RefactoringMessages
					.getString("XSDComponentRenameChange.Renaming"), 1); //$NON-NLS-1$
			Change result = createUndoChange();
			doRename(new SubProgressMonitor(pm, 1));
			return result;
		} finally {
			pm.done();
		}
	}

	/**
	 * Gets the newName.
	 * 
	 * @return Returns a String
	 */
	protected String getNewName() {
		return fNewName;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	public Object getModifiedElement() {
		// TODO Auto-generated method stub
		return fTypeComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initializeValidationData(IProgressMonitor pm) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// TODO implement change validation
		return new RefactoringStatus();
	}
}
