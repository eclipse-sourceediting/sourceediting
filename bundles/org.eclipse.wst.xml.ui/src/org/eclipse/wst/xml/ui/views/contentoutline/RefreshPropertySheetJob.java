/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.views.contentoutline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.PropertySheetPage;


public class RefreshPropertySheetJob extends UIJob {


	private PropertySheetPage fPropertySheetPage;

	/**
	 * @param jobDisplay
	 * @param name
	 */
	public RefreshPropertySheetJob(Display jobDisplay, String name, PropertySheetPage propertySheetPage) {
		super(jobDisplay, name);
		setPriority(Job.SHORT);
		fPropertySheetPage = propertySheetPage;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		try {
			Control control = fPropertySheetPage.getControl();
			// we should have check before even scheduling this, but even if
			// ok then, need to check again, right before executing.
			if (control != null && !control.isDisposed()) {
				fPropertySheetPage.refresh();
			}
		} catch (Exception e) {
			result = errorStatus(e);
		} finally {
			monitor.done();
		}
		return result;
	}
}
