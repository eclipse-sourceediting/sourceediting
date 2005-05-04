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
package org.eclipse.wst.sse.core.internal.tasks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Delegates for the main Task Scanner. Delegates may be contributed using the
 * org.eclipse.wst.sse.core.taskscanner extension point. For resources and
 * resource deltas with matching content types, the main scanner will first
 * call the startup() method, scan(), and then shutdown() in sequence. Scanner
 * instances will be reused across projects but are not shared per content
 * type. Delegates should not hold on to references to models or resources
 * after shutdown().
 */
public interface ITaskScannerDelegate {
	IStatus scan(IFile file, IProgressMonitor monitor);

	/**
	 * Notifies the delegate that scanning is done for now. Resources held
	 * from startup should now be released.
	 */
	void shutdown(IProject project);

	/**
	 * Notifies the delegate that a sequence of scans is about to be
	 * requested. Ideally the time to load preferences and perform any
	 * expensive configuration for the given project.
	 * 
	 * @param project -
	 *            the project that's about to be scanned
	 */
	void startup(IProject project);
}
