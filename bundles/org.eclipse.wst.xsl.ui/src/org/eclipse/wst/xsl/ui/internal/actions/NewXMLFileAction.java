/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.ui.internal.wizards.NewXMLWizard;

public class NewXMLFileAction extends Action
{
	private static final int SIZING_WIZARD_WIDTH = 500;
	private static final int SIZING_WIZARD_HEIGHT = 500;

	private ISelection selection;

	public void run()
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		NewXMLWizard wizard = new NewXMLWizard();

		IStructuredSelection ssel = null;
		if (selection instanceof IStructuredSelection)
		{
			ssel = (IStructuredSelection) selection;
		}
		else
		{
			ssel = new StructuredSelection();
		}
		wizard.init(PlatformUI.getWorkbench(), ssel);

		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		dialog.getShell().setSize(Math.max(SIZING_WIZARD_WIDTH, dialog.getShell().getSize().x), SIZING_WIZARD_HEIGHT);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),IWorkbenchHelpContextIds.NEW_WIZARD);
		dialog.open();
	}

	public void setSelection(ISelection selection)
	{
		this.selection = selection;
	}
}
