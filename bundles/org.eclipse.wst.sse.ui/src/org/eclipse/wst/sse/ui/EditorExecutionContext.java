/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.core.internal.IExecutionDelegate;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

class EditorExecutionContext implements IExecutionDelegate {

	StructuredTextEditor fEditor;

	public EditorExecutionContext(StructuredTextEditor editor) {
		super();
		fEditor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.IExecutionDelegate#execute(java.lang
	 * .Runnable)
	 */
	public void execute(final Runnable runnable) {
		IWorkbench workbench = SSEUIPlugin.getInstance().getWorkbench();
		final Display display = workbench.getDisplay();
		if (display.getThread() == Thread.currentThread()) { 
			// *If already in display thread, we can simply run, "as usual"*/
			runnable.run();
		}
		else {
			/*
			 * This is the part that's really new, that accommodates a change
			 * in a document from background thread, by forcing it on the
			 * display thread.
			 */
			final StructuredTextEditor editor = fEditor;
			IWorkbenchWindow workbenchWindow = editor.getEditorPart().getEditorSite().getWorkbenchWindow();
			IWorkbenchSiteProgressService jobService = (IWorkbenchSiteProgressService) editor.getEditorPart().getSite().getAdapter(IWorkbenchSiteProgressService.class);
			if (workbenchWindow != null && jobService != null) {
				try {
					jobService.runInUI(editor.getEditorPart().getEditorSite().getWorkbenchWindow(), wrapWithProgress(runnable), (ISchedulingRule) editor.getEditorInput().getAdapter(IResource.class));
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
				 * If not in Display Thread, we "force" to run on display
				 * thread. see editors begin/end background job for other
				 * activities to best accommodate (for example, there is a
				 * "timed delay" before the editor itself leaves
				 * background-update mode). NOTE: this execute method itself
				 * is always called from inside of an ILock block, so another
				 * block is not not needed here for all these sycnExec's.
				 */
				display.syncExec(new Runnable() {
					public void run() {
						if (display != null && !display.isDisposed()) {
							editor.beginBackgroundOperation();
							try {
								/*
								 * Here's where the document
								 * update/modification occurs
								 */
								runnable.run();
							}
							finally {
								/*
								 * This 'end' is just a signal to editor that
								 * this particular update is done. Its up to
								 * the editor to decide exactly when to leave
								 * its "background mode"
								 */
								editor.endBackgroundOperation();
							}
						}
					}
				});
			}
		}
	}

	/**
	 * @param runnable
	 * @return
	 */
	private IRunnableWithProgress wrapWithProgress(final Runnable runnable) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (!monitor.isCanceled()) {
					runnable.run();
				}
			}
		};
	}
}
