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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class NewDTDFileWizardDelegate implements IWorkbenchWindowActionDelegate
{
	private NewWizardAction openAction;
	private ISelection selection;

	public void init(IWorkbenchWindow window)
	{
		this.openAction = new NewWizardAction("org.eclipse.wst.dtd.ui.internal.wizard.NewDTDWizard");
	}

	public void run(IAction action)
	{
		openAction.setSelection(selection);
		openAction.run();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
	}

	public void dispose()
	{
	}
}
