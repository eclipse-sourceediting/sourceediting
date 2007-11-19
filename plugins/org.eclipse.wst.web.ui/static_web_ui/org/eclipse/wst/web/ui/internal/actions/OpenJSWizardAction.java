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
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.javascript.ui.internal.wizard.NewJSWizard;

public class OpenJSWizardAction extends AbstractOpenWizardAction implements
		IWorkbenchWindowActionDelegate
{

	public void run( IAction action )
	{
		openWizardDialog( new NewJSWizard() );
	}

}
