/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDNamedComponent;



/**
 * Central access point to execute rename refactorings.
 * <p>
 * Note: this class is not intended to be subclassed.
 * </p>
 */
public class RenameSupport {

	private RenameRefactoring fRefactoring;
	private RefactoringStatus fPreCheckStatus;
	
	/**
	 * Executes some light weight precondition checking. If the returned status
	 * is an error then the refactoring can't be executed at all. However,
	 * returning an OK status doesn't guarantee that the refactoring can be
	 * executed. It may still fail while performing the exhaustive precondition
	 * checking done inside the methods <code>openDialog</code> or
	 * <code>perform</code>.
	 * 
	 * The method is mainly used to determine enable/disablement of actions.
	 * 
	 * @return the result of the light weight precondition checking.
	 * 
	 * @throws CoreException if an unexpected exception occurs while performing the checking.
	 * 
	 * @see #openDialog(Shell)
	 * @see #perform(Shell, IRunnableContext)
	 */
	public IStatus preCheck() throws CoreException {
		ensureChecked();
		if (fPreCheckStatus.hasFatalError())
			return asStatus(fPreCheckStatus.getEntryMatchingSeverity(RefactoringStatus.FATAL));
		else
			return new Status(IStatus.OK, XSDEditorPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}

	/**
	 * Opens the refactoring dialog for this rename support. 
	 * 
	 * @param parent a shell used as a parent for the refactoring dialog.
	 * @throws CoreException if an unexpected exception occurs while opening the
	 * dialog.
	 */
	public void openDialog(Shell parent) throws CoreException {
		ensureChecked();
		if (fPreCheckStatus.hasFatalError()) {
			showInformation(parent, fPreCheckStatus);
			return; 
		}
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					fRefactoring,
					RefactoringMessages.getString("RenameComponentWizard.defaultPageTitle"), //$NON-NLS-1$ TODO: provide correct strings
					RefactoringMessages.getString("RenameComponentWizard.inputPage.description"), //$NON-NLS-1$
					null);
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			int result= op.run(parent, wizard.getDefaultPageTitle());
			op.getInitialConditionCheckingStatus();
			if (result == IDialogConstants.CANCEL_ID || result == RefactoringWizardOpenOperation.INITIAL_CONDITION_CHECKING_FAILED)
				triggerBuild();
		} catch (InterruptedException e) {
			// do nothing. User action got cancelled
		}
	
	}
	
	public void triggerBuild() {
		if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
			new GlobalBuildAction(XSDEditorPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow(), IncrementalProjectBuilder.INCREMENTAL_BUILD).run();
		}
	}
	
	/**
	 * Executes the rename refactoring without showing a dialog to gather
	 * additional user input (for example the new name of the <tt>IJavaElement</tt>).
	 * Only an error dialog is shown (if necessary) to present the result
	 * of the refactoring's full precondition checking.
	 * <p>
	 * The method has to be called from within the UI thread. 
	 * </p>
	 * 
	 * @param parent a shell used as a parent for the error dialog.
	 * @param context a {@link IRunnableContext} to execute the operation.
	 * 
	 * @throws InterruptedException if the operation has been cancelled by the
	 * user.
	 * @throws InvocationTargetException if an error occurred while executing the
	 * operation.
	 * 
	 * @see #openDialog(Shell)
	 * @see IRunnableContext#run(boolean, boolean, org.eclipse.jface.operation.IRunnableWithProgress)
	 */
	public void perform(Shell parent, IRunnableContext context) throws InterruptedException, InvocationTargetException {
		try {
			ensureChecked();
			if (fPreCheckStatus.hasFatalError()) {
				showInformation(parent, fPreCheckStatus);
				return; 
			}
		} catch (CoreException e){
			throw new InvocationTargetException(e);
		}
	}
	


	
	private RenameSupport(RenameComponentProcessor processor, String newName) throws CoreException {
		fRefactoring= new RenameRefactoring(processor);
		
	}

	
	/**
	 * Creates a new rename support for the given {@link IPackageFragment}.
	 * 
	 * @param fragment the {@link IPackageFragment} to be renamed.
	 * @param newName the package fragment's new name. <code>null</code> is a
	 * valid value indicating that no new name is provided.
	 * @param flags flags controlling additional parameters. Valid flags are
	 * <code>UPDATE_REFERENCES</code>, and <code>UPDATE_TEXTUAL_MATCHES</code>,
	 * or their bitwise OR, or <code>NONE</code>.
	 * @return the {@link RenameSupport}.
	 * @throws CoreException if an unexpected error occurred while creating
	 * the {@link RenameSupport}.
	 */
	public static RenameSupport create(XSDNamedComponent component, String newName) throws CoreException {
		RenameComponentProcessor processor= new RenameComponentProcessor(component, newName);
		return new RenameSupport(processor, newName);
	}
	
	
	private void ensureChecked() throws CoreException {
		if (fPreCheckStatus == null) {
			if (!fRefactoring.isApplicable()) {
				fPreCheckStatus= RefactoringStatus.createFatalErrorStatus(RefactoringMessages.getString("RenameSupport.not_available")); //$NON-NLS-1$
			} else {
				fPreCheckStatus= new RefactoringStatus();
			}
		}
	}
	
	private void showInformation(Shell parent, RefactoringStatus status) {
		String message= status.getMessageMatchingSeverity(RefactoringStatus.FATAL);
		MessageDialog.openInformation(parent, RefactoringMessages.getString("RenameSupport.dialog.title"), message); //$NON-NLS-1$
	}
	
	private static IStatus asStatus(RefactoringStatusEntry entry) {
		int statusSeverity= IStatus.ERROR;
		switch (entry.getSeverity()) {
			case RefactoringStatus.OK :
				statusSeverity= IStatus.OK;
				break;
			case RefactoringStatus.INFO :
				statusSeverity= IStatus.INFO;
				break;
			case RefactoringStatus.WARNING :
			case RefactoringStatus.ERROR :
				statusSeverity= IStatus.WARNING;
				break;
		}
		String pluginId= entry.getPluginId();
		int code= entry.getCode();
		if (pluginId == null) {
			pluginId= XSDEditorPlugin.PLUGIN_ID;
			code= IStatus.ERROR;
		}
		return new Status(statusSeverity, pluginId, code, entry.getMessage(), null);
	}
}
