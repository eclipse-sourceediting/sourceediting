/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.dtd.core.internal.DTDCorePlugin;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.Logger;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;

public class NewDTDWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage fNewFilePage;
	private NewDTDTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;
	private List fValidExtensions = null;

	/**
	 * Get list of valid extensions for DTD Content type
	 * 
	 * @return
	 */
	List getValidExtensions() {
		if (fValidExtensions == null) {
			IContentType type = Platform.getContentTypeManager().getContentType(ContentTypeIdForDTD.ContentTypeID_DTD);
			fValidExtensions = new ArrayList(Arrays.asList(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)));
		}
		return fValidExtensions;
	}
	
	public void addPages() {
		fNewFilePage = new WizardNewFileCreationPage("DTDWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))) { //$NON-NLS-1$
			protected boolean validatePage() {
				IPath handlePath = new Path(getFileName());
				String extension = handlePath.getFileExtension();
				if (extension == null || !extension.equalsIgnoreCase("dtd")) { //$NON-NLS-1$
					setErrorMessage(DTDUIMessages._ERROR_FILENAME_MUST_END_DTD); //$NON-NLS-1$
					return false;
				}
				setErrorMessage(null);
				return super.validatePage();
			}
		};
		fNewFilePage.setTitle(DTDUIMessages._UI_CREATE_NEW_DTD_FILE); 
		fNewFilePage.setDescription(DTDUIMessages._UI_WIZARD_NEW_DTD_EXPL); 

		addPage(fNewFilePage);
		
		fNewFileTemplatesPage = new NewDTDTemplatesWizardPage();
		addPage(fNewFileTemplatesPage);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(DTDUIMessages._UI_WIZARD_NEW_DTD_TITLE); //$NON-NLS-1$
		
		ImageDescriptor descriptor = DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_WIZBAN_NEWDTDFILE);
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
		// save user options for next use
		fNewFileTemplatesPage.saveLastSavedPreferences();
		
		IFile file = fNewFilePage.createNewFile();
		
		// put template contents into file
		String templateString = fNewFileTemplatesPage.getTemplateString();
		if (templateString != null) {
			// determine the encoding for the new file
			Preferences preference = DTDCorePlugin.getInstance().getPluginPreferences();
			String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				OutputStreamWriter outputStreamWriter = null;
				if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
					// just use default encoding
					outputStreamWriter = new OutputStreamWriter(outputStream);
				} else {
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
				Logger.log(Logger.WARNING_DEBUG, "Could not create contents for new DTD file", e); //$NON-NLS-1$
			}
		}

		// open the file in editor
		openEditor(file);
		return true;
	}

}