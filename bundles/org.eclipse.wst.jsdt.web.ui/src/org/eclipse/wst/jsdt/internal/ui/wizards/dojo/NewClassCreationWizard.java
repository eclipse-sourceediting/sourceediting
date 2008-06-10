package org.eclipse.wst.jsdt.internal.ui.wizards.dojo;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.JavaPluginImages;
import org.eclipse.wst.jsdt.internal.ui.wizards.NewElementWizard;

public class NewClassCreationWizard extends NewElementWizard {

	private NewClassWizardPage fPage;
	private boolean fOpenEditorOnFinish;

	public NewClassCreationWizard() {
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(JavaScriptPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Dojo Class");

		fPage = null;
		fOpenEditorOnFinish = true;

	}

	/*
	 * @see Wizard#createPages
	 */
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new NewClassWizardPage();
			fPage.init(getSelection());
			fPage.setWizard(this);
		}
		addPage(fPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !fPage.isEnclosingTypeSelected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res = super.performFinish();
		if (res) {
			IResource resource = fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (fOpenEditorOnFinish) {
					openResource((IFile) resource);
				}
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaScriptElement getCreatedElement() {
		return fPage.getCreatedType();
	}

}
