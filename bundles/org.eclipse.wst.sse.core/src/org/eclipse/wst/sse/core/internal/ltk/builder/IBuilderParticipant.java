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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Participants in the Structured building process. Participants may be
 * contributed using the org.eclipse.wst.sse.core.builderparticipant extension point.
 * For resources and resource deltas with matching content types, the builder
 * will first call the build() method and then the cleanup() method in
 * sequence. Participant instances will be reused across projects but are not
 * shared per content type. Participants should not hold on to references to
 * models or resources after cleanup().
 * 
 * @deprecated
 */
public interface IBuilderParticipant {


	/**
	 * Perform a build for the given resources within this project. May be
	 * called multiple times between startup and shutdown.
	 * 
	 * @param resources -
	 *            an IResource list of resources in this project with the
	 *            correct content type
	 * @param project -
	 *            the project on which this participant is operating
	 * @param provider -
	 *            a model provider for this project
	 * @param monitor
	 * @return
	 */
	boolean build(IResource[] resources, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor);

	/**
	 * Perform an incremental build. May be called multiple times between
	 * startup and shutdown.
	 * 
	 * @param delta -
	 *            the IResourceDelta to examine for building
	 * @param project -
	 *            the project on which this participant is operating
	 * @param provider -
	 *            a model provider for this delta's resource
	 * @param monitor
	 * @return
	 */
	boolean build(IResourceDelta delta, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor);

	/**
	 * Do any post-build cleanup from a full build for a project. Called once
	 * for every matching build() call.
	 * 
	 * @param resources -
	 *            the IResource list of resources in this project with the
	 *            correct content type
	 * @param project -
	 *            the project on which this participant is operating
	 * @param provider -
	 *            a model provider for this delta's resource
	 * @param monitor
	 * @return
	 */
	boolean cleanup(IResource[] resources, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor);

	/**
	 * Do any post-build cleanup from building a resource delta. Called once
	 * for every matching build() call.
	 * 
	 * @param delta -
	 *            the IResourceDelta to examine for building
	 * @param project -
	 *            the project on which this participant is operating
	 * @param provider -
	 *            a model provider for this delta's resource
	 * @param monitor
	 * @return
	 */
	boolean cleanup(IResourceDelta delta, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor);

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
