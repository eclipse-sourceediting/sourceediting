/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * A handler that launches JDT move element wizard
 * 
 * Still relies heavily on internal API
 * will change post 3.0 with public move support
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817 
 */
public class MoveElementHandler extends AbstractHandler {
//	private IEditorPart fEditor;

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// no-op until we know how we're supposed to use this 
		// eclipse 3.2M5
		// public move support: https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
		
//		IJavaElement[] elements = getSelectedElements();
//		if (elements.length > 0) {
//
//			// need to check if it's movable
//			try {
//				JavaMoveProcessor processor = JavaMoveProcessor.create(getResources(elements), elements);
//				
//				Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//				MoveRefactoring refactoring = new MoveRefactoring(processor);
//
//				RefactoringWizard wizard = createWizard(refactoring);
//
//				/*
//				 * We want to get the shell from the refactoring dialog but
//				 * it's not known at this point, so we pass the wizard and
//				 * then, once the dialog is open, we will have access to its
//				 * shell.
//				 */
//
//				processor.setCreateTargetQueries(new CreateTargetQueries(wizard));
//				processor.setReorgQueries(new ReorgQueries(wizard));
//				// String openRefactoringWizMsg =
//				// RefactoringMessages.getString("OpenRefactoringWizardAction.refactoring");
//				// //$NON-NLS-1$
//				String openRefactoringWizMsg = JSPUIMessages.MoveElementWizard; // "Move
//																				// the
//																				// selected
//																				// elements";
//																				// //$NON-NLS-1$
//				new RefactoringStarter().activate(refactoring, wizard, parent, openRefactoringWizMsg, true);
//
//				PlatformStatusLineUtil.clearStatusLine();
//
//			}
//			catch (JavaModelException e) {
//				Logger.logException(e);
//			}
//		}
//		else {
//			PlatformStatusLineUtil.displayErrorMessage(JSPUIMessages.JSPMoveElementAction_0); //$NON-NLS-1$
//		}
		return null;
	}

}
