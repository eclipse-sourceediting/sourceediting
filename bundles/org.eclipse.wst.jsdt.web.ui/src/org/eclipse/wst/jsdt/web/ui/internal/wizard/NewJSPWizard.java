/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.jsdt.web.core.internal.JSPCorePlugin;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.jsdt.web.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.wst.jsdt.web.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;

public class NewJSPWizard extends Wizard implements INewWizard {
	private NewJSPFileWizardPage fNewFilePage;
	private NewJSPTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;

	@Override
	public void addPages() {
		fNewFilePage = new NewJSPFileWizardPage(
				"JSPWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))); //$NON-NLS-1$ 
		fNewFilePage.setTitle(JSPUIMessages._UI_WIZARD_NEW_HEADING);
		fNewFilePage.setDescription(JSPUIMessages._UI_WIZARD_NEW_DESCRIPTION);
		addPage(fNewFilePage);

		fNewFileTemplatesPage = new NewJSPTemplatesWizardPage();
		addPage(fNewFileTemplatesPage);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(JSPUIMessages._UI_WIZARD_NEW_TITLE);

		ImageDescriptor descriptor = JSPEditorPluginImageHelper.getInstance()
				.getImageDescriptor(
						JSPEditorPluginImages.IMG_OBJ_WIZBAN_NEWJSPFILE);
		setDefaultPageImageDescriptor(descriptor);
	}

	private void openEditor(final IFile file) {
		if (file != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
						Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
					}
				}
			});
		}
	}

	@Override
	public boolean performFinish() {
		boolean performedOK = false;

		// save user options for next use
		fNewFileTemplatesPage.saveLastSavedPreferences();

		// no file extension specified so add default extension
		String fileName = fNewFilePage.getFileName();
		if (fileName.lastIndexOf('.') == -1) {
			String newFileName = fNewFilePage.addDefaultExtension(fileName);
			fNewFilePage.setFileName(newFileName);
		}

		// create a new empty file
		IFile file = fNewFilePage.createNewFile();

		// if there was problem with creating file, it will be null, so make
		// sure to check
		if (file != null) {
			// put template contents into file
			String templateString = fNewFileTemplatesPage.getTemplateString();
			if (templateString != null) {
				// determine the encoding for the new file
				Preferences preference = JSPCorePlugin.getDefault()
						.getPluginPreferences();
				String charSet = preference
						.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					OutputStreamWriter outputStreamWriter = null;
					if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
						// just use default encoding
						outputStreamWriter = new OutputStreamWriter(
								outputStream);
					} else {
						outputStreamWriter = new OutputStreamWriter(
								outputStream, charSet);
					}
					outputStreamWriter.write(templateString);
					outputStreamWriter.flush();
					outputStreamWriter.close();
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
							outputStream.toByteArray());
					file.setContents(inputStream, true, false, null);
					inputStream.close();
				} catch (Exception e) {
					Logger.log(Logger.WARNING_DEBUG,
							"Could not create contents for new JSP file", e); //$NON-NLS-1$
				}
			}

			// open the file in editor
			openEditor(file);

			// everything's fine
			performedOK = true;
		}
		return performedOK;
	}

}