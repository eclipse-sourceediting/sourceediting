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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class OpenJSWizardAction extends AbstractOpenWizardAction
{
	static private final String NEW_JS_WIZARD = "org.eclipse.wst.jsdt.ui.NewJSWizard"; //$NON-NLS-1$
	static private final String NEW_FILE_WIZARD = "org.eclipse.ui.wizards.new.file"; //$NON-NLS-1$
	
	@Override
	public void run(IAction action) {
		IWizardDescriptor newJSWizardDescriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(NEW_JS_WIZARD);
		try {
			IWorkbenchWizard wizard = newJSWizardDescriptor != null ? newJSWizardDescriptor.createWizard() : null;
			if (wizard != null) {
				openWizardDialog(wizard);
			} else {
				runAlternateWizard(action);
			}
		} catch (CoreException e1) {
			// Ignore
		}
	}
	
	public void runAlternateWizard(IAction action) {
		IWizardDescriptor newFileWizardDescriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(NEW_FILE_WIZARD);
		try {
			IWorkbenchWizard wizard = newFileWizardDescriptor != null ? newFileWizardDescriptor.createWizard() : null;
			if (wizard != null) {
				WizardDialog dialog = createWizardDialog(wizard);
				IWizardPage fp = wizard.getPage("newFilePage1"); //$NON-NLS-1$
				if (fp instanceof WizardNewFileCreationPage) {
					((WizardNewFileCreationPage)fp).setFileExtension("js"); //$NON-NLS-1$
				}
				dialog.open();
			}
		} catch (CoreException e1) {
			// Ignore
		}
	}
}
