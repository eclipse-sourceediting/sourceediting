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
package org.eclipse.wst.html.ui.internal.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;

public class NewHTMLWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage fNewFilePage;
	private NewHTMLTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;
	private IContentType fContentType;
	private List fValidExtensions = null;

	/**
	 * Adds default extension to the filename
	 * 
	 * @param filename
	 * @return
	 */
	String addDefaultExtension(String filename) {
		StringBuffer newFileName = new StringBuffer(filename);

		Preferences preference = HTMLCorePlugin.getDefault().getPluginPreferences();
		String ext = preference.getString(HTMLCorePreferenceNames.DEFAULT_EXTENSION);

		newFileName.append("."); //$NON-NLS-1$
		newFileName.append(ext);

		return newFileName.toString();
	}

	/**
	 * Get content type associated with this new file wizard
	 * 
	 * @return IContentType
	 */
	IContentType getContentType() {
		if (fContentType == null)
			fContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForHTML.ContentTypeID_HTML);
		return fContentType;
	}

	/**
	 * Get list of valid extensions for HTML Content type
	 * 
	 * @return
	 */
	List getValidExtensions() {
		if (fValidExtensions == null) {
			IContentType type = getContentType();
			fValidExtensions = new ArrayList(Arrays.asList(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)));
		}
		return fValidExtensions;
	}

	public void addPages() {
		fNewFilePage = new WizardNewFileCreationPage("HTMLWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))) { //$NON-NLS-1$
			protected boolean validatePage() {
				IContentType type = getContentType();
				String fileName = getFileName();
				IPath fullPath = getContainerFullPath();
				if ((fullPath != null) && (fullPath.isEmpty() == false) && (fileName != null)) {
					// check that filename does not contain invalid extension
					if ((fileName.lastIndexOf('.') != -1) && (!type.isAssociatedWith(fileName))) {
						setErrorMessage(NLS.bind(HTMLUIMessages._ERROR_FILENAME_MUST_END_HTML, getValidExtensions().toString()));
						return false;
					}
					// no file extension specified so check adding default
					// extension doesn't equal a file that already exists
					if (fileName.lastIndexOf('.') == -1) {
						String newFileName = addDefaultExtension(fileName);
						IPath resourcePath = fullPath.append(newFileName);

						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IStatus result = workspace.validatePath(resourcePath.toString(), IResource.FOLDER);
						if (!result.isOK()) {
							// path invalid
							setErrorMessage(result.getMessage());
							return false;
						}

						if ((workspace.getRoot().getFolder(resourcePath).exists() || workspace.getRoot().getFile(resourcePath).exists())) {
							setErrorMessage(HTMLUIMessages.ResourceGroup_nameExists);
							return false;
						}
					}
				}

				setErrorMessage(null);
				return super.validatePage();
			}
		};
		fNewFilePage.setTitle(HTMLUIMessages._UI_WIZARD_NEW_HEADING);
		fNewFilePage.setDescription(HTMLUIMessages._UI_WIZARD_NEW_DESCRIPTION);

		addPage(fNewFilePage);

		fNewFileTemplatesPage = new NewHTMLTemplatesWizardPage();
		addPage(fNewFileTemplatesPage);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(HTMLUIMessages._UI_WIZARD_NEW_TITLE);

		ImageDescriptor descriptor = HTMLEditorPluginImageHelper.getInstance().getImageDescriptor(HTMLEditorPluginImages.IMG_WIZBAN_NEWHTMLFILE);
		setDefaultPageImageDescriptor(descriptor);
	}

	private void openEditor(final IFile file) {
		if (file != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, file, true);
					}
					catch (PartInitException e) {
						Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
					}
				}
			});
		}
	}

	public boolean performFinish() {
		boolean performedOK = false;
		// save user options for next use
		fNewFileTemplatesPage.saveLastSavedPreferences();

		// no file extension specified so add default extension
		String fileName = fNewFilePage.getFileName();
		if (fileName.lastIndexOf('.') == -1) {
			String newFileName = addDefaultExtension(fileName);
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
				Preferences preference = HTMLCorePlugin.getDefault().getPluginPreferences();
				String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					OutputStreamWriter outputStreamWriter = null;
					if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
						// just use default encoding
						outputStreamWriter = new OutputStreamWriter(outputStream);
					}
					else {
						outputStreamWriter = new OutputStreamWriter(outputStream, charSet);
					}
					outputStreamWriter.write(templateString);
					outputStreamWriter.flush();
					outputStreamWriter.close();
					ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
					file.setContents(inputStream, true, false, null);
					inputStream.close();
				}
				catch (Exception e) {
					Logger.log(Logger.WARNING_DEBUG, "Could not create contents for new HTML file", e); //$NON-NLS-1$
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