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

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameTargetNamespaceProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RenameRefactoringWizard;

public class RenameTargetNamespaceAction extends XSDSelectionDispatchAction {

	public RenameTargetNamespaceAction(ISelection selection,
			Object aModel) {
		super(selection, aModel);
		setText(RefactoringWizardMessages.RenameTargetNamespace_text);

	}


	protected boolean canEnable(Object selectedObject) {

		return super.canEnable(selectedObject);

	}
	
	public boolean canRun() {

		return getSchema() != null;
	}


	public void run(ISelection selection) {

		
		RenameTargetNamespaceProcessor processor = new RenameTargetNamespaceProcessor(getSchema(), getSchema().getTargetNamespace());
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					refactoring,
					RefactoringWizardMessages.RenameComponentWizard_defaultPageTitle,//TODO: provide correct strings
					RefactoringWizardMessages.RenameComponentWizard_inputPage_description, 
					null);
			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
					wizard);
			op.run(XSDEditorPlugin.getShell(), wizard
					.getDefaultPageTitle());
			triggerBuild();
		} catch (InterruptedException e) {
			// do nothing. User action got cancelled
		}

	}

	public static void triggerBuild() {
		if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
			new GlobalBuildAction(XSDEditorPlugin.getPlugin().getWorkbench()
					.getActiveWorkbenchWindow(),
					IncrementalProjectBuilder.INCREMENTAL_BUILD).run();
		}
	}

}
