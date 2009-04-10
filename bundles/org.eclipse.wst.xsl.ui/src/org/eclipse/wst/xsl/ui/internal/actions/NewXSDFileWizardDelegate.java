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

public class NewXSDFileWizardDelegate implements IWorkbenchWindowActionDelegate
{
	private static final String XSD_UI_INTERNAL_WIZARDS_NEW_XSD_WIZARD_ID = "org.eclipse.wst.xsd.ui.internal.wizards.NewXSDWizard"; //$NON-NLS-1$
	private NewWizardAction openAction;
	private ISelection selection;

	public void init(IWorkbenchWindow window)
	{
		this.openAction = new NewWizardAction(XSD_UI_INTERNAL_WIZARDS_NEW_XSD_WIZARD_ID);
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
