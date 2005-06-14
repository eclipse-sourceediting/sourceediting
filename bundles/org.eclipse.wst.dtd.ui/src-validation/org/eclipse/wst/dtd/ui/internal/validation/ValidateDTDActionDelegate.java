/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.ui.internal.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.wst.dtd.core.internal.validation.DTDValidationMessages;

/**
 * Validate schema - from popup
 */
public class ValidateDTDActionDelegate implements IActionDelegate {
	protected ISelection selection;

	public void run(IAction action) {
		try {
			// CS.. for now the following line tests to ensure the user has
			// xerces jars installed
			// so that we can perform some 'fail fast' behaviour
			//
			Class theClass = Class.forName("org.apache.xerces.xni.parser.XMLParserConfiguration", true, this.getClass().getClassLoader()); //$NON-NLS-1$
			if (theClass == null) {
				throw (new Exception("Missing Xerces jars in plugin's 'jars' folder")); //$NON-NLS-1$
			}

			IFile fileResource = null;
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Object element = structuredSelection.getFirstElement();

				if (element instanceof IFile) {
					fileResource = (IFile) element;
				}
				else {
					return;
				}
			}
			ValidateDTDAction validateaction = new ValidateDTDAction(fileResource, true);
			validateaction.setValidator(new DTDValidator());
			validateaction.run();
		}
		catch (Exception e) {
			// CS..here's where we need to pop up a dialog to tell the user
			// that xerces is not available
			//
			String xercesLine1 = DTDValidationMessages.Missing_required_files_1;
			String xercesLine2 = DTDValidationMessages.Missing_required_files_2;
			String xercesLine3 = DTDValidationMessages.Missing_required_files_3;
			MessageDialog.openError(Display.getDefault().getActiveShell(), DTDValidationMessages.Missing_required_files_4, xercesLine1 + xercesLine2 + xercesLine3);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
