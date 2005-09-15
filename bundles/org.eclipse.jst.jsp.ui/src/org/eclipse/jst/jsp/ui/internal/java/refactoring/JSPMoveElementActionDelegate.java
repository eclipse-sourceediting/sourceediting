/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.internal.ui.refactoring.reorg.CreateTargetQueries;
import org.eclipse.jdt.internal.ui.refactoring.reorg.ReorgMoveWizard;
import org.eclipse.jdt.internal.ui.refactoring.reorg.ReorgQueries;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.util.PlatformStatusLineUtil;

/**
 * An action delegate that launches JDT move element wizard
 * 
 * Still relies heavily on internal API
 * will change post 3.0 with public move support
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817 
 */
public class JSPMoveElementActionDelegate implements IEditorActionDelegate, IActionDelegate2, IViewActionDelegate {
	private IEditorPart fEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditor = targetEditor;
	}

	public void dispose() {
		// nulling out just in case
		fEditor = null;
	}

	public void init(IAction action) {
		if (action != null) {
			action.setText(JSPUIMessages.MoveElement_label);
			action.setToolTipText(JSPUIMessages.MoveElement_label);
		}
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public void run(IAction action) {
		IJavaElement[] elements = getSelectedElements();
		if (elements.length > 0) {

			// need to check if it's movable
			try {
				JavaMoveProcessor processor = JavaMoveProcessor.create(getResources(elements), elements/*
																										 * ,
																										 * getCodeGenerationSettings()
																										 */);
				Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

				MoveRefactoring refactoring = new MoveRefactoring(processor);

				RefactoringWizard wizard = createWizard(refactoring);

				/*
				 * We want to get the shell from the refactoring dialog but
				 * it's not known at this point, so we pass the wizard and
				 * then, once the dialog is open, we will have access to its
				 * shell.
				 */

				processor.setCreateTargetQueries(new CreateTargetQueries(wizard));
				processor.setReorgQueries(new ReorgQueries(wizard));
				// String openRefactoringWizMsg =
				// RefactoringMessages.getString("OpenRefactoringWizardAction.refactoring");
				// //$NON-NLS-1$
				String openRefactoringWizMsg = JSPUIMessages.MoveElementWizard; // "Move
																				// the
																				// selected
																				// elements";
																				// //$NON-NLS-1$
				new RefactoringStarter().activate(refactoring, wizard, parent, openRefactoringWizMsg, true);

				PlatformStatusLineUtil.clearStatusLine();

			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
		else {
			PlatformStatusLineUtil.displayErrorMessage(JSPUIMessages.JSPMoveElementAction_0); //$NON-NLS-1$
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		PlatformStatusLineUtil.clearStatusLine();
	}

	public void init(IViewPart view) {
		// do nothing
	}

	private IJavaElement[] getSelectedElements() {

		IJavaElement[] elements = new IJavaElement[0];
		if (fEditor instanceof ITextEditor)
			elements = JSPJavaSelectionProvider.getSelection((ITextEditor) fEditor);
		return elements;
	}

	private IResource[] getResources(IJavaElement[] elements) {
		IResource[] resources = new IResource[elements.length];
		for (int i = 0; i < elements.length; i++) {
			try {
				resources[i] = elements[i].getPrimaryElement().getCorrespondingResource();// elements[i].getResource();
			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
		return resources;
	}
	
	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	private RefactoringWizard createWizard(MoveRefactoring refactoring) {
		return new ReorgMoveWizard(refactoring);
	}
}
