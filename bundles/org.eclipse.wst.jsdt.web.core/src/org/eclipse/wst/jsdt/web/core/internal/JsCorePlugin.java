/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsIndexManager;
import org.osgi.framework.BundleContext;

public class JsCorePlugin extends Plugin {
	/**
	 * <p>
	 * A {@link Job} used to perform delayed initialization for the plug-in
	 * </p>
	 */
	private static class PluginInitializerJob extends Job {
		/**
		 * <p>
		 * Default constructor to set up this {@link Job} as a long running
		 * system {@link Job}
		 * </p>
		 */
		PluginInitializerJob() {
			super(JsCoreMessages.model_initialization);

			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
		}

		/**
		 * <p>
		 * Perform delayed initialization for the plugin
		 * </p>
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try {
				/*
				 * Restore save state and process any events that happened
				 * before plug-in loaded. Don't do it immediately since adding
				 * the save participant requires a lock on the workspace to
				 * compute the accumulated deltas, and if the tree is not
				 * already locked it becomes a blocking call.
				 */
				IWorkspaceRunnable registerParticipant = new IWorkspaceRunnable() {
					public void run(final IProgressMonitor monitor) throws CoreException {
						ISavedState savedState = null;

						try {
							// add the save participant for this bundle
							savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(JsCorePlugin.PLUGIN_ID, new SaveParticipant());
						}
						catch (CoreException e) {
							Logger.logException("JavaScript Web Core failed loading previously saved state; it will be recalculated for this workspace.", e); //$NON-NLS-1$
						}

						/*
						 * if there is a saved state start up using that, else
						 * start up cold
						 */
						if (savedState != null) {
							try {
								Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
							}
							finally {
								savedState.processResourceChangeEvents(new IResourceChangeListener() {
									/**
									 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
									 */
									public void resourceChanged(IResourceChangeEvent event) {
										JSWebResourceEventManager.getDefault().start(event.getDelta(), monitor);
									}
								});
							}
						}
						else {
							JSWebResourceEventManager.getDefault().start(null, monitor);
						}
					}
				};
				workspace.run(registerParticipant, monitor);
			}
			catch (CoreException e) {
				status = e.getStatus();
			}

			return status;
		}

	}

	/**
	 * Used so that all of the IResourceChangeEvents that occurred before this
	 * plugin loaded can be processed.
	 */
	private static class SaveParticipant implements ISaveParticipant {
		/**
		 * <p>
		 * Default constructor
		 * </p>
		 */
		protected SaveParticipant() {
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException {
			context.needDelta();
		}
	}

	// The shared instance.
	private static JsCorePlugin plugin;
	public static final String PLUGIN_ID = "org.eclipse.wst.jsdt.web.core"; //$NON-NLS-1$

	/**
	 * <p>
	 * Job used to finish tasks needed to start up the plugin but that did not
	 * have to block the plugin start up process.
	 * </p>
	 */
	private Job fPluginInitializerJob;

	/**
	 * Returns the shared instance.
	 * 
	 * @deprecated - will be removed. Currently used to get
	 *             "model preferences", but there are other, better ways.
	 */

	public static JsCorePlugin getDefault() {
		return JsCorePlugin.plugin;
	}

	public JsCorePlugin() {
		super();
		JsCorePlugin.plugin = this;
		this.fPluginInitializerJob = new PluginInitializerJob();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */

	public void start(BundleContext context) throws Exception {
		super.start(context);
		// listen for include path changes
		JsIndexManager.getInstance().initialize();

		// schedule delayed initialization of our save participant
		this.fPluginInitializerJob.schedule(2000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */

	public void stop(BundleContext context) throws Exception {
		/*
		 * stop listening for resource changes and interacting with the JS
		 * IndexManager
		 */
		JsIndexManager.getInstance().shutdown();
		/* Stop the resource event manager */
		JSWebResourceEventManager.getDefault().stop();
		super.stop(context);
	}
}
