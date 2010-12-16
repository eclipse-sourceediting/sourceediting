/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;

/**
 * @deprecated - no longer used
 */
public class IndexWorkspaceJob extends Job {

	// for debugging
	static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspindexmanager"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	public IndexWorkspaceJob() {
		// pa_TODO may want to say something like "Rebuilding JSP Index" to be more
		// descriptive instead of "Updating JSP Index" since they are 2 different things
		super(JSPCoreMessages.JSPIndexManager_0);
		setPriority(Job.LONG);
		setSystem(true);
	}

	/**
	 * @see org eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor) 
	 * for similar method
	 */
	protected IStatus run(IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}
	
	void setCanceledState() {}
}
