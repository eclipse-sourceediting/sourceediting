/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
import org.eclipse.wst.jsdt.internal.core.util.Messages;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsIndexManager;
import org.osgi.framework.BundleContext;

/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsCorePlugin extends Plugin {
	/**
	 * <p>A {@link Job} used to perform delayed initialization for the plugin</p>
	 */
	private static class PluginInitializerJob extends Job {
		/**
		 * <p>Default constructor to set up this {@link Job} as a
		 * long running system {@link Job}</p>
		 */
		protected PluginInitializerJob() {
			super(Messages.javamodel_initialization);
			
			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
		}
		
		/**
		 * <p>Perform delayed initialization for the plugin</p>
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try {
				/*
				 * Restore save state and process any events that happened before
				 * plug-in loaded. Don't do it immediately since adding the save
				 * participant requires a lock on the workspace to compute the
				 * accumulated deltas, and if the tree is not already locked it
				 * becomes a blocking call.
				 */
				workspace.run(new IWorkspaceRunnable() {
					public void run(final IProgressMonitor worspaceMonitor) throws CoreException {
						ISavedState savedState = null;
						
						try {
							//add the save participant for this bundle
							savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(
									JsCorePlugin.plugin.getBundle().getSymbolicName(), new SaveParticipant());
						} catch (CoreException e) {
							Logger.logException("JSP Core Plugin failed at loading previously saved state." + //$NON-NLS-1$
									" All componenets dependent on this state will start as if first workspace load.", e); //$NON-NLS-1$
						}
						
						//if there is a saved state start up using that, else start up cold
						if(savedState != null) {
							try {
								Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
							} finally {
								savedState.processResourceChangeEvents(new IResourceChangeListener() {
									/**
									 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
									 */
									public void resourceChanged(IResourceChangeEvent event) {
										JSWebResourceEventManager.getDefault().start(event.getDelta(), worspaceMonitor);
									}
								});
							}
						} else {
							JSWebResourceEventManager.getDefault().start(null, worspaceMonitor);
						}
					}
				}, monitor);
			} catch(CoreException e) {
				status = e.getStatus();
			}
			
			return status;
		}
		
	}
	
	/**
	 * Used so that all of the IResourceChangeEvents that occurred before
	 * this plugin loaded can be processed.
	 */
	private static class SaveParticipant implements ISaveParticipant {
		/**
		 * <p>Default constructor</p>
		 */
		protected SaveParticipant() {
		}
		
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
			//ignore
		}
	
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {
			//ignore
		}
	
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			//ignore
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
	 * <p>Job used to finish tasks needed to start up the plugin but that did not have
	 * to block the plugin start up process.</p>
	 */
	private Job fPluginInitializerJob;

	/**
	 * Returns the shared instance.
	 * 
	 * @deprecated - will be removed. Currently used to get "model preferences",
	 *             but there are other, better ways.
	 */

	public static JsCorePlugin getDefault() {
		return JsCorePlugin.plugin;
	}
	
	/**
	 * The constructor.
	 */
	public JsCorePlugin() {
		super();
		JsCorePlugin.plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// JSPIndexManager depends on TaglibController, so TaglibController
		// should be started first
		// listen for classpath changes
		JsIndexManager.getInstance().initialize();
		
		//schedule delayed initialization
		this.fPluginInitializerJob.schedule(2000);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	
	public void stop(BundleContext context) throws Exception {
		// stop listenning for resource changes to update content properties
		// keys
		// stop any indexing
		JsIndexManager.getInstance().shutdown();
		//Stop the resource event manager
		JSWebResourceEventManager.getDefault().stop();
		super.stop(context);
	}
}
