/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.dtd.core.internal.DTDCorePlugin;
import org.eclipse.wst.dtd.core.internal.preferences.DTDCorePreferenceNames;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.Logger;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;

public class NewDTDWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage fNewFilePage;
	private NewDTDTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;
	private static final String defaultName = "NewFile"; //$NON-NLS-1$
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

		Preferences preference = DTDCorePlugin.getInstance().getPluginPreferences();
		String ext = preference.getString(DTDCorePreferenceNames.DEFAULT_EXTENSION);

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
			fContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForDTD.ContentTypeID_DTD);
		return fContentType;
	}

	/**
	 * Get list of valid extensions for DTD Content type
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

	private String applyLineDelimiter(IFile file, String text) {
		String lineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), new IScopeContext[] {new ProjectScope(file.getProject()), new InstanceScope() });//$NON-NLS-1$
		String convertedText = StringUtils.replace(text, "\r\n", "\n");
		convertedText = StringUtils.replace(convertedText, "\r", "\n");
		convertedText = StringUtils.replace(convertedText, "\n", lineDelimiter);
		return convertedText;
	}

	/**
	 * Verifies if fileName is valid name for content type. Takes base content
	 * type into consideration.
	 * 
	 * @param fileName
	 * @return true if extension is valid for this content type
	 */
	boolean extensionValidForContentType(String fileName) {
		boolean valid = false;

		IContentType type = getContentType();
		// there is currently an extension
		if (fileName.lastIndexOf('.') != -1) {
			// check what content types are associated with current extension
			IContentType[] types = Platform.getContentTypeManager().findContentTypesFor(fileName);
			int i = 0;
			while (i < types.length && !valid) {
				valid = types[i].isKindOf(type);
				++i;
			}
		}
		else
			valid = true; // no extension so valid
		return valid;
	}
	
	public void addPages() {
		fNewFilePage = new WizardNewFileCreationPage("DTDWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))) { //$NON-NLS-1$
			public void createControl(Composite parent) {
				// inherit default container and name specification widgets
				super.createControl(parent);
				setFileName(computeDefaultFileName());
				setPageComplete(validatePage());
			}
			protected String computeDefaultFileName() {
				int count = 0;
				String fileName = addDefaultExtension(defaultName);
				IPath containerFullPath = getContainerFullPath();
				if (containerFullPath != null) {
					while (true) {
						IPath path = containerFullPath.append(fileName);
						if (ResourcesPlugin.getWorkspace().getRoot().exists(path)) {
							count++;
							fileName = addDefaultExtension(defaultName + count);
						}
						else {
							break;
						}
					}
				}
				return fileName;
			}
			protected boolean validatePage() {
				String fileName = getFileName();
				IPath fullPath = getContainerFullPath();
				if ((fullPath != null) && (fullPath.isEmpty() == false) && (fileName != null)) {
					// check that filename does not contain invalid extension
					if (!extensionValidForContentType(fileName)) {
						setErrorMessage(NLS.bind(DTDUIMessages._ERROR_FILENAME_MUST_END_DTD, getValidExtensions().toString()));
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
							setErrorMessage(DTDUIMessages.ResourceGroup_nameExists);
							return false;
						}
					}
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
				templateString = applyLineDelimiter(file, templateString);
				// determine the encoding for the new file
				String charSet = getAppropriateCharset();
				
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
					Logger.log(Logger.WARNING_DEBUG, "Could not create contents for new DTD file", e); //$NON-NLS-1$
				}
			}

			// open the file in editor
			openEditor(file);

			// everything's fine
			performedOK = true;
		}
		return performedOK;
	}
	
	/**
	 * If the DTD preference identifies a charset, use that. If not, revert to the XML
	 * charset preference
	 * 
	 * @return charset based on DTD preferences if defined, if not, from the XML preferences
	 */
	private String getAppropriateCharset() {
		String charset = DTDCorePlugin.getInstance().getPluginPreferences().getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
		if(charset == null || charset.trim().equals(""))
			charset = XMLCorePlugin.getDefault().getPluginPreferences().getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
		return charset;
	}

	
}