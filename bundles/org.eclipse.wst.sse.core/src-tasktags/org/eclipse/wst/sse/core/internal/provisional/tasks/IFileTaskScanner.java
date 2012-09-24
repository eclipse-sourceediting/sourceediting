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
package org.eclipse.wst.sse.core.internal.provisional.tasks;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;

/**
 * Scanners for the main Task Scanner. Scanners may be contributed using the
 * org.eclipse.wst.sse.core.taskscanner extension point. For resources and
 * resource deltas with matching content types, the main scanner will first
 * call the startup() method, scan(), and then shutdown() in sequence. Scanner
 * instances will be reused across projects but are not shared per content
 * type. Scanners should not hold on to references to models or resources
 * after shutdown() and should take care not to leak memory or resources.
 */
public interface IFileTaskScanner {
	/**
	 * Default marker type ID of task markers that are created.
	 */
	String TASK_MARKER_ID = SSECorePlugin.ID + ".task"; //$NON-NLS-1$;

	/**
	 * @return the task marker type that should be removed each time tasks are
	 *         rescanned and used by default when creating task markers.
	 *         Children of this marker type will be removed automatically.
	 */
	String getMarkerType();

	/**
	 * Requests that the list of automatically discovered tasks for the given
	 * file be updated. Once completed, the list of tasks should correspond
	 * exactly to the file's contents.
	 * 
	 * @param file -
	 *            the file to be scanned
	 * @param taskTags -
	 *            the list of task tags for which to scan
	 * @param monitor -
	 *            a progress monitor
	 * @return an array of maps containing the attributes of task markers to
	 *         be created
	 *         <p>
	 *         The reserved attribute name
	 *         <b>org.eclipse.core.resources.taskmarker</b> may be used to
	 *         specify a type to use when creating the task marker.
	 *         </p>
	 */
	Map[] scan(IFile file, TaskTag[] taskTags, IProgressMonitor monitor);

	/**
	 * Notifies the scanner that scanning is done for now. Resources held from
	 * startup should now be released.
	 * 
	 * @param project -
	 *            the project that was just scanned
	 */
	void shutdown(IProject project);

	/**
	 * Notifies the scanner that a sequence of scans is about to be requested.
	 * Ideally the time to load preferences and perform any expensive
	 * configuration for the given project.
	 * 
	 * @param project -
	 *            the project that is about to be scanned
	 * 
	 */
	void startup(IProject project);
}
