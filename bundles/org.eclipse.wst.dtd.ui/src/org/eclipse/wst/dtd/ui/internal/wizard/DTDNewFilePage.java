package org.eclipse.wst.dtd.ui.internal.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;

class DTDNewFilePage extends WizardNewFileCreationPage {
	public DTDNewFilePage(IStructuredSelection selection) {
		super("", selection);
		setTitle(DTDUIPlugin.getResourceString("_UI_WIZARD_NEW_DTD_HEADING"));
		setDescription(DTDUIPlugin.getResourceString("_UI_WIZARD_NEW_DTD_EXPL"));
	}

	public void createControl(Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);
		setPageComplete(validatePage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createFileHandle(org.eclipse.core.runtime.IPath)
	 */
	protected IFile createFileHandle(IPath filePath) {
		// enforce the creation of a ".dtd" file
		IPath dtdFilePath = filePath;
		String extension = dtdFilePath.getFileExtension();
		if (extension == null || !extension.equalsIgnoreCase("dtd")) {
			dtdFilePath = dtdFilePath.addFileExtension("dtd");
		}
		return super.createFileHandle(dtdFilePath);
	}
}