/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public void dispose()
	{
	}

	public void init( IWorkbenchWindow window )
	{
		this.window = window;
	}

	abstract public void run( IAction action );

	public void selectionChanged( IAction action, ISelection selection )
	{
	}

	protected void openWizardDialog( IWorkbenchWizard wizard )
	{
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
		dialog.open();
	}

}
