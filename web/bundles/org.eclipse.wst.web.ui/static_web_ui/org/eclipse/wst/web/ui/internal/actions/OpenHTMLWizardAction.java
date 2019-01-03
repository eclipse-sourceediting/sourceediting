/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
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
import org.eclipse.wst.html.ui.internal.wizard.NewHTMLWizard;

public class OpenHTMLWizardAction extends AbstractOpenWizardAction
{

	@Override
	public void run( IAction action )
	{
		openWizardDialog( new NewHTMLWizard() );
	}

}
