/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.validation.core.errorinfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.ui.internal.validation.XMLValidationUIMessages;

public class ReferencedFileErrorActionDelegate extends ActionDelegate implements IObjectActionDelegate {
	protected IMarker selectedMarker;

	/**
	 * 
	 */
	public ReferencedFileErrorActionDelegate() {
		super();
	}

	public void run(IAction action) {
		if (selectedMarker != null) {
			try {

				Map map = (Map) selectedMarker.getResource().getSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME);
				if (map == null) {
					String infoUnavailable = XMLValidationUIMessages._UI_DETAILS_INFORMATION_UNAVAILABLE;
					String revalidateToRegenerateErrors = XMLValidationUIMessages._UI_DETAILS_INFO_REVALIDATE_TO_REGENERATE;
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), infoUnavailable, revalidateToRegenerateErrors);
				}
				else {
					String uri = null;

					String groupName = (String) selectedMarker.getAttribute("groupName"); //$NON-NLS-1$
					if (groupName.startsWith("referencedFileError")) //$NON-NLS-1$
					{
						int index1 = groupName.indexOf("("); //$NON-NLS-1$
						int index2 = groupName.lastIndexOf(")"); //$NON-NLS-1$
						if ((index1 != -1) && (index2 > index1)) {
							uri = groupName.substring(index1 + 1, index2);
						}
					}

					if (uri != null) {
						List list = Collections.EMPTY_LIST;

						ValidationMessage message = (ValidationMessage) map.get(uri);
						if (message != null) {
							list = message.getNestedMessages();
						}

						String validatedFileURI = selectedMarker.getResource().getLocation().toOSString();// URIHelper.normalize(selectedMarker.getResource().getLocation().toOSString());
						validatedFileURI = "file:/" + validatedFileURI; //$NON-NLS-1$

						ReferencedFileErrorDialog dialog = new ReferencedFileErrorDialog(Display.getCurrent().getActiveShell(), list, validatedFileURI, uri);
						dialog.createAndOpen();
					}
				}
			}
			catch (CoreException e) {
				// Do nothing.
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		selectedMarker = null;
		if (selection instanceof IStructuredSelection) {
			try {
				Object first = ((IStructuredSelection) selection).getFirstElement();

				IMarker marker = (IMarker) first;
				selectedMarker = marker;
				// String groupName = (String)
				// marker.getAttribute("groupName");
				// if (groupName.startsWith("referencedFileError"))
				// {
				// selectedMarker = marker;
				// }
			}
			catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// dw int i = 0;
	}
}

/*
 * private static void printMap(Map map) { for (Iterator i =
 * map.keySet().iterator(); i.hasNext();) { String key = (String) i.next();
 * System.out.println("entry : " + key + " = " + map.get(key)); } }
 * 
 * private void printErrorMap(Map map) { for (Iterator i =
 * map.keySet().iterator(); i.hasNext();) { String key = (String) i.next();
 * ErrorMessage message = (ErrorMessage) map.get(key); if (message != null) {
 * printErrorMessage(message); } } }
 * 
 * private void printErrorMessage(ErrorMessage errorMessage) {
 * System.out.println(errorMessage.getText()); for (Iterator i =
 * errorMessage.getNestedErrors().iterator(); i.hasNext();) {
 * printErrorMessage((ErrorMessage) i.next()); } }
 */
