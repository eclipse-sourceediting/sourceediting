package org.eclipse.wst.css.ui.internal.wizard;

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
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.Logger;

public class NewCSSWizard extends Wizard implements INewWizard {
	static String PAGE_IMAGE = "/icons/full/wizban/newcssfile_wiz.gif";
	private WizardNewFileCreationPage fNewFilePage;
	private IStructuredSelection fSelection;

	public void addPages() {
		fNewFilePage = new WizardNewFileCreationPage("CSSWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))) {
			protected boolean validatePage() {
				IPath handlePath = new Path(getFileName());
				String extension = handlePath.getFileExtension();
				if (extension == null || !extension.equalsIgnoreCase("css")) {
					setErrorMessage(CSSUIPlugin.getResourceString("%_ERROR_FILENAME_MUST_END_CSS"));
					return false;
				}
				setErrorMessage(null);
				return super.validatePage();
			}
		};
		fNewFilePage.setTitle(CSSUIPlugin.getResourceString("%_UI_WIZARD_NEW_HEADING"));
		fNewFilePage.setDescription(CSSUIPlugin.getResourceString("%_UI_WIZARD_NEW_DESCRIPTION"));

		addPage(fNewFilePage);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(CSSUIPlugin.getResourceString("%_UI_WIZARD_NEW_TITLE"));
		if (PAGE_IMAGE != null) {
			ImageDescriptor descriptor = CSSUIPlugin.getDefault().getImageRegistry().getDescriptor(PAGE_IMAGE);
			if (descriptor == null) {
				descriptor = ImageDescriptor.createFromURL(CSSUIPlugin.getDefault().getBundle().getEntry(PAGE_IMAGE));
				CSSUIPlugin.getDefault().getImageRegistry().put(PAGE_IMAGE, descriptor);
			}
			setDefaultPageImageDescriptor(descriptor);
		}
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