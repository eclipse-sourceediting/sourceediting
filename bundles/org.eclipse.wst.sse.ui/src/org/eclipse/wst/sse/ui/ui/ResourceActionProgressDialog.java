/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class ResourceActionProgressDialog extends IconAndMessageDialog implements IRunnableContext {
	protected Text fText = null;
	protected String fDialogTitle = ResourceHandler.getString("ResourceActionProgressDialog.0"); //$NON-NLS-1$
	protected String fActionCompletedMessage = ResourceHandler.getString("ResourceActionProgressDialog.1"); //$NON-NLS-1$
	protected String fActionCancelledMessage = ResourceHandler.getString("ResourceActionProgressDialog.2"); //$NON-NLS-1$

	/**
	 * Name to use for task when normal task name is empty string.
	 */
	private static String DEFAULT_TASKNAME = JFaceResources.getString("ProgressMonitorDialog.message"); //$NON-NLS-1$

	/**
	 * Constants for label and monitor size
	 */
	private static int LABEL_DLUS = 21;
	private static int BAR_DLUS = 9;


	/**
	 * The progress indicator control.
	 */
	protected ProgressIndicator progressIndicator;

	/**
	 * The label control for the task. Kept for backwards compatibility.
	 */
	protected Label taskLabel;

	/**
	 * The label control for the subtask.
	 */
	protected Label subTaskLabel;

	/**
	 * The Okay button control.
	 */
	protected Button okButton;

	/**
	 * The Cancel button control.
	 */
	protected Button cancel;

	/**
	 * Indicates whether the Cancel button is to be shown.
	 */
	protected boolean operationCancelableState = false;

	/**
	 * Indicates whether the Cancel button is to be enabled.
	 */
	protected boolean enableCancelButton;

	/**
	 * The progress monitor.
	 */
	private ProgressMonitor progressMonitor = new ProgressMonitor();

	/**
	 * The name of the current task (used by ProgressMonitor).
	 */
	private String task;

	/**
	 * The nesting depth of currently running runnables.
	 */
	private int nestingDepth;

	/**
	 * The cursor used in the cancel button;
	 */
	protected Cursor arrowCursor;

	/**
	 * The cursor used in the shell;
	 */
	private Cursor waitCursor;

	/**
	 * Flag indicating whether to open or merely create the dialog before run.
	 */
	private boolean openOnRun = true;

	/**
	 * Internal progress monitor implementation.
	 */
	private class ProgressMonitor implements IProgressMonitorWithBlocking {

		private String fSubTask = "";//$NON-NLS-1$
		private boolean fIsCanceled;
		protected boolean forked = false;
		protected boolean locked = false;

		public void beginTask(String name, int totalWork) {
			if (progressIndicator.isDisposed())
				return;

			if (name == null)
				task = "";//$NON-NLS-1$
			else
				task = name;

			String s = task;
			if (s.length() <= 0)
				s = DEFAULT_TASKNAME;
			setMessage(s);
			if (!forked)
				update();

			if (totalWork == UNKNOWN) {
				progressIndicator.beginAnimatedTask();
			}
			else {
				progressIndicator.beginTask(totalWork);
			}
		}

		public void done() {
			if (!progressIndicator.isDisposed()) {
				progressIndicator.sendRemainingWork();
				progressIndicator.done();
			}
		}

		public void setTaskName(String name) {
			if (name == null)
				task = "";//$NON-NLS-1$
			else
				task = name;

			String s = task;
			if (s.length() <= 0)
				s = DEFAULT_TASKNAME;
			setMessage(s);
			if (!forked)
				update();
		}

		public boolean isCanceled() {
			return fIsCanceled;
		}

		public void setCanceled(boolean b) {
			fIsCanceled = b;
			if (locked)
				clearBlocked();
		}

		public void subTask(String name) {
			if (subTaskLabel.isDisposed())
				return;

			if (name == null)
				fSubTask = "";//$NON-NLS-1$
			else
				fSubTask = name;

			subTaskLabel.setText(fSubTask);
			if (!forked)
				subTaskLabel.update();
		}

		public void worked(int work) {
			internalWorked(work);
		}

		public void internalWorked(double work) {
			if (!progressIndicator.isDisposed())
				progressIndicator.worked(work);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IProgressMonitorWithBlocking#clearBlocked()
		 */
		public void clearBlocked() {
			setMessage(task);
			locked = false;
			imageLabel.setImage(getImage());

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IProgressMonitorWithBlocking#setBlocked(org.eclipse.core.runtime.IStatus)
		 */
		public void setBlocked(IStatus reason) {
			setMessage(reason.getMessage());
			locked = true;
			imageLabel.setImage(getImage());

		}
	}

	/**
	 * Enables the cancel button (asynchronously).
	 */
	private void asyncSetOperationCancelButtonEnabled(final boolean b) {
		if (getShell() != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					setOperationCancelButtonEnabled(b);
				}
			});
		}
	}

	protected void cancelPressed() {
		//NOTE: this was previously done from a listener installed on the
		// cancel
		//button. On GTK, the listener installed by Dialog.createButton is
		// called
		//first and this was throwing an exception because the cancel button
		//was already disposed
		cancel.setEnabled(false);
		progressMonitor.setCanceled(true);
		super.cancelPressed();
	}

	/*
	 * (non-Javadoc) Method declared on Window.
	 */
	/**
	 * The <code>ProgressMonitorDialog</code> implementation of this method
	 * only closes the dialog if there are no currently running runnables.
	 */
	public boolean close() {
		if (getNestingDepth() <= 0) {
			clearCursors();
			return super.close();
		}
		return false;
	}

	/**
	 * Clear the cursors in the dialog.
	 */
	protected void clearCursors() {
		if (cancel != null && !cancel.isDisposed()) {
			cancel.setCursor(null);
		}
		Shell shell = getShell();
		if (shell != null && !shell.isDisposed()) {
			shell.setCursor(null);
		}
		if (arrowCursor != null)
			arrowCursor.dispose();
		if (waitCursor != null)
			waitCursor.dispose();
		arrowCursor = null;
		waitCursor = null;
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(fDialogTitle);
		if (waitCursor == null)
			waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);

		// cancel button
		cancel = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		if (arrowCursor == null)
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}


	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected Point getInitialSize() {

		Point calculatedSize = super.getInitialSize();
		if (calculatedSize.x < 450)
			calculatedSize.x = 450;
		return calculatedSize;
	}

	/**
	 * Returns the progress monitor to use for operations run in this progress
	 * dialog.
	 * 
	 * @return the progress monitor
	 */
	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/*
	 * (non-Javadoc) Method declared on IRunnableContext. Runs the given
	 * <code> IRunnableWithProgress </code> with the progress monitor for this
	 * progress dialog. The dialog is opened before it is run, and closed
	 * after it completes.
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		setCancelable(cancelable);
		try {
			aboutToRun();
			//Let the progress monitor know if they need to update in UI
			// Thread
			progressMonitor.forked = fork;
			ModalContext.run(runnable, fork, getProgressMonitor(), getShell().getDisplay());
		}
		finally {
			finishedRun();
		}
	}

	/**
	 * Returns whether the dialog should be opened before the operation is
	 * run. Defaults to <code>true</code>
	 * 
	 * @return <code>true</code> to open the dialog before run,
	 *         <code>false</code> to only create the dialog, but not open it
	 */
	public boolean getOpenOnRun() {
		return openOnRun;
	}

	/**
	 * Sets whether the dialog should be opened before the operation is run.
	 * NOTE: Setting this to false and not forking a process may starve any
	 * asyncExec that tries to open the dialog later.
	 * 
	 * @param openOnRun
	 *            <code>true</code> to open the dialog before run,
	 *            <code>false</code> to only create the dialog, but not open
	 *            it
	 */
	public void setOpenOnRun(boolean openOnRun) {
		this.openOnRun = openOnRun;
	}

	/**
	 * Returns the nesting depth of running operations.
	 * 
	 * @return the nesting depth of running operations
	 */
	protected int getNestingDepth() {
		return nestingDepth;
	}

	/**
	 * Increments the nesting depth of running operations.
	 */
	protected void incrementNestingDepth() {
		nestingDepth++;
	}

	protected void decrementNestingDepth() {
		nestingDepth--;
	}

	/**
	 * Called just before the operation is run. Default behaviour is to open
	 * or create the dialog, based on the setting of <code>getOpenOnRun</code>,
	 * and increment the nesting depth.
	 */
	protected void aboutToRun() {
		if (getOpenOnRun()) {
			open();
		}
		else {
			create();
		}
		incrementNestingDepth();
	}

	/**
	 * Called just after the operation is run. Default behaviour is to
	 * decrement the nesting depth, and close the dialog.
	 */
	protected void finishedRun() {
		decrementNestingDepth();

		if (progressMonitor.isCanceled())
			fText.append("\n" + fActionCancelledMessage); //$NON-NLS-1$
		else
			fText.append("\n" + fActionCompletedMessage); //$NON-NLS-1$
		clearCursors();
		okButton.setEnabled(true);
		cancel.setEnabled(false);
		getShell().setDefaultButton(okButton);
	}

	/**
	 * Sets whether the progress dialog is cancelable or not.
	 * 
	 * @param cancelable
	 *            <code>true</code> if the end user can cancel this progress
	 *            dialog, and <code>false</code> if it cannot be canceled
	 */
	public void setCancelable(boolean cancelable) {
		if (cancel == null)
			enableCancelButton = cancelable;
		else
			asyncSetOperationCancelButtonEnabled(cancelable);
	}

	/**
	 * Helper to enable/disable Cancel button for this dialog.
	 * 
	 * @param b
	 *            <code>true</code> to enable the cancel button, and
	 *            <code>false</code> to disable it
	 */
	protected void setOperationCancelButtonEnabled(boolean b) {
		operationCancelableState = b;
		cancel.setEnabled(b);
	}

	/*
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
	 */
	protected Image getImage() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		return display.getSystemImage(SWT.ICON_INFORMATION);
		//return JFaceResources.getImageRegistry().get(Dialog.DLG_IMG_INFO);
	}

	/**
	 * Set the message in the message label.
	 */
	private void setMessage(String messageString) {
		//must not set null text in a label
		message = messageString == null ? "" : messageString; //$NON-NLS-1$
		if (fText == null || fText.isDisposed())
			return;
		fText.append(message + "\n"); //$NON-NLS-1$
	}

	/**
	 * Update the message label. Required if the monitor is forked.
	 */
	private void update() {
		if (messageLabel == null || messageLabel.isDisposed())
			return;
		messageLabel.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		//Check to be sure it is not already done. If it is just return OK.
		if (!getOpenOnRun()) {
			if (getNestingDepth() == 0)
				return OK;
		}
		return super.open();
	}

	/**
	 * @param parent
	 */
	public ResourceActionProgressDialog(Shell parent) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL); // no
		// close
		// button
		setBlockOnOpen(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		fText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.NO_FOCUS | SWT.H_SCROLL);
		fText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = convertVerticalDLUsToPixels(300);
		gridData.widthHint = convertHorizontalDLUsToPixels(400);
		fText.setLayoutData(gridData);

		return composite;
	}

	/**
	 * @param dialogTitle The fDialogTitle to set.
	 */
	public void setDialogTitle(String dialogTitle) {
		fDialogTitle = dialogTitle;
	}

	/**
	 * @param actionCompletedMessage The fActionCompletedMessage to set.
	 */
	public void setActionCompletedMessage(String actionCompletedMessage) {
		fActionCompletedMessage = actionCompletedMessage;
	}

	/**
	 * @param actionCancelledMessage The fActionCancelledMessage to set.
	 */
	public void setActionCancelledMessage(String actionCancelledMessage) {
		fActionCancelledMessage = actionCancelledMessage;
	}
}
