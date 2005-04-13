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
package org.eclipse.wst.sse.core.internal.ltk.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Delegates for the Structured Builder. Delegates may be contributed using
 * the org.eclipse.wst.sse.core.builderdelegate extension point. For resources and
 * resource deltas with matching content types, the builder will first call
 * the startup() method, build(), and shutdown in sequence. Participant
 * instances will be reused across projects but are not shared per content
 * type. Participants should not hold on to references to models or resources
 * after shutdown().
 */
public interface IBuilderDelegate {
	IStatus build(IFile file, int kind, Map args, IProgressMonitor monitor);

	/**
	 * Notifies the participant that building is done for now. Resources held
	 * from startup should now be released.
	 */
	void shutdown(IProject project);

	/**
	 * Notifies the participant that a sequence of builds is about to be
	 * called. Ideally the time to load preferences and perform any expensive
	 * configuration for the given project.
	 * 
	 * @param project -
	 *            the project that's about to be built
	 * @param kind -
	 *            the kind of build being done; possible values are
	 *            IncrementalProjectBuilder.AUTO_BUILD,
	 *            IncrementalProjectBuilder.FULL_BUILD, or
	 *            IncrementalProjectBuilder.INCREMENTAL_BUILD
	 * @param args
	 *            the table of builder-specific arguments sent to the
	 *            Structured Document Builder, keyed by argument name (key
	 *            type: <code>String</code>, value type:
	 *            <code>String</code>);<code>null</code> is equivalent
	 *            to an empty map
	 */
	void startup(IProject project, int kind, Map args);
}
