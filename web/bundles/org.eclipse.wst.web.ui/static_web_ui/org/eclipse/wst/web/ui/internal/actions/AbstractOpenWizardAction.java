/*******************************************************************************
 * Copyright (c) 2006, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWizard;

public abstract class AbstractOpenWizardAction implements IWorkbenchWindowActionDelegate
{

	private IWorkbenchWindow window;

	public AbstractOpenWizardAction()
	{
		super();
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void init( IWorkbenchWindow window )
	{
		this.window = window;
	}

	@Override
	abstract public void run( IAction action );

	@Override
	public void selectionChanged( IAction action, ISelection selection )
	{
	}

	protected void openWizardDialog( IWorkbenchWizard wizard )
	{
		WizardDialog dialog = createWizardDialog(wizard);
		dialog.open();
	}

	protected WizardDialog createWizardDialog(IWorkbenchWizard wizard) {
		ISelection selection = window.getSelectionService().getSelection();
		
		if ( selection instanceof IStructuredSelection )
		{
			wizard.init( window.getWorkbench(), (IStructuredSelection) selection );
		}
		else
		{
			wizard.init( window.getWorkbench(), StructuredSelection.EMPTY );
		}
	
		Shell parent = window.getShell();
		WizardDialog dialog = new WizardDialog( parent, wizard );
	
		dialog.create();
		return dialog;
	}
}
