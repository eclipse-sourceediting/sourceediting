/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentproperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class JSPFContentPropertiesManager {
	public JSPFContentPropertiesManager() {
		super();
		fResourceChangeListener = new ResourceChangeListener();
		fJob = new ContentPropertiesManagerJob();
	}

	private static JSPFContentPropertiesManager _instance = null;
	private IResourceChangeListener fResourceChangeListener;
	ContentPropertiesManagerJob fJob;

	/**
	 * This job implementation is used to allow the resource change listener
	 * to schedule operations that need to modify the workspace.
	 */
	private class ContentPropertiesManagerJob extends Job {
		private static final int PROPERTIES_UPDATE_DELAY = 500;
		private List asyncChanges = new ArrayList();

		public ContentPropertiesManagerJob() {
			super(JSPCoreMessages.JSPFContentPropertiesManager_Updating);
			setSystem(true);
			setPriority(Job.INTERACTIVE);
		}

		public void addChanges(Set newChanges) {
			if (newChanges.isEmpty())
				return;
			synchronized (asyncChanges) {
				asyncChanges.addAll(newChanges);
				asyncChanges.notify();
			}
			schedule(PROPERTIES_UPDATE_DELAY);
		}

		public IProject getNextChange() {
			synchronized (asyncChanges) {
				return asyncChanges.isEmpty() ? null : (IProject) asyncChanges.remove(asyncChanges.size() - 1);
			}
		}

		protected IStatus run(IProgressMonitor monitor) {
			MultiStatus result = new MultiStatus(JSPFContentProperties.JSPCORE_ID, IResourceStatus.FAILED_SETTING_CHARSET, JSPCoreMessages.JSPFContentPropertiesManager_Updating, null);
			monitor = monitor == null ? new NullProgressMonitor() : monitor;
			try {
				monitor.beginTask(JSPCoreMessages.JSPFContentPropertiesManager_Updating, asyncChanges.size());
				try {
					IProject next;
					while ((next = getNextChange()) != null) {
						// just exit if the system is shutting down or has
						// been shut down
						// it is too late to change the workspace at this
						// point anyway
						if (Platform.getBundle("org.eclipse.osgi").getState() != Bundle.ACTIVE) //$NON-NLS-1$
							return Status.OK_STATUS;
						try {
							// save the preferences nodes
							if (next.isAccessible()) {
								// save content type preferences
								Preferences projectPrefs = JSPFContentProperties.getPreferences(next, JSPFContentProperties.JSPCONTENTTYPE, false);
								if (projectPrefs != null)
									projectPrefs.flush();
								// save language preferences
								projectPrefs = JSPFContentProperties.getPreferences(next, JSPFContentProperties.JSPLANGUAGE, false);
								if (projectPrefs != null)
									projectPrefs.flush();

							}
						}
						catch (BackingStoreException e) {
							// we got an error saving
							String detailMessage = NLS.bind(JSPCoreMessages.JSPFContentPropertiesManager_Problems_Updating, next.getFullPath());
							result.add(new Status(1 << (IResourceStatus.FAILED_SETTING_CHARSET % 100 / 33), ResourcesPlugin.PI_RESOURCES, IResourceStatus.FAILED_SETTING_CHARSET, detailMessage, e));
						}
					}
					monitor.worked(1);
				}
				catch (OperationCanceledException e) {
					throw e;
				}
			}
			finally {
				monitor.done();
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
		 */
		public boolean shouldRun() {
			synchronized (asyncChanges) {
				return !asyncChanges.isEmpty();
			}
		}
	}

	class ResourceChangeListener implements IResourceChangeListener {
		private void processEntryChanges(IResourceDelta projectDelta, Set projectsToSave) {
			// check each resource with jsp fragment setting to see if it has
			// been moved/deleted
			boolean resourceChanges = false;
			boolean resourceChanges2 = false;

			// project affected
			IProject currentProject = (IProject) projectDelta.getResource();

			resourceChanges = processPreferences(currentProject, JSPFContentProperties.JSPCONTENTTYPE, projectDelta, projectsToSave);
			resourceChanges2 = processPreferences(currentProject, JSPFContentProperties.JSPLANGUAGE, projectDelta, projectsToSave);

			// if there was a preference key change, need to save preferences
			if (resourceChanges || resourceChanges2)
				projectsToSave.add(currentProject);
		}

		/**
		 * Goes through all the resource-dependent preferences associated with
		 * currentProject & key and updates the preference keys if needed
		 * based on projectDelta
		 * 
		 * @param currentProject
		 *            current project of the preferences to be looked at
		 * @param key
		 *            current key/subcategory of the preferences to be looked
		 *            at
		 * @param projectDelta
		 *            the changes to process the preference keys against
		 * @param projectsToSave
		 *            the projects that need to be updated/saved
		 * @return true if currentProject's preferences were modified
		 */
		private boolean processPreferences(IProject currentProject, String key, IResourceDelta projectDelta, Set projectsToSave) {
			boolean resourceChanges = false;

			// get the project-key preference node
			Preferences projectPrefs = JSPFContentProperties.getPreferences(currentProject, key, false);
			if (projectPrefs == null)
				// no preferences for this project-key, just bail
				return false;
			String[] affectedResources;
			try {
				affectedResources = projectPrefs.keys();
			}
			catch (BackingStoreException e) {
				// problems with the project scope... we gonna miss the
				// changes (but will log)
				Logger.log(Logger.WARNING_DEBUG, "Problem retreiving JSP Fragment preferences", e); //$NON-NLS-1$
				return false;
			}

			// go through each preference key (which is really a file name)
			for (int i = 0; i < affectedResources.length; i++) {
				// see if preference key/file name was file that was changed
				IResourceDelta memberDelta = projectDelta.findMember(new Path(affectedResources[i]));
				// no changes for the given resource
				if (memberDelta == null)
					continue;
				if (memberDelta.getKind() == IResourceDelta.REMOVED) {
					resourceChanges = true;
					// remove the setting for the original location
					String currentValue = projectPrefs.get(affectedResources[i], null);
					projectPrefs.remove(affectedResources[i]);
					if ((memberDelta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
						// if moving, copy the setting for the new location
						IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(memberDelta.getMovedToPath().segment(0));
						Preferences targetPrefs = JSPFContentProperties.getPreferences(targetProject, key, true);
						targetPrefs.put(JSPFContentProperties.getKeyFor(memberDelta.getMovedToPath()), currentValue);
						if (targetProject != currentProject)
							projectsToSave.add(targetProject);
					}
				}
			}
			return resourceChanges;
		}

		/**
		 * For any change to the encoding file or any resource with encoding
		 * set, just discard the cache for the corresponding project.
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta == null)
				return;
			IResourceDelta[] projectDeltas = delta.getAffectedChildren();
			// process each project in the delta
			Set projectsToSave = new HashSet();
			for (int i = 0; i < projectDeltas.length; i++)
				// nothing to do if a project has been added/removed/moved
				if (projectDeltas[i].getKind() == IResourceDelta.CHANGED && (projectDeltas[i].getFlags() & IResourceDelta.OPEN) == 0)
					processEntryChanges(projectDeltas[i], projectsToSave);
			fJob.addChanges(projectsToSave);
		}
	}

	public synchronized static void startup() {
		_instance = new JSPFContentPropertiesManager();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(_instance.fResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	public synchronized static void shutdown() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(_instance.fResourceChangeListener);
		_instance = null;
	}
}
