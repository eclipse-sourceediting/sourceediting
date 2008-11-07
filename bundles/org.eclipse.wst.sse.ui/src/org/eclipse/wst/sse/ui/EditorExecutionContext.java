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

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.IExecutionDelegate;



class EditorExecutionContext implements IExecutionDelegate {


	StructuredTextEditor fEditor;

	public EditorExecutionContext(StructuredTextEditor editor) {
		super();
		fEditor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.document.IExecutionDelegate#execute(java.lang.Runnable)
	 */
	public void execute(final Runnable runnable) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		if (display.getThread() == Thread.currentThread()) {
			// if already in display thread, we can simply run, "as usual"
			runnable.run();
		} else {
			// this is the part that's really new, that
			// accomidate's a change in a document
			// from background thread, by forcing it on
			// the display thread.
			final StructuredTextEditor editor = fEditor;
			// if not in display thread, we "force" to run on display thread.
			// see editors begin/end background job for other
			// activities to best accomidate (for example, there
			// is a "timed delay" before the editor itself leaves
			// background-update mode).
			// NOTE: this execute method itself is always called from
			// inside of an ILock block, so another
			// block is not not needed here for all these sycnExec's
			display.syncExec(new Runnable() {
				public void run() {
					if (display != null && !display.isDisposed()) {
						editor.beginBackgroundOperation();
						try {
							// here's where the document update/modification
							// occurs
							runnable.run();

							// for future, possibly explore solutions such as
							// this
							//							IWorkbenchSiteProgressService jobService =
							// (IWorkbenchSiteProgressService)
							// editor.getEditorPart().getSite().getAdapter(IWorkbenchSiteProgressService.class);
							//							jobService.runInUI(xxxxx)
						} finally {
							// this 'end' is just a signal to editor that this
							// particular update is done. Its up to the editor
							// to decide exactly when to leave its "background
							// mode"
							editor.endBackgroundOperation();
						}
					}
				}
			});
		}
	}
}

