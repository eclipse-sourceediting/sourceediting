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
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameResourceProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RenameRefactoringWizard;



public class RenameResourceAction extends SelectionDispatchAction {



	
	public RenameResourceAction(ISelection selection)
	{
		super(selection);
	}

	public void selectionChanged(IStructuredSelection selection) {
		IResource element= getResource(selection);
		if (element == null) {
			setEnabled(false);
		} else {
			RenameResourceProcessor processor= new RenameResourceProcessor(element);
			setEnabled(processor.isApplicable());
			
		}
	}

	public void run(IStructuredSelection selection) {
		IResource resource = getResource(selection);
		RenameResourceProcessor processor= new RenameResourceProcessor(resource);

			if(!processor.isApplicable())
				return;
			RenameRefactoring refactoring= new RenameRefactoring(processor);
			try {
				RefactoringWizard wizard = new RenameRefactoringWizard(
						refactoring,
						RefactoringWizardMessages.RenameComponentWizard_defaultPageTitle, //TODO: provide correct strings
						RefactoringWizardMessages.RenameComponentWizard_inputPage_description, 
						null);
				RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
				op.run(XSDEditorPlugin.getShell(), wizard.getDefaultPageTitle());
			} catch (InterruptedException e) {
				// do nothing. User action got cancelled
			}
			
	}
	
	private static IResource getResource(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;
		Object first= selection.getFirstElement();
		if (! (first instanceof IResource))
			return null;
		return (IResource)first;
	}

}
