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
package org.eclipse.wst.xml.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.contentmodel.modelquery.CMDocumentManager;

/**
 * Updates the status line when an appropriate Job is about to be run.
 */
public class JobStatusLineHelper extends JobChangeAdapter {
	private int running = 0;
	private static JobStatusLineHelper instance;

	public static void init() {
		if (instance == null) {
			instance = new JobStatusLineHelper();
		}
	}

	private JobStatusLineHelper() {
		Platform.getJobManager().addJobChangeListener(this);
	}

	public void aboutToRun(IJobChangeEvent event) {
		Job job = event.getJob();
		if (job.belongsTo(CMDocumentManager.class)) {
			running++;
			setStatusLine(event.getJob().getName());
		}
	}

	public void done(IJobChangeEvent event) {
		Job job = event.getJob();
		if (job.belongsTo(CMDocumentManager.class)) {
			running--;
			if (running == 0) {
				setStatusLine(""); //$NON-NLS-1$
			}
		}
	}

	private void setStatusLine(final String message) {
		String msgString = message;
		if (message == null) {
			msgString = ""; //$NON-NLS-1$
		}
		final String finalMessageForThread = msgString;
		if (getDisplay() != null) {
			Runnable runnable = new Runnable() {
				public void run() {
					IWorkbench workbench = PlatformUI.getWorkbench();
					if (workbench != null) {
						IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
						if (workbenchWindow != null) {
							IEditorPart part = workbenchWindow.getActivePage().getActiveEditor();
							// part is sometimes null by the time this runs
							// ... must be better way to get actionBars
							// and/or statLineManager?
							if (part != null) {
								IActionBars actionBars = part.getEditorSite().getActionBars();
								if (actionBars != null) {
									IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
									if (statusLineManager != null) {
										statusLineManager.setMessage(finalMessageForThread);
									}
								}
							}
						}
					}
				}
			};
			Display workbenchDefault = PlatformUI.getWorkbench().getDisplay();
			workbenchDefault.asyncExec(runnable);
		}
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}
}
