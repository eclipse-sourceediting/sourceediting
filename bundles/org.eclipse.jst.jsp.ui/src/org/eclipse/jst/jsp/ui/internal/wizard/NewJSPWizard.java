package org.eclipse.jst.jsp.ui.internal.wizard;

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
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;

public class NewJSPWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage fNewFilePage;
	private NewJSPTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;
	private List fValidExtensions = null;

	/**
	 * Get list of valid extensions for JSP Content type
	 * 
	 * @return
	 */
	List getValidExtensions() {
		if (fValidExtensions == null) {
			IContentType type = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
			fValidExtensions = new ArrayList(Arrays.asList(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)));
		}
		return fValidExtensions;
	}

	public void addPages() {
		fNewFilePage = new WizardNewFileCreationPage("JSPWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))) { //$NON-NLS-1$
			protected boolean validatePage() {
				IPath handlePath = new Path(getFileName());
				String extension = handlePath.getFileExtension();
				if (extension == null || !getValidExtensions().contains(extension)) {
					setErrorMessage(NLS.bind(JSPUIMessages._ERROR_FILENAME_MUST_END_JSP, getValidExtensions().toString()));
					return false;
				}
				setErrorMessage(null);
				return super.validatePage();
			}
		};
		fNewFilePage.setTitle(JSPUIMessages._UI_WIZARD_NEW_HEADING);
		fNewFilePage.setDescription(JSPUIMessages._UI_WIZARD_NEW_DESCRIPTION);

		addPage(fNewFilePage);

		fNewFileTemplatesPage = new NewJSPTemplatesWizardPage();
		addPage(fNewFileTemplatesPage);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(JSPUIMessages._UI_WIZARD_NEW_TITLE);
		
		ImageDescriptor descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.IMG_OBJ_WIZBAN_NEWJSPFILE);
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

		// create a new empty file
		IFile file = fNewFilePage.createNewFile();

		// put template contents into file
		String templateString = fNewFileTemplatesPage.getTemplateString();
		if (templateString != null) {
			// determine the encoding for the new file
			Preferences preference = JSPCorePlugin.getDefault().getPluginPreferences();
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
				Logger.log(Logger.WARNING_DEBUG, "Could not create contents for new JSP file", e); //$NON-NLS-1$
			}
		}

		// open the file in editor
		openEditor(file);
		return true;
	}

}