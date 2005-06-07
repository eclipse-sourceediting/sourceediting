package org.eclipse.wst.dtd.ui.internal.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.Logger;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;

public class NewDTDWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage fNewFilePage;
	private IStructuredSelection fSelection;

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
		IFile file = fNewFilePage.createNewFile();
		openEditor(file);
		return true;
	}

}