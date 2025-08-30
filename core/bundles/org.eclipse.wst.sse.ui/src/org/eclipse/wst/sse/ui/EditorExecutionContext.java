/*******************************************************************************
 * Copyright (c) 2001, 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.core.internal.IExecutionDelegate;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

class EditorExecutionContext implements IExecutionDelegate {

	/**
	 * Reusable runnable for the Display execution queue to cut down on
	 * garbage creation. Will make use of the progress service if possible.
	 */
	private static class ReusableUIRunner implements Runnable, IRunnableWithProgress {
		private StructuredTextEditor textEditor;
		private IEditorPart editorPart = null;
		private ISafeRunnable fRunnable = null;

		ReusableUIRunner(IEditorPart editorPart, StructuredTextEditor textEditor) {
			super();
			this.textEditor = textEditor;
			this.editorPart = editorPart;
		}

		/*
		 * Expected to only be run by Display queue in the UI Thread
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			IWorkbenchPartSite site = editorPart.getSite();
			final IWorkbenchWindow workbenchWindow = (site == null) ? null : site.getWorkbenchWindow();
			final IWorkbenchSiteProgressService jobService = (site == null) ? null : site.getAdapter(IWorkbenchSiteProgressService.class);
			/*
			 * Try to use the progress service so the workbench can give more
			 * feedback to the user (although editors seem to make less use of
			 * the service than views -
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=86221 .
			 */
			if (workbenchWindow != null && jobService != null) {
				/*
				 * Doc is ambiguous, but it must be run from the UI thread -
				 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=165180
				 */
				try {
					jobService.runInUI(workbenchWindow, this, editorPart.getEditorInput().getAdapter(IResource.class));
				}
				catch (InvocationTargetException e) {
					Logger.logException(e);
				}
				catch (InterruptedException e) {
					Logger.logException(e);
				}
			}
			else {
				/*
				 * Run it directly and direct the UI of the editor. See
				 * StructuredTextEditor's begin/end background job for other
				 * activities to best accommodate (for example, there is a
				 * "timed delay" before the editor itself leaves
				 * background-update mode). NOTE: this execute method itself
				 * is always called from inside of an ILock block, so another
				 * block is not not needed here for all these sycnExec's.
				 */
				IWorkbench workbench = SSEUIPlugin.getInstance().getWorkbench();
				final Display display = workbench.getDisplay();
				if (display != null && !display.isDisposed()) {
					if (textEditor != null) {
						textEditor.beginBackgroundOperation();
					}
					try {
						/*
						 * Here's where the document update/modification
						 * occurs
						 */
						SafeRunner.run(fRunnable);
					}
					finally {
						/*
						 * This 'end' is just a signal to editor that this
						 * particular update is done. Its up to the editor to
						 * decide exactly when to leave its "background mode"
						 */
						if (textEditor != null) {
							textEditor.endBackgroundOperation();
						}
					}
				}
				fRunnable = null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse
		 * .core.runtime.IProgressMonitor)
		 */
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if (fRunnable != null)
				SafeRunner.run(fRunnable);
		}

		void setRunnable(ISafeRunnable r) {
			fRunnable = r;
		}
	}

	StructuredTextEditor fTextEditor;
	IEditorPart fEditorPart = null;
	private ReusableUIRunner fReusableRunner;

	public EditorExecutionContext(IEditorPart editorPart, StructuredTextEditor editor) {
		super();
		fEditorPart = editorPart;
		fTextEditor = editor;
		fReusableRunner = new ReusableUIRunner(fEditorPart, fTextEditor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.IExecutionDelegate#execute(java.lang
	 * .Runnable)
	 */
	public void execute(final ISafeRunnable runnable) {
		IWorkbench workbench = SSEUIPlugin.getInstance().getWorkbench();
		final Display display = workbench.getDisplay();
		if (display.getThread() == Thread.currentThread()) {
			// *If already in display thread, we can simply run, "as usual"*/
			SafeRunner.run(runnable);
		}
		else {
			// *otherwise run through the reusable runner */
			fReusableRunner.setRunnable(runnable);
			display.syncExec(fReusableRunner);
		}
	}
}
